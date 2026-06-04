package cn.zswltech.mithras.service.gendoc.render.contract.baoli.wz;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
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
public class WzBaoLiTransferNotifyRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "3-应收账款转让通知书.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(NOTIFY_CODE, baseInfo.getContractCode().replace("保理", "转"));
        //债权人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> renderMap.put(CREDITOR_NAME, tenantry.getLesseeName()));
        //债务人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "DEBTOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> renderMap.put(DEBTOR_NAME, tenantry.getLesseeName()));
        //回款账户
        List<ContractAccount> accountList = getBean(ContractAccountService.class).listByContractUse(baseInfo.getId(), ContractAccountUseEnum.BLHK.name());
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
     * 当前合同编号
     */
    public static final String NOTIFY_CODE = "notifyCode";
    /**
     * 债权人
     */
    public static final String CREDITOR_NAME = "creditorName";

    /**
     * 债务人
     */
    public static final String DEBTOR_NAME = "debtorName";

    /**
     * 保理回款账户-账户名称
     */
    public static final String CONTRACT_ACCOUNT_NAME = "contractAccountName";
    /**
     * 保理回款账户-开户行
     */
    public static final String CONTRACT_ACCOUNT_BANK_NAME = "contractAccountBankName";
    /**
     * 保理回款账户-银行账户
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
        String filename = "3-应收账款转让通知书.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "3-应收账款转让通知书.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
