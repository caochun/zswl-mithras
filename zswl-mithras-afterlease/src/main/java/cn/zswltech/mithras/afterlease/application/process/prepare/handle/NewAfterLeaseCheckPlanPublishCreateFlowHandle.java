package cn.zswltech.mithras.afterlease.application.process.prepare.handle;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.workflow.application.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.process.prepare.CommonProcessPrepareMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class NewAfterLeaseCheckPlanPublishCreateFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        log.info("NewAfterLeaseCheckPlanPublishCreateFlowHandle commit {}", prepare);
        if (CharSequenceUtil.isBlank(prepare.getBusinessData())) {
            throw MithrasException.newException("请先保存【基本信息】模块！");
        }
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(prepare.getBusinessData()));
        if (ObjectUtil.isEmpty(prepare.getBusinessData()) || ObjectUtil.isEmpty(planBase)) {
            throw new MithrasException("未保存计划");
        }
        if (StrUtil.equalsAny(planBase.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.PUBLISH.name(), AfterLeaseCheckPlanStatusEnum.FINISH.name())) {
            throw new MithrasException("计划已发布");
        }

        CommonProcessPrepare toUpdate = CommonProcessPrepare.builder().id(prepare.getId()).isAssetConfirm("已确认").build();
        commonProcessPrepareMapper.updateById(toUpdate);
        return null;
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        if (StrUtil.isNotBlank(prepare.getBusinessData())) {
            SpringUtil.getBean(AfterLeaseCheckPlanBaseService.class).close(Long.valueOf(prepare.getBusinessData()));
        }
    }
}
