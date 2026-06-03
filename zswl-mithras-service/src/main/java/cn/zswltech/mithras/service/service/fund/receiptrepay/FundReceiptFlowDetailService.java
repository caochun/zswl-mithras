package cn.zswltech.mithras.service.service.fund.receiptrepay;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailException;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.service.enums.capital.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.third.enums.CQCollectionTypeENUM;
import cn.zswltech.mithras.third.enums.CQPaymentMethodENUM;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2CollectionVO;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/6/3
 * @description
 */
@Slf4j
@Service
public class FundReceiptFlowDetailService extends ServiceImpl<FundReceiptFlowDetailMapper, FundReceiptFlowDetail> {

    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private FundReceiptRepayBorrowingService fundReceiptRepayBorrowingService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundReceiptRepayCashDepositService fundReceiptRepayCashDepositService;
    @Resource
    private FundReceiptRepayExpenseService fundReceiptRepayExpenseService;
    @Resource
    private FundFinancingService fundFinancingService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundCreditService fundCreditService;

    public List<FundReceiptFlowDetail> listByBankFlowIds(Collection<Long> bankFlowIds) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.in(FundReceiptFlowDetail::getFinanceFlowId, bankFlowIds);
        return this.list(query);
    }

    public List<FundReceiptFlowDetail> listByReceiptRepayId(Long receiptRepayId) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayId);
        return this.list(query);
    }


    /**
     * 仅获取还本付息
     * @param receiptRepayIdList
     * @return key=现金流编号
     */
    public Map<String, List<FundReceiptFlowDetail>> listByReceiptRepayIds(List<Long> receiptRepayIdList) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.in(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayIdList);
        query.eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name());
        List<FundReceiptFlowDetail> flowDetailList = this.list(query);
        if(CollectionUtil.isNotEmpty(flowDetailList)){
            return flowDetailList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        }
        return Collections.emptyMap();
    }


    public Map<Long, List<FundReceiptFlowDetail>> listByReceiptRepayIds(Collection<Long> receiptRepayIdList) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.in(CollectionUtil.isNotEmpty(receiptRepayIdList), FundReceiptFlowDetail::getReceiptRepayId, receiptRepayIdList);
        List<FundReceiptFlowDetail> list = this.list(query);
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getReceiptRepayId));
    }

    public Long sum(Long receiptRepayId, String cashFlowCode) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayId);
        query.eq(FundReceiptFlowDetail::getCashFlowCode, cashFlowCode);
        List<FundReceiptFlowDetail> fundReceiptFlowDetailList = this.list(query);
        if (CollectionUtil.isEmpty(fundReceiptFlowDetailList)) {
            return 0L;
        }
        return fundReceiptFlowDetailList.stream().mapToLong(FundReceiptFlowDetail::getTotalAmount).sum();
    }

    public FundReceiptFlowDetail getByCode(String code) {
        return this.getOne(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                .eq(FundReceiptFlowDetail::getCashFlowCode, code)
                .last(StringUtil.mysqlLimitOne()));
    }

    public List<FundReceiptFlowDetail> listByCashFlowCodes(Collection<String> cashFlowCodes) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.in(FundReceiptFlowDetail::getCashFlowCode, cashFlowCodes);
        query.eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name());
        return this.list(query);
    }

    public List<FundReceiptFlowDetail> listByCashFlowCodesNotOnlyRepay(Collection<String> cashFlowCodes) {
        LambdaQueryWrapper<FundReceiptFlowDetail> query = Wrappers.lambdaQuery();
        query.in(FundReceiptFlowDetail::getCashFlowCode, cashFlowCodes);
        return this.list(query);
    }

    //反核销付款数据
    @Transactional(rollbackFor = Throwable.class)
    public void withdraw(Long id) {
        FundReceiptFlowDetail fundReceiptFlowDetail = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(fundReceiptFlowDetail)) {
            return;
        }
        LambdaUpdateWrapper<FundReceiptFlowDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FundReceiptFlowDetail::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(FundReceiptFlowDetail::getId, fundReceiptFlowDetail.getId());
        baseMapper.update(null, updateWrapper);
        withdrawReleaseCreditLimit(fundReceiptFlowDetail);
        //这里需要处理不同现金流的基本表
        FinanceCashFlowItemEnum of = Optional.ofNullable(FinanceCashFlowItemEnum.of(fundReceiptFlowDetail.getCashFlowItem())).orElseThrow(() -> new MailException(ResultMsg.UNSUPPORT_TYPE));
        switch (of) {
            case FINANCE_FUND:
                fundReceiptRepayBorrowingService.doWriteOffStateModify(fundReceiptFlowDetail.getReceiptRepayId(), fundReceiptFlowDetail.getCashFlowCode());
                break;
            case REPAY:
                fundReceiptRepayCashFlowService.doWriteOffStateModify(fundReceiptFlowDetail.getReceiptRepayId(), fundReceiptFlowDetail.getCashFlowCode());
                break;
            case DEPOSIT_PAYMENT:
            case DEPOSIT_RETURN:
                fundReceiptRepayCashDepositService.doWriteOffStateModify(fundReceiptFlowDetail.getReceiptRepayId(), fundReceiptFlowDetail.getCashFlowCode());
                break;
            default:
                fundReceiptRepayExpenseService.doWriteOffStateModify(fundReceiptFlowDetail.getReceiptRepayId(), fundReceiptFlowDetail.getCashFlowCode());
        }
        //回退银行流水
        financeFlowRecordService.withdrawBankFlow(fundReceiptFlowDetail.getId(), FinanceFlowDetailTableEnum.FUND_RECEIPT_FLOW_DETAIL.name(), fundReceiptFlowDetail.getTotalAmount());
    }

    private void withdrawReleaseCreditLimit(FundReceiptFlowDetail fundReceiptFlowDetail) {
        // 校验是否为核销本金
        if(!Objects.equals(FinanceCashFlowItemEnum.REPAY.name(),fundReceiptFlowDetail.getCashFlowItem()) || fundReceiptFlowDetail.getPrincipalAmount() == null || fundReceiptFlowDetail.getPrincipalAmount() == 0L) {
            return;
        }
        // 校验是否为间融
        FundReceiptRepayBaseInfo receiptRepayBaseInfo = fundReceiptRepayBaseInfoService.getById(fundReceiptFlowDetail.getReceiptRepayId());
        if(receiptRepayBaseInfo == null || receiptRepayBaseInfo.getFinancingType() != null){
            return;
        }
        // 校验是否为循环授信
        List<FundFinancingCreditRef> refList = financingCreditRefService.queryByFinancingId(receiptRepayBaseInfo.getFinancingId());
        List<FundCredit> creditList = fundCreditService.listByIds(refList.stream().map(FundFinancingCreditRef::getCreditId).collect(Collectors.toList()));
        if(creditList.stream().map(FundCredit::getRecyclable).collect(Collectors.toList()).contains(YesOrNoNumberEnum.NO.getCode())){
            return;
        }

        FundFinancingBaseInfo financingBaseInfo = fundFinancingBaseInfoService.getById(receiptRepayBaseInfo.getFinancingId());
        fundFinancingService.occupyCredit(financingBaseInfo, fundReceiptFlowDetail.getPrincipalAmount());
    }

    //收款单
    public List<CQ2CollectionVO> getCollectionVO(List<Long> ids){
        if(ObjectUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        //这里只要收款
        List<FundReceiptFlowDetail> fundReceiptFlowDetails = baseMapper.selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                .in(FundReceiptFlowDetail::getId, ids)
                .in(FundReceiptFlowDetail::getCashFlowItem, ListUtil.toList(FinanceCashFlowItemEnum.FINANCE_FUND.name(), FinanceCashFlowItemEnum.DEPOSIT_RETURN.name()))
                .isNotNull(FundReceiptFlowDetail::getBankFlowNo));
        if (ObjectUtil.isEmpty(fundReceiptFlowDetails)) {
            return Collections.emptyList();
        }
        List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = fundReceiptRepayBaseInfoService.listByIds(fundReceiptFlowDetails.stream().map(FundReceiptFlowDetail::getReceiptRepayId).collect(Collectors.toList()));
        if (ObjectUtil.isEmpty(receiptRepayBaseInfos)) {
            return Collections.emptyList();
        }
        List<Long> directIds = receiptRepayBaseInfos.stream().filter(e -> "DIRECT".equals(e.getFinancingType())).map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
        List<Long> financingIds = receiptRepayBaseInfos.stream().filter(e -> !"DIRECT".equals(e.getFinancingType())).map(FundReceiptRepayBaseInfo::getFinancingId).collect(Collectors.toList());
        List<Long> orgIds = new ArrayList<>();
        List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfos = null;
        if (CollectionUtil.isNotEmpty(directIds)) {
            fundDirectFinancingBaseInfos = fundDirectFinancingBaseInfoService.listByIds(directIds);
        }
        Map<Long, FundDirectFinancingBaseInfo> directBaseMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(fundDirectFinancingBaseInfos)) {
            directBaseMap = fundDirectFinancingBaseInfos.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
            orgIds.addAll(fundDirectFinancingBaseInfos.stream().map(FundDirectFinancingBaseInfo::getDeptId).collect(Collectors.toList()));
        }
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = null;
        if (CollectionUtil.isNotEmpty(financingIds)) {
            fundFinancingBaseInfos = fundFinancingBaseInfoService.listByIds(financingIds);
        }
        Map<Long, FundFinancingBaseInfo> fundFinancingMap = new HashMap<>();
        if(ObjectUtil.isNotEmpty(fundFinancingBaseInfos)) {
            fundFinancingMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
            orgIds.addAll(fundFinancingBaseInfos.stream().map(FundFinancingBaseInfo::getDeptId).collect(Collectors.toList()));
        }
        Map<Long, OrgDO> orgMap = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(orgIds, null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
        List<CQ2CollectionVO> rsps = new ArrayList<>();
        Map<Long, FundReceiptRepayBaseInfo> baseInfoId2Bean = receiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, e -> e, (a, b) -> a));
        Map<Long, List<FundReceiptFlowDetail>> collectionId2RecordMap = fundReceiptFlowDetails.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getReceiptRepayId));
        //收款维度
        for (Map.Entry<Long, List<FundReceiptFlowDetail>> entry : collectionId2RecordMap.entrySet()) {
            Long receiptRepayId = entry.getKey();
            List<FundReceiptFlowDetail> recordInfos = entry.getValue();
            FundReceiptRepayBaseInfo baseInfo = baseInfoId2Bean.get(receiptRepayId);
            if (ObjectUtil.isNotEmpty(baseInfo)) {
                OrgDO orgDO = null;
                String timeLimitType = null;
                if ("DIRECT".equals(baseInfo.getFinancingType())) {
                    FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = directBaseMap.get(baseInfo.getFinancingId());
                    if (ObjectUtil.isNotEmpty(fundDirectFinancingBaseInfo)) {
                        orgDO = orgMap.get(fundDirectFinancingBaseInfo.getDeptId());
                    }
                } else {
                    FundFinancingBaseInfo baseInfo1 = fundFinancingMap.get(baseInfo.getFinancingId());
                    if (ObjectUtil.isNotEmpty(baseInfo1)) {
                        timeLimitType = baseInfo1.getTimeLimitType();
                        orgDO = orgMap.get(baseInfo1.getDeptId());
                    }
                }
                rsps.add(buildCollection(baseInfo, recordInfos, recordInfos.get(0).getBankFlowNo(), orgDO != null ? String.valueOf(orgDO.getMainOrgId()) : null, timeLimitType));
            }
        }
        return rsps;
    }

    private CQ2CollectionVO buildCollection(FundReceiptRepayBaseInfo baseInfo, List<FundReceiptFlowDetail> recordInfos, String flowId, String orgCode, String timeLimitType){
        CQ2CollectionVO vo = new CQ2CollectionVO();
        vo.setBizdate(recordInfos.get(recordInfos.size() - 1).getCashFlowDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        vo.setPayertype(FinancialConstants.OTHER);
        //vo.setItempayertype(vo.getPayertype());//同 payertype
        vo.setCico_srcbillno(String.join("-", baseInfo.getReceiptRepayCode(), UUIDUtil.genUuid()));

        vo.setPayername(FinancialConstants.RZZL_NAME);
        vo.setPayernumber(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
        vo.setCico_srcsystem(FinancialConstants.RZY);
        //FinanceCashFlowItemEnum of = FinanceCashFlowItemEnum.of(recordInfos.get(0).getCashFlowItem());
        vo.setTxt_description(String.format("收到%s客户%s", FinancialConstants.RZZL_NAME, Optional.ofNullable(FinanceCashFlowItemEnum.of(recordInfos.get(0).getCashFlowItem())).map(FinanceCashFlowItemEnum::getDisplay).orElse(recordInfos.get(0).getCashFlowItem())));
        vo.setCico_relateddepartments_number(orgCode);//关联部门

        List<CQ2CollectionVO.CQ2CollectionVOBody> bodys = new ArrayList<>();
        recordInfos.forEach(recordInfo -> {
            CQ2CollectionVO.CQ2CollectionVOBody body = vo.new CQ2CollectionVOBody();
            if (FinanceCashFlowItemEnum.REPAY.name().equals(recordInfo.getCashFlowItem())) {
                //本金
                vo.setReceivingtype_number(Optional.ofNullable(CQCollectionTypeENUM.getCQTypeByCapital(timeLimitType,true)).map(CQCollectionTypeENUM::getCode).orElse(null));
                body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getPrincipalAmount()))));
                vo.setCico_srcbillno(String.join(baseInfo.getReceiptRepayCode() + "bj", UUIDUtil.genUuid()));
            } else {
                //其他
                vo.setReceivingtype_number(Optional.ofNullable(CQCollectionTypeENUM.getCQTypeByCapital(timeLimitType,true)).map(CQCollectionTypeENUM::getCode).orElse(null));
                body.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getTotalAmount()))));
            }
            body.setE_actamt(body.getE_receivableamt());
            body.setCico_customerfield_number(FinancialConstants.BD_SUPPLIER_RZZL_CODE);
            body.setE_fundflowitem_number(Optional.ofNullable(CQCollectionTypeENUM.getCQTypeByCapital(timeLimitType,true)).map(CQCollectionTypeENUM::getChannelCode).orElse(null));
            body.setCico_purposeoffunds_number(body.getE_fundflowitem_number());
            vo.setSettletype_number(Optional.ofNullable(CQPaymentMethodENUM.ofDisplay(recordInfo.getSettleMethod())).map(CQPaymentMethodENUM::name).orElse(recordInfo.getSettleMethod()));//结算方式
            if (ObjectUtil.isNotEmpty(body.getE_receivableamt()) && body.getE_receivableamt().compareTo(BigDecimal.ZERO) != 0) {
                bodys.add(body);
            }
            //添加利息
            if (CashFlowItemEnum.RENT.name().equals(recordInfo.getCashFlowItem())) {
                CQ2CollectionVO.CQ2CollectionVOBody body1 = BeanUtil.copyProperties(body, CQ2CollectionVO.CQ2CollectionVOBody.class);
                body1.setE_receivableamt(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(recordInfo.getInterestAmount()))));
                body1.setE_actamt(body1.getE_receivableamt());
                CQCollectionTypeENUM cqTypeByBusiness = CQCollectionTypeENUM.getCQTypeByCapital(timeLimitType, false);
                if (cqTypeByBusiness != null) {
                    body1.setE_fundflowitem_number(cqTypeByBusiness.getChannelCode());
                    body1.setCico_purposeoffunds_number(cqTypeByBusiness.getChannelCode());
                }
                if (ObjectUtil.isNotEmpty(body1.getE_receivableamt()) && body1.getE_receivableamt().compareTo(BigDecimal.ZERO) != 0) {
                    bodys.add(body1);
                }
            }
            if (ObjectUtil.isNotEmpty(recordInfo.getOurAccountNumber())) {
                vo.setAccountbank_number(recordInfo.getOurAccountNumber());
            }
        });
        vo.setEntry(bodys);
        vo.setActrecamt(bodys.stream().map(CQ2CollectionVO.CQ2CollectionVOBody::getE_receivableamt).reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setLocalamt(vo.getActrecamt());
        vo.setSourcebillnumber(flowId);
        return vo;
    }
}
