package cn.zswltech.mithras.application.orchestration.facade.policy;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.policy.application.info.PolicyInfoVersionApplicationService;
import cn.zswltech.mithras.dto.policy.PolicyInfoCancelREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoEffectREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoRemoveREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.policy.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.policy.model.PolicyInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.policy.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.policy.PolicyInfoService;
import cn.zswltech.mithras.application.orchestration.policy.PolicyInfoVersionService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.associationreport.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.CLOSED;
import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.TAKE_EFFECT;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 * @create: 2023-06-15
 **/
@Slf4j
@Service
public class PolicyInfoVersionFacade implements PolicyInfoVersionApplicationService {

    @Resource
    private PolicyInfoVersionService policyInfoVersionService;
    @Resource
    private PolicyInfoMapper policyInfoMapper;
    @Resource
    private PolicyInfoService policyInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private RedisDistLock redisDistLock;

    @Override
    public R<Void> effect(@Valid PolicyInfoEffectREQ req) {
        // 加锁
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.POLICY.name(), req.getId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            PolicyInfo baseInfo = policyInfoMapper.selectById(req.getId());
            if (isNull(baseInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            // 是否可提交 简单校验
            if (Objects.nonNull(policyInfoVersionService.findRelatedProcess(req.getId()))) {
                throw new MithrasException("该保单数据变动处于流程中，无法提交数据");
            }

            policyInfoVersionService.effectCheck(baseInfo);

            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
            ChangeDTO changeDTO = policyInfoVersionService.checkActualChange(req.getId());
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            policyInfoVersionService.effect(req.getId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<Void> cancel(@Valid PolicyInfoCancelREQ req) {
        PolicyInfo info = policyInfoMapper.selectById(req.getId());
        Assert.notNull(info, () -> MithrasException.newException("保单信息不存在"));
        if (info.getApprovalStatus().equals(PolicyApprovalStatusEnum.NEW_UN_SUBMIT.name())){
            if (info.getAutomatic() == 0) {
                PolicyInfoRemoveREQ remove = new PolicyInfoRemoveREQ();
                remove.setId(req.getId());
                policyInfoService.remove(remove);
            }else {
                info.setInsuranceCompany(null);
                info.setInsuranceEndDate(null);
                info.setInsuranceStartDate(null);
                info.setPolicyAmount(null);
                info.setPolicyCode(null);
                policyInfoMapper.updateAnnotationIncludeNullById(info);
                materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBelongId,req.getId()).eq(MaterialsList::getBusinessType,BusinessModuleEnum.POLICY.name()).eq(MaterialsList::getMaterialsType,"POLICY"));
            }
        }else if (info.getApprovalStatus().equals(PolicyApprovalStatusEnum.CHANGING_UN_SUBMIT.name())){
            policyInfoVersionService.reset(req.getId());
        }
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(@Valid CommonVersionListREQ req) {
        if (StringUtils.isEmpty(req.getModule())) {
            req.setModule(BusinessModuleEnum.POLICY.name());
        }
        PageR<CommonVersionListRSP> data = policyInfoVersionService.selectPage(req);
        return R.ok(data);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@Valid PolicyInfoVersionDiffREQ req) {
        return R.ok(policyInfoVersionService.comparePreVersion(req.getId()));
    }
}
