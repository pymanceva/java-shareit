package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.NotSavedException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequestDto add(RequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " was not found."));

        Request request = RequestMapper.mapToRequest(requestDto);
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);

        try {
            requestRepository.save(request);
            log.info("New request id " + request.getId() + " has been saved.");
            return RequestMapper.mapToRequestDto(request);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("Request was not save " + requestDto);
        }
    }

    @Override
    @Transactional
    public void delete(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request id " + requestId + " was not found."));

        if (request.getRequester().getId().equals(userId)) {
            requestRepository.deleteById(requestId);
            log.info("Existed request id " + requestId + " has been deleted.");
        } else {
            throw new NotOwnerException("Request can by deleted by requester only.");
        }
    }

    @Override
    public Collection<RequestDto> getAll(Long userId, int from, int size) {
        Collection<Request> requests = requestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(userId, PageRequest.of(from, size));

        log.info("List of all requests length " + requests.size() + " has been gotten.");

        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<RequestDto> getRequestsOfUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " does not exist."));

        Collection<Request> requests = requestRepository
                .findAllByRequesterIdOrderByCreatedDesc(userId);

        log.info("List of requests of user id " + userId + " length " + requests.size() + " has been gotten.");

        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id " + userId + " does not exist."));

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request id " + requestId + " was not found."));

        log.info("Request id " + requestId + " has been gotten.");

        return RequestMapper.mapToRequestDto(request);
    }
}
