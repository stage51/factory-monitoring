package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService, CrudService<UserRequest, UserResponse> {
    UserResponse getByEmail(String email);
    UserResponse getProfile(String accessToken);
    UserResponse updateProfile(String accessToken, UserRequest userRequest);
    SettingResponse updateSetting(String accessToken, SettingRequest settingRequest);
    UserResponse create(AdminUserRequest adminUserRequest);
    UserResponse update(Long id, AdminUserRequest adminUserRequest);
    void approve(Long id);
    void disapprove(Long id);
}
