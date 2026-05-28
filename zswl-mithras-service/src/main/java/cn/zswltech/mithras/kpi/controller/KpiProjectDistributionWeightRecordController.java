package cn.zswltech.mithras.kpi.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionWeightRecordApi;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordRemoveREQ;
import cn.zswltech.mithras.kpi.service.KpiProjectDistributionWeightRecordService;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeightRecord;

import java.util.List;

/**
* @description 绩效考核-项目分配表-分配比重信息记录表
* @author vico
* @date 2024-09-27
*/
@RestController
public class KpiProjectDistributionWeightRecordController implements KpiProjectDistributionWeightRecordApi {

    @Resource
    private KpiProjectDistributionWeightRecordService kpiProjectDistributionWeightRecordService;

    @Override
    public R<Void> add(KpiProjectDistributionWeightRecordAddREQ req) {
        kpiProjectDistributionWeightRecordService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(KpiProjectDistributionWeightRecordModifyREQ req){
        kpiProjectDistributionWeightRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiProjectDistributionWeightRecordListRSP>> list(KpiProjectDistributionWeightRecordListREQ req){
        Page<KpiProjectDistributionWeightRecord> data = kpiProjectDistributionWeightRecordService.list(req);
        List<KpiProjectDistributionWeightRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), KpiProjectDistributionWeightRecordListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(KpiProjectDistributionWeightRecordRemoveREQ req){
        kpiProjectDistributionWeightRecordService.remove(req);
        return R.ok();
    }

}