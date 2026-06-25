package com.indivara.jdt17.service;

import com.indivara.jdt17.exception.DuplicateEmailException;
import com.indivara.jdt17.exception.EmployeeNotFoundException;
import com.indivara.jdt17.model.Employee;
import com.indivara.jdt17.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 *  STUDI KASUS: EmployeeService Unit Test
 * ============================================================
 *  Coverage saat ini: 3 dari 17 test sudah ditulis (~18%)
 *  Target: Lengkapi semua method yang ditandai TODO
 *
 *  ATURAN:
 *  - Setiap positive test HARUS punya minimal 1 negative test
 *  - Gunakan pola AAA (Arrange → Act → Assert)
 *  - Gunakan verify() untuk memastikan repository dipanggil
 *  - Perhatikan contoh yang sudah ada sebagai referensi
 *
 *  LINE vs BRANCH COVERAGE:
 *  - TODO #1–#8  → fokus LINE coverage (setiap baris ke-run)
 *  - TODO #9–#14 → fokus BRANCH coverage (setiap cabang if/else & && diuji
 *    ke arah TRUE dan FALSE). Build GAGAL jika ada branch yang belum lengkap.
 *    Cek kolom "Missed Branches" + ikon berlian kuning di report JaCoCo.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee(1L, "Budi Santoso", "budi@indivara.com", "Engineering", 15000000.0);
        employee2 = new Employee(2L, "Siti Rahma", "siti@indivara.com", "Marketing", 12000000.0);
    }

    // ====================================================================
    // getAllEmployees
    // ====================================================================

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        // Arrange
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Budi Santoso", result.get(0).getName());
        assertEquals("Siti Rahma", result.get(1).getName());
        verify(employeeRepository, times(1)).findAll();
    }

    // TODO #1: getAllEmployees — ketika database kosong, harus return list kosong
    //
    // Hint:
    // - when(employeeRepository.findAll()).thenReturn(???)
    // - assert bahwa result.size() == 0 atau result.isEmpty()
    // - verify bahwa findAll() tetap dipanggil

    // ====================================================================
    // getEmployeeById
    // ====================================================================

    @Test
    void getEmployeeById_WhenExists_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        // Act
        Employee result = employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Budi Santoso", result.getName());
        assertEquals("budi@indivara.com", result.getEmail());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(99L)
        );

        assertTrue(exception.getMessage().contains("99"));
        verify(employeeRepository, times(1)).findById(99L);
    }

    // ====================================================================
    // createEmployee
    // ====================================================================

    // TODO #2: createEmployee — ketika data valid dan email belum ada, harus berhasil save
    //
    // Hint:
    // - Arrange: when(employeeRepository.findByEmail(...)).thenReturn(Optional.empty())
    //            when(employeeRepository.save(any(Employee.class))).thenReturn(employee1)
    // - Act:     panggil employeeService.createEmployee(employee1)
    // - Assert:  assertNotNull, assertEquals nama & email
    //            verify bahwa findByEmail() dan save() dipanggil

    @Test
    void createEmployee_WhenDataValid_ShouldSuccess() throws Exception{
        //Arrange
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.save(any())).thenReturn(employee1);

        //Act
        Employee employee = employeeService.createEmployee(employee1);

        //Assert
        assertNotNull(employee);
        assertEquals("Budi Santoso", employee.getName());
        assertEquals("budi@indivara.com", employee.getEmail());
        verify(employeeRepository, times(1)).findByEmail(anyString());
        verify(employeeRepository, times(1)).save(any());
    }

    // TODO #3: createEmployee — ketika email sudah ada, harus throw DuplicateEmailException
    //
    // Hint:
    // - Arrange: when(employeeRepository.findByEmail(...)).thenReturn(Optional.of(employee1))
    // - Act & Assert: assertThrows(DuplicateEmailException.class, ...)
    //                 verify bahwa save() TIDAK PERNAH dipanggil → verify(repo, never()).save(...)

    // ====================================================================
    @Test
    void createEmployee_WhenEmailExists_ShouldThrowDuplicateEmailException() {
        //Arrange
        when(employeeRepository.findByEmail(any())).thenReturn(Optional.of(employee1));

        //Act & Assert
        assertThrows(DuplicateEmailException.class, () -> employeeService.createEmployee(employee1));

        verify(employeeRepository, times(1)).findByEmail(any());
        verify(employeeRepository, never()).save(any());
    }
    // ====================================================================

    // TODO #4: updateEmployee — ketika employee ditemukan, harus update dan return
    //
    // Hint:
    // - Arrange: buat updatedData = new Employee(...) dengan data baru
    //            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1))
    //            when(employeeRepository.save(any(Employee.class))).thenReturn(employee1)
    // - Act:     panggil employeeService.updateEmployee(1L, updatedData)
    // - Assert:  verify bahwa findById() dan save() dipanggil

    @Test
    void updateEmployee_WhenEmployeeExists_ShouldUpdate() {
        //Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setDepartment("Engineering");
        updatedEmployee.setEmail("budi@gmail.com");
        updatedEmployee.setSalary(1000.00);
        updatedEmployee.setName("Budi Santoso");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any())).thenReturn(updatedEmployee);

        //Act
        Employee employee = employeeService.updateEmployee(1L, updatedEmployee);

        //Assert
        assertEquals("Budi Santoso", employee.getName());
        assertEquals("budi@gmail.com", employee.getEmail());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any());
    }

    // TODO #5: updateEmployee — ketika employee tidak ditemukan, harus throw exception
    //
    // Hint:
    // - Arrange: when(employeeRepository.findById(99L)).thenReturn(Optional.empty())
    // - Act & Assert: assertThrows(EmployeeNotFoundException.class, ...)
    //                 verify bahwa save() TIDAK PERNAH dipanggil

    // ====================================================================
    @Test
    void updateEmployee_WhenNotExists_ShouldThrowException() throws Exception {
        //Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(1L, employee1));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any());
    }
    // ====================================================================

    // TODO #6: deleteEmployee — ketika employee ditemukan, harus delete
    //
    // Hint:
    // - Arrange: when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1))
    //            doNothing().when(employeeRepository).delete(employee1)
    // - Act:     panggil employeeService.deleteEmployee(1L)
    // - Assert:  verify bahwa findById() dan delete() dipanggil masing-masing 1x

    @Test
    void deleteEmployee_WhenExists_ShouldDelete() {
        //Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        doNothing().when(employeeRepository).delete(employee1);

        //Act
        employeeService.deleteEmployee(1L);

        //Assert
        verify(employeeRepository, times(1)).findById(1L);
    }

    // TODO #7: deleteEmployee — ketika employee tidak ditemukan, harus throw exception
    //
    // Hint:
    // - Arrange: when(employeeRepository.findById(99L)).thenReturn(Optional.empty())
    // - Act & Assert: assertThrows(EmployeeNotFoundException.class, ...)
    //                 verify bahwa delete() TIDAK PERNAH dipanggil

    // ====================================================================
    @Test
    void deleteEmployee_WhenEmployeeNotExists_ShouldThrowException() {
        //Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).delete(any());
    }
    // ====================================================================

    // TODO #8: getEmployeesByDepartment — harus return employees dengan department yang sesuai
    //
    // Hint:
    // - Arrange: buat list dengan 1 employee (Engineering saja)
    //            when(employeeRepository.findByDepartment("Engineering")).thenReturn(list)
    // - Act:     panggil employeeService.getEmployeesByDepartment("Engineering")
    // - Assert:  assertEquals size, verify findByDepartment dipanggil

    @Test
    void getEmployeesByDepartment_WhenValid_ShouldReturnSuccess() {
        //Arrange
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);

        when(employeeRepository.findByDepartment("Engineering")).thenReturn(employeeList);

        //Act
        List<Employee> resulst = employeeService.getEmployeesByDepartment("Engineering");

        //Assert
        assertEquals(employeeList, resulst);
    }

    // ====================================================================
    // getSalaryGrade  →  LATIHAN BRANCH COVERAGE (if / else if / else)
    // ====================================================================
    // Method ini punya 3 jalur: HIGH (>= 20jt), MEDIUM (>= 10jt), LOW (< 10jt).
    // Kalau kamu hanya menulis 1 test, JaCoCo akan menampilkan berlian KUNING
    // (branch sebagian) + baris MERAH pada cabang yang belum diuji.
    // Untuk 100% branch coverage, butuh KETIGA test di bawah ini.

    // TODO #9: getSalaryGrade — gaji >= 20jt harus return "HIGH"
    //
    // Hint:
    // - Arrange: employee1.setSalary(25000000.0)
    //            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1))
    // - Act:     String grade = employeeService.getSalaryGrade(1L)
    // - Assert:  assertEquals("HIGH", grade)

    @Test
    void getSalaryGrade_WhenSalaryGreaterThanTwentyMillion_ShouldReturnHigh() {
        //Assert
        employee1.setSalary(250000000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        //Act
        String grade = employeeService.getSalaryGrade(1L);

        //Assert
        assertEquals("HIGH", grade);
    }

    // TODO #10: getSalaryGrade — gaji 10jt..<20jt harus return "MEDIUM"
    //
    // Hint:
    // - Arrange: employee1.setSalary(15000000.0)  → menguji cabang "else if"
    // - Assert:  assertEquals("MEDIUM", grade)

    @Test
    void getSalaryGrade_WhenSalaryBetweenTenAndTwentyMillion_ShouldReturnMedium() {
        //Assert
        employee1.setSalary(15000000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        //Act
        String grade = employeeService.getSalaryGrade(1L);

        //Assert
        assertEquals("MEDIUM", grade);
    }


    // TODO #11: getSalaryGrade — gaji < 10jt harus return "LOW"
    //
    // Hint:
    // - Arrange: employee1.setSalary(8000000.0)   → menguji cabang "else"
    // - Assert:  assertEquals("LOW", grade)

    // ====================================================================
    // isEligibleForBonus  →  LATIHAN BRANCH COVERAGE (operator &&)
    // ====================================================================
    // return gaji >= 10jt  &&  department == "Engineering"
    // Ada 2 kondisi = 4 branch. Butuh 3 test untuk menutup semua cabang:

    @Test
    void getSalaryGrade_WhenSalarBelowTenMillion_ShouldReturnLow() {
        //Assert
        employee1.setSalary(10000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        //Act
        String grade = employeeService.getSalaryGrade(1L);

        //Assert
        assertEquals("LOW", grade);
    }

    // TODO #12: isEligibleForBonus — gaji < 10jt harus return false
    //           (kondisi PERTAMA gagal → && langsung short-circuit ke false)
    //
    // Hint:
    // - Arrange: employee1.setSalary(5000000.0); employee1.setDepartment("Engineering")
    // - Act:     boolean result = employeeService.isEligibleForBonus(1L)
    // - Assert:  assertFalse(result)

    @Test
    void isEligibleForBonus_WhenSalaryBelowTenMillion_ShouldReturnFalse() {
        //Arrange
        employee1.setSalary(5000000.0);
        employee1.setDepartment("Engineering");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        //Act
        boolean result = employeeService.isEligibleForBonus(1L);

        //Assert
        assertFalse(result);
    }

    // TODO #13: isEligibleForBonus — gaji cukup + dept "Engineering" harus return true
    //           (kedua kondisi TRUE)
    //
    // Hint:
    // - Arrange: employee1.setSalary(15000000.0); employee1.setDepartment("Engineering")
    // - Assert:  assertTrue(result)

    @Test
    void isEligibleForBonus_WhenSalaryBelowTenMillion_ShouldReturnTrue() {
        //Arrange
        employee1.setSalary(500000000.0);
        employee1.setDepartment("Engineering");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        //Act
        boolean result = employeeService.isEligibleForBonus(1L);

        //Assert
        assertTrue(result);
    }

    // TODO #14: isEligibleForBonus — gaji cukup tapi dept BUKAN "Engineering" harus return false
    //           (kondisi KEDUA gagal)
    //
    // Hint:
    // - Arrange: employee1.setSalary(15000000.0); employee1.setDepartment("Marketing")
    // - Assert:  assertFalse(result)

    @Test
    void isEligibleForBonus_WhenSalaryEnough_ShouldReturnFalse() {
        //Arrange
        employee1.setSalary(500000000.0);
        employee1.setDepartment("Marketing");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        //Act
        boolean result = employeeService.isEligibleForBonus(1L);

        //Assert
        assertFalse(result);
    }
}
