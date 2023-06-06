package org.example.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("TABLE_SIMILAR")
public class TableSimilarAssessor extends Assessor {

    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo curTableMetaInfo = assessParam.getTableMetaInfo();
        GovernanceMetric governanceMetric = assessParam.getGovernanceMetric();
        Map<String, TableMetaInfo> tableMetaInfoMap = assessParam.getTableMetaInfoMap();

        JSONObject paramJson = JSON.parseObject(governanceMetric.getMetricParamsJson());
        Integer percent = paramJson.getInteger("percent");
        Set<String>  similarTableSet=new HashSet<>();
        for (TableMetaInfo targetTableMetaInfo : tableMetaInfoMap.values()) {
            //判断同层不同表
            if(curTableMetaInfo.getTableName()!=targetTableMetaInfo.getTableName()
                    &&curTableMetaInfo.getTableMetaInfoExtra().getDwLevel().equals(targetTableMetaInfo.getTableMetaInfoExtra().getDwLevel())){


                List<JSONObject> colNameList = JSON.parseArray(curTableMetaInfo.getColNameJson(), JSONObject.class);

                List<JSONObject> targetColNameList = JSON.parseArray(targetTableMetaInfo.getColNameJson(), JSONObject.class);
                Integer sameFieldCount=0;
                int sameFieldLimit = colNameList.size() * percent / 100;

                //通过两层循环比较字段的相似的个数
                for (JSONObject colJsonObj : colNameList) {
                    String curColName = colJsonObj.getString("name");
                    for (JSONObject targetColjsonObj : targetColNameList) {
                        String targetColName = targetColjsonObj.getString("name");
                        if(curColName.equals(targetColName)){
                            sameFieldCount++;
                            break;
                        }
                    }
                    //重复字段达到阈值，则认为是重复表
                    if(sameFieldCount==sameFieldLimit) {
                        similarTableSet.add(targetTableMetaInfo.getTableName());
                        break;
                    }
                }
            }
        }
        if(similarTableSet.size()>0){
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
            governanceAssessDetail.setAssessProblem("存在相似表:"+JSON.toJSONString(similarTableSet));
        }
    }

}
