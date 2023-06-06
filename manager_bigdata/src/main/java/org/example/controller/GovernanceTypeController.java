package org.example.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.GovernanceType;
import org.example.service.GovernanceTypeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 治理考评类别权重表(GovernanceType)表控制层
 *
 * @author makejava
 * @since 2023-06-05 16:29:11
 */
@RestController
@RequestMapping("governanceType")
public class GovernanceTypeController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private GovernanceTypeService governanceTypeService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param governanceType 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<GovernanceType> page, GovernanceType governanceType) {
        return success(this.governanceTypeService.page(page, new QueryWrapper<>(governanceType)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.governanceTypeService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param governanceType 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody GovernanceType governanceType) {
        return success(this.governanceTypeService.save(governanceType));
    }

    /**
     * 修改数据
     *
     * @param governanceType 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody GovernanceType governanceType) {
        return success(this.governanceTypeService.updateById(governanceType));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.governanceTypeService.removeByIds(idList));
    }
}

