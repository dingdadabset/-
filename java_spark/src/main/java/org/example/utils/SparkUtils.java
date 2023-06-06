package org.example.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.example.spark_sql.entity.CityInfo;

import java.util.regex.MatchResult;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.utils
 * @Author: dingquan
 * @CreateTime: 2023-05-25  10:22
 * @Description: TODO
 * @Version: 1.0
 */
public class SparkUtils {
    private static SparkConf conf;
    private static SparkSession spark ;
    static {
        conf= new SparkConf().setMaster("local[*]").setAppName("sparkCore");
        spark = SparkSession.builder().config(conf).getOrCreate();
    }

    public static Dataset<Row> getDataFromFile(String name){
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> csv = spark.read().option("delimiter","\\t").text("java_spark/doc/city_info.txt");

        return csv;
    }
    public static Dataset<CityInfo> getDataFromFileCity(){

        Dataset<Row> csv = spark.read().option("delimiter","\\t").text("java_spark/doc/city_info.txt");
        Dataset<CityInfo> map = csv.map(new MapFunction<Row, CityInfo>() {
            @Override
            public CityInfo call(Row row) throws Exception {
                String[] row2 = row.getString(0).split("\t");

                return new CityInfo(Integer.parseInt(row2[0]),row2[1],row2[2]);
            }
        }, Encoders.bean(CityInfo.class));
        return map;
    }

    public static<T> boolean saveDataToDifFile(String fileType,String path,Dataset<T> dataset){

        return true;

    }
    public static SparkConf getConf() {
        return conf;
    }

    public static void setConf(SparkConf conf) {
        SparkUtils.conf = conf;
    }

    public static SparkSession getSpark() {
        return spark;
    }

    public static void setSpark(SparkSession spark) {
        SparkUtils.spark = spark;
    }
}
