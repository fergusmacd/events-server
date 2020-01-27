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


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee extends RepresentationModel<Employee> {

    private @Id
    @GeneratedValue
    Long id;
    @Size(min=2, message="{name.minsize}")
    @Size(max=16, message="{name.maxsize}")
    private String firstName;
    private String lastName;
    private String role;

    public Employee(final String firstName, final String lastName, final String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public final String getName() {
        return this.firstName + " " + this.lastName;
    }

    public final void setName(final String name) {
        String[] parts = name.split(" ");
        this.firstName = parts[0];
        this.lastName = parts[1];
    }
}
