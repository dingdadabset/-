package org.example.service;

import org.example.bean.Employee;

import java.util.List;

public interface EmployeeService {

    //必须至少提供5种方法对应用户的5种操作
    //增删改查
    Employee getEmployeeById(Integer id);

    void insertEmployee(Employee employee);

    void updateEmployee(Employee employee);

    void deleteEmployeeById(Integer id);

    // 查询所有
    List<Employee> getAll();
}
