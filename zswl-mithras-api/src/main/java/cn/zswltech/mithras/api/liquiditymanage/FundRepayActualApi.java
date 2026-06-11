package cn.zswltech.mithras.api.liquiditymanage;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquiditymanage.financingrepay.FinancingRepayPlanModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.financingrepay.FinancingRepayWriteOffModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author zhouning
 * @date 2024/12/24 16:27
 * @className FundTransferApi
 * @description
 */
@Api(tags = "还本付息计划确认Api")
@RequestMapping(path = "/repayActual")
public interface FundRepayActualApi {

    @PostMapping(path = "/plan/modify")
    @ApiOperation(value = "还款计划修改")
    R<Void> modify(@RequestBody @Valid FinancingRepayPlanModifyREQ req);


    @PostMapping(path = "/writeOff/modify")
    @ApiOperation(value = "还款核销修改")
    R<Void> modify(@RequestBody @Valid FinancingRepayWriteOffModifyREQ req);

}
