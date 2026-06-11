package cn.zswltech.mithras.others.hand.extract.contract.step2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.application.orchestration.contract.ContractRentActualService;
import cn.zswltech.mithras.contract.archive.service.ContractRentActualLibService;
import cn.zswltech.mithras.foundation.util.VersionUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author yibin
 */
//@RunWith(SpringRunner.class)
@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("pre")
public class ContractReceiptImporter {

    @Test
    public void test() {
        List<ContractRentActual> list = getBean(ContractRentActualMapper.class)
                .selectList(Wrappers.<ContractRentActual>lambdaQuery()
                        .ge(ContractRentActual::getCreateTime, LocalDateTimeUtil.parse("2023-04-10 16:00:04", "yyyy-MM-dd HH:mm:ss"))
                        .notIn(ContractRentActual::getContractId, 1175, 1168, 1119, 1048));
        Map<Long, List<ContractRentActual>> map = list.stream().collect(Collectors.groupingBy(ContractRentActual::getContractId));
        for (Long contractId : map.keySet()) {
            List<PaymentBaseInfo> infos = getBean(PaymentBaseInfoMapper.class).selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getPaymentStatus, "TAKE_EFFECT")
                    .eq(PaymentBaseInfo::getContractId, contractId));
            if (infos.size() != 1) {
                System.out.println("XXXX" + contractId);
                continue;
            }
            String paymentCode = infos.get(0).getPaymentCode();
            //删除合同借据并插入
            getBean(ContractReceiptMapper.class).delete(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getContractId, contractId));
            ContractReceipt contractReceipt = new ContractReceipt();
            contractReceipt.setContractId(contractId);
            contractReceipt.setPaymentApplyCode(paymentCode);
            getBean(ContractReceiptMapper.class).insert(contractReceipt);

            //借据版本表
            getBean(ContractReceiptLibMapper.class).delete(Wrappers.<ContractReceiptLib>lambdaQuery().eq(ContractReceiptLib::getContractId, contractId));
            ContractReceiptLib receiptLib = BeanUtil.copyProperties(contractReceipt, ContractReceiptLib.class);
            receiptLib.setVersion(VersionUtil.generateVersion(null));
            receiptLib.setVersionType(1);
            receiptLib.setOriginId(contractReceipt.getId());
            getBean(ContractReceiptMapper.class).insert(contractReceipt);


            List<ContractRentActual> rentActualList = map.get(contractId);
            List<ContractRentActualLib> libList = new ArrayList<>(rentActualList.size());
//            getBean(ContractRentActualMapper.class).delete(Wrappers.<ContractRentActual>lambdaQuery().eq(ContractRentActual::getContractId, contractId));
            getBean(ContractRentActualLibMapper.class).delete(Wrappers.<ContractRentActualLib>lambdaQuery().eq(ContractRentActualLib::getContractId, contractId));
            for (ContractRentActual contractRentActual : rentActualList) {
                contractRentActual.setReceiptId(contractReceipt.getId());
                contractRentActual.setCashFlowCode(paymentCode + "-" + (contractRentActual.getCashFlowPhase() > 9 ? "0" + contractRentActual.getCashFlowPhase() : "00" + contractRentActual.getCashFlowPhase()));
                ContractRentActualLib rentLib = BeanUtil.copyProperties(contractRentActual, ContractRentActualLib.class);
                rentLib.setVersion(VersionUtil.generateVersion(null));
                rentLib.setVersionType(1);
                rentLib.setOriginId(contractRentActual.getId());
                libList.add(rentLib);
            }
            getBean(ContractRentActualService.class).updateBatchById(rentActualList);
            getBean(ContractRentActualLibService.class).saveBatch(libList);
        }
        System.out.println(666);
    }
}
