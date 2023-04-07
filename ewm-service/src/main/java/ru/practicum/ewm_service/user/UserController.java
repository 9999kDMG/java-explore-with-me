package ru.practicum.ewm_service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("admin/users")
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Integer> ids,
                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.getUsersById(ids, from, size);
    }

    @PostMapping("admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @Valid UserDto userDto) {
        return userService.create(userDto);
    }

    @DeleteMapping("admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        userService.delete(userId);
    }
}
