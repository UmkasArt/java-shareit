package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingDto add(long userId, BookingDtoWithId bookingWithId) {
        BookingDto bookingDto = initBook(userId, bookingWithId);
        bookingDto.setStatus(BookingStatus.WAITING);
        if (!bookingDto.getItem().getAvailable())
            throw new ValidationException("Этот item недоступен");
        if (bookingDto.getItem().getOwnerId() == userId) {
            throw new NoSuchElementException("User не может забронировать свой же item");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания раньше даты начала");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала в прошлом");
        }
        Booking booking = BookingMapper.toModel(bookingDto);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto changeStatus(Long userId, Long bookingId, Boolean isApproved) {
        BookingStatus bookingStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new ValidationException("Статус не может быть изменен");
        if (booking.getBooker().getId() == userId) {
            throw new NoSuchElementException("Букер не может изменить статус бронирования");
        }
        booking.setStatus(bookingStatus);
        bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getByUserIdAndBookingId(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (!(booking.getBooker().getId() == userId || booking.getItem().getOwnerId() == userId))
            throw new NoSuchElementException(String.format("User_id = %d и booking_id = %d не связаны", userId, bookingId));
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> findAllByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow();
        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        return bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow();
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId);
        return bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getUserIdAndByState(Long userId, String state) {
        userRepository.findById(userId).orElseThrow();
        List<Booking> result;
        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "PAST":
                result = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "CURRENT":
                result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "FUTURE":
                result = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return result.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerIdAndByState(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow();
        List<Booking> result = new ArrayList<>();
        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId);
                break;
            case "PAST":
                result = bookingRepository.findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "CURRENT":
                result = bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "FUTURE":
                result = bookingRepository.findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                break;
            case "WAITING":
                result = bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return result.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private BookingDto initBook(long userId, BookingDtoWithId bookingDtoWithId) {
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(bookingDtoWithId.getItemId()).orElseThrow();
        BookingDto bookingDto = BookingMapper.toFullDto(bookingDtoWithId);
        bookingDto.setBooker(user);
        bookingDto.setItem(item);
        return bookingDto;
    }

}
