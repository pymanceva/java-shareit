package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " was not found."));

        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwner(owner);

        try {
            itemRepository.save(item);
            return ItemMapper.mapToItemDto(item);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Item was not save " + itemDto);
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
            return ItemMapper.mapToItemDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Item was not update " + itemDto);
        }
    }

    @Transactional
    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> getAllByUserId(Long userId) {
        Collection<Item> items = itemRepository.findAllByOwnerId(userId, Sort.by(Sort.Direction.ASC, "id"));

        return items.stream()
                .map(item -> getById(item.getId(), userId))
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

        return ItemMapper.mapToItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " was not found."));

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsBeforeOrderByStartDesc(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now());
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now());
        }

        return ItemMapper.mapToItemDtoForOwner(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return ItemMapper.mapToItemDto(itemRepository.search(text));
        }
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item with id " + itemId + " was not found."));

        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " was not found."));

        if (bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                .isEmpty()) {
            throw new NotAvailableException("Cannot comment item you have never booked.");
        }


        Comment comment = CommentMapper.mapToComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setItem(item);

        try {
            return CommentMapper.mapToCommentDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Comment was not created.");
        }
    }
}

