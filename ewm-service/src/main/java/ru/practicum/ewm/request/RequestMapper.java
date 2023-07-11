package ru.practicum.ewm.request;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.utility.DateParser;

public class RequestMapper {

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .created(DateParser.parseDate
                        (participationRequestDto.getCreated())).build();
    }

    public static ParticipationRequestDto toRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.toString())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

}
