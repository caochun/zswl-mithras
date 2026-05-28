package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.kpi.config.TaxRateEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description 咨询合同
 */
@Component
public class ContractConsultingRender extends AbstractContractRender<ContractBaseInfo> {
    @Resource
    private ContractPriceService contractPriceService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(64);
        Long contractId = contractBaseInfo.getId();
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractId);
        if (CollectionUtils.isEmpty(contractTenantryMap)) {
            throw new MithrasException("没有找到承租人");
        }
        ContractTenantry mainTenantry = null;
        List<ContractTenantry> joinTenantryList = new LinkedList<>();
        for (Map.Entry<Long, ContractTenantry> entry : contractTenantryMap.entrySet()) {
            ContractTenantry ct = entry.getValue();
            if (Objects.equals(LesseeTypeEnum.MAIN_LESSSEE.name(), ct.getLesseeType())) {
                mainTenantry = ct;
            } else {
                joinTenantryList.add(ct);
            }
        }
        if (Objects.isNull(mainTenantry)) {
            throw new MithrasException("主承租人为空");
        }
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(contractTenantryMap.keySet());
        renderMap.put(RenderParameterKeyHolder.CONSULTING_CONTRACT_CODE, contractBaseInfo.getConsultingContractCode());

        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 填充主办信息
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_NAME, Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_MAIL, Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_PHONE, Optional.ofNullable(sponsorPhone).orElse(""));


        // 主承租人信息填充
        Client mainClient = clientMap.get(mainTenantry.getLesseeId());
        renderMap.put(RenderParameterKeyHolder.LESSEE_NAME, mainClient.getClientName());
        CorpCommerceInfoLib corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(mainClient.getId());
        renderMap.put(RenderParameterKeyHolder.LESSEE_REPRESENT, corpCommerceInfo.getCorpRepresent());
        List<CorpAddressInfoLib> corpAddressInfoList = businessDataRepository.getCorpAddressInfo(mainClient.getId());
        renderMap.put(RenderParameterKeyHolder.LESSEE_ADDRESS, this.getCorpRegistryAddress(corpAddressInfoList));
        if (Objects.nonNull(mainTenantry.getContactId())) {
            CorpContactInfoLib corpContactInfo = this.getNewestContact(mainTenantry.getContactId());
            renderMap.put(RenderParameterKeyHolder.LESSEE_CONTACT, Optional.ofNullable(corpContactInfo).map(CorpContactInfo::getName).orElse(""));
            renderMap.put(RenderParameterKeyHolder.LESSEE_CONTACT_MOBILE, Optional.ofNullable(corpContactInfo).map(CorpContactInfo::getTelephone).orElse(""));
            renderMap.put(RenderParameterKeyHolder.LESSEE_CONTACT_EMAIL, Optional.ofNullable(corpContactInfo).map(CorpContactInfo::getMail).orElse(""));
            if (Objects.nonNull(corpContactInfo)) {
                renderMap.put(RenderParameterKeyHolder.LESSEE_CONTACT_TELEPHONE, StrUtil.isBlank(corpContactInfo.getLandlineTelephone()) ? "\\" : corpContactInfo.getLandlineTelephone());
            }
        }
        InputStream inputStream;
        if (CollectionUtil.isEmpty(joinTenantryList)) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-咨询合同", "合同_咨询合同_单一承租人.docx");
        } else {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-咨询合同", "合同_咨询合同_共同承租人.docx");

            // 填充联合承租人信息
            List<String> joinLesseeNameList = joinTenantryList.stream().map(ContractTenantry::getLesseeName).collect(Collectors.toList());
            ContractTenantry joinTenantry = joinTenantryList.get(0);
            renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_NAME, Joiner.on("、").join(joinLesseeNameList));
            CorpCommerceInfoLib joinCorpCommerceInfo = businessDataRepository.getCorpCommerceInfo(joinTenantry.getLesseeId());
            renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_REPRESENT, joinCorpCommerceInfo.getCorpRepresent());
            List<CorpAddressInfoLib> joinCorpAddressInfoList = businessDataRepository.getCorpAddressInfo(joinTenantry.getLesseeId());
            renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_ADDRESS, this.getCorpRegistryAddress(joinCorpAddressInfoList));
            if (Objects.nonNull(joinTenantry.getContactId())) {
                CorpContactInfoLib joinCorpContactInfo = this.getNewestContact(joinTenantry.getContactId());
                renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_CONTACT, Optional.ofNullable(joinCorpContactInfo).map(CorpContactInfo::getName).orElse(""));
                renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_CONTACT_MOBILE, Optional.ofNullable(joinCorpContactInfo).map(CorpContactInfo::getTelephone).orElse(""));
                renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_CONTACT_EMAIL, Optional.ofNullable(joinCorpContactInfo).map(CorpContactInfo::getMail).orElse(""));
                if (Objects.nonNull(joinCorpContactInfo)) {
                    renderMap.put(RenderParameterKeyHolder.JOIN_LESSEE_CONTACT_TELEPHONE, StrUtil.isBlank(joinCorpContactInfo.getLandlineTelephone()) ? "\\" : joinCorpContactInfo.getLandlineTelephone());
                }
            }
        }
        // 报价方案
        ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
        contractPriceDetailREQ.setContractId(contractId);
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(contractPriceDetailREQ);
        String repayRate = null;
        Long consultingFee = null;
        if (Objects.nonNull(contractPriceDetailRSP.getLeasePriceModifyRSP())) {
            repayRate = contractPriceDetailRSP.getLeasePriceModifyRSP().getRepayRate();
            consultingFee = contractPriceDetailRSP.getLeasePriceModifyRSP().getConsultingFee();
        } else if (Objects.nonNull(contractPriceDetailRSP.getFactoringPriceRSP())) {
            repayRate = contractPriceDetailRSP.getFactoringPriceRSP().getRateType();
            consultingFee = contractPriceDetailRSP.getFactoringPriceRSP().getConsultingFee();
        } else if (Objects.nonNull(contractPriceDetailRSP.getAocPriceRSP())) {
            repayRate = contractPriceDetailRSP.getAocPriceRSP().getRateType();
            consultingFee = contractPriceDetailRSP.getAocPriceRSP().getConsultingFee();
        }
        RepayRateEnum repayRateEnum = RepayRateEnum.of(repayRate);
        renderMap.put(RenderParameterKeyHolder.REPAY_RATE, Optional.ofNullable(repayRateEnum).map(RepayRateEnum::display).orElse("    "));
        if (Objects.nonNull(consultingFee)) {
            BigDecimal consultingBigDecimal = NumberUtil.div(consultingFee.toString(), GlobalConstants.MONEY_MULTIPLE);
            renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE_CN, NumberChineseFormatter.format(consultingBigDecimal.doubleValue(), true, true));
            renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE, this.toYuan(consultingFee));
            BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.ZXS_ZL_JYX);
            renderMap.put(RenderParameterKeyHolder.CONSULTING_EXCLUDE_TAX, calculateTaxValue(consultingFee, taxRate, null));
            renderMap.put(RenderParameterKeyHolder.CONSULTING_TAX, calculateTaxValue(consultingFee, taxRate, taxRate));
        } else {
            renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE_CN, "              ");
            renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE, "            ");
            renderMap.put(RenderParameterKeyHolder.CONSULTING_EXCLUDE_TAX, "              ");
            renderMap.put(RenderParameterKeyHolder.CONSULTING_TAX, "              ");
        }
        // 甲方（承租人）账户信息 --- 对应系统中的乙方收款账户
        ContractAccount contractAccount = null;
        List<ContractAccount> contractAccountList = businessDataRepository.listContractAccount(contractId, ContractAccountUseEnum.ZLSK);
        if (CollectionUtil.isNotEmpty(contractAccountList)) {
            List<ContractAccount> yiContractAccount = contractAccountList.stream()
                    .filter(item -> Objects.equals(item.getPayeeType(), ContractAccountPayeeTypeEnum.YI.name()) || Objects.equals(item.getPayeeType(), ContractAccountPayeeTypeEnum.SELLER.name()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(yiContractAccount)) {
                contractAccount = yiContractAccount.get(0);
            }
        }
        renderMap.put(RenderParameterKeyHolder.LEASE_ACCOUNT_NAME, Optional.ofNullable(contractAccount).map(ContractAccount::getAccountName).orElse("                        "));
        renderMap.put(RenderParameterKeyHolder.LEASE_ACCOUNT_BANK, Optional.ofNullable(contractAccount).map(ContractAccount::getAccountAddress).orElse("                   "));
        renderMap.put(RenderParameterKeyHolder.LEASE_ACCOUNT_NO, Optional.ofNullable(contractAccount).map(ContractAccount::getAccountNum).orElse("                   "));
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "2.租赁结构安排及管理咨询合同-" + mainTenantry.getLesseeName() + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private FileTemplate getFileTemplate(ContractBaseInfo contractBaseInfo) {
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractTenantryMap)) {
            throw new MithrasException("没有找到承租人");
        }
        List<ContractTenantry> joinTenantryList = new LinkedList<>();
        for (Map.Entry<Long, ContractTenantry> entry : contractTenantryMap.entrySet()) {
            ContractTenantry ct = entry.getValue();
            if (Objects.equals(LesseeTypeEnum.JOINT_LESSEE.name(), ct.getLesseeType())) {
                joinTenantryList.add(ct);
            }
        }
        FileTemplate fileTemplate;
        if (CollectionUtil.isEmpty(joinTenantryList)) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-咨询合同", "合同_咨询合同_单一承租人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-咨询合同", "合同_咨询合同_共同承租人.docx");
        }
        return fileTemplate;
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 咨询合同编号
        public static final String CONSULTING_CONTRACT_CODE = "consultingContractCode";
        // 承租人名称
        public static final String LESSEE_NAME = "lesseeName";
        // 承租人法人代表
        public static final String LESSEE_REPRESENT = "lesseeRepresent";
        // 承租人联系地址
        public static final String LESSEE_ADDRESS = "lesseeAddress";
        // 承租人联系人
        public static final String LESSEE_CONTACT = "lesseeContact";
        // 承租人联系人手机
        public static final String LESSEE_CONTACT_MOBILE = "lesseeContactMobile";
        // 承租人联系人电子邮箱
        public static final String LESSEE_CONTACT_EMAIL = "lesseeContactEmail";
        // 承租人联系人座机
        public static final String LESSEE_CONTACT_TELEPHONE = "lesseeContactTelephone";
        // 联合承租人名称
        public static final String JOIN_LESSEE_NAME = "joinLesseeName";
        // 承租人法人代表
        public static final String JOIN_LESSEE_REPRESENT = "joinLesseeRepresent";
        // 承租人联系地址
        public static final String JOIN_LESSEE_ADDRESS = "joinLesseeAddress";
        // 承租人联系人
        public static final String JOIN_LESSEE_CONTACT = "joinLesseeContact";
        // 承租人联系人手机
        public static final String JOIN_LESSEE_CONTACT_MOBILE = "joinLesseeContactMobile";
        // 承租人联系人电子邮箱
        public static final String JOIN_LESSEE_CONTACT_EMAIL = "joinLesseeContactEmail";
        // 承租人联系人座机
        public static final String JOIN_LESSEE_CONTACT_TELEPHONE = "joinLesseeContactTelephone";
        // 报价方案 还款频率
        public static final String REPAY_RATE = "repayRate";
        // 报价方案 服务费/咨询费（元） 大写
        public static final String CONSULTING_FEE_CN = "consultingFeeCN";
        // 报价方案 服务费/咨询费（元） 小写
        public static final String CONSULTING_FEE = "consultingFee";
        // 报价方案 不含税金额
        public static final String CONSULTING_EXCLUDE_TAX = "consultingExcludeTax";
        // 报价方案 税额
        public static final String CONSULTING_TAX = "consultingTax";
        // 项目主办名称
        public static final String SPONSOR_USER_NAME = "sponsorUserName";
        // 项目主办邮箱
        public static final String SPONSOR_USER_MAIL = "sponsorUserMail";
        // 项目主办手机
        public static final String SPONSOR_USER_PHONE = "sponsorUserPhone";
        // 甲方（承租人）账户信息-户名
        public static final String LEASE_ACCOUNT_NAME = "leaseAccountName";
        // 甲方（承租人）账户信息-开户行
        public static final String LEASE_ACCOUNT_BANK = "leaseAccountBank";
        // 甲方（承租人）账户信息-账号
        public static final String LEASE_ACCOUNT_NO = "leaseAccountNo";
    }
}
