package cn.zswltech.mithras.others.service.service.third;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.margin.RecordTypeEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.job.CQFinanceJob;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionOverdueRecordInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.third.financial.FinancialService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName FinancialServiceTest
 * @Description TODO
 * @Author jackerhe
 * @Date 2022/12/26 2:26 下午
 * @Version 1.0
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class FinancialServiceTest {

    @Resource
    private FinancialService financialService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private CQFinanceJob cqFinanceJob;
    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private CollectionAddEventListener collectionAddEventListener;

    /**
     *批量核销收款
     * 1.补充需要忽略的现金流编号
     * 2.选择需核销的现金流类型，开始结束时间
     **/
    @Test
    public void collectionRecode(){
        //忽略现金流编号
        List<String> ignoreFlowCode = Arrays.asList();
        //忽略合同 河南骏化俩合同1013L, 1017L
        List<Long> ignoreContract = Arrays.asList(1013L, 1017L);
        //现金流类型
        List<String> cashFlowTypes = Arrays.asList(CashFlowItemEnum.RENT.name());
        DateTime startTime = DateUtil.parse("2022-12-31 00:00:00", DatePattern.NORM_DATETIME_PATTERN);
        DateTime endTime = DateUtil.parse("2023-02-28 23:59:59", DatePattern.NORM_DATETIME_PATTERN);
        List<String> errorList = new ArrayList<>();
        //查询所有未核销完毕的现金流
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .between(CollectionBaseInfo::getPlanCollectionDate, startTime, endTime)
                .in(CollectionBaseInfo::getCashFlowItem, cashFlowTypes)
                //指定客户是使用
                //.eq(CollectionBaseInfo::getClientId, 222L)
                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                .notIn(ignoreFlowCode.size() > 0 ,CollectionBaseInfo::getCode, ignoreFlowCode)
                .notIn(ignoreContract.size() > 0 ,CollectionBaseInfo::getContractId, ignoreContract)
                );
        //依次拼接核销
        if(ObjectUtil.isEmpty(collectionBaseInfos)){
            throw new MithrasException("无需要核销的期限");
        }
        List<ThirdCollectionRecordREQ> reqs = collectionBaseInfos.stream().map(this::buildReq).collect(Collectors.toList());
        //依次核销
        for(ThirdCollectionRecordREQ req : reqs){
            try {
                financialService.collectionRecode(req);
            } catch (Exception e){
                errorList.add(req.getCollectionCode());
                log.info("核销失败" + e);
            }
        }
        if(ObjectUtil.isNotEmpty(errorList)){
            log.info("已结束。核销失败现金流编号为： {}", errorList);
        }else {
            log.info("全部核销完毕，共{}期, 现金流编号为 : {}", reqs.size(), reqs.stream().map(ThirdCollectionRecordREQ::getCollectionCode).collect(Collectors.toList()));
        }

    }

    private ThirdCollectionRecordREQ buildReq(CollectionBaseInfo base) {
        ThirdCollectionRecordREQ thirdReq;
        thirdReq = new ThirdCollectionRecordREQ();
        thirdReq.setCollectionCode(base.getCode());
        thirdReq.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.display());
        thirdReq.setCollectionDate(base.getPlanCollectionDate());//收款时间为计划收款时间
        thirdReq.setCollectionAmount(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getPlanCollectionAmount(), base.getCollectionAmount()).toString()));
        thirdReq.setPrincipal(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getPrincipal(), LongUtil.null2zero(base.getCollectionPrincipal())).toString()));
        thirdReq.setInterest(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getInterest(), LongUtil.null2zero(base.getCollectionInterest())).toString()));
        thirdReq.setPenaltyInterest(LongUtil.tenThousand2Dollar(NumberUtil.sub(LongUtil.null2zero(base.getPenaltyInterest()), LongUtil.null2zero(LongUtil.null2zero(base.getCollectionPenaltyInterest()) + LongUtil.null2zero(base.getPenaltyInterestDeductionAmount()))).toString()));
        thirdReq.setCashFlowItem(base.getCashFlowItem());
        thirdReq.setOurAccountName("-");
        thirdReq.setOurAccountBank("-");
        thirdReq.setOurAccountNumber("-");
        thirdReq.setInvoiceFlag(1);
        thirdReq.setPknumber(base.getCode() + "1234");
        //加上罚息
        thirdReq.setCollectionAmount(NumberUtil.add(thirdReq.getCollectionAmount(), thirdReq.getPenaltyInterest()));
        return thirdReq;
    }

    /**
     *批量核销付款
     **/
    @Test
    public void paymentRecode(){
        DateTime startTime = DateUtil.parse("2022-12-30 00:00:00", DatePattern.NORM_DATETIME_PATTERN);
        DateTime endTime = DateUtil.parse("2023-02-28 23:59:59", DatePattern.NORM_DATETIME_PATTERN);
        List<String> errorList = new ArrayList<>();
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .ne(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF.name())
                .eq(PaymentBaseInfo::getPaymentProcessStatus, ProcessStatus.APPROVAL_PASS.name())
                .between(PaymentBaseInfo::getApplyPaymentDate, startTime, endTime));
        List<ThirdPaymentDetailREQ> paymentDetailREQS = paymentBaseInfos.stream().map(this::buildThirdPaymentDetailREQ).collect(Collectors.toList());
        for (ThirdPaymentDetailREQ thirdPaymentDetailREQ : paymentDetailREQS) {
            try {
                financialService.paymentRecode(thirdPaymentDetailREQ);
            } catch (Exception e) {
                errorList.add(thirdPaymentDetailREQ.getCollectionCode());
                log.info("付款核销失败", e);
            }
        }
        log.info("核销完毕 共{}期. 现金流编号为{}", paymentDetailREQS.size(), paymentDetailREQS.stream().map(ThirdPaymentDetailREQ::getCollectionCode).collect(Collectors.toList()));
        if(ObjectUtil.isNotEmpty(errorList)){
            log.info("核销失败{}", errorList);
        }

    }

    @Test
    public void fullBRFlowRecord(){
        cqFinanceJob.fullBRFlowRecord();
    }

    @Test
    public void sendWriteOffNotice(){
        cqFinanceJob.sendWriteOffNotice();
    }
    @Test
    public void pp(){
        System.out.println(collectionRecordInfoService.getLastWriteDate(35098L));
    }
    private ThirdPaymentDetailREQ buildThirdPaymentDetailREQ(PaymentBaseInfo baseInfo){
        ThirdPaymentDetailREQ req = new ThirdPaymentDetailREQ();

        List<PaymentActualDetail> actualDetails = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, baseInfo.getId()));
        //已付金额
        Long paidAmount = 0L;
        for (PaymentActualDetail actualDetail : actualDetails) {
            if (WriteOffStatus.WRITTEN_OFF.name().equals(actualDetail.getWriteOffStatus())) {
                paidAmount += actualDetail.getPaidInAmount();
            }
        }
        req.setCashFlowItem("CREDIT_PAYMENT");
        req.setCollectionCode(baseInfo.getPaymentCode());
        req.setPaymentMethod(RecordTypeEnum.WIRE_TRANSFER.display());
        req.setPaidInAmount(LongUtil.tenThousand2Dollar(NumberUtil.sub(baseInfo.getApplyPaymentAmount(), paidAmount).toString()));
        req.setPaidInDate(LocalDate.from(baseInfo.getApplyPaymentDate()));
        req.setOurAccountName("-");
        req.setOurAccountBank("-");
        req.setOurAccountNumber("-");
        req.setOppositeAccountBank("-");
        req.setOppositeAccountName("-");
        req.setOppositeAccountNumber("-");
        req.setPknumber("payment" + baseInfo.getPaymentCode());
        return req;
    }

    @Test
    public void doRerunPenaltyInterest(){
        collectionOverdueRecordInfoService.doRerunPenaltyInterest(ListUtil.toList(21216L));
    }

    @Test
    public void rentChange(){
        collectionAddEventListener.rentChange(1402L, ProcessModelTypeEnum.ContractChangeRepayPlanFlow);
    }
}
