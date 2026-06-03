package cn.zswltech.mithras.others.数据订正;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yibin
 * 改订正用于
 * 部门修改了名称，或者是合并了部门改了名称，之前的部门已不再使用
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 合同导数据 {

    /**
     * 需要拉一份承租人、联合承租人、债权人、债务人、担保人的清单
     */
    @Test
    public void test2() {
        List<String> contractCodeList = ListUtil.of("中拓租【2019】租字第(A-0015)号",
                "中拓租【2020】租字第(A-0009)号",
                "浙商租【2021】租字第(A-0011)号",
                "浙商租【2021】租字第(A-0024)号",
                "浙商租【2021】租字第(A-0026)号",
                "浙商租【2021】租字第(A-0041)号",
                "浙商租【2021】租字第(A-0060)号",
                "浙商租【2021】租字第(A-0067)号",
                "浙商租【2022】租字第(C-0008)号)"
        );
        StringBuilder builder = new StringBuilder();
        List<ContractBaseInfo> list = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractCode, contractCodeList));
        for (ContractBaseInfo baseInfo : list) {
            List<ContractTenantry> tenantries = getBean(ContractTenantryService.class).listByContractId(baseInfo.getId());
            StringBuilder ts = new StringBuilder();
            for (ContractTenantry tenantry : tenantries) {
                Client client = getBean(ClientService.class).getById(tenantry.getLesseeId());
                ts.append(tenantry.getLesseeType()).append("：").append(client.getClientName()).append("，");
            }
            //
            List<ContractGuarantor> guarantorList = getBean(ContractGuarantorService.class).listByContractId(baseInfo.getId());
            Set<Long> ids = new HashSet<>();
            String guaList = null;
            for (ContractGuarantor contractGuarantor : guarantorList) {
                ids.addAll(JSONUtil.toBean(contractGuarantor.getGuarantorIds(), new TypeReference<List<Long>>() {
                }, true));
            }
            if (!ids.isEmpty()) {
                guaList = getBean(ClientService.class).listByIds(ids).stream().map(Client::getClientName).collect(Collectors.joining("，"));
            }
            builder.append(baseInfo.getContractCode())
                    .append(",").append(ts.toString())
                    .append(",").append(guaList);
            builder.append(System.lineSeparator());

        }
        System.out.println(builder);
        //

    }

    /**
     * 需要拉一张表，合同编号   承租人（如果有多个承租人，就用；隔开）、担保人、合同金额、剩余本金。
     */
    @Test
    public void test() {
        List<Long> contractIdList = getBean(PaymentBaseInfoService.class).list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getPaymentStatus, "TAKE_EFFECT")
        ).stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toList());

        List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractIdList)
                .notIn(ContractBaseInfo::getContractStatus, "INVALID", "CLOSED")
                .le(ContractBaseInfo::getCreateTime, LocalDateTime.of(2023, 4, 30, 23, 59, 59))
        );
        StringBuilder builder = new StringBuilder();
        for (ContractBaseInfo contractBaseInfo : contractList) {
            List<ContractTenantry> tenantryList = getBean(ContractTenantryService.class).list(Wrappers.<ContractTenantry>lambdaQuery()
                    .eq(ContractTenantry::getContractId, contractBaseInfo.getId())
            );
            List<Client> clients = getBean(ClientService.class).listByIds(tenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList()));
            String czrList = clients.stream().map(Client::getClientName).collect(Collectors.joining("，"));
            List<ContractGuarantor> guarantorList = getBean(ContractGuarantorService.class).list(Wrappers.<ContractGuarantor>lambdaQuery().eq(ContractGuarantor::getContractId, contractBaseInfo.getId()));
            Set<Long> ids = new HashSet<>();
            String guaList = null;
            for (ContractGuarantor contractGuarantor : guarantorList) {
                ids.addAll(JSONUtil.toBean(contractGuarantor.getGuarantorIds(), new TypeReference<List<Long>>() {
                }, true));
            }
            if (!ids.isEmpty()) {
                guaList = getBean(ClientService.class).listByIds(ids).stream().map(Client::getClientName).collect(Collectors.joining("，"));
            } else {
                System.out.println(55);
            }
            //

            String bizType = contractBaseInfo.getBizType();
            Long amount = null;
            if (StrUtil.equalsAny(bizType, "ZL", "ZZ")) {
                amount = getBean(ContractLeasePriceService.class).getByContractId(contractBaseInfo.getId()).getApplyCreditAmount();
            } else if (StrUtil.equals(bizType, "BL")) {
                amount = getBean(ContractFactoringPriceService.class).getByContractId(contractBaseInfo.getId()).getContractAmount();
            } else if (StrUtil.equals(bizType, "ZR")) {
                amount = getBean(ContractAocPriceService.class).getByContractId(contractBaseInfo.getId()).getContractAmount();
            }

            //
            List<CollectionBaseInfo> list = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, "RENT")
                    .ne(CollectionBaseInfo::getWriteOffStatus, "WRITE_OFF_COMPLETED")
                    .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId())
            );
            long total = 0;
            for (CollectionBaseInfo collectionBaseInfo : list) {
                Long principal = collectionBaseInfo.getPrincipal();
                Long collectionPrincipal = collectionBaseInfo.getCollectionPrincipal();
                long value = LongUtil.null2zero(principal) - LongUtil.null2zero(collectionPrincipal);
                if (value < 0) {
                    value = 0;
                }
                total += value;
            }
            builder.append(contractBaseInfo.getContractCode())
                    .append(",").append(Util.toWanYuan(amount))
                    .append(",").append(Util.toWanYuan(total))
                    .append(",").append(czrList)
                    .append(",").append(guaList);
            builder.append(System.lineSeparator());
        }
        System.out.println(builder.toString());
    }
}

