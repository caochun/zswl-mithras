package cn.zswltech.mithras.metric.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.metric.RiskMetricTimedApi;
import cn.zswltech.mithras.dto.metric.timed.RiskMetricTimedListReq;
import cn.zswltech.mithras.dto.metric.timed.RiskMetricTimedListRsp;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @author yibin
 */
@RestController
public class RiskMetricTimedController implements RiskMetricTimedApi {
    @Resource
    private RiskMetricTimedService metricTimedService;

    @Override
    public R<PageR<RiskMetricTimedListRsp>> list(RiskMetricTimedListReq req) {
        if (req.getDataTime() != null) {
            req.setDataTime(req.getDataTime().with(TemporalAdjusters.lastDayOfMonth()));
        }
        Page<RiskMetricTimed> data = metricTimedService.list(req);
        List<RiskMetricTimed> records = data.getRecords();
        List<RiskMetricTimedListRsp> list = BeanUtil.copyToList(records, RiskMetricTimedListRsp.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }
}
