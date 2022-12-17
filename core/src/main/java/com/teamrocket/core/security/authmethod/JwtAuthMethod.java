package com.teamrocket.core.security.authmethod;

import com.teamrocket.VerifyGrpc;
import com.teamrocket.VerifyUserRequest;
import com.teamrocket.VerifyUserResponse;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.security.util.VerifiedUser;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtAuthMethod implements AuthMethod {

    @GrpcClient("grpc-service")
    private VerifyGrpc.VerifyBlockingStub blockingAuthClient;

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equalsIgnoreCase("Bearer");
    }

    @Override
    public Optional<VerifiedUser> authenticate(String token, RoutePathDto routePathDto) {
        try {
            VerifyUserResponse verifyUserResponse = blockingAuthClient.verifyUser(createRequest(token));

            Optional<VerifiedUser> result = routePathDto.getRolesAllowed()
                .stream()
                .filter(role -> role.toString().equalsIgnoreCase(verifyUserResponse.getUserRole()))
                .map(role -> new VerifiedUser(verifyUserResponse.getRoleId(), role.toString()))
                .findFirst();

            if (result.isEmpty()) {
                log.info("Invalid role for user: {} and role: {}", verifyUserResponse.getRoleId(), verifyUserResponse.getUserRole());
            }

            return result;

        } catch (StatusRuntimeException e) {
            log.warn("Invalid login attempt", e);
            return Optional.empty();
        }
    }

    private static VerifyUserRequest createRequest(String token) {
        return VerifyUserRequest.newBuilder()
            .setJwt(token)
            .build();
    }

}
