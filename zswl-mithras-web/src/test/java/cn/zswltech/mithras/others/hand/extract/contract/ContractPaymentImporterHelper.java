package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionRecordWriteOffStatus;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentMethod;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.service.enums.projreview.ProjectType;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

import static cn.zswltech.mithras.others.hand.extract.ImportCommonHelper.extractNumber;

/**
 * 帮助类
 *
 * @author wangchuanhao
 * @date 2022/9/26 8:56 PM
 */
public class ContractPaymentImporterHelper {

    public static String extractLeaseType(String businessType) {
        if ("直接租赁".equals(businessType)) {
            return LeaseType.zhi_zu.name();
        } else if ("售后回租".equals(businessType)) {
            return LeaseType.hui_zu.name();
        }
        return null;
    }

    public static String extractSponsorUserIds(JSONObject handData, Map<String, Long> userMap) {
        List<Long> cosUserList = new ArrayList<>();
        Long cosUser1 = Optional.ofNullable(handData.getString("assist_employee_id_n")).map(userMap::get).orElse(null);
        Long cosUser2 = Optional.ofNullable(handData.getString("assist_employee_id_a_n")).map(userMap::get).orElse(null);
        if (cosUser1 != null) {
            cosUserList.add(cosUser1);
        }
        if (cosUser2 != null) {
            cosUserList.add(cosUser2);
        }
        return JSON.toJSONString(cosUserList);
    }

    public static String extractRepayRate(String annualPayTimes) {
        if (StringUtils.isBlank(annualPayTimes)) {
            return null;
        }
        switch (annualPayTimes) {
            case "月": return RepayRateEnum.MONTH.name();
            case "双月": return RepayRateEnum.DOUBLE_MONTH.name();
            case "半年": return RepayRateEnum.HALF_YEAR.name();
            case "季": return RepayRateEnum.QUARTER.name();
            case "年": return RepayRateEnum.YEAR.name();
            case "不规则还款": return RepayRateEnum.LRREGULAR.name();
        }
        return null;
    }

    public static String extractRentalCalcType(String priceList) {
        if (StringUtils.isBlank(priceList)) {
            return null;
        }
        switch (priceList) {
            case "等额本金": return RepayCalcType.DEBJ.name();
            case "等额本息": return RepayCalcType.DEBX.name();
            case "固定本金": return RepayCalcType.DEBX.name();
            case "不等额租金": return RepayCalcType.BGZHK.name();
        }
        return null;
    }

    public static String extractRateType(String rateDisplay) {
        if (StringUtils.isBlank(rateDisplay)) {
            return null;
        }
        switch (rateDisplay) {
            case "固定利率": return RateType.FIXED.name();
            case "浮动利率": return RateType.FLOAT.name();
        }
        return null;
    }

    public static String extractLprType(String lprRateType) {
        if (StringUtils.isBlank(lprRateType)) {
            return null;
        }
        switch (lprRateType) {
            case "一年内贷款利率": return LPRTypeEnum.ONE_YEAR.name();
            case "一年至五年贷款利率": return LPRTypeEnum.ONE_YEAR.name();
            case "五年以上贷款利率": return LPRTypeEnum.FIVE_YEAR.name();
        }
        return null;
    }

    public static String getCashFlowCode(String paymentApplyCode, Integer cashFlowPhase) {
        return paymentApplyCode + "-" + NumberUtil.decimalFormat("000", cashFlowPhase);
    }

    public static String getCollectionCode(String cashFlowItem,String paymentCode,Integer phase,String contractCode){
        CollectionBaseInfoMapper collectionBaseInfoMapper = SpringContextHolder.getBean(CollectionBaseInfoMapper.class);
        if (CashFlowItemEnum.RENT.name().equals(cashFlowItem)){
            DecimalFormat format = new DecimalFormat("000");
            return paymentCode+"-"+format.format(phase);
        }else if (CashFlowItemEnum.OTHERAMOUNT.name().equals(cashFlowItem)){
            return paymentCode+"-zf";
        }else if (CashFlowItemEnum.NOMINAL_PRICE.name().equals(cashFlowItem) ||  CashFlowItemEnum.EARLY_STOP_COMPENSATION.name().equals(cashFlowItem)){
            String year = contractCode.substring(contractCode.indexOf("【")+1,contractCode.indexOf("】"));
            String code = contractCode.substring(contractCode.indexOf("(")+1,contractCode.indexOf(")"));
            String partone = year + code.substring(0, code.indexOf("-")) + code.substring(code.indexOf("-") + 1);
            Integer integer = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractCode, contractCode)
                    .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem));
            DecimalFormat format = new DecimalFormat("00");
            return CashFlowItemEnum.NOMINAL_PRICE.name().equals(cashFlowItem) ? partone+"-jk"+ "-" + format.format(integer + 1) : partone+"-bcj"+ "-" + format.format(integer + 1);
        }else if (CashFlowItemEnum.FIRST_RENT.name().equals(cashFlowItem)){
            return paymentCode+"-sf";
        }else if (CashFlowItemEnum.EARNEST_MONEY.name().equals(cashFlowItem)){
            return paymentCode+"-bzj";
        }else {
            throw new MithrasException("无此现金流项目收款");
        }
    }

    public static String getBzjCode(String contractCode){
        String year = contractCode.substring(contractCode.indexOf("【")+1,contractCode.indexOf("】"));
        String code = contractCode.substring(contractCode.indexOf("(")+1,contractCode.indexOf(")"));
        return year+code.substring(0,code.indexOf("-"))+code.substring(code.indexOf("-")+1)+"-bzj";
    }

    /**
     * excel提取收款记录
     * @param detailSheetDataList
     * @param collectionBaseInfo
     * @return
     */
    public static List<CollectionRecordInfo> buildCollectionRecordList(List<List<Object>> detailSheetDataList, CollectionBaseInfo collectionBaseInfo, Integer row) {
        long remainInterest = Optional.ofNullable(detailSheetDataList.get(row).get(14)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L);
        long remainPrincipal = Optional.ofNullable(detailSheetDataList.get(row).get(15)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L);
        long remainPenaltyInterest = Optional.ofNullable(detailSheetDataList.get(row).get(31)).map(d -> "-".equals(d) ? null : d).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L);
        List<CollectionRecordInfo> recordList = new ArrayList<>();
        LocalDate firstDate = Optional.ofNullable(detailSheetDataList.get(row).get(19)).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null),
                secondDate = Optional.ofNullable(detailSheetDataList.get(row).get(21)).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null),
                thirdDate = Optional.ofNullable(detailSheetDataList.get(row).get(23)).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null);
        if (Objects.isNull(firstDate)) {
            return recordList;
        }
        CollectionRecordInfo firstRecord = buildCollectionRecord(collectionBaseInfo);
        firstRecord.setCollectionDate(firstDate);
        firstRecord.setCollectionAmount(Optional.ofNullable(detailSheetDataList.get(row).get(20)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L));
        long firstRemainAmount = firstRecord.getCollectionAmount();
        // 优先还利息
        firstRecord.setInterest(remainInterest > firstRemainAmount ? firstRemainAmount : remainInterest);
        firstRemainAmount -= firstRecord.getInterest();
        remainInterest -= firstRecord.getInterest();
        // 然后是本金
        firstRecord.setPrincipal(remainPrincipal > firstRemainAmount ? firstRemainAmount : remainPrincipal);
        firstRemainAmount -= firstRecord.getPrincipal();
        remainPrincipal -= firstRecord.getPrincipal();
        // 最后是罚息
        firstRecord.setPenaltyInterest(remainPenaltyInterest > firstRemainAmount ? firstRemainAmount : remainPenaltyInterest);
        firstRemainAmount -= firstRecord.getPenaltyInterest();
        remainPrincipal -= firstRecord.getPenaltyInterest();
        firstRecord.setSortId(1);
        recordList.add(firstRecord);
        if (Objects.isNull(secondDate)) {
            return recordList;
        }
        CollectionRecordInfo secondRecord = buildCollectionRecord(collectionBaseInfo);
        secondRecord.setCollectionDate(secondDate);
        secondRecord.setCollectionAmount(Optional.ofNullable(detailSheetDataList.get(row).get(22)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L));
        long secondRemainAmount = secondRecord.getCollectionAmount();
        // 优先还利息
        secondRecord.setInterest(remainInterest > secondRemainAmount ? secondRemainAmount : remainInterest);
        secondRemainAmount -= secondRecord.getInterest();
        remainInterest -= secondRecord.getInterest();
        // 然后是本金
        secondRecord.setPrincipal(remainPrincipal > secondRemainAmount ? secondRemainAmount : remainPrincipal);
        secondRemainAmount -= secondRecord.getPrincipal();
        remainPrincipal -= secondRecord.getPrincipal();
        // 最后是罚息
        secondRecord.setPenaltyInterest(remainPenaltyInterest > secondRemainAmount ? secondRemainAmount : remainPenaltyInterest);
        secondRemainAmount -= secondRecord.getPenaltyInterest();
        remainPrincipal -= secondRecord.getPenaltyInterest();
        secondRecord.setSortId(2);
        recordList.add(secondRecord);
        if (Objects.isNull(thirdDate)) {
            return recordList;
        }
        CollectionRecordInfo thirdRecord = buildCollectionRecord(collectionBaseInfo);
        thirdRecord.setCollectionDate(thirdDate);
        thirdRecord.setCollectionAmount(Optional.ofNullable(detailSheetDataList.get(row).get(24)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(0L));
        long thirdRemainAmount = thirdRecord.getCollectionAmount();
        // 优先还利息
        thirdRecord.setInterest(remainInterest > thirdRemainAmount ? thirdRemainAmount : remainInterest);
        thirdRemainAmount -= thirdRecord.getInterest();
        remainInterest -= thirdRecord.getInterest();
        // 然后是本金
        thirdRecord.setPrincipal(remainPrincipal > thirdRemainAmount ? thirdRemainAmount : remainPrincipal);
        thirdRemainAmount -= thirdRecord.getPrincipal();
        remainPrincipal -= thirdRecord.getPrincipal();
        // 最后是罚息
        thirdRecord.setPenaltyInterest(remainPenaltyInterest > thirdRemainAmount ? thirdRemainAmount : remainPenaltyInterest);
        thirdRemainAmount -= thirdRecord.getPenaltyInterest();
        remainPrincipal -= thirdRecord.getPenaltyInterest();
        thirdRecord.setSortId(3);
        recordList.add(thirdRecord);
        return recordList;
    }

    public static CollectionRecordInfo buildCollectionRecord(CollectionBaseInfo collectionBaseInfo) {
        CollectionRecordInfo collectionRecordInfo = new CollectionRecordInfo();
        collectionRecordInfo.setCollectionId(collectionBaseInfo.getId());
        collectionRecordInfo.setDataSource(String.valueOf(collectionBaseInfo.getCreateBy()));
        collectionRecordInfo.setCollectionType(PaymentMethod.WIRE_TRANSFER.name());
        collectionRecordInfo.setSourceFlag(1);
        collectionRecordInfo.setWriteOffStatus(CollectionRecordWriteOffStatus.WRITTEN_OFF.name());
//        collectionRecordInfo.setOurAccountId();
        collectionRecordInfo.setOurAccountName("浙江浙商融资租赁有限公司");
        collectionRecordInfo.setOurAccountNumber("33050161612700000175");
//        collectionRecordInfo.setOurAccountBank();
        collectionRecordInfo.setCreateBy(collectionBaseInfo.getCreateBy());
        collectionRecordInfo.setUpdateBy(collectionBaseInfo.getUpdateBy());
        return collectionRecordInfo;
    }

    public static String extractProjClassify(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        switch (data) {
            case "鼓励介入类": return ProjectClassify.ENCOURAGEMENT.name();
            case "适度支持类": return ProjectClassify.MODERATE_SUPPORT.name();
            case "谨慎支持类": return ProjectClassify.CAUTIOUS.name();
            case "工程机械类（厂商担保模式）": return ProjectClassify.CONSTRUCTION_MACHINERY.name();
            case "协同业务类": return ProjectClassify.INTRA_GROUP_COLLABORATION.name();
        }
        return null;
    }

    public static String extractProjType(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        switch (data) {
            case "公共事业类": return ProjectType.PUBLIC_UTILITIES.name();
            case "公用事业类": return ProjectType.PUBLIC_UTILITIES.name();
            case "省内国（央）企": return ProjectType.STATE_OWNED_ENTERPRISE.name();
            case "省内国(央)企": return ProjectType.STATE_OWNED_ENTERPRISE.name();
            case "其他": return ProjectType.OTHER.name();
        }
        return null;
    }

    public static String removeSpecialChar(String originData) {
        if (StringUtils.isBlank(originData)) {
            return originData;
        }
        return originData.trim().replaceAll("\n|\r|\t", "");
    }

}
