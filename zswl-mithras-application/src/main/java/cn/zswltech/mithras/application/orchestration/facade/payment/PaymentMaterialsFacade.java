package cn.zswltech.mithras.application.orchestration.facade.payment;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.payment.application.PaymentMaterialsApplicationService;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsOperateReq;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.payment.enums.LendingMaterialType;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentMaterialsService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 14:34
 */
@Service
public class PaymentMaterialsFacade implements PaymentMaterialsApplicationService {
    @Resource
    private PaymentMaterialsService materialsService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<Map<String, List<PaymentMaterialsListRsp>>> list(PaymentMaterialsListReq req) {
        return R.ok(materialsService.listLending(req));
    }

    @Override
    public R<Void> upload(MultipartFile file, Long paymentId, String materialsType) {
        if (LendingMaterialType.LOAN_APPROVAL.name().equals(materialsType)) {
            throw new MithrasException("该类文件只能在审批流中由放款审核岗位人员上传");
        }
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        authCheck(paymentBaseInfo);
        materialsService.upload(file, paymentId, materialsType, BusinessModuleEnum.PAYMENT.name());
        return R.ok();
    }

    @Override
    public R<Void> remove(PaymentMaterialsOperateReq req) {
        // 先查询文档信息
        MaterialsList materialsList = materialsListService.getById(req.getId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        // 再查找付款主数据
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(materialsList.getBelongId());
        authCheck(paymentBaseInfo);
        materialsService.remove(req.getId());
        return R.ok();
    }

    private void authCheck(PaymentBaseInfo paymentBaseInfo) {
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(PaymentStatusEnum.CLOSED.name().equals(paymentBaseInfo.getPaymentStatus())){
            throw new MithrasException("该申请已关闭，不允许再修改有关信息");
        }
//        if(PaymentWriteOffStatus.WRITTEN_OFF.name().equals(paymentBaseInfo.getWriteOffStatus())){
//            throw new MithrasException("该申请已核销，不允许再修改有关信息");
//        }
//        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
//        if (!ObjectUtil.equal(contractBaseInfo.getProjSponsorUserId(), AccountUtil.getLoginInfo().getId())) {
//            throw new MithrasException("权限校验失败: 只有项目主办可以操作删除！");
//        }
        boolean hit = sysUserService.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        ProcessResp processResp = paymentService.findRelatedProcess(paymentBaseInfo.getId());
        if (Objects.nonNull(processResp)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
    }

}
