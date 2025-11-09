using 'logic-app.bicep'

// Core resource names
param logicAppName = 'nedbot-email-processor'

// API configuration
param apiBaseUrl = 'https://nedbot-app.azurewebsites.net'
param apiKeyName = '7c0067c9-0ca8-4e33-b9aa-ede117c033ae'
param apiKeyValue = '7c0067c9-0ca8-4e33-b9aa-ede117c033ae'

// Environment
param environment = 'dev'
