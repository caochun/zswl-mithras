package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2024/6/23
 * @description
 */
@Api(tags = "业务工作台-融资视图")
@RequestMapping(path = "/dashboard/finance")
public interface DashboardFundFinanceApi {
    @ApiOperation("业务工作台-融资视图-分组统计")
    @PostMapping(path = "/statisticsList")
    R<List<DashboardFundFinanceStatisticsRSP>> statisticsList(@RequestBody @Valid DashboardFundFinanceBaseREQ req);

    @ApiOperation("业务工作台-融资视图-还本付息")
    @PostMapping(path = "/repay/list")
    R<Map<String, Object>> listRepay(@RequestBody @Valid DashboardFundFinanceRepayREQ req);

    @ApiOperation("业务工作台-融资视图-融资情况")
    @PostMapping(path = "/loaninfo/list")
    R<Map<String, Object>> listLoanInfo(@RequestBody @Valid DashboardFundFinanceLoanInfoREQ req);

    @ApiOperation("业务工作台-融资视图-授信情况")
    @PostMapping(path = "/creditinfo/list")
    R<Map<String, Object>> listCredit(@RequestBody @Valid DashboardFundFinanceCreditInfoREQ req);

    @Deprecated
    @ApiOperation("业务工作台-融资视图-融资余额")
    @PostMapping(path = "/balance/list")
    R<Map<String, Object>> listBalance(@RequestBody @Valid DashboardFundFinanceBalanceREQ req);

    @ApiOperation("业务工作台-融资视图-资金成本")
    @PostMapping(path = "/funds/list")
    R<Map<String, Object>> costFunds(@RequestBody @Valid DashboardFundFinanceFundsREQ req);
}
