package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.riskcontrol.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* @description 评分卡指标
* @author vico
* @date 2023-02-27
*/
@Api(tags = "评分卡指标-接口")
public interface RiskControlScoreCardTargetApi {

    @ApiOperation("新增评分卡指标")
    @PostMapping("/risk/control/score/card/target/add")
    R<String> add(@RequestBody @Valid RiskControlScoreCardTargetAddREQ req);

    @ApiOperation("修改评分卡指标")
    @PostMapping("/risk/control/score/card/target/modify")
    R<String> modify(@RequestBody @Valid RiskControlScoreCardTargetModifyREQ req);

    @ApiOperation("评分卡指标列表")
    @PostMapping("/risk/control/score/card/target/list")
    R<PageR<RiskControlScoreCardTargetListRSP>> list(@RequestBody @Valid RiskControlScoreCardTargetListREQ req);

    @ApiOperation("删除评分卡指标")
    @PostMapping("/risk/control/score/card/target/remove")
    R<Void> remove(@RequestBody @Valid RiskControlScoreCardTargetRemoveREQ req);

    @ApiOperation("查询指标名称")
    @PostMapping("/risk/control/score/card/target/search")
    R<List<RiskControlScoreCardTargetSearchRSP>> targetSearch(@RequestBody @Valid RiskControlScoreCardTargetSearchREQ req);

}