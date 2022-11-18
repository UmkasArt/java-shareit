package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    public BookingDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
    }
}
