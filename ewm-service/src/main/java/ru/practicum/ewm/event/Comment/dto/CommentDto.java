package ru.practicum.ewm.event.Comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private String eventTitle;
    private String authorName;
    private LocalDateTime created;

}
