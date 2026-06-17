package cn.zswltech.mithras.application.orchestration.adapter.blackgray;

import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.blackgray.application.port.BlackGrayApprovalProcessPort;
import cn.zswltech.mithras.blackgray.application.port.BlackGrayApprovalProcessType;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class BlackGrayApprovalProcessPortAdapter implements BlackGrayApprovalProcessPort {

    @Resource
    private FlowProcessApiService processApiService;

    @Override
    public String start(BlackGrayApprovalProcessType type, Long businessId, String processInstanceName, String startUserDeptId) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(toProcessModelType(type).name());
        startProcessReq.setBusinessKey(String.valueOf(businessId));
        startProcessReq.setProcessInstanceName(processInstanceName);
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(startUserDeptId);
        startProcessReq.setVariables(MapUtil.of());
        return processApiService.start(startProcessReq);
    }

    private ProcessModelTypeEnum toProcessModelType(BlackGrayApprovalProcessType type) {
        switch (type) {
            case BREAK:
                return ProcessModelTypeEnum.BLACK_GRAY_BREAK;
            case OUTBOUND:
                return ProcessModelTypeEnum.BLACK_GRAY_OUTBOUND;
            case WAREHOUSE:
                return ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE;
            case WAREHOUSE_TASK:
                return ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE_TASK;
            default:
                throw new MithrasException("不支持的黑灰审批流程类型");
        }
    }
}
