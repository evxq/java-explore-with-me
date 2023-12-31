package ru.practicum.statsdto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class StatsDto {

    @NotBlank(message = "Название сервиса не должно быть пустым")
    private String app;

    @NotBlank(message = "uri запроса не должно быть пустым")
    private String uri;

    private Long hits;

}
