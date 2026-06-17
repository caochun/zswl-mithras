package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.collection.application.job.CollectionRentDueContractInfo;
import cn.zswltech.mithras.collection.application.job.CollectionRentDueContractInfoPort;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component
public class CollectionRentDueContractInfoPortAdapter implements CollectionRentDueContractInfoPort {

    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;

    @Override
    public CollectionRentDueContractInfo getLatestContractInfo(Long contractId) {
        ContractBaseInfoLib contract = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractId);
        if (contract == null) {
            return null;
        }
        CollectionRentDueContractInfo info = new CollectionRentDueContractInfo();
        info.setClientId(contract.getClientId());
        info.setContractCode(contract.getContractCode());
        info.setProjSponsorUserId(contract.getProjSponsorUserId());
        info.setProjCosponsorUserIds(parseUserIds(contract.getProjCosponsorUserIds()));
        info.setBizDeptLeaderId(contract.getBizDeptLeaderId());
        return info;
    }

    private List<Long> parseUserIds(String userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(userIds, Long.class);
    }
}
