package org.springframework.samples.petclinic.rest.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO for Pet (record-based)
 */
public record PetDto(Integer id, String name, LocalDate birthDate, PetTypeDto type, List<VisitDto> visits) {

    public PetDto {
        // ensure visits is non-null to avoid NPEs in mapping code
        if (visits == null) {
            visits = Collections.emptyList();
        }
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank if provided");
        }
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate cannot be in the future");
        }
    }

    // convenience builders and compatibility methods for existing fluent/setter call-sites
    public PetDto withId(Integer id) {
        return new PetDto(id, this.name, this.birthDate, this.type, this.visits);
    }

    public PetDto withName(String name) {
        return new PetDto(this.id, name, this.birthDate, this.type, this.visits);
    }

    public PetDto withBirthDate(LocalDate birthDate) {
        return new PetDto(this.id, this.name, birthDate, this.type, this.visits);
    }

    public PetDto withType(PetTypeDto type) {
        return new PetDto(this.id, this.name, this.birthDate, type, this.visits);
    }

    public PetDto withVisits(List<VisitDto> visits) {
        return new PetDto(this.id, this.name, this.birthDate, this.type, visits);
    }

    public PetDto addVisit(VisitDto visit) {
        List<VisitDto> newVisits = new java.util.ArrayList<>(this.visits);
        newVisits.add(visit);
        return new PetDto(this.id, this.name, this.birthDate, this.type, Collections.unmodifiableList(newVisits));
    }

    // Fluent-style methods (match generated DTO fluent API)
    public PetDto id(Integer id) { return withId(id); }
    public PetDto name(String name) { return withName(name); }
    public PetDto birthDate(LocalDate birthDate) { return withBirthDate(birthDate); }
    public PetDto type(PetTypeDto type) { return withType(type); }
    public PetDto addVisitsItem(VisitDto visit) { return addVisit(visit); }
    public PetDto addPetsItem(VisitDto visit) { return addVisit(visit); } // backward-compatible alias

    // Setter-style methods used by some tests
    public PetDto setId(Integer id) { return withId(id); }
    public PetDto setName(String name) { return withName(name); }
    public PetDto setBirthDate(LocalDate birthDate) { return withBirthDate(birthDate); }
    public PetDto setType(PetTypeDto type) { return withType(type); }
    public PetDto setVisits(List<VisitDto> visits) { return withVisits(visits); }

    // Getter-style compatibility for code expecting getX() methods
    public Integer getId() { return id; }
    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
    public PetTypeDto getType() { return type; }
    public List<VisitDto> getVisits() { return visits; }

}
