package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzdy;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.contract.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.ContractAccountService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */
@Component
public class YzDyBaoLiTransferNotifyRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-3应收账款转让通知书及回执（有追单一卖方）.docx";
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
        //
        renderMap.put(SEQ_NO, extractSeqNo(baseInfo.getContractCode()));
        renderMap.put(YEAR, extractYear(baseInfo.getContractCode()));


        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputstream).render(renderMap);
        template.writeAndClose(outputStream);
        return filename;
    }

    private static String extractYear(String contractCode) {
        String regex = "【\\d+】";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contractCode);
        if (matcher.find()) {
            String group = matcher.group();
            return group.substring(1, group.length() - 1);
        }
        return "";
    }

    private static String extractSeqNo(String contractCode) {
        String regex = "\\(B-\\d+\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contractCode);
        if (matcher.find()) {
            String group = matcher.group();
            return group.substring(3, group.length() - 1);
        }
        return "";
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

    /**
     * 序号
     */
    public static final String SEQ_NO = "seqNo";
    /**
     * 年
     */
    public static final String YEAR = "year";


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
        String filename = "1-3应收账款转让通知书及回执（有追单一卖方）.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-3应收账款转让通知书及回执（有追单一卖方）.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
