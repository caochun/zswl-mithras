package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoLibMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.credit.application.groupcredit.review.handler.impl.GroupCreditReviewBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("groupCreditReviewBaseInfo")
public class GroupCreditReviewBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private GroupCreditReviewBaseInfoLibMapper libMapper;
    @Resource
    private GroupCreditReviewBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<GroupCreditReviewBaseInfo, GroupCreditReviewBaseInfoLib, GroupCreditReviewBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.GROUP_CREDIT_REVIEW.name(), version);
    }
}
