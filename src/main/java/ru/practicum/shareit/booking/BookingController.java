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
        return bookingService.add(userId, booking);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto changeStatus(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam(value = "approved") boolean isApproved) {
        return bookingService.changeStatus(userId, bookingId, isApproved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getByUserIdAndBookingId(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                              @PathVariable long bookingId) {
        return bookingService.getByUserIdAndBookingId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllById(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.findAllByUserId(userId);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.findAllByOwnerId(userId);
    }

    @GetMapping(params = "state")
    public List<BookingDto> getByState(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                       @RequestParam String state) {
        return bookingService.getUserIdAndByState(userId, state);
    }

    @GetMapping(path = "/owner", params = "state")
    public List<BookingDto> getByOwnerState(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                            @RequestParam String state) {
        return bookingService.getOwnerIdAndByState(ownerId, state);
    }
}
