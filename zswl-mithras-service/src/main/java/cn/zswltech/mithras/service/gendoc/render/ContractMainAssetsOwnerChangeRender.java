package cn.zswltech.mithras.service.gendoc.render;

import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description 租赁合同-资产所有权转移确认书
 */
@Component
public class ContractMainAssetsOwnerChangeRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        // 获取数据
        // 承租人列表
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        // 主承租人
        ContractTenantry mainTenanry = contractTenantryMap.values()
                .stream()
                .filter(t -> LesseeTypeEnum.MAIN_LESSSEE.name().equals(t.getLesseeType()))
                .findFirst()
                .orElse(null);

        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("mainLesseeName", Optional.ofNullable(mainTenanry)
                .map(ContractTenantry::getLesseeName)
                .orElse(""));

        InputStream inputStream;
        if (contractTenantryMap.size() == 1) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_资产所有权转移确认书_单一承租人.docx");
        } else {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_资产所有权转移确认书_共同承租人.docx");
            renderMap.put("jointLesseeName", contractTenantryMap.values().stream().filter(ct -> LesseeTypeEnum.JOINT_LESSEE.name().equals(ct.getLesseeType())).map(ContractTenantry::getLesseeName).collect(Collectors.joining("、")));
        }

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1-" + ContractSubTypeEnum.ASSETS_OWNER_CONFIRM.getSort() + "." + ContractSubTypeEnum.ASSETS_OWNER_CONFIRM.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        // 承租人列表
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        // 承租人列表
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private FileTemplate getFileTemplate(ContractBaseInfo contractBaseInfo) {
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        FileTemplate fileTemplate;
        if (contractTenantryMap.size() == 1) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_资产所有权转移确认书_单一承租人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_资产所有权转移确认书_共同承租人.docx");
        }
        return fileTemplate;
    }
}
