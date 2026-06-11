package cn.zswltech.mithras.contract.adapter.job;

import cn.zswltech.mithras.contract.job.service.ContractDataInitJobService;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.contract.versioning.application.ContractReceiptLibService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/8/27/10:44
 * @description
 */
@Slf4j
@Component
public class ContractDataInitJobServiceImpl implements ContractDataInitJobService {

    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;

    @Override
    public void initReceiptStartDate() {
        boolean lock = redisDistLock.tryLockWithoutReleaseTime("initReceiptStartDate", 10000);
        try {
            if (lock) {
                List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list();
                StringBuilder errorContractCode = new StringBuilder();
                Map<Long, List<ContractReceipt>> contractIdReceiptListMap = contractReceiptService.list().stream().collect(Collectors.groupingBy(ContractReceipt::getContractId));
                for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                    List<ContractReceipt> receiptList = contractIdReceiptListMap.get(contractBaseInfo.getId());
                    if (receiptList != null && !receiptList.isEmpty() && receiptList.size() > 1) {
                        log.error("合同编号:{}存在多借据，实际起租日期初始化失败", contractBaseInfo.getContractCode());
                        errorContractCode.append(contractBaseInfo.getContractCode()).append(",");
                        continue;
                    }
                    if (receiptList != null && !receiptList.isEmpty()) {
                        ContractReceipt contractReceipt = receiptList.get(0);
                        contractReceipt.setReceiptStartDate(contractBaseInfo.getActualLeaseDate());
                        contractReceiptService.lambdaUpdate()
                                .set(ContractReceipt::getReceiptStartDate, contractBaseInfo.getActualLeaseDate())
                                .eq(ContractReceipt::getId, contractReceipt.getId())
                                .update();
                        contractReceiptLibService.lambdaUpdate()
                                .eq(ContractReceiptLib::getOriginId, contractReceipt.getId())
                                .set(ContractReceiptLib::getReceiptStartDate, contractBaseInfo.getActualLeaseDate())
                                .update();
                        log.info("合同编号:{}实际起租日期初始化成功", contractBaseInfo.getContractCode());
                    } else {
                        log.error("合同编号:{}不存在借据，实际起租日期初始化失败", contractBaseInfo.getContractCode());
                        errorContractCode.append(contractBaseInfo.getContractCode()).append(",");
                    }
                }
                log.info("实际起租日期初始化失败的ERR-MSG:{}", errorContractCode);
            }
        } finally {
            if (lock) {
                redisDistLock.unlock("initReceiptStartDate");
            }
            log.info("initReceiptStartDate执行完成");
        }
    }
}
