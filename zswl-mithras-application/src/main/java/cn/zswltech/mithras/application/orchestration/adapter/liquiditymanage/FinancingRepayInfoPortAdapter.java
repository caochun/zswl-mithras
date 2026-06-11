package cn.zswltech.mithras.application.orchestration.adapter.liquiditymanage;

import cn.zswltech.mithras.liquidity.service.FinancingRepayInfoPort;
import cn.zswltech.mithras.application.orchestration.job.FinancingRepayInfoJob;
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
