package cn.zswltech.mithras.service.controller.payment;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentMaterialsApi;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsOperateReq;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.payment.LendingMaterialType;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.PaymentMaterialsService;
import cn.zswltech.mithras.service.service.payment.PaymentService;
import cn.zswltech.mithras.service.util.FlowUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 14:34
 */
@RestController
public class PaymentMaterialsController implements PaymentMaterialsApi {
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
