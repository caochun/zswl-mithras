package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Api(tags = "融资管理相关接口")
@RequestMapping(path = "/fund/financing")
public interface FundFinancingApi {
    @ApiOperation("融资列表")
    @PostMapping(path = "/pagelist")
    R<FundFinancingListRSP> pageList(@RequestBody @Valid FundFinancingListREQ req);

    @ApiOperation("创建融资")
    @PostMapping(path = "/create")
    R<Long> create(@RequestBody @Valid FundFinancingCreateREQ req);

    @ApiOperation("作废融资")
    @PostMapping(path = "/close")
    R<Void> close(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("贷后变更预检查")
    @PostMapping(path = "/change/precheck")
    R<FundFinancingChangePreCheckRSP> changePreCheck(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("删除融资")
    @PostMapping(path = "/delete")
    R<Void> delete(@RequestBody @Valid SingleFinancingIdREQ req);
}
