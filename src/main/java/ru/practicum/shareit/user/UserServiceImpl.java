package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(@NotNull User user) {
        return repository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        if (!repository.findById(userId).isPresent()) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return repository.findById(userId).get();
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public User updateUser(@NotNull UserDto userDto, Long userId) {
        Optional<User> user = repository.findById(userId);
        if (userDto.getName() != null) {
            user.get().setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.get().setEmail(userDto.getEmail());
        }
        return repository.save(user.get());
    }
}
