package cn.zswltech.mithras.others.service.third;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.enums.CQPaymentTypeENUM;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.third.retry.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.third.financialshare.application.ExceptionRequestInfoService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.application.orchestration.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.service.financial.req.CQ2PaymentReq;
import cn.zswltech.mithras.third.service.financial.vo.CQ2PaymentVO;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @ClassName FinancialService
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/18 08:36
 * @Version 1.0
 **/
public class FinancialService extends ApplicationTest {
    @Resource
    private ExceptionRequestInfoService exceptionRequestInfoService;

    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;

    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;

    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    //  修改付款银行编号
    @Test
    public void modifyPayment() {
        List<ExceptionRequestInfo> list = exceptionRequestInfoService.list(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                .eq(ExceptionRequestInfo::getPlatform, "CQ2_PAYMENT")
                .notLike(ExceptionRequestInfo::getReqData, "ZR")
                .notLike(ExceptionRequestInfo::getReqData, "DK"));
        list.forEach(exceptionRequestInfo -> {
            List<CQ2PaymentReq> cq2PaymentReqs = JSON.parseArray(exceptionRequestInfo.getReqData(), CQ2PaymentReq.class);
            cq2PaymentReqs.forEach(cq2PaymentReq -> {
                List<CQ2PaymentReq.CQ2PaymentEntry> entrys = cq2PaymentReq.getEntry();
                entrys.forEach(entry -> {
                    entry.setCico_uniquecode(cq2PaymentReq.getCico_paynum_rby());
                });
            });
            exceptionRequestInfo.setReqData(JSON.toJSONString(cq2PaymentReqs));
        });
        exceptionRequestInfoService.updateBatchById(list);
    }

    @Test
    public void cq2PaymentExec() {
        PaymentBaseInfo payment = paymentBaseInfoService.getById(1400L);
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, payment.getId()));
        List<CQ2PaymentVO> cq2PaymentVOS = new ArrayList<>();
        paymentActualDetails.forEach(detail -> {
            cq2PaymentVOS.add(buildPayment(payment, detail));
        });
        //付款单
        financialManagerServiceImpl2.cq2PaymentExec(getBean(CollectionAddEventListener.class).getBizInfo(payment.getContractId()), cq2PaymentVOS);
    }

    private CQ2PaymentVO buildPayment(PaymentBaseInfo baseInfo, PaymentActualDetail paymentActualDetail) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            return null;
        }
        //提供客户（租金往来方）名称
        ContractTenantry contractTenantry = getBean(ContractTenantryService.class).getOne(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, baseInfo.getContractId())
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .last(StringUtil.mysqlLimitOne()));
        Client client = null;
        if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
            client = getBean(ClientService.class).getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
        }
        if (ObjectUtil.isEmpty(client)) {
            client = getBean(ClientService.class).getById(contractBaseInfo.getClientId());
        }
        if (ObjectUtil.isEmpty(client)) {
            return null;
        }
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setCico_payzh_number(paymentActualDetail.getOurAccountNumber());
        //vo.setPayzh_bank_name(paymentActualDetail.getOurAccountBank());
        vo.setApplydate(paymentActualDetail.getPaidInDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        vo.setCico_srcbillno(String.join("-", baseInfo.getPaymentCode(), UUIDUtil.genUuid()));
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_JR001.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(paymentActualDetail.getPaidInAmount()))));
        entry.setE_asstact_name(id2NameService.clientId2NameSingle(baseInfo.getClientId()));
        entry.setCico_pay_bank_number_number(paymentActualDetail.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(paymentActualDetail.getOurAccountBank());
        entry.setE_settlementtype_number(paymentActualDetail.getPaymentMethod());
        //entry.setCico_accountname(paymentActualDetail.getOppositeAccountName());
        entry.setE_asstact_name(client.getClientName());
        entry.setE_asstact(client.getClientCode());
        entry.setCico_uniquecode(paymentActualDetail.getBankDetailNo());
        vo.setEntry(CollectionUtil.toList(entry));
        vo.setCico_paynum_rby(paymentActualDetail.getBankDetailNo());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), id2NameService.clientId2NameSingle(baseInfo.getClientId()),
                    Optional.of(LeaseType.valueOf(contractBaseInfo.getLeaseType())).map(LeaseType::display).orElse(null)));
        } else {
            vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), id2NameService.clientId2NameSingle(baseInfo.getClientId()),
                    Optional.of(ProjectBizType.valueOf(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(null)));
        }
        //项目端付款申请
        vo.setSource(ExceptionSourceENUM.BUSINESS_FLOW.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(baseInfo.getPaymentCode());
        return vo;
    }

}
