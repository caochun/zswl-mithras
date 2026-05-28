package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.riskcontrol.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@Api(tags = "客户集中度-接口")
public interface RiskControlConcentrationClientApi {

    @ApiOperation("客户集中度列表")
    @PostMapping("/risk/control/concentration/list/client")
    R<PageR<RiskControlConcentrationClientListRSP>> listClient(@RequestBody @Valid RiskControlConcentrationClientListREQ req);

    @ApiOperation("集团集中度列表")
    @PostMapping("/risk/control/concentration/list/group")
    R<PageR<RiskControlConcentrationGroupListRSP>> listGroup(@RequestBody @Valid RiskControlConcentrationGroupListREQ req);

    @ApiOperation("关联方集中度列表")
    @PostMapping("/risk/control/concentration/list/relate")
    R<PageR<RiskControlConcentrationClientListRSP>> listRelate(@RequestBody @Valid RiskControlConcentrationRelateListREQ req);

    @ApiOperation("全部关联方集中度")
    @PostMapping("/risk/control/concentration/all/relate")
    R<RiskControlConcentrationAllRelateRSP> listAllRelate(@RequestBody RiskControlConcentrationRelateListREQ req);

    @ApiOperation("财报信息")
    @PostMapping("/risk/control/concentration/fri")
    R<FRIRsp> fri();
}