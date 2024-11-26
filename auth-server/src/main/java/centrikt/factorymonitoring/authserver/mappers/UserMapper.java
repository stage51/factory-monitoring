package centrikt.factorymonitoring.authserver.mappers;

import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.User;

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

    public static User toEntity(UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        User user = new User();
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
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setMiddleName(userRequest.getMiddleName());
        existingUser.setTimezone(userRequest.getTimezone());
        existingUser.setSubscribe(userRequest.isSubscribe());
        return existingUser;
    }
}

