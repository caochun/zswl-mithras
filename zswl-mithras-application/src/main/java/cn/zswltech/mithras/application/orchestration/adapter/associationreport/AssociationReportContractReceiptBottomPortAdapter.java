package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.zswltech.mithras.associationreport.application.AssociationReportContractReceiptBottomPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportContractReceiptBottomSnapshot;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.rating.mapper.ContractReceiptBottomMapper;
import cn.zswltech.mithras.rating.model.ContractReceiptBottom;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssociationReportContractReceiptBottomPortAdapter implements AssociationReportContractReceiptBottomPort {

    @Resource
    private ContractReceiptBottomMapper contractReceiptBottomMapper;

    @Override
    public List<AssociationReportContractReceiptBottomSnapshot> listByReportDate(LocalDate reportDate) {
        return contractReceiptBottomMapper.selectList(Wrappers.<ContractReceiptBottom>lambdaQuery()
                        .ge(BaseModel::getCreateTime, reportDate.plusDays(1).atStartOfDay())
                        .lt(BaseModel::getCreateTime, reportDate.plusDays(2).atStartOfDay()))
                .stream()
                .map(item -> AssociationReportContractReceiptBottomSnapshot.builder()
                        .contractId(item.getContractId())
                        .remainPrincipal(item.getRemainPrincipal())
                        .leaseType(item.getLeaseType())
                        .province(item.getProvince())
                        .build())
                .collect(Collectors.toList());
    }
}
