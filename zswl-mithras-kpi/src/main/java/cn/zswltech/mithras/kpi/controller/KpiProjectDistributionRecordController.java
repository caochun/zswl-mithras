package cn.zswltech.mithras.kpi.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionRecordApi;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordRemoveREQ;
import cn.zswltech.mithras.kpi.service.KpiProjectDistributionRecordService;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionRecord;

import java.util.List;

/**
* @description 绩效考核-项目分配记录表
* @author vico
* @date 2024-09-27
*/
@RestController
public class KpiProjectDistributionRecordController implements KpiProjectDistributionRecordApi {

    @Resource
    private KpiProjectDistributionRecordService kpiProjectDistributionRecordService;

    @Override
    public R<Void> add(KpiProjectDistributionRecordAddREQ req) {
        kpiProjectDistributionRecordService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(KpiProjectDistributionRecordModifyREQ req){
        kpiProjectDistributionRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiProjectDistributionRecordListRSP>> list(KpiProjectDistributionRecordListREQ req){
        Page<KpiProjectDistributionRecord> data = kpiProjectDistributionRecordService.list(req);
        List<KpiProjectDistributionRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), KpiProjectDistributionRecordListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(KpiProjectDistributionRecordRemoveREQ req){
        kpiProjectDistributionRecordService.remove(req);
        return R.ok();
    }

}