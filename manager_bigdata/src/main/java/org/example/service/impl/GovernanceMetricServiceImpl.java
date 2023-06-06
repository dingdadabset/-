package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.mapper.GovernanceMetricMapper;
import org.example.entity.GovernanceMetric;
import org.example.service.GovernanceMetricService;
import org.springframework.stereotype.Service;

/**
 * 考评指标参数表(GovernanceMetric)表服务实现类
 *
 * @author makejava
 * @since 2023-06-05 16:29:24
 */
@Service("governanceMetricService")
public class GovernanceMetricServiceImpl extends ServiceImpl<GovernanceMetricMapper, GovernanceMetric> implements GovernanceMetricService {

}

