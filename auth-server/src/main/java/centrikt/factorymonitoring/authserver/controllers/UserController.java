package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UploadAvatarResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.services.UserService;
import centrikt.factorymonitoring.authserver.utils.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth-server/users")
@Slf4j
public class UserController implements centrikt.factorymonitoring.authserver.controllers.docs.UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        log.info("UserController initialized");
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
        log.debug("UserService set");
    }

    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        UserResponse user = userService.get(id);
        log.debug("Fetched user: {}", user);

        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/verification/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<UserResponse>> getNotVerifiedUserPage(@RequestBody PageRequest pageRequest) {
        Map<String, String> filters = pageRequest.getFilters();
        filters.put("role", Role.ROLE_GUEST.toString());
        filters.put("active", "true");
        pageRequest.setFilters(filters);
        log.info("Fetching not verified user page with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<UserResponse> users = userService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched {} user records", users.getContent().size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable Long id) {
        log.info("Approving user with id: {}", id);
        userService.approve(id);
        log.debug("User with id {} approved", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/disapprove")
    public ResponseEntity<Void> disapproveUser(@PathVariable Long id) {
        log.info("Disapproving user with id: {}", id);
        userService.disapprove(id);
        log.debug("User with id {} disapproved", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> createUser(@RequestBody AdminUserRequest userRequest) {
        log.info("Creating new user: {}", userRequest);
        UserResponse createdUser = userService.create(userRequest);
        log.debug("User created: {}", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody AdminUserRequest userRequest) {
        log.info("Updating user with id: {}", id);
        UserResponse updatedUser = userService.update(id, userRequest);
        log.debug("User updated: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.delete(id);
        log.debug("User with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<UserResponse>> getPage(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<UserResponse> users = userService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched {} user records", users.getContent().size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> profile(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Fetching profile for accessToken");
        UserResponse profile = userService.getProfile(accessToken);
        log.debug("Profile fetched: {}", profile);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UserRequest userRequest) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Updating profile for accessToken");
        UserResponse updatedProfile = userService.updateProfile(accessToken, userRequest);
        log.debug("Profile updated: {}", updatedProfile);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/profile/setting")
    public ResponseEntity<SettingResponse> updateSetting(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SettingRequest settingRequest) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Updating setting for accessToken");
        SettingResponse updatedSetting = userService.updateSetting(accessToken, settingRequest);
        log.debug("Setting updated: {}", updatedSetting);
        return ResponseEntity.ok(updatedSetting);
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("Uploading avatar");
        try {
            UploadAvatarResponse uploadAvatarResponse = userService.uploadAvatar(file);
            log.debug("Avatar uploaded successfully: {}", uploadAvatarResponse);
            return ResponseEntity.ok().body(uploadAvatarResponse);
        } catch (IOException e) {
            log.error("Error uploading avatar: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Input/Output Error. " + e.getMessage()));
        }
    }
}

