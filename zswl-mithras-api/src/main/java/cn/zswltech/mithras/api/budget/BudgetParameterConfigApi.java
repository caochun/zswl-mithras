package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListREQ;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListRSP;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-参数设置
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-参数设置-接口")
public interface BudgetParameterConfigApi {

    @ApiOperation("修改预算管理-参数设置")
    @PostMapping("/budget/parameter/config/modify")
    R<Void> modify(@RequestBody @Valid BudgetParameterConfigModifyREQ req);

    @ApiOperation("预算管理-参数设置列表")
    @PostMapping("/budget/parameter/config/list")
    R<List<BudgetParameterConfigListRSP>> list(@RequestBody @Valid BudgetParameterConfigListREQ req);

}