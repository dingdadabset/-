package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("TEC_OWNER")
public class TecOwnerAssessor extends Assessor {
    @Override
    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        if(tableMetaInfo.getTableMetaInfoExtra().getTecOwnerUserName()!=null&&tableMetaInfo.getTableMetaInfoExtra().getTecOwnerUserName().length()>0){
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(100));
        }else{
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
            governanceAssessDetail.setAssessProblem("缺少技术OWNER");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl() +tableMetaInfo.getId());
        }

    }
}
