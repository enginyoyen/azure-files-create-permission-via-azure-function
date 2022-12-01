# Azure Files - Grant SMB File Share Permission via Azure Functions
This code is part of a proof of concept to grant `Storage File Data SMB Share Reader` and `Storage File Data SMB Share Contributor` roles to users, given file-share via HTTP request.
It is a Java project, uses Azure Resource Manager SDK.

### Warning
As application grants permission to storage, in case if you want to use it, you need to make sure that this function is secured.

## Execution of the function
The function expects HTTP POST request and JSON payload, the payload should be:
```
{
    "properties": {
      "resourceGroup": "rg-MyFileStorage",
      "storageAccount" : "azuresmbfilestoragexn2",
      "fileShare" : "test123",
      "readers": ["134d5af8-bf60-1234-9c3f-f2f425357f3d"],
      "authors": ["23e13a54-2991-1231-14da-c17273def121"]
    }
}
```
`resourceGroup`, `storageAccount` and `fileShare` parameters are needed to identify the file-share that you want to grant permission.
`readers` and `authors` contains the Active Directory Object ID(user identifier). While `readers` are assigned `Storage File Data SMB Share Reader` role for the file-share `authors` are assigned `Storage File Data SMB Share Contributor`.

By using curl you can execute the function by

```
curl -X POST  https://[YOUR-AZURE-FUNCTION-URL/api/GrantPermission  -d @input.json
```
The code above assumes you have a file called `input.json` which the payload matches the one above.

## Deploying Function
You can deploy your function to Azure via VSCode, or GitHub action or tools of your choice:

https://learn.microsoft.com/en-us/azure/developer/javascript/how-to/with-web-app/azure-function-resource-group-management/deploy-azure-function-with-visual-studio-code
https://learn.microsoft.com/en-us/azure/azure-functions/functions-deployment-technologies


## Required permissions
Function app relies on Managed identity to handle all actions, therefore it will need some permissions to assign roles
* Microsoft.Authorization/*/Write : Create roles, role assignments, policy assignments, policy definitions and policy set definitions
* Grant access to Microsoft Graph : https://learn.microsoft.com/en-us/azure/app-service/scenario-secure-app-access-microsoft-graph-as-app?tabs=azure-cli

# Licence
Use of this source code is governed by an MIT license that can be found in the LICENSE file.