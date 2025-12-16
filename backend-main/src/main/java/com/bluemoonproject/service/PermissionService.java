package com.bluemoonproject.service;

import com.bluemoonproject.dto.request.PermissionRequest;
import com.bluemoonproject.dto.response.PermissionResponse;
import com.bluemoonproject.entity.Permission;
import com.bluemoonproject.mapper.PermissionMapper;
import com.bluemoonproject.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Service
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission=permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    //
    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
