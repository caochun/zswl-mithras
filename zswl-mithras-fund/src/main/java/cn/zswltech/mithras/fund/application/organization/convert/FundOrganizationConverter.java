package cn.zswltech.mithras.fund.application.organization.convert;

import cn.zswltech.mithras.dto.fund.FundOrganizationAddREQ;
import cn.zswltech.mithras.dto.fund.FundOrganizationDetailRSP;
import cn.zswltech.mithras.dto.fund.FundOrganizationListRSP;
import cn.zswltech.mithras.dto.fund.FundOrganizationModifyREQ;
import cn.zswltech.mithras.fund.application.convert.FundTypeConversionWorker;
import cn.zswltech.mithras.fund.persistence.model.organization.FundOrganization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 16:42
 */
@Mapper(componentModel = "spring", uses = FundTypeConversionWorker.class)
public interface FundOrganizationConverter {

    @Mapping(target = "contactInfo", source = "contactInfo", qualifiedByName = "toJsonString")
    @Mapping(target = "addressInfo", source = "addressInfo", qualifiedByName = "toJsonString")
    @Mapping(target = "accountsInfo", source = "accountsInfo", qualifiedByName = "toJsonString")
    FundOrganization addReq2Entity(FundOrganizationAddREQ req);

    @Mapping(target = "contactInfo", source = "contactInfo", qualifiedByName = "toJsonString")
    @Mapping(target = "addressInfo", source = "addressInfo", qualifiedByName = "toJsonString")
    @Mapping(target = "accountsInfo", source = "accountsInfo", qualifiedByName = "toJsonString")
    FundOrganization modifyReq2Entity(FundOrganizationModifyREQ req);

    @Mapping(target = "contactInfo", source = "contactInfo", qualifiedByName = "jsonStringToContactInfo")
    @Mapping(target = "addressInfo", source = "addressInfo", qualifiedByName = "jsonStringToAddressInfo")
    @Mapping(target = "accountsInfo", source = "accountsInfo", qualifiedByName = "jsonStringToAccountInfoList")
    FundOrganizationDetailRSP entity2DetailRsp(FundOrganization fundOrganization);

    FundOrganizationListRSP entity2ListRsp(FundOrganization record);
}
