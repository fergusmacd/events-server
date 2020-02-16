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

import com.mononokehime.events.data.Event;
import com.mononokehime.events.data.EventNotFoundException;
import com.mononokehime.events.data.EventRepository;
import com.mononokehime.events.model.EventDTO;
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
class EventController {

    @Autowired
    private Environment env;

    private final ModelMapper modelMapper = new ModelMapper();

    private final EventRepository repository;

    private final EventModelAssembler assembler;

    EventController(final EventRepository repository,
                    final EventModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    private List<EventDTO> convertEntityListToModelList(final List<Event> events) {
        return events.stream()
                .map(event -> modelMapper.map(event, EventDTO.EventDTOBuilder.class).build())
                .collect(Collectors.toList());
    }

    // Aggregate root
    @GetMapping("/events")
    public CollectionModel<EntityModel<EventDTO>> getAll() {
        // bit of a grim call!
        List<EntityModel<EventDTO>> events = convertEntityListToModelList(repository.findAll()).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(events,
                linkTo(methodOn(EventController.class).getAll()).withSelfRel());
    }

    @Timed("event-create")
    @PostMapping("/events")
    public ResponseEntity<?> newEvent(@Valid @RequestBody final EventDTO eventDTO) throws URISyntaxException {

        Event event = modelMapper.map(eventDTO, Event.EventBuilder.class).build();
        Event newEvent = repository.save(event);

        EventDTO newEventDTO = modelMapper.map(newEvent, EventDTO.EventDTOBuilder.class).build();

        Link link = linkTo(EventController.class).slash(newEventDTO.getId()).withSelfRel().withRel("events");
        return ResponseEntity
                .created(new URI(link.getHref()))
                .body(assembler.toModel(newEventDTO));
    }

    // Single item

    @GetMapping("/events/{id}")
    public EntityModel<EventDTO> one(@PathVariable final Long id) {
        log.debug("********* entered one /event/{id}");
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        EventDTO eventDTO = modelMapper.map(event, EventDTO.EventDTOBuilder.class).build();
        ResponseEntity.ok(eventDTO);
        return assembler.toModel(eventDTO);
    }

    @Timed("event-update")
    @PutMapping("/events/{id}")
    public ResponseEntity<?> replaceEvent(@RequestBody final EventDTO eventDTO, @PathVariable final Long id) throws URISyntaxException {
        Event newEvent = modelMapper.map(eventDTO, Event.EventBuilder.class).build();

        Event updatedEvent = repository.findById(id)
                .map(event -> {
                    event.setBookingURL(newEvent.getBookingURL());
                    event.setDescription(newEvent.getDescription());
                    event.setName(newEvent.getName());
                    event.setVenue(newEvent.getVenue());
                    return repository.save(event);
                })
                .orElseGet(() -> {
                    newEvent.setId(id);
                    return repository.save(newEvent);
                });

        EventDTO newEventDTO = modelMapper.map(updatedEvent, EventDTO.EventDTOBuilder.class).build();
        EntityModel<EventDTO> resource = assembler.toModel(newEventDTO);
        Link link = linkTo(EventController.class).slash(newEventDTO.getId()).withSelfRel().withRel("Events");
        return ResponseEntity
                .created(new URI(link.getHref()))
                .body(resource);
    }

    @Timed("event-delete")
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable final Long id) {

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
