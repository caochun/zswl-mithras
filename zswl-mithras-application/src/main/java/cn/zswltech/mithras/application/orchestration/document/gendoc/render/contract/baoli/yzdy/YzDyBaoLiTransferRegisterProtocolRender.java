package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.yzdy;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
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
public class YzDyBaoLiTransferRegisterProtocolRender extends AbstractContractRender<ContractBaseInfo> {


    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-4应收账款转让登记协议（有追单一卖方）.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(YEAR, extractYear(baseInfo.getContractCode()));
        renderMap.put(SEQ_NO, extractSeqNo(baseInfo.getContractCode()));
        renderMap.put(CONTRACT_CODE, baseInfo.getContractCode());
        //债权人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry ->
                {
                    //注册地
                    renderMap.put(CREDITOR_NAME, tenantry.getLesseeName());
                    List<CorpAddressInfoLib> addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
                    String corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
                    renderMap.put(REGISTER_ADDRESS, corpRegistryAddress);
                    //法人
                    CorpCommerceInfoLib corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(tenantry.getLesseeId());
                    renderMap.put(LEGAL_PERSON, corpCommerceInfo.getCorpRepresent());
                    //统一社会信用代码
                    Client client = businessDataRepository.getClient(tenantry.getLesseeId());
                    renderMap.put(USC_CODE, client.getUscCode());
                });
        //债务人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "DEBTOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> renderMap.put(DEBTOR_NAME, tenantry.getLesseeName()));
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputstream).render(renderMap);
        template.writeAndClose(outputStream);
        return filename;
    }

    /**
     * @param contractCode e.g：浙商租【2023】保理字第(B-0008)号
     * @return
     */
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
     * 合同编号
     */
    public static final String CONTRACT_CODE = "contractCode";

    /**
     * 年
     */
    public static final String YEAR = "year";

    /**
     * 序号
     */
    public static final String SEQ_NO = "seqNo";
    /**
     * 债权人
     */
    public static final String CREDITOR_NAME = "creditorName";

    /**
     * 债务人
     */
    public static final String DEBTOR_NAME = "debtorName";

    /**
     * 注册地
     */
    public static final String REGISTER_ADDRESS = "registerAddress";

    /**
     * 法人
     */
    public static final String LEGAL_PERSON = "legalPerson";

    /**
     * 统一社会信用代码
     */
    public static final String USC_CODE = "uscCode";

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
        String filename = "1-4应收账款转让登记协议（有追单一卖方）.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e ->Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-4应收账款转让登记协议（有追单一卖方）.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
