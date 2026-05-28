package cn.zswltech.mithras.service.convert.fund;

import cn.zswltech.mithras.dto.fund.FundGuaranteeInfoAddREQ;
import cn.zswltech.mithras.dto.fund.FundGuaranteeInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.FundGuaranteeInfoListRSP;
import cn.zswltech.mithras.dto.fund.FundGuaranteeInfoModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.fund.FundGuaranteeInfo;
import org.mapstruct.Mapper;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 16:45
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface FundGuaranteeInfoConverter {

    FundGuaranteeInfo addReq2Entity(FundGuaranteeInfoAddREQ req);

    FundGuaranteeInfo modifyReq2Entity(FundGuaranteeInfoModifyREQ req);

    FundGuaranteeInfoDetailRSP entity2DetailRsp(FundGuaranteeInfo entity);

    FundGuaranteeInfoListRSP entity2ListRsp(FundGuaranteeInfo entity);
}
