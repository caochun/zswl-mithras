package cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yibin
 */
@Component
public class YzGtBaoLiTransferRegisterProtocolRender extends AbstractContractRender<ContractBaseInfo> {

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-4应收账款转让登记协议.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(PROTOCOL_CODE, baseInfo.getContractCode().replace("保理", "转登"));
        renderMap.put(CONTRACT_CODE, baseInfo.getContractCode());
        //多债权人
        List<ContractTenantry> tenantryList = getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).sorted(Comparator.comparingLong(ContractTenantry::getId)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(tenantryList)) {
            ContractTenantry tenantry = tenantryList.get(0);
            renderMap.put(CREDITOR_NAME_1, tenantry.getLesseeName());
            //法人
            CorpCommerceInfoLib corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(tenantry.getLesseeId());
            renderMap.put(LEGAL_PERSON_1, corpCommerceInfo.getCorpRepresent());
            //注册地址
            List<CorpAddressInfoLib> addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
            String corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
            renderMap.put(REGISTER_ADDRESS_1, corpRegistryAddress);
            //统一社会信用代码
            Client client = businessDataRepository.getClient(tenantry.getLesseeId());
            renderMap.put(USC_CODE_1, client.getUscCode());

            //
            if (tenantryList.size() > 1) {
                tenantry = tenantryList.get(1);
                renderMap.put(CREDITOR_NAME_2, tenantry.getLesseeName());
                //法人
                corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(tenantry.getLesseeId());
                renderMap.put(LEGAL_PERSON_2, corpCommerceInfo.getCorpRepresent());
                //注册地址
                addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
                corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
                renderMap.put(REGISTER_ADDRESS_2, corpRegistryAddress);
                //统一社会信用代码
                client = businessDataRepository.getClient(tenantry.getLesseeId());
                renderMap.put(USC_CODE_2, client.getUscCode());
            }
        }
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
     * 注册地址
     */
    public static final String REGISTER_ADDRESS_1 = "registerAddress1";
    public static final String REGISTER_ADDRESS_2 = "registerAddress2";
    /**
     * 法人
     */
    public static final String LEGAL_PERSON_1 = "legalPerson1";
    public static final String LEGAL_PERSON_2 = "legalPerson2";

    /**
     * 当前合同编号
     */
    public static final String PROTOCOL_CODE = "protocolCode";
    /**
     * 合同编号
     */
    public static final String CONTRACT_CODE = "contractCode";

    /**
     * 债权人
     */
    public static final String CREDITOR_NAME_1 = "creditorName1";
    public static final String CREDITOR_NAME_2 = "creditorName2";

    /**
     * 债务人
     */
    public static final String DEBTOR_NAME = "debtorName";

    /**
     * 统一社会信用代码
     */
    public static final String USC_CODE_1 = "uscCode1";
    public static final String USC_CODE_2 = "uscCode2";

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
        String filename = "1-4应收账款转让登记协议.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-4应收账款转让登记协议.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
