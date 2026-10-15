package org.springframework.samples.petclinic.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.samples.petclinic.rest.validation.PetAgeValidation;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "Pet", description = "A pet.")
public record PetDto(
    @NotNull @Min(0) Integer id,
    @NotNull @Size(max = 30) String name,
    @NotNull @PetAgeValidation LocalDate birthDate,
    @NotNull @Valid PetTypeDto type,
    @Min(0) Integer ownerId,
    @NotNull @Valid List<VisitDto> visits
) {

    public PetDto {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id must be non-null and non-negative");
        }
        if (name == null || name.isBlank() || name.length() > 30) {
            throw new IllegalArgumentException("name must be non-blank and at most 30 characters");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("birthDate must be non-null");
        }
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today) || birthDate.isBefore(today.minusYears(50))) {
            throw new IllegalArgumentException("birthDate must be within the last 50 years");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must be non-null");
        }
        if (ownerId != null && ownerId < 0) {
            throw new IllegalArgumentException("ownerId must be non-negative");
        }
        if (visits == null) {
            throw new IllegalArgumentException("visits must be non-null");
        }
        visits = List.copyOf(visits);
    }
}
