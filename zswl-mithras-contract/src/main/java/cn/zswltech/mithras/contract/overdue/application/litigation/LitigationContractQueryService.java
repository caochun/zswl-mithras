package cn.zswltech.mithras.contract.overdue.application.litigation;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ClientOverdueInfoDto;
import cn.zswltech.mithras.contract.overdue.domain.acl.ClientRole;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractClientInfo;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.foundation.port.ClientRiskExposureResolver;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LitigationContractQueryService {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientRiskExposureResolver clientRiskExposureResolver;

    public Map<Long, String> contractPulldown(Long clientId) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.listByClientIds(clientId);
        return contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getContractCode));
    }

    public List<ContractClientInfo> contractClient(Set<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        List<ClientRole> clientRoles = contractBaseInfoMapper.listClientsByContractId(contractIds);
        List<ContractClientInfo> res = new ArrayList<>();
        for (ClientRole clientRole : clientRoles) {
            if ("guarantor".equals(clientRole.getRole())) {
                List<Long> guarantorIds = JSON.parseArray(clientRole.getClientId(), Long.class);
                for (Long guarantorId : guarantorIds) {
                    ContractClientInfo contractClientInfo = new ContractClientInfo();
                    contractClientInfo.setClientId(guarantorId);
                    contractClientInfo.setRole(clientRole.getRole());
                    contractClientInfo.setContractId(clientRole.getContractId());
                    res.add(contractClientInfo);
                }
            } else {
                ContractClientInfo contractClientInfo = new ContractClientInfo();
                contractClientInfo.setClientId(Long.valueOf(clientRole.getClientId()));
                contractClientInfo.setRole(clientRole.getRole());
                contractClientInfo.setContractId(clientRole.getContractId());
                res.add(contractClientInfo);
            }
        }
        if (ObjectUtil.isEmpty(res)) {
            return Collections.emptyList();
        }
        Set<Long> allClientIds = res.stream().map(ContractClientInfo::getClientId).collect(Collectors.toSet());
        Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, allClientIds))
                .stream()
                .collect(Collectors.toMap(Client::getId, v -> v));
        for (ContractClientInfo re : res) {
            Client client = clientMap.get(re.getClientId());
            if (ObjectUtil.isEmpty(client)) {
                continue;
            }
            re.setName(client.getClientName());
            if (client.getClientType().equals(ClientType.NORMAL.name())) {
                re.setCertificateType("身份证");
                re.setCertificateNumber(client.getCertNumber());
            } else {
                re.setCertificateType("统一社会信用代码");
                re.setCertificateNumber(client.getUscCode());
            }
        }
        Map<Long, List<ContractClientInfo>> resMap = res.stream().collect(Collectors.groupingBy(ContractClientInfo::getClientId));
        List<ContractClientInfo> resList = new ArrayList<>();
        resMap.forEach((key, value) -> {
            Map<String, List<ContractClientInfo>> roleMap = value.stream().collect(Collectors.groupingBy(ContractClientInfo::getRole));
            ContractClientInfo origin = value.get(0);
            ContractClientInfo contractClientInfo = new ContractClientInfo();
            contractClientInfo.setClientId(origin.getClientId());
            contractClientInfo.setName(origin.getName());
            contractClientInfo.setCertificateType(origin.getCertificateType());
            contractClientInfo.setCertificateNumber(origin.getCertificateNumber());
            if (roleMap.size() > 1) {
                contractClientInfo.setRole("承租人/担保人");
            } else {
                contractClientInfo.setRole("lessee".equals(origin.getRole()) ? "承租人" : "担保人");
            }
            resList.add(contractClientInfo);
        });
        return resList;
    }

    public ClientOverdueInfoDto clientOverdueInfo(Long clientId) {
        ClientOverdueInfoDto clientOverdueInfoDto = contractBaseInfoMapper.clientOverdueInfo(clientId);
        if (ObjectUtil.isEmpty(clientOverdueInfoDto)) {
            clientOverdueInfoDto = new ClientOverdueInfoDto();
        }
        Map<Long, Long> riskExposureMap = clientRiskExposureResolver.clientStockRiskExposureMap(Collections.singletonList(clientId));
        clientOverdueInfoDto.setRiskExposure(riskExposureMap.getOrDefault(clientId, 0L));
        return clientOverdueInfoDto;
    }
}
