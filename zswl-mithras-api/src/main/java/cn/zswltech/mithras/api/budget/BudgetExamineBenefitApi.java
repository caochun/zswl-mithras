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
* @description 预算管理-预算考核-效益考核表
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算考核-效益考核表-接口")
public interface BudgetExamineBenefitApi {

    @ApiOperation("修改预算管理-预算考核-效益考核表")
    @PostMapping("/budget/examine/benefit/modify")
    R<Void> modify(@RequestBody @Valid List<BudgetExamineBenefitModifyREQ> req);

    @ApiOperation("预算管理-预算考核-效益考核表列表")
    @PostMapping("/budget/examine/benefit/list")
    R<List<BudgetExamineBenefitListRSP>> list(@RequestBody @Valid BudgetExamineBenefitListREQ req);

}