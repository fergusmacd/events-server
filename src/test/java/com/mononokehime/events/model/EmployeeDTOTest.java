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
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class EmployeeDTOTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenConvertEntityToPostDTO_thenCorrect() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        Long id = Long.valueOf(100000000);
        String firstName = "bilbo";
        String lastName = "baggins";
        String role = "wraith";
        Employee employee = Employee.builder().id(id).firstName(firstName).lastName(lastName).role(role).build();

        EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.EmployeeDTOBuilder.class).build();
        assertEquals(employee.getId(), employeeDTO.getId());
        assertEquals(employee.getFirstName(), employeeDTO.getFirstName());
        assertEquals(employee.getLastName(), employeeDTO.getLastName());
        assertEquals(employee.getRole(), employeeDTO.getRole());
    }

    @Test
    public void whenConvertEntityListToDTOList_thenCorrect() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        final Collection<Employee> employeeList = new ArrayList<>();
        String firstName = "bilbo";
        String lastName = "baggins";
        String role = "wraith";
        Employee employee = Employee.builder().firstName(firstName).lastName(lastName).role(role).build();
        employeeList.add(employee);
        firstName = "sam";
        lastName = "gangee";
        role = "holder";
        employee = Employee.builder().firstName(firstName).lastName(lastName).role(role).build();
        employeeList.add(employee);
        firstName = "merry";
        lastName = "merridock";
        role = "fighter";
        employee = Employee.builder().firstName(firstName).lastName(lastName).role(role).build();
        employeeList.add(employee);


        List<EmployeeDTO> employeesDTO = employeeList.stream()
                .map(employe -> modelMapper.map(employe, EmployeeDTO.EmployeeDTOBuilder.class).build())
                .collect(Collectors.toList());

        assertEquals(employeeList.size(), employeesDTO.size());
    }

}
