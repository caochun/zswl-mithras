package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合同_质押合同_确认函
 */
@Component
public class ContractPledgeConfirmationRender extends AbstractContractRender<ContractPledge> {

    @Override
    public String render(OutputStream outputStream, ContractPledge contractPledge) throws Exception {
        InputStream inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate("合同-质押合同", "5-2.确认函.docx");
        Map<String, Object> renderMap = new HashMap<>();
        Long contractId = contractPledge.getContractId();
        ContractBaseInfo contractBaseInfo = this.getContractBaseInfo(contractId);

        List<Long> clientIds = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        List<String> pledgeNameList = clientMap.values().stream().map(Client::getClientName).collect(Collectors.toList());
        renderMap.put(RenderParameterKeyHolder.PLEDGE_NAME, Joiner.on("、").join(pledgeNameList));

        renderMap.put(RenderParameterKeyHolder.PLEDGE_CONTRACT_CODE, contractPledge.getPledgeContractCode());
        ContractTenantry contractTenantry = this.getMainTenantry(contractId, this.getContractTenantryType(contractBaseInfo));
        Client clientNotNull = businessDataRepository.getClientNotNull(contractTenantry.getLesseeId());
        renderMap.put(RenderParameterKeyHolder.MAIN_LESSEE_NAME, Optional.ofNullable(clientNotNull).map(Client::getClientName).orElse("             "));

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "确认函.docx";
    }

    @Override
    protected Set<Long> signClientIds(ContractPledge contractPledge) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractPledge contractPledge) {
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord("合同-质押合同", "5-2.确认函.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractPledge contractPledge) {
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord("合同-质押合同", "5-2.确认函.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 质押合同编号
        public static final String PLEDGE_CONTRACT_CODE = "pledgeContractCode";
        // 出质人名称
        public static final String PLEDGE_NAME = "pledgeName";
        // 主承租人名称
        public static final String MAIN_LESSEE_NAME = "mainLesseeName";
    }

}
