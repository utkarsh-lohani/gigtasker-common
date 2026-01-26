package com.gigtasker.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserDTO (
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        UUID keycloakId,
        String country,
        String gender,
        LocalDate dateOfBirth,
        String profileImageUrl
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
