package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor
public class NewCompilationDto {

    private final Set<Long> events;

    private final Boolean pinned;

    @Size(max = 50)
    @NotBlank(message = "Заголовок подборки не может быть пустым")
    private final String title;

}
