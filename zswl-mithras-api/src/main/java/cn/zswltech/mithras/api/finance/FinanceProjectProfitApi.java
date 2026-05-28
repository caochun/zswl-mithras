package cn.zswltech.mithras.api.finance;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.finance.*;
import cn.zswltech.mithras.dto.monthly.MonthlySubmitREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Api(tags = "财务管理-项目利润")
@RequestMapping(path = "/finance/projectprofit")
public interface FinanceProjectProfitApi {
    @ApiOperation("财务管理-项目利润-分页列表")
    @PostMapping(path = "/pagelist")
    R<PageR<FinanceProjectProfitRSP>> pageList(@RequestBody @Valid PageReq req);

    @ApiOperation("确认项目利润")
    @PostMapping(path = "/confirm")
    R<Void> confirm(@RequestBody @Valid FinanceProfitConfirmREQ req);

    @ApiOperation("财务管理-项目利润-详情-分页列表")
    @PostMapping(path = "/detail/pagelist")
    R<PageR<FinanceProjectProfitDetailRSP>> detailPageList(@RequestBody @Valid FinanceProjectProfitDetailREQ req);

    @ApiOperation("利润测算")
    @PostMapping("/profit/calculation")
    R<Void> profitCalculation(@RequestBody @Valid FinanceProjectCalculationREQ req);
}
