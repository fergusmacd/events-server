package com.mononokehime.events.controller;

/*-
 * #%L
 * Events Server Application
 * %%
 * Copyright (C) 2020 Mononokehime
 * %%
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
 * #L%
 */

import com.mononokehime.events.data.Employee;
import com.mononokehime.events.data.EmployeeNotFoundException;
import com.mononokehime.events.data.EmployeeRepository;
import com.mononokehime.events.model.EmployeeDTO;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@Slf4j
class EmployeeController {

    @Autowired
    private Environment env;

    private final ModelMapper modelMapper = new ModelMapper();

    public String getGoogleKey() {
        return env.getProperty("fake-key");
    }
    private final EmployeeRepository repository;

    private final EmployeeModelAssembler assembler;

    EmployeeController(final EmployeeRepository repository,
                       final EmployeeModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    private List<EmployeeDTO> convertEntityListToModelList(final List<Employee> employees) {
        return employees.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.EmployeeDTOBuilder.class).build())
                .collect(Collectors.toList());
    }

    // Aggregate root
    @GetMapping("/employees")
    public CollectionModel<EntityModel<EmployeeDTO>> getAll() {
        log.debug("********** entered all /employees" + getGoogleKey());
        // bit of a grim call!
        List<EntityModel<EmployeeDTO>> employees = convertEntityListToModelList(repository.findAll()).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(employees,
                linkTo(methodOn(EmployeeController.class).getAll()).withSelfRel());
    }

    @Timed("employees-create")
    @PostMapping("/employees")
    public ResponseEntity<?> newEmployee(@Valid @RequestBody final EmployeeDTO employeeDTO) throws URISyntaxException {

        Employee employee = modelMapper.map(employeeDTO, Employee.EmployeeBuilder.class).build();
        Employee newEmployee = repository.save(employee);

        EmployeeDTO newEmployeeDTO = modelMapper.map(newEmployee, EmployeeDTO.EmployeeDTOBuilder.class).build();
        Link link = linkTo(EmployeeController.class).slash(newEmployeeDTO.getId()).withSelfRel().withRel("employees");
        return ResponseEntity
                .created(new URI(link.getHref()))
                .body(assembler.toModel(newEmployeeDTO));
    }

    // Single item

    @GetMapping("/employees/{id}")
    public EntityModel<EmployeeDTO> one(@PathVariable final Long id) {
        log.debug("********* entered one /employees/{id}");
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.EmployeeDTOBuilder.class).build();
        ResponseEntity.ok(employeeDTO);
        return assembler.toModel(employeeDTO);
    }

    @Timed("employees-update")
    @PutMapping("/employees/{id}")
    public ResponseEntity<?> replaceEmployee(@RequestBody final EmployeeDTO employeeDTO, @PathVariable final Long id) throws URISyntaxException {
        Employee newEmployee = modelMapper.map(employeeDTO, Employee.EmployeeBuilder.class).build();

        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setFirstName(newEmployee.getFirstName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });

        EmployeeDTO newEmployeeDTO = modelMapper.map(updatedEmployee, EmployeeDTO.EmployeeDTOBuilder.class).build();
        EntityModel<EmployeeDTO> resource = assembler.toModel(newEmployeeDTO);
        Link link = linkTo(EmployeeController.class).slash(newEmployeeDTO.getId()).withSelfRel().withRel("employees");
        return ResponseEntity
                .created(new URI(link.getHref()))
                .body(resource);
    }

    @Timed("employee-delete")
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable final Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private Map<String, String> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
