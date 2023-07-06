package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    @Size(min = 2, max = 250)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @Size(min = 6, max = 254)
    @NotBlank(message = "Адрес не может быть пустым")
    @Email
    private String email;

}
