package com.teamrocket.gateway.repository;

import com.teamrocket.gateway.entity.AppRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AppRoleRepository extends R2dbcRepository<AppRole, Long> {

    Mono<AppRole> findAppRoleByRole(String role);

}
