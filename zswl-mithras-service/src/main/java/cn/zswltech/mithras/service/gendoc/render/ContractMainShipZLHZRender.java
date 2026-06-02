package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.kpi.config.TaxRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2024/10/15
 * @description 船舶_融资租赁合同_回租
 */
@Component
public class ContractMainShipZLHZRender extends AbstractContractRender<ContractBaseInfo> {
    private static final String COLLECT_MONEY_ACCOUNT_TEXT_TEMPLATE = "户名：[%s]；开户行：[%s]；账号：[%s]";

    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private ClientService clientService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(512);
        // 先查询一些基础公共参数
        ContractTenantry contractTenantry = contractTenantryService.getMain(contractBaseInfo.getId());
        if (Objects.isNull(contractTenantry)) {
            throw new MithrasException("主承租人不存在");
        }
        Client client = clientService.getById(contractTenantry.getLesseeId());
        if (Objects.isNull(client)) {
            throw new MithrasException("主承租人客户信息不存在");
        }
        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("firstLesseeName", client.getClientName());
        // 填充主合同
        this.fillMain(renderMap, contractBaseInfo, contractTenantry);
        // 填充附件一：租赁船舶清单
        this.fillShipInfo(renderMap, contractBaseInfo);
        // 填充附件二：船舶买卖合同
        this.fillShipTradeContract(renderMap, contractBaseInfo);
        // 填充附件三：租赁船舶接受书
        this.fillShipAccept(renderMap, contractBaseInfo);
        // 填充附件四：租金支付概算表
        this.fillRentEstimateTable(renderMap, contractBaseInfo);
        // 填充附件五：租金支付表
        this.fillRentActualTable(renderMap, contractBaseInfo);
        // 填充附件六：租金调整通知书
        this.fillRentAdjustNotice(renderMap, contractBaseInfo);
        // 填充附件七：租金支付通知书
        this.fillRentPayNotice(renderMap, contractBaseInfo);
        // 填充附件八：终止租赁通知书
        this.fillFinishLeaseNotice(renderMap, contractBaseInfo);
        // 填充附件九：提前终止合同申请书
        this.fillFinishAdvancedApply(renderMap, contractBaseInfo);
        // 填充附件十：所有权转移证书
        this.fillOwnerTransfer(renderMap, contractBaseInfo);
        // 填充附件十一：融资租赁合同终止确认书
        this.fillFinishContractConfirm(renderMap, contractBaseInfo);
        // 渲染文档
//        InputStream inputStream = FileUtil.getInputStream("/Users/mockorz/Downloads/船舶_融资租赁合同_回租.docx");
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "船舶_融资租赁合同_回租.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "融资租赁合同（回租）" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private void fillMain(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo, ContractTenantry firstTenantry) {
        // 获取合同承租人信息
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractTenantryMap)) {
            throw new MithrasException("没有找到任何承租人信息");
        }
        // 批量查询相关信息
        List<Long> clientIds = Collections.singletonList(firstTenantry.getLesseeId());
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoMap = businessDataRepository.getCorpCommerceMap(clientIds);
        Map<Long, List<CorpAddressInfoLib>> corpAddressInfoMap = businessDataRepository.getCorpAddressMap(clientIds);
        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 填充主办信息
        renderMap.put("sponsorUserName", Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put("sponsorUserMail", Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put("sponsorUserPhone", Optional.ofNullable(sponsorPhone).orElse(""));

        // 填充第一承租人信息
        Client firstClient = clientMap.get(firstTenantry.getLesseeId());
        renderMap.put("firstLesseeName", firstClient.getClientName());
        CorpCommerceInfo firstCorpCommerceInfo = corpCommerceInfoMap.get(firstClient.getId());
        if (Objects.nonNull(firstCorpCommerceInfo)) {
            renderMap.put("firstLesseeLegalRepresentative", firstCorpCommerceInfo.getCorpRepresent());
        }
        List<CorpAddressInfoLib> firstCorpAddressInfoList = corpAddressInfoMap.get(firstClient.getId());
        if (!CollectionUtils.isEmpty(firstCorpAddressInfoList)) {
            renderMap.put("firstLesseeAddress", this.getCorpRegistryAddress(firstCorpAddressInfoList));
        }
        if (Objects.nonNull(firstTenantry.getContactId())) {
            CorpContactInfoLib firstCorpContactInfo = this.getNewestContact(firstTenantry.getContactId());
            if (Objects.nonNull(firstCorpContactInfo)) {
                renderMap.put("firstLesseeContact", firstCorpContactInfo.getName());
                renderMap.put("firstLesseeContactMobile", firstCorpContactInfo.getTelephone());
                renderMap.put("firstLesseeEmail", firstCorpContactInfo.getMail());
                renderMap.put("firstLesseeContactTelephone", StrUtil.isBlank(firstCorpContactInfo.getLandlineTelephone()) ? "\\" : firstCorpContactInfo.getLandlineTelephone());
            }
        }
        // 查询报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        if (Objects.nonNull(contractLeasePrice)) {
            renderMap.put("monthCount", contractLeasePrice.getLeaseMonthCount());
            renderMap.put("repayTimes", contractLeasePrice.getRepayTimesTotal());
            BigDecimal applyCreditAmount = NumberUtil.div(contractLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE);
            BigDecimal commissionAmount = NumberUtil.div(contractLeasePrice.getCommission().toString(), GlobalConstants.MONEY_MULTIPLE);
            renderMap.put("contractAmountCN", NumberChineseFormatter.format(applyCreditAmount.doubleValue(), true, true));
            renderMap.put("contractAmount", this.toYuan(contractLeasePrice.getApplyCreditAmount()));
            // 获取手续费
            renderMap.put("commissionAmountCN", NumberChineseFormatter.format(commissionAmount.doubleValue(), true, true));
            renderMap.put("commissionAmount", Optional.ofNullable(this.toYuan(contractLeasePrice.getCommission())).orElse("       "));
            BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
            renderMap.put("commissionExcludeTax", Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, null)).orElse("      "));
            renderMap.put("commissionTax", Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, taxRate)).orElse("      "));
            RateType rateType = RateType.of(contractLeasePrice.getRateType());
            if (Objects.nonNull(rateType)) {
                if (rateType == RateType.FIXED) {
                    renderMap.put("interestRateType", 1);
                } else {
                    renderMap.put("interestRateType", 2);
                }
            }
            Assert.notNull(contractLeasePrice.getLprAddPercent(), () -> MithrasException.newException("请先完善LPR加点数据"));
            if (RateType.FIXED.name().equals(contractLeasePrice.getRateType())) {
                // 固定利率
                renderMap.put("fixedInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
                // 浮动利率不填的 默认填'\'
                renderMap.put("floatInterestRate", "\\");
                renderMap.put("floatLprAdd", "\\");
                renderMap.put("floatLpr", "\\");
            } else if (RateType.FLOAT.name().equals(contractLeasePrice.getRateType())) {
                // 浮动利率
                renderMap.put("floatInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
                renderMap.put("floatLprAdd", NumberUtil.div(String.valueOf(contractLeasePrice.getLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                renderMap.put("floatLpr", NumberUtil.div(String.valueOf(contractLeasePrice.getLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                // 固定利率不填的 默认填'\'
                renderMap.put("fixedInterestRate", "\\");
            }
            // 罚息日利率
            if (Objects.nonNull(contractLeasePrice.getDefaultInterestRate())) {
                renderMap.put("dailyPenaltyInterestRate", NumberUtil.div(String.valueOf(contractLeasePrice.getDefaultInterestRate()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
            RepayRateEnum repayRateEnum = RepayRateEnum.of(contractLeasePrice.getRepayRate());
            if (Objects.nonNull(repayRateEnum)) {
                renderMap.put("paymentWay", repayRateEnum.display);
            } else {
                renderMap.put("paymentWay", "    ");
            }
            Long earnest = contractLeasePrice.getEarnestMoney();
            if (Objects.nonNull(earnest) && earnest > 0) {
                renderMap.put("earnestWay", "1");
                BigDecimal b = NumberUtil.div(earnest.toString(), GlobalConstants.MONEY_MULTIPLE);
                renderMap.put("earnestCN", NumberChineseFormatter.format(b.doubleValue(), true, true));
                renderMap.put("earnest", this.toYuan(earnest));
            } else {
                renderMap.put("earnestWay", "2");
                renderMap.put("earnestCN", "\\");
                renderMap.put("earnest", "\\");
            }
            Long nominalPrice = contractLeasePrice.getNominalPrice();
            if (Objects.nonNull(nominalPrice)) {
                BigDecimal b = NumberUtil.div(nominalPrice.toString(), GlobalConstants.MONEY_MULTIPLE);
                renderMap.put("nominalPriceCN", NumberChineseFormatter.format(b.doubleValue(), true, true));
                renderMap.put("nominalPrice", this.toYuan(nominalPrice));
            }
        }
        // 收款账户
        List<ContractAccount> contractAccountList = businessDataRepository.listContractAccount(contractBaseInfo.getId(), ContractAccountUseEnum.ZLSK);
        // 区分甲方和乙方
        List<ContractAccount> jiaContractAccountList = new LinkedList<>();
        List<ContractAccount> yiContractAccountList = new LinkedList<>();
        for (ContractAccount contractAccount : contractAccountList) {
            if (Objects.equals(contractAccount.getPayeeType(), ContractAccountPayeeTypeEnum.JIA.name())) {
                jiaContractAccountList.add(contractAccount);
            } else {
                yiContractAccountList.add(contractAccount);
            }
        }
        // 填充甲方账户
        if (CollectionUtil.isNotEmpty(jiaContractAccountList)) {
            ContractAccount myAccount = jiaContractAccountList.get(0);
            renderMap.put("myBankAccountName", myAccount.getAccountName());
            renderMap.put("myBankName", myAccount.getAccountAddress());
            renderMap.put("myBankAccount", myAccount.getAccountNum());
        } else {
            renderMap.put("myBankAccountName", "         ");
            renderMap.put("myBankName", "            ");
            renderMap.put("myBankAccount", "              ");
        }
        // 填充乙方账户
        if (!CollectionUtils.isEmpty(yiContractAccountList)) {
            List<String> accounts = new ArrayList<>(contractAccountList.size());
            for (ContractAccount contractAccount : yiContractAccountList) {
                String accountText = String.format(COLLECT_MONEY_ACCOUNT_TEXT_TEMPLATE, contractAccount.getAccountName(), contractAccount.getAccountAddress(), contractAccount.getAccountNum());
                accounts.add(accountText);
            }
            renderMap.put("collectMoneyAccountList", Joiner.on("\n").join(accounts));
        } else {
            renderMap.put("collectMoneyAccountList", "户名：[        ]；开户行：[           ]；账号：[        ]");
        }
        // 担保方式
        List<ContractGuarantor> contractGuarantorList = businessDataRepository.listContractGuarantor(contractBaseInfo.getId());
        renderMap.put("guaranteeWayList", this.renderGuarantorText(contractGuarantorList));
    }

    private void fillShipInfo(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        renderMap.put("shipInfoTable", this.renderShipTable(contractBaseInfo));
    }

    private void fillShipTradeContract(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillShipAccept(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillRentEstimateTable(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        // 报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        // 概算租金表 只取现金流项目为最近的
        List<ContractRentEstimate> contractRentEstimateList = businessDataRepository.listContractRentEstimate(contractBaseInfo.getId())
                .stream()
                .sorted(Comparator.comparing(ContractRentEstimate::getCashFlowPhase))
                .collect(Collectors.toList());
        //  增加首期利息
        if (!ObjectUtils.isEmpty(contractRentEstimateList) && !ObjectUtils.isEmpty(contractLeasePrice)) {
            Long firstInstallmentInterest = contractLeasePrice.getFirstInstallmentInterest();
            if (!ObjectUtils.isEmpty(contractLeasePrice) && !ObjectUtils.isEmpty(firstInstallmentInterest) && firstInstallmentInterest > 0) {
                ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
                contractRentEstimate.setContractId(contractBaseInfo.getId());
                contractRentEstimate.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
                contractRentEstimate.setCashFlowPhase(0);
                contractRentEstimate.setRent(firstInstallmentInterest);
                contractRentEstimate.setPrincipal(0L);
                contractRentEstimate.setInterest(firstInstallmentInterest);
                contractRentEstimate.setRemainingPrincipal(0L);
                contractRentEstimateList.add(contractRentEstimate);
                contractRentEstimateList = contractRentEstimateList.stream().sorted(Comparator.comparing(ContractRentEstimate::getCashFlowPhase)).collect(Collectors.toList());
            }
        }
        // 概算租金总额
        BigDecimal rentEstimateAmountTotal = Util.mithrasLong2BigDecimal(contractRentEstimateList.stream()
                .mapToLong(ContractRentEstimate::getRent)
                .sum());
        renderMap.put("leaseYear", Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getLeaseMonthCount).map(l -> new BigDecimal(l).divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()).orElse(""));
        renderMap.put("estimatedLeaseDate", Optional.ofNullable(contractBaseInfo.getEstimatedLeaseDate()).map(eld -> LocalDateTimeUtil.format(eld, "【yyyy】年【MM】月【dd】日")).orElse("【     】年【  】月【  】"));
        renderMap.put("rentEstimateAmountTotal", toYuan(rentEstimateAmountTotal.multiply(new BigDecimal(10000L)).longValue()));
        renderMap.put("rentEstimateAmountTotalCn", NumberChineseFormatter.format(rentEstimateAmountTotal.doubleValue(), true, true));
        // 表格
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("期数", "租金支付日", "租金", "其中：租金", null).center().create());
        tableDataList.add(Rows.of(null, null, null, "租赁成本", "租赁利息").center().create());
        for (ContractRentEstimate info : contractRentEstimateList) {
            tableDataList.add(Rows.of(Optional.ofNullable(info.getCashFlowPhase()).map(String::valueOf).orElse(""),
                    Optional.ofNullable(info.getCashFlowDate()).map(LocalDateTimeUtil::formatNormal).orElse(""),
                    Optional.ofNullable(info.getRent()).map(this::toYuan2Digit).orElse(""),
                    Optional.ofNullable(info.getPrincipal()).map(this::toYuan2Digit).orElse(""),
                    Optional.ofNullable(info.getInterest()).map(this::toYuan2Digit).orElse(""))
                    .center().create());
        }
        tableDataList.add(Rows.of("合计", "",
                this.toYuan2Digit(contractRentEstimateList.stream().filter(m -> Objects.nonNull(m.getRent())).mapToLong(ContractRentEstimate::getRent).sum()),
                this.toYuan2Digit(contractRentEstimateList.stream().filter(m -> Objects.nonNull(m.getPrincipal())).mapToLong(ContractRentEstimate::getPrincipal).sum()),
                this.toYuan2Digit(contractRentEstimateList.stream().filter(m -> Objects.nonNull(m.getInterest())).mapToLong(ContractRentEstimate::getInterest).sum())
        ).center().create());
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        TableRenderData tableRenderData = Tables.of(tableDataArray)
                .mergeRule(MergeCellRule.builder()
                        .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))
                        .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(1, 1))
                        .map(MergeCellRule.Grid.of(0, 2), MergeCellRule.Grid.of(1, 2))
                        .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4))
                        .build())
                .width(15.92D, new double[]{1.38D, 3.67D, 3.5D, 3.685D, 3.685D})
                .create();
        renderMap.put("rentEstimateTable", tableRenderData);
    }

    private void fillRentActualTable(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        // 实际租金列表
        List<ContractRentActual> rentActualList = businessDataRepository.listFirstContractRentActual(contractBaseInfo.getId())
                .stream()
                .sorted(Comparator.comparing(ContractRentActual::getCashFlowPhase))
                .collect(Collectors.toList());
        List<ContractRentActual> contractRentActualList = new ArrayList<>();
        //  增加首期利息
        if (!ObjectUtils.isEmpty(rentActualList)) {
            ContractRentActual rentActual = rentActualList.get(0);
            ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).lambdaQuery().eq(ContractLeasePrice::getContractId, rentActual.getContractId()).one();
            Long firstInstallmentInterest = contractLeasePrice.getFirstInstallmentInterest();
            if (!ObjectUtils.isEmpty(contractLeasePrice) && !ObjectUtils.isEmpty(firstInstallmentInterest) && firstInstallmentInterest > 0) {
                ContractRentActual contractRentActual = new ContractRentActual();
                contractRentActual.setContractId(rentActual.getContractId());
                contractRentActual.setReceiptId(rentActual.getReceiptId());
                contractRentActual.setCashFlowDate(rentActual.getCashFlowDate());
                contractRentActual.setCashFlowPhase(0);
                contractRentActual.setRent(firstInstallmentInterest);
                contractRentActual.setInterest(firstInstallmentInterest);
                contractRentActual.setPrincipal(0L);
                contractRentActual.setRemainingPrincipal(0L);
                contractRentActualList.add(contractRentActual);
            }
            contractRentActualList.addAll(rentActualList);
        }

        Long rentTotal = contractRentActualList.stream().map(ContractRentActual::getRent).filter(Objects::nonNull).reduce(Long::sum).orElse(null);
        // 租金总和
        renderMap.put("rentTotalCN", Optional.ofNullable(rentTotal).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("rentTotal", Optional.ofNullable(rentTotal).map(this::toYuan).orElse("          "));
        // 利息总和
        Long interestTotal = contractRentActualList.stream().map(ContractRentActual::getInterest).filter(Objects::nonNull).reduce(Long::sum).orElse(null);
        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
        renderMap.put("interestExcludeTax", Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, null)).orElse("        "));
        renderMap.put("interestTax", Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, taxRate)).orElse("        "));
        // 表格
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("期数", "租金支付日", "租金", "其中：租金", null).center().create());
        tableDataList.add(Rows.of(null, null, null, "租赁成本", "租赁利息").center().create());
        for (ContractRentActual info : contractRentActualList) {
            tableDataList.add(Rows.of(Optional.ofNullable(info.getCashFlowPhase()).map(c -> String.format("第%s期", c)).orElse(""),
                    Optional.ofNullable(info.getCashFlowDate()).map(LocalDateTimeUtil::formatNormal).orElse(""),
                    Optional.ofNullable(info.getRent()).map(this::toYuan2Digit).orElse(""),
                    Optional.ofNullable(info.getPrincipal()).map(this::toYuan2Digit).orElse(""),
                    Optional.ofNullable(info.getInterest()).map(this::toYuan2Digit).orElse(""))
                    .center().create());
        }
        // 2023-02-28 应客户要求 实际租金表的合计行去掉
        // 2023-03-02 应客户要求 实际租金表的合计行加上 为空值
        tableDataList.add(Rows.of("合计", "", "", "", "").center().create());
//                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getRent())).mapToLong(ContractRentActual::getRent).sum()),
//                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getPrincipal())).mapToLong(ContractRentActual::getPrincipal).sum()),
//                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getInterest())).mapToLong(ContractRentActual::getInterest).sum())
//        ).center().create());
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        TableRenderData tableRenderData = Tables.of(tableDataArray)
                .mergeRule(MergeCellRule.builder()
                        .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))
                        .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(1, 1))
                        .map(MergeCellRule.Grid.of(0, 2), MergeCellRule.Grid.of(1, 2))
                        .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4))
                        .build())
                .width(15.44D, new double[]{2.19D, 2.75D, 3.75D, 3.5D, 3.25D})
                .create();
        renderMap.put("rentActualTable", tableRenderData);
    }

    private void fillRentAdjustNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillRentPayNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillFinishLeaseNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillFinishAdvancedApply(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillOwnerTransfer(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillFinishContractConfirm(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        return contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "船舶_融资租赁合同_回租.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "船舶_融资租赁合同_回租.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
