package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.util.CommentMapper;
import ru.practicum.shareit.exceptions.EntityAvailabilityException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IllegalEntityAccessException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PageableFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Item addItem(Long userId, Item item) {
        userService.getUserById(userId);
        item.setOwner(userId);
        log.info("Предмет с названием " + item.getName() + " добавлен.");
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Long itemId, Long userId, Item item) {
        Item updatedItem = checkItemOwner(itemId, userId);
        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        log.info("Предмет с id " + itemId + " был обновлен.");
        return updatedItem;
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        checkItemOwner(itemId, userId);
        itemRepository.deleteById(itemId);
        log.info("Предмет с id " + itemId + " был удален.");
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItemById(Long itemId) {
        return checkItemExistence(itemId);
    }

    @Override
    @Transactional
    public List<ItemDtoWithBooking> getAllItemsByUserId(Long userId, Integer from, Integer size) {
        userService.getUserById(userId);
        return itemRepository.findAllByOwner(userId, PageableFactory.makePage(from, size)).stream()
                .map(ItemMapper::toItemDtoWithBooking)
                .peek(i -> i.setLastBooking(BookingMapper.toBookingDto(
                        bookingRepository.findByItemIdAndEndBeforeAndStatusNot(i.getId(),
                                LocalDateTime.now(), REJECTED))))
                .peek(i -> i.setNextBooking(BookingMapper.toBookingDto(
                        bookingRepository.findByItemIdAndStartAfterAndStatus(i.getId(),
                                LocalDateTime.now(), APPROVED))))
                .peek(i -> i.setComments(commentRepository.findAllByItemId(i.getId()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())))
                .sorted(Comparator.comparingLong(ItemDtoWithBooking::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDtoWithBooking getItemDtoById(Long itemId, Long userId) {
        ItemDtoWithBooking item = ItemMapper.toItemDtoWithBooking(checkItemExistence(itemId));
        item.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (item.getOwner().equals(userId)) {
            item.setLastBooking(BookingMapper.toBookingDto(bookingRepository.findByItemIdAndEndBeforeAndStatusNot(itemId,
                    LocalDateTime.now(), REJECTED)));
            item.setNextBooking(BookingMapper.toBookingDto(bookingRepository.findByItemIdAndStartAfterAndStatus(itemId,
                    LocalDateTime.now(), APPROVED)));
            return item;
        }
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getItemsByText(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text, PageableFactory.makePage(from, size));
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Comment comment = CommentMapper.toComment(commentDto, userService.getUserById(userId), itemId);
        comment.setCreated(LocalDateTime.now());
        if (bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now()) == null) {
            throw new EntityAvailabilityException("Пользователь с id " + userId +
                    " не может оставить комментарий предмету с id " + itemId + ", так как не пользовался им.");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAllItemsByRequestId(Long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }

    private Item checkItemExistence(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмет с id " + itemId + " не найден."));
    }

    private Item checkItemOwner(Long itemId, Long userId) {
        Item item = checkItemExistence(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new IllegalEntityAccessException("Пользователь с id " + userId +
                    " не имеет прав доступа к изменению объекта с id " + itemId + ".");
        }
        return item;
    }
}
