package ru.practicum.ewm.event.Comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixCommentDto {

    @NotNull
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;

}
