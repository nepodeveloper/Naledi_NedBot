
param location string = resourceGroup().location
param containerAppName string = 'nedbot-app'
param containerImage string = 'mcr.microsoft.com/oss/nginx/nginx:1.21'
param cpu string = '0.25'
param memory string = '0.5Gi'
param minReplicas int = 1
param maxReplicas int = 3
param logRetentionDays int = 30

param registries array = []

resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2021-06-01' = {
	name: '${containerAppName}-law'
	location: location
	properties: {
		sku: {
			name: 'PerGB2018'
		}
		retentionInDays: logRetentionDays
	}
}

resource managedEnv 'Microsoft.App/managedEnvironments@2023-10-01' = {
	name: '${containerAppName}-env'
	location: location
	properties: {
		appLogsConfiguration: {
			destination: 'log-analytics'
			logAnalyticsConfiguration: {
				customerId: logAnalytics.properties.customerId
				sharedKey: listKeys(logAnalytics.id, '2021-06-01').primarySharedKey
			}
		}
	}
}

resource containerApp 'Microsoft.App/containerApps@2023-10-01' = {
	name: containerAppName
	location: location
	properties: {
		managedEnvironmentId: managedEnv.id
		configuration: {
			ingress: {
				external: true
				targetPort: 80
			}
			registries: registries
		}
		template: {
			containers: [
				{
					name: 'app'
					image: containerImage
					resources: {
						cpu: cpu
						memory: memory
					}
				}
			]
			scale: {
				minReplicas: minReplicas
				maxReplicas: maxReplicas
			}
		}
	}
}

output containerAppFqdn string = containerApp.properties.configuration.ingress.fqdn
output managedEnvironmentId string = managedEnv.id
output logAnalyticsWorkspaceId string = logAnalytics.id
