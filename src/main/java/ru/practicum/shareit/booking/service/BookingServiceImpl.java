package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingOutgoingDto add(BookingIncomingDto bookingIncomingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingIncomingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Item with id " + bookingIncomingDto.getItemId() + " was not found."));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Item " + item.getId() + " is not available for booking");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Item can not be booked by owner.");
        }

        if (!bookingIncomingDto.validate()) {
            throw new ValidationException("End of booking must be after start.");
        }

        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("User with id " + bookerId + " was not found."));

        Booking booking = BookingMapper.mapToBooking(bookingIncomingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        try {
            bookingRepository.save(booking);
            log.info("New booking id " + booking.getId() + " has been saved.");
            return BookingMapper.mapToBookingOutgoingDto(booking);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Booking was not saved.");
        }
    }

    @Transactional
    @Override
    public BookingOutgoingDto approve(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " was not found."));

        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item id " + item.getId() + " does not belong to user id " + userId);
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new NotAvailableException("Item " + item.getId() + " is not waiting to be approved.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        try {
            bookingRepository.save(booking);
            log.info("Booking id " + booking.getId() + " has been answered by owner id " + userId +
                    ". Reply: " + approved);
            return BookingMapper.mapToBookingOutgoingDto(booking);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Booking was not approved.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BookingOutgoingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " was not found."));

        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            throw new NotFoundException("Item id " + booking.getItem().getId() + " does not belong to user id " + userId);
        }

        log.info("Booking id " + bookingId + " has been gotten.");
        return BookingMapper.mapToBookingOutgoingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingOutgoingDto> getAllByBookerId(Long bookerId, BookingRequestState state) {
        userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("User with id " + bookerId + " was not found."));

        switch (state) {
            case ALL:
                log.info("All bookings of booker id " + bookerId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(bookingRepository.findAllByBookerIdOrderByEndDesc(bookerId));
            case CURRENT:
                log.info("Current bookings of booker id " + bookerId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                                bookerId, LocalDateTime.now(), LocalDateTime.now()));
            case PAST:
                log.info("Past bookings of booker id " + bookerId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now()));
            case FUTURE:
                log.info("Future bookings of booker id " + bookerId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now()));
            case WAITING:
                log.info("Waiting bookings of booker id " + bookerId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING));
            case REJECTED:
                log.info("Rejected bookings of booker id " + bookerId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED));
            default:
                throw new NotSupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingOutgoingDto> getAllByItemsOfUser(Long userId, BookingRequestState state) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " was not found."));

        switch (state) {
            case ALL:
                log.info("All bookings for items of owner id " + userId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(bookingRepository.findAllByItemOwnerIdOrderByEndDesc(userId));
            case CURRENT:
                log.info("Current bookings for items of owner id " + userId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                userId, LocalDateTime.now(), LocalDateTime.now()));
            case PAST:
                log.info("Past bookings for items of owner id " + userId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
            case FUTURE:
                log.info("Future bookings for items of owner id " + userId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case WAITING:
                log.info("Waiting bookings for items of owner id " + userId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING));
            case REJECTED:
                log.info("Rejected bookings for items of owner id " + userId + " has been gotten.");
                return BookingMapper.mapToBookingOutgoingDto(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED));
            default:
                throw new NotSupportedStatusException("Unknown state: " + state);
        }
    }
}
