package cn.zswltech.mithras.service.convert.fund;

import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.mapper.model.fund.FundCreditGuaranteeDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 16:44
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface FundCreditConverter {
    FundCredit addReq2Entity(FundCreditAddREQ req);

    @Mapping(target = "enhanceCreditMethod", source = "enhanceCreditMethod", qualifiedByName = "toJsonString")
    FundCredit modifyReq2Entity(FundCreditModifyREQ req);

    List<FundCreditGuaranteeDetail> detailDto2Entity(List<FundCreditGuaranteeDetailDto> guaranteeDetail);

    List<FundCreditGuaranteeDetailDto> entity2DetailDto(List<FundCreditGuaranteeDetail> byCreditId);


    @Mapping(target = "enhanceCreditMethod", source = "enhanceCreditMethod", qualifiedByName = "jsonStringToStringList")
    FundCreditDetailRSP entity2DetailRsp(FundCredit fundCredit);

    FundCreditListRSP.FundCreditList entity2ListRsp(FundCredit fundCredit);
}

