package cn.zswltech.mithras.api.kpi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 绩效考核-项目利润明细-记录表
* @author vico
* @date 2024-09-25
*/
@Api(tags = "绩效考核-项目利润明细-记录表-接口")
//预留，后续需求使用
public interface KpiFinanceProjectProfitRecordApi {

    @ApiOperation("修改绩效考核-项目利润明细-记录表")
    @PostMapping("/kpi/finance/project/profit/record/modify")
    R<Void> modify(@RequestBody @Valid KpiFinanceProjectProfitRecordModifyREQ req);

    @ApiOperation("绩效考核-项目利润明细-记录表列表")
    @PostMapping("/kpi/finance/project/profit/record/list")
    R<PageR<KpiFinanceProjectProfitRecordListRSP>> list(@RequestBody @Valid KpiFinanceProjectProfitRecordListREQ req);

    @ApiOperation("删除绩效考核-项目利润明细-记录表")
    @PostMapping("/kpi/finance/project/profit/record/remove")
    R<Void> remove(@RequestBody @Valid KpiFinanceProjectProfitRecordRemoveREQ req);

}