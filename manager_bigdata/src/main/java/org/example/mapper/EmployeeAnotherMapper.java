package org.example.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.bean.Employee;

import java.util.List;

@Mapper
@DS("mybatis")
public interface EmployeeAnotherMapper
{
    
    @Select("select * from employee where id = #{a}")
    Employee getEmployeeById(@Param("a")Integer id);
    @Select("select * from employee where gender = ${gender}") //这种叫做sql注入
    List<Employee> getEmpsByCondition(@Param("gender") String gender);
}
