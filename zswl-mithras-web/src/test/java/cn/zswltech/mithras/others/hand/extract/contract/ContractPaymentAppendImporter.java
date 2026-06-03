package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.margin.enums.MarginWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentMethod;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.*;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.*;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.lib.PaymentBaseInfoLibMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.lib.PaymentPlanedDetailLibMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentPlanedDetailMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewLeasePriceMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.lib.contract.ContractVersionService;
import cn.zswltech.mithras.payment.application.lib.libservice.impl.PaymentVersionServiceImpl;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.zswltech.mithras.others.hand.extract.ImportCommonHelper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.others.hand.extract.ImportCommonHelper.extractNumber;

/**
 * 追加导入合同、付款数据
 *
 * @author wangchuanhao
 * @date 2022/10/27 3:53 PM
 */
@Component
public class ContractPaymentAppendImporter {

    private static final Logger log = LoggerFactory.getLogger(ContractPaymentAppendImporter.class);

    @Resource
    private ImportCommonHelper importCommonHelper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjReviewLeasePriceMapper projReviewLeasePriceMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private ContractRentEstimateMapper contractRentEstimateMapper;
    @Resource
    private ContractRentEstimateLibMapper contractRentEstimateLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoLibMapper paymentBaseInfoLibMapper;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private ContractTenantryMapper contractTenantryMapper;
    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private ContractAccountMapper contractAccountMapper;
    @Resource
    private ContractAccountLibMapper contractAccountLibMapper;
    @Resource
    private ContractGuarantorMapper contractGuarantorMapper;
    @Resource
    private ContractGuarantorLibMapper contractGuarantorLibMapper;
    @Resource
    private PaymentPlanedDetailMapper paymentPlanedDetailMapper;
    @Resource
    private PaymentPlanedDetailLibMapper paymentPlanedDetailLibMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private ContractVersionService contractVersionService;
    @Resource
    private PaymentVersionServiceImpl paymentVersionService;
    @Resource
    private ContractService contractService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private MarginRecordInfoMapper marginRecordInfoMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @SneakyThrows
    public void initBaseData() {
        Set<String> needHandleContractCodeSet = new HashSet<>(Arrays.asList("浙商租【2021】租字第(CF-0005)号","浙商租【2021】租字第(CF-0003)号","浙商租【2021】租字第(CF-0002)号","浙商租【2021】租字第(CF-0001)号"));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, needHandleContractCodeSet));

        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Downloads/补录自然人合同.xlsx"));
        Set<String> sheetNameSheet = new HashSet<>(tzExcelReader.getSheetNames());
//        tzExcelReader.setSheet("业务合同情况汇总表");
//        List<List<Object>> contractSheetList = tzExcelReader.read();
        JSONObject excelDataObj = new JSONObject();
//        for (List<Object> rowData : contractSheetList) {
//            if (rowData.size() > 8
//                    && rowData.get(8) instanceof String
//                    && ((String) rowData.get(8)).contains("号")
//            ) {
//                // 临时处理
////                if (!"诸暨市城东新城建设有限公司1".equals(rowData.get(14))) {
////                    continue;
////                }
//
//                JSONObject rowDataObj = new JSONObject();
//                String actualContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
//                excelDataObj.put(actualContractCode, rowDataObj);
//                rowDataObj.put("actualContractCode", ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")"));
//                rowDataObj.put("detailSheetName", ((String) rowData.get(14)).replaceAll("（", "(").replaceAll("）", ")"));
//                rowDataObj.put("contractStatus", rowData.get(49));
//                rowDataObj.put("actualFinishDate", rowData.get(50));
//            }
//        }

        // 自然人合同导入
        JSONObject cf005 = new JSONObject();
        cf005.put("actualContractCode", "浙商租【2021】租字第(CF-0005)号");
        cf005.put("detailSheetName", "胡高山");
        cf005.put("contractStatus", "已结清");
        cf005.put("actualFinishDate", cn.hutool.core.date.DateTime.of("2022-04-15", "yyyy-MM-dd"));
        excelDataObj.put("浙商租【2021】租字第(CF-0005)号", cf005);
        JSONObject cf003 = new JSONObject();
        cf003.put("actualContractCode", "浙商租【2021】租字第(CF-0003)号");
        cf003.put("detailSheetName", "陆冰花");
        cf003.put("contractStatus", "起租");
        excelDataObj.put("浙商租【2021】租字第(CF-0003)号", cf003);
        JSONObject cf002 = new JSONObject();
        cf002.put("actualContractCode", "浙商租【2021】租字第(CF-0002)号");
        cf002.put("detailSheetName", "周昊瀚");
        cf002.put("contractStatus", "起租");
        excelDataObj.put("浙商租【2021】租字第(CF-0002)号", cf002);
        JSONObject cf001 = new JSONObject();
        cf001.put("actualContractCode", "浙商租【2021】租字第(CF-0001)号");
        cf001.put("detailSheetName", "华铅良");
        cf001.put("contractStatus", "起租");
        excelDataObj.put("浙商租【2021】租字第(CF-0001)号", cf001);



        transactionTemplate.execute(status -> {
            try {
                for (int i = 0; i < contractBaseInfoList.size(); i++) {
                    ContractBaseInfo contractBaseInfo = contractBaseInfoList.get(i);
                    log.info("处理合同编号{}:{}", i, contractBaseInfo.getContractCode());
                    JSONObject excelSimpleObj = excelDataObj.getJSONObject(contractBaseInfo.getContractCode());
                    String detailSheetName = excelSimpleObj.getString("detailSheetName");
                    if (!sheetNameSheet.contains(detailSheetName)) {
                        log.error("sheet不存在:{}", detailSheetName);
                        continue;
                    }
                    log.info("需要处理的数据:{}", contractBaseInfo.getContractCode());
                    tzExcelReader.setSheet(detailSheetName);
                    List<List<Object>> detailSheetDataList = tzExcelReader.read();

                    contractBaseInfo.setActualLeaseDate(Optional.ofNullable(detailSheetDataList.get(3).get(14)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                    contractBaseInfo.setActualFinishDate(Optional.ofNullable(excelSimpleObj.get("actualFinishDate")).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                    contractBaseInfoMapper.updateById(contractBaseInfo);
                    // 删除原有的收款主表、保证金
                    marginBaseInfoMapper.delete(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, contractBaseInfo.getId()));
                    collectionBaseInfoMapper.delete(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId()));
                    // 删除原有的实际租金表
                    contractRentActualMapper.delete(Wrappers.<ContractRentActual>lambdaQuery().eq(ContractRentActual::getContractId, contractBaseInfo.getId()));
                    contractReceiptMapper.delete(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getContractId, contractBaseInfo.getId()));

                    PaymentBaseInfo paymentBaseInfo = new PaymentBaseInfo();
                    paymentBaseInfo.setPaymentCode(PaymentBaseInfoService.generatePaymentCode(contractBaseInfo.getContractCode(), 1));
                    paymentBaseInfo.setContractId(contractBaseInfo.getId());
                    paymentBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                    paymentBaseInfo.setClientId(contractBaseInfo.getClientId());
                    paymentBaseInfo.setPayables("授信款");
                    paymentBaseInfo.setApplyPaymentDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd HH:mm:ss")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).orElse(null));
                    paymentBaseInfo.setApplyPaymentAmount(Optional.ofNullable(detailSheetDataList.get(5).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setEarnestMoney(Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setDownPayment(Optional.ofNullable(detailSheetDataList.get(2).get(12)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setConsultingFee(Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setNominalPrice(Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setPaymentProcessStatus(ProcessStatus.UN_SUBMIT.name());
                    paymentBaseInfo.setPaymentStatus(PaymentStatusEnum.NEW.name());
                    paymentBaseInfo.setPaidInDate(contractBaseInfo.getEstimatedLeaseDate());
                    paymentBaseInfo.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
                    paymentBaseInfo.setActualDetailCount(0);
                    paymentBaseInfo.setConBizDeptId(contractBaseInfo.getBizDeptId());
                    paymentBaseInfo.setConBizDeptLeaderId(contractBaseInfo.getBizDeptLeaderId());
                    paymentBaseInfo.setConBizDivisionLeaderId(contractBaseInfo.getBizDivisionLeaderId());
                    paymentBaseInfo.setCreateBy(contractBaseInfo.getCreateBy());
                    paymentBaseInfo.setUpdateBy(contractBaseInfo.getCreateBy());
                    paymentBaseInfoMapper.insert(paymentBaseInfo);

                    // 合同 - 借据
                    ContractReceipt contractReceipt = new ContractReceipt();
                    contractReceipt.setContractId(contractBaseInfo.getId());
                    contractReceipt.setPaymentApplyCode(paymentBaseInfo.getPaymentCode());
                    contractReceipt.setCreateBy(contractBaseInfo.getCreateBy());
                    contractReceipt.setUpdateBy(contractBaseInfo.getCreateBy());
                    contractReceiptMapper.insert(contractReceipt);

                    // 合同实际租金表 需要先处理付款主表 然后新增借据 然后再插实际租金表
                    List<ContractRentActual> rentActualList = new ArrayList<>();
                    Map<Integer, ContractRentActual> rentActualMap = new HashMap<>();
                    for (int rentRow = 6;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        ContractRentActual rentActual = new ContractRentActual();
                        // 借据id
                        rentActual.setReceiptId(contractReceipt.getId());
                        rentActual.setContractId(contractBaseInfo.getId());
                        rentActual.setCashFlowPhase(Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d)).map(b -> b.intValue()).orElse(null));
                        rentActual.setCashFlowCode(ContractPaymentImporterHelper.getCashFlowCode(paymentBaseInfo.getPaymentCode(), rentActual.getCashFlowPhase()));
                        rentActual.setCashFlowDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        rentActual.setRent(Optional.ofNullable(detailSheetDataList.get(rentRow).get(13)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(14)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(15)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setRemainingPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setCreateBy(contractBaseInfo.getCreateBy());
                        rentActual.setUpdateBy(contractBaseInfo.getCreateBy());

                        rentActualList.add(rentActual);
                        rentActualMap.put(rentActual.getCashFlowPhase(), rentActual);
                    }
                    rentActualList.forEach(contractRentActualMapper::insert);

                    // 实际支付记录
                    PaymentActualDetail paymentActualDetail = new PaymentActualDetail();
                    paymentActualDetail.setPaymentId(paymentBaseInfo.getId());
                    paymentActualDetail.setSeqCode("01");
                    paymentActualDetail.setContractId(contractBaseInfo.getId());
                    paymentActualDetail.setInfoSource("手动录入");
                    paymentActualDetail.setPaymentMethod(PaymentMethod.WIRE_TRANSFER.name());
                    paymentActualDetail.setPaidInDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                    paymentActualDetail.setPaidInAmount(Optional.ofNullable(detailSheetDataList.get(5).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentActualDetail.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
                    paymentActualDetail.setCreateBy(contractBaseInfo.getCreateBy());
                    paymentActualDetail.setUpdateBy(contractBaseInfo.getCreateBy());
                    paymentActualDetailMapper.insert(paymentActualDetail);

                    // 收款核销 主表
                    List<CollectionBaseInfo> collectionBaseInfoList = new ArrayList<>();
                    int rentRecordCount = 0;
                    for (int rentRow = 6;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
                        collectionBaseInfo.setContractId(contractBaseInfo.getId());
                        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                        collectionBaseInfo.setClientId(contractBaseInfo.getClientId());
                        collectionBaseInfo.setPaymentId(paymentBaseInfo.getId());
                        collectionBaseInfo.setPaymentCode(paymentBaseInfo.getPaymentCode());
                        collectionBaseInfo.setCashFlowItem(CashFlowItemEnum.RENT.name());
                        collectionBaseInfo.setPhase(Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d)).map(b -> b.intValue()).orElse(null));
                        collectionBaseInfo.setCode(ContractPaymentImporterHelper.getCollectionCode(collectionBaseInfo.getCashFlowItem(), collectionBaseInfo.getPaymentCode(), collectionBaseInfo.getPhase(), collectionBaseInfo.getContractCode()));

                        collectionBaseInfo.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        collectionBaseInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(19)).map(o -> "".equals(o) ? null : o).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        collectionBaseInfo.setCashFlowAmount(Optional.ofNullable(detailSheetDataList.get(rentRow).get(13)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPlanCollectionAmount(collectionBaseInfo.getCashFlowAmount());
                        collectionBaseInfo.setInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(14)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(15)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPenaltyInterestUpdate(1);
                        collectionBaseInfo.setPenaltyInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(31)).map(d -> "-".equals(d) ? null : d).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setRentActualId(rentActualMap.get(collectionBaseInfo.getPhase()).getId());
                        collectionBaseInfo.setCreateBy(contractBaseInfo.getCreateBy());
                        collectionBaseInfo.setUpdateBy(contractBaseInfo.getCreateBy());
                        rentRecordCount++;

                        List<CollectionRecordInfo> recordList = ContractPaymentImporterHelper.buildCollectionRecordList(detailSheetDataList, collectionBaseInfo, rentRow);
                        collectionBaseInfo.setAllRecordSort(recordList.size());
                        collectionBaseInfo.setCollectionAmount(recordList.isEmpty() ? 0L : LongUtil.null2zero(collectionBaseInfo.getPrincipal()) + LongUtil.null2zero(collectionBaseInfo.getInterest()) + LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
                        collectionBaseInfo.setWriteOffStatus(recordList.size() > 0 ? CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name() : CollectionWriteOffStatusEnum.UNCOLLECTION.name());
                        collectionBaseInfo.setCollectionPrincipal(recordList.stream().mapToLong(CollectionRecordInfo::getPrincipal).sum());
                        collectionBaseInfo.setCollectionInterest(recordList.stream().mapToLong(CollectionRecordInfo::getInterest).sum());
                        collectionBaseInfo.setCollectionPenaltyInterest(recordList.stream().mapToLong(CollectionRecordInfo::getPenaltyInterest).sum());
                        collectionBaseInfoMapper.insert(collectionBaseInfo);
                        recordList.forEach(r -> {
                            r.setCollectionId(collectionBaseInfo.getId());
                            collectionRecordInfoMapper.insert(r);
                        });
                        collectionBaseInfoList.add(collectionBaseInfo);
                    }
                    // 收款核销 主表 咨询费
                    Long otherAmount = Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (otherAmount > 0) {
                        CollectionBaseInfo otherAmountCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, paymentBaseInfo, CashFlowItemEnum.OTHERAMOUNT, contractBaseInfo.getCreateBy());
                        otherAmountCbi.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        otherAmountCbi.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        otherAmountCbi.setCashFlowAmount(otherAmount);
                        otherAmountCbi.setCollectionAmount(otherAmount);
                        otherAmountCbi.setPlanCollectionAmount(otherAmount);
                        otherAmountCbi.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        otherAmountCbi.setAllRecordSort(1);
                        collectionBaseInfoMapper.insert(otherAmountCbi);
                        // 明细
                        CollectionRecordInfo recordInfo = ContractPaymentImporterHelper.buildCollectionRecord(otherAmountCbi);
                        recordInfo.setCollectionDate(otherAmountCbi.getCollectionDate());
                        recordInfo.setCollectionAmount(otherAmountCbi.getCollectionAmount());
                        collectionRecordInfoMapper.insert(recordInfo);

                        collectionBaseInfoList.add(otherAmountCbi);
                    }
                    // 收款核销 主表 名义货价
                    Long nominalPrice = Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (nominalPrice > 0) {
                        CollectionBaseInfo nominalPriceCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, paymentBaseInfo, CashFlowItemEnum.NOMINAL_PRICE, contractBaseInfo.getCreateBy());
                        nominalPriceCbi.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(5+rentRecordCount).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        nominalPriceCbi.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5+rentRecordCount).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        nominalPriceCbi.setCashFlowAmount(nominalPrice);
                        nominalPriceCbi.setCollectionAmount(nominalPrice);
                        nominalPriceCbi.setPlanCollectionAmount(nominalPrice);
                        nominalPriceCbi.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        nominalPriceCbi.setAllRecordSort(1);
                        collectionBaseInfoMapper.insert(nominalPriceCbi);
                        // 明细
                        CollectionRecordInfo recordInfo = ContractPaymentImporterHelper.buildCollectionRecord(nominalPriceCbi);
                        recordInfo.setCollectionDate(nominalPriceCbi.getCollectionDate());
                        recordInfo.setCollectionAmount(nominalPriceCbi.getCollectionAmount());
                        collectionRecordInfoMapper.insert(recordInfo);

                        collectionBaseInfoList.add(nominalPriceCbi);
                    }

                    // 保证金收款记录 主表 保证金
                    Long earnestMoney = Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (earnestMoney > 0) {
                        MarginBaseInfo marginBaseInfo = new MarginBaseInfo();
                        marginBaseInfo.setContractId(contractBaseInfo.getId());
                        marginBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                        marginBaseInfo.setMarginCode(ContractPaymentImporterHelper.getBzjCode(contractBaseInfo.getContractCode()));
                        marginBaseInfo.setClientId(contractBaseInfo.getClientId());
                        marginBaseInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        marginBaseInfo.setPlanMarginDate(marginBaseInfo.getCollectionDate());
                        marginBaseInfo.setCollectionAmount("已结清".equals(excelSimpleObj.getString("contractStatus")) ? 0L : earnestMoney);
                        marginBaseInfo.setPlanMarginAmount(earnestMoney);
                        marginBaseInfo.setContractIsSettle("已结清".equals(excelSimpleObj.getString("contractStatus")) ? 1 : 0);
                        marginBaseInfo.setBackAmount("已结清".equals(excelSimpleObj.getString("contractStatus")) ? earnestMoney : 0L);
                        marginBaseInfo.setCreateBy(contractBaseInfo.getCreateBy());
                        marginBaseInfo.setUpdateBy(contractBaseInfo.getCreateBy());
                        marginBaseInfoMapper.insert(marginBaseInfo);
                        // 明细
                        // 收款
                        MarginRecordInfo marginCollectionInfo = new MarginRecordInfo();
                        marginCollectionInfo.setMarginId(marginBaseInfo.getId());
                        marginCollectionInfo.setDataSource("3");
                        marginCollectionInfo.setSourceFlag(1);
                        marginCollectionInfo.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.name());
                        marginCollectionInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
                        marginCollectionInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        marginCollectionInfo.setCollectionAmount(earnestMoney);
                        marginCollectionInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        marginCollectionInfo.setOurAccountName("浙江浙商融资租赁有限公司");
                        marginCollectionInfo.setOurAccountNumber("33050161612700000175");
                        marginCollectionInfo.setSortId(String.valueOf(1));
                        marginCollectionInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
                        marginCollectionInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
                        marginCollectionInfo.setCreateBy(contractBaseInfo.getCreateBy());
                        marginCollectionInfo.setUpdateBy(contractBaseInfo.getCreateBy());
                        marginRecordInfoMapper.insert(marginCollectionInfo);
                        // 退款
                        if ("已结清".equals(excelSimpleObj.getString("contractStatus"))) {
                            MarginRecordInfo marginRefundInfo = new MarginRecordInfo();
                            marginRefundInfo.setMarginId(marginBaseInfo.getId());
                            marginRefundInfo.setDataSource("3");
                            marginRefundInfo.setSourceFlag(1);
                            marginRefundInfo.setCollectionType(RecordTypeEnum.REFUND_MARGIN.name());
                            marginRefundInfo.setRecordType(RecordTypeEnum.REFUND.name());
                            marginRefundInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5+rentRecordCount).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                            marginRefundInfo.setCollectionAmount(earnestMoney);
                            marginRefundInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                            marginRefundInfo.setOurAccountName("浙江浙商融资租赁有限公司");
                            marginRefundInfo.setOurAccountNumber("33050161612700000175");
                            marginRefundInfo.setSortId(String.valueOf(1));
                            marginRefundInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
                            marginRefundInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
                            marginRefundInfo.setCreateBy(contractBaseInfo.getCreateBy());
                            marginRefundInfo.setUpdateBy(contractBaseInfo.getCreateBy());
                            marginRecordInfoMapper.insert(marginRefundInfo);
                        }

                    }

                }

                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
    }

    private CollectionBaseInfo buildSpecialCollectionBaseInfo(ContractBaseInfo contractBaseInfo, PaymentBaseInfo paymentBaseInfo,
                                                              CashFlowItemEnum cashFlowItemEnum, Long createByUserId) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setContractId(contractBaseInfo.getId());
        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
        collectionBaseInfo.setClientId(contractBaseInfo.getClientId());
        collectionBaseInfo.setPaymentId(paymentBaseInfo.getId());
        collectionBaseInfo.setPaymentCode(paymentBaseInfo.getPaymentCode());
        collectionBaseInfo.setCashFlowItem(cashFlowItemEnum.name());
        collectionBaseInfo.setPhase(0);
        collectionBaseInfo.setCode(ContractPaymentImporterHelper.getCollectionCode(collectionBaseInfo.getCashFlowItem(), collectionBaseInfo.getPaymentCode(), collectionBaseInfo.getPhase(), collectionBaseInfo.getContractCode()));

        collectionBaseInfo.setCreateBy(createByUserId);
        collectionBaseInfo.setUpdateBy(createByUserId);
        return collectionBaseInfo;
    }

    public void genVersion() {
        List<String> contractCodeList = Arrays.asList("浙商租【2021】租字第(CF-0005)号","浙商租【2021】租字第(CF-0003)号","浙商租【2021】租字第(CF-0002)号","浙商租【2021】租字第(CF-0001)号");
        List<ContractBaseInfo> contractList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, contractCodeList));
        List<Long> contractIdList = contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<Long> paymentIdList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIdList))
                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());

//        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/20220831-融资租赁业务合同情况总表20220831-王传昊整理(2).xlsx"));
//        tzExcelReader.setSheet("业务合同情况汇总表");
//        List<List<Object>> contractSheetList = tzExcelReader.read();
        JSONObject excelDataObj = new JSONObject();
//        for (List<Object> rowData : contractSheetList) {
//            if (rowData.size() > 8
//                    && rowData.get(8) instanceof String
//                    && ((String) rowData.get(8)).contains("号")
//            ) {
//                // 临时处理
//                if (!"诸暨市城东新城建设有限公司1".equals(rowData.get(14))) {
//                    continue;
//                }
//
//                String actualContractCode = ((String) rowData.get(8)).trim().replaceAll("（", "(").replaceAll("）", ")");
//                JSONObject rowDataObj = new JSONObject();
//                excelDataObj.put(actualContractCode, rowDataObj);
//                rowDataObj.put("contractStatus", rowData.get(49));
//            }
//        }

        // 自然人合同导入
        JSONObject cf005 = new JSONObject();
        cf005.put("actualContractCode", "浙商租【2021】租字第(CF-0005)号");
        cf005.put("detailSheetName", "胡高山");
        cf005.put("contractStatus", "已结清");
        cf005.put("actualFinishDate", cn.hutool.core.date.DateTime.of("2022-04-15", "yyyy-MM-dd"));
        excelDataObj.put("浙商租【2021】租字第(CF-0005)号", cf005);
        JSONObject cf003 = new JSONObject();
        cf003.put("actualContractCode", "浙商租【2021】租字第(CF-0003)号");
        cf003.put("detailSheetName", "陆冰花");
        cf003.put("contractStatus", "起租");
        excelDataObj.put("浙商租【2021】租字第(CF-0003)号", cf003);
        JSONObject cf002 = new JSONObject();
        cf002.put("actualContractCode", "浙商租【2021】租字第(CF-0002)号");
        cf002.put("detailSheetName", "周昊瀚");
        cf002.put("contractStatus", "起租");
        excelDataObj.put("浙商租【2021】租字第(CF-0002)号", cf002);
        JSONObject cf001 = new JSONObject();
        cf001.put("actualContractCode", "浙商租【2021】租字第(CF-0001)号");
        cf001.put("detailSheetName", "华铅良");
        cf001.put("contractStatus", "起租");
        excelDataObj.put("浙商租【2021】租字第(CF-0001)号", cf001);

        transactionTemplate.execute(status -> {
            try {
                contractList.forEach(c -> {
                    log.info("生成合同版本数据:{}", c.getContractCode());
                    boolean settleFlag = "已结清".equals(excelDataObj.getJSONObject(c.getContractCode()).getString("contractStatus"));
                    contractService.recordContractStatus(c.getId(), settleFlag ? ContractStatus.SETTLE : ContractStatus.START_RENT, null);
                    contractVersionService.recordVersion(c.getId(), VersionTypeEnum.EFFECT, 3L, null, VersionTypeConstants.NORMAL);
                });
                paymentIdList.forEach(p -> {
                    log.info("生成付款版本数据:{}", p);
                    paymentService.recordPaymentStatus(p, RecordStatus.TAKE_EFFECT, ProcessStatus.APPROVAL_PASS);
                    paymentVersionService.recordVersion(p, VersionTypeEnum.EFFECT, 3L, null, VersionTypeConstants.NORMAL);
                });
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }});
    }
    /**
     * 清空导入的数据
     * 增量处理 数据由客户手录 不方便回滚了 务必本地测试后再上预发
     */
    public void clearData() {
//        List<String> clearContractCodeList = Arrays.asList("浙商租【2021】租字第(A-0021)号","中拓租【2019】租字第(A-0005)号","浙商租【2022】租字第(A-0053)号-HZ","浙商租【2022】租字第(A-0032)号","浙商租【2022】租字第(A-0008)号","浙商租【2022】租字第(A-0026)号","浙商租【2021】租字第(JLG-0001)号","浙商租【2022】租字第(A-0054)号","浙商租【2022】租字第(A-0030)号","浙商租【2021】租字第(A-0009)号","浙商租【2022】租字第(A-0021)号","浙商租【2022】租字第(A-0022)号","浙商租【2022】租字第(A-0041)号","浙商租【2022】租字第(A-0027)号","浙商租【2022】租字第(A-0028)号","浙商租【2022】租字第(A-0007)号","浙商租【2022】租字第(A-0033)号","中拓租【2020】租字第(A-0023)号","中拓租【2019】租字第(A-0008)号","浙商租【2022】租字第(A-0005)号","浙商租【2022】租字第(A-0006)号","浙商租【2022】租字第(A-0040)号","浙商租【2022】租字第(A-0089)号");
//        List<Long> contractIdList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, clearContractCodeList))
//                .stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
//        List<Long> paymentIdList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIdList))
//                .stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
//        List<Long> collectionIdList= collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIdList))
//                .stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
//        List<Long> marginIdList = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getContractId, contractIdList))
//                .stream().map(MarginBaseInfo::getId).collect(Collectors.toList());
//        transactionTemplate.execute(status -> {
//            try {
//                commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name()).in(CommonVersion::getMainId, contractIdList));
//                commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.PAYMENT.name()).in(CommonVersion::getMainId, paymentIdList));
//                contractBaseInfoMapper.delete(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getId, contractIdList));
//                contractBaseInfoLibMapper.delete(Wrappers.<ContractBaseInfoLib>lambdaQuery().in(ContractBaseInfoLib::getOriginId, contractIdList));
//                contractLeasePriceMapper.delete(Wrappers.<ContractLeasePrice>lambdaQuery().in(ContractLeasePrice::getContractId, contractIdList));
//                contractLeasePriceLibMapper.delete(Wrappers.<ContractLeasePriceLib>lambdaQuery().in(ContractLeasePriceLib::getContractId, contractIdList));
//                contractRentEstimateMapper.delete(Wrappers.<ContractRentEstimate>lambdaQuery().in(ContractRentEstimate::getContractId, contractIdList));
//                contractRentEstimateLibMapper.delete(Wrappers.<ContractRentEstimateLib>lambdaQuery().in(ContractRentEstimateLib::getContractId, contractIdList));
//                paymentBaseInfoMapper.delete(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getId, paymentIdList));
//                paymentBaseInfoLibMapper.delete(Wrappers.<PaymentBaseInfoLib>lambdaQuery().in(PaymentBaseInfoLib::getOriginId, paymentIdList));
//                contractReceiptMapper.delete(Wrappers.<ContractReceipt>lambdaQuery().in(ContractReceipt::getContractId, contractIdList));
//                contractReceiptLibMapper.delete(Wrappers.<ContractReceiptLib>lambdaQuery().in(ContractReceiptLib::getContractId, contractIdList));
//                contractRentActualMapper.delete(Wrappers.<ContractRentActual>lambdaQuery().in(ContractRentActual::getContractId, contractIdList));
//                contractRentActualLibMapper.delete(Wrappers.<ContractRentActualLib>lambdaQuery().in(ContractRentActualLib::getContractId, contractIdList));
//                collectionBaseInfoMapper.delete(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getId, collectionIdList));
//                collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().in(CollectionRecordInfo::getCollectionId, collectionIdList));
//                contractTenantryMapper.delete(Wrappers.<ContractTenantry>lambdaQuery().in(ContractTenantry::getContractId, contractIdList));
//                contractTenantryLibMapper.delete(Wrappers.<ContractTenantryLib>lambdaQuery().in(ContractTenantryLib::getContractId, contractIdList));
//                contractAccountMapper.delete(Wrappers.<ContractAccount>lambdaQuery().in(ContractAccount::getContractId, contractIdList));
//                contractAccountLibMapper.delete(Wrappers.<ContractAccountLib>lambdaQuery().in(ContractAccountLib::getContractId, contractIdList));
//                contractGuarantorMapper.delete(Wrappers.<ContractGuarantor>lambdaQuery().in(ContractGuarantor::getContractId, contractIdList));
//                contractGuarantorLibMapper.delete(Wrappers.<ContractGuarantorLib>lambdaQuery().in(ContractGuarantorLib::getContractId, contractIdList));
//                paymentPlanedDetailMapper.delete(Wrappers.<PaymentPlanedDetail>lambdaQuery().in(PaymentPlanedDetail::getPaymentId, paymentIdList));
//                paymentPlanedDetailLibMapper.delete(Wrappers.<PaymentPlanedDetailLib>lambdaQuery().in(PaymentPlanedDetailLib::getPaymentId, paymentIdList));
//                paymentActualDetailMapper.delete(Wrappers.<PaymentActualDetail>lambdaQuery().in(PaymentActualDetail::getPaymentId, paymentIdList));
//                marginBaseInfoMapper.delete(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getId, marginIdList));
//                marginRecordInfoMapper.delete(Wrappers.<MarginRecordInfo>lambdaQuery().in(MarginRecordInfo::getMarginId, marginIdList));
//                return true;
//            } catch (Exception e) {
//                status.setRollbackOnly();
//                log.error("事务执行出错", e);
//                throw e;
//            }});
//        log.info("删除合同id:{}", JSON.toJSONString(contractIdList));
    }

}
