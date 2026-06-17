package cn.zswltech.mithras.application.orchestration.adapter.creditreport;

import cn.zswltech.mithras.creditreport.service.CreditReportPaymentPort;
import cn.zswltech.mithras.creditreport.service.CreditReportPaymentProjectSnapshot;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractTradeStructureMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTradeStructure;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class CreditReportPaymentPortAdapter implements CreditReportPaymentPort {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractTradeStructureMapper contractTradeStructureMapper;

    @Override
    public Long getContractIdByPaymentId(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        return paymentBaseInfo == null ? null : paymentBaseInfo.getContractId();
    }

    @Override
    public CreditReportPaymentProjectSnapshot getProjectSnapshotByPaymentId(Long paymentId) {
        Long contractId = getContractIdByPaymentId(paymentId);
        if (contractId == null) {
            return null;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        CreditReportPaymentProjectSnapshot snapshot = new CreditReportPaymentProjectSnapshot();
        snapshot.setContractId(contractId);
        if (contractBaseInfo == null) {
            snapshot.setClientIds(Collections.emptyList());
            return snapshot;
        }
        snapshot.setProjCode(contractBaseInfo.getProjCode());
        snapshot.setProjName(contractBaseInfo.getProjName());
        snapshot.setClientIds(contractTradeStructureMapper.selectList(Wrappers.<ContractTradeStructure>lambdaQuery()
                        .eq(ContractTradeStructure::getContractId, contractBaseInfo.getId()))
                .stream()
                .map(ContractTradeStructure::getClientId)
                .collect(Collectors.toList()));
        return snapshot;
    }
}
