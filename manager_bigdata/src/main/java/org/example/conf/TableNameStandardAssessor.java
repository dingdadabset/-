package org.example.conf;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("TABLE_NAME_STANDARD")
public class TableNameStandardAssessor extends Assessor {
   

    Pattern odsPattern = Pattern.compile("^ods_.+_(inc|full)$");
    Pattern dwdPattern =Pattern.compile("^dwd_.+_.+_(inc|full)$");
    Pattern dimPattern =Pattern.compile("^dim_.+_(zip|full)$");
    Pattern dwsPattern =Pattern.compile("^dws_.+_.+_.+_(\\d+d|td)$");
    Pattern adsPattern =Pattern.compile("^ads_.+");
    Pattern dmPattern =Pattern.compile("^dm_.+");

    @Override
    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {

        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();

        if(tableMetaInfo.getTableMetaInfoExtra().getDwLevel().equals("OTHER")){
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(5));
            governanceAssessDetail.setAssessProblem("未纳入分层体系");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl() +tableMetaInfo.getId());
            return;
        }
        if(tableMetaInfo.getTableMetaInfoExtra().getDwLevel().equals("UNSET")){
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            governanceAssessDetail.setAssessProblem("未设置分层,无法匹配规范");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl() +tableMetaInfo.getId());
            return;
        }


        Pattern pattern=null ;
          switch (tableMetaInfo.getTableMetaInfoExtra().getDwLevel()){
                    case "ODS" : pattern= odsPattern  ; break;
                    case "DWD" : pattern= dwdPattern  ; break;
                    case "DWS" : pattern= dwsPattern  ; break;
                    case "DIM" : pattern= dimPattern  ; break;
                    case "DM" :  pattern= dmPattern  ; break;
                    case "ADS" : pattern= adsPattern  ; break;
                }

        if( pattern.matcher(tableMetaInfo.getTableName()).matches()) {
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(100));

        } else   {
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(100));
            governanceAssessDetail.setAssessProblem("表名不符合规范");
            governanceAssessDetail.setGovernanceUrl(governanceMetric.getGovernanceUrl()+"/"+tableMetaInfo.getId());
        }

    }

    @Test
    public void testName( ){
        Matcher matcher = dmPattern.matcher("dm_traffic_log_td");
        System.out.println("matcher = " + matcher.matches());

    }
}
