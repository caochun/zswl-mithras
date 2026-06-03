package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.margin.enums.MarginWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.margin.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.others.hand.extract.ImportCommonHelper.extractNumber;

/**
 * 收款核销
 *
 * @author wangchuanhao
 * @date 2023/4/23 10:55 AM
 */
public class CollectionWriteOff extends ApplicationTest {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private MarginRecordInfoMapper marginRecordInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;

    /**
     * 导入收款核销记录
     */
    @Test
    public void importCollectionWriteOffRecord() {
        Map<String, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                        .ne(ContractBaseInfo::getContractStatus, ContractStatus.NEW.name())
                )
                .stream().collect(Collectors.toMap(ContractBaseInfo::getContractCode, c -> c));

        ExcelReader tzExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/用于导入收款核销明细的台帐2023.xlsx"));
        ExcelReader detailExcelReader = ExcelUtil.getReader(new File("/Users/wang/Desktop/用于导入收款核销明细的台帐2023.xlsx"));

        Set<String> sheetNameSet = new HashSet<>(tzExcelReader.getSheetNames());
        tzExcelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractSheetList = tzExcelReader.read();
        transactionTemplate.execute(status -> {
            try {
                for (int i = 0; i < contractSheetList.size(); i++) {
                    List<Object> rowData = contractSheetList.get(i);
                    if (rowData.size() <= 6 || !(rowData.get(6) instanceof String)) {
                        continue;
                    }
                    String curRowContractCode = ContractPaymentImporterHelper.removeSpecialChar((String) rowData.get(6));
                    if (!curRowContractCode.contains("号") || curRowContractCode.contains("系统合同编号")) {
                        continue;
                    }
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(curRowContractCode);
                    if (Objects.isNull(contractBaseInfo)) {
                        log.error("根据台帐合同编号查找系统内合同不存在: {}", curRowContractCode);
                        continue;
                    }
                    Long createByUserId = contractBaseInfo.getProjSponsorUserId();
                    // 如果有指定的就取指定的，否则取第一个
                    String targetReceiptCode = (String)rowData.get(73);
                    ContractReceipt contractReceipt = contractReceiptMapper.selectOne(Wrappers.<ContractReceipt>lambdaQuery()
                            .eq(ContractReceipt::getContractId, contractBaseInfo.getId())
                            .eq(StringUtils.isNotBlank(targetReceiptCode), ContractReceipt::getReceiptCode, targetReceiptCode)
                            .last("LIMIT 1")
                    );
                    if (Objects.isNull(contractReceipt)) {
                        log.error("根据关系查找系统内合同中借据不存在, 合同编号: {}, 借据编号: {}", curRowContractCode, targetReceiptCode);
                        continue;
                    }

                    Cell cell = tzExcelReader.getCell(5, i);
                    String detailSheetName = ContractPaymentImporterHelper.removeSpecialChar(cell.getHyperlink().getAddress()).replaceAll("'", "");
                    if (sheetNameSet.contains(detailSheetName)) {
                        log.error("根据hyperlink查找的sheet不存在: {}", detailSheetName);
                        continue;
                    }

                    detailExcelReader.setSheet(detailSheetName);
                    List<List<Object>> detailSheetDataList = detailExcelReader.read();

                    // ---------------------------- 租金数据处理 只处理明细 开始 --------------------------------
                    // 查找这个借据对应的所有收款主表 租金数据
                    List<CollectionBaseInfo> stockCollectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId())
                            .eq(CollectionBaseInfo::getReceiptId, contractReceipt.getId())
                            .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    );
                    // 清除该借据对应收款主表 所有的 收款明细
                    collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().in(CollectionRecordInfo::getCollectionId, stockCollectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList())));
                    // 找到最后一期的还款 名义价款、保证金等需要用到该期还款对应的日期
                    CollectionBaseInfo lastRentCollectionBaseInfo = stockCollectionBaseInfoList.stream().sorted(Comparator.comparing(CollectionBaseInfo::getPhase).reversed()).findFirst().orElse(null);
                    Map<Integer, CollectionBaseInfo> phaseCollectionBaseInfoMap = stockCollectionBaseInfoList.stream().collect(Collectors.toMap(CollectionBaseInfo::getPhase, c -> c));
                    for (int rentRow = 6;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }

                        int excelDataRowPhase = Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d)).map(b -> b.intValue()).orElse(null);
                        CollectionBaseInfo collectionBaseInfo = phaseCollectionBaseInfoMap.get(excelDataRowPhase);
                        List<CollectionRecordInfo> recordList = ContractPaymentImporterHelper.buildCollectionRecordList(detailSheetDataList, collectionBaseInfo, rentRow);

                        collectionBaseInfo.setAllRecordSort(recordList.size());
                        collectionBaseInfo.setCollectionAmount(recordList.isEmpty() ? 0L : LongUtil.null2zero(collectionBaseInfo.getPrincipal()) + LongUtil.null2zero(collectionBaseInfo.getInterest()) + LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
                        collectionBaseInfo.setWriteOffStatus(recordList.size() > 0 ? CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name() : CollectionWriteOffStatusEnum.UNCOLLECTION.name());
                        collectionBaseInfo.setCollectionPrincipal(recordList.stream().mapToLong(CollectionRecordInfo::getPrincipal).sum());
                        collectionBaseInfo.setCollectionInterest(recordList.stream().mapToLong(CollectionRecordInfo::getInterest).sum());
                        collectionBaseInfo.setCollectionPenaltyInterest(recordList.stream().mapToLong(CollectionRecordInfo::getPenaltyInterest).sum());
                        collectionBaseInfoMapper.updateById(collectionBaseInfo);

                        recordList.forEach(r -> {
                            r.setCollectionId(collectionBaseInfo.getId());
                            collectionRecordInfoMapper.insert(r);
                        });

                    }
                    // ---------------------------- 租金数据处理 只处理明细 结束 --------------------------------


                    // ----------------------- 非租金 其他费用数据 开始 -----------------------------
                    // 收款核销 主表 咨询费
                    // 清除已有的咨询费
                    List<Long> stockOtherCbiIdList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                    .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId()).eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.OTHERAMOUNT.name()))
                            .stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(stockOtherCbiIdList)) {
                        collectionBaseInfoMapper.deleteBatchIds(stockOtherCbiIdList);
                        collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().in(CollectionRecordInfo::getCollectionId, stockOtherCbiIdList));
                    }
                    Long otherAmount = Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (otherAmount > 0) {
                        CollectionBaseInfo otherAmountCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, contractReceipt, CashFlowItemEnum.OTHERAMOUNT, contractBaseInfo.getProjSponsorUserId());
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

                    }

                    // 清除已有的名义货价
                    List<Long> stockNominalPriceCbiIdList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                    .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId()).eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.NOMINAL_PRICE.name()))
                            .stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(stockNominalPriceCbiIdList)) {
                        collectionBaseInfoMapper.deleteBatchIds(stockNominalPriceCbiIdList);
                        collectionRecordInfoMapper.delete(Wrappers.<CollectionRecordInfo>lambdaQuery().in(CollectionRecordInfo::getCollectionId, stockNominalPriceCbiIdList));
                    }
                    // 收款核销 主表 名义货价
                    Long nominalPrice = Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (nominalPrice > 0) {
                        CollectionBaseInfo nominalPriceCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, contractReceipt, CashFlowItemEnum.NOMINAL_PRICE, createByUserId);
                        nominalPriceCbi.setPlanCollectionDate(lastRentCollectionBaseInfo.getPlanCollectionDate());
                        nominalPriceCbi.setCollectionDate(lastRentCollectionBaseInfo.getCollectionDate());
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

                    }

                    // 保证金收款记录 主表 保证金
                    // TODO 这样写单合同多借据的情况下会有问题，需要在数据库里sql处理下金额
                    Long earnestMoney = Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null);
                    if (earnestMoney > 0) {
                        MarginBaseInfo marginBaseInfo = new MarginBaseInfo();
                        marginBaseInfo.setContractId(contractBaseInfo.getId());
                        marginBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                        marginBaseInfo.setMarginCode(ContractPaymentImporterHelper.getBzjCode(contractBaseInfo.getContractCode()));
                        marginBaseInfo.setClientId(contractBaseInfo.getClientId());
                        marginBaseInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime) d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        marginBaseInfo.setPlanMarginDate(marginBaseInfo.getCollectionDate());
                        marginBaseInfo.setCollectionAmount(ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus()) ? 0L : earnestMoney);
                        marginBaseInfo.setPlanMarginAmount(earnestMoney);
                        marginBaseInfo.setContractIsSettle(ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus()) ? 1 : 0);
                        marginBaseInfo.setBackAmount(ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus()) ? earnestMoney : 0L);
                        marginBaseInfo.setCreateBy(createByUserId);
                        marginBaseInfo.setUpdateBy(createByUserId);
                        marginBaseInfoMapper.insert(marginBaseInfo);
                        // 明细
                        // 收款
                        MarginRecordInfo marginCollectionInfo = new MarginRecordInfo();
                        marginCollectionInfo.setMarginId(marginBaseInfo.getId());
                        marginCollectionInfo.setDataSource("3");
                        marginCollectionInfo.setSourceFlag(1);
                        marginCollectionInfo.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.name());
                        marginCollectionInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
                        marginCollectionInfo.setCollectionDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime) d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        marginCollectionInfo.setCollectionAmount(earnestMoney);
                        marginCollectionInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        marginCollectionInfo.setOurAccountName("浙江浙商融资租赁有限公司");
                        marginCollectionInfo.setOurAccountNumber("33050161612700000175");
                        marginCollectionInfo.setSortId(String.valueOf(1));
                        marginCollectionInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
                        marginCollectionInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
                        marginCollectionInfo.setCreateBy(createByUserId);
                        marginCollectionInfo.setUpdateBy(createByUserId);
                        marginRecordInfoMapper.insert(marginCollectionInfo);
                        // 退款
                        if (ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus())) {
                            MarginRecordInfo marginRefundInfo = new MarginRecordInfo();
                            marginRefundInfo.setMarginId(marginBaseInfo.getId());
                            marginRefundInfo.setDataSource("3");
                            marginRefundInfo.setSourceFlag(1);
                            marginRefundInfo.setCollectionType(RecordTypeEnum.REFUND_MARGIN.name());
                            marginRefundInfo.setRecordType(RecordTypeEnum.REFUND.name());
                            marginRefundInfo.setCollectionDate(lastRentCollectionBaseInfo.getCollectionDate());
                            marginRefundInfo.setCollectionAmount(earnestMoney);
                            marginRefundInfo.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                            marginRefundInfo.setOurAccountName("浙江浙商融资租赁有限公司");
                            marginRefundInfo.setOurAccountNumber("33050161612700000175");
                            marginRefundInfo.setSortId(String.valueOf(1));
                            marginRefundInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
                            marginRefundInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
                            marginRefundInfo.setCreateBy(createByUserId);
                            marginRefundInfo.setUpdateBy(createByUserId);
                            marginRecordInfoMapper.insert(marginRefundInfo);
                        }

                        // 保证金的collectionBaseInfo需要关联到付款
                        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                                .eq(PaymentBaseInfo::getReceiptId, contractReceipt.getId())
                                .eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId())
                                .last("LIMIT 1")
                        );
                        // 保证金collection_base_info
                        CollectionBaseInfo marginCbi = buildSpecialCollectionBaseInfo(contractBaseInfo, contractReceipt, CashFlowItemEnum.EARNEST_MONEY, createByUserId);
                        marginCbi.setPlanCollectionDate(marginCollectionInfo.getCollectionDate());
                        marginCbi.setCollectionDate(marginCollectionInfo.getCollectionDate());
                        marginCbi.setCashFlowAmount(earnestMoney);
                        marginCbi.setCollectionAmount(earnestMoney);
                        marginCbi.setPlanCollectionAmount(earnestMoney);
                        marginCbi.setWriteOffStatus(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
                        marginCbi.setAllRecordSort(1);
                        // 保证金不和借据关联 和付款关联
                        marginCbi.setReceiptId(null);
                        marginCbi.setReceiptCode(null);
                        marginCbi.setPaymentId(paymentBaseInfo.getId());
                        marginCbi.setPaymentCode(paymentBaseInfo.getPaymentCode());
                        collectionBaseInfoMapper.insert(marginCbi);
                        // 保证金collection_base_info 明细
                        CollectionRecordInfo recordInfo = ContractPaymentImporterHelper.buildCollectionRecord(marginCbi);
                        recordInfo.setCollectionDate(marginCbi.getCollectionDate());
                        recordInfo.setCollectionAmount(marginCbi.getCollectionAmount());
                        collectionRecordInfoMapper.insert(recordInfo);
                    }

                    // ----------------------- 非租金 其他费用数据 结束 -----------------------------

                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
    }

    private CollectionBaseInfo buildSpecialCollectionBaseInfo(ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt,
                                                              CashFlowItemEnum cashFlowItemEnum, Long createByUserId) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setContractId(contractBaseInfo.getId());
        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
        collectionBaseInfo.setClientId(contractBaseInfo.getClientId());
        collectionBaseInfo.setReceiptId(contractReceipt.getId());
        collectionBaseInfo.setReceiptCode(contractReceipt.getReceiptCode());
        collectionBaseInfo.setCashFlowItem(cashFlowItemEnum.name());
        collectionBaseInfo.setPhase(0);
        collectionBaseInfo.setCode(ContractPaymentImporterHelper.getCollectionCode(collectionBaseInfo.getCashFlowItem(), collectionBaseInfo.getPaymentCode(), collectionBaseInfo.getPhase(), collectionBaseInfo.getContractCode()));

        collectionBaseInfo.setCreateBy(createByUserId);
        collectionBaseInfo.setUpdateBy(createByUserId);
        return collectionBaseInfo;
    }

}
