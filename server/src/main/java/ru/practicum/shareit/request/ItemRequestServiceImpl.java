package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Невозможно создать запрос - " +
                        "не найден пользователь с id " + userId));
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);

        return toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Невозможно найти запросы пользователя - " +
                        "не найден пользователь с id " + userId));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId, PageRequest.of(from / size, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        setItemsToItemRequestDtos(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAll(int from, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Невозможно найти запросы - " +
                        "не найден пользователь с id " + userId));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestorIsNotOrderByCreatedAsc(user,
                        PageRequest.of(from / size, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        setItemsToItemRequestDtos(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Невозможно найти запрос - " +
                        "не найден пользователь с id " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Невозможно найти запрос - " +
                        "не существует запроса с id " + requestId));
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(itemRequestDto);
        return itemRequestDto;
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList()));
    }

    private void setItemsToItemRequestDtos(List<ItemRequestDto> itemRequestDtos) {
        List<Long> listOfItemRequestDtosIds = itemRequestDtos.stream().map(ItemRequestDto::getId).collect(Collectors.toList());

        List<Item> itemsList = itemRepository.findAllByRequestIdIn(listOfItemRequestDtosIds);

        itemRequestDtos.forEach(itemRequestDto -> {
            List<ItemShortDto> itemShortDtoList = itemsList.stream().filter(item -> item.getRequestId().equals(itemRequestDto.getId())).map(ItemMapper::toItemShortDto).collect(Collectors.toList());
            itemRequestDto.setItems(itemShortDtoList);
        });
    }
}
