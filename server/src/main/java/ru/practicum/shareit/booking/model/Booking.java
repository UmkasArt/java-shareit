package ru.practicum.shareit.booking.model;


import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "START_DATE")
    private LocalDateTime start;
    @Column(name = "END_DATE")
    private LocalDateTime end;
    @OneToOne
    @JoinColumn(
            name = "ITEM_ID",
            referencedColumnName = "ID"
    )
    private Item item;
    @OneToOne
    @JoinColumn(
            name = "BOOKER_ID",
            referencedColumnName = "ID"
    )
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}