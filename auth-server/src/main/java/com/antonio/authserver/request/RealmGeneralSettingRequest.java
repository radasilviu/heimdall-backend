package com.antonio.authserver.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealmGeneralSettingRequest {

    private String name;

    private String displayName;

    private boolean enabled;
    public RealmGeneralSettingRequest() {
    }
    public RealmGeneralSettingRequest(String name, String displayName, boolean enabled) {
        this.name = name;
        this.displayName = displayName;
        this.enabled = enabled;
    }
}
