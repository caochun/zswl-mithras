package cn.zswltech.mithras.policy.application.info;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.policy.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.policy.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.persistence.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.policy.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.persistence.projection.NearPolicyEndTimeProjection;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyInfoSupportService {

    @Resource
    private PolicyInfoMapper policyInfoMapper;

    public List<NearPolicyEndTimeProjection> listNearPolicyEndTime(LocalDate endDate) {
        return policyInfoMapper.nearPolicyEndTimeList(endDate);
    }

    public PolicyInfo getById(Long id) {
        return policyInfoMapper.selectById(id);
    }

    public List<Long> listChildPolicyIds(Long parentId) {
        return policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                        .eq(PolicyInfo::getParentId, parentId))
                .stream()
                .map(PolicyInfo::getId)
                .collect(Collectors.toList());
    }

    public void refreshRenewInsuranceResultByChildren(Long policyId) {
        Integer renewInsuranceResult = policyInfoMapper.selectCount(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getParentId, policyId)) > 0
                ? YesOrNoNumberEnum.YES.getCode()
                : YesOrNoNumberEnum.NO.getCode();
        PolicyInfo policyInfo = new PolicyInfo();
        policyInfo.setId(policyId);
        policyInfo.setRenewInsuranceResult(renewInsuranceResult);
        policyInfoMapper.updateById(policyInfo);
    }

    public void clearAutomaticUnSubmitPolicy(Long policyId) {
        PolicyInfo info = new PolicyInfo();
        info.setId(policyId);
        info.setInsuranceCompany(null);
        info.setInsuranceEndDate(null);
        info.setInsuranceStartDate(null);
        info.setPolicyAmount(null);
        info.setPolicyCode(null);
        policyInfoMapper.updateAnnotationIncludeNullById(info);
    }

    public List<PolicyInfo> listAutomaticUnSubmitPolicies() {
        return policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getAutomatic, 1)
                .eq(PolicyInfo::getApprovalStatus, PolicyApprovalStatusEnum.NEW_UN_SUBMIT));
    }

    public Long createAutomaticUnSubmitPolicy(Long projId) {
        PolicyInfo info = new PolicyInfo();
        info.setProjId(projId);
        info.setApprovalStatus(PolicyApprovalStatusEnum.NEW_UN_SUBMIT.name());
        info.setAutomatic(1);
        policyInfoMapper.insert(info);
        return info.getId();
    }

    public List<PolicyInfo> listNeedStartReminderPolicies(LocalDate now) {
        return policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                .eq(PolicyInfo::getExpirationReminderFlag, YesOrNoNumberEnum.NO.getCode())
                .eq(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.NO.getCode())
                .ge(PolicyInfo::getInsuranceEndDate, now)
                .lt(PolicyInfo::getInsuranceEndDate, now.plusDays(15)));
    }

    public List<PolicyInfo> listNeedStartOverdueReminderPolicies(LocalDate now) {
        List<PolicyInfo> policies = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                .eq(PolicyInfo::getRenewalOverdueFlag, YesOrNoNumberEnum.NO.getCode())
                .eq(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.NO.getCode())
                .eq(PolicyInfo::getInsuranceEndDate, now.minusDays(5)));
        policies.addAll(policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                .ge(PolicyInfo::getCreateTime, LocalDate.of(2024, 12, 27))
                .eq(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.NO.getCode())
                .eq(PolicyInfo::getInsuranceEndDate, now.minusDays(30))));
        return policies;
    }

    public List<PolicyInfo> listReminderAutoCommitPolicies(LocalDate now) {
        return policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                .eq(PolicyInfo::getExpirationReminderFlag, YesOrNoNumberEnum.YES.getCode())
                .eq(PolicyInfo::getInsuranceEndDate, now));
    }

    public List<PolicyInfo> listOverdueAutoCommitPolicies(LocalDate now) {
        return policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                .eq(PolicyInfo::getRenewalOverdueFlag, YesOrNoNumberEnum.YES.getCode())
                .le(PolicyInfo::getInsuranceEndDate, now));
    }
}
