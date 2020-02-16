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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mononokehime.events.EventsApplication;
import com.mononokehime.events.config.CustomMessageSourceConfiguration;
import com.mononokehime.events.data.EventRepository;
import com.mononokehime.events.data.SinglePostgresqlContainer;
import com.mononokehime.events.model.EventDTO;
import com.neovisionaries.i18n.CountryCode;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.Is;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EventsApplication.class)
@AutoConfigureMockMvc
@Slf4j
public class EventControllerTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = SinglePostgresqlContainer.getInstance();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EventRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CustomMessageSourceConfiguration messageSource;

    @Test
    public void givenEvents_whenAll_thenReturnJsonArray()
            throws Exception {

        MvcResult result = mvc.perform(get("/events")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.eventDTOList", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/events"))).andReturn();

    }

    @Test
    public void givenEVENTS_whenRequestOne_thenReturnJsonArray()
            throws Exception {
        Integer id = new Integer(3);
        MvcResult result = mvc.perform(get("/events/"+id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.countryCode", is("HK")))
                .andExpect(jsonPath("$.venue", is("AsiaWorldExpo"))).andReturn();
    }

    @Test
    public void givenEvents_whenRequestOne_thenReturnNotFound()
            throws Exception {
        Integer id = new Integer(26);
        mvc.perform(get("/events/"+id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    public void createEvents_whenCreateOneNoName_thenReturnError()
            throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String countryCode = CountryCode.getByCode("HK").getAlpha2();
        String description = "The band are really good";
        String name = "G";
        String bookingURL = "https://a.long.url";
        String venue = "AsiaWorldExpo";

        EventDTO dto = EventDTO.builder().countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).venue(venue).build();
        String json = mapper.writeValueAsString(dto);
        MvcResult result = mvc.perform(post("/events")
                .content(json)
                .contentType(textPlainUtf8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", Is.is("Name should have at least 2 characters")))
                .andReturn();

    }

    @Test
    @DirtiesContext
    public void createEvent_whenCreateOne_thenReturnJsonArray()
            throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String countryCode = CountryCode.getByCode("HK").getAlpha2();
        String description = "The band are really good";
        String name = "Gross Out";
        String bookingURL = "https://a.long.url";
        String venue = "AsiaWorldExpo";

        EventDTO dto = EventDTO.builder().countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).venue(venue).build();
        String json = mapper.writeValueAsString(dto);
        MvcResult result = mvc.perform(post("/events")
                .content(json)
                .contentType(textPlainUtf8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.venue", is(dto.getVenue())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/events/5")))
                .andExpect(jsonPath("$._links.events.href", is("http://localhost/events")))
                .andReturn();
    }

    @Test
    @DirtiesContext
    public void updateEvent_whenUpdateOne_thenReturnJsonArray()
            throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String countryCode = CountryCode.getByCode("HK").getAlpha2();
        String description = "The band are really ok";
        String name = "Gross Out";
        String bookingURL = "https://a.long.url";
        String venue = "AsiaWorldExpo";

        EventDTO dto = EventDTO.builder().countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).venue(venue).build();
        String json = mapper.writeValueAsString(dto);
        MvcResult result = mvc.perform(put("/events/3")
                .content(json)
                .contentType(textPlainUtf8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.venue", is(dto.getVenue())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/events/3")))
                .andExpect(jsonPath("$._links.events.href", is("http://localhost/events")))
                .andReturn();
    }

    @Test
    public void updateEvent_whenUpdateOne_thenThrowNotAllowed()
            throws Exception {
        MediaType textPlainUtf8 = new MediaType(MediaType.APPLICATION_JSON, Charset.forName("UTF-8"));
        String countryCode = CountryCode.getByCode("HK").getAlpha2();
        String description = "The band are really ok";
        String name = "Gross Out";
        String bookingURL = "https://a.long.url";
        String venue = "AsiaWorldExpo";

        EventDTO dto = EventDTO.builder().countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).venue(venue).build();
        String json = mapper.writeValueAsString(dto);
        MvcResult result = mvc.perform(post("/employees/1")
                .content(json)
                .contentType(textPlainUtf8))
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
    }
}
