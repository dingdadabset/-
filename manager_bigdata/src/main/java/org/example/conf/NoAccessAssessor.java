package org.example.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateUtils;
import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Component("NO_ACCESS")
public class NoAccessAssessor extends Assessor {


    @Override
    protected void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam) throws Exception {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();

        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject metricParamsJsonObj = JSON.parseObject(metricParamsJson);
        Integer days = metricParamsJsonObj.getInteger("days");

        Date assessDate = DateUtils.parseDate(assessParam.getAssessDate(), "yyyy-MM-dd");

        Calendar assessDateC = Calendar.getInstance();
        assessDateC.setTime(assessDate);
        int assessDateInt = assessDateC.get(Calendar.DATE);


        Calendar lastAccessDateC = Calendar.getInstance();
        lastAccessDateC.setTime(tableMetaInfo.getTableLastAccessTime());
        int lastAccessDateInt = assessDateC.get(Calendar.DATE);


        //如果差值天数大于 阈值天数
        if (lastAccessDateInt - assessDateInt > days) {
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            governanceAssessDetail.setAssessProblem("长期没有被读取");

        }

    }
}
