package cn.zswltech.mithras.api.capital;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/19/14:46
 * @description
 */
@Api(tags = "业务流水接口")
@RequestMapping(path = "/business/flow")
public interface BusinessFlowApi {

    @ApiOperation(value = "业务流水-资金端列表")
    @PostMapping(path = "/finance/list")
    R<PageR<BusinessFlowFinanceListRSP>> selectList(@RequestBody @Valid BusinessFlowFinanceListREQ req);

    @ApiOperation("业务流水-资金端列表-导出")
    @PostMapping("/finance/list/export")
    void exportExcel(@RequestBody @Valid BusinessFlowFinanceListREQ req);

    @ApiOperation(value = "业务流水-资金端-核销明细列表")
    @PostMapping(path = "/finance/detail/list")
    R<List<BusinessFlowFinanceDetailListRSP>> detailList(@RequestBody @Valid BusinessFlowFinanceDetailListREQ req);

    @ApiOperation(value = "业务流水-资金端-保存核销明细")
    @PostMapping(path = "/finance/detail/save")
    R<Void> saveDetail(@RequestBody @Valid BusinessFlowFinanceDetailSaveREQ req);

    @ApiOperation(value = "业务流水-资金端-人工核销完毕推送单据")
    @PostMapping(path = "/finance/manual/push")
    R<Void> manualPush(@RequestBody @Valid BusinessFlowFinanceManualPushREQ req);
}
