package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合同_质押合同_应收账款质押登记协议
 */
@Component
public class ContractPledgeProtocolRender extends AbstractContractRender<ContractPledge> {
    @Override
    public String render(OutputStream outputStream, ContractPledge contractPledge) throws Exception {
        InputStream inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate("合同-质押合同", "6-1.应收账款质押登记协议.docx");
        Map<String, Object> renderMap = new HashMap<>();

        List<Long> clientIds = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        List<String> pledgeNameList = clientMap.values().stream().map(Client::getClientName).collect(Collectors.toList());
        renderMap.put(RenderParameterKeyHolder.PLEDGE_NAME, Joiner.on("、").join(pledgeNameList));

        renderMap.put(RenderParameterKeyHolder.PLEDGE_CONTRACT_CODE, contractPledge.getPledgeContractCode());

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "应收账款质押登记协议.docx";
    }

    @Override
    protected Set<Long> signClientIds(ContractPledge contractPledge) {
        if (StrUtil.isBlank(contractPledge.getPledgeIds())) {
            return null;
        }
        List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
        return new HashSet<>(ids);
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractPledge contractPledge) {
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord("合同-质押合同", "6-1.应收账款质押登记协议.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractPledge contractPledge) {
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord("合同-质押合同", "6-1.应收账款质押登记协议.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 质押合同编号
        public static final String PLEDGE_CONTRACT_CODE = "pledgeContractCode";
        // 出质人名称
        public static final String PLEDGE_NAME = "pledgeName";
    }

}
