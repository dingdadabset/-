package org.example.spark_sql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-26  16:37
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
public class CityInfo implements Serializable {
    private int id;
    private String city;
    private String part;
}
