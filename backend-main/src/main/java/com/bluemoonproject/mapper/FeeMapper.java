package com.bluemoonproject.mapper;


import com.bluemoonproject.dto.request.FeeUpdateRequest;
import com.bluemoonproject.dto.request.RoleRequest;
import com.bluemoonproject.dto.request.UserUpdateRequest;
import com.bluemoonproject.dto.response.RoleResponse;
import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Role;
import com.bluemoonproject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeeMapper {

    @Mapping(target="status",ignore=true)
    void updateFee(@MappingTarget Fee fee, FeeUpdateRequest request);
}
