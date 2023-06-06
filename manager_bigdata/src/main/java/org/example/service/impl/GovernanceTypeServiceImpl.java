package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.mapper.GovernanceTypeMapper;
import org.example.entity.GovernanceType;
import org.example.service.GovernanceTypeService;
import org.springframework.stereotype.Service;

/**
 * 治理考评类别权重表(GovernanceType)表服务实现类
 *
 * @author makejava
 * @since 2023-06-05 16:29:11
 */
@Service("governanceTypeService")
public class GovernanceTypeServiceImpl extends ServiceImpl<GovernanceTypeMapper, GovernanceType> implements GovernanceTypeService {

}

