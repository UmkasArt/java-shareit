package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingDtoWithId lastBooking;
    private BookingDtoWithId nextBooking;
    private List<CommentDto> comments = new ArrayList<>();

    @JsonCreator
    public ItemDto(Long id, String name, String description, Boolean available, Long ownerId, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.requestId = requestId;
    }

    public void setNextBooking(BookingDtoWithId nextBooking) {
        if (this.lastBooking == null) this.lastBooking = nextBooking;
        else this.lastBooking = this.nextBooking;
        this.nextBooking = nextBooking;
    }
}