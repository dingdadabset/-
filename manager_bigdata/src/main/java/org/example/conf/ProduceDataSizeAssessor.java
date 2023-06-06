package org.example.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

@Component("PRODUCE_DATA_SIZE")
public class ProduceDataSizeAssessor extends Assessor {
    @Override
    protected void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam) throws Exception {

        String assessDate = assessParam.getAssessDate();
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        String tableFsOwner = assessParam.getTableMetaInfo().getTableFsOwner();

        JSONObject paramsJsonObj = JSON.parseObject(metricParamsJson);
        BigDecimal upperLimitPercent = paramsJsonObj.getBigDecimal("upper_limit");
        BigDecimal lowerLimitPercent = paramsJsonObj.getBigDecimal("lower_limit");
        Integer days = paramsJsonObj.getInteger("days");

        String tableFsPath = assessParam.getTableMetaInfo().getTableFsPath();


        if(MetaConst.LIFECYCLE_TYPE_DAY.equals( assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleType())){  //按日分区

            //计算前n天的分区目录名
            String partitionColNameJson = assessParam.getTableMetaInfo().getPartitionColNameJson();
            JSONArray partColArr = JSON.parseArray(partitionColNameJson);
            String partName = partColArr.getJSONObject(0).getString("name");

            Date partitionDate = DateUtils.addDays(DateUtils.parseDate(assessDate, "yyyy-MM-dd"),  -1);
            String partitionDt = DateFormatUtils.format(partitionDate, "yyyy-MM-dd");

            String curPartDirName=tableFsPath+"/"+partName+"="+partitionDt;
            //当天日期分区的大小
            Long curDataSize = getDataSizeByPath(curPartDirName, tableFsOwner);
            //governanceAssessDetail.addLog("(当前分区:"+curPartDirName +",数据量:"+curDataSize );
            //n天前每日分区
            int partCountWithData=0;
            Long partTotalDateSize=0L;


            //求前n天的总大小
            for (int i = 1; i <= days ; i++) {
                Date date = DateUtils.addDays(partitionDate, 0 - i );
                String partDt = DateFormatUtils.format(date, "yyyy-MM-dd");
                String partDirName=tableFsPath+"/"+partName+"="+partDt;
                Long partDataSize = getDataSizeByPath(partDirName, tableFsOwner);
                if(partDataSize>0){
                    partCountWithData++;
                    partTotalDateSize+=partDataSize;
                    //governanceAssessDetail.addLog("(分区:"+partDirName +",数据量:"+partDataSize );
                }

            }
            //governanceAssessDetail.addLog("( 总数据量:"+partTotalDateSize+")" );

            //进行比较
            if(partCountWithData>0){
                Long avgPartDataSize=partTotalDateSize/partCountWithData;
                if(curDataSize > avgPartDataSize *  ( upperLimitPercent.longValue()+100L)  /100L ){
                    governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                    governanceAssessDetail.setAssessProblem("产出 高于现有"+partCountWithData+"日平均数据量的"+upperLimitPercent.longValue()+"%");
                }else if(curDataSize < avgPartDataSize *  lowerLimitPercent.longValue()   /100L){
                    governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                    governanceAssessDetail.setAssessProblem("产出 低于现有"+partCountWithData+"日平均数据量的"+lowerLimitPercent.longValue()+"%");
                }
            }

        }

    }

    //创建文件系统客户端，并调用递归方法获得文件大小
    public  Long  getDataSizeByPath(String path,String fsUser) throws Exception{
        //初始化文件服务器客户端
        FileSystem fs = FileSystem.get(new URI(path), new Configuration(), fsUser);
        Long dataSize=0L;
        if(fs.exists(new Path(path))){
            FileStatus[] fileStatuses = fs.listStatus(new Path(path));
            dataSize = getFileDataSize(fileStatuses, 0L, fs);
        }

        return dataSize;

    }

    //通过递归获得目录及子目录中文件大小
    public  Long  getFileDataSize (FileStatus[] curPathFileStatuses, Long curDateSize, FileSystem fs) throws Exception{
        for (FileStatus curPathFileStatus : curPathFileStatuses) {
            if(curPathFileStatus.isDirectory()){
                FileStatus[] fileStatuses = fs.listStatus(curPathFileStatus.getPath());
                curDateSize = getFileDataSize(fileStatuses, curDateSize, fs);
            }else {
                curDateSize+=curPathFileStatus.getLen();
            }
        }
        return curDateSize;
    }


    @Test
    public void test() throws URISyntaxException, IOException, InterruptedException {
        String path="hdfs://hadoop102:8020/warehouse/gmall/ads/ads_coupon_stats";
        FileSystem fs = FileSystem.get(new URI( path ), new Configuration(), "atguigu");
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        System.out.println(fileStatuses);

    }

}
