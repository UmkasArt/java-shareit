package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingDto add(long userId, BookingDtoWithId bookingWithId) {
        BookingDto bookingDto = initBook(userId, bookingWithId);
        bookingDto.setStatus(WAITING);
        if (!bookingDto.getItem().getAvailable())
            throw new ValidationException("Этот item недоступен");
        if (bookingDto.getItem().getOwnerId() == userId) {
            throw new NoSuchElementException("User не может забронировать свой же item");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
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
        BookingStatus bookingStatus = isApproved ? BookingStatus.APPROVED : REJECTED;
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new ValidationException("Статус не может быть изменен");
        if (Objects.equals(booking.getBooker().getId(), userId)) {
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
        if (!(Objects.equals(booking.getBooker().getId(), userId) || Objects.equals(booking.getItem().getOwnerId(), userId)))
            throw new NoSuchElementException(String.format("User_id = %d и booking_id = %d не связаны", userId, bookingId));
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> findAllByUser(Long userId, String state, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Booking> bookingDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository
                        .findAllByBookerOrderByStartDesc(user, pageRequest).toList());
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest).toList());
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(user,
                        LocalDateTime.now(), pageRequest).toList());
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now(),
                        pageRequest).toList());
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, WAITING,
                        pageRequest).toList());
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user, REJECTED,
                        pageRequest).toList());
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingDtoList
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Booking> bookingDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                bookingDtoList.addAll(bookingRepository
                        .findAllByItem_OwnerIdOrderByStartDesc(user.getId(), pageRequest).toList());
                break;
            case "CURRENT":
                bookingDtoList.addAll(bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(),
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest).toList());
                break;
            case "PAST":
                bookingDtoList.addAll(bookingRepository.findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                        LocalDateTime.now(), pageRequest).toList());
                break;
            case "FUTURE":
                bookingDtoList.addAll(bookingRepository.findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(user.getId(), LocalDateTime.now(),
                        pageRequest).toList());
                break;
            case "WAITING":
                bookingDtoList.addAll(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(user.getId(), WAITING,
                        pageRequest).toList());
                break;
            case "REJECTED":
                bookingDtoList.addAll(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(user.getId(), REJECTED,
                        pageRequest).toList());
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingDtoList
                .stream()
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
