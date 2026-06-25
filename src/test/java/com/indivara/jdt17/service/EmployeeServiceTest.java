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

    // TODO #3: createEmployee — ketika email sudah ada, harus throw DuplicateEmailException
    //
    // Hint:
    // - Arrange: when(employeeRepository.findByEmail(...)).thenReturn(Optional.of(employee1))
    // - Act & Assert: assertThrows(DuplicateEmailException.class, ...)
    //                 verify bahwa save() TIDAK PERNAH dipanggil → verify(repo, never()).save(...)

    // ====================================================================
    // updateEmployee
    // ====================================================================

    // TODO #4: updateEmployee — ketika employee ditemukan, harus update dan return
    //
    // Hint:
    // - Arrange: buat updatedData = new Employee(...) dengan data baru
    //            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1))
    //            when(employeeRepository.save(any(Employee.class))).thenReturn(employee1)
    // - Act:     panggil employeeService.updateEmployee(1L, updatedData)
    // - Assert:  verify bahwa findById() dan save() dipanggil

    // TODO #5: updateEmployee — ketika employee tidak ditemukan, harus throw exception
    //
    // Hint:
    // - Arrange: when(employeeRepository.findById(99L)).thenReturn(Optional.empty())
    // - Act & Assert: assertThrows(EmployeeNotFoundException.class, ...)
    //                 verify bahwa save() TIDAK PERNAH dipanggil

    // ====================================================================
    // deleteEmployee
    // ====================================================================

    // TODO #6: deleteEmployee — ketika employee ditemukan, harus delete
    //
    // Hint:
    // - Arrange: when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1))
    //            doNothing().when(employeeRepository).delete(employee1)
    // - Act:     panggil employeeService.deleteEmployee(1L)
    // - Assert:  verify bahwa findById() dan delete() dipanggil masing-masing 1x

    // TODO #7: deleteEmployee — ketika employee tidak ditemukan, harus throw exception
    //
    // Hint:
    // - Arrange: when(employeeRepository.findById(99L)).thenReturn(Optional.empty())
    // - Act & Assert: assertThrows(EmployeeNotFoundException.class, ...)
    //                 verify bahwa delete() TIDAK PERNAH dipanggil

    // ====================================================================
    // getEmployeesByDepartment
    // ====================================================================

    // TODO #8: getEmployeesByDepartment — harus return employees dengan department yang sesuai
    //
    // Hint:
    // - Arrange: buat list dengan 1 employee (Engineering saja)
    //            when(employeeRepository.findByDepartment("Engineering")).thenReturn(list)
    // - Act:     panggil employeeService.getEmployeesByDepartment("Engineering")
    // - Assert:  assertEquals size, verify findByDepartment dipanggil

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

    // TODO #10: getSalaryGrade — gaji 10jt..<20jt harus return "MEDIUM"
    //
    // Hint:
    // - Arrange: employee1.setSalary(15000000.0)  → menguji cabang "else if"
    // - Assert:  assertEquals("MEDIUM", grade)

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

    // TODO #12: isEligibleForBonus — gaji < 10jt harus return false
    //           (kondisi PERTAMA gagal → && langsung short-circuit ke false)
    //
    // Hint:
    // - Arrange: employee1.setSalary(5000000.0); employee1.setDepartment("Engineering")
    // - Act:     boolean result = employeeService.isEligibleForBonus(1L)
    // - Assert:  assertFalse(result)

    // TODO #13: isEligibleForBonus — gaji cukup + dept "Engineering" harus return true
    //           (kedua kondisi TRUE)
    //
    // Hint:
    // - Arrange: employee1.setSalary(15000000.0); employee1.setDepartment("Engineering")
    // - Assert:  assertTrue(result)

    // TODO #14: isEligibleForBonus — gaji cukup tapi dept BUKAN "Engineering" harus return false
    //           (kondisi KEDUA gagal)
    //
    // Hint:
    // - Arrange: employee1.setSalary(15000000.0); employee1.setDepartment("Marketing")
    // - Assert:  assertFalse(result)
}
