package com.bluemoonproject.service;

import com.bluemoonproject.dto.request.RoleRequest;
import com.bluemoonproject.dto.response.RoleResponse;
import com.bluemoonproject.mapper.RoleMapper;
import com.bluemoonproject.repository.PermissionRepository;
import com.bluemoonproject.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    //
    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);
        var permissions= permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);

    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();


    }

    public void delete(String role) {
        roleRepository.deleteById(role);

    }
}
