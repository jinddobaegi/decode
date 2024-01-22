package com.decode.web.domain.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class UserInfoDto {

    private Long id;
    private String email;
    private String password;
    private String phoneNumber;
    private String birth;
    private String name;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;


}
