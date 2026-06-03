package cn.zswltech.mithras.kpi.controller;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiFinanceProjectProfitRecordApi;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordRemoveREQ;
import cn.zswltech.mithras.kpi.mapper.model.KpiFinanceProjectProfitRecord;
import cn.zswltech.mithras.kpi.service.KpiFinanceProjectProfitRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 绩效考核-项目利润明细-记录表
* @author vico
* @date 2024-09-25
*/
@RestController
public class KpiFinanceProjectProfitRecordController implements KpiFinanceProjectProfitRecordApi {

    @Resource
    private KpiFinanceProjectProfitRecordService kpiFinanceProjectProfitRecordService;

    @Override
    public R<Void> modify(KpiFinanceProjectProfitRecordModifyREQ req){
        kpiFinanceProjectProfitRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiFinanceProjectProfitRecordListRSP>> list(KpiFinanceProjectProfitRecordListREQ req){
        Page<KpiFinanceProjectProfitRecord> data = kpiFinanceProjectProfitRecordService.list(req);
        List<KpiFinanceProjectProfitRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), KpiFinanceProjectProfitRecordListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(KpiFinanceProjectProfitRecordRemoveREQ req){
        kpiFinanceProjectProfitRecordService.remove(req);
        return R.ok();
    }

}