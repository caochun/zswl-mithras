package cn.zswltech.mithras.blackgray.service.audit;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalSubmitREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayApprovalTaskREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayApprovalSubmitRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessApprovalTaskRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessDetailRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayBreakBusinessMapper;
import cn.zswltech.mithras.blackgray.port.BlackGrayApprovalProcessPort;
import cn.zswltech.mithras.blackgray.port.BlackGrayApprovalProcessType;
import cn.zswltech.mithras.blackgray.service.BlackGrayBreakBusinessService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 黑灰名单审批-入库申请
 *
 */
@Slf4j
@Service
public class BlackGrayBreakBusinessAuditService  {


    @Resource
    private BlackGrayBreakBusinessService blackGrayBreakBusinessService;
    @Resource
    private BlackGrayBreakBusinessMapper blackGrayBreakBusinessMapper;
    @Resource
    private BlackGrayApprovalProcessPort blackGrayApprovalProcessPort;


    @Transactional(rollbackFor = Throwable.class)
    public BlackGrayApprovalSubmitRSP approvalSubmit(BlackGrayApprovalSubmitREQ req) {
        /*List<AuditTask> auditTasks = auditTaskService.getAuditTasksByBizIds(AuditBizTypeEnum.BLACK_GRAY_BUSINESS_BREAK.getType(), Collections.singletonList(req.getId()));
        Long auditTaskId = null;
        if (CollectionUtils.isNotEmpty(auditTasks) && auditTasks.get(0) != null) {
            auditTaskId = auditTasks.get(0).getId();
        }
        List<BaseAuditCmd.BizInstance> submit = this.submit(BaseAuditCmd
                .builder()
                .instances(Lists.newArrayList(new BaseAuditCmd.BizInstance(req.getId(), auditTaskId)))
                .bizType(AuditBizTypeEnum.BLACK_GRAY_BUSINESS_BREAK.getType())
                .auditUser(req.getAuditUser())
                .build(), FlowModelKeyConstant.BLACK_GRAY_BUSINESS_BREAK);
        //修改业务状态
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode());
        return BeanUtil.copyProperties(submit, BlackGrayApprovalSubmitRSP.class);*/
        BlackGrayBreakBusinessDetailRSP detail = blackGrayBreakBusinessService.detail(req.getId());
        if(ObjectUtil.isEmpty(detail)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //修改业务状态
        changeBusinessStatus(req.getId(), (int) AuditStatusEnum.AUDIT.getCode());
        String taskId = blackGrayApprovalProcessPort.start(BlackGrayApprovalProcessType.BREAK,
                detail.getId(), detail.getEnterpriseName(), null);
        BlackGrayApprovalSubmitRSP rsp = new BlackGrayApprovalSubmitRSP();
        rsp.setBizId(req.getId());
        rsp.setTaskId(taskId);
        return rsp;
    }

    public PageR<BlackGrayBreakBusinessApprovalTaskRSP> auditList(BlackGrayApprovalTaskREQ req){
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayBreakBusinessApprovalTaskRSP> blackGrayWarehouseApprovalTaskRSPS = blackGrayBreakBusinessMapper.queryForAudit(req);
        PageInfo<BlackGrayBreakBusinessApprovalTaskRSP> blackGrayManualOutboundApprovalTaskRSPPageInfo = new PageInfo<>(blackGrayWarehouseApprovalTaskRSPS);
        return PageR.of(blackGrayWarehouseApprovalTaskRSPS, blackGrayManualOutboundApprovalTaskRSPPageInfo.getTotal());
    }

    private void changeBusinessStatus(Long id, Integer status){
        //修改业务状态
        blackGrayBreakBusinessService.updateStatue(id, status);
    }


}
