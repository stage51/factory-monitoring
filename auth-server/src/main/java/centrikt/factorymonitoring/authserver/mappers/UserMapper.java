package centrikt.factorymonitoring.authserver.mappers;

import centrikt.factorymonitoring.authserver.configs.DateTimeConfig;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse dto = UserResponse.builder().id(user.getId()).createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt())
                .email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName())
                .middleName(user.getMiddleName()).timezone(user.getTimezone()).subscribe(user.isSubscribe())
                .active(user.isActive()).role(user.getRole().toString()).organization(OrganizationMapper.toResponse(user.getOrganization()))
                .build();
        return dto;
    }

    public static User toEntityFromCreateRequest(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        User user = new User();

        user.setActive(true);
        user.setRole(Role.ROLE_GUEST);
        user.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        user.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));

        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setMiddleName(userRequest.getMiddleName());
        user.setTimezone(userRequest.getTimezone());
        user.setSubscribe(userRequest.isSubscribe());
        return user;
    }

    public static User toEntityFromCreateRequest(AdminUserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        User user = new User();

        user.setActive(userRequest.isActive());
        user.setRole(Role.valueOf(userRequest.getRole()));
        user.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        user.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));

        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setMiddleName(userRequest.getMiddleName());
        user.setTimezone(userRequest.getTimezone());
        user.setSubscribe(userRequest.isSubscribe());
        return user;
    }

    public static User toEntityFromUpdateRequest(User existingUser, UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        existingUser.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setMiddleName(userRequest.getMiddleName());
        existingUser.setTimezone(userRequest.getTimezone());
        existingUser.setSubscribe(userRequest.isSubscribe());
        return existingUser;
    }

    public static User toEntityFromUpdateRequest(User existingUser, AdminUserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        existingUser.setRole(Role.valueOf(userRequest.getRole()));
        existingUser.setActive(userRequest.isActive());

        existingUser.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setMiddleName(userRequest.getMiddleName());
        existingUser.setTimezone(userRequest.getTimezone());
        existingUser.setSubscribe(userRequest.isSubscribe());
        return existingUser;
    }
}

