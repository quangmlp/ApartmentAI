package com.bluemoonproject.mapper;


import com.bluemoonproject.dto.request.RoleRequest;
import com.bluemoonproject.dto.request.RoomRequest;
import com.bluemoonproject.dto.response.RoleResponse;
import com.bluemoonproject.dto.response.RoomResponse;
import com.bluemoonproject.entity.Role;
import com.bluemoonproject.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    Room toRoom(RoomRequest request);

    @Mapping(target = "id", source = "id")
    RoomResponse toRoomResponse(Room room);

    @Mapping(target = "id", ignore = true) // ID should not be updated
    void updateRoomFromRequest(RoomRequest request, @MappingTarget Room room);
}
