package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor
public class UpdateCompilationRequest {

    private final Set<Long> events;

    @NotNull
    private final Boolean pinned;

    @Size(max = 50)
    @NotBlank(message = "Заголовок подборки не может быть пустым")
    private final String title;

}
