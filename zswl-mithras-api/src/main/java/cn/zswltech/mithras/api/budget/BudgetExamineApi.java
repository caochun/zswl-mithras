package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 预算管理-预算考核
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算考核-接口")
public interface BudgetExamineApi {

    @ApiOperation("新增预算管理-预算考核")
    @PostMapping("/budget/examine/add")
    R<Void> add(@RequestBody @Valid BudgetExamineAddREQ req);

    @ApiOperation("预算管理-预算考核列表")
    @PostMapping("/budget/examine/list")
    R<PageR<BudgetExamineListRSP>> list(@RequestBody @Valid BudgetExamineListREQ req);

    @ApiOperation("删除预算管理-预算考核")
    @PostMapping("/budget/examine/remove")
    R<Void> remove(@RequestBody @Valid BudgetExamineRemoveREQ req);

    @ApiOperation("提交预算管理-预算考核")
    @PostMapping("/budget/examine/submit")
    R<Void> submit(@RequestBody @Valid IdREQ req);

    @ApiOperation("预算管理-预算考核-预算执行情况表列表")
    @PostMapping("/budget/examine/detail")
    R<BudgetExamineListRSP> detail(@RequestBody @Valid IdREQ req);


}