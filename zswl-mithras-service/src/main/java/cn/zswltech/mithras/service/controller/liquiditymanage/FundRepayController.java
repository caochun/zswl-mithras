package cn.zswltech.mithras.service.controller.liquiditymanage;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundRepayActualApi;
import cn.zswltech.mithras.dto.liquiditymanage.financingRepay.FinancingRepayPlanModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.financingRepay.FinancingRepayWriteOffModifyREQ;
import cn.zswltech.mithras.service.service.liquiditymanage.FundRepayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @date 2024/12/17 18:56
 * @description
 */
@Slf4j
@RestController
public class FundRepayController implements FundRepayActualApi {

    @Resource
    private FundRepayService fundRepayService;


    @Override
    public R<Void> modify(FinancingRepayPlanModifyREQ req) {
        fundRepayService.planModify(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(FinancingRepayWriteOffModifyREQ req) {
        fundRepayService.writeOffModify(req);
        return R.ok();
    }
}
