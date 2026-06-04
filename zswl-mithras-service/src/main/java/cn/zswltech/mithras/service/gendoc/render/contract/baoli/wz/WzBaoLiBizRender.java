package cn.zswltech.mithras.service.gendoc.render.contract.baoli.wz;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.core.application.ContractAccountService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
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
public class WzBaoLiBizRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-国内保理业务合同.docx";
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
            renderMap.put(ADDRESS, corpRegistryAddress);
            //
            CorpAddressInfoLib addressInfo = addressInfoLibList.stream().filter(e -> CorpAddressType.REGISTRY_ADDRESS.name().equals(e.getAddressType())).findFirst().orElse(null);
            if (null != addressInfo) {
                renderMap.put(POST_CODE, addressInfo.getRegionCode());
            }
        }
        CorpContactInfoLib mainContact = getMainContact(baseInfo.getClientId());
        if (null != mainContact) {
            renderMap.put(CONTACT_NAME, mainContact.getName());
            renderMap.put(CONTACT_MAIL, mainContact.getMail());
            renderMap.put(CONTACT_MOBILE, mainContact.getTelephone());
        }
        List<ContractAccount> accountList = getBean(ContractAccountService.class).listByContractUse(baseInfo.getId(), ContractAccountUseEnum.BLSK.name());
        if (CollUtil.isNotEmpty(accountList)) {
            ContractAccount contractAccount = accountList.get(0);
            renderMap.put(CONTRACT_ACCOUNT_NAME, contractAccount.getAccountName());
            renderMap.put(CONTRACT_ACCOUNT_BANK_NAME, contractAccount.getAccountAddress());
            renderMap.put(CONTRACT_ACCOUNT_BANK_ACCOUNT, contractAccount.getAccountNum());
        }
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
     * 联系地
     */
    public static final String ADDRESS = "address";
    /**
     * 债权人
     */
    public static final String CREDITOR_NAME = "creditorName";
    /**
     * 邮编
     */
    public static final String POST_CODE = "postCode";

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
        String filename = "1-国内保理业务合同.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-国内保理业务合同.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
