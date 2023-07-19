package ru.practicum.ewm.event.Comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    @NotNull
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
    private String eventTitle;
    private String authorName;
    private LocalDateTime created;

}
