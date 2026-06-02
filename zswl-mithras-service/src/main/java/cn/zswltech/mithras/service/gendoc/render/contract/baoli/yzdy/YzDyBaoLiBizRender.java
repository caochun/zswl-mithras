package cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzdy;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpContactInfoLib;
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

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */
@Component
public class YzDyBaoLiBizRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-1国内保理合同（公开型有追索权）-单一卖方.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(CONTRACT_CODE, baseInfo.getContractCode());
        ContractTenantry tenantry = getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).findFirst().orElse(null);
        if (null != tenantry) {
            renderMap.put(CREDITOR_NAME, tenantry.getLesseeName());
            List<CorpAddressInfoLib> addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
            String corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
            String corpWorkAddress = getCorpWorkAddress(addressInfoLibList);
            renderMap.put(REGISTER_ADDRESS, corpRegistryAddress);
            renderMap.put(WORK_ADDRESS, corpWorkAddress);
            //法人
            CorpCommerceInfoLib corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(tenantry.getLesseeId());
            renderMap.put(LEGAL_PERSON, corpCommerceInfo.getCorpRepresent());
        }
        //联系人信息
        CorpContactInfoLib mainContact = getMainContact(baseInfo.getClientId());
        if (null != mainContact) {
            renderMap.put(CONTACT_NAME, mainContact.getName());
            renderMap.put(CONTACT_MAIL, mainContact.getMail());
            renderMap.put(CONTACT_MOBILE, mainContact.getTelephone());
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
    public static final String REGISTER_ADDRESS = "registerAddress";
    /**
     * 工作地址
     */
    public static final String WORK_ADDRESS = "workAddress";

    /**
     * 债权人
     */
    public static final String CREDITOR_NAME = "creditorName";
    /**
     * 法人
     */
    public static final String LEGAL_PERSON = "legalPerson";

    /**
     * 合同金额
     */
    public static final String CONTRACT_AMOUNT = "contractAmount";

    /**
     * 联系人姓名
     */
    public static final String CONTACT_NAME = "contactName";

    /**
     * 联系人手机号
     */
    public static final String CONTACT_MOBILE = "contactMobile";

    /**
     * 联系人邮箱
     */
    public static final String CONTACT_MAIL = "contactMail";

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
        String filename = "1-1国内保理合同（公开型有追索权）-单一卖方.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-1国内保理合同（公开型有追索权）-单一卖方.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
