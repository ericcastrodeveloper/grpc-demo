package com.example.grpc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequest {
    private Long id;
    private String name;
    private String email;
}
