package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {

    @Size(min = 20, max = 2000)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String annotation;

    private Integer category_id;

    @Size(min = 20, max = 7000)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String title;

}
