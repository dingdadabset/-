package org.example.spark_sql.demo;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.example.spark_sql.entity.CityInfo;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-26  17:08
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoJsonFile {
    public static void main(String[] args) {
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> csv = spark.read().option("delimiter","\\t").text("java_spark/doc/city_info.txt");
        Dataset<CityInfo> map = csv.map(new MapFunction<Row, CityInfo>() {
            @Override
            public CityInfo call(Row row) throws Exception {
                String[] row2 = row.getString(0).split("\t");

                return new CityInfo(Integer.parseInt(row2[0]),row2[1],row2[2]);
            }
        }, Encoders.bean(CityInfo.class));
        map.write().json("java_spark/doc/city_info.json");
        spark.close();
    }
}
