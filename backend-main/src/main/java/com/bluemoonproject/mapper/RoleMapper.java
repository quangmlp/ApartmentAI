package com.bluemoonproject.mapper;


import com.bluemoonproject.dto.request.RoleRequest;
import com.bluemoonproject.dto.response.RoleResponse;
import com.bluemoonproject.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target= "permissions", ignore=true)
    Role toRole(RoleRequest request);
//
    RoleResponse toRoleResponse(Role role);
}
