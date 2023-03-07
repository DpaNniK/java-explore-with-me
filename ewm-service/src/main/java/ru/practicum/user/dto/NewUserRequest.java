package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotNull
    String name;
    @NotNull
    String email;
}
