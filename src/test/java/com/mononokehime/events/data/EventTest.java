package com.mononokehime.events.data;

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


import com.mononokehime.events.model.EventDTO;
import com.neovisionaries.i18n.CountryCode;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import static org.junit.Assert.assertEquals;


public class EventTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenConvertDTOToEntity_thenCorrect() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        String countryCode = CountryCode.getByCode("HK").getAlpha2();
        String description = "The band are really good";
        String name = "Green Day";
        String bookingURL = "https://a.long.url";
        String venue = "AsiaWorldExpo";

        DateTime dateTime = new DateTime();
        EventDTO dto = EventDTO.builder().countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).startDateTime(dateTime).venue(venue).build();

        Event event = modelMapper.map(dto, Event.EventBuilder.class).build();

        assertEquals(event.getBookingURL(), dto.getBookingURL());
        assertEquals(event.getName(), dto.getName());
        assertEquals(event.getCountryCode(), dto.getCountryCode());
        assertEquals(event.getDescription(), dto.getDescription());
        assertEquals(event.getVenue(), dto.getVenue());
        assertEquals(event.getStartDateTime(), dto.getStartDateTime());
    }

}
