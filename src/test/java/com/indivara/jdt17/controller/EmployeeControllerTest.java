package com.indivara.jdt17.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indivara.jdt17.exception.DuplicateEmailException;
import com.indivara.jdt17.exception.EmployeeNotFoundException;
import com.indivara.jdt17.model.Employee;
import com.indivara.jdt17.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================
 *  STUDI KASUS: EmployeeController Unit Test
 * ============================================================
 *  Coverage saat ini: 2 dari 10 test sudah ditulis (20%)
 *  Target: Lengkapi semua method yang ditandai TODO
 *
 *  ATURAN:
 *  - Setiap positive test HARUS punya minimal 1 negative test
 *  - Gunakan pola AAA (Arrange → Act → Assert)
 *  - Gunakan MockMvc untuk simulasi HTTP request
 *  - Perhatikan HTTP status code yang sesuai
 * ============================================================
 */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee(1L, "Budi Santoso", "budi@indivara.com", "Engineering", 15000000.0);
        employee2 = new Employee(2L, "Siti Rahma", "siti@indivara.com", "Marketing", 12000000.0);
    }

    // ====================================================================
    // GET /api/employees
    // ====================================================================

    @Test
    void getAllEmployees_ShouldReturnOkWithList() throws Exception {
        // Arrange
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Budi Santoso"))
                .andExpect(jsonPath("$[1].name").value("Siti Rahma"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    // TODO #1: GET /api/employees — ketika tidak ada data, harus return 200 dengan list kosong
    //
    // Hint:
    // - Arrange: when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList())
    // - Act & Assert: mockMvc.perform(get("/api/employees"))
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$", hasSize(0)))

    // ====================================================================
    // GET /api/employees/{id}
    // ====================================================================

    @Test
    void getEmployeeById_WhenExists_ShouldReturnOk() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(1L)).thenReturn(employee1);

        // Act & Assert
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Budi Santoso"))
                .andExpect(jsonPath("$.email").value("budi@indivara.com"))
                .andExpect(jsonPath("$.department").value("Engineering"));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    // TODO #2: GET /api/employees/{id} — ketika ID tidak ada, harus return 404
    //
    // Hint:
    // - Arrange: when(employeeService.getEmployeeById(99L))
    //                .thenThrow(new EmployeeNotFoundException(99L))
    // - Act & Assert: mockMvc.perform(get("/api/employees/99"))
    //                 .andExpect(status().isNotFound())
    //                 .andExpect(jsonPath("$.message").value(containsString("99")))

    // ====================================================================
    // POST /api/employees
    // ====================================================================

    // TODO #3: POST /api/employees — dengan data valid, harus return 201 Created
    //
    // Hint:
    // - Arrange: buat newEmployee tanpa ID
    //            when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee1)
    // - Act & Assert:
    //   mockMvc.perform(post("/api/employees")
    //          .contentType(MediaType.APPLICATION_JSON)
    //          .content(objectMapper.writeValueAsString(newEmployee)))
    //          .andExpect(status().isCreated())
    //          .andExpect(jsonPath("$.name").value("Budi Santoso"))

    @Test
    void createEmployee_WithValidData_ShouldReturnCreated() throws Exception {
        //Arrange
        Employee newEmployee = new Employee();
        newEmployee.setName("Budi Santoso");
        newEmployee.setSalary(1000.00);
        newEmployee.setEmail("budi@gmail.com");
        newEmployee.setDepartment("Engineering");

        when(employeeService.createEmployee(any())).thenReturn(newEmployee);

        //Act & Assert
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Budi Santoso"));

        verify(employeeService, times(1)).createEmployee(any());
    }

    // TODO #4: POST /api/employees — dengan nama kosong, harus return 400 Bad Request
    //
    // Hint:
    // - Arrange: buat invalidEmployee dengan name = "" (kosong)
    // - Act & Assert: .andExpect(status().isBadRequest())
    //   PENTING: service TIDAK BOLEH dipanggil → verify(employeeService, never()).createEmployee(...)

    @Test
    void createEmployee_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        //Assert
        employee1.setName("");

        //Act & Assert
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).createEmployee(any());

    }

    // TODO #5: POST /api/employees — dengan email duplikat, harus return 409 Conflict
    //
    // Hint:
    // - Arrange: when(employeeService.createEmployee(any(Employee.class)))
    //                .thenThrow(new DuplicateEmailException("budi@indivara.com"))
    // - Act & Assert: .andExpect(status().isConflict())

    @Test
    void createEmployee_WhenEmailDuplicate_ShouldReturnConflict() throws  Exception {
        //Arrange
        when(employeeService.createEmployee(any())).thenThrow(new DuplicateEmailException("budi@gmail.com"));

        //Act & Assert
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isConflict());
    }

    // TODO #6: POST /api/employees — dengan salary negatif, harus return 400
    //
    // Hint:
    // - Arrange: buat employee dengan salary = -5000000.0
    // - Act & Assert: .andExpect(status().isBadRequest())

    // ====================================================================
    @Test
    void createEmployee_WhenSalaryMinus_ShouldReturnBadRequest() throws Exception {
        //Arrange
        employee1.setSalary(-1000.0);

        when(employeeService.createEmployee(any())).thenReturn(employee1);

        //Act & Assert
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).createEmployee(any());
    }
    // ====================================================================

    // TODO #7: PUT /api/employees/{id} — ketika ditemukan, harus return 200 OK
    //
    // Hint:
    // - Arrange: buat updatedEmployee, mock service return updated data
    // - Act & Assert:
    //   mockMvc.perform(put("/api/employees/1")
    //          .contentType(MediaType.APPLICATION_JSON)
    //          .content(objectMapper.writeValueAsString(updatedEmployee)))
    //          .andExpect(status().isOk())

    @Test
    void updateEmployee_WhenExist_ShouldReturnOk() throws Exception {
        //Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(2L);
        updatedEmployee.setName("Budi Santoso");
        updatedEmployee.setSalary(1000.00);
        updatedEmployee.setEmail("budi@gmail.com");
        updatedEmployee.setDepartment("Engineering");

        when(employeeService.updateEmployee(any(), any())).thenReturn(updatedEmployee);

        //Act & Assert
        mockMvc.perform(put("/api/employees/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk());

        verify(employeeService, times(1)).updateEmployee(any(), any());
    }

    // TODO #8: PUT /api/employees/{id} — ketika tidak ditemukan, harus return 404
    //
    // Hint:
    // - Arrange: when(employeeService.updateEmployee(eq(99L), any(Employee.class)))
    //                .thenThrow(new EmployeeNotFoundException(99L))
    // - Act & Assert: .andExpect(status().isNotFound())

    // ====================================================================
    @Test
    void updateEmployee_WhenNotExist_ShouldReturnNotFound() throws Exception {
        //Arrange
        when(employeeService.updateEmployee(eq(99L), any(Employee.class)))
                .thenThrow(new EmployeeNotFoundException(99L));

        //Act & Assert
        mockMvc.perform(put("/api/employees/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).updateEmployee(eq(99L), any(Employee.class));
    }
    // ====================================================================

    // TODO #9: DELETE /api/employees/{id} — ketika ditemukan, harus return 204 No Content
    //
    // Hint:
    // - Arrange: doNothing().when(employeeService).deleteEmployee(1L)
    // - Act & Assert:
    //   mockMvc.perform(delete("/api/employees/1"))
    //          .andExpect(status().isNoContent())
    // - verify bahwa deleteEmployee(1L) dipanggil

    @Test
    void deleteEmployee_WhenExists_ShouldReturnNoContent() throws Exception {
        //Arrange
        doNothing().when(employeeService).deleteEmployee(1L);

        //Act & Assert
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    // TODO #10: DELETE /api/employees/{id} — ketika tidak ditemukan, harus return 404
    //
    // Hint:
    // - Arrange: doThrow(new EmployeeNotFoundException(99L))
    //                .when(employeeService).deleteEmployee(99L)
    // - Act & Assert: .andExpect(status().isNotFound())

    @Test
    void deleteEmployee_WhenNotExist_ShouldReturnNotFound() throws Exception {
        //Arrange
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).deleteEmployee(99L);

        //Act & Assert
        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).deleteEmployee(99L);
    }

    @Test
    void getDepartment_WhenExists_ShouldReturnOk() throws  Exception {
        //Arrange
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);

        when(employeeService.getEmployeesByDepartment(any())).thenReturn(employeeList);

        //Act & Assert
        mockMvc.perform(get("/api/employees/department/1"))
                .andExpect(status().isOk());

        verify(employeeService, times(1)).getEmployeesByDepartment(any());
    }
}
