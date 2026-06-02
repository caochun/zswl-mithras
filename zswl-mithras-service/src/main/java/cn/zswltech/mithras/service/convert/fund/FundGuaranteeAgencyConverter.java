package cn.zswltech.mithras.service.convert.fund;

import cn.zswltech.mithras.dto.fund.FundGuaranteeAgencyDetailRSP;
import cn.zswltech.mithras.dto.fund.FundGuaranteeAgencyListRSP;
import cn.zswltech.mithras.dto.fund.FundGuaranteeAgencyModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.fund.FundGuaranteeAgency;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 16:44
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface FundGuaranteeAgencyConverter {
    FundGuaranteeAgency tycInfo2Entity(MithrasBaseInfo mithrasBaseInfo);

    FundGuaranteeAgency modifyReq2Entity(FundGuaranteeAgencyModifyREQ req);

    @Mapping(target = "totalGuaranteeLimit", source = "effectTotalLimit")
    FundGuaranteeAgencyListRSP entity2ListRsp(FundGuaranteeAgency entity);

    List<FundGuaranteeAgencyListRSP> entities2ListRsps(List<FundGuaranteeAgency> entities);


    FundGuaranteeAgencyDetailRSP entity2DetailRsp(FundGuaranteeAgency entity);
}
