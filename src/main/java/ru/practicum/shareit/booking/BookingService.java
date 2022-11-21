package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;

import java.util.List;

interface BookingService {

    BookingDto add(long userId, BookingDtoWithId booking);

    BookingDto changeStatus(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getByUserIdAndBookingId(Long userId, Long bookingId);

    List<BookingDto> findAllByUser(Long userId, String state, int from, int size);

    List<BookingDto> findAllByOwner(Long userId, String state, int from, int size);
}
