# NedBot Infrastructure - Azure Logic Apps Email Processing

This folder contains the infrastructure code for NedBot's automated email transaction processing system.

## 🎯 Solution Overview

An Azure Logic Apps (Consumption) solution that:
- **Monitors Office 365 Outlook** for customer transaction requests
- **Analyzes email content** to determine transaction type
- **Processes PDF attachments** and stores them in blob storage
- **Routes to appropriate API endpoints** (Account Opening, Loan, Card, Transfer)
- **Tracks all transactions** in Azure Blob Storage as JSON files
- **Generates summaries** for back-office capture
- **Sends confirmation emails** to customers

## 📁 Files

| File | Purpose |
|------|---------|
| `logic-app.bicep` | Main infrastructure template (Logic App, Storage, Connections) |
| `logic-app.bicepparam` | Default parameters for deployment |
| `DEPLOYMENT_GUIDE.md` | Comprehensive deployment and configuration guide |
| `QUICK_REFERENCE.md` | Quick reference for common tasks and troubleshooting |
| `../../.github/workflows/infrastructure.yml` | CI/CD pipeline for automated deployment |

## 🏗️ Architecture

```
Customer Email → Office 365 → Logic App → API Endpoints
                                 ↓
                           Blob Storage
                                 │
                    (Attachments & Transaction Data)
```

## Prerequisites

Before deploying, ensure the required Azure resource providers are registered. This requires subscription owner privileges:

```powershell
# Login as subscription owner/admin
az login

# Register required providers
az provider register --namespace Microsoft.App
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.ContainerRegistry

# Check registration status (might take a few minutes)
az provider show -n Microsoft.App --query registrationState -o tsv
az provider show -n Microsoft.OperationalInsights --query registrationState -o tsv
az provider show -n Microsoft.ContainerRegistry --query registrationState -o tsv
```

## Quick deploy (PowerShell / Azure CLI)

1. Log in and set variables

```powershell
az login
$rg = 'myResourceGroup'
$location = 'eastus'
az group create --name $rg --location $location
```

2. Register resource providers (may already be registered)

```powershell
az provider register --namespace Microsoft.Web
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.App
```

3. Deploy the Bicep template

Use a friendly deployment name so outputs are easy to query.

```powershell
$deploymentName = 'nedbot-containerapp-deploy'
az deployment group create \
  --resource-group $rg \
  --name $deploymentName \
  --template-file .\naledi_nedbot\infra\main.bicep \
  --parameters containerAppName='nedbot-app' containerImage='mcr.microsoft.com/oss/nginx/nginx:1.21' cpu='0.25' memory='0.5Gi' minReplicas=1 maxReplicas=2
```

4. Retrieve the app FQDN from deployment outputs

```powershell
az deployment group show --resource-group $rg --name $deploymentName --query "properties.outputs.containerAppFqdn.value" -o tsv
```

## Using a private registry (ACR)

If you want to use a private image in ACR, either:

- Provide credentials via the `registries` parameter (example JSON shown below). Note: storing passwords in cleartext parameters is not ideal for production. Prefer managed identity + ACR pull role where possible.
- Or configure the Container App to use a system-assigned managed identity and grant the identity `AcrPull` on the ACR.

Example `registries` parameter (pass as a JSON string on the CLI):

```powershell
$registries = '[{"server":"myacr.azurecr.io","username":"<acr-username>","password":"<acr-password>"}]'
az deployment group create --resource-group $rg --name $deploymentName --template-file .\naledi_nedbot\infra\main.bicep --parameters containerAppName='nedbot-app' containerImage='myacr.azurecr.io/myimage:tag' registries="$registries"
```

## Using parameter files

The `infra` folder includes environment-specific parameter files:

- `dev.parameters.json` - Development environment (minimal resources)
- `prod.parameters.json` - Production environment (higher resources, more replicas)

To deploy using a parameter file:

```powershell
az deployment group create \
  --resource-group $rg \
  --name $deploymentName \
  --template-file .\naledi_nedbot\infra\main.bicep \
  --parameters .\naledi_nedbot\infra\dev.parameters.json
```

## GitHub Actions CI/CD

A GitHub Actions workflow is included in `.github/workflows/container-app-ci.yml`. It:

1. Builds the Java application
2. Builds and pushes the container image to ACR
3. Deploys using the Bicep template + environment parameter file

Required secrets (add to GitHub repository):
- `AZURE_CLIENT_ID` - Service principal client ID
- `AZURE_TENANT_ID` - Azure tenant ID
- `AZURE_SUBSCRIPTION_ID` - Azure subscription ID
- `AZURE_RG` - Resource group name

To set up the workflow:

1. Create an Azure AD app registration and configure OIDC:

```powershell
# Set variables
$appName="nedbot-github"
$rgName="your-resource-group"
$subscription="your-subscription-id"
$repoName="N-ovation-Recruitment-Hackathon-2025/NedBot"

# Create the app registration and service principal
$sp = az ad sp create-for-rbac --name $appName --role contributor `
    --scopes /subscriptions/$subscription/resourceGroups/$rgName `
    --json | ConvertFrom-Json

# Store the IDs for next steps
$clientId = $sp.appId
$tenantId = $sp.tenant

# Configure federated credentials for GitHub environments and PR validation
$configurations = @(
    @{
        name = "github-pr-validation"
        subject = "repo:${repoName}:pull_request"
        description = "GitHub Actions federated credential for pull requests"
    },
    @{
        name = "github-dev"
        subject = "repo:${repoName}:environment:dev"
        description = "GitHub Actions federated credential for dev environment"
    },
    @{
        name = "github-prod"
        subject = "repo:${repoName}:environment:prod"
        description = "GitHub Actions federated credential for prod environment"
    }
)

foreach ($config in $configurations) {
    $body = @{
        name = $config.name
        issuer = "https://token.actions.githubusercontent.com"
        subject = $config.subject
        description = $config.description
        audiences = @("api://AzureADTokenExchange")
    } | ConvertTo-Json

    az ad app federated-credential create `
        --id $clientId `
        --parameters $body
}

Write-Host "`nAdd these secrets to your GitHub repository:"
Write-Host "AZURE_CLIENT_ID: $($sp.appId)"
Write-Host "AZURE_TENANT_ID: $($sp.tenant)"
Write-Host "AZURE_SUBSCRIPTION_ID: $subscription"
Write-Host "AZURE_RG: $rgName"
```

2. Create GitHub environments (Settings > Environments):
   - Create environment named `pr-validation`
   - Create environment named `dev`
   - Create environment named `prod`

3. Add the generated values as GitHub repository secrets (Settings > Secrets > Actions):
   - `AZURE_CLIENT_ID`
   - `AZURE_TENANT_ID`
   - `AZURE_SUBSCRIPTION_ID`
   - `AZURE_RG`

4. Create an Azure Container Registry:
```powershell
az acr create -n myacr -g $rg --sku Basic
az role assignment create --assignee $clientId \
    --role AcrPush \
    --scope /subscriptions/{subscription-id}/resourceGroups/{resource-group}/providers/Microsoft.ContainerRegistry/registries/myacr
```

The workflow can be triggered:
- On push/PR to main (when relevant paths change)
- Manually via workflow_dispatch (select environment)

## Notes & next steps

- The template uses ACR for container images. Update the registry name in the workflow file.
- Consider enabling HTTPS-only and custom domains via the Container Apps configuration if you need custom TLS.
- Add more environments (staging) by creating new parameter files and GitHub environments.

---
File: `main.bicep` (in this folder) contains parameter docs and outputs. Use the output `containerAppFqdn` to find the deployed app.
