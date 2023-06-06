package org.example.spark_sql.test;

import org.apache.spark.sql.SparkSession;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql.test
 * @Author: dingquan
 * @CreateTime: 2023-05-26  17:34
 * @Description: TODO
 * @Version: 1.0
 */
public class Test01AccumulateTop3 {
    public static void main(String[] args) {

        SparkSession spark = SparkUtils.getSpark();
        SparkSession orCreate = spark.builder().enableHiveSupport().getOrCreate();
        orCreate.sql("");
    }
}
