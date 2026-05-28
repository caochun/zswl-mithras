package cn.zswltech.mithras.service.convert.riskcontrol;

import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetAddREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetListRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlScoreCardTargetModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlScoreCardTarget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/9 14:00
 */
@Mapper(uses = TypeConversionWorker.class,componentModel = "spring")
public interface RiskControlCardTargetConverter {

    @Mapping(source = "optionGrade", target = "optionGrade", qualifiedByName = "toJsonString")
    @Mapping(source = "areaConfig", target = "areaConfig", qualifiedByName = "toJsonString")
    RiskControlScoreCardTarget addReq2Entity(RiskControlScoreCardTargetAddREQ req);

    @Mapping(source = "optionGrade", target = "optionGrade", qualifiedByName = "toJsonString")
    @Mapping(source = "areaConfig", target = "areaConfig", qualifiedByName = "toJsonString")
    RiskControlScoreCardTarget modifyReq2Entity(RiskControlScoreCardTargetModifyREQ req);

    @Mapping(source = "optionGrade", target = "optionGrade", qualifiedByName = "jsonStringToRiskControlScoreCordOptionGradeType")
    @Mapping(source = "areaConfig", target = "areaConfig", qualifiedByName = "jsonStringToRiskControlScoreCordAreaList")
    RiskControlScoreCardTargetListRSP entity2Rsp(RiskControlScoreCardTarget req);

}
