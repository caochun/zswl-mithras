package cn.zswltech.mithras.margin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.dto.margin.*;
import cn.zswltech.mithras.margin.application.MarginRecordApplicationService;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.margin.enums.MarginWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.financialshare.enums.CQBusinessTypeENUM;
import cn.zswltech.mithras.third.financialshare.enums.CQPaymentTypeENUM;
import cn.zswltech.mithras.third.financialshare.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginWriteOffRecordMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.margin.application.port.MarginRecordSupportPort;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginWriteOffRecord;
import cn.zswltech.mithras.margin.application.port.model.MarginCollectionRecordInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @create: 2022-08-18
 **/
@Slf4j
@Service
public class MarginRecordService extends ServiceImpl<MarginRecordInfoMapper, MarginRecordInfo> implements MarginRecordApplicationService {

    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private MarginRecordInfoMapper marginRecordInfoMapper;
    @Resource
    private MarginWriteOffRecordMapper marginWriteOffRecordMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MarginRecordSupportPort marginRecordSupportPort;

    public long calculateBalanceBeforeTargetDate(Long contractId, LocalDate targetDate) {
        // 找到保证金主表
        MarginBaseInfo marginBaseInfo = SpringUtil.getBean(MarginBaseInfoService.class).getOne(
                Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, contractId).last(StringUtil.mysqlLimitOne())
        );
        if (Objects.isNull(marginBaseInfo)) {
            return 0L;
        }
        // 找到保证金余额
        List<MarginRecordInfo> marginRecordInfoList = this.list(
                Wrappers.<MarginRecordInfo>lambdaQuery()
                        .eq(MarginRecordInfo::getMarginId, marginBaseInfo.getId())
                        .lt(MarginRecordInfo::getCollectionDate, targetDate)
        );
        long collect = marginRecordInfoList.stream().filter(e -> Objects.equals(e.getRecordType(), RecordTypeEnum.COLLECTION.name())).mapToLong(MarginRecordInfo::getCollectionAmount).sum();
        long refund = marginRecordInfoList.stream().filter(e -> Objects.equals(e.getRecordType(), RecordTypeEnum.REFUND.name())).mapToLong(MarginRecordInfo::getCollectionAmount).sum();
        long balance = collect - refund;
        return balance < 0 ? 0 : balance;
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public String add(MarginRecordInfo info) {
        info.setWriteOff(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
        info.setReview(MarginWriteOffStatusEnum.TO_BE_REVIEW.name());
        info.setWriteOffStatus(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
        Integer count = marginRecordInfoMapper.selectCount(Wrappers.<MarginRecordInfo>lambdaQuery()
                .eq(MarginRecordInfo::getMarginId, info.getMarginId()).eq(MarginRecordInfo::getRecordType, info.getRecordType()));
        info.setSortId(String.valueOf(count + 1));
        marginRecordInfoMapper.insert(info);
        return null;
    }

    //    @Transactional(rollbackFor = Throwable.class)
//    public R<String> update(MarginRecordUpdateREQ req){
//        MarginRecordInfo recordInfo = marginRecordInfoMapper.selectById(req.getId());
//        if (MarginWriteOffStatusEnum.IGNORE.name().equals(recordInfo.getWriteOffStatus()) || MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(recordInfo.getWriteOffStatus() )){
//            return R.fail("核销完毕或忽略状态，不允许操作！");
//        }
////        MarginWriteOffRecord writeOffRecord = new MarginWriteOffRecord();
//        AccountVO loginInfo = AccountUtil.getLoginInfo();
//        if (Strings.isNotEmpty(req.getWriteOff())){
//            List<UserDO> user = userDOMapper.queryLikeJobs(JobEnum.financialmanager.name(), Collections.singletonList(loginInfo.getId()));
//            if (CollectionUtil.isEmpty(user)){
//                return R.fail("财务经理才能进行核销操作！");
//            }
//            if (req.getWriteOff().equals(recordInfo.getWriteOff())){
//                return R.ok();
//            }
//            recordInfo.setWriteOff(req.getWriteOff());
////            writeOffRecord.setOperate(Optional.ofNullable(req.getWriteOff()).map(MarginWriteOffStatusEnum::of).map(MarginWriteOffStatusEnum::display).orElse(null));
//            if (req.getWriteOff().equals(MarginWriteOffStatusEnum.WRITTEN_OFF.name()) || req.getWriteOff().equals(MarginWriteOffStatusEnum.IGNORE.name())) {
//                recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.TO_BE_REVIEW.name());
//                recordInfo.setWriteOffUser(loginInfo.getId());
//            }else if(req.getWriteOff().equals(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name()) ){
//                recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
//                recordInfo.setWriteOffUser(null);
//            }
//        }
//        MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectById(recordInfo.getMarginId());
////        writeOffRecord.setMarginId(marginBaseInfo.getId());
////        writeOffRecord.setRecordId(req.getId());
//        long marginAmount = marginBaseInfo.getCollectionAmount();
//        if (Strings.isNotEmpty(req.getReview())){
//            if (recordInfo.getWriteOffUser() == null){
//                return R.fail("请先核销！");
//            }
//            List<UserDO> user = userDOMapper.queryLikeJobs(JobEnum.cashier.name(), Collections.singletonList(loginInfo.getId()));
//            if (CollectionUtil.isEmpty(user)){
//                return R.fail("出纳才能进行复核操作！");
//            }
//            if (req.getReview().equals(recordInfo.getReview())){
//                return R.ok();
//            }
//            recordInfo.setReview(req.getReview());
////            writeOffRecord.setOperate(Optional.ofNullable(req.getReview()).map(MarginWriteOffStatusEnum::of).map(MarginWriteOffStatusEnum::display).orElse(null));
//            if (req.getReview().equals(MarginWriteOffStatusEnum.REVIEWED.name())) {
//                if (recordInfo.getWriteOff().equals(MarginWriteOffStatusEnum.WRITTEN_OFF.name())) {
//                    recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
//                    recordInfo.setCollectionDate(LocalDate.now());
//                    recordInfo.setReviewUser(loginInfo.getId());
//                    Long collectionAmount = LongUtil.null2zero(recordInfo.getCollectionAmount());
//                    if (RecordTypeEnum.COLLECTION.name().equals(recordInfo.getRecordType())){
//                        marginAmount = marginAmount + collectionAmount;
//                        marginBaseInfo.setCollectionAmount(marginAmount);
//                        marginBaseInfo.setCollectionDate(recordInfo.getCollectionDate());
//                    }else if(RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN.name().equals(recordInfo.getCollectionType())){
//                        marginAmount = marginAmount - collectionAmount;
//                        marginBaseInfo.setCollectionAmount(marginAmount);
//                        marginBaseInfo.setBackAmount(LongUtil.null2zero(marginBaseInfo.getBackAmount())+collectionAmount);
//                    }else if(RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN_DEDUCT.name().equals(recordInfo.getCollectionType())){
//                        marginAmount = marginAmount - collectionAmount;
//                        marginBaseInfo.setCollectionAmount(marginAmount);
//                        marginBaseInfo.setDeductAmount(LongUtil.null2zero(marginBaseInfo.getDeductAmount())+collectionAmount);
//                    }
//                    if (marginBaseInfo.getCollectionAmount() < 0){
//                        R.fail("退款或抵扣金额不可大于保证金余额！");
//                    }
//                    if (recordInfo.getCollectionType().equals(RecordTypeEnum.REFUND_MARGIN_DEDUCT.name())) {
//                        CollectionRecordInfo info = new CollectionRecordInfo();
//                        info.setCollectionId(recordInfo.getCollectionId());
//                        info.setDataSource(String.valueOf(marginBaseInfo.getId()));
//                        info.setCollectionDate(recordInfo.getCollectionDate());
//                        info.setCollectionType(RecordTypeEnum.REFUND_MARGIN_DEDUCT.name());
//                        info.setPostscript(recordInfo.getPostscript());
//                        info.setCollectionAmount(recordInfo.getCollectionAmount());
//                        info.setWriteOffStatus(CollectionRecordWriteOffStatus.WRITTEN_OFF.name());
//                        info.setPrincipal(recordInfo.getDeductPrincipal());
//                        info.setInterest(recordInfo.getDeductInterest());
//                        info.setPenaltyInterest(recordInfo.getDeductPenaltyInterest());
//                        Integer count = collectionRecordInfoMapper.selectCount(Wrappers.<CollectionRecordInfo>lambdaQuery()
//                                .eq(CollectionRecordInfo::getCollectionId, info.getCollectionId()));
//                        info.setSortId(count + 1);
//                        collectionRecordInfoMapper.insert(info);
//                        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(recordInfo.getCollectionId());
//                        collectionRecordInfoService.writeOffRecord(info,baseInfo);
//                        collectionBaseInfoMapper.updateById(baseInfo);
//                    }
//                }else {
//                    return R.fail("请先联系财务经理核销！");
//                }
//            }else if (req.getReview().equals(MarginWriteOffStatusEnum.IGNORE.name())) {
//                if (recordInfo.getWriteOff().equals(MarginWriteOffStatusEnum.IGNORE.name())){
//                    recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.IGNORE.name());
//                    recordInfo.setReviewUser(loginInfo.getId());
//                }else {
//                    return R.fail("请先联系财务经理忽略核销！");
//                }
//            }
//        }
//        marginRecordInfoMapper.updateById(recordInfo);
//        if (MarginWriteOffStatusEnum.REVIEWED.name().equals(req.getReview())){
//            marginBaseInfoMapper.updateById(marginBaseInfo);
//            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(marginBaseInfo.getContractId());
//            if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
//                Integer count = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, marginBaseInfo.getContractId()).ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED));
//                if (count == 0 && marginBaseInfo.getCollectionAmount() <= 0) {
//                    log.info("保证金核销完毕，通知合同执行结清操作[contractId: {}]", marginBaseInfo.getContractId());
//                    contractBaseInfoService.contractSettle(marginBaseInfo.getContractId());
//                }
//            }
////            writeOffRecord.setOperateInfo(String.valueOf(marginBaseInfo.getMarginCode()));
//            }
////        else {
////            writeOffRecord.setOperateInfo(String.valueOf(recordInfo.getSortId()));
////        }
////        writeOffRecord.setMarginAmount(marginAmount);
////        marginWriteOffRecordMapper.insert(writeOffRecord);
//        return R.ok();
//    }
    @Transactional(rollbackFor = Throwable.class)
    public void financialAdd(MarginRecordInfo info) {
        int count = marginRecordInfoMapper.selectCount(Wrappers.<MarginRecordInfo>lambdaQuery()
                .eq(MarginRecordInfo::getMarginId, info.getMarginId()).eq(MarginRecordInfo::getRecordType, info.getRecordType()));
        info.setSortId(String.valueOf(count + 1));
        marginRecordInfoMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void financialWriteOff(MarginRecordInfo recordInfo, String collectionCode, Long paidInAmount, LocalDate paidInDate) {
        MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectById(recordInfo.getMarginId());
        MarginWriteOffRecord writeOffRecord = new MarginWriteOffRecord();
        writeOffRecord.setMarginId(marginBaseInfo.getId());
        writeOffRecord.setRecordId(recordInfo.getId());
        //todo 这里怎么会空
        long marginAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount());
        writeOffRecord.setOperate(recordInfo.getWriteOffStatus());
        Long collectionAmount = LongUtil.null2zero(recordInfo.getCollectionAmount());
        if (RecordTypeEnum.COLLECTION.name().equals(recordInfo.getRecordType())) {
            marginAmount = marginAmount + collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setCollectionDate(recordInfo.getCollectionDate());
        } else if (RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN.name().equals(recordInfo.getCollectionType())) {
            marginAmount = marginAmount - collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setBackAmount(LongUtil.null2zero(marginBaseInfo.getBackAmount()) + collectionAmount);
        } else if (RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN_DEDUCT.name().equals(recordInfo.getCollectionType())) {
            marginAmount = marginAmount - collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setDeductAmount(LongUtil.null2zero(marginBaseInfo.getDeductAmount()) + collectionAmount);
        }
        marginBaseInfoMapper.updateById(marginBaseInfo);
        //ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(marginBaseInfo.getContractId());
        //提前结清不是起租状态了
        //if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
        int count = marginRecordSupportPort.countUnfinishedCollectionsByContractId(marginBaseInfo.getContractId());
        if (marginBaseInfo.getCollectionAmount() <= 0) {
            //保证金核销完成，通知苍穹
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    List<MarginRecordInfo> marginRecordInfos = marginRecordInfoMapper.selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                            .eq(MarginRecordInfo::getMarginId, marginBaseInfo.getId())
                            .orderByAsc(MarginRecordInfo::getCollectionDate));
                    if (ObjectUtil.isNotEmpty(marginRecordInfos)) {
                        List<CQ2PaymentVO> vos = new ArrayList<>();
                        marginRecordInfos.stream().filter(e -> StrUtil.equalsAny(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN.name(), RecordTypeEnum.REFUND.name())).forEach(recordInfo -> vos.add(buildPayment(marginBaseInfo, recordInfo)));
                        marginRecordSupportPort.pushMarginRefundPayments(marginBaseInfo.getContractId(), vos);
                    }
                }
            });
        }
        if (count == 0 && marginBaseInfo.getCollectionAmount() <= 0) {
            log.info("保证金核销完毕，通知合同执行结清操作[contractId: {}]", marginBaseInfo.getContractId());
            if (marginRecordSupportPort.canSettleContractAfterMarginCompleted(marginBaseInfo.getContractId())) {
                marginRecordSupportPort.contractSettle(marginBaseInfo.getContractId());
            }
        }
        //}
        writeOffRecord.setOperateInfo(String.valueOf(marginBaseInfo.getMarginCode()));
        writeOffRecord.setMarginAmount(marginAmount);
        marginWriteOffRecordMapper.insert(writeOffRecord);

        if (isNotNull(collectionCode)) {
            marginRecordSupportPort.listReceivedRentCollections(collectionCode)
                    .stream()
                    .findFirst()
                    .ifPresent(info -> marginRecordSupportPort.sendRentReceivedMessage(info, paidInAmount, paidInDate));
        }
    }

    //反核销付款数据
    @Transactional(rollbackFor = Throwable.class)
    public void withdraw(Long id) {
        MarginRecordInfo marginRecordInfo = baseMapper.selectById(id);
        if(ObjectUtil.isEmpty(marginRecordInfo)) {
            return;
        }
        LambdaUpdateWrapper<MarginRecordInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(MarginRecordInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(MarginRecordInfo::getId, marginRecordInfo.getId());
        baseMapper.update(null, updateWrapper);
        MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectById(marginRecordInfo.getMarginId());
        //todo 这里怎么会空
        long marginAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount());
        Long collectionAmount = LongUtil.null2zero(marginRecordInfo.getCollectionAmount());
        if (RecordTypeEnum.COLLECTION.name().equals(marginRecordInfo.getRecordType())) {
            marginAmount = marginAmount - collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setCollectionDate(marginRecordInfo.getCollectionDate());
        } else if (RecordTypeEnum.REFUND.name().equals(marginRecordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN.name().equals(marginRecordInfo.getCollectionType())) {
            marginAmount = marginAmount + collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setBackAmount(LongUtil.null2zero(marginBaseInfo.getBackAmount()) + collectionAmount);
        } else if (RecordTypeEnum.REFUND.name().equals(marginRecordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN_DEDUCT.name().equals(marginRecordInfo.getCollectionType())) {
            marginAmount = marginAmount + collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setDeductAmount(LongUtil.null2zero(marginBaseInfo.getDeductAmount()) + collectionAmount);
        }
        marginBaseInfoMapper.updateById(marginBaseInfo);
        marginRecordSupportPort.withdrawBankFlow(marginRecordInfo.getId(), "MARGIN_RECORD_INFO", marginRecordInfo.getCollectionAmount());
    }

    private CQ2PaymentVO buildPayment(MarginBaseInfo baseInfo, MarginRecordInfo detail){
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(baseInfo.getContractId());
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            return null;
        }
        Client client = clientMapper.selectById(contractBaseInfo.getClientId());
        if(ObjectUtil.isEmpty(client)){
            return null;
        }
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = null;
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();//051售后回租
            } else if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.FINANCE_LEASING.getCode();//048融资租赁
            }
        }
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setCico_payzh_number(detail.getOurAccountNumber());
        //vo.setPayzh_bank_name(detail.getOurAccountBank());
        vo.setApplydate(detail.getCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        vo.setCico_srcbillno(String.join("-", baseInfo.getMarginCode(), UUIDUtil.genUuid()));
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK05_004.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(detail.getCollectionAmount()))));
        entry.setE_asstact_name(client.getClientName());
        entry.setCico_pay_bank_number_number(detail.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(detail.getOurAccountBank());
        entry.setE_settlementtype_number(detail.getCollectionType());
        entry.setE_asstact(client.getClientCode());
        entry.setCico_businesstype_number(leaseTypeCode);
        vo.setEntry(CollectionUtil.toList(entry));
        vo.setCico_paynum_rby(detail.getBankDetailNo());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), client.getClientName(),
                Optional.of(ProjectBizType.valueOf(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(null)));
        //这里是项目端保证金推款或者抵扣
        vo.setSource(ExceptionSourceENUM.BUSINESS_MARGIN.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(baseInfo.getMarginCode());
        return vo;
    }


    @Override
    public List<MarginRecordListRSP> list(MarginRecordListREQ req) {
        marginRecordSupportPort.checkMarginView(req.getId());
        List<MarginRecordInfo> infoList;
        if (RecordTypeEnum.COLLECTION.name().equals(req.getRecordType())) {
            MarginBaseInfo info = marginBaseInfoMapper.selectById(req.getId());
            List<MarginCollectionRecordInfo> collectionBaseInfos = marginRecordSupportPort.listEarnestMoneyCollectionsByContractId(info.getContractId());
            infoList = new LinkedList<>();
            for (MarginCollectionRecordInfo collectionBaseInfo : collectionBaseInfos) {
                MarginRecordInfo recordInfo = new MarginRecordInfo();
                collectionInfo2marginRecordInfo(collectionBaseInfo, recordInfo);
                infoList.add(recordInfo);
            }
        } else {
            infoList = marginRecordInfoMapper.selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                    .eq(MarginRecordInfo::getMarginId, req.getId())
                    .eq(MarginRecordInfo::getRecordType, req.getRecordType())
                    .orderByAsc(MarginRecordInfo::getSortId));
        }
        List<MarginRecordListRSP> rsps = new LinkedList<>();
        //现金流编号转合同编号
        Map<String, String> rentCode2ContractCode = new HashMap<>();
        Set<Long> collectIdSet = infoList.stream().map(MarginRecordInfo::getCollectionId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<MarginCollectionRecordInfo> collectionBaseInfos = null;
        if (ObjectUtil.isNotEmpty(collectIdSet)) {
            collectionBaseInfos = marginRecordSupportPort.listCollectionsByIds(collectIdSet);
            if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
                rentCode2ContractCode.putAll(collectionBaseInfos.stream().collect(java.util.stream.Collectors.toMap(MarginCollectionRecordInfo::getCode, MarginCollectionRecordInfo::getContractCode)));
            }
        }
        //查询应收日期
        Map<Long, MarginCollectionRecordInfo> collectionId2Bean = new HashMap<>();
        if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
            collectionId2Bean = collectionBaseInfos.stream().collect(java.util.stream.Collectors.toMap(MarginCollectionRecordInfo::getId, e -> e, (a, b) -> a));
        }
        for (MarginRecordInfo o : infoList) {
            MarginRecordListRSP rsp = new MarginRecordListRSP();
            BeanUtil.copyProperties(o, rsp);
            if (RecordTypeEnum.COLLECTION.name().equals(o.getRecordType())) {
                rsp.setWriteOffStatus(marginRecordSupportPort.displayCollectionWriteOffStatus(o.getWriteOffStatus()));
            } else {
                rsp.setWriteOffStatus(Optional.ofNullable(o.getWriteOffStatus()).map(MarginWriteOffStatusEnum::of).map(MarginWriteOffStatusEnum::display).orElse(null));
            }
            rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
            rsp.setDataSource("财务系统");
            rsp.setContractCode(rentCode2ContractCode.get(o.getRentCollectionCode()));
            MarginCollectionRecordInfo collectionBaseInfo = collectionId2Bean.get(o.getCollectionId());
            if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
                rsp.setPlanCollectionDate(collectionBaseInfo.getPlanCollectionDate());
                rsp.setPlanCollectionAmount(collectionBaseInfo.getPlanCollectionAmount());
            }
            rsps.add(rsp);
        }
        return rsps;
    }

    private void collectionInfo2marginRecordInfo(MarginCollectionRecordInfo collectionBaseInfo, MarginRecordInfo recordInfo) {
        recordInfo.setCollectionId(collectionBaseInfo.getId());
        recordInfo.setId(collectionBaseInfo.getId());
        recordInfo.setSortId(collectionBaseInfo.getCode());
        recordInfo.setCollectionDate(collectionBaseInfo.getCollectionDate());
        recordInfo.setCollectionAmount(collectionBaseInfo.getCollectionAmount());
        recordInfo.setWriteOffStatus(collectionBaseInfo.getWriteOffStatus());
        recordInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
        recordInfo.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.name());
    }

    @Override
    public MarginRecordDetailRSP collectionDetail(MarginRecordDetailREQ req) {
        marginRecordSupportPort.checkCollectionView(req.getId());
        MarginCollectionRecordInfo collectionBaseInfo = marginRecordSupportPort.getCollectionById(req.getId());
        MarginRecordInfo marginRecordInfo = new MarginRecordInfo();
        collectionInfo2marginRecordInfo(collectionBaseInfo, marginRecordInfo);
        MarginCollectionRecordInfo recordInfo = marginRecordSupportPort.getLastCollectionRecord(req.getId());
        MarginRecordDetailRSP rsp = new MarginRecordDetailRSP();
        if (recordInfo != null) {
            MarginRecordDetailRSP.BankInfo bankInfo = new MarginRecordDetailRSP.BankInfo();
            bankInfo.setAccountId(recordInfo.getOurAccountId());
            bankInfo.setAccountName(recordInfo.getOurAccountName());
            bankInfo.setAccountNumber(recordInfo.getOurAccountNumber());
            bankInfo.setAccountBank(recordInfo.getOurAccountBank());
            rsp.setOurBankInfo(bankInfo);
        }
        BeanUtil.copyProperties(marginRecordInfo, rsp);
        rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
//        if (marginRecordInfo.getSourceFlag() == 1) {
//            Map<Long, String> id2Name = id2NameService.sysUserId2Name(Collections.singleton(Long.parseLong(marginRecordInfo.getDataSource())));
//            rsp.setDataSource(id2Name.get(Long.parseLong(marginRecordInfo.getDataSource())));
//        }
        return rsp;
    }

    @Override
    public MarginRecordDetailRSP detail(MarginRecordDetailREQ req) {
        marginRecordSupportPort.checkMarginRecordView(req.getId());
        MarginRecordInfo marginRecordInfo = marginRecordInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(marginRecordInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        MarginRecordDetailRSP rsp = new MarginRecordDetailRSP();
        if (StrUtil.isNotBlank(marginRecordInfo.getOurAccountNumber())) {
            rsp.setOurBankInfo(getBankInfo(marginRecordInfo, false));
        }
        if (StrUtil.isNotEmpty(marginRecordInfo.getOtherAccountNumber())) {
            rsp.setOtherBankInfo(getBankInfo(marginRecordInfo, true));
        }
        BeanUtil.copyProperties(marginRecordInfo, rsp);
        rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
//        if (marginRecordInfo.getSourceFlag() == 1) {
//            Map<Long, String> id2Name = id2NameService.sysUserId2Name(Collections.singleton(Long.parseLong(marginRecordInfo.getDataSource())));
//            rsp.setDataSource(id2Name.get(Long.parseLong(marginRecordInfo.getDataSource())));
//        }
        rsp.setDataSource("财务系统");
        return rsp;
    }

    @Override
    public MarginDeductDetailRSP deductDetail(MarginRecordDetailREQ req) {
        marginRecordSupportPort.checkMarginRecordView(req.getId());
        MarginRecordInfo marginRecordInfo = marginRecordInfoMapper.selectById(req.getId());
        MarginDeductDetailRSP rsp = new MarginDeductDetailRSP();
        BeanUtil.copyProperties(marginRecordInfo, rsp);
        MarginCashInfoRSP cashInfo = new MarginCashInfoRSP();
        MarginCollectionRecordInfo info = marginRecordSupportPort.getCollectionById(marginRecordInfo.getCollectionId());
        rsp.setPhase(info.getPhase());
        rsp.setPaymentCode(info.getPaymentCode());
        cashInfo.setCode(info.getCode());
        cashInfo.setCashFlowAmount(info.getCashFlowAmount());
        cashInfo.setCashFlowItem(Optional.ofNullable(info.getCashFlowItem()).map(CashFlowItemEnum::of).map(CashFlowItemEnum::display).orElse(null));
        cashInfo.setInterest(info.getInterest());
        cashInfo.setPenaltyInterest(info.getPenaltyInterest());
        cashInfo.setPrincipal(info.getPrincipal());
        cashInfo.setPlanCollectionDate(info.getPlanCollectionDate());
        cashInfo.setPlanCollectionAmount(info.getPlanCollectionAmount());
        cashInfo.setLastDeductPrincipal(LongUtil.null2zero(info.getPrincipal()) - LongUtil.null2zero(info.getCollectionPrincipal()));
        cashInfo.setLastDeductInterest(LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
        cashInfo.setLastDeductPenaltyInterest(LongUtil.null2zero(info.getPenaltyInterest()) - LongUtil.null2zero(info.getCollectionPenaltyInterest()));
        cashInfo.setLastDeductAmount(cashInfo.getLastDeductInterest() + cashInfo.getLastDeductPenaltyInterest() + cashInfo.getLastDeductPrincipal());
        rsp.setCashInfo(cashInfo);
        return rsp;
    }

    private MarginRecordDetailRSP.BankInfo getBankInfo(MarginRecordInfo marginRecordInfo, boolean isOther) {
        MarginRecordDetailRSP.BankInfo bankInfo = new MarginRecordDetailRSP.BankInfo();
        if (isOther) {
            bankInfo.setAccountName(marginRecordInfo.getOtherAccountName());
            bankInfo.setAccountNumber(marginRecordInfo.getOtherAccountNumber());
            bankInfo.setAccountBank(marginRecordInfo.getOtherAccountBank());
        } else {
            bankInfo.setAccountId(marginRecordInfo.getOurAccountId());
            bankInfo.setAccountName(marginRecordInfo.getOurAccountName());
            bankInfo.setAccountNumber(marginRecordInfo.getOurAccountNumber());
            bankInfo.setAccountBank(marginRecordInfo.getOurAccountBank());
        }

        return bankInfo;
    }
}
