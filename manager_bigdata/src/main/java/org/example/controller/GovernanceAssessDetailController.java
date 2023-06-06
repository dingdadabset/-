package org.example.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.GovernanceAssessDetail;
import org.example.service.GovernanceAssessDetailService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 治理考评结果明细(GovernanceAssessDetail)表控制层
 *
 * @author makejava
 * @since 2023-06-05 16:29:35
 */
@RestController
@RequestMapping("governanceAssessDetail")
public class GovernanceAssessDetailController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private GovernanceAssessDetailService governanceAssessDetailService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param governanceAssessDetail 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<GovernanceAssessDetail> page, GovernanceAssessDetail governanceAssessDetail) {
        return success(this.governanceAssessDetailService.page(page, new QueryWrapper<>(governanceAssessDetail)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.governanceAssessDetailService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param governanceAssessDetail 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody GovernanceAssessDetail governanceAssessDetail) {
        return success(this.governanceAssessDetailService.save(governanceAssessDetail));
    }

    /**
     * 修改数据
     *
     * @param governanceAssessDetail 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody GovernanceAssessDetail governanceAssessDetail) {
        return success(this.governanceAssessDetailService.updateById(governanceAssessDetail));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.governanceAssessDetailService.removeByIds(idList));
    }
}

