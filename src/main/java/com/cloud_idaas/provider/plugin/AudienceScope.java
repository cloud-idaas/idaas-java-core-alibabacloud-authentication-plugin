package com.cloud_idaas.provider.plugin;

import java.util.List;
import java.util.Set;

public class AudienceScope {

    private String audience;

    private List<String> scopeValues;

    public AudienceScope(){
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public List<String> getScopeValues() {
        return scopeValues;
    }

    public void setScopeValues(List<String> scopeValues) {
        this.scopeValues = scopeValues;
    }
}
