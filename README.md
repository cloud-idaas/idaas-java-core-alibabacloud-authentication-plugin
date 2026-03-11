# idaas-java-core-alibabacloud-authentication-plugin

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://github.com/cloud-idaas/idaas-java-core-alibabacloud-authentication-plugin)

Authentication plugin for IDaaS (Identity as a Service) core SDK, providing Alibaba Cloud EIAM integration for machine-to-machine authentication.

## Features

- **Alibaba Cloud EIAM Integration**: Seamlessly integrates with Alibaba Cloud EIAM service for OAuth2 token generation
- **Plugin Architecture**: Implements the `PluginCredentialProvider` interface from `idaas-java-core-sdk`
- **Automatic Registration**: Auto-registers as a plugin via SPI (Service Provider Interface) mechanism

## Requirements

- Java >= 8
- Maven >= 3.6

## Dependencies

- `idaas-java-core-sdk` >= 0.0.2-beta

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-alibabacloud-authentication-plugin</artifactId>
    <version>0.0.1-beta</version>
</dependency>
```


## Quick Start

### 1. Configuration File

Create a configuration file `~/.cloud_idaas/client_config.json`:

```json
{
    "idaasInstanceId": "your-idaas-instance-id",
    "clientId": "your-client-id",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "your-open-api-endpoint",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PLUGIN",
        "pluginName": "alibabacloudPluginCredentialProvider"
    }
}
```

### 2. Use in code

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.credential.IDaaSCredentialProvider;
import com.cloud_idaas.core.credential.IDaaSTokenResponse;

// Initialize (automatically loads configuration file)
IDaaSCredentialProviderFactory.init();

// Get credential provider
IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();

// Get access token
IDaaSTokenResponse tokenResponse = credentialProvider.getBearerToken();
System.out.println("Access Token: " + tokenResponse.getAccessToken());
```

## Configuration Details

### Complete Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PLUGIN",
        "pluginName": "alibabacloudPluginCredentialProvider"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### Configuration Items

| Configuration Item | Type | Required | Description |
|-------------------|------|----------|-------------|
| idaasInstanceId | string | Yes | IDaaS instance ID |
| clientId | string | Yes | Client ID for authentication |
| issuer | string | Yes | OAuth2 issuer URL |
| tokenEndpoint | string | Yes | OAuth2 token endpoint URL |
| scope | string | No | Requested scope |
| openApiEndpoint | string | Yes | Alibaba Cloud EIAM OpenAPI endpoint |
| authnConfiguration | object | Yes | Authentication configuration |
| httpConfiguration | object | No | HTTP client configuration |

### Plugin Configuration

To use this plugin, set the `authnMethod` to `PLUGIN` and specify the plugin name:

```json
{
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PLUGIN",
        "pluginName": "alibabacloudPluginCredentialProvider"
    }
}
```

## Support and Feedback

- **Email**: cloudidaas@list.alibaba-inc.com
- **Issues**: Please submit an Issue for questions or suggestions

## License

This project is licensed under the [Apache License 2.0](LICENSE).
