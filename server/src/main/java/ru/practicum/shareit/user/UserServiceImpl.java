package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(User user) {
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
    public User updateUser(UserDto userDto, Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NoSuchElementException("Не найден пользователь"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return repository.save(user);
    }
}
