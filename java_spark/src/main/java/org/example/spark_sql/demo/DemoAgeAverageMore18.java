package org.example.spark_sql.demo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-25  10:41
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoAgeAverageMore18 {
    public static void main(String[] args) {
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> dataFromFile = SparkUtils.getDataFromFile("");
        dataFromFile.createOrReplaceTempView("user_info");
        Dataset<Row> sql = spark.sql("select * from user_info");
        sql.show();
        spark.close();
    }
}
