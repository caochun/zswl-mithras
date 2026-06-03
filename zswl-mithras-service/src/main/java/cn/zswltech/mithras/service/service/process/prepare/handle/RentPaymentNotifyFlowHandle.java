package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.workflow.application.process.prepare.RentCollectionMonthDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.collection.ListUtil.toList;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class RentPaymentNotifyFlowHandle extends AbstractFlowCommitHandle {

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.RentPaymentNotifyFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setBusinessKey(valueOf(prepare.getId()));
        String processInstanceId = null;
        //
        List<RentCollectionMonthDetail> detailList = getBean(RentCollectionMonthDetailService.class).byPrepareId(prepare.getId());
        if (!detailList.isEmpty()) {
            Long deptId = detailList.get(0).getDeptId();
            Long deptLeader = getBean(SysUserService.class).getUserIdByOrgJob(deptId, JobEnum.businesshead.name());
            Long divisionLeader = getBean(SysUserService.class).getUserIdByOrgJob(deptId, JobEnum.leaderincharge.name());
            Map<String, Object> variables = MapUtil.<String, Object>builder()
                    .put("bizDeptLeader", isNull(deptLeader) ? new ArrayList<>() : toList(valueOf(deptLeader)))
                    .put("bizDivisionLeader", isNull(divisionLeader) ? new ArrayList<>() : toList(valueOf(divisionLeader)))
                    .build();
            req.setVariables(variables);
            processInstanceId = getBean(FlowProcessApiService.class).start(req);
        } else {
            log.info("没有任何的租金支付记录，无需提交流程");
        }
        return processInstanceId;
    }


}
