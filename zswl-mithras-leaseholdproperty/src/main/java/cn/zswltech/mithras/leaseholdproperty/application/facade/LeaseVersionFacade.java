package cn.zswltech.mithras.leaseholdproperty.application.facade;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseVersionApplicationService;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseReviewEffectREQ;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemInfoService;
import cn.zswltech.mithras.leaseholdproperty.application.review.LeaseReviewService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.util.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Objects;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;

@Service
public class LeaseVersionFacade implements LeaseVersionApplicationService {

    private static final String CONTRACT_MODULE = "CONTRACT";

    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private LeaseReviewService leaseReviewService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Void> effect(LeaseReviewEffectREQ param) {
        //合同维度提起到
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(CONTRACT_MODULE, param.getContractId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(param.getContractId());
            if (ObjectUtils.isEmpty(contractBaseInfo)) {
                throw new MithrasException("未查询到合同信息");
            }
            //项目存在审批中数据时不允许再次发起
            if (leaseItemInfoService.count(Wrappers.<LeaseItemInfo>lambdaQuery()
            .eq(LeaseItemInfo::getProjReviewId, contractBaseInfo.getProjReviewId())
            .eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.UNDER_APPROVAL.name())) > 0) {
                throw new MithrasException("该项目下已存在审批中租赁物流程");
            }
            if(!ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType()) || !LeaseType.hui_zu.name().equals(contractBaseInfo.getLeaseType())) {
                throw new MithrasException("非回租项目，不可发起租赁物变更流程");
            }
            //验证岗位
            if(!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name(), JobEnum.operationManagement.name(), JobEnum.operationmanagementagent.name(), JobEnum.yunYingGuanLi.name(), JobEnum.legalmanager.name())){
                throw new MithrasException("非项目经理/运营/法务，不可发起租赁物变更流程");
            }
            //保存基本信息
            LeaseItemInfo leaseItemInfo = new LeaseItemInfo();
            leaseItemInfo.setProjReviewId(contractBaseInfo.getProjReviewId());
            leaseItemInfo.setProjName(contractBaseInfo.getProjName());
            leaseItemInfo.setClientId(contractBaseInfo.getClientId());
            //leaseItemInfo.setFlowId(param.getFlowId());
            leaseItemInfo.setProjSponsorUserId(contractBaseInfo.getProjSponsorUserId());
            leaseItemInfo.setProjCosponsorUserIds(contractBaseInfo.getProjCosponsorUserIds());
            leaseItemInfo.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
            leaseItemInfoService.save(leaseItemInfo);
            //存量项目为创建审批，非存量为变更审批
            Long projReviewId = contractBaseInfo.getProjReviewId();
            Integer stockContractFlag = contractBaseInfoService.getStockContractFlag(projReviewId);
            if (Objects.equals(stockContractFlag, YesOrNoNumberEnum.YES.getCode())) {
                leaseReviewService.effect(leaseItemInfo.getId(), ProcessModelTypeEnum.LeaseCreateFlow);
            } else {
                leaseReviewService.effect(leaseItemInfo.getId(), ProcessModelTypeEnum.LeaseModifyFlow);
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }
}
