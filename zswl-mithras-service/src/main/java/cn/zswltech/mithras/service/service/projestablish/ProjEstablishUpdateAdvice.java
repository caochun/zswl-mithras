package cn.zswltech.mithras.service.service.projestablish;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.impl.ProjEstablishStateMachine;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.ProjStateMachine;
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
public interface ProjEstablishUpdateAdvice {

    default void saveCheck(Long projEstablishId) {
        ProjEstablishService projEstablishService = SpringContextHolder.getBean(ProjEstablishService.class);
        ProjReviewBaseInfoMapper projReviewBaseInfoMapper = SpringContextHolder.getBean(ProjReviewBaseInfoMapper.class);
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        if (Objects.isNull(projEstablishId)) {
            throw new MithrasException("立项信息不存在，不可修改数据");
        }
        if (!projEstablishService.canSave(projEstablishId)) {
            throw new MithrasException("立项信息处于审批流程中，不可修改数据");
        }
        List<ProjReviewBaseInfo> effectRefers = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjEstablishId, projEstablishId)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()));
        if (ObjectUtil.isNotEmpty(effectRefers)) {
            throw new MithrasException("项目立项已被评审模块引用，不可修改");
        }
    }

    default void recordStatus(Long projEstablishId) {
        ProjEstablishService projEstablishService = SpringContextHolder.getBean(ProjEstablishService.class);
        ProjEstablishBaseInfoMapper baseInfoMapper = SpringContextHolder.getBean(ProjEstablishBaseInfoMapper.class);
        ProcessResp processResp = projEstablishService.findRelatedProcess(projEstablishId);
        ProjEstablishBaseInfo baseInfo = baseInfoMapper.selectById(projEstablishId);
        ProjStateMachine stateMachine = SpringContextHolder.getBean(ProjEstablishStateMachine.class);
        if (Objects.isNull(baseInfo)) {
            return;
        }
        // 如果审批流程中保存了数据或状态为新建时 只更新最后更新时间 不更新客户状态
        if (RecordStatus.NEW.name().equals(baseInfo.getProjEstablishStatus()) || Objects.nonNull(processResp)) {
            // 只更新 最后更新时间
            LambdaUpdateWrapper<ProjEstablishBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjEstablishBaseInfo::getId, baseInfo.getId());
            updateWrapper.set(ProjEstablishBaseInfo::getUpdateTime, LocalDateTime.now());
            baseInfoMapper.update(null, updateWrapper);
            return;
        }
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
//        projEstablishService.recordProjEstablishStatus(projEstablishId, null, ProjEstablishProcessStatus.UN_SUBMIT);
    }

}
