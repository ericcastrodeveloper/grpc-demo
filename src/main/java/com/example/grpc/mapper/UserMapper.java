package com.example.grpc.mapper;

import com.example.grpc.domain.UserEntity;
import com.example.grpc.proto.service.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    UserEntity userToEntity(User user);
    User entityToUser(UserEntity entity);
    List<User> entityListToUserList(List<UserEntity> entityList);
}
