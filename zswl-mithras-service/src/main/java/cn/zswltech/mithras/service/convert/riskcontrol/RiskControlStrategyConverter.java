package cn.zswltech.mithras.service.convert.riskcontrol;

import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyDetailRsp;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyListRsp;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlStrategyModifyReq;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import org.mapstruct.Mapper;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/9 14:00
 */
@Mapper(componentModel = "spring")
public interface RiskControlStrategyConverter {

    RiskControlStrategyListRsp entity2ListRsp(RiskControlStrategy riskControlStrategy);

    RiskControlStrategy modifyReq2Entity(RiskControlStrategyModifyReq req);

    RiskControlStrategyDetailRsp entity2DetailRsp(RiskControlStrategy strategy);
}
