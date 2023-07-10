package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventUpdateByUserDto {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;         // пользователь может отправлять запрос на изменение события, с одним из двух статусов: SEND_TO_REVIEW, CANCEL_REVIEW

    @Size(min = 3, max = 120)
    private String title;

}
