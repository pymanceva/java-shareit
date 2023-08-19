package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

@UtilityClass
public class RequestMapper {

    public static RequestDto mapToRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequesterId(request.getRequester().getId());
        requestDto.setItems(ItemMapper.mapToItemDto(request.getItems()));
        return requestDto;
    }

    public static Request mapToRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setCreated(requestDto.getCreated());
        return request;
    }
}
