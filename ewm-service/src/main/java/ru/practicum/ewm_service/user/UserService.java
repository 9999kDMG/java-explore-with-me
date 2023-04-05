package ru.practicum.ewm_service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.category.CategoryMapper;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.user.dto.UserDto;
import ru.practicum.ewm_service.utils.Pagination;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getUsersById(List<Integer> ids, Integer from, Integer size) {
        Pageable pageable = Pagination.getPageOrThrow(from, size);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        List<User> users = userRepository.findByIdIn(ids,pageable);
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("the user with this email is registered");
        }

    }

    public void delete(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        userRepository.deleteById(userId);
    }

    public User getUserOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", id)));
    }

}
