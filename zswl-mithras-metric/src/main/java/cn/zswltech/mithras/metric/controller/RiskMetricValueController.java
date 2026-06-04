package cn.zswltech.mithras.metric.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.metric.RiskMetricValueApi;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueListReq;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueListRsp;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueModifyReq;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueReportReq;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricValue;
import cn.zswltech.mithras.metric.service.RiskMetricValueService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static cn.hutool.core.bean.BeanUtil.copyToList;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author yibin
 */
@RestController
public class RiskMetricValueController implements RiskMetricValueApi {
    @Resource
    private RiskMetricValueService metricValueService;

    @Override
    public R<Void> modifyValue(List<RiskMetricValueModifyReq> req) {
        metricValueService.modify(req);
        return R.ok();
    }


    @Override
    public R<RiskMetricValueListRsp> list(RiskMetricValueListReq req) {
        Page<RiskMetricValue> data = metricValueService.list(req);
        List<RiskMetricValue> records = data.getRecords();
        List<RiskMetricValueListRsp.RiskMetricValueListSingle> list = copyToList(records, RiskMetricValueListRsp.RiskMetricValueListSingle.class);
        RiskMetricValueListRsp rsp = new RiskMetricValueListRsp();
        PageR<RiskMetricValueListRsp.RiskMetricValueListSingle> dataList = PageR.of(
                list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize());
        rsp.setDataList(dataList);
        rsp.setLastReportTime(metricValueService.lastReportTime());
        return R.ok(rsp);
    }

    @Override
    public R<Void> report(RiskMetricValueReportReq req) {
        LocalDate date = req.getDataTime().plusMonths(1);
        LocalDate now = LocalDate.now();
        if (date.getYear() != now.getYear() || date.getMonthValue() != now.getMonthValue()) {
            err("只能报送上个月的指标");
        }
        req.setDataTime(req.getDataTime().with(TemporalAdjusters.lastDayOfMonth()));
        if (!metricValueService.report(req.getDataTime())) {
            err("报送失败");
        }
        return R.ok();
    }

    @Override
    public R<Void> calc(RiskMetricValueReportReq req) {
        LocalDate date = req.getDataTime().plusMonths(1);
        LocalDate now = LocalDate.now();
        if (date.getYear() != now.getYear() || date.getMonthValue() != now.getMonthValue()) {
            err("只能计算上个月的指标");
        }
        req.setDataTime(req.getDataTime().with(TemporalAdjusters.lastDayOfMonth()));
        metricValueService.calc(req.getDataTime());
        return R.ok();
    }
}
