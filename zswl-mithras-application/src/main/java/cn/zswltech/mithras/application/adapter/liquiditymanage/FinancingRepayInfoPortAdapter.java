package cn.zswltech.mithras.application.adapter.liquiditymanage;

import cn.zswltech.mithras.liquiditymanage.service.FinancingRepayInfoPort;
import cn.zswltech.mithras.service.job.FinancingRepayInfoJob;
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
