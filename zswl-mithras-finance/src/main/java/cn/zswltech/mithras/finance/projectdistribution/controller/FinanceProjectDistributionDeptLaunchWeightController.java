package cn.zswltech.mithras.finance.projectdistribution.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financeprojectdistribution.FinanceProjectDistributionDeptLaunchWeightApi;
import cn.zswltech.mithras.dto.financeprojectdistribution.*;
import cn.zswltech.mithras.finance.projectdistribution.mapper.model.FinanceProjectDistributionDeptLaunchWeight;
import cn.zswltech.mithras.finance.projectdistribution.service.FinanceProjectDistributionDeptLaunchWeightApplicationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 财务-部门-项目投放分配比重表
* @author lllin
* @date 2025-12-19
*/
@RestController
public class FinanceProjectDistributionDeptLaunchWeightController implements FinanceProjectDistributionDeptLaunchWeightApi {

    @Resource
    private FinanceProjectDistributionDeptLaunchWeightApplicationService financeProjectDistributionDeptLaunchWeightService;

    @Override
    public R<Void> add(FinanceProjectDistributionDeptLaunchWeightAddREQ req) {
        financeProjectDistributionDeptLaunchWeightService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(FinanceProjectDistributionDeptLaunchWeightModifyREQ req){
        financeProjectDistributionDeptLaunchWeightService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinanceProjectDistributionDeptLaunchWeightListRSP>> list(FinanceProjectDistributionDeptLaunchWeightListREQ req){
        Page<FinanceProjectDistributionDeptLaunchWeight> data = financeProjectDistributionDeptLaunchWeightService.list(req);
        List<FinanceProjectDistributionDeptLaunchWeightListRSP> list = BeanUtil.copyToList(data.getRecords(), FinanceProjectDistributionDeptLaunchWeightListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(FinanceProjectDistributionDeptLaunchWeightRemoveREQ req){
        financeProjectDistributionDeptLaunchWeightService.remove(req);
        return R.ok();
    }

}
