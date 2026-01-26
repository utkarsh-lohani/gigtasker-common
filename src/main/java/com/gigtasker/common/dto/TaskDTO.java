package com.gigtasker.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public record TaskDTO (
        Long id,
        String title,
        String description,
        Long posterUserId,
        Long assignedUserId,
        String status,
        Instant deadline,
        Double minPay,
        Double maxPay,
        Integer maxBidsPerUser
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
