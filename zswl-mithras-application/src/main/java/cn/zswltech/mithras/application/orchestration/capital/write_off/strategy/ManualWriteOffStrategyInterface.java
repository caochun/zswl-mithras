package cn.zswltech.mithras.application.orchestration.capital.write_off.strategy;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.capital.enums.writeoff.WriteOffBusinessModelEnum;
import cn.zswltech.mithras.dto.capital.write_off.AddBusinessFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.AddFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.BankFlowCenterListBO;
import cn.zswltech.mithras.dto.capital.write_off.CheckBeforeImportREQ;
import cn.zswltech.mithras.dto.capital.write_off.CheckBeforeImportRSP;
import cn.zswltech.mithras.dto.capital.write_off.DeleteBusinessFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.DeleteFlowREQ;
import cn.zswltech.mithras.dto.capital.write_off.DeleteTabREQ;
import cn.zswltech.mithras.dto.capital.write_off.FinanceFlowMatchResultRSP;
import cn.zswltech.mithras.dto.capital.write_off.FlowMatchResultRSP;
import cn.zswltech.mithras.dto.capital.write_off.RematchTabREQ;
import cn.zswltech.mithras.dto.capital.write_off.SingleTabREQ;
import cn.zswltech.mithras.dto.capital.write_off.UpdateBusinessFlowREQ;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.capital.enums.DataSourceEnum;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowMatchResult;
import cn.zswltech.mithras.third.financialshare.persistence.model.FinanceFlowRecord;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/9/20 09:56
 * @description 手动核销的策略抽象，因为将来可能会存在其他的手动核销方式
 */
public interface ManualWriteOffStrategyInterface {

    /**
     * 提供自己对应的枚举
     */
    WriteOffBusinessModelEnum getWriteOffBusinessModel();

    /**
     * 实际的核销方法
     */
    void manualWriteOff(String batchNumber);

    /**
     * 用户选择流水后的校验
     */
    CheckBeforeImportRSP checkBeforeImport(CheckBeforeImportREQ req);

    /**
     * 排序结果
     */
    List<FlowMatchResultRSP> flowMatchResult(CheckBeforeImportREQ req);

    /**
     * 删除银行流水
     */
    void deleteBankFlow(DeleteFlowREQ req);

    /**
     * 增加银行流水
     */
    void addBankFlow(AddFlowREQ req);

    /**
     * 修改业务流水
     */
    void updateBusinessFlow(UpdateBusinessFlowREQ req);

    /**
     * 增加业务流水
     */
    void addBusinessFlow(AddBusinessFlowREQ req);

    /**
     * 删除业务流水
     */
    void deleteBusinessFlow(DeleteBusinessFlowREQ req);

    /**
     * 重新匹配单个Tab内的信息
     */
    void reMatch(RematchTabREQ req);

    /**
     * 删除单个Tab
     */
    void deleteTab(DeleteTabREQ req);


    /**
     * 查询单个Tab
     */
    FlowMatchResultRSP singleTab(SingleTabREQ req);

    /**
     * 生成批次号
     */
    default String generateBatchNumber() {
        // 默认使用当前时间
        return LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
    }

    default List<BankFlowCenterListBO> buildBankFlowCenterListNoticeBoList(List<FinanceFlowRecord> bankFlowList) {
        if (bankFlowList == null || bankFlowList.isEmpty()) {
            return Collections.emptyList();
        }
        List<BankFlowCenterListBO> result = new LinkedList<>();
        bankFlowList.stream().collect(Collectors.groupingBy(o -> o.getOppunit() + o.getOppbanknumber()))
                .forEach((key, value) -> {
                    BankFlowCenterListBO rsp = getListBO(value.get(0));
                    result.add(rsp);
                });
        return result;
    }

    default List<BankFlowCenterListBO> buildBankFlowCenterListViewBoList(List<FinanceFlowRecord> bankFlowList) {
        if (bankFlowList == null || bankFlowList.isEmpty()) {
            return Collections.emptyList();
        }
        List<BankFlowCenterListBO> result = new LinkedList<>();
        bankFlowList.forEach(value -> {
            BankFlowCenterListBO rsp = getListBO(value);
            result.add(rsp);
        });
        return result;
    }

    static BankFlowCenterListBO getListBO(FinanceFlowRecord dto) {
        BankFlowCenterListBO rsp = new BankFlowCenterListBO();
        rsp.setId(dto.getId());
        rsp.setTransactionDetailsNumber(dto.getBillno());
        rsp.setFinancialOrganization(dto.getCompanyName());
        rsp.setBankAccount(dto.getAccountbankBankaccountnumber());
        rsp.setBankName(dto.getBankName());
        rsp.setCurrency(dto.getCurrencyName());
        rsp.setTransactionDate(LocalDateTimeUtil.format(dto.getBiztime(), DatePattern.NORM_DATETIME_PATTERN));
        rsp.setMainInfo(dto.getDescription());
        rsp.setCollectionAmount(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getCreditamount()).orElse(0.00))));
        rsp.setPaymentAmount(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getDebitamount()).orElse(0.00))));
        rsp.setDepositAmount(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getTransbalance()).orElse(0.00))));
        rsp.setHandingFees(LongUtil.other2Long(String.valueOf(Optional.ofNullable(dto.getTransfercharge()).orElse(0.00))));
        rsp.setOtherName(dto.getOppunit());
        rsp.setOtherBankAccount(dto.getOppbanknumber());
        rsp.setOtherBankName(dto.getOppbank());
        rsp.setDetailSerialNumber(dto.getDetailid());
        rsp.setDataSource(Optional.ofNullable(DataSourceEnum.of(dto.getDatasource())).map(DataSourceEnum::getDisplay).orElse(null));
        if (Objects.nonNull(dto.getLastmodifytime())) {
            rsp.setUpdateTime(dto.getLastmodifytime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        }
        rsp.setPromptLabel(String.valueOf(dto.getLogicDeleteFlag()));
        rsp.setFlowStatus(dto.getWriteOffStatus());
        if (dto.getCreditamount() > 0) {
            rsp.setSurplusAmount(Objects.nonNull(dto.getSurplusAmount()) ? dto.getSurplusAmount() : LongUtil.other2Long(String.valueOf(dto.getCreditamount())));
        } else {
            rsp.setSurplusAmount(Objects.nonNull(dto.getSurplusAmount()) ? dto.getSurplusAmount() : LongUtil.other2Long(String.valueOf(dto.getDebitamount())));
        }
        rsp.setCicoBruid(dto.getCicoBruid());
        return rsp;
    }

    default List<FinanceFlowMatchResultRSP> buildBusinessMatchRsp(List<FinanceFlowMatchResult> financeFlowMatchResults) {
        return financeFlowMatchResults.stream().map(dto ->
                        FinanceFlowMatchResultRSP.builder()
                                .id(dto.getId())
                                .sourceId(dto.getSourceId())
                                .clientId(dto.getClientId())
                                .sourceBusinessCode(dto.getSourceBusinessCode())
                                .cashFlowItem(dto.getCashFlowItem())
                                .cashFlowCode(dto.getCashFlowCode())
                                .shouldWriteOffTime(dto.getShouldWriteOffTime())
                                .shouldWriteOffAmount(dto.getShouldWriteOffAmount())
                                .noWriteOffAmount(dto.getNoWriteOffAmount())
                                .thisWriteOffAmount(dto.getThisWriteOffAmount())
                                .isSystemGenerate(dto.getIsSystemGenerate())
                                .businessModel(dto.getBusinessModel())
                                .build())
                .collect(Collectors.toList());
    }
}
