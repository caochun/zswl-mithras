package cn.zswltech.mithras.credit.application.groupcredit.convert.review;

import cn.zswltech.mithras.credit.application.groupcredit.convert.GroupCreditTypeConversionWorker;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoModifyREQ;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 集团授信评审
 * @author wangchuanhao
 * @date 2022/11/14 10:07 AM
 */
@Mapper(uses = GroupCreditTypeConversionWorker.class, componentModel = "spring")
public interface GroupCreditReviewBaseInfoConverter {

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "jsonStringToLongList")
    GroupCreditReviewBaseInfoDetailRSP entityToDetailRSP(GroupCreditReviewBaseInfo baseInfo);

    @Mapping(source = "projCosponsorUserIds", target = "projCosponsorUserIds", qualifiedByName = "toJsonString")
    GroupCreditReviewBaseInfo modifyREQtoEntity(GroupCreditReviewBaseInfoModifyREQ modifyREQ);

}
