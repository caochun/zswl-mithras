package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.collection.application.contractcp.port.ContractCollectionMarginInfo;
import cn.zswltech.mithras.collection.application.contractcp.port.ContractCollectionMarginPort;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractCollectionMarginPortAdapter implements ContractCollectionMarginPort {

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public ContractCollectionMarginInfo getByContractId(Long contractId) {
        MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(
                Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, contractId));
        return toInfo(marginBaseInfo);
    }

    @Override
    public List<ContractCollectionMarginInfo> listByContractId(Long contractId) {
        return marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .eq(MarginBaseInfo::getContractId, contractId)).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractCollectionMarginInfo> listByMarginCodes(Collection<String> marginCodes) {
        if (CollectionUtil.isEmpty(marginCodes)) {
            return Collections.emptyList();
        }
        return marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .in(MarginBaseInfo::getMarginCode, marginCodes)).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractCollectionMarginInfo> listByContractIds(Collection<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .in(MarginBaseInfo::getContractId, contractIds)).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    private ContractCollectionMarginInfo toInfo(MarginBaseInfo marginBaseInfo) {
        if (marginBaseInfo == null) {
            return null;
        }
        ContractCollectionMarginInfo info = new ContractCollectionMarginInfo();
        info.setId(marginBaseInfo.getId());
        info.setContractId(marginBaseInfo.getContractId());
        info.setMarginCode(marginBaseInfo.getMarginCode());
        info.setCollectionAmount(marginBaseInfo.getCollectionAmount());
        info.setPlanMarginAmount(marginBaseInfo.getPlanMarginAmount());
        info.setPlanMarginDate(marginBaseInfo.getPlanMarginDate());
        return info;
    }
}
