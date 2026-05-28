package cn.zswltech.mithras.service.controller.afterlease;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.afterlease.AfterLeaseVersionApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustDetailREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCancelREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseAdjustEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.afterlease.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * 租后-版本管理
 * @author: jackerhe
 * @date: 2022/11/10 3:04 下午
 **/
@RestController
public class AfterLeaseVersionController implements AfterLeaseVersionApi {

    @Resource
    private AfterLeaseAdjustInfoService baseInfoService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientTransferService clientTransferService;

    @Override
    @DataAuthCheck(keyFieldName = "adjustId", checkerClass = CommonModifyMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.ADJUST)
    public R<Void> effect(AfterLeaseAdjustDetailREQ req) {
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.ADJUST.name(), req.getAdjustId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            AfterLeaseAdjustInfo baseInfo = baseInfoService.getById(req.getAdjustId());
            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            baseInfoService.checkDetail(baseInfo);
            List<ProcessResp> relatedProcess = baseInfoService.findRelatedProcesses(req.getAdjustId());
            if (ObjectUtil.isNotEmpty(relatedProcess)) {
                for (ProcessResp process : relatedProcess) {
                    ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.valueOf(process.getModelKey());
                    if (ProcessModelTypeEnum.AfterLeaseRepaymentFlow.equals(processModelTypeEnum) ||
                            ProcessModelTypeEnum.AfterLeaseExtendFlow.equals(processModelTypeEnum)) {
                        throw new MithrasException(String.format("项目调整评审已处于'%s'中，提交审批失败", processModelTypeEnum.getDisplay()));
                    }
                }
            }
            if (RecordStatus.CLOSED.name().equals(baseInfo.getAdjustStatus()) || RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getAdjustStatus())) {
                throw new MithrasException("项目调整已生效或关闭，不能提交审核");
            }
            if(!ProjProcessState.UN_SUBMIT.name().equals(baseInfo.getAdjustProcessStatus())){
                throw new MithrasException(String.format("项目调整处于%s,无法提交审批", Objects.requireNonNull(ProjProcessState.of(baseInfo.getAdjustProcessStatus())).display));
            }
            //客户移交
            if (clientTransferService.inTransfer(baseInfo.getClientId())) {
                err(ERR_IN_TRANSFER);
            }
            baseInfoService.effect(req.getAdjustId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "mainId", checkerClass = CommonModifyMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.ADJUST)
    public R<Void> adjustCancel(@Valid AfterLeaseCancelREQ req) {
        baseInfoService.adjustCancel(req);
        return R.ok();
    }

}
