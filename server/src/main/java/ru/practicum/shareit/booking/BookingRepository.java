package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdAndBooker_IdNotAndStatusNot(Long itemId, Long bookerId, BookingStatus status);

    List<Booking> findAllByItemIdAndBooker_IdAndStatusEqualsAndEndIsBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);


    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime start, LocalDateTime end,
                                                                           Pageable pageable);

    Page<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long itemOwnerId, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start,
                                                                                     LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(Long itemOwnerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long itemOwnerId, BookingStatus status, Pageable pageable);
}