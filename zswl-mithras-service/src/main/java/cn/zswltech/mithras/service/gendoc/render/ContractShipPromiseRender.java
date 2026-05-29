package cn.zswltech.mithras.service.gendoc.render;

import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2024/10/16
 * @description 船舶_承诺函_租赁登记
 */
@Component
public class ContractShipPromiseRender extends AbstractContractRender<ContractBaseInfo> {
    @Resource
    private ClientService clientService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        ContractTenantry contractTenantry = contractTenantryService.getMain(contractBaseInfo.getId());
        if (Objects.isNull(contractTenantry)) {
            throw new MithrasException("主承租人不存在");
        }
        Client client = clientService.getById(contractTenantry.getLesseeId());
        if (Objects.isNull(client)) {
            throw new MithrasException("主承租人客户信息不存在");
        }
        renderMap.put("firstLesseeName", client.getClientName());
        // 找法人
        CorpCommerceInfo corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(client.getId());
        renderMap.put("legalRepresentative", Optional.ofNullable(corpCommerceInfo).map(CorpCommerceInfo::getCorpRepresent).orElse(""));
        // 渲染文档
//        InputStream inputStream = FileUtil.getInputStream("/Users/mockorz/Downloads/船舶_承诺函_租赁登记.docx");
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-合同清单", "船舶_承诺函_租赁登记.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "承诺函-租赁登记" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        return contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-合同清单", "船舶_承诺函_租赁登记.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-合同清单", "船舶_承诺函_租赁登记.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
