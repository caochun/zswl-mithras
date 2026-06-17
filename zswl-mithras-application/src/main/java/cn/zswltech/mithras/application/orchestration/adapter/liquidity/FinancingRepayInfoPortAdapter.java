package cn.zswltech.mithras.application.orchestration.adapter.liquidity;

import cn.zswltech.mithras.liquidity.application.port.FinancingRepayInfoPort;
import cn.zswltech.mithras.application.orchestration.job.fund.FinancingRepayInfoJob;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FinancingRepayInfoPortAdapter implements FinancingRepayInfoPort {

    @Resource
    private FinancingRepayInfoJob financingRepayInfoJob;

    @Override
    public void financingRepayInfoInAdvance() {
        financingRepayInfoJob.financingRepayInfoInAdvance();
    }
}
