package org.example.spark_sql.demo;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.example.spark_sql.entity.CityInfo;
import org.example.spark_sql.entity.User;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-26  16:17
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoCsvFile {
    public static void main(String[] args) {
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> csv = spark.read().option("delimiter","\\t").text("java_spark/doc/city_info.txt");
        csv.show();

        Dataset<CityInfo> map = csv.map(new MapFunction<Row, CityInfo>() {
            @Override
            public CityInfo call(Row row) throws Exception {
                String[] row2 = row.getString(0).split("\t");

                return new CityInfo(Integer.parseInt(row2[0]),row2[1],row2[2]);
            }
        }, Encoders.bean(CityInfo.class));
        map.show();
        map.write().option("sep", "\t")
                .mode(SaveMode.ErrorIfExists)
                .option("header", "true")
                .csv("outputCSV.csv");

        spark.close();
    }
}
