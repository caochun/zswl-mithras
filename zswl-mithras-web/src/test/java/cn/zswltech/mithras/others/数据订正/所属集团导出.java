package cn.zswltech.mithras.others.数据订正;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum.MAIN_LESSSEE;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author yibin
 * 导出存续里项目的承租人和联合承租人的所属集团
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 所属集团导出 {

    /**
     * 导出评审和合同承租人不一致的情况
     */
    @Test
    public void diff() {
        StringBuilder builder = new StringBuilder();
        builder.append("项目名称").append(",")
                .append("项目承租人").append(",")
                .append("合同名称").append(",")
                .append("合同承租人").append(System.lineSeparator());
        List<ProjReviewBaseInfo> infos = getBean(ProjReviewBaseInfoService.class).listByIds(ListUtil.of(522, 541, 542, 556, 558, 560, 567, 580, 583, 625, 631, 642, 650, 666, 679, 683, 706, 722, 723, 724, 728, 746, 774, 776, 778, 779, 785, 786, 787, 803, 811, 826, 833, 834, 838));
        for (ProjReviewBaseInfo info : infos) {
            //
            String b = getBean(ClientService.class).getById(info.getClientId()).getClientName();

            //
            List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class)
                    .list(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.CLOSED.name(), ContractStatus.INVALID.name())
                            .eq(ContractBaseInfo::getProjReviewId, info.getId()));
            for (ContractBaseInfo baseInfo : contractList) {

                List<ContractTenantry> tenantryList = getBean(ContractTenantryService.class).list(
                        Wrappers.<ContractTenantry>lambdaQuery()
                                .in(ContractTenantry::getLesseeType, MAIN_LESSSEE.name(), CreditorDebtorTypeEnum.CREDITOR.name())
                                .eq(ContractTenantry::getContractId, baseInfo.getId()));
                String a = "";
                if (null != tenantryList) {
                    a = getBean(ClientService.class).listByIds(tenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList())).stream()
                            .map(Client::getClientName).collect(Collectors.joining("，"));
                }
                if (!StrUtil.equals(a, b)) {
                    builder.append(info.getProjName()).append(",")
                            .append(b).append(",")
                            .append(baseInfo.getContractCode()).append(",")
                            .append(a).append(System.lineSeparator());
                }
            }
            //
        }
        System.out.println(builder);
    }

    @Test
    public void export() {
        List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.CLOSED.name(), ContractStatus.INVALID.name())
        );
        Map<Long, List<ContractBaseInfo>> projReviewContractMap = contractList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, List<ContractTenantry>> contractTenantryMap = getBean(ContractTenantryService.class)
                .listByContractIds(contractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(ContractTenantry::getContractId));
        Map<Long, ProjReviewBaseInfo> projReviewMap = getBean(ProjReviewBaseInfoService.class).listByIds(projReviewContractMap.keySet()).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e));
        Set<Long> clientIdSet = new HashSet<>();
        Set<Long> diffProjReviewIdSet = new TreeSet<>();
        projReviewMap.values().forEach(e -> {
            Set<Long> idSet = new TreeSet<>();
            Long projReviewId = e.getId();
            idSet.addAll(StrUtil.isEmpty(e.getLesseeInfo()) ? ListUtil.empty() : JSONUtil.toList(e.getLesseeInfo(), ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
            idSet.addAll(StrUtil.isEmpty(e.getCreditorInfo()) ? ListUtil.empty() : JSONUtil.toList(e.getCreditorInfo(), ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
            //
            Set<Long> cIdSet = new TreeSet<>();
            List<ContractBaseInfo> infos = projReviewContractMap.get(projReviewId);
            for (ContractBaseInfo info : infos) {
                List<ContractTenantry> tenantryList = contractTenantryMap.get(info.getId());
                if (!CollUtil.isEmpty(tenantryList)) {
                    cIdSet.addAll(tenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList()));
                }
            }
            if (!JSONUtil.toJsonStr(idSet).equals(JSONUtil.toJsonStr(cIdSet))) {
                diffProjReviewIdSet.add(projReviewId);
                System.out.println("合同和评审的承租人不一样；projReviewId:" + projReviewId);
            }
            clientIdSet.addAll(idSet);
        });
        contractTenantryMap.values().forEach(e -> {
            clientIdSet.addAll(e.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList()));
        });

        Map<Long, Client> clientMap = getBean(ClientService.class).listByIds(clientIdSet).stream().collect(Collectors.toMap(Client::getId, e -> e));
        Map<Long, Long> clientGroupMap = getBean(CorpCommerceInfoMapper.class)
                .selectList(Wrappers.<CorpCommerceInfo>lambdaQuery().in(CorpCommerceInfo::getClientId, clientIdSet)).stream()
                .collect(HashMap::new, (newMap, entity) -> newMap.put(entity.getClientId(), entity.getBelongGroupClientId()), HashMap::putAll);
        Map<Long, String> groupNameMap = getBean(ClientService.class).listByIds(clientGroupMap.values()).stream().collect(Collectors.toMap(Client::getId, Client::getClientName));
        //
        StringBuilder builder = new StringBuilder();
        builder.append("项目名称").append(",")
                .append("承租人名称").append(",")
                .append("所属集团").append(System.lineSeparator());
        for (Long projReviewId : projReviewContractMap.keySet()) {
            ProjReviewBaseInfo baseInfo = projReviewMap.get(projReviewId);
            Set<Long> idSet = new TreeSet<>();
            idSet.addAll(StrUtil.isEmpty(baseInfo.getLesseeInfo()) ? ListUtil.empty() : JSONUtil.toList(baseInfo.getLesseeInfo(), ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
            idSet.addAll(StrUtil.isEmpty(baseInfo.getCreditorInfo()) ? ListUtil.empty() : JSONUtil.toList(baseInfo.getCreditorInfo(), ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
            if (idSet.isEmpty()) {
                System.out.println("XXXX:没有承租人：" + projReviewId);
            }
            for (Long aLong : idSet) {
                builder.append(baseInfo.getProjName()).append(",")
                        .append(clientMap.get(aLong).getClientName()).append(",")
                        .append(groupNameMap.get(clientGroupMap.get(aLong)))
                        .append(System.lineSeparator());
            }
        }
        System.out.println(JSONUtil.toJsonStr(diffProjReviewIdSet));
        System.out.println(builder);
    }
}

