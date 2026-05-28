package cn.zswltech.mithras.service.convert.groupcreditestablish;

import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.groupcreditestablish.GroupCreditEstablishBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 集团授信立项
 * @author wangchuanhao
 * @date 2022/11/14 10:07 AM
 */
@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface GroupCreditEstablishBaseInfoConverter {

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "riskControlManagerId", target = "riskControlManagerId", qualifiedByName = "jsonStringToLongList")
    GroupCreditEstablishBaseInfoDetailRSP entityToDetailRSP(GroupCreditEstablishBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    GroupCreditEstablishBaseInfo modifyREQtoEntity(GroupCreditEstablishBaseInfoModifyREQ modifyREQ);

}
