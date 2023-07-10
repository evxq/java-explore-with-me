package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventShortDto {

    private Long id;

    private String annotation;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CategoryDto category;

    private Integer confirmedRequests;

    private String eventDate;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Integer views;

}