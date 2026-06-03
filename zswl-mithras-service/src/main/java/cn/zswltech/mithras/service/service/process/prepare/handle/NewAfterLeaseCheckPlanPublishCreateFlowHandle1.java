package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class NewAfterLeaseCheckPlanPublishCreateFlowHandle1 extends AbstractFlowCommitHandle {

    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        afterLeaseCheckPlanClientService.submitApproval(Long.valueOf(prepare.getBusinessId()));
        return null;
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanClient::getId, Long.parseLong(prepare.getBusinessId()));
        query.last(StringUtil.mysqlLimitOne());
        NewAfterLeaseCheckPlanClient checkPlanClient = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).getOne(query);
        if (Objects.nonNull(checkPlanClient)) {
            SpringUtil.getBean(AfterLeaseCheckPlanBaseService.class).close(checkPlanClient.getPlanId());
        }
    }
}
