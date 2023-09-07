package com.linzhi.datasource.app.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private String name;
    private int id;
    private String city;
    private String sex;
    private LocalDateTime birthDay;
}
