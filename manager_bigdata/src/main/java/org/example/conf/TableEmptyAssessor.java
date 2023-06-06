package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("TABLE_EMPTY")
public class TableEmptyAssessor extends Assessor {


    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        if(tableMetaInfo.getTableSize()==0L){
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
            governanceAssessDetail.setAssessProblem("数据为空");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl()+"/"+tableMetaInfo.getId());
        }
    }
}
