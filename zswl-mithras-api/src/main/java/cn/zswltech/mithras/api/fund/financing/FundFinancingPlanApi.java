package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.plan.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Api(tags = "融资管理-融资方案相关接口")
@RequestMapping(path = "/fund/financing/plan")
public interface FundFinancingPlanApi {
    @ApiOperation("修改融资方案")
    @PostMapping(path = "/modify")
    R<Void> modify(@RequestBody @Valid FundFinancingPlanModifyREQ req);

    @ApiOperation("融资方案详情")
    @PostMapping(path = "/detail")
    R<FundFinancingPlanDetailRSP> detail(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("获取LPR数据")
    @PostMapping(path = "/lpr/get")
    R<FundFinancingChangeLprRSP> getLprData(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("调整LPR")
    @PostMapping(path = "/lpr/change")
    R<Void> changeLpr(@RequestBody @Valid FundFinancingChangeLprREQ req);

    @ApiOperation("计算担保费金额")
    @PostMapping(path = "/guaranteefee/calculate")
    R<Long> calculateGuaranteeAmount(@RequestBody @Valid FundFinancingCalcGuaranteeAmountREQ req);
}
