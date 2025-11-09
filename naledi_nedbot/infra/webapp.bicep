// Azure Web App deployment with container support
param location string = resourceGroup().location
param appName string = 'nedbot-app'
param containerImage string = 'mcr.microsoft.com/oss/nginx/nginx:1.21'
param skuName string = 'B1'
param registryServer string = ''
param registryUsername string = ''
@secure()
param registryPassword string = ''

// App Service Plan
resource appServicePlan 'Microsoft.Web/serverfarms@2022-09-01' = {
  name: '${appName}-plan'
  location: location
  sku: {
    name: skuName
  }
  kind: 'linux'
  properties: {
    reserved: true // Required for Linux
  }
}

// Web App for Containers
resource webApp 'Microsoft.Web/sites@2022-09-01' = {
  name: appName
  location: location
  properties: {
    serverFarmId: appServicePlan.id
    siteConfig: {
      linuxFxVersion: 'DOCKER|${containerImage}'
      appSettings: concat([
        {
          name: 'WEBSITES_ENABLE_APP_SERVICE_STORAGE'
          value: 'false'
        }
        {
          name: 'DOCKER_ENABLE_CI'
          value: 'true'
        }
      ], !empty(registryServer) ? [
        {
          name: 'DOCKER_REGISTRY_SERVER_URL'
          value: 'https://${registryServer}'
        }
        {
          name: 'DOCKER_REGISTRY_SERVER_USERNAME'
          value: registryUsername
        }
        {
          name: 'DOCKER_REGISTRY_SERVER_PASSWORD'
          value: registryPassword
        }
      ] : [])
      healthCheckPath: '/actuator/health' // Spring Boot actuator health endpoint
      alwaysOn: true
    }
  }
}

// Container Registry (if needed)
resource acr 'Microsoft.ContainerRegistry/registries@2023-07-01' = if (empty(registryServer)) {
  name: replace(toLower(appName), '-', '')
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: true
  }
}

output webAppName string = webApp.name
output webAppHostName string = webApp.properties.defaultHostName
output acrLoginServer string = empty(registryServer) ? acr.properties.loginServer : registryServer
