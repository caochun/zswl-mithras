package cn.zswltech.mithras.projectprocess.application.projreview;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.state.ProjContext;
import cn.zswltech.mithras.foundation.state.ProjEvent;
import cn.zswltech.mithras.projectprocess.application.projfms.impl.ProjReviewStateMachine;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhaozhengkang
 * @description ProjEstablish更新后执行的切面操作
 * @date 2022-07-19
 */
public interface ProjReviewUpdateAdvice {

    default void saveCheck(Long projectId) {
        ProjReviewUpdateSupport updateSupport = SpringContextHolder.getBean(ProjReviewUpdateSupport.class);
//        ContractBaseInfoService contractBaseInfoService = SpringContextHolder.getBean(ContractBaseInfoService.class);
        if (Objects.isNull(projectId)) {
            throw new MithrasException("项目评审信息不存在，不可修改数据");
        }
        if (!updateSupport.canSave(projectId)) {
            throw new MithrasException("项目评审处于审批流程中，不可修改数据");
        }
//        List< ContractBaseInfo > contractBaseInfos = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
//                .eq(ContractBaseInfo::getProjReviewId, projectId)
//                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID));
//        if(ObjectUtil.isNotEmpty(contractBaseInfos)){
//            throw new MithrasException("项目评审已被合同引用，不可修改数据");
//        }
    }

    default void recordStatus(Long projectId) {
        ProjReviewUpdateSupport updateSupport = SpringContextHolder.getBean(ProjReviewUpdateSupport.class);
        ProjReviewBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(ProjReviewBaseInfoMapper.class);
        boolean hasRelatedProcess = updateSupport.hasRelatedProcess(projectId);
        ProjReviewBaseInfo baseInfo = baseInfoMapper.selectById(projectId);
        ProjReviewStateMachine stateMachine = SpringContextHolder.getBean(ProjReviewStateMachine.class);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或客户状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getProjReviewStatus()) || hasRelatedProcess) {
            // 只更新 最后更新时间
            LambdaUpdateWrapper<ProjReviewBaseInfo> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.eq(ProjReviewBaseInfo::getId, baseInfo.getId());
            updateWrapper.set(ProjReviewBaseInfo::getUpdateTime, LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
//        projReviewService.recordProjReviewStatus(projectId, null, ProjReviewProcessStatus.UN_SUBMIT);
    }

}
