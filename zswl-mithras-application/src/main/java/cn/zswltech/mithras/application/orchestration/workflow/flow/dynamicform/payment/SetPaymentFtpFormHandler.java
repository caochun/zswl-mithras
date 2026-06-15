package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.payment;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.SetPaymentFtpFormRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.mapper.FtpAssessmentInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/8/31
 * @description 起租/新增借据流程设置付款FTP
 */
@Component
public class SetPaymentFtpFormHandler implements DynamicFormHandler {
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private FtpAssessmentInfoMapper ftpAssessmentInfoMapper;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
//        Long contractId = Long.parseLong(taskResp.getBusinessKey());
//        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByContractIds(Collections.singletonList(contractId));
//        // 去掉没有关联借据id的付款申请
//        paymentBaseInfoList.removeIf(e -> Objects.isNull(e.getReceiptId()));
//        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
//            return;
//        }
////        Map<Long, SetPaymentFtpFormREQ> reqMap;
////        String jsonStr = JSONUtil.toJsonStr(formMap.get(this.getType().name()));
////        if (StrUtil.isNotBlank(jsonStr)) {
////            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
////            List<SetPaymentFtpFormREQ> reqList = JSONUtil.toList(jsonObject.getJSONArray("paymentList"), SetPaymentFtpFormREQ.class);
////            reqMap = reqList.stream().collect(Collectors.toMap(SetPaymentFtpFormREQ::getPaymentId, e -> e));
////        } else {
////            reqMap = Collections.emptyMap();
////        }
////        // 校验审批流中是否已经保存FTP相关数据
//        List<String> msgList = new LinkedList<>();
////        List<PaymentBaseInfo> toUpdateList = new LinkedList<>();
//        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
//            if (Objects.nonNull(paymentBaseInfo.getCashFtpFinal()) && Objects.nonNull(paymentBaseInfo.getBillFtpFinal())) {
//                // 已经固定的FTP不能改变
//                continue;
//            }
///*            SetPaymentFtpFormREQ req = reqMap.get(paymentBaseInfo.getId());
//            if (Objects.isNull(paymentBaseInfo.getCashFtpFinal())) {
//                paymentBaseInfo.setCashFtp(Optional.ofNullable(req).map(SetPaymentFtpFormREQ::getCashFtp).orElse(null));
//            }
//            if (Objects.isNull(paymentBaseInfo.getBillFtpFinal())) {
//                paymentBaseInfo.setBillFtp(Optional.ofNullable(req).map(SetPaymentFtpFormREQ::getBillFtp).orElse(null));
//            }*/
//            if (Objects.isNull(paymentBaseInfo.getCashFtp()) || Objects.isNull(paymentBaseInfo.getBillFtp())) {
//                msgList.add(String.format("付款编号为<%s>的付款申请没有设置资金FTP或票据FTP，请检查", paymentBaseInfo.getPaymentCode()));
//            }
////            } else {
////                toUpdateList.add(paymentBaseInfo);
////            }
//        }
//        if (CollectionUtil.isNotEmpty(msgList)) {
//            throw new MithrasException(CharSequenceUtil.join("\n", msgList));
//        }
////        if (CollectionUtil.isNotEmpty(toUpdateList)) {
////            paymentBaseInfoService.updateBatchById(toUpdateList);
////        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        Long contractId = Long.parseLong(rsp.getBusinessKey());
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getContractId, contractId)
                .ne(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.CLOSED.name()));
        // 去掉没有关联借据id的付款申请
        paymentBaseInfoList.removeIf(e -> Objects.isNull(e.getReceiptId()));
        SetPaymentFtpFormRSP setPaymentFtpFormRSP = new SetPaymentFtpFormRSP();
        setPaymentFtpFormRSP.setContractId(contractId);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            setPaymentFtpFormRSP.setPaymentList(Collections.emptyList());
        } else {
//            // 获取当前最新的相关FTP数据
//            LocalDate now = LocalDate.now();
//            CashFtpInfluenceBO cashFtpInfluenceBO = ftpService.getCashFtpInfluence(contractId, now);
//            cashFtpInfluenceBO.setJudgePledge(true);
//            Set<Long> contractIds = new HashSet<>();
//            contractIds.add(contractId);
//            Integer cashFtp = ftpService.getCashFtp(cashFtpInfluenceBO, contractIds);
//            Integer billFtp = ftpService.getBillFtp(now).getBillFtpSell();
//            List<SetPaymentFtpFormRSP.PaymentData> paymentDataList = new ArrayList<>(paymentBaseInfoList.size());
//            paymentBaseInfoList.forEach(paymentBaseInfo -> {
//                SetPaymentFtpFormRSP.PaymentData paymentData = new SetPaymentFtpFormRSP.PaymentData();
//                paymentData.setPaymentId(paymentBaseInfo.getId());
//                paymentData.setPaymentCode(paymentBaseInfo.getPaymentCode());
//                paymentData.setCashFtp(Objects.nonNull(paymentBaseInfo.getCashFtp()) ? paymentBaseInfo.getCashFtp() : cashFtp);
//                paymentData.setCanEditCashFtp(false);
//                paymentData.setBillFtp(Objects.nonNull(paymentBaseInfo.getBillFtp()) ? paymentBaseInfo.getBillFtp() : billFtp);
//                paymentData.setCanEditBillFtp(false);
//                paymentDataList.add(paymentData);
//            });
            List<SetPaymentFtpFormRSP.PaymentData> paymentDataList = new ArrayList<>(paymentBaseInfoList.size());
            paymentBaseInfoList.forEach(paymentBaseInfo -> {
                List<FtpAssessmentInfo> ftpAssessmentInfoList = ftpAssessmentInfoMapper.selectList(Wrappers.<FtpAssessmentInfo>lambdaQuery()
                        .eq(FtpAssessmentInfo::getReceiptId, paymentBaseInfo.getReceiptId()));
                if (CollectionUtil.isNotEmpty(ftpAssessmentInfoList)) {
                    ftpAssessmentInfoList.removeIf(a -> Objects.equals(a.getIsEffect(), YesOrNoNumberEnum.YES.getCode()));
                }
                if (CollectionUtil.isNotEmpty(ftpAssessmentInfoList)) {
                    FtpAssessmentInfo ftpAssessmentInfo = ftpAssessmentInfoList.get(0);
                    SetPaymentFtpFormRSP.PaymentData paymentData = new SetPaymentFtpFormRSP.PaymentData();
                    paymentData.setPaymentId(paymentBaseInfo.getId());
                    paymentData.setPaymentCode(paymentBaseInfo.getPaymentCode());
                    paymentData.setCashFtp(Objects.nonNull(ftpAssessmentInfo.getAssessmentPrice()) ? Math.toIntExact(ftpAssessmentInfo.getAssessmentPrice()) : null);
                    paymentData.setCanEditCashFtp(false);
                    paymentData.setBillFtp(Objects.nonNull(ftpAssessmentInfo.getTicketPrice()) ? Math.toIntExact(ftpAssessmentInfo.getTicketPrice()) : null);
                    paymentData.setCanEditBillFtp(false);
                    paymentDataList.add(paymentData);
                }
            });
            setPaymentFtpFormRSP.setPaymentList(paymentDataList);
        }
        rsp.getDynamicFormData().put(this.getType().name(), setPaymentFtpFormRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.contract_setPaymentFtp;
    }
}
