package cn.zswltech.mithras.riskcontrol.scorecard.application.assembler;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetAddREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetListRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetModifyREQ;
import cn.zswltech.mithras.riskcontrol.common.RiskControlTypeConversionWorker;
import cn.zswltech.mithras.riskcontrol.scorecard.infrastructure.model.RiskControlScoreCardTarget;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/9 14:00
 */
@Component
public class RiskControlCardTargetConverter {

    @Resource
    private RiskControlTypeConversionWorker typeConversionWorker;

    public RiskControlScoreCardTarget addReq2Entity(RiskControlScoreCardTargetAddREQ req) {
        RiskControlScoreCardTarget target = BeanUtil.copyProperties(req, RiskControlScoreCardTarget.class);
        target.setOptionGrade(typeConversionWorker.toJsonString(req.getOptionGrade()));
        target.setAreaConfig(typeConversionWorker.toJsonString(req.getAreaConfig()));
        return target;
    }

    public RiskControlScoreCardTarget modifyReq2Entity(RiskControlScoreCardTargetModifyREQ req) {
        RiskControlScoreCardTarget target = BeanUtil.copyProperties(req, RiskControlScoreCardTarget.class);
        target.setOptionGrade(typeConversionWorker.toJsonString(req.getOptionGrade()));
        target.setAreaConfig(typeConversionWorker.toJsonString(req.getAreaConfig()));
        return target;
    }

    public RiskControlScoreCardTargetListRSP entity2Rsp(RiskControlScoreCardTarget req) {
        RiskControlScoreCardTargetListRSP rsp = BeanUtil.copyProperties(req, RiskControlScoreCardTargetListRSP.class);
        rsp.setOptionGrade(typeConversionWorker.jsonStringToRiskControlScoreCordOptionGradeType(req.getOptionGrade()));
        rsp.setAreaConfig(typeConversionWorker.jsonStringToRiskControlScoreCordAreaList(req.getAreaConfig()));
        return rsp;
    }

}
