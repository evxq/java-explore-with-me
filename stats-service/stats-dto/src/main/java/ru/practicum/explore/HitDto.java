package ru.practicum.explore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class HitDto {

    private Integer id;

    @NotBlank(message = "Название сервиса не должно быть пустым")
    private String app;

    @NotBlank(message = "uri запроса не должен быть пустым")
    private String uri;

    @NotBlank(message = "ip пользователя не должен быть пустым")
    private String ip;

    @NotNull(message = "Дата запроса не может быть пустой")
    private String datetime;

}
