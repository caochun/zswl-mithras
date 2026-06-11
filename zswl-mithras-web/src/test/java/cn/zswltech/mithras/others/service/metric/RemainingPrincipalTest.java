package cn.zswltech.mithras.others.service.metric;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Industry;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.ContractPriceService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @description: 包括合同名+客户名+项目金额（报价方案）+投放金额（付款核销）+剩余本金+地区+业务部（基本信息）
 * @author: zhaozhengkang
 * @date: 2023/6/5 10:48
 */
public class RemainingPrincipalTest extends ApplicationTest {
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientService clientService;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;

    @Test
    public void remainingPrincipalTest() {
        LocalDate endDate = LocalDate.of(2023, 9, 30);
        Map<Long, Client> clients = clientService.list(Wrappers.<Client>lambdaQuery()
                        .eq(Client::getClientType, ClientType.CORPORATION.name()))
                .stream().collect(Collectors.toMap(Client::getId, item -> item, (a, b) -> a));

        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setInClientIds(clients.keySet());
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoLibMap = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto).stream().collect(Collectors.toMap(CorpCommerceInfoLib::getClientId, item -> item, (a, b) -> a));
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name()));

        Map<Long, Long> amountMap = contractPriceService.queryNewestContractAmount(contracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));

        Set<Long> deptIds = contracts.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toSet());
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);

        List<PaymentActualDetail> paymentDetails = paymentActualDetailMapper.selectList(
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate));
        Map<Long, List<PaymentActualDetail>> paymentDetailsGroupByContract = paymentDetails
                .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));

        Map<Long, List<PaymentActualDetail>> thisYearPaymentDetailsGroupByContract = paymentDetails
                .stream().filter(v -> v.getPaidInDate().getYear() == 2023).collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        

        List<ContractInfo> res = new CopyOnWriteArrayList<>();
        contracts.stream().forEach(contract -> {
            ContractInfo contractInfo = new ContractInfo();
            contractInfo.setContractCode(contract.getContractCode());
            contractInfo.setClientName(clients.get(contract.getClientId()).getClientName());
            contractInfo.setContractAmount(new BigDecimal(amountMap.get(contract.getId())).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            long paidSum = paymentDetailsGroupByContract.getOrDefault(contract.getId(), new ArrayList<>())
                    .stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            long thisYearPaidSum = thisYearPaymentDetailsGroupByContract.getOrDefault(contract.getId(), new ArrayList<>()).stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            thisYearPaymentDetailsGroupByContract.remove(contract.getId());

            contractInfo.setPaidAmount(new BigDecimal(paidSum).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            contractInfo.setThisYearPaidAmount(new BigDecimal(thisYearPaidSum).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());

            BigDecimal remainingPrincipal = remainingPrincipalServiceImpl.remainingPrincipal(contract.getId(), endDate);
            if (remainingPrincipal != null) {
                contractInfo.setRemainingPrincipal(remainingPrincipal
                        .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).doubleValue());
            }
            contractInfo.setArea(clients.get(contract.getClientId()).getProvinceOfAffiliation());
            contractInfo.setBizDeptName(deptId2Name.get(contract.getBizDeptId()));
            CorpCommerceInfoLib commerceInfoLib = corpCommerceInfoLibMap.get(contract.getClientId());
            if (ObjectUtil.isNotEmpty(commerceInfoLib)) {
                // 行业分类
                String industryType = commerceInfoLib.getIndustryType();
                try {
                    Industry industry = Industry.valueOf(String.valueOf(industryType.charAt(0)));
                    contractInfo.setIndustryClassify(industry.display());
                } catch (Exception e) {
                    log.error("行业分类错误，clientId:{}", contract.getClientId());
                    contractInfo.setIndustryClassify("未分类");
                }

                // 风控行业分类
                String riskControlIndustryClassify = commerceInfoLib.getRiskControlIndustryClassify();
                if (ObjectUtil.isNotEmpty(riskControlIndustryClassify)) {
                    contractInfo.setRiskControlIndustryClassify(RiskControlIndustryClassify.valueOf(riskControlIndustryClassify).display());
                }
            }
            res.add(contractInfo);
        });

        String s = JSON.toJSONString(res);
        System.out.println("hello world");
    }

    @Data
    @Accessors
    public static class ContractInfo {
        private String contractCode;
        private Long clientId;
        private String clientName;
        private String contractAmount;
        private String thisYearPaidAmount;
        private String paidAmount;
        private double remainingPrincipal;
        private String area;
        private String bizDeptName;
        private String industryClassify;
        private String riskControlIndustryClassify;
    }


    /**
     * 导出公用事业类合同剩余本金
     */
    @Test
    public void remainingPrincipalTest2() {

        CorpCommerceInfoLibDto commerceDto = new CorpCommerceInfoLibDto();
        commerceDto.setInRiskControlIndustryClassify(Arrays.asList(RiskControlIndustryClassify.PUBLIC_UTILITIES.name()));
        Set<Long> targetClients = corpCommerceInfoLibMapper.listNewestCommerceInfo(commerceDto)
                .stream().map(ClientBaseModel::getClientId).collect(Collectors.toSet());

        Map<Long, String> clientId2Name = id2NameService.clientId2Name(targetClients);
        // 目标合同
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, targetClients));

        List<ContractInfo> res = new ArrayList<>();
        contracts.parallelStream().forEach(contract -> {
            ContractInfo contractInfo = new ContractInfo();
            contractInfo.setClientId(contract.getClientId());
            contractInfo.setContractCode(contract.getContractCode());
            contractInfo.setClientName(clientId2Name.get(contract.getClientId()));
            contractInfo.setRemainingPrincipal(remainingPrincipalServiceImpl.remainingPrincipal(contract.getId(), null)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).doubleValue());
            res.add(contractInfo);
        });
        String s = JSON.toJSONString(res);
        BigDecimal reduce1 = res.stream().map(ContractInfo::getRemainingPrincipal).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<Long, List<ContractInfo>> collect = res.stream().collect(Collectors.groupingBy(ContractInfo::getClientId));

        System.out.println("hello world");


        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setClientIds(targetClients);
        Map<Long, Long> re = remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(dto);
        BigDecimal reduce = re.values().stream().map(LongUtil::null2zero).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println();
    }
}
