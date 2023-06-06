package org.example.mapper;

import org.example.bean.Employee;

import java.util.List;

public interface EmployeeMapper {

    //增删改查
    Employee getEmployeeById(Integer id);

    void insertEmployee(Employee employee);

    void updateEmployee(Employee employee);

    void deleteEmployeeById(Integer id);

    // 查询所有
    List<Employee> getAll();

}
