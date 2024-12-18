package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth-server/users")
@Slf4j
public class UserController implements centrikt.factorymonitoring.authserver.controllers.docs.UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        UserResponse user = userService.get(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/verification/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<UserResponse>> getNotVerifiedUserPage(
            @RequestBody PageRequestDTO pageRequestDTO
    ) {
        Map<String, String> filters = pageRequestDTO.getFilters();
        filters.put("role", Role.ROLE_GUEST.toString());
        filters.put("active", "true");
        pageRequestDTO.setFilters(filters);
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<UserResponse> users = userService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable Long id) {
        log.info("Approving user with id: {}", id);
        userService.approve(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/disapprove")
    public ResponseEntity<Void> disapproveUser(@PathVariable Long id) {
        log.info("Disapproving user with id: {}", id);
        userService.disapprove(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> createUser(@RequestBody AdminUserRequest userRequest) {
        log.info("Creating new user: {}", userRequest);
        UserResponse createdUser = userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody AdminUserRequest userRequest) {
        log.info("Updating user with id: {}", id);
        UserResponse updatedUser = userService.update(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<UserResponse>> getPage(
            @RequestBody PageRequestDTO pageRequestDTO
    ) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<UserResponse> users = userService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> profile(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        return ResponseEntity.ok(userService.getProfile(accessToken));
    }
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UserRequest userRequest) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        return ResponseEntity.ok(userService.updateProfile(accessToken, userRequest));
    }
    @PutMapping("/profile/setting")
    public ResponseEntity<SettingResponse> updateSetting(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SettingRequest settingRequest) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        return ResponseEntity.ok(userService.updateSetting(accessToken, settingRequest));
    }
}
