package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.NotSavedException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " was not found."));

        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Request id " + itemDto.getRequestId() + " was not found."));
        }

        try {
            itemRepository.save(item);
            log.info("New item id " + item.getId() + " has been saved.");
            return ItemMapper.mapToItemDto(item);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Item was not saved " + itemDto);
        }
    }

    @Transactional
    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " was not found."));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Item id " + itemId + " does not belong to user id " + userId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        try {
            log.info("Existed item id " + item.getId() + " has been updated.");
            return ItemMapper.mapToItemDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Item was not update " + itemDto);
        }
    }

    @Transactional
    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
        log.info("Existed item id " + itemId + " has been deleted.");
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDtoWithBookings> getAllByUserId(Long userId, int from, int size) {
        Collection<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size));
        log.info("List of all items of user id " + userId + " has been gotten.");
        return items.stream()
                .map(item -> getById(item.getId(), userId))
                .sorted(Comparator.comparing(ItemDtoWithBookings::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getByOwnerIdAndItemId(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item id " + itemId + " was not found."));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Item id " + itemId + " does not belong to user id " + userId);
        }
        log.info("Item id " + itemId + " of user id " + userId + " has been gotten.");
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoWithBookings getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " was not found."));

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId, PageRequest.of(0, 10));

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsBeforeOrderByStartDesc(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now());
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now());
        }
        log.info("Item id " + itemId + " of user id " + userId + " has been gotten.");
        return ItemMapper.mapToItemDtoForOwner(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> search(String text, int from, int size) {
        if (text.isBlank()) {
            log.info("Search result is empty.");
            return new ArrayList<>();
        } else {
            log.info("Search result has been gotten.");
            return ItemMapper.mapToItemDto(itemRepository.search(text, PageRequest.of(from, size)));
        }
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " was not found."));

        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " was not found."));

        if (bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(0, 10))
                .isEmpty()) {
            throw new NotAvailableException("Cannot comment item you have never booked.");
        }


        Comment comment = CommentMapper.mapToComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setItem(item);

        try {
            log.info("New comment id " + comment.getId() +
                    " for item id " + itemId +
                    " from user id " + userId + " has been added");
            return CommentMapper.mapToCommentDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Comment was not created.");
        }
    }
}

