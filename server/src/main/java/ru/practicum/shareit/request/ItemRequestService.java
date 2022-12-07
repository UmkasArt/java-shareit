package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByUser(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAll(int from, int size, Long userId);

    ItemRequestDto getById(Long requestId, Long userId);
}