package com.indivara.jdt17.service;

import com.indivara.jdt17.exception.DuplicateEmailException;
import com.indivara.jdt17.exception.EmployeeNotFoundException;
import com.indivara.jdt17.model.Employee;
import com.indivara.jdt17.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee createEmployee(Employee employee) {
        employeeRepository.findByEmail(employee.getEmail())
                .ifPresent(existing -> {
                    throw new DuplicateEmailException(employee.getEmail());
                });
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        existing.setName(updatedEmployee.getName());
        existing.setEmail(updatedEmployee.getEmail());
        existing.setDepartment(updatedEmployee.getDepartment());
        existing.setSalary(updatedEmployee.getSalary());

        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employeeRepository.delete(employee);
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    // ====================================================================
    //  LATIHAN BRANCH COVERAGE
    //  Dua method di bawah punya PERCABANGAN (if/else if/else dan &&).
    //  Untuk mencapai 100% BRANCH coverage, setiap cabang harus diuji
    //  ke arah TRUE dan FALSE — bukan cuma satu sisi.
    // ====================================================================

    /**
     * Menentukan grade gaji karyawan.
     * Percabangan: if / else if / else → ada 3 jalur (HIGH, MEDIUM, LOW).
     * Untuk 100% branch coverage butuh minimal 3 test (satu per grade).
     */
    public String getSalaryGrade(Long id) {
        Employee employee = getEmployeeById(id);
        double salary = employee.getSalary();

        if (salary >= 20_000_000) {
            return "HIGH";
        } else if (salary >= 10_000_000) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Karyawan berhak bonus jika gaji minimal 10jt DAN bekerja di "Engineering".
     * Percabangan: operator && → ada 2 kondisi (4 branch).
     * Untuk 100% branch coverage butuh minimal 3 test:
     *   1) gaji di bawah 10jt              → false (kondisi pertama gagal)
     *   2) gaji cukup + dept Engineering   → true
     *   3) gaji cukup + dept lain          → false (kondisi kedua gagal)
     */
    public boolean isEligibleForBonus(Long id) {
        Employee employee = getEmployeeById(id);
        return employee.getSalary() >= 10_000_000
                && "Engineering".equals(employee.getDepartment());
    }
}
