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


import com.mononokehime.events.model.EmployeeDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import static org.junit.Assert.assertEquals;


public class EmployeeTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenConvertDTOToEntity_thenCorrect() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        String firstName = "bilbo";
        String lastName = "baggins";
        String role = "wraith";
        EmployeeDTO employeeDTO = EmployeeDTO.builder().firstName(firstName).lastName(lastName).role(role).build();

        Employee employee = modelMapper.map(employeeDTO, Employee.EmployeeBuilder.class).build();
        assertEquals(employee.getFirstName(), employeeDTO.getFirstName());
        assertEquals(employee.getLastName(), employeeDTO.getLastName());
        assertEquals(employee.getRole(), employeeDTO.getRole());
    }

}
