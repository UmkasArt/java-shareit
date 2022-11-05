package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long item_ownerId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long booker_id, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long booker_id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long booker_id, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatus(Long booker_id, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(Long item_ownerId, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long item_ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(Long item_ownerId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long item_ownerId, BookingStatus status);

    List<Booking> findAllByItemIdAndBooker_IdNotAndStatusNot(Long item_id, Long booker_id, BookingStatus status);

    List<Booking> findAllByItemIdAndBooker_IdAndStatusAndEndIsBefore(Long item_id, Long booker_id, BookingStatus status, LocalDateTime end);
}
