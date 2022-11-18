package io.github.tobiasz.integration.dto;

import io.github.tobiasz.integration.enums.AppRole;
import io.github.tobiasz.integration.enums.AuthenticationMethod;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutePathDto {

    private String path;
    private AuthenticationMethod method;
    private List<AppRole> appRoles;


}
