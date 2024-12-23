package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UploadAvatarResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends CrudService<UserRequest, UserResponse> {
    UserResponse getByEmail(String email);
    UserResponse getProfile(String accessToken);
    UserResponse updateProfile(String accessToken, UserRequest userRequest);
    SettingResponse updateSetting(String accessToken, SettingRequest settingRequest);
    UserResponse create(AdminUserRequest adminUserRequest);
    UserResponse update(Long id, AdminUserRequest adminUserRequest);
    UploadAvatarResponse uploadAvatar(MultipartFile file) throws IOException;
    void approve(Long id);
    void disapprove(Long id);
}
