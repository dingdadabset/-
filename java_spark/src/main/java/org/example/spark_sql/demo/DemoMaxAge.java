package org.example.spark_sql.demo;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.*;
import org.example.spark_sql.entity.User;
import org.example.utils.SparkUtils;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-25  10:23
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoMaxAge {
    public static void main(String[] args) {
        //SparkConf conf = SparkUtils.getConf();
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> json = spark.read().json("");
        Dataset<User> map = json.map(new MapFunction<Row, User>() {
            @Override
            public User call(Row row) throws Exception {
                return new User(row.getLong(0),row.getString(1));

            }
        }, Encoders.bean(User.class));
        KeyValueGroupedDataset<String, User> stringUserKeyValueGroupedDataset = map.groupByKey(new MapFunction<User, String>() {
            @Override
            public String call(User user) throws Exception {
                return user.getName();
            }
        }, Encoders.bean(String.class));
        stringUserKeyValueGroupedDataset.reduceGroups(new ReduceFunction<User>() {
            @Override
            public User call(User user, User t1) throws Exception {
                return new User(Math.max(user.age,t1.age),user.name);
            }
        }).show();
        spark.close();

    }
}
