package cn.zswltech.mithras.riskcontrol.relation;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import cn.zswltech.mithras.riskcontrol.relation.RiskControlRelatedClient;
import org.mapstruct.Mapper;

/**
* @description 金控关联方名录
* @author zhaozhengkang
* @date 2023-03-08
*/
@Mapper(componentModel = "spring")
public interface RiskControlRelatedClientConverter{

    RiskControlRelatedTransactionRsp client2TransactionRsp(RiskControlRelatedClient relatedClient);
}