package com.bluemoonproject.mapper;


import com.bluemoonproject.dto.request.PermissionRequest;
import com.bluemoonproject.dto.response.PermissionResponse;
import com.bluemoonproject.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
//
    PermissionResponse toPermissionResponse(Permission permission);
}
