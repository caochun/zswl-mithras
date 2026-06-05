package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.leaseholdproperty.application.lib.appraisalcompanywhitelist.AppraisalCompanyWhitelistVersionService;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.leaseholdproperty.AppraisalCompanyWhitelistService;
import cn.zswltech.mithras.message.service.MessageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
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
public class AppraisalWhitelistJob {
    private static final int MESSAGE_NOTIFY_DAYS = 30;
    private static final String MESSAGE_NOTIFY_TEMPLATE = "【%s】创建的白名单评估机构【%s】将于【%s】日后到期，请关注处理！";

    @Resource
    private AppraisalCompanyWhitelistVersionService appraisalCompanyWhitelistVersionService;
    @Resource
    private AppraisalCompanyWhitelistService appraisalCompanyWhitelistService;
    @Resource
    private MessageService messageService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;

    @XxlJob("AppraisalWhitelistDailyJob")
    public void AppraisalWhitelistDailyJob() {
        List<AppraisalCompanyWhitelist> todoList = appraisalCompanyWhitelistService.list(
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
            appraisalCompanyWhitelistService.updateById(appraisalCompanyWhitelist);
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
        OrgDO org = sysUserService.getBizDeptByUserId(appraisalCompanyWhitelist.getCreateBy());
        if (Objects.isNull(org)) {
            return;
        }
        Long businessheadId = sysUserService.getUserIdByOrgJob(org.getId(), JobEnum.businesshead.name());
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
        // 发送通知
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        MessageUrlEnum messageUrlEnum = MessageUrlEnum.APPRAISAL_COMPANY_WHITELIST_EXPIRE;
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(toIds);
        messageAddREQ.setContent(String.valueOf(appraisalCompanyWhitelist.getId()));
        messageAddREQ.setFlowid(String.valueOf(appraisalCompanyWhitelist.getId()));
        messageAddREQ.setRelation(String.format(MESSAGE_NOTIFY_TEMPLATE, id2NameService.deptId2NameSingle(appraisalCompanyWhitelist.getDeptId()), appraisalCompanyWhitelist.getCompanyName(), days));
        messageAddREQ.setNeedOa(Boolean.FALSE);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.APPRAISAL_COMPANY_WHITELIST_EXPIRE.name());
        messageAddREQ.setMessageType(MessageTypeEnum.APPRAISAL_COMPANY_WHITELIST_EXPIRE.name());
        messageAddREQ.setPcurl(String.format(messageUrlEnum.pcUrl, appraisalCompanyWhitelist.getId()));
        messageAddREQ.setAppurl(messageUrlEnum.appUrl);
        messageAddREQ.setBusinessId(String.valueOf(appraisalCompanyWhitelist.getId()));
        List<Long> msgIds = messageService.sendMessage(SpringUtil.getBean(MessageConver.class).reqToTodoMessage(messageAddREQ));
        log.info("评估机构白名单准入到期通知发送完成[content:{}, msgIds:{}]", JSONUtil.toJsonStr(messageAddREQ), JSONUtil.toJsonStr(msgIds));
    }
}
