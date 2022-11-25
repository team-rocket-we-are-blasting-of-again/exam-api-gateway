package com.teamrocket.core.security.authmethod;

import com.teamrocket.VerifyGrpc;
import com.teamrocket.VerifyUserRequest;
import com.teamrocket.VerifyUserResponse;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.security.util.VerifiedUser;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthMethod implements AuthMethod {

    @GrpcClient("auth-client")
    private VerifyGrpc.VerifyBlockingStub blockingAuthClient;

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equalsIgnoreCase("Bearer");
    }

    @Override
    public Optional<VerifiedUser> authenticate(String token, RoutePathDto routePathDto) {
        try {
            VerifyUserResponse verifyUserResponse = blockingAuthClient.verifyUser(createRequest(token));

            return routePathDto.getRolesAllowed()
                .stream()
                .filter(role -> role.toString().equals(verifyUserResponse.getUserRole()))
                .map(role -> new VerifiedUser(verifyUserResponse.getUserId(), role.toString()))
                .findFirst();

        } catch (StatusRuntimeException e) {
            return Optional.empty();
        }
    }

    private static VerifyUserRequest createRequest(String token) {
        return VerifyUserRequest.newBuilder()
            .setJwt(token)
            .build();
    }

}
