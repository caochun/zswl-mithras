package cn.zswltech.mithras.application.orchestration.payment;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.payment.application.convert.PaymentMaterialsListConverter;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.document.enums.SpecialFileBusinessType;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

import static cn.zswltech.mithras.foundation.enums.common.RecordStatus.TAKE_EFFECT;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 14:49
 */
@Service
public class PaymentMaterialsService {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private PaymentMaterialsListConverter converter;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentBaseInfoService baseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    /**
     * 查询放款材料
     *
     * @param req
     * @return
     */
    public Map<String, List<PaymentMaterialsListRsp>> listLending(PaymentMaterialsListReq req) {
        List<MaterialsList> entitis = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PAYMENT.name())
                .in(MaterialsList::getBelongId, req.getPaymentId())
                .orderByDesc(MaterialsList::getUpdateTime));
        List<PaymentMaterialsListRsp> rspData = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (MaterialsList entity : entitis) {
            userIds.add(entity.getCreateBy());
            PaymentMaterialsListRsp rsp = converter.entityToRsp(entity);
            rsp.setCreateTimestamp(Optional.ofNullable(entity.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
            rspData.add(rsp);
        }
        rspData.sort(new CommonFileSortComparator());
        Map<Long, String> userNames = id2NameService.sysUserId2Name(userIds);
        Map<String, List<PaymentMaterialsListRsp>> res = new HashMap<>();
        for (PaymentMaterialsListRsp rspDatum : rspData) {
            rspDatum.setCreator(userNames.get(rspDatum.getCreateBy()));
            List<PaymentMaterialsListRsp> orDefault = res.getOrDefault(rspDatum.getMaterialsType(), new ArrayList<>());
            orDefault.add(rspDatum);
            res.put(rspDatum.getMaterialsType(), orDefault);
        }
        return res;
    }

    public void uploadPaidProf(MultipartFile file, Long belongId, String materialsType, String businessType) {
        materialsListService.add(file, belongId, materialsType, businessType);
    }

    public void upload(MultipartFile file, Long belongId, String materialsType, String businessType) {
        PaymentBaseInfo payment = baseInfoService.getById(belongId);
//        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(payment.getContractId());
        if (ObjectUtil.isEmpty(payment)) {
            throw new MithrasException("付款申请不存在");
        }
        if (PaymentStatusEnum.CLOSED.name().equals(payment.getPaymentStatus())) {
            throw new MithrasException("已关闭的付款申请不可上传材料");
        }
        if (PaymentStatusEnum.TAKE_EFFECT.name().equals(payment.getPaymentStatus())) {
            throw new MithrasException("生效中的付款申请不可上传材料");
        }
        ProcessResp processResp = this.getRunningProcessNode(payment.getId());
        if (Objects.isNull(processResp)) {
            if (!ProcessStatus.APPROVAL_REJECT.name().equals(payment.getPaymentProcessStatus())
                    && !PaymentStatusEnum.NEW.name().equals(payment.getPaymentStatus())) {
                throw new MithrasException("付款申请当前状态不允许上传文件");
            }
        } else {
            Long currentId = AccountUtil.getLoginInfo().getId();
            String[] userIds = processResp.getCurAssigneeIds().split(",");
            String startUserId = processResp.getStartUserId();
            // 只有流程节点审批人可以操作
            boolean isCurrentUserId = false;
            for (String curAssigneeId : userIds) {
                if (currentId.equals(Long.valueOf(curAssigneeId))) {
                    isCurrentUserId = true;
                    break;
                }
            }
            if (!isCurrentUserId) {
                throw new AuthCheckException("非当前流程节点操作人，上传失败");
            }
            if (!currentId.equals(Long.valueOf(startUserId))) {
                throw new AuthCheckException("非流程发起人节点，上传失败");
            }
        }
        // 确定是否为发起人
        if (Objects.equals(AccountUtil.getLoginInfo().getId(), payment.getCreateBy())) {
            materialsListService.add(file, belongId, materialsType, businessType);
        } else {
            throw new AuthCheckException("只允许发起人上传，其他岗位请在审批流中上传对应文件报告");
        }
    }

    public void remove(Long id) {
        MaterialsList materials = materialsListMapper.selectById(id);
        if (Objects.isNull(materials)) {
            throw new MithrasException("没有找到对应报告文件");
        }
        PaymentBaseInfo payment = baseInfoService.getById(materials.getBelongId());
        if (PaymentStatusEnum.CLOSED.name().equals(payment.getPaymentStatus())) {
            throw new MithrasException("关闭状态不支持删除报告文件");
        }
        if (Objects.equals(PaymentStatusEnum.TAKE_EFFECT.name(), payment.getPaymentStatus())) {
            throw new MithrasException("生效状态不支持删除报告文件");
        }
        ProcessResp processResp = this.getRunningProcessNode(payment.getId());
        if (Objects.isNull(processResp)) {
            // 确认是否发起人
            if (!Objects.equals(AccountUtil.getLoginInfo().getId(), payment.getCreateBy())) {
                throw new AuthCheckException("权限校验失败: 只有付款创建人可以操作删除");
            }
            if (!ProcessStatus.APPROVAL_REJECT.name().equals(payment.getPaymentProcessStatus())
                    && !PaymentStatusEnum.NEW.name().equals(payment.getPaymentStatus())) {
                throw new MithrasException("付款申请当前状态不允许删除");
            }
        } else {
            Long currentId = AccountUtil.getLoginInfo().getId();
            String[] userIds = processResp.getCurAssigneeIds().split(",");
            // 只有流程节点审批人可以操作
            boolean isCurrentUserId = false;
            for (String curAssigneeId : userIds) {
                if (currentId.equals(Long.valueOf(curAssigneeId))) {
                    isCurrentUserId = true;
                    break;
                }
            }
            if (!isCurrentUserId) {
                throw new AuthCheckException("非当前流程节点审批人，删除失败");
            }
            if (!currentId.equals(materials.getCreateBy())) {
                throw new AuthCheckException("只能操作删除自己上传的文件，删除失败");
            }
        }
        materialsListMapper.deleteById(id);
    }

    public void removeByBelongId(Long belongId) {
        materialsListMapper.delete(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBelongId, belongId));
    }

    /**
     * 查询付款记录证明材料
     *
     * @param actualDetailId
     * @return
     */
    public List<PaymentMaterialsListRsp> listProofOfPaid(Long actualDetailId) {
        List<MaterialsList> entities = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, SpecialFileBusinessType.PAID_RECORD.name())
                .in(MaterialsList::getBelongId, actualDetailId)
                .orderByDesc(MaterialsList::getUpdateTime));
        for (MaterialsList entiti : entities) {

        }
        return converter.entitiesToRsp(entities);
    }

    public MaterialsList getById(Long id) {
        return materialsListMapper.selectById(id);
    }

    @Resource
    private FlowTaskApiService flowTaskApiService;

    private ProcessResp getRunningProcessNode(Long paymentId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.PaymentCreateFlow.name()));
        processPageReq.setBusinessKey(paymentId.toString());
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (CollectionUtils.isEmpty(processRespPage.getContents())) {
            return null;
        } else {
            return processRespPage.getContents().get(0);
        }
    }


}
