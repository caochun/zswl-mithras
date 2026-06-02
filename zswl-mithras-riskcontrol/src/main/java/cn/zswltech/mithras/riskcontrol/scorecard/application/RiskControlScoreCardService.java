package cn.zswltech.mithras.riskcontrol.scorecard.application;

import cn.zswltech.mithras.dto.riskcontrol.scorecard.*;

import java.util.List;

public interface RiskControlScoreCardService {
    void importFile(RiskControlScoreCordImportREQ req);

    //生效
    Boolean effectAuth(Long cardId);

    //查找地区
    List<RiskControlScoreCordAreaSearchRSP> areaSearch(RiskControlScoreCordAreaSearchREQ req);

    //查找地区
    List<RiskControlScoreCordAreaAllRSP> areaAll(RiskControlScoreCordAreaSearchREQ req);

    //试计算
    RiskControlScoreCordTryCalculateRSP tryCalculate(RiskControlScoreCordTryCalculateREQ req);

    //选择适用评分卡
    RiskControlScoreCordChangeCardRSP calculate(RiskControlScoreCordCalculateREQ req);
}
