package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("TABLE_COMMENT")
public class TableCommentAssessor extends Assessor {


    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        if(tableMetaInfo.getTableComment()==null||tableMetaInfo.getTableComment().length()==0){

            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
            governanceAssessDetail.setAssessProblem("缺少表备注");
        }
    }
}
