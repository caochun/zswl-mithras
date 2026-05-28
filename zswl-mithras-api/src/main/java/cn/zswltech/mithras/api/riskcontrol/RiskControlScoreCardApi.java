package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "风控策略-评分卡接口")
@RequestMapping("/risk/control/score/card")
public interface RiskControlScoreCardApi {

    @ApiOperation("导入经济数据")
    @PostMapping("/import")
    R<Void> importFile(RiskControlScoreCordImportREQ req);

    @ApiOperation("下载经济数据")
    @GetMapping("/downLoad")
    R<FileDownLoadRSP> downLoad(RiskControlScoreCordDownloadREQ req);

    /*@ApiOperation("生效")
    @PostMapping("/effect")
    R<Void> effect(@RequestBody @Valid RiskControlScoreEffectREQ req);*/

    @ApiOperation("地区全量查找")
    @PostMapping("/area/search")
    R<List<RiskControlScoreCordAreaSearchRSP>> areaSearch(@RequestBody @Valid RiskControlScoreCordAreaSearchREQ req);

    @ApiOperation("地区全量查找")
    @PostMapping("/area/all")
    R<List<RiskControlScoreCordAreaAllRSP>> areaAll(RiskControlScoreCordAreaSearchREQ req);

    @ApiOperation("试计算")
    @PostMapping("/try/calculate")
    R<RiskControlScoreCordTryCalculateRSP> tryCalculate(@RequestBody @Valid RiskControlScoreCordTryCalculateREQ req);

    @ApiOperation("获取评分卡信息")
    @PostMapping("/change/card")
    R<RiskControlScoreCordChangeCardRSP> calculate(@RequestBody @Valid RiskControlScoreCordCalculateREQ req);

    @ApiOperation("保存计算结果")
    @PostMapping("/calculate/save")
    R<Void> calculateSave(@RequestBody @Valid RiskControlScoreCordCalculateSaveREQ req);

    @ApiOperation("查询地区得分详情")
    @PostMapping("/calculate/detail")
    R<RiskControlScoreCordCalculateDetailRSP> calculateDetail(@RequestBody @Valid RiskControlScoreCordCalculateDetailREQ req);

}
