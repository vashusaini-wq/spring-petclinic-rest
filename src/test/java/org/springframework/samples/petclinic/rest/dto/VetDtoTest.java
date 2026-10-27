package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class VetDtoTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void validEmail_passesValidation() {
        VetDto dto = new VetDto();
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setEmail("james.carter@petclinic.example");
        dto.setSpecialties(List.of());

        Set<ConstraintViolation<VetDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidEmail_failsValidation() {
        VetDto dto = new VetDto();
        dto.setEmail("not-an-email");

        Set<ConstraintViolation<VetDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void nullEmail_isAllowed() {
        VetDto dto = new VetDto();
        dto.setEmail(null);

        Set<ConstraintViolation<VetDto>> violations = validator.validate(dto);
        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void oversizedEmail_failsValidation() {
        VetDto dto = new VetDto();
        dto.setEmail("a".repeat(250) + "@x.com"); // > 254 chars

        Set<ConstraintViolation<VetDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
