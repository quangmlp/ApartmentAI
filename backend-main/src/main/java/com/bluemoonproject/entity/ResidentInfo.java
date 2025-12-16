package com.bluemoonproject.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ResidentInfo {
    private String name;
    private Integer age;
}

//  Lớp đơn giản lưu thông tin của người trong nhà
// không phải là entity