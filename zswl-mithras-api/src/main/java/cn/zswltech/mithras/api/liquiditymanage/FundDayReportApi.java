package cn.zswltech.mithras.api.liquiditymanage;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/13 16:27
 * @className FundDayReportApi
 * @description
 */
@Api(tags = "资金日报Api")
@RequestMapping(path = "/fundDayReport")
public interface FundDayReportApi {

    @PostMapping(path = "/indicator/list")
    @ApiOperation(value = "日结指标")
    R<DayReportIndicatorListRSP> dayReportIndicatorList(@RequestBody @Valid DayReportIndicatorListREQ req);

    @PostMapping(path = "/account/balance")
    @ApiOperation(value = "账户余额")
    R<AccountBalanceListRSP> accountBalanceList(@RequestBody @Valid AccountBalanceListREQ req);

    @PostMapping(path = "/rent/income")
    @ApiOperation(value = "租金流入")
    R<List<RentIncomeListRSP>> rentIncomeList(@RequestBody @Valid RentIncomeListREQ req);

    @PostMapping(path = "/repay/principalInterest")
    @ApiOperation(value = "还本付息")
    R<PageR<RepayPrincipalInterestListRSP>> repayPrincipalInterestList(@RequestBody @Valid RepayPrincipalInterestListREQ req);
}
