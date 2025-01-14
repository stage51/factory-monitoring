package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UploadAvatarResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.models.enums.ReportNotification;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import centrikt.factorymonitoring.authserver.services.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private AdminUserRequest adminUserRequest;
    private SettingRequest settingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        userResponse = UserResponse.builder()
                .id(1L
                )
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        adminUserRequest = new AdminUserRequest("john.doe@example.com", "temptemp", "John", "Doe", "Mike", true, Role.ROLE_USER.toString());

        settingRequest = new SettingRequest();
        settingRequest.setSubscribe(true);
        settingRequest.setTimezone("UTC+03:00");
        settingRequest.setAvatarUrl("image.com");
        settingRequest.setReportNotifications(Collections.singletonList(ReportNotification.FIVE_MINUTE.toString()));
    }

    @Test
    void getUser_shouldReturnUser_whenValidId() throws Exception {
        when(userService.get(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/auth-server/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).get(1L);
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        when(userService.create(any(AdminUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth-server/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).create(any(AdminUserRequest.class));
    }

    @Test
    void updateSetting_shouldReturnUpdatedSetting() throws Exception {
        SettingResponse settingResponse = new SettingResponse("UTC+03:00", true, Collections.singletonList(ReportNotification.FIVE_MINUTE.toString()), "image.com");

        when(userService.updateSetting(anyString(), any(SettingRequest.class))).thenReturn(settingResponse);

        mockMvc.perform(put("/api/v1/auth-server/users/profile/setting")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subscribe").value(true))
                .andExpect(jsonPath("$.timezone").value("UTC+03:00"))
                .andExpect(jsonPath("$.avatarUrl").value("image.com"))
                .andExpect(jsonPath("$.reportNotifications[0]").value("FIVE_MINUTE"));

        verify(userService, times(1)).updateSetting(eq("valid_token"), any(SettingRequest.class));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/v1/auth-server/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void approveUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).approve(1L);

        mockMvc.perform(get("/api/v1/auth-server/users/1/approve"))
                .andExpect(status().isOk());

        verify(userService, times(1)).approve(1L);
    }

    @Test
    void disapproveUser_shouldReturnOk() throws Exception {
        doNothing().when(userService).disapprove(1L);

        mockMvc.perform(get("/api/v1/auth-server/users/1/disapprove"))
                .andExpect(status().isOk());

        verify(userService, times(1)).disapprove(1L);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        when(userService.update(eq(1L), any(AdminUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/auth-server/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService, times(1)).update(eq(1L), any(AdminUserRequest.class));
    }

    @Test
    void getNotVerifiedUserPage_shouldReturnPage() throws Exception {
        Page<UserResponse> userPage = new TestPage<>(Collections.singletonList(userResponse), PageRequest.of(0, 10), 1);

        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setNumber(0);
        pageRequestDTO.setSize(10);
        pageRequestDTO.setSortBy("firstName");
        pageRequestDTO.setSortDirection("ASC");
        pageRequestDTO.setFilters(new HashMap<>());
        pageRequestDTO.setDateRanges(new HashMap<>());

        when(userService.getPage(anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(userPage);
        mockMvc.perform(post("/api/v1/auth-server/users/verification/fetch")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"));

        verify(userService, times(1)).getPage(anyInt(), anyInt(), any(), any(), any(), any());
    }

    @Test
    void profile_shouldReturnUserProfile() throws Exception {
        when(userService.getProfile("valid_token")).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/auth-server/users/profile")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(userService, times(1)).getProfile("valid_token");
    }

    @Test
    void uploadAvatar_shouldReturnAvatarResponse() throws Exception {
        UploadAvatarResponse avatarResponse = new UploadAvatarResponse("image.com");

        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "avatar".getBytes());

        when(userService.uploadAvatar(any(MultipartFile.class))).thenReturn(avatarResponse);

        mockMvc.perform(multipart("/api/v1/auth-server/users/profile/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.avatarUrl").value("image.com"));

        verify(userService, times(1)).uploadAvatar(any(MultipartFile.class));
    }

    private class TestPage<T> extends PageImpl<T> {

        @JsonIgnore
        private Pageable pageable;

        TestPage(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
            this.pageable = pageable;
        }
    }

}

