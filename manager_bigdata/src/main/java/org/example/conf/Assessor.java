package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public abstract class Assessor {

    public GovernanceAssessDetail metricAssess(AssessParam assessParam) {
        //1 从参数中提取默认值
        GovernanceAssessDetail governanceAssessDetail =new GovernanceAssessDetail();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();

        String assessDate = assessParam.getAssessDate();

        governanceAssessDetail.setMetricId(governanceMetric.getId().toString());
        governanceAssessDetail.setMetricName(governanceMetric.getMetricName());
        governanceAssessDetail.setSchemaName(tableMetaInfo.getSchemaName());
        governanceAssessDetail.setTableName(tableMetaInfo.getTableName());
        governanceAssessDetail.setAssessScore(Double.valueOf(10)); //默认给满分
        governanceAssessDetail.setAssessDate(assessDate);
        governanceAssessDetail.setGovernanceType(governanceMetric.getGovernanceType());
        try {
            assess(  governanceAssessDetail,assessParam);  //2  捕获异常写在考评结果中
        }catch (Exception e){   //3  捕获异常写在考评结果中
            governanceAssessDetail.setIsAssessException("1");
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter=new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            governanceAssessDetail.setAssessExceptionMsg(stringWriter.toString().substring(0,2000));
        }
        governanceAssessDetail.setAssessComment(governanceAssessDetail.getAssessComment());
        governanceAssessDetail.setCreateTime(new Date());
        return  governanceAssessDetail;
    }

    // 用于考评器子类继承重写
    protected abstract  void   assess( GovernanceAssessDetail governanceAssessDetail,AssessParam assessParam) throws   Exception;

}
