package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.location.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {

    @Size(min = 20, max = 2000)
    @NotBlank(message = "Аннотация не может быть пустой")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

}
