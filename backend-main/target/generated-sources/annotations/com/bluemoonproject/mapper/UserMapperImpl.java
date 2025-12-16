package com.bluemoonproject.mapper;

import com.bluemoonproject.dto.request.UserActivateRequest;
import com.bluemoonproject.dto.request.UserCreationRequest;
import com.bluemoonproject.dto.request.UserUpdateRequest;
import com.bluemoonproject.dto.response.PermissionResponse;
import com.bluemoonproject.dto.response.RoleResponse;
import com.bluemoonproject.dto.response.UserResponse;
import com.bluemoonproject.entity.Permission;
import com.bluemoonproject.entity.Role;
import com.bluemoonproject.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-16T03:16:50+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.dob( request.getDob() );
        user.email( request.getEmail() );
        user.firstName( request.getFirstName() );
        user.lastName( request.getLastName() );
        user.password( request.getPassword() );
        user.residencyStatus( request.getResidencyStatus() );
        user.username( request.getUsername() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.residencyStatus( user.getResidencyStatus() );
        userResponse.dob( user.getDob() );
        userResponse.email( user.getEmail() );
        userResponse.firstName( user.getFirstName() );
        if ( user.getId() != null ) {
            userResponse.id( String.valueOf( user.getId() ) );
        }
        userResponse.lastName( user.getLastName() );
        userResponse.password( user.getPassword() );
        userResponse.roles( roleSetToRoleResponseSet( user.getRoles() ) );
        userResponse.username( user.getUsername() );

        return userResponse.build();
    }

    @Override
    public void updateUser(User user, UserUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        user.setDob( request.getDob() );
        user.setEmail( request.getEmail() );
        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setPassword( request.getPassword() );
        user.setResidencyStatus( request.getResidencyStatus() );
    }

    @Override
    public void activateUser(User user, UserActivateRequest request) {
        if ( request == null ) {
            return;
        }

        user.setDob( request.getDob() );
        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setPassword( request.getPassword() );
        user.setResidencyStatus( request.getResidencyStatus() );
    }

    protected PermissionResponse permissionToPermissionResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionResponse.PermissionResponseBuilder permissionResponse = PermissionResponse.builder();

        permissionResponse.description( permission.getDescription() );
        permissionResponse.name( permission.getName() );

        return permissionResponse.build();
    }

    protected Set<PermissionResponse> permissionSetToPermissionResponseSet(Set<Permission> set) {
        if ( set == null ) {
            return null;
        }

        Set<PermissionResponse> set1 = new LinkedHashSet<PermissionResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Permission permission : set ) {
            set1.add( permissionToPermissionResponse( permission ) );
        }

        return set1;
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.description( role.getDescription() );
        roleResponse.name( role.getName() );
        roleResponse.permissions( permissionSetToPermissionResponseSet( role.getPermissions() ) );

        return roleResponse.build();
    }

    protected Set<RoleResponse> roleSetToRoleResponseSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleResponse> set1 = new LinkedHashSet<RoleResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleToRoleResponse( role ) );
        }

        return set1;
    }
}
