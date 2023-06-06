package org.example.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component("LIFECYCLE")
public class LifecycleAssessor extends Assessor {
    @Override
    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        JSONObject paramJson = JSON.parseObject(governanceMetric.getMetricParamsJson());
        Integer lifecycleSuggestDays = paramJson.getInteger("days");

        if(tableMetaInfo.getTableMetaInfoExtra().getLifecycleType()==null||tableMetaInfo.getTableMetaInfoExtra().getLifecycleType().equals("UNSET")){
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
            governanceAssessDetail.setAssessProblem("未设置生命周期类型");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl()+"/"+tableMetaInfo.getId());

        }else  if(tableMetaInfo.getTableMetaInfoExtra().getLifecycleType().equals("NORMAL")){
                if(tableMetaInfo.getPartitionColNameJson()!=null){
                    List<JSONObject> partitionColList = JSON.parseArray(tableMetaInfo.getPartitionColNameJson(), JSONObject.class);
                    if(partitionColList==null||partitionColList.size()==0){
                        governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
                        governanceAssessDetail.setAssessProblem("缺少分区信息");
                    }else if(tableMetaInfo.getTableMetaInfoExtra()==null||tableMetaInfo.getTableMetaInfoExtra().getLifecycleDays()==0L){
                        governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
                        governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl()  +tableMetaInfo.getId());
                        governanceAssessDetail.setAssessProblem("缺少生命周期");
                    } else if(tableMetaInfo.getTableMetaInfoExtra().getLifecycleDays()>lifecycleSuggestDays){

                        long score = lifecycleSuggestDays * 10 / tableMetaInfo.getTableMetaInfoExtra().getLifecycleDays();
                        governanceAssessDetail.setAssessScore(BigDecimal.valueOf(score));
                        governanceAssessDetail.setAssessProblem("生命周期超过建议值");
                        governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl() +tableMetaInfo.getId());
                    }

                }
            }
    }
}
