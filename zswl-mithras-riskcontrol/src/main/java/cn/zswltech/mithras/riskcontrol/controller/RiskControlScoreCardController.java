package cn.zswltech.mithras.riskcontrol.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlScoreCardApi;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.*;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.riskcontrol.application.RiskControlScoreCardApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class RiskControlScoreCardController implements RiskControlScoreCardApi {
    @Resource
    private RiskControlScoreCardApplicationService riskControlScoreCardApplicationService;

    @Override
    public R<Void> importFile(RiskControlScoreCordImportREQ req) {
        return riskControlScoreCardApplicationService.importFile(req);
    }

    @Override
    public R<FileDownLoadRSP> downLoad(RiskControlScoreCordDownloadREQ req) {
        return riskControlScoreCardApplicationService.downLoad(req);
    }

    @Override
    public R<List<RiskControlScoreCordAreaSearchRSP>> areaSearch(@Valid RiskControlScoreCordAreaSearchREQ req) {
        return riskControlScoreCardApplicationService.areaSearch(req);
    }

    @Override
    public R<List<RiskControlScoreCordAreaAllRSP>> areaAll(RiskControlScoreCordAreaSearchREQ req) {
        return riskControlScoreCardApplicationService.areaAll(req);
    }

    @Override
    public R<RiskControlScoreCordTryCalculateRSP> tryCalculate(RiskControlScoreCordTryCalculateREQ req) {
        return riskControlScoreCardApplicationService.tryCalculate(req);
    }

    @Override
    public R<RiskControlScoreCordChangeCardRSP> calculate(@Valid RiskControlScoreCordCalculateREQ req) {
        return riskControlScoreCardApplicationService.calculate(req);
    }

    @Override
    public R<Void> calculateSave(RiskControlScoreCordCalculateSaveREQ req) {
        return riskControlScoreCardApplicationService.calculateSave(req);
    }

    @Override
    public R<RiskControlScoreCordCalculateDetailRSP> calculateDetail(RiskControlScoreCordCalculateDetailREQ req) {
        return riskControlScoreCardApplicationService.calculateDetail(req);
    }

}
