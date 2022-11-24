package com.teamrocket.core.repository;

import com.teamrocket.core.entity.AppRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AppRoleRepository extends R2dbcRepository<AppRole, Long> {

    Mono<AppRole> findAppRoleByRole(String role);

}
