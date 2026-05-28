package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractLeaseItemFileTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 合同_质押合同_应收账款质押登记协议
 */
@Component
public class ContractTenantryRender extends AbstractContractRender<ContractTenantry> {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientService clientService;

    private final static String templateType = "租赁物文件";

    @Override
    public String render(OutputStream outputStream, ContractTenantry contractTenantry) throws Exception {
        String leaseItemFileType = contractTenantry.getLeaseItemFileType();
        String fileName = (ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.name().equals(leaseItemFileType) ? ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.getDisplay() : ContractLeaseItemFileTypeEnum.INVOICE_SUPPLEMENT_PROMISE_LETTER.getDisplay());
        InputStream inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate(templateType,fileName + ".docx");

        Map<String, Object> renderMap = new HashMap<>();

        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractTenantry.getContractId());
        Client client = clientService.getById(contractBaseInfo.getClientId());
        if(client == null){
            throw new MithrasException("客户不存在");
        }
        Client clientNotNull = businessDataRepository.getClientNotNull(contractTenantry.getLesseeId());
        renderMap.put(RenderParameterKeyHolder.LEASE_NAME, clientNotNull.getClientName());
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CODE, contractBaseInfo.getContractCode());

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
//        String lesseeName = "";
//        if(ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.name().equals(leaseItemFileType)) {
//            lesseeName = client.getClientName();
//        }else if(ContractLeaseItemFileTypeEnum.INVOICE_SUPPLEMENT_PROMISE_LETTER.name().equals(leaseItemFileType)){
//            lesseeName = contractTenantry.getLesseeName();
//        }
        return fileName + "-" + client.getClientName() + ".docx";
    }

    @Override
    protected Set<Long> signClientIds(ContractTenantry contractTenantry) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractTenantry.getContractId());
        return new HashSet<>(ListUtil.of(contractBaseInfo.getClientId()));
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractTenantry contractTenantry) {
        String leaseItemFileType = contractTenantry.getLeaseItemFileType();
        String fileName = (ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.name().equals(leaseItemFileType) ? ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.getDisplay() : ContractLeaseItemFileTypeEnum.INVOICE_SUPPLEMENT_PROMISE_LETTER.getDisplay());
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord(templateType, fileName + ".docx");
        return Optional.of(templateRecord).map(e ->Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractTenantry contractTenantry) {
        String leaseItemFileType = contractTenantry.getLeaseItemFileType();
        String fileName = (ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.name().equals(leaseItemFileType) ? ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.getDisplay() : ContractLeaseItemFileTypeEnum.INVOICE_SUPPLEMENT_PROMISE_LETTER.getDisplay());
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord(templateType, fileName + ".docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 承租人名称
        public static final String LEASE_NAME = "leaseName";
        // 合同编号
        public static final String CONTRACT_CODE = "contractCode";
    }

}
