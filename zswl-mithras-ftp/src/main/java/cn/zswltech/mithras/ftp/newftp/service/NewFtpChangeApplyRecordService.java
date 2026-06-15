package cn.zswltech.mithras.ftp.newftp.service;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplyRecordRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplySaveREQ;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpChangeApplyRecordMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpChangeApplyRecord;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpInterestChangeAssessmentPort;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpWorkflowPort;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@Service
public class NewFtpChangeApplyRecordService extends ServiceImpl<NewFtpChangeApplyRecordMapper, NewFtpChangeApplyRecord> {
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private NewFtpWorkflowPort newFtpWorkflowPort;
    @Resource
    private NewFtpInterestChangeAssessmentPort assessmentPort;

    public NewFtpInterestChangeApplyRecordRSP detail(Long applyId) {
        NewFtpChangeApplyRecord record;
        NewFtpInterestChangeApplyRecordRSP rsp = new NewFtpInterestChangeApplyRecordRSP();
        if (Objects.nonNull(applyId)) {
            // 明确指定查询某一个申请数据，审批流详情中使用
            record = this.getById(applyId);
            if (Objects.isNull(record)) {
                throw new MithrasException("FTP计息申请记录不存在");
            }
            rsp.setApplyUserId(record.getApplyUserId());
        } else {
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            LambdaQueryWrapper<NewFtpChangeApplyRecord> applyRecordQuery = Wrappers.lambdaQuery();
            applyRecordQuery.eq(NewFtpChangeApplyRecord::getApplyUserId, currentUserId);
            applyRecordQuery.eq(NewFtpChangeApplyRecord::getApprovalStatus, ProcessStatus.UN_SUBMIT.name());
            applyRecordQuery.orderByDesc(NewFtpChangeApplyRecord::getId);
            applyRecordQuery.last(StringUtil.mysqlLimitOne());
            record = this.getOne(applyRecordQuery);
            rsp.setApplyUserId(currentUserId);
        }
        rsp.setApplyUserName(userNameResolver.sysUserId2NameSingle(rsp.getApplyUserId()));
        if (Objects.nonNull(record)) {
            rsp.setId(record.getId());
            // 填充FTP信息
            List<FtpAssessInfo> ftpAssessInfoList = assessmentPort.listByApplyId(record.getId());
            rsp.setFtpAssessmentInfoList(ftpAssessInfoList);
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long save(NewFtpInterestChangeApplySaveREQ req) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        Long ftpInterestChangeApplyRecordId = req.getId();
        if (Objects.isNull(ftpInterestChangeApplyRecordId)) {
            // 新增
            NewFtpChangeApplyRecord newFtpChangeApplyRecord = new NewFtpChangeApplyRecord();
            newFtpChangeApplyRecord.setApplyUserId(currentUserId);
            newFtpChangeApplyRecord.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            this.save(newFtpChangeApplyRecord);
            ftpInterestChangeApplyRecordId = newFtpChangeApplyRecord.getId();
        } else {
            NewFtpChangeApplyRecord record = this.getById(ftpInterestChangeApplyRecordId);
            if (Objects.isNull(record)) {
                throw new MithrasException("FTP计息变更记录不存在");
            }
            if (StrUtil.equalsAny(record.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())) {
                throw new MithrasException("审批通过的申请不允许修改");
            }
        }
        assessmentPort.replaceByApplyId(ftpInterestChangeApplyRecordId, req.getFtpAssessmentInfoList());
        return ftpInterestChangeApplyRecordId;
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submit(Long id) {
        // 校验数据
        if (!assessmentPort.existsByApplyId(id)) {
            throw new MithrasException("至少需要存在一条FTP考核信息");
        }
        // 修改审批状态
        NewFtpChangeApplyRecord record = this.getById(id);
        if (Objects.isNull(record)) {
            throw new MithrasException("FTP计息申请记录不存在");
        }
        record.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        this.updateById(record);
        return newFtpWorkflowPort.startInterestChangeApplyFlow(id);
    }

    public void modifyApprovalStatus(Long id, ProcessStatus processStatus) {
        LambdaUpdateWrapper<NewFtpChangeApplyRecord> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(NewFtpChangeApplyRecord::getApprovalStatus, processStatus.name());
        updateWrapper.eq(NewFtpChangeApplyRecord::getId, id);
        this.update(updateWrapper);
    }
}
