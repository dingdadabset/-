package org.example.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.AssessParam;
import org.example.conf.Assessor;
import org.example.entity.GovernanceMetric;
import org.example.entity.TableMetaInfo;
import org.example.mapper.GovernanceAssessDetailMapper;
import org.example.entity.GovernanceAssessDetail;
import org.example.service.GovernanceAssessDetailService;
import org.example.service.GovernanceMetricService;
import org.example.service.TableMetaInfoService;
import org.example.utils.SpringBeanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 治理考评结果明细(GovernanceAssessDetail)表服务实现类
 *
 * @author makejava
 * @since 2023-06-05 16:29:35
 */
@Service("governanceAssessDetailService")
@DS("dga")
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {
    @Resource
    TableMetaInfoService tableMetaInfoService;
    @Resource
    private GovernanceMetricService governanceAssessDetailService;
    @Autowired
    TDsTaskDefinitionService tDsTaskDefinitionService;


    // 考评每张表 每个指标项 并把结果写入检查结果表
    public void assessAllTableMetrics(String assessDate) {
        List<GovernanceMetric> governanceMetricList = governanceAssessDetailService.list(new QueryWrapper<GovernanceMetric>().ne("is_disabled", "1"));
        //1 获得所有待考评表的列表 保存为map
        List<TableMetaInfo> tableMetaInfoList = tableMetaInfoService.getTableMetaInfoList();
        ArrayList<GovernanceAssessDetail> governanceAssessDetails = new ArrayList<>();
        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
            for (GovernanceMetric governanceMetric : governanceMetricList) {
                //从容器中获得对应名称的组件
                Assessor assessor = SpringBeanProvider.getBean(governanceMetric.getMetricCode(), Assessor.class);  //根据名称获得spring容器中的组件

                //构造考评需要的参数
                AssessParam assessParam = new AssessParam();
                assessParam.setAssessDate(assessDate);
                assessParam.setTableMetaInfo(tableMetaInfo);
                assessParam.setGovernanceMetric(governanceMetric);

                assessParam.setTableMetaInfo(tableMetaInfo);
                //TDsTaskDefinition tDsTaskDefinition = taskDefinitionMap.get(tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName());
                //assessParam.setTDsTaskDefinition(tDsTaskDefinition);

                //进行指标考评
                GovernanceAssessDetail governanceAssessDetail = assessor.metricAssess(assessParam);
                governanceAssessDetails.add(governanceAssessDetail);
            }

        }
        saveBatch(governanceAssessDetails);
    }
}

