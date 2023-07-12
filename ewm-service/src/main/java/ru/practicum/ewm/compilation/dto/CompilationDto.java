package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.dto.EventFullDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor
public class CompilationDto {

    private final Long id;

    private final Set<EventFullDto> events;

    private final Boolean pinned;

    @Size(max = 50)
    @NotBlank(message = "Заголовок подборки не может быть пустым")
    private final String title;

}
