package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFullDto {

    private Long id;

    @Size(min = 20, max = 2000)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Integer confirmedRequests;

    private String createdOn;

    @Size(min = 20, max = 7000)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    private Integer participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private String state;

    @Size(min = 3, max = 120)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String title;

    private Integer views;

}
