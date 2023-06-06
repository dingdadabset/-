package org.example.spark_sql.demo;

import org.apache.spark.sql.Dataset;
import org.example.spark_sql.entity.CityInfo;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-26  17:11
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoParquetFile {
    public static void main(String[] args) {
        Dataset<CityInfo> dataFromFileCity = SparkUtils.getDataFromFileCity();
        dataFromFileCity.write().parquet("");

    }
}
