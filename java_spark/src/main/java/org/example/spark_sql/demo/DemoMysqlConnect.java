package org.example.spark_sql.demo;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.example.utils.SparkUtils;

import java.util.Properties;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql.demo
 * @Author: dingquan
 * @CreateTime: 2023-05-26  17:15
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoMysqlConnect {
    public static void main(String[] args) {
        DataFrameReader read = SparkUtils.getSpark().read();
        Properties properties = new Properties();
        properties.put("user","root");
        properties.put("password","password");

        Dataset<Row> base_provinceDS = read
                .jdbc("jdbc:mysql://localhost:3306/sg_blog", "sys_user", properties);
        base_provinceDS.createOrReplaceTempView("user");
        SparkUtils.getSpark().sql("select * from user where id =1").show();
        base_provinceDS.printSchema();
        base_provinceDS.write().mode(SaveMode.Overwrite)
                .jdbc("jdbc:mysql://localhost:3306/sg_blog","test_user",properties);
        read.jdbc("jdbc:mysql://localhost:3306/sg_blog", "test_user", properties).show();



    }
}
