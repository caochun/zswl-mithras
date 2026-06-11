package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contractzlzz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.kpi.application.config.KpiParameterConfigService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.customer.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2024/10/16
 * @description 船舶_融资租赁合同_直租
 */
@Component
public class ContractZLZZShipMainRender extends AbstractContractZLZZRender<ContractBaseInfo> {
    @Resource
    private ClientService clientService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
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
        // 填充附件一 船舶买卖合同
        this.fillShipTradeContract(renderMap, contractBaseInfo);
        // 填充附件二 租赁船舶清单
        this.fillShipInfo(renderMap, contractBaseInfo);
        // 填充附件三 概算租金/租前息支付表
        this.fillRentEstimateTable(renderMap, contractBaseInfo);
        // 填充附件四 实际租金/租前息支付表
        this.fillRentActualTable(renderMap, contractBaseInfo);
        // 填充附件五 租赁物接受书
        this.fillShipAccept(renderMap, contractBaseInfo);
        // 填充附件六 起租通知书
        this.fillStartRentNotice(renderMap, contractBaseInfo);
        // 填充附件七 租金/租前息支付通知书
        this.fillRentPayNotice(renderMap, contractBaseInfo);
        // 填充附件八 补足租赁保证金通知书
        this.fillSuppleEarnestNotice(renderMap, contractBaseInfo);
        // 填充附件九 所有权转移证书
        this.fillOwnerTransfer(renderMap, contractBaseInfo);
        // 填充附件十 租金/租前息调整通知书
        this.fillRentAdjustNotice(renderMap, contractBaseInfo);
        // 填充附件十一 融资租赁合同终止确认书
        this.fillFinishContractConfirm(renderMap, contractBaseInfo);
        // 渲染文档
//        InputStream inputStream = FileUtil.getInputStream("/Users/mockorz/Downloads/船舶_融资租赁合同_直租.docx");
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "船舶_融资租赁合同_直租.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "融资租赁合同（直租）" + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        // 报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案为空"));
        renderMap.put("monthCount", Optional.ofNullable(contractLeasePrice.getLeaseMonthCount()).map(String::valueOf).orElse("   "));
        renderMap.put("contractAmountCN", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("contractAmount", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(this::toYuan).orElse("          "));
        renderMap.put("beforeRateType", Optional.ofNullable(contractLeasePrice.getBeforeRateType()).map(e -> Objects.equals(RateType.FIXED.name(), e) ? "1" : "2").orElse("  "));

        // 手续费
        BigDecimal commissionAmount = NumberUtil.div(contractLeasePrice.getCommission().toString(), GlobalConstants.MONEY_MULTIPLE);
        renderMap.put("commissionAmountCN", NumberChineseFormatter.format(commissionAmount.doubleValue(), true, true));
        renderMap.put("commissionAmount", Optional.ofNullable(this.toYuan(contractLeasePrice.getCommission())).orElse("       "));
        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
        renderMap.put("commissionExcludeTax", Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, null)).orElse("      "));
        renderMap.put("commissionTax", Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, taxRate)).orElse("      "));

        if (RateType.FIXED.name().equals(contractLeasePrice.getBeforeRateType())) {
            // 固定利率
            renderMap.put("fixedBeforeInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getBeforeLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getBeforeLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            // 浮动利率不填的 默认填'\'
            renderMap.put("floatBeforeLprAdd", "\\");
            renderMap.put("floatBeforeLpr", "\\");
            renderMap.put("floatBeforeLprType", "\\");
            renderMap.put("floatBeforeInterestRate", "\\");
        } else if (RateType.FLOAT.name().equals(contractLeasePrice.getBeforeRateType())) {
            // 浮动利率
            renderMap.put("floatBeforeLprAdd", NumberUtil.div(String.valueOf(contractLeasePrice.getBeforeLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            renderMap.put("floatBeforeLpr", NumberUtil.div(String.valueOf(contractLeasePrice.getBeforeLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
            LPRTypeEnum lprTypeEnum = LPRTypeEnum.of(contractLeasePrice.getBeforeLprType());
            renderMap.put("floatBeforeLprType", Optional.ofNullable(lprTypeEnum).map(e -> e.display).orElse("    "));
            renderMap.put("floatBeforeInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getBeforeLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getBeforeLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            // 固定利率不填的 默认填'\'
            renderMap.put("fixedBeforeInterestRate", "\\");
        }
        renderMap.put("rateType", Optional.ofNullable(contractLeasePrice.getRateType()).map(e -> Objects.equals(RateType.FIXED.name(), e) ? "1" : "2").orElse("  "));
        if (RateType.FIXED.name().equals(contractLeasePrice.getRateType())) {
            // 固定利率
            renderMap.put("fixedInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            renderMap.put("interestRate", renderMap.get("fixedInterestRate"));
            // 浮动利率不填的 默认填'\'
            renderMap.put("floatLprAdd", "\\");
            renderMap.put("floatLpr", "\\");
            renderMap.put("floatLprType", "\\");
            renderMap.put("floatInterestRate", "\\");
        } else if (RateType.FLOAT.name().equals(contractLeasePrice.getRateType())) {
            // 浮动利率
            renderMap.put("floatLprAdd", NumberUtil.div(String.valueOf(contractLeasePrice.getLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            renderMap.put("floatLpr", NumberUtil.div(String.valueOf(contractLeasePrice.getLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
            LPRTypeEnum lprTypeEnum = LPRTypeEnum.of(contractLeasePrice.getLprType());
            renderMap.put("floatLprType", Optional.ofNullable(lprTypeEnum).map(e -> e.display).orElse("    "));
            renderMap.put("floatInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            renderMap.put("interestRate", renderMap.get("floatInterestRate"));
            // 固定利率不填的 默认填'\'
            renderMap.put("fixedInterestRate", "\\");
        }
        // 罚息日利率
        if (Objects.nonNull(contractLeasePrice.getDefaultInterestRate())) {
            renderMap.put("dailyPenaltyInterestRate", NumberUtil.div(String.valueOf(contractLeasePrice.getDefaultInterestRate()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        renderMap.put("downPaymentCN", Optional.ofNullable(contractLeasePrice.getDownPayment()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("           "));
        renderMap.put("downPayment", Optional.ofNullable(contractLeasePrice.getDownPayment()).map(this::toYuan).orElse("          "));
        if (Objects.nonNull(contractLeasePrice.getEarnestMoney()) && contractLeasePrice.getEarnestMoney() > 0) {
            renderMap.put("hasEarnestMoney", 1);
            renderMap.put("earnestCN", NumberChineseFormatter.format(NumberUtil.div(contractLeasePrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true));
            renderMap.put("earnest", this.toYuan(contractLeasePrice.getEarnestMoney()));
        } else {
            renderMap.put("hasEarnestMoney", 2);
            renderMap.put("earnestCN", "\\");
            renderMap.put("earnest", "\\");
        }
        renderMap.put("nominalPriceCN", Optional.ofNullable(contractLeasePrice.getNominalPrice()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("       "));
        renderMap.put("nominalPrice", this.toYuan(contractLeasePrice.getNominalPrice()));

        // 收款账户
        List<ContractAccount> contractAccountList = businessDataRepository.listContractAccount(contractBaseInfo.getId(), ContractAccountUseEnum.ZLSK);
        ContractAccount contractAccountJia = this.findAccount(ContractAccountPayeeTypeEnum.JIA, contractAccountList);
        ContractAccount contractAccountYi = (this.findAccount(ContractAccountPayeeTypeEnum.YI, contractAccountList)!=null)?
                this.findAccount(ContractAccountPayeeTypeEnum.YI,contractAccountList):this.findAccount(ContractAccountPayeeTypeEnum.SELLER,contractAccountList);
        renderMap.put("accountNameJia", Optional.ofNullable(contractAccountJia).map(ContractAccount::getAccountName).orElse("        "));
        renderMap.put("accountBankJia", Optional.ofNullable(contractAccountJia).map(ContractAccount::getAccountAddress).orElse("        "));
        renderMap.put("accountNoJia", Optional.ofNullable(contractAccountJia).map(ContractAccount::getAccountNum).orElse("        "));
        renderMap.put("accountNameYi", Optional.ofNullable(contractAccountYi).map(ContractAccount::getAccountName).orElse("        "));
        renderMap.put("accountBankYi", Optional.ofNullable(contractAccountYi).map(ContractAccount::getAccountAddress).orElse("        "));
        renderMap.put("accountNoYi", Optional.ofNullable(contractAccountYi).map(ContractAccount::getAccountNum).orElse("        "));

        // 担保方式
        List<ContractGuarantor> contractGuarantorList = businessDataRepository.listContractGuarantor(contractBaseInfo.getId());
        renderMap.put("guaranteeWayList", this.renderGuarantorText(contractGuarantorList));
    }

    private ContractAccount findAccount(ContractAccountPayeeTypeEnum contractAccountPayeeTypeEnum, List<ContractAccount> contractAccountList) {
        if (CollectionUtil.isEmpty(contractAccountList)) {
            return null;
        }
        for (ContractAccount contractAccount : contractAccountList) {
            if (Objects.equals(contractAccount.getPayeeType(), contractAccountPayeeTypeEnum.name())) {
                return contractAccount;
            }
        }
        return null;
    }

    private void fillShipTradeContract(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillShipInfo(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        renderMap.put("shipInfoTable", this.renderShipTable(contractBaseInfo));
    }

    private void fillRentEstimateTable(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        // 获取概算租金表
        List<ContractRentEstimate> estimateList = businessDataRepository.listContractRentEstimate(contractBaseInfo.getId());
        Assert.notEmpty(estimateList, () -> MithrasException.newException("概算租金表为空"));
        List<RentHelper> beforeRentList = new LinkedList<>();
        List<RentHelper> startRentList = new LinkedList<>();
        // 排序
        estimateList.sort(Comparator.comparing(ContractRentEstimate::getCashFlowDate));
        estimateList.sort(Comparator.comparing(ContractRentEstimate::getId));
        // 拆分租前和起租
        boolean isBefore = true;
        for (ContractRentEstimate contractRentEstimate : estimateList) {
            if (isBefore) {
                if (Objects.nonNull(contractRentEstimate.getPrincipal()) && contractRentEstimate.getPrincipal() == 0) {
                    beforeRentList.add(BeanUtil.copyProperties(contractRentEstimate, RentHelper.class));
                } else {
                    isBefore = false;
                    startRentList.add(BeanUtil.copyProperties(contractRentEstimate, RentHelper.class));
                }
            } else {
                startRentList.add(BeanUtil.copyProperties(contractRentEstimate, RentHelper.class));
            }
        }
        //  增加首期租金
        if(!ObjectUtils.isEmpty(startRentList)){
            ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).lambdaQuery().eq(ContractLeasePrice::getContractId, contractBaseInfo.getId()).one();
            if (!ObjectUtils.isEmpty(contractLeasePrice)
                    && !ObjectUtils.isEmpty( contractLeasePrice.getFirstInstallmentInterest()) &&  contractLeasePrice.getFirstInstallmentInterest()> 0) {
                RentHelper rentHelper = startRentList.get(0);
                RentHelper zeroRent = new RentHelper();
                zeroRent.setCashFlowPhase(0);
                zeroRent.setRent(contractLeasePrice.getFirstInstallmentInterest());
                zeroRent.setCashFlowDate(rentHelper.getCashFlowDate());
                startRentList.add(zeroRent);
                startRentList = startRentList.stream().sorted(Comparator.comparing(RentHelper::getCashFlowPhase)).collect(Collectors.toList());
            }
        }
        // 记录租金和
        long rentTotal = 0;
        for (RentHelper rentHelper : startRentList) {
            if (Objects.nonNull(rentHelper.getRent())) {
                rentTotal = rentTotal + rentHelper.getRent();
            }
        }
        renderMap.put("beforeRepayTimes", beforeRentList.size());
        LocalDate beforeStartDate = null;
        if (CollectionUtil.isNotEmpty(beforeRentList)) {
            beforeStartDate = beforeRentList.get(0).getCashFlowDate();
        }
        if (Objects.nonNull(beforeStartDate)) {
            renderMap.put("beforePlanStartYear", beforeStartDate.getYear());
            renderMap.put("beforePlanStartMonth", beforeStartDate.getMonthValue());
            renderMap.put("beforePlanStartDay", beforeStartDate.getDayOfMonth());
        } else {
            renderMap.put("beforePlanStartYear", "    ");
            renderMap.put("beforePlanStartMonth", "   ");
            renderMap.put("beforePlanStartDay", "   ");
        }
        if (CollectionUtil.isNotEmpty(beforeRentList)) {
            LocalDate start = beforeRentList.get(0).getCashFlowDate();
            LocalDate end = beforeRentList.get(beforeRentList.size() - 1).getCashFlowDate();
            renderMap.put("beforeMonthCount", LocalDateTimeUtil.between(start.atStartOfDay(), end.atStartOfDay(), ChronoUnit.MONTHS) + 1);
        } else {
            renderMap.put("beforeMonthCount", 0);
        }
        // 报价方案
        ContractLeasePrice contractLeasePrice = this.businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案为空"));
        renderMap.put("leaseYear", Optional.ofNullable(contractLeasePrice.getLeaseMonthCount()).map(e -> String.valueOf(BigDecimal.valueOf(e).divide(BigDecimal.valueOf(12), RoundingMode.UP).intValue())).orElse("     "));
        renderMap.put("repayTimes", Optional.ofNullable(contractLeasePrice.getRepayTimesTotal()).map(String::valueOf).orElse("    "));
        if (Objects.nonNull(contractBaseInfo.getEstimatedLeaseDate())) {
            renderMap.put("planStartYear", contractBaseInfo.getEstimatedLeaseDate().getYear());
            renderMap.put("planStartMonth", contractBaseInfo.getEstimatedLeaseDate().getMonthValue());
            renderMap.put("planStartDay", contractBaseInfo.getEstimatedLeaseDate().getDayOfMonth());
        } else {
            renderMap.put("planStartYear", "    ");
            renderMap.put("planStartMonth", "   ");
            renderMap.put("planStartDay", "   ");
        }
        renderMap.put("contractAmountCN", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("contractAmount", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(this::toYuan).orElse("          "));
        RateType beforeRateType = RateType.of(contractLeasePrice.getBeforeRateType());
        renderMap.put("beforeInterestRateText", Optional.ofNullable(beforeRateType).map(RateType::display).orElse("       "));
        renderMap.put("beforeInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getBeforeLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getBeforeLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        RateType rateType = RateType.of(contractLeasePrice.getRateType());
        renderMap.put("interestRateText", Optional.ofNullable(rateType).map(RateType::display).orElse("       "));
        renderMap.put("interestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        renderMap.put("rentEstimateTotalCN", NumberChineseFormatter.format(NumberUtil.div(String.valueOf(rentTotal), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true));
        renderMap.put("rentEstimateTotal", this.toYuan(rentTotal));
        // 概算租前息和租金支付表格
        renderMap.put("rentEstimateTable", this.renderRentTable(contractBaseInfo, contractLeasePrice, beforeRentList, startRentList, rentTotal, false));
    }

    private void fillRentActualTable(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        // 获取实际租金表
        List<ContractRentActual> actualList = businessDataRepository.listContractRentActual(contractBaseInfo.getId());
        Assert.notEmpty(actualList, () -> MithrasException.newException("实际租金表为空"));
        List<RentHelper> beforeRentList = new LinkedList<>();
        List<RentHelper> startRentList = new LinkedList<>();
        // 排序
//        actualList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
        actualList.sort(Comparator.comparing(ContractRentActual::getId));

        // 拆分租前和起租
        boolean isBefore = true;
        for (ContractRentActual contractRentActual : actualList) {
            if (isBefore) {
                if (Objects.nonNull(contractRentActual.getPrincipal()) && contractRentActual.getPrincipal() == 0) {
                    beforeRentList.add(BeanUtil.copyProperties(contractRentActual, RentHelper.class));
                } else {
                    isBefore = false;
                    startRentList.add(BeanUtil.copyProperties(contractRentActual, RentHelper.class));
                }
            } else {
                startRentList.add(BeanUtil.copyProperties(contractRentActual, RentHelper.class));
            }
        }

        //  增加首期租金
        if(!ObjectUtils.isEmpty(startRentList)){
            ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).lambdaQuery().eq(ContractLeasePrice::getContractId, contractBaseInfo.getId()).one();
            if (!ObjectUtils.isEmpty(contractLeasePrice)
                    && !ObjectUtils.isEmpty( contractLeasePrice.getFirstInstallmentInterest()) &&  contractLeasePrice.getFirstInstallmentInterest()> 0) {
                RentHelper rentHelper = startRentList.get(0);
                RentHelper zeroRent = new RentHelper();
                zeroRent.setCashFlowPhase(0);
                zeroRent.setRent(contractLeasePrice.getFirstInstallmentInterest());
                zeroRent.setCashFlowDate(rentHelper.getCashFlowDate());
                startRentList.add(zeroRent);
                startRentList = startRentList.stream().sorted(Comparator.comparing(RentHelper::getCashFlowPhase)).collect(Collectors.toList());
            }
        }
        // 记录租金和
        long rentTotal = 0;
        for (RentHelper rentHelper : startRentList) {
            if (Objects.nonNull(rentHelper.getRent())) {
                rentTotal = rentTotal + rentHelper.getRent();
            }
        }
        // 记录利息和
        long interestTotal = 0;
        for (RentHelper rentHelper : startRentList) {
            if (Objects.nonNull(rentHelper.getInterest())) {
                interestTotal = interestTotal + rentHelper.getInterest();
            }
        }
        // 报价方案
        ContractLeasePrice contractLeasePrice = this.businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案为空"));
        // 合同金额
        renderMap.put("contractAmountCN", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("contractAmount", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(this::toYuan).orElse("          "));

//        // 租金总和
//        renderMap.put("rentTotal", Optional.of(rentTotal).filter(aLong -> aLong != 0).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
//        renderMap.put("rentTotalCN", Optional.of(rentTotal).filter(aLong -> aLong != 0).map(this::toYuan).orElse("          "));

//        // 利息总和
//        BigDecimal interestAmount = NumberUtil.div(String.valueOf(interestTotal), GlobalConstants.MONEY_MULTIPLE);
//        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_AMOUNT_CN, NumberChineseFormatter.format(interestAmount.doubleValue(), true, true));
//        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_AMOUNT, Optional.ofNullable(this.toYuan(interestTotal)).orElse("        "));
//        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_ZZ);
//        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_EXCLUDE_TAX, Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, null)).orElse("       "));
//        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_TAX, Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, taxRate)).orElse("       "));

        // 租前息和租金支付表格
        renderMap.put("rentActualTable", this.renderRentTable(contractBaseInfo, contractLeasePrice, beforeRentList, startRentList, rentTotal, true));
    }

    private void fillShipAccept(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillStartRentNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillRentPayNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillSuppleEarnestNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillOwnerTransfer(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    private void fillRentAdjustNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

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
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "船舶_融资租赁合同_直租.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "船舶_融资租赁合同_直租.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
