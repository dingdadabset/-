package org.example.spark_sql.demo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.example.utils.SparkUtils;

import static org.apache.spark.sql.functions.udaf;

/**
 * @BelongsProject: all_project_on_atguigu
 * @BelongsPackage: org.example.spark_sql
 * @Author: dingquan
 * @CreateTime: 2023-05-25  10:51
 * @Description: TODO
 * @Version: 1.0
 */
public class DemoUdafFunction {
    public static void main(String[] args) {
        SparkSession spark = SparkUtils.getSpark();
        Dataset<Row> dataFromFile = SparkUtils.getDataFromFile("");
        //UserDefinedFunction udf = udaf(new UDAF1<String, String>() {
        //    @Override
        //    public String call(String s) throws Exception {
        //        return s + "ading";
        //    }
        //}, DataTypes.StringType);

        UserDefinedFunction avg = spark.udf().register("agg", udaf(new MyAvg(), Encoders.LONG()));
        dataFromFile.createOrReplaceTempView("user_info");
        spark.sql("select avg(age) from user_info group by nane").show();
        spark.close();
    }

}
