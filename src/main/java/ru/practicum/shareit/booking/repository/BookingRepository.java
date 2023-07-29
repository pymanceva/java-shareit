package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByEndDesc(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdOrderByEndDesc(Long ownerId);

    Collection<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    Booking findFirstByItemIdAndStatusAndStartIsBeforeOrderByStartDesc(
            Long itemId, BookingStatus status, LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime end);
}
