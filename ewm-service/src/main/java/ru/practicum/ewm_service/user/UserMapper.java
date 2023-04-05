package ru.practicum.ewm_service.user;

import ru.practicum.ewm_service.user.dto.UserDto;
import ru.practicum.ewm_service.user.dto.UserShortDto;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(null)
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
