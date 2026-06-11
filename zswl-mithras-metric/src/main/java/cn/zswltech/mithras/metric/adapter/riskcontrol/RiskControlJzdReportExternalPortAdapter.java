package cn.zswltech.mithras.metric.adapter.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.emit.model.req.concentration.ConcentrationAddReqBody;
import cn.zswltech.mithras.riskcontrol.report.jzd.RiskControlJzdReportExternalPort;
import cn.zswltech.mithras.riskcontrol.report.jzd.RiskControlJzdReportSubmitItem;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RiskControlJzdReportExternalPortAdapter implements RiskControlJzdReportExternalPort {

    @Resource
    private MetricEmitter metricEmitter;

    @Override
    public String submitConcentration(String timePoint, List<RiskControlJzdReportSubmitItem> items) {
        ConcentrationAddReqBody body = new ConcentrationAddReqBody();
        body.setTimePoint(timePoint);
        body.setUploadData(BeanUtil.copyToList(items, ConcentrationAddReqBody.UploadCustomData.class));
        return metricEmitter.emitConcentration(body);
    }
}
