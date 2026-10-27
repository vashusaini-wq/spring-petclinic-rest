/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.rest.controller.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.mapper.VetMapper;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.api.VetsApi;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VetRestControllerV1 implements VetsApi {

    private static final Logger logger = LoggerFactory.getLogger(VetRestControllerV1.class);

    private final ClinicService clinicService;
    private final VetMapper vetMapper;
    private final SpecialtyMapper specialtyMapper;

    public VetRestControllerV1(ClinicService clinicService, VetMapper vetMapper, SpecialtyMapper specialtyMapper) {
        this.clinicService = clinicService;
        this.vetMapper = vetMapper;
        this.specialtyMapper = specialtyMapper;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<VetDto>> listVets() {
        List<VetDto> vets = new ArrayList<>(vetMapper.toVetDtos(this.clinicService.findAllVets()));
        if (vets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        audit("list", "vets");
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .body(vets);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> getVet(Integer vetId)  {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        audit("read", String.valueOf(vetId));
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .body(vetMapper.toVetDto(vet));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> addVet(VetDto vetDto) {
        HttpHeaders headers = new HttpHeaders();
        Vet vet = vetMapper.toVet(vetDto);
        if(vet.getNrOfSpecialties() > 0){
            List<Specialty> vetSpecialities = this.clinicService.findSpecialtiesByNameIn(vet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
            vet.setSpecialties(vetSpecialities);
        }
        this.clinicService.saveVet(vet);
        audit("create", String.valueOf(vet.getId()));
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/vets/{id}").buildAndExpand(vet.getId()).toUri());
        headers.setCacheControl(CacheControl.noStore());
        return new ResponseEntity<>(vetMapper.toVetDto(vet), headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> updateVet(Integer vetId,VetDto vetDto)  {
        Vet currentVet = this.clinicService.findVetById(vetId);
        if (currentVet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentVet.setFirstName(vetDto.getFirstName());
        currentVet.setLastName(vetDto.getLastName());
        currentVet.setEmail(vetDto.getEmail());
        currentVet.clearSpecialties();
        for (Specialty spec : specialtyMapper.toSpecialtys(vetDto.getSpecialties())) {
            currentVet.addSpecialty(spec);
        }
        if(currentVet.getNrOfSpecialties() > 0){
            List<Specialty> vetSpecialities = this.clinicService.findSpecialtiesByNameIn(currentVet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
            currentVet.setSpecialties(vetSpecialities);
        }
        this.clinicService.saveVet(currentVet);
        audit("update", String.valueOf(vetId));
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore());
        return new ResponseEntity<>(vetMapper.toVetDto(currentVet), headers, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VetDto> deleteVet(Integer vetId) {
        Vet vet = this.clinicService.findVetById(vetId);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVet(vet);
        audit("delete", String.valueOf(vetId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void audit(String action, String vetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Vet profile audit: action={}, vetId={}, actor={}", action, vetId, authentication.getName());
    }
}
