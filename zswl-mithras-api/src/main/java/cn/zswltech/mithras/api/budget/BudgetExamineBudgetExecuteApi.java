package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-预算考核-预算执行情况表
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算考核-预算执行情况表-接口")
public interface BudgetExamineBudgetExecuteApi {

    @ApiOperation("修改预算管理-预算考核-预算执行情况表")
    @PostMapping("/budget/examine/budget/execute/modify")
    R<Void> modify(@RequestBody @Valid List<BudgetExamineBudgetExecuteModifyREQ> req);

    @ApiOperation("预算管理-预算考核-预算执行情况表列表")
    @PostMapping("/budget/examine/budget/execute/list")
    R<List<BudgetExamineBudgetExecuteListRSP>> list(@RequestBody @Valid BudgetExamineBudgetExecuteListREQ req);

}