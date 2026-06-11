package cn.zswltech.mithras.blackgray.service.audit;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalSubmitREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayApprovalSubmitRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundDetailRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayManualOutboundMapper;
import cn.zswltech.mithras.blackgray.service.BlackGrayManualOutboundService;
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
public class BlackGrayOutboundAuditService  {

    @Resource
    private BlackGrayManualOutboundService blackGrayManualOutboundService;
    @Resource
    private BlackGrayManualOutboundMapper blackGrayManualOutboundMapper;
    @Resource
    private FlowProcessApiService processApiService;


    @Transactional(rollbackFor = Throwable.class)
    public BlackGrayApprovalSubmitRSP approvalSubmit(BlackGrayApprovalSubmitREQ req) {
        /*List<AuditTask> auditTasks = auditTaskService.getAuditTasksByBizIds(AuditBizTypeEnum.BLACK_GRAY_MANUAL_OUTBOUND.getType(), Collections.singletonList(req.getId()));
        Long auditTaskId = null;
        if (CollectionUtils.isNotEmpty(auditTasks) && auditTasks.get(0) != null) {
            auditTaskId = auditTasks.get(0).getId();
        }
        BlackGrayManualOutboundDetailRSP detail = blackGrayManualOutboundService.detail(req.getId());
        List<BaseAuditCmd.BizInstance> submit = this.submit(BaseAuditCmd
                .builder()
                .instances(Lists.newArrayList(new BaseAuditCmd.BizInstance(req.getId(), auditTaskId)))
                .bizType(AuditBizTypeEnum.BLACK_GRAY_MANUAL_OUTBOUND.getType())
                .auditUser(req.getAuditUser())
                .build(), FlowModelKeyConstant.BLACK_GRAY_MANUAL_OUTBOUND);
        //修改业务状态
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode());
        */
        BlackGrayManualOutboundDetailRSP detail = blackGrayManualOutboundService.detail(req.getId());
        if (ObjectUtil.isEmpty(detail)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode());
        // 生成流程实例
        StartProcessReq startProcessReq = buildCommonStartProcessReq(detail);
        startProcessReq.setModelKey(ProcessModelTypeEnum.BLACK_GRAY_OUTBOUND.name());
        String taskId = processApiService.start(startProcessReq);
        BlackGrayApprovalSubmitRSP rsp = new BlackGrayApprovalSubmitRSP();
        rsp.setBizId(req.getId());
        rsp.setTaskId(taskId);
        return rsp;
    }

    private StartProcessReq buildCommonStartProcessReq(BlackGrayManualOutboundDetailRSP detailRSP) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(detailRSP.getId()));
        startProcessReq.setProcessInstanceName(detailRSP.getEnterpriseName());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        //增加法律合规部负责人
        startProcessReq.setVariables(MapUtil.of(
        ));
        return startProcessReq;
    }

    public PageR<BlackGrayManualOutboundApprovalTaskRSP> auditList(BlackGrayApprovalTaskREQ req){
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayManualOutboundApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPS = blackGrayManualOutboundMapper.queryForAudit(req);
        PageInfo<BlackGrayManualOutboundApprovalTaskRSP> blackGrayManualOutboundApprovalTaskRSPPageInfo = new PageInfo<>(blackGrayWarehouseApprovalTaskRSPS);
        return PageR.of(blackGrayWarehouseApprovalTaskRSPS, blackGrayManualOutboundApprovalTaskRSPPageInfo.getTotal());
    }





    public void changeBusinessStatus(Long id, Integer status){
        //修改业务状态
        blackGrayManualOutboundService.updateStatue(id, status);
    }


}
