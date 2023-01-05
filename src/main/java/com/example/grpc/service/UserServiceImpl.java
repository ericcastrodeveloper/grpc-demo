package com.example.grpc.service;

import com.example.grpc.domain.UserEntity;
import com.example.grpc.mapper.UserMapper;
import com.example.grpc.proto.service.User;
import com.example.grpc.proto.service.UserList;
import com.example.grpc.proto.service.UserService;
import com.example.grpc.repository.UserRepository;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.quarkus.grpc.GrpcService;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@GrpcService
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Uni<User> create(User request) {
        log.info("Creating a new user.");
        UserEntity entity = mapper.userToEntity(request);
        entity.setId(null);
        return repository.persistAndFlush(entity).map(mapper::entityToUser);
    }

    @Override
    public Uni<User> update(User request) {
        log.info("Updating the user. id: " + request.getId());
        UserEntity entity = mapper.userToEntity(request);
        return repository.findById(request.getId())
                .onItem().ifNull().fail()
                .onItem().ifNotNull().transformToUni(saved ->
                {
                    saved.setName(request.getName());
                    saved.setEmail(request.getEmail());
                    return repository.persistAndFlush(saved).onItem().transform(mapper::entityToUser);
                });
    }

    @Override
    public Uni<User> findById(Int64Value request) {
        log.info("Finding the user. id: " + request.getValue());
        Uni<UserEntity> entity = repository.findById(request.getValue());
        return entity.onItem().ifNull().fail().map(mapper::entityToUser);
    }

    @Override
    public Uni<UserList> list(Empty request) {
        log.info("Listing all users.");
        Uni<List<UserEntity>> entityList = repository.listAll();
        return entityList.onItem().transform(list -> UserList.newBuilder()
                .addAllResultList(mapper.entityListToUserList(list))
                .setResultCount(Int64Value.of(list.size()))
                .build());
    }

    @Override
    @ReactiveTransactional
    public Uni<BoolValue> delete(Int64Value request) {
        log.info("Deleting user by id. id: " + request.getValue());
        return repository.deleteById(request.getValue()).map(item -> BoolValue.newBuilder().setValue(item).build());
    }
}
