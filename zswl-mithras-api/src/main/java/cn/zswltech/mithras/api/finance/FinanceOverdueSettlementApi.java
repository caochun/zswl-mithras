package cn.zswltech.mithras.api.finance;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.finance.overdue.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 应收逾期结算表
* @author vico
* @date 2025-09-15
*/
@Api(tags = "应收逾期结算表-接口")
public interface FinanceOverdueSettlementApi {

    @ApiOperation("新增应收逾期结算表")
    @PostMapping("/finance/overdue/settlement/add")
    R<Void> add(@RequestBody @Valid FinanceOverdueSettlementAddREQ req);

    @ApiOperation("修改应收逾期结算表")
    @PostMapping("/finance/overdue/settlement/modify")
    R<Void> modify(@RequestBody @Valid FinanceOverdueSettlementModifyREQ req);

    @ApiOperation("应收逾期结算表列表")
    @PostMapping("/finance/overdue/settlement/list")
    R<PageR<FinanceOverdueSettlementListRSP>> list(@RequestBody @Valid FinanceOverdueSettlementListREQ req);

    @ApiOperation("删除应收逾期结算表")
    @PostMapping("/finance/overdue/settlement/remove")
    R<Void> remove(@RequestBody @Valid FinanceOverdueSettlementRemoveREQ req);

    @ApiOperation("逾期查询客户下合同信息")
    @PostMapping("/finance/overdue/settlement/contract/relation")
    R<List<FinanceOverdueSettlementContractRelationRsp>> contractRelation(@RequestBody @Valid FinanceOverdueSettlementContractRelationREQ req);


}