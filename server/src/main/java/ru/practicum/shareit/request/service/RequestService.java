package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Collection;

public interface RequestService {
    RequestDto add(RequestDto requestDto, Long userId);

    void delete(Long requestId, Long userId);

    Collection<RequestDto> getAll(Long userId, int from, int size);

    Collection<RequestDto> getRequestsOfUser(Long userId);

    RequestDto getById(Long userId, Long requestId);
}
