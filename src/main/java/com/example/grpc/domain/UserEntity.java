package com.example.grpc.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "USERS")
public class UserEntity {
    @Id
    @SequenceGenerator(name = "users_id_seq", allocationSize = 1, initialValue = 5)
    @GeneratedValue(generator = "users_id_seq")
    private Long id;
    private String name;
    private String email;
}
