package ru.practicum.ewm.user;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> getRequiredUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);

}
