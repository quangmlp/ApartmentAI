package com.bluemoonproject.mapper;

import com.bluemoonproject.dto.request.FeeUpdateRequest;
import com.bluemoonproject.entity.Fee;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-16T03:16:48+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class FeeMapperImpl implements FeeMapper {

    @Override
    public void updateFee(Fee fee, FeeUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        fee.setAmount( request.getAmount() );
        fee.setDescription( request.getDescription() );
        fee.setDueDate( request.getDueDate() );
        fee.setRoomNumber( request.getRoomNumber() );
    }
}
