package com.bluemoonproject.mapper;


import com.bluemoonproject.dto.request.UserActivateRequest;
import com.bluemoonproject.dto.request.UserCreationRequest;
import com.bluemoonproject.dto.request.UserUpdateRequest;
import com.bluemoonproject.dto.response.UserResponse;
import com.bluemoonproject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(target = "residencyStatus", source = "residencyStatus")  // ánh xạ thủ công (nếu cần
    UserResponse toUserResponse(User user);

    @Mapping(target="roles",ignore=true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target="roles",ignore=true)
    void activateUser(@MappingTarget User user, UserActivateRequest request);
//
}
