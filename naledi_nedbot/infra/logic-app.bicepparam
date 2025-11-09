using 'logic-app.bicep'

// Core resource names
param logicAppName = 'nedbot-email-processor'

// API configuration
param apiBaseUrl = 'https://nedbot-app.azurewebsites.net'
param apiKeyName = '7c0067c9-0ca8-4e33-b9aa-ede117c033ae'
// Note: apiKeyValue should be passed at deployment time as a secure parameter

// Environment
param environment = 'dev'

param apiKeyValue = 'REPLACE_WITH_YOUR_API_KEY_VALUE' // --- IGNORE ---

