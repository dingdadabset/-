package org.example.spark_sql.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-26  16:38
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ProductInfo implements Serializable {
    private int id;
    private String sku;
    private String self;
}
