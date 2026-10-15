package org.springframework.samples.petclinic.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(name = "Visit", description = "A booking for a vet visit.")
public record VisitDto(
    @NotNull @Min(0) Integer id,
    @NotNull @Min(0) Integer petId,
    LocalDate date,
    @NotNull @Size(min = 1, max = 255) String description
) {

    public VisitDto {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id must be non-null and non-negative");
        }
        if (petId == null || petId < 0) {
            throw new IllegalArgumentException("petId must be non-null and non-negative");
        }
        if (description == null || description.isBlank() || description.length() > 255) {
            throw new IllegalArgumentException("description must be non-blank and at most 255 characters");
        }
    }
}
