package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.mapper.model.payment.FtpAssessmentInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpChangeApplyRecordService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@Slf4j
@Component
public class FtpInterestChangeProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private NewFtpChangeApplyRecordService newFtpChangeApplyRecordService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.FtpInterestChangeApplyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        Long recordId = Long.valueOf(endContext.getBusinessKey());
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
                newFtpChangeApplyRecordService.modifyApprovalStatus(recordId, ProcessStatus.APPROVAL_PASS);
                ftpAssessmentInfoService.effectByApplyId(recordId);
                // 异步重算对应FTP计息
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        ThreadPoolUtil.getCommonPool().execute(() -> {
                            List<FtpAssessmentInfo> list = ftpAssessmentInfoService.listEffectByApplyId(recordId);
                            if (CollectionUtil.isEmpty(list)) {
                                return;
                            }
                            for (FtpAssessmentInfo ftpAssessmentInfo : list) {
                                try {
                                    ftpInterestBaseInfoService.calculateFtpInterestTimeRangeWithDiff(ftpAssessmentInfo, ftpAssessmentInfo.getFtpInterestDiffDate());
                                } catch (Exception e) {
                                    log.error("FTP计息变更流程通过后重算FTP发生异常[{}]", JSONUtil.toJsonStr(ftpAssessmentInfo), e);
                                }
                            }
                        });
                    }
                });
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                newFtpChangeApplyRecordService.modifyApprovalStatus(recordId, ProcessStatus.APPROVAL_REJECT);
                break;
            }
            case CANCEL: {
                newFtpChangeApplyRecordService.modifyApprovalStatus(recordId, ProcessStatus.UN_SUBMIT);
                break;
            }
            default: {
                throw new MithrasException("未定义的流程结束状态");
            }
        }
    }
}
