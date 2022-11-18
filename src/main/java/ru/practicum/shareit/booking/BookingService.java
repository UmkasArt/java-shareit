package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;

import java.util.List;

interface BookingService {

    BookingDto add(long userId, BookingDtoWithId booking);

    BookingDto changeStatus(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getByUserIdAndBookingId(Long userId, Long bookingId);

    List<BookingDto> findAllByUserId(Long userId);

    List<BookingDto> findAllByOwnerId(Long ownerId);

    List<BookingDto> getUserIdAndByState(Long userId, String state);

    List<BookingDto> getOwnerIdAndByState(Long ownerId, String state);
}
