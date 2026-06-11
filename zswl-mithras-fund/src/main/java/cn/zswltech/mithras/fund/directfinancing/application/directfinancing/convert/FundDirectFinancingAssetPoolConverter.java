package cn.zswltech.mithras.fund.directfinancing.application.directfinancing.convert;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolModifyREQ;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingAssetPool;
import org.mapstruct.Mapper;

/**
 * @author zhaozhengkang
 * @description 直接融资-资产池信息
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingAssetPoolConverter {

    FundDirectFinancingAssetPool modifyReq2Entity(FundDirectFinancingAssetPoolModifyREQ req);

    FundDirectFinancingAssetPoolDetailRSP entity2DetailRsp(FundDirectFinancingAssetPool assetPool);
}
