package cn.zswltech.mithras.projectprocess.application.projpricing;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.state.ProjContext;
import cn.zswltech.mithras.foundation.state.ProjEvent;
import cn.zswltech.mithras.projectprocess.application.projfms.impl.ProjPricingStateMachine;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhaozhengkang
 * @description ProjEstablish更新后执行的切面操作
 * @date 2022-07-19
 */
public interface ProjPricingUpdateAdvice {

    default void saveCheck(Long projectId) {
        ProjPricingUpdateSupport updateSupport = SpringContextHolder.getBean(ProjPricingUpdateSupport.class);
//        ContractBaseInfoService contractBaseInfoService = SpringContextHolder.getBean(ContractBaseInfoService.class);
        if (Objects.isNull(projectId)) {
            throw new MithrasException("项目定价信息不存在，不可修改数据");
        }
        if (!updateSupport.canSave(projectId)) {
            throw new MithrasException("项目定价处于审批流程中，不可修改数据");
        }
//        List< ContractBaseInfo > contractBaseInfos = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
//                .eq(ContractBaseInfo::getProjReviewId, projectId)
//                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID));
//        if(ObjectUtil.isNotEmpty(contractBaseInfos)){
//            throw new MithrasException("项目评审已被合同引用，不可修改数据");
//        }
    }

    default void recordStatus(Long projectId) {
        ProjPricingUpdateSupport updateSupport = SpringContextHolder.getBean(ProjPricingUpdateSupport.class);
        ProjPricingBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(ProjPricingBaseInfoMapper.class);
        boolean hasRelatedProcess = updateSupport.hasRelatedProcess(projectId);
        ProjPricingBaseInfo baseInfo = baseInfoMapper.selectById(projectId);
        ProjPricingStateMachine stateMachine = SpringContextHolder.getBean(ProjPricingStateMachine.class);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或客户状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getProjPricingStatus()) || hasRelatedProcess) {
            // 只更新 最后更新时间
            LambdaUpdateWrapper<ProjPricingBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjPricingBaseInfo::getId, baseInfo.getId());
            updateWrapper.set(ProjPricingBaseInfo::getUpdateTime, LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
//        projReviewService.recordProjReviewStatus(projectId, null, ProjReviewProcessStatus.UN_SUBMIT);
    }

}
