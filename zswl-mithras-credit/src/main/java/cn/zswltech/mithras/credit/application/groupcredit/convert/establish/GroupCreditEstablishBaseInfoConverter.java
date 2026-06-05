package cn.zswltech.mithras.credit.application.groupcredit.convert.establish;

import cn.zswltech.mithras.credit.application.groupcredit.convert.GroupCreditTypeConversionWorker;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoModifyREQ;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 集团授信立项
 * @author wangchuanhao
 * @date 2022/11/14 10:07 AM
 */
@Mapper(uses = GroupCreditTypeConversionWorker.class, componentModel = "spring")
public interface GroupCreditEstablishBaseInfoConverter {

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "riskControlManagerId", target = "riskControlManagerId", qualifiedByName = "jsonStringToLongList")
    GroupCreditEstablishBaseInfoDetailRSP entityToDetailRSP(GroupCreditEstablishBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    GroupCreditEstablishBaseInfo modifyREQtoEntity(GroupCreditEstablishBaseInfoModifyREQ modifyREQ);

}
