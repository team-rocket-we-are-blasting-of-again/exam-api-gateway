package com.teamrocket.core.security.authmethod;

import com.teamrocket.VerifyGrpc;
import com.teamrocket.VerifyUserRequest;
import com.teamrocket.VerifyUserResponse;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.security.util.VerifiedUser;
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
        VerifyUserResponse verifyUserResponse = blockingAuthClient.verifyUser(createRequest(token));

        // The token that was provided was invalid
        if (!verifyUserResponse.getVerified()) {
            return Optional.empty();
        }

        return routePathDto.getRolesAllowed()
            .stream()
            .filter(role -> role.toString().equals(verifyUserResponse.getUserRole()))
            .map(role -> new VerifiedUser(verifyUserResponse.getUserId(), role.toString()))
            .findFirst();
    }

    private static VerifyUserRequest createRequest(String token) {
        return VerifyUserRequest.newBuilder()
            .setJwt(token)
            .build();
    }

}
