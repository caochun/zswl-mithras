package cn.zswltech.mithras.riskcontrol.concentration;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListRSP;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationGroup;
import org.mapstruct.Mapper;

import java.util.List;

/**
* @description 集团集中度
* @author zhaozhengkang
* @date 2023-03-02
*/
@Mapper(componentModel = "spring")
public interface RiskControlConcentrationGroupConverter{

    List<RiskControlConcentrationGroupListRSP> entity2ListRsp(List<RiskControlConcentrationGroup> records);
    RiskControlConcentrationGroupListRSP entity2ListRsp(RiskControlConcentrationGroup record);

}