package cn.zswltech.mithras.service.convert.riskcontrol;

import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationClientListRSP;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlConcentrationClient;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@Mapper(componentModel = "spring")
public interface RiskControlConcentrationClientConverter {

    List<RiskControlConcentrationClientListRSP> entity2ListRsp(List<RiskControlConcentrationClient> records);

    RiskControlConcentrationClientListRSP entity2ListRsp(RiskControlConcentrationClient record);
}