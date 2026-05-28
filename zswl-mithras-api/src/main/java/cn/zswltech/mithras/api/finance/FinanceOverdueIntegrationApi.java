package cn.zswltech.mithras.api.finance;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.finance.overdue.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 应收逾期集成表
* @author vico
* @date 2025-09-15
*/
@Api(tags = "应收逾期集成表-接口")
public interface FinanceOverdueIntegrationApi {

    @ApiOperation("修改应收逾期集成表")
    @PostMapping("/finance/overdue/integration/modify")
    R<Void> modify(@RequestBody @Valid FinanceOverdueIntegrationModifyREQ req);

    @ApiOperation("应收逾期集成表列表")
    @PostMapping("/finance/overdue/integration/list")
    R<PageR<FinanceOverdueIntegrationListRSP>> list(@RequestBody @Valid FinanceOverdueIntegrationListREQ req);

    @ApiOperation("删除应收逾期集成表")
    @PostMapping("/finance/overdue/integration/remove")
    R<Void> remove(@RequestBody @Valid FinanceOverdueIntegrationRemoveREQ req);

}