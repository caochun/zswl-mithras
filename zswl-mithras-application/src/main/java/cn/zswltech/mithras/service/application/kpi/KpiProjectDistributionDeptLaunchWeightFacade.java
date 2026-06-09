package cn.zswltech.mithras.service.application.kpi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionDeptLaunchWeightApplicationService;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightRemoveREQ;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionDeptLaunchWeightService;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptLaunchWeight;

import java.util.List;

/**
* @description 绩效考核-部门-项目投放分配比重表
* @author hspcadmin
* @date 2025-09-29
*/
@Service
public class KpiProjectDistributionDeptLaunchWeightFacade implements KpiProjectDistributionDeptLaunchWeightApplicationService {

    @Resource
    private KpiProjectDistributionDeptLaunchWeightService kpiProjectDistributionDeptLaunchWeightService;

    @Override
    public R<Void> add(KpiProjectDistributionDeptLaunchWeightAddREQ req) {
        kpiProjectDistributionDeptLaunchWeightService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(KpiProjectDistributionDeptLaunchWeightModifyREQ req){
        kpiProjectDistributionDeptLaunchWeightService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiProjectDistributionDeptLaunchWeightListRSP>> list(KpiProjectDistributionDeptLaunchWeightListREQ req){
        Page<KpiProjectDistributionDeptLaunchWeight> data = kpiProjectDistributionDeptLaunchWeightService.list(req);
        List<KpiProjectDistributionDeptLaunchWeightListRSP> list = BeanUtil.copyToList(data.getRecords(), KpiProjectDistributionDeptLaunchWeightListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(KpiProjectDistributionDeptLaunchWeightRemoveREQ req){
        kpiProjectDistributionDeptLaunchWeightService.remove(req);
        return R.ok();
    }

}