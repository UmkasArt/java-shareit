package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @RequestBody BookingDtoWithId booking) {
        return bookingService.add(userId, booking);//
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto changeStatus(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam(value = "approved") boolean isApproved) {
        return bookingService.changeStatus(userId, bookingId, isApproved);//
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getByUserIdAndBookingId(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                              @PathVariable long bookingId) {
        return bookingService.getByUserIdAndBookingId(userId, bookingId);//
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return bookingService.findAllByUser(userId, state, from, size);//
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {

        return bookingService.findAllByOwner(userId, state, from, size);//
    }
}
