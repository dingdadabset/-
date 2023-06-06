package org.example.spark_sql.demo;

import org.apache.spark.sql.*;
import org.example.spark_sql.entity.User;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-25  10:37
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoUserVisitCount {
    public static void main(String[] args) {
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> json = spark.read().json("");
        Dataset<User> as = json.as(Encoders.bean(User.class));
        as.groupBy(new Column("name")).count().show();
        spark.close();
    }
}
