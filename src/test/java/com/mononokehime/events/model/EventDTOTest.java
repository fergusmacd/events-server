package com.mononokehime.events.model;

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
import com.mononokehime.events.data.Event;
import com.neovisionaries.i18n.CountryCode;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class EventDTOTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenConvertEntityToPostDTO_thenCorrect() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        String countryCode = CountryCode.getByCode("HK").getAlpha2();
        String description = "The band are really good";
        String name = "Green Day";
        String bookingURL = "https://a.long.url";
        String venue = "AsiaWorldExpo";

        DateTime dateTime = new DateTime();
        Event entity = Event.builder().countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).startDateTime(dateTime).venue(venue).build();

        EventDTO dto = modelMapper.map(entity, EventDTO.EventDTOBuilder.class).build();

        assertEquals(dto.getBookingURL(), entity.getBookingURL());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getCountryCode(), entity.getCountryCode());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getVenue(), entity.getVenue());
        assertEquals(dto.getStartDateTime(), entity.getStartDateTime());
    }

}
