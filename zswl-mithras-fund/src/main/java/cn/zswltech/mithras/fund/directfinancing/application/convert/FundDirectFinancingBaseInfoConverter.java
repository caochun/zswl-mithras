package cn.zswltech.mithras.fund.directfinancing.application.convert;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoModifyREQ;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingBaseInfoConverter {

    FundDirectFinancingBaseInfo addReq2Entity(FundDirectFinancingBaseInfoAddREQ req);

    FundDirectFinancingBaseInfo modifyReq2Entity(FundDirectFinancingBaseInfoModifyREQ req);

    FundDirectFinancingBaseInfoDetailRSP entity2DetailRsp(FundDirectFinancingBaseInfo baseInfo);

    FundDirectFinancingBaseInfoListRSP entity2ListRsp(FundDirectFinancingBaseInfo record);
    
}
