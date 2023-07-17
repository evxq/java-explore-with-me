package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFullDto {

    private Long id;

    @Size(min = 20, max = 2000)
    @NotBlank(message = "Аннотация не может быть пустой")
    private String annotation;

    @NotNull
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CategoryDto category;

    private Integer confirmedRequests;

    private String createdOn;

    @Size(min = 20, max = 7000)
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserShortDto initiator;

    @NotNull
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Location location;

    @NotNull
    private Boolean paid;

    private Integer participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private String state;

    @Size(min = 3, max = 120)
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    private Long views;

    private List<CommentDto> comments;

}
