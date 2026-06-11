package cn.zswltech.mithras.blackgray.service.audit;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalSubmitREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayApprovalSubmitRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayWarehouseTaskApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseTaskMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseTask;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseTaskService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 黑灰名单审批-入库申请
 *
 */
@Slf4j
@Service
public class BlackGrayWarehouseTaskAuditService {

    @Resource
    private BlackGrayWarehouseTaskService blackGrayWarehouseTaskService;
    @Resource
    private BlackGrayWarehouseTaskMapper blackGrayWarehouseTaskMapper;
    @Resource
    private FlowProcessApiService processApiService;

    @Transactional(rollbackFor = Throwable.class)
    public BlackGrayApprovalSubmitRSP approvalSubmit(BlackGrayApprovalSubmitREQ req) {
        BlackGrayWarehouseTask detail = blackGrayWarehouseTaskService.detail(req.getId());

        if (ObjectUtil.isEmpty(detail)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkSubmit(req);
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode(), null);
        StartProcessReq startProcessReq = buildCommonStartProcessReq(detail);
        startProcessReq.setModelKey(ProcessModelTypeEnum.BLACK_GRAY_WAREHOUSE_TASK.name());
        String taskId = processApiService.start(startProcessReq);
        //修改业务状态
        BlackGrayApprovalSubmitRSP blackGrayApprovalSubmitRSP = new BlackGrayApprovalSubmitRSP();
        blackGrayApprovalSubmitRSP.setBizId(req.getId());
        blackGrayApprovalSubmitRSP.setTaskId(taskId);
        return blackGrayApprovalSubmitRSP;
    }

    private StartProcessReq buildCommonStartProcessReq(BlackGrayWarehouseTask detial) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(detial.getId()));
        startProcessReq.setProcessInstanceName(detial.getTaskNum());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        //startProcessReq.setStartUserDeptId(Optional.ofNullable(detial.getApplyDept()).orElse(null));
        //增加法律合规部负责人
        startProcessReq.setVariables(MapUtil.of(
        ));
        return startProcessReq;
    }

    private void checkSubmit(BlackGrayApprovalSubmitREQ req){
        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskService.detail(req.getId());
        if(ObjectUtil.isEmpty(blackGrayWarehouseTask)){
            throw new MithrasException("记录不存在");
        }
        if(ObjectUtil.equals(AuditStatusEnum.AUDIT.getCode(), blackGrayWarehouseTask.getAuditStatus()) || ObjectUtil.equals(AuditStatusEnum.FINISH.getCode(), blackGrayWarehouseTask.getAuditStatus())){
            throw new MithrasException("已在流程中或流程已结束，不可发起审批");
        }
    }

    public PageR<BlackGrayWarehouseTaskApprovalTaskRSP> auditList(BlackGrayWarehouseApprovalTaskREQ req){
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayWarehouseTaskApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPS = blackGrayWarehouseTaskMapper.queryForAudit(req);
        PageInfo<BlackGrayWarehouseTaskApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPPageInfo = new PageInfo<>(blackGrayWarehouseApprovalTaskRSPS);
        return PageR.of(blackGrayWarehouseApprovalTaskRSPS, blackGrayWarehouseApprovalTaskRSPPageInfo.getTotal());
    }

    public void changeBusinessStatus(Long id, Integer status, Long taskId){
        //修改业务状态
        blackGrayWarehouseTaskService.updateStatue(id, status, taskId);
    }


}
