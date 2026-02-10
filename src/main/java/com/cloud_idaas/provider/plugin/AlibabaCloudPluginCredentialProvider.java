package com.cloud_idaas.provider.plugin;

import com.aliyun.eiam20211201.Client;
import com.aliyun.eiam20211201.models.GenerateOauthTokenRequest;
import com.aliyun.eiam20211201.models.GenerateOauthTokenResponse;
import com.aliyun.eiam20211201.models.GenerateOauthTokenResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.exception.ConfigException;
import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.exception.ServerException;
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.PluginCredentialProvider;
import com.cloud_idaas.core.util.ScopeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AlibabaCloudPluginCredentialProvider implements PluginCredentialProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlibabaCloudPluginCredentialProvider.class);

    @Override
    public String getName() {
        return "alibabacloudPluginCredentialProvider";
    }

    @Override
    public IDaaSTokenResponse getIDaaSCredential(String scope) {
        AudienceScope audienceScope = convertScope(scope);
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        Config config = new Config()
                .setEndpoint(IDaaSCredentialProviderFactory.getOpenApiEndpoint())
                .setCredential(credential);
        try {
            Client client = new Client(config);
            GenerateOauthTokenRequest request = new GenerateOauthTokenRequest()
                    .setInstanceId(IDaaSCredentialProviderFactory.getIDaasInstanceId())
                    .setApplicationId(IDaaSCredentialProviderFactory.getClientId())
                    .setAudience(audienceScope.getAudience())
                    .setScopeValues(audienceScope.getScopeValues());
            GenerateOauthTokenResponse response = client.generateOauthToken(request);
            GenerateOauthTokenResponseBody.GenerateOauthTokenResponseBodyTokenResponse tokenResponse = response.getBody().getTokenResponse();
            return convertIDaaSTokenResponse(tokenResponse);
        } catch (TeaException e){
            int statusCode = e.getStatusCode();
            String code = e.getCode();
            String message = e.getMessage();
            String requestId = Optional.ofNullable(e.getData())
                    .map(data -> (String) data.get("RequestId"))
                    .orElse(null);
            if (statusCode >= 400 && statusCode < 500){
                LOGGER.error("Client Error: {}", code);
                LOGGER.error("Client Error Message: {}", message);
                LOGGER.error("Client Error RequestId: {}", requestId);
                throw new ClientException(code, message, requestId);
            } else if (statusCode >= 500) {
                LOGGER.error("Server Error: {}", code);
                LOGGER.error("Server Error Message: {}", message);
                LOGGER.error("Server Error RequestId: {}", requestId);
                throw new ServerException(code, message, requestId);
            } else {
                LOGGER.error("Error Message: {}", message);
                throw e;
            }
        } catch (Exception e){
            LOGGER.error("Error Message: {}", e.getMessage());
            throw new CredentialException(e.getMessage(), e);
        }
    }

    private AudienceScope convertScope(String scopes){
        List<String> scopeList = ScopeUtil.splitScope(scopes);
        Set<String> audiences = new HashSet<>();
        Set<String> scopeValues = new HashSet<>();
        for (String scope : scopeList) {
            if (!ScopeUtil.isValidScope(scope)){
                throw new ConfigException(ErrorCode.INVALID_SCOPE.getCode(), String.format("Invalid scope: %s", scope));
            }
            String[] scopeSplit = scope.split("\\|");
            audiences.add(scopeSplit[0]);
            scopeValues.add(scopeSplit[1]);
        }
        if (audiences.size() > 1){
            throw new ConfigException(ErrorCode.MULTIPLE_AUDIENCE_NOT_SUPPORTED.getCode(), "Multiple Audience is not supported");
        }
        AudienceScope audienceScope = new AudienceScope();
        audienceScope.setAudience(audiences.iterator().next());
        audienceScope.setScopeValues(new ArrayList<>(scopeValues));
        return audienceScope;
    }

    private IDaaSTokenResponse convertIDaaSTokenResponse(GenerateOauthTokenResponseBody.GenerateOauthTokenResponseBodyTokenResponse tokenResponse) {
        IDaaSTokenResponse idaasTokenResponse = new IDaaSTokenResponse();
        idaasTokenResponse.setAccessToken(tokenResponse.getAccessToken());
        idaasTokenResponse.setExpiresIn(tokenResponse.getExpiresIn());
        idaasTokenResponse.setExpiresAt(tokenResponse.getExpiresAt());
        return idaasTokenResponse;
    }
}