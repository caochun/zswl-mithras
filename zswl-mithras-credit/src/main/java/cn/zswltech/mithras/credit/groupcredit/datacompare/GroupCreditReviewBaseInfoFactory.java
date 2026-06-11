package cn.zswltech.mithras.credit.groupcredit.datacompare;

import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoLibMapper;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.credit.groupcredit.review.versioning.handler.impl.GroupCreditReviewBaseInfoLibHandler;
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
        return new DefaultDataCompare<GroupCreditReviewBaseInfo, GroupCreditReviewBaseInfoLib, GroupCreditReviewBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper, "GROUP_CREDIT_REVIEW", version);
    }
}
