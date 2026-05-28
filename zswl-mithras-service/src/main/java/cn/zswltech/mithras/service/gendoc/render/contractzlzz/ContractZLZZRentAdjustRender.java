package cn.zswltech.mithras.service.gendoc.render.contractzlzz;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
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
 * @date 2023/4/19
 * @description 租赁-直租-主合同-租金租前息调整通知书
 */
@Component
public class ContractZLZZRentAdjustRender extends AbstractContractZLZZRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(8);
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 承租人
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        List<String> nameList = contractTenantryList.stream().map(ContractTenantry::getLesseeName).collect(Collectors.toList());
        renderMap.put("tenantryNameListText", StrUtil.join("/", nameList));

        // 签字
        StringBuilder stringBuilder = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder.append("承租人：").append(contractTenantryList.get(0).getLesseeName()).append("（盖章）");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder.append("承租人").append(i).append("：").append(contractTenantry.getLesseeName()).append("（盖章）");
                if (i != contractTenantryList.size()) {
                    stringBuilder.append("\n");
                }
            }
        }
        renderMap.put("leaseNameListText", stringBuilder.toString());

        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "1-10.租金租前息调整通知书.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractSubTypeEnum.ZL_ZZ_BEFOREINTEREST_ADJUST.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

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
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-10.租金租前息调整通知书.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-10.租金租前息调整通知书.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
