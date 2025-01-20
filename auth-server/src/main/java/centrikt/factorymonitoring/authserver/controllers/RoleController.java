package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.responses.RoleResponse;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth-server/roles")
@Slf4j
public class RoleController implements centrikt.factorymonitoring.authserver.controllers.docs.RoleController {
    @GetMapping()
    public ResponseEntity<Page<RoleResponse>> getRoles() {
        log.info("Fetching role request");
        List<RoleResponse> roles = new ArrayList<>();
        roles.add(RoleResponse.builder().role(Role.ROLE_GUEST.toString())
                .description("Гостевая роль без какого-либо доступа к сервису для недавно зарегистрировавшихся пользователей").build());
        roles.add(RoleResponse.builder().role(Role.ROLE_USER.toString())
                .description("Роль простого пользователя, прошедшего верификацию и имеющий доступ к основному функционалу сервиса").build());
        roles.add(RoleResponse.builder().role(Role.ROLE_MANAGER.toString())
                .description("Роль сотрудника, имеющего ограниченный доступ к средствам администрирования").build());
        roles.add(RoleResponse.builder().role(Role.ROLE_ADMIN.toString())
                .description("Роль администратора, имеющего доступ ко всему функционалу приложения").build());
        log.debug("Fetched role page");
        return ResponseEntity.ok(new PageImpl<>(roles));
    }
}
