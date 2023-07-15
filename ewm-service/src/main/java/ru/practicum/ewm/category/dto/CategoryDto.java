package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryDto {

    private Long id;

    @Size(min = 1, max = 50)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

}
