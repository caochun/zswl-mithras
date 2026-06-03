package cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.service.contract.ContractAccountService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */
@Component
public class YzGtBaoLiBizRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-1国内保理合同（公开型有追索权）.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(CONTRACT_CODE, baseInfo.getContractCode());
        //债务人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "DEBTOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> renderMap.put(DEBTOR_NAME, tenantry.getLesseeName()));
        //多债权人
        List<ContractTenantry> tenantryList = getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).sorted(Comparator.comparingLong(ContractTenantry::getId)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(tenantryList)) {
            ContractTenantry tenantry = tenantryList.get(0);
            renderMap.put(CREDITOR_NAME_1, tenantry.getLesseeName());
            List<CorpAddressInfoLib> addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
            String corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
            String corpWorkAddress = getCorpWorkAddress(addressInfoLibList);
            renderMap.put(REGISTER_ADDRESS_1, corpRegistryAddress);
            renderMap.put(WORK_ADDRESS_1, corpWorkAddress);
            //法人
            CorpCommerceInfoLib corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(tenantry.getLesseeId());
            renderMap.put(LEGAL_PERSON_1, corpCommerceInfo.getCorpRepresent());
            //联系人
            CorpContactInfoLib mainContact = getMainContact(tenantry.getLesseeId());
            if (null != mainContact) {
                renderMap.put(CONTACT_NAME_1, mainContact.getName());
                renderMap.put(CONTACT_MAIL_1, mainContact.getMail());
                renderMap.put(CONTACT_MOBILE_1, mainContact.getTelephone());
            }
            //
            if (tenantryList.size() > 1) {
                tenantry = tenantryList.get(1);
                renderMap.put(CREDITOR_NAME_2, tenantry.getLesseeName());
                addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
                corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
                corpWorkAddress = getCorpWorkAddress(addressInfoLibList);
                renderMap.put(REGISTER_ADDRESS_2, corpRegistryAddress);
                renderMap.put(WORK_ADDRESS_2, corpWorkAddress);
                //法人
                corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(tenantry.getLesseeId());
                renderMap.put(LEGAL_PERSON_2, corpCommerceInfo.getCorpRepresent());
                //
                mainContact = getMainContact(tenantry.getLesseeId());
                if (null != mainContact) {
                    renderMap.put(CONTACT_NAME_2, mainContact.getName());
                    renderMap.put(CONTACT_MAIL_2, mainContact.getMail());
                    renderMap.put(CONTACT_MOBILE_2, mainContact.getTelephone());
                }
            }

        }
        //收款账户信息
        List<ContractAccount> accountList = getBean(ContractAccountService.class).listByContractUse(baseInfo.getId(), ContractAccountUseEnum.BLSK.name());
        if (CollUtil.isNotEmpty(accountList)) {
            ContractAccount contractAccount = accountList.get(0);
            renderMap.put(CONTRACT_ACCOUNT_NAME, contractAccount.getAccountName());
            renderMap.put(CONTRACT_ACCOUNT_BANK_NAME, contractAccount.getAccountAddress());
            renderMap.put(CONTRACT_ACCOUNT_BANK_ACCOUNT, contractAccount.getAccountNum());
        }
        //合同金额
        ContractFactoringPrice factoringPrice = businessDataRepository.getContractFactoringPrice(baseInfo.getId());
        Long contractAmount = factoringPrice.getContractAmount();
        String yuan = toYuan(contractAmount);
        renderMap.put(CONTRACT_AMOUNT, yuan);


        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputstream).render(renderMap);
        template.writeAndClose(outputStream);
        return filename;
    }

    /**
     * 合同编号
     */
    public static final String CONTRACT_CODE = "contractCode";

    /**
     * 注册地址
     */
    public static final String REGISTER_ADDRESS_1 = "registerAddress1";
    public static final String REGISTER_ADDRESS_2 = "registerAddress2";
    /**
     * 工作地址
     */
    public static final String WORK_ADDRESS_1 = "workAddress1";
    public static final String WORK_ADDRESS_2 = "workAddress2";

    /**
     * 债权人
     */
    public static final String CREDITOR_NAME_1 = "creditorName1";
    public static final String CREDITOR_NAME_2 = "creditorName2";
    /**
     * 法人
     */
    public static final String LEGAL_PERSON_1 = "legalPerson1";
    public static final String LEGAL_PERSON_2 = "legalPerson2";

    /**
     * 合同金额
     */
    public static final String CONTRACT_AMOUNT = "contractAmount";

    /**
     * 联系人姓名
     */
    public static final String CONTACT_NAME_1 = "contactName1";
    public static final String CONTACT_NAME_2 = "contactName2";

    /**
     * 联系人手机号
     */
    public static final String CONTACT_MOBILE_1 = "contactMobile1";
    public static final String CONTACT_MOBILE_2 = "contactMobile2";

    /**
     * 联系人邮箱
     */
    public static final String CONTACT_MAIL_1 = "contactMail1";
    public static final String CONTACT_MAIL_2 = "contactMail2";

    /**
     * 卖方收款账户-账户名称
     */
    public static final String CONTRACT_ACCOUNT_NAME = "contractAccountName";
    /**
     * 卖方收款账户-开户行
     */
    public static final String CONTRACT_ACCOUNT_BANK_NAME = "contractAccountBankName";
    /**
     * 卖方收款账户-银行账户
     */
    public static final String CONTRACT_ACCOUNT_BANK_ACCOUNT = "contractAccountBankAccount";

    /**
     * 债务人
     */
    public static final String DEBTOR_NAME = "debtorName";

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        String filename = "1-1国内保理合同（公开型有追索权）.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-1国内保理合同（公开型有追索权）.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
