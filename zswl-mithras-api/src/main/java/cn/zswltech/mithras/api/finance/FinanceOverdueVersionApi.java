package cn.zswltech.mithras.api.finance;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationPushRSP;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueVersionSubmitREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 应收逾期结算表
* @author vico
* @date 2025-09-15
*/
@Api(tags = "应收逾期版本表-接口")
public interface FinanceOverdueVersionApi {

    @ApiOperation("提交应收逾期集成单")
    @PostMapping("/finance/overdue/submit")
    R<Void> submit(@RequestBody @Valid FinanceOverdueVersionSubmitREQ req);

    @ApiOperation("推送应收逾期集成表")
    @PostMapping("/finance/overdue/integration/push")
    R<FinanceOverdueIntegrationPushRSP> push(@RequestBody @Valid FinanceOverdueVersionSubmitREQ req);

}