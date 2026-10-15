package org.springframework.samples.petclinic.rest.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class DtoRecordValidationTests {

    private final PetTypeDto petType = new PetTypeDto().id(1).name("dog");

    @Test
    void petDtoRejectsNullRequiredValuesAndInvalidBoundaries() {
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(null, "Rosy", LocalDate.now(), petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "Rosy", null, petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, " ", LocalDate.now(), petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "x".repeat(31), LocalDate.now(), petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(-1, "Rosy", LocalDate.now(), petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "Rosy", LocalDate.now().minusYears(50).minusDays(1), petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "Rosy", LocalDate.now().plusDays(1), petType, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "Rosy", LocalDate.now(), null, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "Rosy", LocalDate.now(), petType, -1, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PetDto(1, "Rosy", LocalDate.now(), petType, null, null));
    }

    @Test
    void petDtoDefensivelyCopiesVisitList() {
        List<VisitDto> visits = new ArrayList<>();
        PetDto pet = new PetDto(1, "Rosy", LocalDate.now(), petType, null, visits);

        assertNotSame(visits, pet.visits());
        assertEquals(List.of(), pet.visits());
        assertThrows(UnsupportedOperationException.class, () -> pet.visits().add(null));
    }

    @Test
    void visitDtoRejectsNullRequiredValuesAndInvalidBoundaries() {
        assertThrows(IllegalArgumentException.class,
            () -> new VisitDto(null, 1, LocalDate.now(), "checkup"));
        assertThrows(IllegalArgumentException.class,
            () -> new VisitDto(1, null, LocalDate.now(), "checkup"));
        assertThrows(IllegalArgumentException.class,
            () -> new VisitDto(1, -1, LocalDate.now(), "checkup"));
        assertThrows(IllegalArgumentException.class,
            () -> new VisitDto(1, 1, LocalDate.now(), null));
        assertThrows(IllegalArgumentException.class,
            () -> new VisitDto(1, 1, LocalDate.now(), " "));
        assertThrows(IllegalArgumentException.class,
            () -> new VisitDto(1, 1, LocalDate.now(), "x".repeat(256)));
    }
}
