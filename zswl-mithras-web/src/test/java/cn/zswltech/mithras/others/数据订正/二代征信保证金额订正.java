package cn.zswltech.mithras.others.数据订正;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.base.model.CrGuarantorBase;
import cn.zswltech.mithras.report.mapper.formal.model.CrGuarantor;
import cn.zswltech.mithras.report.service.formal.CrGuarantorService;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;
import cn.zswltech.mithras.contract.versioning.application.ContractGuarantorLibService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/30 11:21
 */
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("dev")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 二代征信保证金额订正 {

    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private CrGuarantorService crGuarantorService;

    @Test
    public void updateGuaranteeAmount() {
        //查询征信库中保证表中已报送的保证记录，按合同分组
        Map<Long, List<CrGuarantor>> groupByContract = crGuarantorService.list(
                        Wrappers.<CrGuarantor>lambdaQuery().isNotNull(CrGuarantorBase::getRepayLiabilityAmount))
                .stream().collect(Collectors.groupingBy(CrBaseModel::getContractId));
        Set<Long> contractIds = groupByContract.keySet();
        // 查询业务库中已报送征信的的合同对应的保证记录，按合同分组
        Map<Long, List<ContractGuarantor>> guarantorMap = contractGuarantorLibService.newestList(contractIds)
                .stream().collect(Collectors.groupingBy(ContractGuarantor::getContractId));

        List<CrGuarantor> toBeUpdate = new ArrayList<>();
        // 遍历所有已报送的合同
        for (Long contractId : contractIds) {
            //取到该合同已报送的征信保证记录
            List<CrGuarantor> crGuarantors = groupByContract.getOrDefault(contractId, new ArrayList<>());
            // 取到该合同的保证记录
            List<ContractGuarantor> guarantors = guarantorMap.getOrDefault(contractId, new ArrayList<>());
            // 合同保证记录转成clientId的map， 与上面的map应该是一对一的关系
            Map<Long, Long> guaranteeAmount = new HashMap<>();
            // 以下逻辑从合同的保证表中获取每个客户的保证金额
            for (ContractGuarantor guarantor : guarantors) {
                List<Long> guarantorIds = JSON.parseArray(guarantor.getGuarantorIds(), Long.class);
                if (guarantor.getGuaranteeAmountSingle() != null) {
                    guarantorIds.forEach(id -> guaranteeAmount.put(id, guarantor.getGuaranteeAmountSingle()));
                } else if (guarantor.getGuaranteeAmountMultiple() != null) {
                    List<ContractGuarantor.GuaranteeMultipleJsonWrapper> guaranteeMultipleJsonWrappers = JSON.parseArray(guarantor.getGuaranteeAmountMultiple(), ContractGuarantor.GuaranteeMultipleJsonWrapper.class);
                    guaranteeMultipleJsonWrappers.forEach(wrapper -> guaranteeAmount.put(wrapper.getClientId(), wrapper.getAmount()));
                }
            }
            //判断征信表中的金额是否需要更新
            crGuarantors.forEach(crGuarantor -> {
                Long correctValue = guaranteeAmount.getOrDefault(crGuarantor.getClientId(), 0L);
                if (!Objects.equals(correctValue, crGuarantor.getRepayLiabilityAmount())) {
                    crGuarantor.setRepayLiabilityAmount(correctValue);
                    toBeUpdate.add(crGuarantor);
                }
            });
        }
        // 更新征信表
        if (ObjectUtil.isNotEmpty(toBeUpdate)) {
            crGuarantorService.updateBatchById(toBeUpdate);
        }
    }
}
