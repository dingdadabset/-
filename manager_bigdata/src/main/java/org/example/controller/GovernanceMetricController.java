package org.example.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.GovernanceMetric;
import org.example.service.GovernanceMetricService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 考评指标参数表(GovernanceMetric)表控制层
 *
 * @author makejava
 * @since 2023-06-05 16:29:24
 */
@RestController
@RequestMapping("governanceMetric")
public class GovernanceMetricController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private GovernanceMetricService governanceMetricService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param governanceMetric 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<GovernanceMetric> page, GovernanceMetric governanceMetric) {
        return success(this.governanceMetricService.page(page, new QueryWrapper<>(governanceMetric)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.governanceMetricService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param governanceMetric 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody GovernanceMetric governanceMetric) {
        return success(this.governanceMetricService.save(governanceMetric));
    }

    /**
     * 修改数据
     *
     * @param governanceMetric 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody GovernanceMetric governanceMetric) {
        return success(this.governanceMetricService.updateById(governanceMetric));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.governanceMetricService.removeByIds(idList));
    }
}

