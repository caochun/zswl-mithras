package cn.zswltech.mithras.credit.application.groupcredit.review.service.impl;

import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoLibMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.GroupCreditReviewBaseInfoLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Service
public class GroupCreditReviewBaseInfoLibServiceImpl extends ServiceImpl<GroupCreditReviewBaseInfoLibMapper, GroupCreditReviewBaseInfoLib>
        implements GroupCreditReviewBaseInfoLibService {
}
