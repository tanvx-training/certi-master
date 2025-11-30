package com.certimaster.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPermissionRequest {

    private String resourceCode;
    private Long resourceId;
    private Map<String, Object> context;
}
