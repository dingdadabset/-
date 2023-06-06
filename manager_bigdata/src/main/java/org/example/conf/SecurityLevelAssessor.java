package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("SECURITY_LEVEL")
public class SecurityLevelAssessor  extends Assessor {


    @Override
    protected void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam) throws Exception {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        if (tableMetaInfo.getTableMetaInfoExtra().getSecurityLevel() == null || tableMetaInfo.getTableMetaInfoExtra().getSecurityLevel().equals("UNSET")) {
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
            governanceAssessDetail.setAssessProblem("没有设置安全级别");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl()  + tableMetaInfo.getId());
        }
    }
}
