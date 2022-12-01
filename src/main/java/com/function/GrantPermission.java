package com.function;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.exception.ManagementException;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.authorization.models.BuiltInRole;
import com.azure.resourcemanager.authorization.models.RoleAssignment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Azure Functions with HTTP Trigger.
 */
public class GrantPermission {
    /**
     * This function listens at endpoint "/api/GrantPermission". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/GrantPermission
     * 2. curl {your host}/api/GrantPermission?name=HTTP%20Query
     */
    @FunctionName("GrantPermission")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        RequestInput input = null;
        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Input payload is not specified.").build();
        } else {
            try {
                input = objectMapper.readValue(request.getBody().get(), RequestInput.class);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("JSON payload is malformed..").build();
            }
        }

        String tenantId = System.getenv("tenantId");
        String subscriptionId = System.getenv("subscriptionId");

        String scope = createScope(subscriptionId, input);
        AzureResourceManager azure = getAzureResourceManager(tenantId, subscriptionId);

        HashMap<String, Object> result = new HashMap<>();
        assign(input.getProperties().getReaders(), BuiltInRole.STORAGE_FILE_DATA_SMB_SHARE_READER, scope, azure, result);
        assign(input.getProperties().getAuthors(), BuiltInRole.STORAGE_FILE_DATA_SMB_SHARE_CONTRIBUTOR, scope, azure, result);

        if (result.size() == 0) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not assign any role").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(objectMapper.writeValueAsString(result)).build();
        }
    }

    private void assign(List<String> users, BuiltInRole role, String scope, AzureResourceManager azure, HashMap<String, Object> result) {
        for (String user : users) {
            try{
                RoleAssignment roleAssignment = azure.accessManagement().roleAssignments()
                        .define(UUID.randomUUID().toString())
                        .forUser(user)
                        .withBuiltInRole(role)
                        .withScope(scope)
                        .create();
                result.put(user, roleAssignment);
            }catch (ManagementException e){
                result.put(user, e.getMessage());
            }
        }
    }

    private String createScope(String subscriptionId, RequestInput input) {
        String storageResourceTemplate = "/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Storage/storageAccounts/%s/fileServices/default/fileshares/%s";
        return String.format(storageResourceTemplate,
                subscriptionId,
                input.getProperties().getResourceGroup(),
                input.getProperties().getStorageAccount(),
                input.getProperties().getFileShare());
    }

    private AzureResourceManager getAzureResourceManager(String tenantId, String subscriptionId) {
        AzureProfile profile = new AzureProfile(tenantId, subscriptionId, AzureEnvironment.AZURE);
        // ManagedIdentityCredential managedIdentityCredential = new ManagedIdentityCredentialBuilder().build();
        TokenCredential credential = new DefaultAzureCredentialBuilder().tenantId(tenantId).build();
        return AzureResourceManager
                .authenticate(credential, profile)
                .withTenantId(tenantId)
                .withSubscription(subscriptionId);
    }
}
