package cn.zswltech.mithras.application.orchestration.adapter.report;

import cn.zswltech.mithras.creditreport.mapper.CreditReportMapper;
import cn.zswltech.mithras.api.report.ReportCreditClientPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ReportCreditClientPortAdapter implements ReportCreditClientPort {

    @Resource
    private CreditReportMapper creditReportMapper;

    @Override
    public boolean hasReportClient(Long clientId) {
        return creditReportMapper.countReportClient(clientId) > 0;
    }
}
