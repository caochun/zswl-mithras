package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.AppraisalCompanyWhitelistProcessStatusEnum;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.AppraisalCompanyWhitelist;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.leaseholdproperty.AppraisalCompanyWhitelistService;
import cn.zswltech.mithras.service.service.lib.appraisalcompanywhitelist.AppraisalCompanyWhitelistVersionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@Component
public class AppraisalCompanyWhitelistProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private AppraisalCompanyWhitelistService appraisalCompanyWhitelistService;
    @Resource
    private AppraisalCompanyWhitelistVersionService appraisalCompanyWhitelistVersionService;

    public boolean needHandle(ProcessEndContext endContext) {
        return StrUtil.equalsAny(endContext.getModelKey(),
                ProcessModelTypeEnum.AppraisalCompanyWhitelistCreateFlow.name(),
                ProcessModelTypeEnum.AppraisalCompanyWhitelistModifyFlow.name(),
                ProcessModelTypeEnum.AppraisalCompanyWhitelistOutFlow.name()
        );
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.getByName(endContext.getModelKey());
        if (Objects.isNull(processModelTypeEnum)) {
            throw new MithrasException("未定义的流程模型类型");
        }
        AppraisalCompanyWhitelist record = appraisalCompanyWhitelistService.getById(Long.parseLong(endContext.getBusinessKey()));
        if (Objects.isNull(record)) {
            throw new MithrasException("主数据不存在");
        }
        switch (processModelTypeEnum) {
            case AppraisalCompanyWhitelistCreateFlow: {
                this.doCreateFlow(endContext, record);
                break;
            }
            case AppraisalCompanyWhitelistModifyFlow: {
                this.doModifyFlow(endContext, record);
                break;
            }
            case AppraisalCompanyWhitelistOutFlow: {
                this.doOutFlow(endContext, record);
                break;
            }
            default: {
                throw new MithrasException("未定义流程处理器");
            }
        }
    }

    private void doCreateFlow(ProcessEndContext endContext, AppraisalCompanyWhitelist record) {
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
                AppraisalCompanyWhitelist existEffectOne = appraisalCompanyWhitelistService.findEffectByUscCode(record.getUscCode());
                if (Objects.nonNull(existEffectOne) && !Objects.equals(existEffectOne.getId(), record.getId())) {
                    throw new MithrasException("已存在生效的评估机构");
                }
                record.setRecordStatus(RecordStatus.TAKE_EFFECT.name());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.NEW_APPROVAL_PASS.name());
                record.setRecordEffectDate(LocalDate.now());
                record.setRecordExpireDate(LocalDate.now().plusYears(1).minusDays(1));
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                record.setRecordStatus(RecordStatus.EXPIRE.name());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.NEW_REJECT.name());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                break;
            }
            case CANCEL: {
                record.setRecordStatus(RecordStatus.EXPIRE.name());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CANCEL_NEW.name());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                break;
            }
            default: {
                throw new MithrasException("未定义流程状态处理器");
            }
        }
    }

    private void doModifyFlow(ProcessEndContext endContext, AppraisalCompanyWhitelist record) {
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_APPROVAL_PASS.name());
                record.setRecordExpireDate(LocalDate.now().plusYears(1).minusDays(1));
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_REJECT.name());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                // 还原到上一个生效版本数据
                appraisalCompanyWhitelistVersionService.reset(record.getId());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CHANGE_REJECT.name());
                appraisalCompanyWhitelistService.updateById(record);
                break;
            }
            case CANCEL: {
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CANCEL_CHANGE.name());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                // 还原到上一个生效版本数据
                appraisalCompanyWhitelistVersionService.reset(record.getId());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CANCEL_CHANGE.name());
                appraisalCompanyWhitelistService.updateById(record);
                break;
            }
            default: {
                throw new MithrasException("未定义流程状态处理器");
            }
        }
    }

    private void doOutFlow(ProcessEndContext endContext, AppraisalCompanyWhitelist record) {
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
                record.setRecordStatus(RecordStatus.EXPIRE.name());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.OUT_APPROVAL_PASS.name());
                record.setRecordExpireDate(LocalDate.now());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.OUT_REJECT.name());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                // 还原到上一个生效版本数据
                appraisalCompanyWhitelistVersionService.reset(record.getId());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.OUT_REJECT.name());
                appraisalCompanyWhitelistService.updateById(record);
                break;
            }
            case CANCEL: {
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CANCEL_OUT.name());
                appraisalCompanyWhitelistService.updateById(record);
                appraisalCompanyWhitelistVersionService.recordVersion(record.getId(), VersionTypeEnum.APPROVAL, Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                // 还原到上一个生效版本数据
                appraisalCompanyWhitelistVersionService.reset(record.getId());
                record.setProcessStatus(AppraisalCompanyWhitelistProcessStatusEnum.CANCEL_OUT.name());
                appraisalCompanyWhitelistService.updateById(record);
                break;
            }
            default: {
                throw new MithrasException("未定义流程状态处理器");
            }
        }
    }
}
