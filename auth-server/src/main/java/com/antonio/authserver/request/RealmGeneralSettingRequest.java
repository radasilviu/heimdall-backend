package com.antonio.authserver.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealmGeneralSettingRequest {
    private Long id;

    private String name;

    private String displayName;

    private boolean enabled;
}
