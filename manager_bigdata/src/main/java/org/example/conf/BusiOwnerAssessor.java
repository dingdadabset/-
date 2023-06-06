package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

@Component("BUSI_OWNER")
public class BusiOwnerAssessor extends Assessor {
    
    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        if(tableMetaInfo.getTableMetaInfoExtra().getBusiOwnerUserName()==null||tableMetaInfo.getTableMetaInfoExtra().getBusiOwnerUserName().length()==0){
            governanceAssessDetail.setAssessScore(Double.valueOf(0));
            governanceAssessDetail.setAssessProblem("缺少业务OWNER");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl()+tableMetaInfo.getId());
        }

    }
}
