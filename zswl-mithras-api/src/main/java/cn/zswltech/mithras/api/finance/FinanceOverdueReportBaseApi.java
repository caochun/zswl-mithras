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
* @description 逾期报送计划表
* @author vico
* @date 2025-09-15
*/
@Api(tags = "逾期报送计划表-接口")
public interface FinanceOverdueReportBaseApi {

    @ApiOperation("新增逾期报送计划表")
    @PostMapping("/finance/overdue/report/base/add")
    R<Long> add(@RequestBody @Valid FinanceOverdueReportBaseAddREQ req);

    @ApiOperation("逾期报送计划表列表")
    @PostMapping("/finance/overdue/report/base/list")
    R<PageR<FinanceOverdueReportBaseListRSP>> list(@RequestBody @Valid FinanceOverdueReportBaseListREQ req);

    @ApiOperation("关闭逾期报送计划表")
    @PostMapping("/finance/overdue/report/base/close")
    R<Void> close(@RequestBody @Valid FinanceOverdueReportBaseRemoveREQ req);

}