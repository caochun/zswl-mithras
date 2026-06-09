package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.projectprocess.enums.projreview.MeetMinuteStatuesEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectInsurancePurchaserEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectPolicyTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewMeetMinuteBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewMeetMinuteBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/17
 * @description 融资租赁合同（回租）
 */
@Component
public class ContractMainZLHZRender extends AbstractContractRender<ContractBaseInfo> {

    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        ProjectBizType projectBizType = Assert.notNull(ProjectBizType.of(contractBaseInfo.getBizType()), () -> MithrasException.newException("业务类型不能为空"));
        LeaseType leaseType = Assert.notNull(LeaseType.of(contractBaseInfo.getLeaseType()), () -> MithrasException.newException("租赁类型不能为空"));
        Map<String, Object> renderMap = new HashMap<>(128);
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CODE, contractBaseInfo.getContractCode());
        // 获取合同承租人信息
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractTenantryMap)) {
            throw new MithrasException("没有找到任何承租人信息");
        }
        List<Long> clientIds = new ArrayList<>(contractTenantryMap.size());
        List<ContractTenantry> secondLessee = new ArrayList<>(contractTenantryMap.size() - 1);
        // 找到主承租人作为第一承租人
        ContractTenantry firstContractTenantry = null;
        for (Map.Entry<Long, ContractTenantry> entry : contractTenantryMap.entrySet()) {
            ContractTenantry contractTenantry = entry.getValue();
            clientIds.add(contractTenantry.getLesseeId());
            if (Objects.equals(contractTenantry.getLesseeType(), LesseeTypeEnum.MAIN_LESSSEE.name())) {
                firstContractTenantry = contractTenantry;
            } else {
                secondLessee.add(contractTenantry);
            }
        }
        if (Objects.isNull(firstContractTenantry)) {
            throw new MithrasException("没有找到主承租人信息");
        }
        // 批量查询相关信息
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoMap = businessDataRepository.getCorpCommerceMap(clientIds);
        Map<Long, List<CorpAddressInfoLib>> corpAddressInfoMap = businessDataRepository.getCorpAddressMap(clientIds);
        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 填充主办信息
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_NAME, Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_MAIL, Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_PHONE, Optional.ofNullable(sponsorPhone).orElse(""));

        // 填充第一承租人信息
        Client firstClient = clientMap.get(firstContractTenantry.getLesseeId());
        if (Objects.nonNull(firstClient)) {
            renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_NAME, firstClient.getClientName());
        }
        CorpCommerceInfo firstCorpCommerceInfo = corpCommerceInfoMap.get(firstContractTenantry.getLesseeId());
        if (Objects.nonNull(firstCorpCommerceInfo)) {
            renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_LEGAL_REPRESENTATIVE, firstCorpCommerceInfo.getCorpRepresent());
        }
        List<CorpAddressInfoLib> firstCorpAddressInfoList = corpAddressInfoMap.get(firstContractTenantry.getLesseeId());
        if (!CollectionUtils.isEmpty(firstCorpAddressInfoList)) {
            renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_ADDRESS, this.getCorpRegistryAddress(firstCorpAddressInfoList));
        }
        if (Objects.nonNull(firstContractTenantry.getContactId())) {
            CorpContactInfoLib firstCorpContactInfo = this.getNewestContact(firstContractTenantry.getContactId());
            if (Objects.nonNull(firstCorpContactInfo)) {
                renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_CONTACT, firstCorpContactInfo.getName());
                renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_CONTACT_MOBILE, firstCorpContactInfo.getTelephone());
                renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_EMAIL, firstCorpContactInfo.getMail());
                renderMap.put(RenderParameterKeyHolder.FIRST_LESSEE_CONTACT_TELEPHONE, StrUtil.isBlank(firstCorpContactInfo.getLandlineTelephone()) ? "\\" : firstCorpContactInfo.getLandlineTelephone());
            }
        }
        // 根据承租人数量使用不同模板文件（两个模板除了第二承租人参数外，其他参数一致）
        InputStream inputStream;
        if (contractTenantryMap.size() == 1) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_主合同_回租_单一承租人.docx");
        } else {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_主合同_回租_共同承租人.docx");
            // 填充第二承租人信息
            int secondLesseeSize = contractTenantryMap.size() - 1;
            // 第二承租人信息临时保存
            List<String> secondLesseeName = new ArrayList<>(secondLesseeSize);
            ContractTenantry secondTenantry = null;
            for (ContractTenantry contractTenantry : secondLessee) {
                Client client = clientMap.get(contractTenantry.getLesseeId());
                if (Objects.nonNull(client)) {
                    secondLesseeName.add(client.getClientName());
                    if (Objects.isNull(secondTenantry)) {
                        secondTenantry = contractTenantry;
                    }
                }
            }
            if (Objects.nonNull(secondTenantry)) {
                renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_NAME, Joiner.on("、").join(secondLesseeName));
                CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMap.get(secondTenantry.getLesseeId());
                if (Objects.nonNull(corpCommerceInfo)) {
                    renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_LEGAL_REPRESENTATIVE, corpCommerceInfo.getCorpRepresent());
                }
                List<CorpAddressInfoLib> corpAddressInfoList = corpAddressInfoMap.get(secondTenantry.getLesseeId());
                if (!CollectionUtils.isEmpty(corpAddressInfoList)) {
                    renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_ADDRESS, this.getCorpRegistryAddress(corpAddressInfoList));
                }
                if (Objects.nonNull(secondTenantry.getContactId())) {
                    CorpContactInfoLib corpContactInfo = this.getNewestContact(secondTenantry.getContactId());
                    if (Objects.nonNull(corpContactInfo)) {
                        renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_CONTACT, corpContactInfo.getName());
                        renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_CONTACT_MOBILE, corpContactInfo.getTelephone());
                        renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_EMAIL, corpContactInfo.getMail());
                        renderMap.put(RenderParameterKeyHolder.SECOND_LESSEE_CONTACT_TELEPHONE, StrUtil.isBlank(corpContactInfo.getLandlineTelephone()) ? "\\" : corpContactInfo.getLandlineTelephone());
                    }
                }
            }
        }
        // 查询报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        if (Objects.nonNull(contractLeasePrice)) {
            renderMap.put(RenderParameterKeyHolder.MONTH_COUNT, contractLeasePrice.getLeaseMonthCount());
            renderMap.put(RenderParameterKeyHolder.REPAY_TIMES, contractLeasePrice.getRepayTimesTotal());
            BigDecimal applyCreditAmount = NumberUtil.div(contractLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE);
            BigDecimal commissionAmount = NumberUtil.div(contractLeasePrice.getCommission().toString(), GlobalConstants.MONEY_MULTIPLE);
            renderMap.put(RenderParameterKeyHolder.CONTRACT_AMOUNT_CN, NumberChineseFormatter.format(applyCreditAmount.doubleValue(), true, true));
            renderMap.put(RenderParameterKeyHolder.CONTRACT_AMOUNT, this.toYuan(contractLeasePrice.getApplyCreditAmount()));
            // 获取手续费
            renderMap.put(RenderParameterKeyHolder.COMMISSION_AMOUNT_CN, NumberChineseFormatter.format(commissionAmount.doubleValue(), true, true));
            renderMap.put(RenderParameterKeyHolder.COMMISSION_AMOUNT, Optional.ofNullable(this.toYuan(contractLeasePrice.getCommission())).orElse("       "));
            BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
            renderMap.put(RenderParameterKeyHolder.COMMISSION_EXCLUDE_TAX, Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, null)).orElse("      "));
            renderMap.put(RenderParameterKeyHolder.COMMISSION_TAX, Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, taxRate)).orElse("      "));
            RateType rateType = RateType.of(contractLeasePrice.getRateType());
            if (Objects.nonNull(rateType)) {
                if (rateType == RateType.FIXED) {
                    renderMap.put(RenderParameterKeyHolder.INTEREST_RATE_TYPE, 1);
                } else {
                    renderMap.put(RenderParameterKeyHolder.INTEREST_RATE_TYPE, 2);
                }
            }
            Assert.notNull(contractLeasePrice.getLprAddPercent(), () -> MithrasException.newException("请先完善LPR加点数据"));
            if (RateType.FIXED.name().equals(contractLeasePrice.getRateType())) {
                // 固定利率
                renderMap.put(RenderParameterKeyHolder.FIXED_INTEREST_RATE, NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
                // 浮动利率不填的 默认填'\'
                renderMap.put(RenderParameterKeyHolder.FLOAT_INTEREST_RATE, "\\");
                renderMap.put(RenderParameterKeyHolder.FLOAT_LPR_ADD, "\\");
                renderMap.put(RenderParameterKeyHolder.FLOAT_LPR, "\\");

//                renderMap.put(RenderParameterKeyHolder.FIXED_LPR_ADD, NumberUtil.div(String.valueOf(contractLeasePrice.getLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
//                renderMap.put(RenderParameterKeyHolder.FIXED_LPR_TYPE, Optional.ofNullable(LPRTypeEnum.of(contractLeasePrice.getLprType())).map(item -> item.display).orElse("      "));
//                renderMap.put(RenderParameterKeyHolder.FIXED_LPR, NumberUtil.div(String.valueOf(contractLeasePrice.getLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
            } else if (RateType.FLOAT.name().equals(contractLeasePrice.getRateType())) {
                // 浮动利率
                renderMap.put(RenderParameterKeyHolder.FLOAT_INTEREST_RATE, NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
                renderMap.put(RenderParameterKeyHolder.FLOAT_LPR_ADD, NumberUtil.div(String.valueOf(contractLeasePrice.getLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
//                renderMap.put(RenderParameterKeyHolder.FLOAT_LPR_TYPE, Optional.ofNullable(LPRTypeEnum.of(contractLeasePrice.getLprType())).map(item -> item.display).orElse("      "));
                renderMap.put(RenderParameterKeyHolder.FLOAT_LPR, NumberUtil.div(String.valueOf(contractLeasePrice.getLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                // 固定利率不填的 默认填'\'
                renderMap.put(RenderParameterKeyHolder.FIXED_INTEREST_RATE, "\\");
            }
            RepayRateEnum repayRateEnum = RepayRateEnum.of(contractLeasePrice.getRepayRate());
            if (Objects.nonNull(repayRateEnum)) {
                renderMap.put(RenderParameterKeyHolder.PAYMENT_WAY, repayRateEnum.display);
            } else {
                renderMap.put(RenderParameterKeyHolder.PAYMENT_WAY, "    ");
            }
            Long earnest = contractLeasePrice.getEarnestMoney();
            if (Objects.nonNull(earnest) && earnest > 0) {
                renderMap.put(RenderParameterKeyHolder.EARNEST_WAY, "1");
                BigDecimal b = NumberUtil.div(earnest.toString(), GlobalConstants.MONEY_MULTIPLE);
                renderMap.put(RenderParameterKeyHolder.EARNEST_CN, NumberChineseFormatter.format(b.doubleValue(), true, true));
                renderMap.put(RenderParameterKeyHolder.EARNEST, this.toYuan(earnest));
                renderMap.put(RenderParameterKeyHolder.EARNEST_PAY_WAY, "1");
            } else {
                renderMap.put(RenderParameterKeyHolder.EARNEST_WAY, "2");
                renderMap.put(RenderParameterKeyHolder.EARNEST_CN, "\\");
                renderMap.put(RenderParameterKeyHolder.EARNEST, "\\");
                renderMap.put(RenderParameterKeyHolder.EARNEST_PAY_WAY, "3");
            }
            Long nominalPrice = contractLeasePrice.getNominalPrice();
            if (Objects.nonNull(nominalPrice)) {
                BigDecimal b = NumberUtil.div(nominalPrice.toString(), GlobalConstants.MONEY_MULTIPLE);
                renderMap.put(RenderParameterKeyHolder.NOMINAL_PRICE_CN, NumberChineseFormatter.format(b.doubleValue(), true, true));
                renderMap.put(RenderParameterKeyHolder.NOMINAL_PRICE, this.toYuan(nominalPrice));
            }
        }
        // 保险
        ProjReviewMeetMinuteBaseInfo projReviewMeetMinuteBaseInfo = SpringUtil.getBean(ProjReviewMeetMinuteBaseInfoService.class).getOne(
                Wrappers.<ProjReviewMeetMinuteBaseInfo>lambdaQuery()
                        .eq(ProjReviewMeetMinuteBaseInfo::getProjReviewId, contractBaseInfo.getProjReviewId())
                        .eq(ProjReviewMeetMinuteBaseInfo::getMeetMinuteStatus, MeetMinuteStatuesEnum.EFFECT.name())
                        .orderByDesc(ProjReviewMeetMinuteBaseInfo::getId)
                        .last(StringUtil.mysqlLimitOne())
        );
        // 取评审会纪要中“保险安排”模块，“无需购买保险”填3，“承租人购买保险”填2，“我司购买保险”填1
        if (Objects.nonNull(projReviewMeetMinuteBaseInfo)) {
            if (Objects.equals(projReviewMeetMinuteBaseInfo.getInsurancePurchaser(), ProjectInsurancePurchaserEnum.NOT.name())) {
                renderMap.put("insuranceWay", "3");
                renderMap.put("insuranceCategory", "\\");
            }
            if (Objects.equals(projReviewMeetMinuteBaseInfo.getInsurancePurchaser(), ProjectInsurancePurchaserEnum.LESSEE_BUY.name())) {
                renderMap.put("insuranceWay", "2");
                // 险种
                ProjectPolicyTypeEnum projectPolicyTypeEnum = ProjectPolicyTypeEnum.of(projReviewMeetMinuteBaseInfo.getPolicyType());
                if (projectPolicyTypeEnum == ProjectPolicyTypeEnum.OTHER) {
                    renderMap.put("insuranceCategory", StrUtil.isNotBlank(projReviewMeetMinuteBaseInfo.getPolicyTypeValue()) ? projReviewMeetMinuteBaseInfo.getPolicyTypeValue() : "             ");
                } else {
                    renderMap.put("insuranceCategory", Optional.ofNullable(projectPolicyTypeEnum).map(ProjectPolicyTypeEnum::display).orElse("             "));
                }
            }
            if (Objects.equals(projReviewMeetMinuteBaseInfo.getInsurancePurchaser(), ProjectInsurancePurchaserEnum.OUR_COMPANY_BUY.name())) {
                renderMap.put("insuranceWay", "1");
                renderMap.put("insuranceCategory", "\\");
            }
        } else {
            renderMap.put("insuranceWay", "   ");
            renderMap.put("insuranceCategory", "             ");
        }
        renderMap.put(RenderParameterKeyHolder.FUNDS_PURPOSE, contractBaseInfo.getFundsPurpose());
        // 起租日
        LocalDate rentStartDate = contractBaseInfo.getActualLeaseDate();
        if (Objects.nonNull(rentStartDate)) {
            renderMap.put(RenderParameterKeyHolder.START_YEAR, rentStartDate.getYear());
            renderMap.put(RenderParameterKeyHolder.START_MONTH, rentStartDate.getMonthValue());
            renderMap.put(RenderParameterKeyHolder.START_DAY, rentStartDate.getDayOfMonth());
        } else {
            renderMap.put(RenderParameterKeyHolder.START_YEAR, "    ");
            renderMap.put(RenderParameterKeyHolder.START_MONTH, "  ");
            renderMap.put(RenderParameterKeyHolder.START_DAY, "  ");
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
            renderMap.put(RenderParameterKeyHolder.MY_BANK_ACCOUNT_NAME, myAccount.getAccountName());
            renderMap.put(RenderParameterKeyHolder.MY_BANK_NAME, myAccount.getAccountAddress());
            renderMap.put(RenderParameterKeyHolder.MY_BANK_ACCOUNT, myAccount.getAccountNum());
        } else {
            renderMap.put(RenderParameterKeyHolder.MY_BANK_ACCOUNT_NAME, "         ");
            renderMap.put(RenderParameterKeyHolder.MY_BANK_NAME, "            ");
            renderMap.put(RenderParameterKeyHolder.MY_BANK_ACCOUNT, "              ");
        }
        // 填充乙方账户
        if (!CollectionUtils.isEmpty(yiContractAccountList)) {
            List<String> accounts = new ArrayList<>(contractAccountList.size());
            for (ContractAccount contractAccount : yiContractAccountList) {
                String accountText = String.format(COLLECT_MONEY_ACCOUNT_TEXT_TEMPLATE, contractAccount.getAccountName(), contractAccount.getAccountAddress(), contractAccount.getAccountNum());
                accounts.add(accountText);
            }
            renderMap.put(RenderParameterKeyHolder.COLLECT_MONEY_ACCOUNT_LIST, Joiner.on("\n").join(accounts));
        } else {
            renderMap.put(RenderParameterKeyHolder.COLLECT_MONEY_ACCOUNT_LIST, "户名：[        ]\n开户行：[           ]\n账号：[        ]");
        }
        // 担保方式文案渲染
        List<ContractGuarantor> contractGuarantorList = businessDataRepository.listContractGuarantor(contractBaseInfo.getId());
        renderMap.put(RenderParameterKeyHolder.GUARANTEE_WAY_LIST, this.renderGuarantorText(contractGuarantorList));
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1." + projectBizType.display + "合同-" + leaseType.display + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private static final String COLLECT_MONEY_ACCOUNT_TEXT_TEMPLATE = "户名：[%s]\n开户行：[%s]\n账号：[%s]";

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

        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private FileTemplate getFileTemplate(ContractBaseInfo contractBaseInfo) {
        // 获取合同承租人信息
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractTenantryMap)) {
            throw new MithrasException("没有找到任何承租人信息");
        }
        FileTemplate fileTemplate;
        if (contractTenantryMap.size() == 1) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_主合同_回租_单一承租人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_主合同_回租_共同承租人.docx");
        }
        return fileTemplate;
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 合同编号
        public static final String CONTRACT_CODE = "contractCode";
        // 第一承租人名称
        public static final String FIRST_LESSEE_NAME = "firstLesseeName";
        // 第一承租人法定代表人
        public static final String FIRST_LESSEE_LEGAL_REPRESENTATIVE = "firstLesseeLegalRepresentative";
        // 第一承租人联系地址
        public static final String FIRST_LESSEE_ADDRESS = "firstLesseeAddress";
        // 第一承租人联系人
        public static final String FIRST_LESSEE_CONTACT = "firstLesseeContact";
        // 第一承租人联系人手机
        public static final String FIRST_LESSEE_CONTACT_MOBILE = "firstLesseeContactMobile";
        // 第一承租人联系人座机
        public static final String FIRST_LESSEE_CONTACT_TELEPHONE = "firstLesseeContactTelephone";
        // 第一承租人电子邮箱
        public static final String FIRST_LESSEE_EMAIL = "firstLesseeEmail";
        // 第二承租人名称
        public static final String SECOND_LESSEE_NAME = "secondLesseeName";
        // 第二承租人法定代表人
        public static final String SECOND_LESSEE_LEGAL_REPRESENTATIVE = "secondLesseeLegalRepresentative";
        // 第二承租人联系地址
        public static final String SECOND_LESSEE_ADDRESS = "secondLesseeAddress";
        // 第二承租人联系人
        public static final String SECOND_LESSEE_CONTACT = "secondLesseeContact";
        // 第二承租人联系人手机
        public static final String SECOND_LESSEE_CONTACT_MOBILE = "secondLesseeContactMobile";
        // 第二承租人联系人座机
        public static final String SECOND_LESSEE_CONTACT_TELEPHONE = "secondLesseeContactTelephone";
        // 第二承租人电子邮箱
        public static final String SECOND_LESSEE_EMAIL = "secondLesseeEmail";
        // 租赁期限
        public static final String MONTH_COUNT = "monthCount";
        // 还款期数
        public static final String REPAY_TIMES = "repayTimes";
        // 起租日 年
        public static final String START_YEAR = "startYear";
        // 起租日 月
        public static final String START_MONTH = "startMonth";
        // 起租日 日
        public static final String START_DAY = "startDay";
        // 合同金额 大写
        public static final String CONTRACT_AMOUNT_CN = "contractAmountCN";
        // 合同金额 小写
        public static final String CONTRACT_AMOUNT = "contractAmount";
        // 手续费 大写
        public static final String COMMISSION_AMOUNT_CN = "commissionAmountCN";
        // 手续费 小写
        public static final String COMMISSION_AMOUNT = "commissionAmount";
        // 手续费-不含税
        public static final String COMMISSION_EXCLUDE_TAX = "commissionExcludeTax";
        // 手续费-税额
        public static final String COMMISSION_TAX = "commissionTax";
        // 资金用途
        public static final String FUNDS_PURPOSE = "fundsPurpose";
        // 乙方收款账户信息列表
        public static final String COLLECT_MONEY_ACCOUNT_LIST = "collectMoneyAccountList";
        // 甲方指定账户 - 户名
        public static final String MY_BANK_ACCOUNT_NAME = "myBankAccountName";
        // 甲方指定账户 - 开户行
        public static final String MY_BANK_NAME = "myBankName";
        // 甲方指定账户 - 账号
        public static final String MY_BANK_ACCOUNT = "myBankAccount";
        // 利率方式
        public static final String INTEREST_RATE_TYPE = "interestRateType";
        // 固定利率 租赁利率
        public static final String FIXED_INTEREST_RATE = "fixedInterestRate";
        // 固定利率 LPR加点
//        public static final String FIXED_LPR_ADD = "fixedLprAdd";
        // 固定利率 LPR类型
//        public static final String FIXED_LPR_TYPE = "fixedLprType";
        // 固定利率 LPR
//        public static final String FIXED_LPR = "fixedLpr";
        // 浮动利率 租赁利率
        public static final String FLOAT_INTEREST_RATE = "floatInterestRate";
        // 浮动利率 LPR加点
        public static final String FLOAT_LPR_ADD = "floatLprAdd";
        // 浮动利率 LPR类型
//        public static final String FLOAT_LPR_TYPE = "floatLprType";
        // 浮动利率 LPR
        public static final String FLOAT_LPR = "floatLpr";
        // 租金支付方式
        public static final String PAYMENT_WAY = "paymentWay";
        // 保证金方式
        public static final String EARNEST_WAY = "earnestWay";
        // 保证金金额 大写
        public static final String EARNEST_CN = "earnestCN";
        // 保证金金额 小写
        public static final String EARNEST = "earnest";
        // 保证金支付方式
        public static final String EARNEST_PAY_WAY = "earnestPayWay";
        // 名义货价 大写
        public static final String NOMINAL_PRICE_CN = "nominalPriceCN";
        // 名义货价 小写
        public static final String NOMINAL_PRICE = "nominalPrice";
        // 担保方式文案列表
        public static final String GUARANTEE_WAY_LIST = "guaranteeWayList";
        // 项目主办名称
        public static final String SPONSOR_USER_NAME = "sponsorUserName";
        // 项目主办邮箱
        public static final String SPONSOR_USER_MAIL = "sponsorUserMail";
        // 项目主办手机
        public static final String SPONSOR_USER_PHONE = "sponsorUserPhone";
    }
}
