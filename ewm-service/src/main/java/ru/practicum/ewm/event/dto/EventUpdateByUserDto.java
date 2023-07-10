package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.model.Location;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventUpdateByUserDto {

    private String annotation;

    private Long category_id;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;         // пользователь может отправлять запрос на изменение события, с одним из двух статусов: SEND_TO_REVIEW, CANCEL_REVIEW

    private String title;

}
