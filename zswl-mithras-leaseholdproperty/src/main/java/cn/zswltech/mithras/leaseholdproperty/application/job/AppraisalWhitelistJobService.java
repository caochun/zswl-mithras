package cn.zswltech.mithras.leaseholdproperty.application.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.leaseholdproperty.versioning.appraisalcompanywhitelist.AppraisalCompanyWhitelistVersionService;
import cn.zswltech.mithras.leaseholdproperty.mapper.AppraisalCompanyWhitelistMapper;
import cn.zswltech.mithras.leaseholdproperty.model.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.OrgJobUserResolver;
import cn.zswltech.mithras.foundation.port.UserBizDeptInfoResolver;
import cn.zswltech.mithras.leaseholdproperty.application.port.AppraisalWhitelistJobPort;
import cn.zswltech.mithras.leaseholdproperty.application.port.AppraisalWhitelistNotificationPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@Slf4j
@Component
public class AppraisalWhitelistJobService implements AppraisalWhitelistJobPort {
    private static final int MESSAGE_NOTIFY_DAYS = 30;
    private static final String MESSAGE_NOTIFY_TEMPLATE = "【%s】创建的白名单评估机构【%s】将于【%s】日后到期，请关注处理！";

    @Resource
    private AppraisalCompanyWhitelistVersionService appraisalCompanyWhitelistVersionService;
    @Resource
    private AppraisalCompanyWhitelistMapper appraisalCompanyWhitelistMapper;
    @Resource
    private AppraisalWhitelistNotificationPort notificationPort;
    @Resource
    private UserBizDeptInfoResolver userBizDeptInfoResolver;
    @Resource
    private OrgJobUserResolver orgJobUserResolver;
    @Resource
    private DeptNameResolver deptNameResolver;

    @Override
    public void appraisalWhitelistDailyJob() {
        List<AppraisalCompanyWhitelist> todoList = appraisalCompanyWhitelistMapper.selectList(
                Wrappers.<AppraisalCompanyWhitelist>lambdaQuery().eq(AppraisalCompanyWhitelist::getRecordStatus, RecordStatus.TAKE_EFFECT.name())
        );
        if (CollectionUtil.isEmpty(todoList)) {
            log.info("没有生效的白名单准入的评估机构，不处理");
            return;
        }
        for (AppraisalCompanyWhitelist appraisalCompanyWhitelist : todoList) {
            try {
                this.doSingle(appraisalCompanyWhitelist);
            } catch (Exception e) {
                log.error("评估机构白名单准入批处理失败[{}]", JSONUtil.toJsonStr(appraisalCompanyWhitelist), e);
            }
        }
    }

    private void doSingle(AppraisalCompanyWhitelist appraisalCompanyWhitelist) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(appraisalCompanyWhitelist.getRecordExpireDate())) {
            // 失效
            appraisalCompanyWhitelist.setRecordStatus(RecordStatus.EXPIRE.name());
            appraisalCompanyWhitelistMapper.updateById(appraisalCompanyWhitelist);
            // 写版本数据
            appraisalCompanyWhitelistVersionService.recordVersion(appraisalCompanyWhitelist.getId(), VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
            return;
        }
        long days = LocalDateTimeUtil.between(now.atStartOfDay(), appraisalCompanyWhitelist.getRecordExpireDate().atStartOfDay(), ChronoUnit.DAYS);
        if (days == MESSAGE_NOTIFY_DAYS) {
            // 发送到期通知
            this.sendNotify(appraisalCompanyWhitelist, days);
        }
    }

    private void sendNotify(AppraisalCompanyWhitelist appraisalCompanyWhitelist, long days) {
        // 找到部门负责人
        OrgDO org = userBizDeptInfoResolver.getBizDeptByUserId(appraisalCompanyWhitelist.getCreateBy());
        if (Objects.isNull(org)) {
            return;
        }
        Long businessheadId = orgJobUserResolver.orgJobUsers(org.getId(), JobEnum.businesshead.name()).stream().findFirst().orElse(null);
        List<Long> toIds = new LinkedList<>();
        if (Objects.nonNull(appraisalCompanyWhitelist.getCreateBy())) {
            toIds.add(appraisalCompanyWhitelist.getCreateBy());
        }
        if (Objects.nonNull(businessheadId)) {
            toIds.add(businessheadId);
        }
        if (CollectionUtil.isEmpty(toIds)) {
            return;
        }
        String relation = String.format(MESSAGE_NOTIFY_TEMPLATE, deptNameResolver.deptId2NameSingle(appraisalCompanyWhitelist.getDeptId()), appraisalCompanyWhitelist.getCompanyName(), days);
        notificationPort.sendExpireRemind(appraisalCompanyWhitelist.getId(), toIds, relation);
        log.info("评估机构白名单准入到期通知发送完成[whitelistId:{}, toIds:{}]", appraisalCompanyWhitelist.getId(), JSONUtil.toJsonStr(toIds));
    }
}
