package org.example.spark_sql.demo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;
import org.example.utils.SparkUtils;
import static org.apache.spark.sql.functions.udf;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-25  10:45
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoUdfFunction {
    public static void main(String[] args) {
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> dataFromFile = SparkUtils.getDataFromFile("");
        UserDefinedFunction udf = udf(new UDF1<String, String>() {
            @Override
            public String call(String s) throws Exception {
                return s + "ading";
            }
        }, DataTypes.StringType);
        UserDefinedFunction addName = spark.udf().register("addName", udf);
        dataFromFile.createOrReplaceTempView("user_info");
        spark.sql("select addName(name) from user_info").show();
        spark.close();
    }

}
