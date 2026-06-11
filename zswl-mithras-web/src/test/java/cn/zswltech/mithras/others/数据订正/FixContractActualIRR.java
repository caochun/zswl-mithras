package cn.zswltech.mithras.others.数据订正;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.application.orchestration.contract.ContractRentActualService;
import cn.zswltech.mithras.contract.archive.service.ContractReceiptLibService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/8/3
 * @description
 */
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FixContractActualIRR {
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Resource
    private ContractRentActualService contractRentActualService;

    @Test
    public void fixContractActualIrr() {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name())));
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return;
        }
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(contractReceiptList)) {
                continue;
            }
            for (ContractReceipt contractReceipt : contractReceiptList) {
                if (Objects.nonNull(contractReceipt.getActualIrr())) {
                    continue;
                }
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        IRRCalculateResultRSP rsp = contractRentActualService.calculateIRR(contractReceipt.getId(), false);
                        if (StrUtil.isNotBlank(rsp.getIrr())) {
                            int irr = new BigDecimal(rsp.getIrr()).multiply(BigDecimal.valueOf(1000000)).intValue();
                            contractReceipt.setActualIrr(irr);
                            contractReceiptService.updateById(contractReceipt);
                            // 如果有版本表则更新版本表
                            ContractReceiptLib contractReceiptLib = contractReceiptLibService.getOne(
                                    Wrappers.<ContractReceiptLib>lambdaQuery()
                                            .eq(ContractReceiptLib::getOriginId, contractReceipt.getId())
                                            .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
                                            .orderByDesc(ContractReceiptLib::getVersion)
                                            .last(StringUtil.mysqlLimitOne()));
                            if (Objects.nonNull(contractReceiptLib)) {
                                contractReceiptLib.setActualIrr(irr);
                                contractReceiptLibService.updateById(contractReceiptLib);
                            }
                        }
                    } catch (MithrasException e) {
                        // ignore
                    } catch (Exception e) {
                        transactionStatus.setRollbackOnly();
                        log.error("更新借据<{}>的实际IRR异常", contractReceipt.getReceiptCode(), e);
                    }
                });
            }
        }
    }
}
