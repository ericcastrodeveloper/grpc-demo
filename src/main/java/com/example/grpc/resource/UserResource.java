package com.example.grpc.resource;

import com.example.grpc.dto.UserRequest;
import com.example.grpc.dto.UserResponse;
import com.example.grpc.mapper.UserMapper;
import com.example.grpc.proto.service.UserList;
import com.example.grpc.proto.service.UserService;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GrpcClient("user-service")
    UserService userService;
    @Inject
    UserMapper mapper;


    @POST
    public Uni<Response> create(UserRequest userRequest) {
        return userService.create(mapper.userRequestToUser(userRequest)).onItem().transform(inserted -> Response.created(URI.create("/users/" + inserted.getId())).build());
    }

    @PUT
    public Uni<UserResponse> update(UserRequest userRequest) {
        return userService.update(mapper.userRequestToUser(userRequest)).map(mapper::userToUserResponse);
    }

    @GET
    public Uni<List<UserResponse>> list() {
        return userService.list(Empty.newBuilder().build())
                .onItem().transform(UserList::getResultListList)
                .map(mapper::userListToUserResponseList);
    }

    @GET
    @Path("/{id}")
    public Uni<UserResponse> findById(Long id){
        return userService.findById(Int64Value.of(id)).map(mapper::userToUserResponse);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(Long id){
        return userService.delete(Int64Value.of(id)).onItem()
                .transform(boolValue ->
                        boolValue.getValue() ?
                                Response.ok().build() :
                                Response.noContent().build());
    }

}
