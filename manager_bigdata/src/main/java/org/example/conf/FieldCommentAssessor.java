package org.example.conf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.entity.AssessParam;
import org.example.entity.GovernanceAssessDetail;
import org.example.entity.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("FIELD_COMMENT")
public class FieldCommentAssessor extends Assessor {
    public  void assess(GovernanceAssessDetail governanceAssessDetail, AssessParam assessParam ) {
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();

        if(tableMetaInfo.getColNameJson()!=null&&tableMetaInfo.getColNameJson().length()>0){

            //获得字段列表
             List<JSONObject> coljsonList= JSON.parseArray(tableMetaInfo.getColNameJson(),JSONObject.class);

            Set<String> colMissCommentSet= new HashSet<>();
            for (JSONObject jsonObject : coljsonList) {
                String comment = jsonObject.getString("comment");
                if(comment==null || comment.length()==0){ //判断哪些缺失
                    colMissCommentSet.add(comment);  //缺失备注的字段
                }
            }
            if(colMissCommentSet.size()>0)   {
                if(colMissCommentSet.size()==coljsonList.size()){ //全部缺失，给0分
                    governanceAssessDetail.setAssessScore(BigDecimal.valueOf(0));
                }else {
                    int score = (coljsonList.size() - colMissCommentSet.size()) * 100 / coljsonList.size();  //根据缺失的占比给分数
                    governanceAssessDetail.setAssessScore(BigDecimal.valueOf(score));
                }
                governanceAssessDetail.setAssessProblem("缺少字段备注:"+JSON.toJSONString(colMissCommentSet));
            }
        }
    }
}
