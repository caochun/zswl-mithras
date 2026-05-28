package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanSaveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/23
 * @description
 */
@Api(tags = "融资管理-提前结清方案相关接口")
@RequestMapping(path = "/fund/financing/earlysettle")
public interface FundFinancingEarlySettleApi {
    @ApiOperation("获取提前结清方案")
    @PostMapping(path = "/plan/get")
    R<FundFinancingEarlySettlePlanRSP> getEarlySettlePlan(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("保存提前结清方案")
    @PostMapping(path = "/plan/save")
    R<Void> saveEarlySettlePlan(@RequestBody @Valid FundFinancingEarlySettlePlanSaveREQ req);
}
