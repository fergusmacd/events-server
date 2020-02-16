package com.mononokehime.events;

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
import com.mononokehime.events.data.EmployeeRepository;
import com.mononokehime.events.data.Event;
import com.mononokehime.events.data.EventRepository;
import com.neovisionaries.i18n.CountryCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initEmployeeDatabase(final EmployeeRepository repository) {
        return args -> {
            String firstName = "bilbo";
            String lastName = "baggins";
            String role = "wraith";
            Employee employee = Employee.builder().id(new Long(1)).firstName(firstName).lastName(lastName).role(role).build();
            log.info("Preloading " + repository.save(employee));
            firstName = "Frodo";
            lastName = "Baggins";
            role = "thief";
            employee = Employee.builder().id(new Long(2)).firstName(firstName).lastName(lastName).role(role).build();
            log.info("Preloading " + repository.save(employee));
        };
    }
    @Bean
    CommandLineRunner initEventsDatabase(final EventRepository repository) {
        return args -> {
            String countryCode = CountryCode.getByCode("HK").getAlpha2();
            String description = "The band are really good";
            String name = "Green Day";
            String bookingURL = "https://a.long.url";
            String venue = "AsiaWorldExpo";

            DateTime dateTime = new DateTime();
            Event entity = Event.builder().id(new Long(3)).countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).startDateTime(dateTime).venue(venue).build();

            log.info("Preloading " + repository.save(entity));
            countryCode = CountryCode.getByCode("HK").getAlpha2();
            description = "The band are really good";
            name = "Pixies";
            bookingURL = "https://a.long.url";
            venue = "Kitec";

            dateTime = new DateTime();
            entity = Event.builder().id(new Long(4)).countryCode(countryCode).description(description).name(name).bookingURL(bookingURL).startDateTime(dateTime).venue(venue).build();
            log.info("Preloading " + repository.save(entity));
        };
    }
}
