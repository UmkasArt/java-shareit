package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long itemOwnerId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(Long itemOwnerId, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(Long itemOwnerId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long itemOwnerId, BookingStatus status);

    List<Booking> findAllByItemIdAndBooker_IdNotAndStatusNot(Long itemId, Long bookerId, BookingStatus status);

    List<Booking> findAllByItemIdAndBooker_IdAndStatusAndEndIsBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);
}
