package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;

/**
 * DTO for Visit (record-based)
 */
public record VisitDto(Integer id, Integer petId, LocalDate date, String description) {

    public VisitDto {
        // Basic validation: petId must be positive if provided
        if (petId != null && petId <= 0) {
            throw new IllegalArgumentException("petId must be positive");
        }
        if (description != null && description.isBlank()) {
            throw new IllegalArgumentException("description cannot be blank if provided");
        }
    }

    // convenience builders and compatibility methods to aid existing call-sites migrating from setters
    public VisitDto withId(Integer id) {
        return new VisitDto(id, this.petId, this.date, this.description);
    }

    public VisitDto withPetId(Integer petId) {
        return new VisitDto(this.id, petId, this.date, this.description);
    }

    public VisitDto withDate(LocalDate date) {
        return new VisitDto(this.id, this.petId, date, this.description);
    }

    public VisitDto withDescription(String description) {
        return new VisitDto(this.id, this.petId, this.date, description);
    }

    // Fluent-style methods (match generated DTO fluent API)
    public VisitDto id(Integer id) { return withId(id); }
    public VisitDto petId(Integer petId) { return withPetId(petId); }
    public VisitDto date(LocalDate date) { return withDate(date); }
    public VisitDto description(String description) { return withDescription(description); }

    // Setter-style methods used by some tests
    public VisitDto setId(Integer id) { return withId(id); }
    public VisitDto setPetId(Integer petId) { return withPetId(petId); }
    public VisitDto setDate(LocalDate date) { return withDate(date); }
    public VisitDto setDescription(String description) { return withDescription(description); }

    // Getter-style compatibility for code expecting getX() methods
    public Integer getId() { return id; }
    public Integer getPetId() { return petId; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }

}