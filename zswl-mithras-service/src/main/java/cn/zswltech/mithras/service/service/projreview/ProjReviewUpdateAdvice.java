package cn.zswltech.mithras.service.service.projreview;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.impl.ProjReviewStateMachine;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author zhaozhengkang
 * @description ProjEstablish更新后执行的切面操作
 * @date 2022-07-19
 */
public interface ProjReviewUpdateAdvice {

    default void saveCheck(Long projectId) {
        ProjReviewService projReviewService = SpringContextHolder.getBean(ProjReviewService.class);
//        ContractBaseInfoService contractBaseInfoService = SpringContextHolder.getBean(ContractBaseInfoService.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        if (Objects.isNull(projectId)) {
            throw new MithrasException("项目评审信息不存在，不可修改数据");
        }
        if (!projReviewService.canSave(projectId)) {
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
        ProjReviewService projReviewService = SpringContextHolder.getBean(ProjReviewService.class);
        ProjReviewBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(ProjReviewBaseInfoMapper.class);
        ProcessResp processResp = projReviewService.findRelatedProcess(projectId);
        ProjReviewBaseInfo baseInfo = baseInfoMapper.selectById(projectId);
        ProjReviewStateMachine stateMachine = SpringContextHolder.getBean(ProjReviewStateMachine.class);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或客户状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getProjReviewStatus()) || Objects.nonNull(processResp)) {
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
