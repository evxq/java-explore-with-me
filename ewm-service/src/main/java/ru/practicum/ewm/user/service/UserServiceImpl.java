package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.utility.PageQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = userRepository.save(user);
        log.info("Создан пользователь id={}", newUser.getId());

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public List<UserDto> getRequiredUsers(List<Long> ids, Integer from, Integer size) {
        List<UserDto> userList = new ArrayList<>();                                         // ПУСТОЙ СПИСОК ЕСЛИ НИЧЕГО НЕ НАЙДЕНО??
        if (ids == null && from != null && size != null) {
            userList = userRepository.findAll(PageQualifier.definePage(from, size))
                    .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        if (ids != null && from != null && size != null) {
            userList = userRepository.findByIdIn(ids, PageQualifier.definePage(from, size))
                    .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        log.info("Вызван список пользователей");

        return userList;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id={} was not found", userId);
                    throw new NotFoundException(String.format("User with id=%d was not found", userId));
                });
        userRepository.deleteById(userId);
        log.info("Удален пользователь id={}", userId);
    }

}
