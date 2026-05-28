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
* @description 评分卡基本信息
* @author vico
* @date 2023-02-27
*/
@Api(tags = "评分卡基本信息-接口")
public interface RiskControlScoreCardBaseInfoApi {

    @ApiOperation("新增评分卡基本信息")
    @PostMapping("/risk/control/score/card/base/info/add")
    R<RiskControlScoreCardBaseInfoDetailRSP> add(@RequestBody @Valid RiskControlScoreCardBaseInfoAddREQ req);

    @ApiOperation("评分卡基本信息详情")
    @PostMapping("/risk/control/score/card/base/info/detail")
    R<RiskControlScoreCardBaseInfoDetailRSP> detail(@RequestBody @Valid RiskControlScoreCardBaseInfoCommREQ req);

    @ApiOperation("修改评分卡基本信息")
    @PostMapping("/risk/control/score/card/base/info/modify")
    R<Void> modify(@RequestBody @Valid RiskControlScoreCardBaseInfoModifyREQ req);

    @ApiOperation("评分卡基本信息列表")
    @PostMapping("/risk/control/score/card/base/info/list")
    R<PageR<RiskControlScoreCardBaseInfoListRSP>> list(@RequestBody @Valid RiskControlScoreCardBaseInfoListREQ req);

    @ApiOperation("删除评分卡基本信息")
    @PostMapping("/risk/control/score/card/base/info/remove")
    R<Void> remove(@RequestBody @Valid RiskControlScoreCardBaseInfoRemoveREQ req);

}