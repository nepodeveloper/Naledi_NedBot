// Simplified Logic App without Blob Storage
// Parameters following Microsoft best practices
param location string = resourceGroup().location
param logicAppName string
param apiBaseUrl string
param apiKeyName string = '7c0067c9-0ca8-4e33-b9aa-ede117c033ae'
@secure()
param apiKeyValue string = '7c0067c9-0ca8-4e33-b9aa-ede117c033ae'
param environment string = 'dev'

// Tags for resource management
var commonTags = {
  Environment: environment
  Project: 'NedBot'
  ManagedBy: 'Bicep'
  Purpose: 'EmailTransactionProcessing'
}

// Log Analytics Workspace
resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name: '${logicAppName}-workspace'
  location: location
  tags: commonTags
  properties: {
    retentionInDays: 90
    features: {
      enableLogAccessUsingOnlyResourcePermissions: true
    }
    sku: {
      name: 'PerGB2018'
    }
  }
}

// Application Insights for monitoring
resource appInsights 'Microsoft.Insights/components@2020-02-02' = {
  name: '${logicAppName}-insights'
  location: location
  tags: commonTags
  kind: 'web'
  properties: {
    Application_Type: 'web'
    RetentionInDays: 90
    IngestionMode: 'LogAnalytics'
    WorkspaceResourceId: logAnalytics.id
    publicNetworkAccessForIngestion: 'Enabled'
    publicNetworkAccessForQuery: 'Enabled'
  }
}

// Logic App (Consumption) with complete workflow
resource logicApp 'Microsoft.Logic/workflows@2019-05-01' = {
  name: logicAppName
  location: location
  tags: commonTags
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    state: 'Enabled'
    definition: {
      '$schema': 'https://schema.management.azure.com/providers/Microsoft.Logic/schemas/2016-06-01/workflowdefinition.json#'
      contentVersion: '1.0.0.0'
      parameters: {
        apiBaseUrl: {
          type: 'string'
        }
        apiKeyName: {
          type: 'string'
        }
        apiKeyValue: {
          type: 'securestring'
        }
      }
      triggers: {
        manual: {
          type: 'Request'
          kind: 'Http'
          inputs: {
            schema: {
              type: 'object'
              properties: {
                from: {
                  type: 'string'
                  description: 'Email sender address'
                }
                subject: {
                  type: 'string'
                  description: 'Email subject line'
                }
                body: {
                  type: 'string'
                  description: 'Email body content'
                }
                dateTimeReceived: {
                  type: 'string'
                  description: 'ISO 8601 datetime when email was received'
                }
                hasAttachments: {
                  type: 'boolean'
                  description: 'Whether email has attachments'
                }
                attachments: {
                  type: 'array'
                  description: 'Array of email attachments'
                  items: {
                    type: 'object'
                    properties: {
                      name: { 
                        type: 'string'
                        description: 'Attachment filename'
                      }
                      contentBytes: { 
                        type: 'string'
                        description: 'Base64-encoded attachment content'
                      }
                      contentType: { 
                        type: 'string'
                        description: 'MIME type (e.g., application/pdf)'
                      }
                    }
                    required: ['name', 'contentBytes']
                  }
                }
                callbackUrl: {
                  type: 'string'
                  description: 'Optional webhook URL to receive completion notification'
                }
              }
              required: ['from', 'subject', 'body']
            }
          }
        }
      }
      actions: {
        // Initialize tracking variables
        Initialize_transaction_ID: {
          type: 'InitializeVariable'
          inputs: {
            variables: [
              {
                name: 'TransactionID'
                type: 'string'
                value: '@{guid()}'
              }
            ]
          }
          runAfter: {}
        }
        Initialize_request_type: {
          type: 'InitializeVariable'
          inputs: {
            variables: [
              {
                name: 'RequestType'
                type: 'string'
                value: ''
              }
            ]
          }
          runAfter: {
            Initialize_transaction_ID: ['Succeeded']
          }
        }
        
        // Main processing scope
        Scope_Email_Analysis_and_Processing: {
          type: 'Scope'
          runAfter: {
            Initialize_request_type: ['Succeeded']
          }
          actions: {
            // Extract email metadata
            Compose_email_metadata: {
              type: 'Compose'
              inputs: {
                from: '@triggerBody()?[\'from\']'
                subject: '@triggerBody()?[\'subject\']'
                body: '@triggerBody()?[\'body\']'
                receivedTime: '@triggerBody()?[\'dateTimeReceived\']'
                hasAttachments: '@triggerBody()?[\'hasAttachments\']'
                attachments: '@triggerBody()?[\'attachments\']'
              }
              runAfter: {}
            }
            
            // Analyze email content to determine transaction type
            Analyze_email_for_transaction_type: {
              type: 'Compose'
              inputs: {
                emailBody: '@{toLower(triggerBody()?[\'body\'])}'
                subject: '@{toLower(triggerBody()?[\'subject\'])}'
                transactionType: '@{if(or(contains(toLower(triggerBody()?[\'body\']), \'account opening\'), contains(toLower(triggerBody()?[\'subject\']), \'open account\')), \'ACCOUNT_OPENING\', if(or(contains(toLower(triggerBody()?[\'body\']), \'loan\'), contains(toLower(triggerBody()?[\'subject\']), \'loan\')), \'LOAN_APPLICATION\', if(or(contains(toLower(triggerBody()?[\'body\']), \'card\'), contains(toLower(triggerBody()?[\'subject\']), \'card\')), \'CARD_REQUEST\', if(or(contains(toLower(triggerBody()?[\'body\']), \'transfer\'), contains(toLower(triggerBody()?[\'subject\']), \'transfer\')), \'TRANSFER\', \'GENERAL_INQUIRY\'))))}'
              }
              runAfter: {
                Compose_email_metadata: ['Succeeded']
              }
            }
            
            Set_request_type: {
              type: 'SetVariable'
              inputs: {
                name: 'RequestType'
                value: '@{outputs(\'Analyze_email_for_transaction_type\')?[\'transactionType\']}'
              }
              runAfter: {
                Analyze_email_for_transaction_type: ['Succeeded']
              }
            }
            
            // Route to appropriate API based on transaction type
            Switch_on_transaction_type: {
              type: 'Switch'
              expression: '@variables(\'RequestType\')'
              runAfter: {
                Set_request_type: ['Succeeded']
              }
              cases: {
                Account_Opening: {
                  case: 'ACCOUNT_OPENING'
                  actions: {
                    Call_Account_Opening_API: {
                      type: 'Http'
                      inputs: {
                        method: 'POST'
                        uri: '@{parameters(\'apiBaseUrl\')}/api/onboarding/applications'
                        headers: {
                          'Content-Type': 'application/json'
                          '@{parameters(\'apiKeyName\')}': '@{parameters(\'apiKeyValue\')}'
                        }
                        body: {
                          firstName: '@{triggerBody()?[\'from\']}'
                          email: '@{triggerBody()?[\'from\']}'
                          accountType: 'CURRENT'
                          source: 'EMAIL_REQUEST'
                          transactionId: '@{variables(\'TransactionID\')}'
                        }
                        retryPolicy: {
                          type: 'exponential'
                          count: 4
                          interval: 'PT7S'
                          minimumInterval: 'PT5S'
                          maximumInterval: 'PT1H'
                        }
                        timeout: 'PT30S'
                      }
                      runAfter: {}
                    }
                    Store_account_opening_result: {
                      type: 'Compose'
                      inputs: {
                        TransactionID: '@{variables(\'TransactionID\')}'
                        RequestType: 'ACCOUNT_OPENING'
                        CustomerEmail: '@{triggerBody()?[\'from\']}'
                        ProcessedDate: '@{utcNow()}'
                        EmailSubject: '@{triggerBody()?[\'subject\']}'
                        ApiResponse: '@{body(\'Call_Account_Opening_API\')}'
                      }
                      runAfter: {
                        Call_Account_Opening_API: ['Succeeded']
                      }
                    }
                  }
                }
                Loan_Application: {
                  case: 'LOAN_APPLICATION'
                  actions: {
                    Call_Loan_API: {
                      type: 'Http'
                      inputs: {
                        method: 'POST'
                        uri: '@{parameters(\'apiBaseUrl\')}/api/banking/loans/apply'
                        headers: {
                          'Content-Type': 'application/json'
                          '@{parameters(\'apiKeyName\')}': '@{parameters(\'apiKeyValue\')}'
                        }
                        body: {
                          customerNumber: '@{triggerBody()?[\'from\']}'
                          loanAmount: 50000
                          loanType: 'PERSONAL'
                          termMonths: 36
                          source: 'EMAIL_REQUEST'
                          transactionId: '@{variables(\'TransactionID\')}'
                        }
                        retryPolicy: {
                          type: 'exponential'
                          count: 4
                          interval: 'PT7S'
                          minimumInterval: 'PT5S'
                          maximumInterval: 'PT1H'
                        }
                        timeout: 'PT30S'
                      }
                      runAfter: {}
                    }
                    Store_loan_result: {
                      type: 'Compose'
                      inputs: {
                        TransactionID: '@{variables(\'TransactionID\')}'
                        RequestType: 'LOAN_APPLICATION'
                        CustomerEmail: '@{triggerBody()?[\'from\']}'
                        ProcessedDate: '@{utcNow()}'
                        EmailSubject: '@{triggerBody()?[\'subject\']}'
                        ApiResponse: '@{body(\'Call_Loan_API\')}'
                      }
                      runAfter: {
                        Call_Loan_API: ['Succeeded']
                      }
                    }
                  }
                }
                Card_Request: {
                  case: 'CARD_REQUEST'
                  actions: {
                    Call_Card_API: {
                      type: 'Http'
                      inputs: {
                        method: 'POST'
                        uri: '@{parameters(\'apiBaseUrl\')}/api/banking/cards/request'
                        headers: {
                          'Content-Type': 'application/json'
                          '@{parameters(\'apiKeyName\')}': '@{parameters(\'apiKeyValue\')}'
                        }
                        body: {
                          accountNumber: '1234567890'
                          cardType: 'CREDIT'
                          source: 'EMAIL_REQUEST'
                          transactionId: '@{variables(\'TransactionID\')}'
                        }
                        retryPolicy: {
                          type: 'exponential'
                          count: 4
                          interval: 'PT7S'
                          minimumInterval: 'PT5S'
                          maximumInterval: 'PT1H'
                        }
                        timeout: 'PT30S'
                      }
                      runAfter: {}
                    }
                    Store_card_result: {
                      type: 'Compose'
                      inputs: {
                        TransactionID: '@{variables(\'TransactionID\')}'
                        RequestType: 'CARD_REQUEST'
                        CustomerEmail: '@{triggerBody()?[\'from\']}'
                        ProcessedDate: '@{utcNow()}'
                        EmailSubject: '@{triggerBody()?[\'subject\']}'
                        ApiResponse: '@{body(\'Call_Card_API\')}'
                      }
                      runAfter: {
                        Call_Card_API: ['Succeeded']
                      }
                    }
                  }
                }
                Transfer: {
                  case: 'TRANSFER'
                  actions: {
                    Call_Transfer_API: {
                      type: 'Http'
                      inputs: {
                        method: 'POST'
                        uri: '@{parameters(\'apiBaseUrl\')}/api/banking/transfer'
                        headers: {
                          'Content-Type': 'application/json'
                          '@{parameters(\'apiKeyName\')}': '@{parameters(\'apiKeyValue\')}'
                        }
                        body: {
                          fromAccount: '1234567890'
                          toAccount: '0987654321'
                          amount: 1000
                          reference: 'Email Transfer Request'
                          transactionId: '@{variables(\'TransactionID\')}'
                        }
                        retryPolicy: {
                          type: 'exponential'
                          count: 4
                          interval: 'PT7S'
                          minimumInterval: 'PT5S'
                          maximumInterval: 'PT1H'
                        }
                        timeout: 'PT30S'
                      }
                      runAfter: {}
                    }
                    Store_transfer_result: {
                      type: 'Compose'
                      inputs: {
                        TransactionID: '@{variables(\'TransactionID\')}'
                        RequestType: 'TRANSFER'
                        CustomerEmail: '@{triggerBody()?[\'from\']}'
                        ProcessedDate: '@{utcNow()}'
                        EmailSubject: '@{triggerBody()?[\'subject\']}'
                        ApiResponse: '@{body(\'Call_Transfer_API\')}'
                      }
                      runAfter: {
                        Call_Transfer_API: ['Succeeded']
                      }
                    }
                  }
                }
                Default: {
                  case: 'GENERAL_INQUIRY'
                  actions: {
                    Handle_general_inquiry: {
                      type: 'Compose'
                      inputs: {
                        TransactionID: '@{variables(\'TransactionID\')}'
                        RequestType: 'GENERAL_INQUIRY'
                        CustomerEmail: '@{triggerBody()?[\'from\']}'
                        ProcessedDate: '@{utcNow()}'
                        EmailSubject: '@{triggerBody()?[\'subject\']}'
                        ApiResponse: 'Request type not automatically supported'
                      }
                      runAfter: {}
                    }
                  }
                }
              }
            }
            
            // Create summary for back-office
            Create_summary_document: {
              type: 'Compose'
              inputs: {
                TransactionID: '@{variables(\'TransactionID\')}'
                RequestType: '@{variables(\'RequestType\')}'
                CustomerEmail: '@{triggerBody()?[\'from\']}'
                EmailSubject: '@{triggerBody()?[\'subject\']}'
                ProcessedTime: '@{utcNow()}'
                AttachmentCount: '@{length(triggerBody()?[\'attachments\'])}'
                Status: 'PROCESSED'
                Summary: 'Email request processed and routed to appropriate API endpoint'
              }
              runAfter: {
                Switch_on_transaction_type: ['Succeeded']
              }
            }
            
            // Send webhook notification if callback URL provided
            Send_webhook_notification: {
              type: 'If'
              expression: {
                and: [
                  {
                    not: {
                      equals: [
                        '@triggerBody()?[\'callbackUrl\']'
                        '@null'
                      ]
                    }
                  }
                  {
                    not: {
                      equals: [
                        '@triggerBody()?[\'callbackUrl\']'
                        ''
                      ]
                    }
                  }
                ]
              }
              actions: {
                Call_webhook: {
                  type: 'Http'
                  inputs: {
                    method: 'POST'
                    uri: '@triggerBody()?[\'callbackUrl\']'
                    headers: {
                      'Content-Type': 'application/json'
                    }
                    body: {
                      transactionId: '@variables(\'TransactionID\')'
                      status: 'completed'
                      requestType: '@variables(\'RequestType\')'
                      customerEmail: '@triggerBody()?[\'from\']'
                      timestamp: '@utcNow()'
                      summary: '@outputs(\'Create_summary_document\')'
                    }
                    retryPolicy: {
                      type: 'exponential'
                      count: 3
                      interval: 'PT10S'
                      minimumInterval: 'PT5S'
                      maximumInterval: 'PT1M'
                    }
                    timeout: 'PT30S'
                  }
                  runAfter: {}
                }
              }
              runAfter: {
                Create_summary_document: ['Succeeded']
              }
            }
          }
        }
        
        // Return HTTP success response
        Return_success_response: {
          type: 'Response'
          inputs: {
            statusCode: 200
            headers: {
              'Content-Type': 'application/json'
            }
            body: {
              status: 'success'
              message: 'Transaction request accepted and processed successfully'
              transactionId: '@variables(\'TransactionID\')'
              requestType: '@variables(\'RequestType\')'
              timestamp: '@utcNow()'
              details: {
                customerEmail: '@triggerBody()?[\'from\']'
                emailSubject: '@triggerBody()?[\'subject\']'
                webhookNotificationSent: '@not(equals(triggerBody()?[\'callbackUrl\'], null))'
              }
            }
          }
          runAfter: {
            Scope_Email_Analysis_and_Processing: ['Succeeded']
          }
        }
        
        // Error handling scope
        Handle_processing_errors: {
          type: 'Scope'
          actions: {
            Log_error_details: {
              type: 'Compose'
              inputs: {
                TransactionID: '@{variables(\'TransactionID\')}'
                RequestType: '@{variables(\'RequestType\')}'
                CustomerEmail: '@{triggerBody()?[\'from\']}'
                ProcessedDate: '@{utcNow()}'
                EmailSubject: '@{triggerBody()?[\'subject\']}'
                ErrorDetails: '@{result(\'Scope_Email_Analysis_and_Processing\')}'
              }
              runAfter: {}
            }
            
            Return_error_response: {
              type: 'Response'
              inputs: {
                statusCode: 500
                headers: {
                  'Content-Type': 'application/json'
                }
                body: {
                  status: 'error'
                  message: 'Transaction processing failed'
                  transactionId: '@variables(\'TransactionID\')'
                  requestType: '@variables(\'RequestType\')'
                  timestamp: '@utcNow()'
                  details: {
                    customerEmail: '@triggerBody()?[\'from\']'
                    emailSubject: '@triggerBody()?[\'subject\']'
                    errorSummary: '@result(\'Scope_Email_Analysis_and_Processing\')'
                  }
                }
              }
              runAfter: {
                Log_error_details: ['Succeeded']
              }
            }
          }
          runAfter: {
            Scope_Email_Analysis_and_Processing: ['Failed', 'Skipped', 'TimedOut']
          }
        }
      }
      outputs: {}
    }
    parameters: {
      apiBaseUrl: {
        value: apiBaseUrl
      }
      apiKeyName: {
        value: apiKeyName
      }
      apiKeyValue: {
        value: apiKeyValue
      }
    }
  }
}

// Outputs
output logicAppId string = logicApp.id
output logicAppName string = logicApp.name
output appInsightsId string = appInsights.id
output appInsightsInstrumentationKey string = appInsights.properties.InstrumentationKey
