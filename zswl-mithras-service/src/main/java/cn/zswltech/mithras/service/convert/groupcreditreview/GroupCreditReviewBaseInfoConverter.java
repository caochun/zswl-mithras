package cn.zswltech.mithras.service.convert.groupcreditreview;

import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoModifyREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 集团授信评审
 * @author wangchuanhao
 * @date 2022/11/14 10:07 AM
 */
@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface GroupCreditReviewBaseInfoConverter {

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    GroupCreditReviewBaseInfoDetailRSP entityToDetailRSP(GroupCreditReviewBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    GroupCreditReviewBaseInfo modifyREQtoEntity(GroupCreditReviewBaseInfoModifyREQ modifyREQ);

}
