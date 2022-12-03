package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new NoSuchElementException("Не найден пользователь в хранилище пользователей с id " + userId);
        }
        itemDto.setOwnerId(userId);
        Item item = itemRepository.save(ItemMapper.toModel(itemDto));
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item updatedItem = null;
        updatedItem = itemRepository.findItemByIdAndOwnerId(itemId, userId);
        if (updatedItem == null) {
            throw new NoSuchElementException("У пользователя с id " + userId + " не найден item с id " + itemId);
        }
        itemDto.setOwnerId(userId);
        Item oldData = itemRepository.findById(itemDto.getId()).orElseThrow();
        Item item = updateItemEntity(oldData, ItemMapper.toModel(itemDto));
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new NoSuchElementException("Не найден пользователь в хранилище пользователей с id " + userId);
        }
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new NoSuchElementException("Не найден item с id=" + itemId);
        }
        ItemDto itemDto = ItemMapper.toDto(item);
        addCommentsToItem(itemDto);
        setBookings(itemDto, userId);
        return itemDto;
    }

    @Override
    public List<ItemDto> getSearchedItems(String text, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Невозможно найти Item - некорректно переданы параметры поиска");
        } else if (size < 1) {
            throw new ValidationException("Невозможно найти Item - некорректно переданы параметры поиска");
        }
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.searchItems(text, pageRequest).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItems(Long userId, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Невозможно найти Item - некорректно переданы параметры поиска");
        } else if (size < 1) {
            throw new ValidationException("Невозможно найти Item - некорректно переданы параметры поиска");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.findItemsByOwnerIdOrderByIdAsc(userId, pageRequest).stream()
                .map(ItemMapper::toDto)
                .map(i -> setBookings(i, userId))
                .map(this::addCommentsToItem)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndBooker_IdAndStatusEqualsAndEndIsBefore(itemId, userId, APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("Item не забронирован этим пользователем или аренда вещи еще не завершена");
        }
        Comment comment = CommentMapper.toModel(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private ItemDto setBookings(ItemDto itemDto, long bookerId) {
        Comparator<Booking> comparator = (b1, b2) -> b1.getStart().isBefore(b2.getStart()) ? -1 : b1.getStart().isAfter(b2.getStart()) ? 1 : 0;
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBooker_IdNotAndStatusNot(itemDto.getId(), bookerId, REJECTED);
        bookings.sort(comparator);
        for (Booking booking : bookings) {
            itemDto.setNextBooking(BookingMapper.toDtoWithId(booking));
        }
        return itemDto;
    }

    private Item updateItemEntity(Item item, Item updItemData) {
        if (updItemData.getName() != null) item.setName(updItemData.getName());
        if (updItemData.getDescription() != null) item.setDescription(updItemData.getDescription());
        if (updItemData.getAvailable() != null) item.setAvailable(updItemData.getAvailable());
        if (updItemData.getOwnerId() != null) item.setOwnerId(updItemData.getOwnerId());
        if (updItemData.getRequestId() != null) item.setRequestId(updItemData.getRequestId());
        return item;
    }

    private ItemDto addCommentsToItem(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(
                comments.stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList())
        );
        return itemDto;
    }
}