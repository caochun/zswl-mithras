package cn.zswltech.mithras.service.gendoc.render;

import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/25
 * @description 咨询合同-接受咨询服务确认函
 */
@Component
public class ContractConsultingAcceptConfirmRender extends AbstractContractRender<ContractBaseInfo> {
    private static final String TEMPLATE = "我方：%s （公章）";

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-咨询合同", "合同_咨询合同_附属_接受咨询服务确认函.docx");
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        List<ContractTenantry> mainList = new LinkedList<>();
        List<ContractTenantry> otherList = new LinkedList<>();
        for (ContractTenantry contractTenantry : contractTenantryList) {
            if (Objects.equals(contractTenantry.getLesseeType(), LesseeTypeEnum.MAIN_LESSSEE.name())) {
                mainList.add(contractTenantry);
            } else {
                otherList.add(contractTenantry);
            }
        }
        List<String> textList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(mainList)) {
            for (ContractTenantry contractTenantry : mainList) {
                textList.add(this.getText(contractTenantry));
            }
        }
        if (!CollectionUtils.isEmpty(otherList)) {
            for (ContractTenantry contractTenantry : otherList) {
                textList.add(this.getText(contractTenantry));
            }
        }
        // 咨询合同只有一份，默认用1取
        renderMap.put("consultingContractCode", contractBaseInfo.getConsultingContractCode());
        renderMap.put("lesseeTextList", Joiner.on("\n\n\n").join(textList));
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "2-1.接受咨询服务确认函" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private String getText(ContractTenantry contractTenantry) {
        return String.format(TEMPLATE, contractTenantry.getLesseeName());
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
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-咨询合同", "合同_咨询合同_附属_接受咨询服务确认函.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-咨询合同", "合同_咨询合同_附属_接受咨询服务确认函.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
