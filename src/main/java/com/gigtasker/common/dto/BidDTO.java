package com.gigtasker.common.dto;

import java.io.Serial;
import java.io.Serializable;

public record BidDTO (
        Integer id,
        Integer taskId,
        Integer bidderId,
        String bidderName,
        Double amount,
        String status,
        String proposal,
        Integer posterId,
        String taskTitle
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
