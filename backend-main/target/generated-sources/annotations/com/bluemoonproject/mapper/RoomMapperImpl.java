package com.bluemoonproject.mapper;

import com.bluemoonproject.dto.request.RoomRequest;
import com.bluemoonproject.dto.response.RoomResponse;
import com.bluemoonproject.entity.Room;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-16T03:16:49+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class RoomMapperImpl implements RoomMapper {

    @Override
    public Room toRoom(RoomRequest request) {
        if ( request == null ) {
            return null;
        }

        Room.RoomBuilder room = Room.builder();

        room.area( request.getArea() );
        room.floor( request.getFloor() );
        room.peopleCount( request.getPeopleCount() );
        room.residentCount( request.getResidentCount() );
        room.roomNumber( request.getRoomNumber() );
        room.roomType( request.getRoomType() );
        room.status( request.getStatus() );

        return room.build();
    }

    @Override
    public RoomResponse toRoomResponse(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomResponse.RoomResponseBuilder roomResponse = RoomResponse.builder();

        if ( room.getId() != null ) {
            roomResponse.id( room.getId() );
        }
        roomResponse.area( room.getArea() );
        roomResponse.floor( room.getFloor() );
        roomResponse.peopleCount( room.getPeopleCount() );
        roomResponse.roomNumber( room.getRoomNumber() );
        roomResponse.roomType( room.getRoomType() );
        roomResponse.status( room.getStatus() );

        return roomResponse.build();
    }

    @Override
    public void updateRoomFromRequest(RoomRequest request, Room room) {
        if ( request == null ) {
            return;
        }

        room.setArea( request.getArea() );
        room.setFloor( request.getFloor() );
        room.setPeopleCount( request.getPeopleCount() );
        room.setResidentCount( request.getResidentCount() );
        room.setRoomNumber( request.getRoomNumber() );
        room.setRoomType( request.getRoomType() );
        room.setStatus( request.getStatus() );
    }
}
