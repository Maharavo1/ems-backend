package com.application.ems_backend_sping.service.Impl;

import com.application.ems_backend_sping.dto.EmployeeDto;
import com.application.ems_backend_sping.entity.Employee;
import com.application.ems_backend_sping.exception.ResourceNotFoundException;
import com.application.ems_backend_sping.exception.EmailAlreadyExistsException;
import com.application.ems_backend_sping.mapper.EmployeeMapper;
import com.application.ems_backend_sping.repository.EmployeeRepository;
import com.application.ems_backend_sping.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (!employee.getEmail().equals(updatedEmployee.getEmail()) &&
                employeeRepository.existsByEmail(updatedEmployee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());

        Employee updatedEmployeeObj = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(updatedEmployeeObj);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        employeeRepository.deleteById(employeeId);
    }
}