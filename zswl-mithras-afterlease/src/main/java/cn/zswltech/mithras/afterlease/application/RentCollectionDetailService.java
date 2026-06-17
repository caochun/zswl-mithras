package cn.zswltech.mithras.afterlease.application;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.rentcollection.*;
import cn.zswltech.mithras.afterlease.enums.RentCollectionLevelEnum;
import cn.zswltech.mithras.afterlease.enums.RentCollectionWriteOffStatus;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.afterlease.util.CollectionLevelUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2022-11-18
 **/
@Slf4j
@Service
public class RentCollectionDetailService {
    @Resource
    private RentCollectionDetailDataPort rentCollectionDetailDataPort;

    public R<RentDetailInfoRSP> rentDetail(RentDetailInfoREQ req) {
        RentCollectionDetailSnapshot info = rentCollectionDetailDataPort.getRentCollectionById(req.getId());
        RentDetailInfoRSP rsp = new RentDetailInfoRSP();
        rsp.setPrincipal(info.getPrincipal());
        rsp.setInterest(info.getInterest());
        rsp.setPenaltyInterest(info.getPenaltyInterest());
        rsp.setPhase(info.getPhase());
        rsp.setWriteOffStatus(info.getWriteOffStatus());
        rsp.setCollectionAmount(info.getCollectionAmount());
        rsp.setPlanCollectionDate(info.getPlanCollectionDate());
        rsp.setId(info.getId());
        rsp.setCollectionCode(info.getCode());
        rsp.setCreditAmount(info.getPenaltyInterestDeductionAmount());
        rsp.setTotalAmount(LongUtil.null2zero(info.getPrincipal())+LongUtil.null2zero(info.getInterest())+LongUtil.null2zero(info.getPenaltyInterest())-LongUtil.null2zero(info.getPenaltyInterestDeductionAmount()));
        List<RentCollectionRecordSnapshot> infoList = rentCollectionDetailDataPort.listCollectionRecords(req.getId());
        List<RentDetailInfoRSP.Records> records = new ArrayList<>();
        for (RentCollectionRecordSnapshot o : infoList) {
            RentDetailInfoRSP.Records tmp = new RentDetailInfoRSP.Records();
            tmp.setCollectionDate(o.getCollectionDate());
            tmp.setInterest(o.getInterest());
            tmp.setId(o.getId());
            tmp.setPrincipal(o.getPrincipal());
            tmp.setPenaltyInterest(o.getPenaltyInterest());
            tmp.setCollectionType(displayCollectionType(o.getCollectionType()));
            records.add(tmp);
        }
        rsp.setCollections(records);
        return R.ok(rsp);
    }

    public R<OverdueDetailInfoRSP> overdueDetail(RentDetailInfoREQ req){
        OverdueDetailInfoRSP rsp = new OverdueDetailInfoRSP();
        RentCollectionDetailSnapshot baseInfo = rentCollectionDetailDataPort.getRentCollectionById(req.getId());
        RentCollectionOverdueContext overdueContext = rentCollectionDetailDataPort.getOverdueContext(req.getId());
        rsp.setDailyRate(overdueContext != null ? LongUtil.null2zero(overdueContext.getDefaultInterestRate()) : 0);
        if (overdueContext != null && !ProjectBizType.ZL.name().equals(overdueContext.getContractBizType())){
            if (overdueContext.getDefaultInterestRate() == null){
                rsp.setDailyRate(500);
            }
        }
        rsp.setSumAmount(LongUtil.null2zero(baseInfo.getPenaltyInterest()) + LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()));
        rsp.setWriteOffAmount(baseInfo.getCollectionPenaltyInterest());
        rsp.setPenaltyInterestAmount(baseInfo.getPenaltyInterest());
        rsp.setCreditAmount(LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()));
        rsp.setLastAmount(LongUtil.null2zero(baseInfo.getPenaltyInterest())-LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()));
        if(LongUtil.null2zero(baseInfo.getOverdueCollectionCount()) <= 0 || ObjectUtil.isNull(baseInfo.getPlanPenaltyInterestDate())){
            rsp.setStatus("未通知财务系统收款");
        }else {
            //通知过需判断是否核销完毕,非今天且未核销完
            if(!baseInfo.getPlanPenaltyInterestDate().equals(LocalDate.now()) && (LongUtil.null2zero(baseInfo.getPenaltyInterest()) - LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest())) > 0){
                rsp.setStatus("财务系统核销失败");
            }
            rsp.setStatus("已通知财务系统收款");
        }
        return R.ok(rsp);
    }

    public int findLongestDayOverdueRent(Collection<Long> contractIds) {
        List<RentCollectionDetailSnapshot> collectionBaseInfoList = rentCollectionDetailDataPort.listRentCollectionsByContractIds(contractIds);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return 0;
        }
        long max = 0;
        LocalDate now = LocalDate.now();
        for (RentCollectionDetailSnapshot collectionBaseInfo : collectionBaseInfoList) {
            if (collectionBaseInfo.getPlanCollectionDate().isBefore(now)) {
                long planRent = Optional.ofNullable(collectionBaseInfo.getPlanCollectionAmount()).orElse(0L);
                long actualRent = Optional.ofNullable(collectionBaseInfo.getCollectionAmount()).orElse(0L);
                if (actualRent >= planRent) {
                    continue;
                }
                // 没有收款且计划收款日期在今天之前的说明已经逾期
                long overdueDays = CollectionLevelUtil.getOverdueDay(collectionBaseInfo.getPlanCollectionDate());
                max = Math.max(max, overdueDays);
            }
        }
        return (int) max;
    }

    public R<List<OverdueRentListInfoRSP>> overdueRentList(ProjDetailInfoREQ req) {
        List<RentCollectionDetailSnapshot> collectionBaseInfos = rentCollectionDetailDataPort.listRentCollectionsByContractId(req.getContractId());
        List<OverdueRentListInfoRSP> rsps = new LinkedList<>();
        if (CollectionUtil.isEmpty(collectionBaseInfos)){
            return R.fail("无收款记录");
        }
        Map<String, List<RentCollectionDetailSnapshot>> receiptCodeMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(RentCollectionDetailSnapshot::getReceiptCode));
        if (CollectionUtil.isEmpty(receiptCodeMap.keySet())){
            return R.ok();
        }
        List<String> receiptCodes = receiptCodeMap.keySet().stream().sorted().collect(Collectors.toList());
        for (String receiptCode :receiptCodes){
            List<RentCollectionDetailSnapshot> infos = receiptCodeMap.get(receiptCode).stream().sorted(Comparator.comparing(RentCollectionDetailSnapshot::getPhase)).collect(Collectors.toList());
            OverdueRentListInfoRSP rsp = new OverdueRentListInfoRSP();
            rsp.setPaymentCode(receiptCode);
            rsp.setPaymentId(infos.get(0).getReceiptId());
            List<OverdueRentListInfoRSP.OverdueRent> rents = new LinkedList<>();
            long maxDay = 0;
            for (RentCollectionDetailSnapshot info : infos){
                OverdueRentListInfoRSP.OverdueRent rent = new OverdueRentListInfoRSP.OverdueRent();
                rent.setPhase(info.getPhase());
                rent.setPlanCollectionDate(info.getPlanCollectionDate());
                rent.setPrincipal(info.getPrincipal());
                rent.setInterest(info.getInterest());
                rent.setPenaltyInterest(info.getPenaltyInterest());
                rent.setWriteOffStatus(Optional.ofNullable(info.getWriteOffStatus()).map(RentCollectionWriteOffStatus::of).map(RentCollectionWriteOffStatus::display).orElse(null));
                rent.setOverdue(false);
                rent.setOverdueDay(0L);
                //未核销完逾期
                if (info.getPlanCollectionDate().isBefore(LocalDate.now()) && !RentCollectionWriteOffStatus.isWriteOffCompleted(info.getWriteOffStatus())) {
                    long until = CollectionLevelUtil.getOverdueDay(info.getPlanCollectionDate());
                    rent.setOverdueDay(until);
                    rent.setOverdue(true);
                    maxDay = Math.max(maxDay,until);
                } else if (RentCollectionWriteOffStatus.isWriteOffCompleted(info.getWriteOffStatus())){
                    //核销完毕但是有过期的
                    long until = CollectionLevelUtil.getOverdueDay(info.getPlanCollectionDate(), info.getCollectionDate());
                    if (until > 0) {
                        rent.setOverdueDay(until);
                        rent.setOverdue(true);
                        maxDay = Math.max(maxDay, until);
                    }
                }
                rents.add(rent);
            }
            rsp.setCollectionLevel(Optional.ofNullable(CollectionLevelUtil.getCollectionLevel(maxDay)).map(RentCollectionLevelEnum::display).orElse(null));
            rsp.setOverdueRents(rents);
            //设置是否可通知财务
            rsp.setNoticeFinancialCollection(noticeFinancialDecide(collectionBaseInfos));
            rsps.add(rsp);
        }
        return R.ok(rsps);
    }

    private Boolean noticeFinancialDecide(List<RentCollectionDetailSnapshot> collectionBaseInfos){
        //非今天且有未核销完毕到罚息即可再次通知
        if(ObjectUtil.isNotEmpty(collectionBaseInfos)){
            for (RentCollectionDetailSnapshot collectionBaseInfo : collectionBaseInfos){
                if((ObjectUtil.isNotNull(collectionBaseInfo.getPlanPenaltyInterestDate()) && !collectionBaseInfo.getPlanPenaltyInterestDate().equals(LocalDate.now())) && (LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest())) > 0){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    private String displayCollectionType(String collectionType) {
        if (collectionType == null) {
            return null;
        }
        switch (collectionType) {
            case "COLLECTION":
                return "收款";
            case "WIRE_TRANSFER":
                return "电汇";
            case "REFUND":
                return "退款";
            case "REFUND_WARRANTY":
                return "质保金退款";
            case "REFUND_MARGIN":
                return "保证金退款";
            case "REFUND_MARGIN_DEDUCT":
                return "保证金抵扣";
            default:
                return collectionType;
        }
    }

}
