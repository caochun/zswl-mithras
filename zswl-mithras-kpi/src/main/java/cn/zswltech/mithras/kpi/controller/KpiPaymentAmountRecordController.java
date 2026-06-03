package cn.zswltech.mithras.kpi.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.kpi.KpiPaymentAmountRecordApi;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordRemoveREQ;
import cn.zswltech.mithras.kpi.service.KpiPaymentAmountRecordService;
import cn.zswltech.mithras.kpi.mapper.model.KpiPaymentAmountRecord;

import java.util.List;

/**
* @description 绩效考核-投放信息记录表
* @author vico
* @date 2024-09-27
*/
@RestController
public class KpiPaymentAmountRecordController implements KpiPaymentAmountRecordApi {

    @Resource
    private KpiPaymentAmountRecordService kpiPaymentAmountRecordService;

    @Override
    public R<Void> add(KpiPaymentAmountRecordAddREQ req) {
        kpiPaymentAmountRecordService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(KpiPaymentAmountRecordModifyREQ req){
        kpiPaymentAmountRecordService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<KpiPaymentAmountRecordListRSP>> list(KpiPaymentAmountRecordListREQ req){
        Page<KpiPaymentAmountRecord> data = kpiPaymentAmountRecordService.list(req);
        List<KpiPaymentAmountRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), KpiPaymentAmountRecordListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(KpiPaymentAmountRecordRemoveREQ req){
        kpiPaymentAmountRecordService.remove(req);
        return R.ok();
    }

}