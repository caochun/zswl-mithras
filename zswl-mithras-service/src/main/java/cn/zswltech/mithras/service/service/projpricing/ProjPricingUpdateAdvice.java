package cn.zswltech.mithras.service.service.projpricing;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.impl.ProjPricingStateMachine;
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
        ProjPricingService projPricingService = SpringContextHolder.getBean(ProjPricingService.class);
//        ContractBaseInfoService contractBaseInfoService = SpringContextHolder.getBean(ContractBaseInfoService.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        if (Objects.isNull(projectId)) {
            throw new MithrasException("项目定价信息不存在，不可修改数据");
        }
        if (!projPricingService.canSave(projectId)) {
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
        ProjPricingService projPricingService = SpringContextHolder.getBean(ProjPricingService.class);
        ProjPricingBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(ProjPricingBaseInfoMapper.class);
        ProcessResp processResp = projPricingService.findRelatedProcess(projectId);
        ProjPricingBaseInfo baseInfo = baseInfoMapper.selectById(projectId);
        ProjPricingStateMachine stateMachine = SpringContextHolder.getBean(ProjPricingStateMachine.class);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或客户状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getProjPricingStatus()) || Objects.nonNull(processResp)) {
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
