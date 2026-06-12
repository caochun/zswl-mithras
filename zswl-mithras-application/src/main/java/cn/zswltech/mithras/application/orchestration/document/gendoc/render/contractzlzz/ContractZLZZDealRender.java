package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contractzlzz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
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
 * @description 租赁-直租-主合同-直租买卖合同
 */
@Component
public class ContractZLZZDealRender extends AbstractContractZLZZRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 使用人（承租人）
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        renderMap.put("tenantryTable", this.renderTenantry("使用人名称", contractTenantryList, false));

        // 租赁物清单
        List<ContractLeaseItem> contractLeaseItemList = businessDataRepository.listContractLeaseItem(contractBaseInfo.getId());
        Assert.notEmpty(contractLeaseItemList, () -> MithrasException.newException("租赁物清单为空"));
        List<String> leaseItemNameList = new ArrayList<>(contractLeaseItemList.size());
        long total = 0;
        for (ContractLeaseItem contractLeaseItem : contractLeaseItemList) {
            if (StrUtil.isNotBlank(contractLeaseItem.getName())) {
                leaseItemNameList.add(contractLeaseItem.getName());
            }
            if (Objects.nonNull(contractLeaseItem.getOriginalBookValue())) {
                total = total + contractLeaseItem.getOriginalBookValue();
            }
        }
        if (CollectionUtil.isNotEmpty(leaseItemNameList)) {
            renderMap.put("leaseItemListText", StrUtil.join("、", leaseItemNameList));
        } else {
            renderMap.put("leaseItemListText", "            ");
        }
        if (total > 0) {
            renderMap.put("leaseItemBookValueCN", NumberChineseFormatter.format(NumberUtil.div(String.valueOf(total), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true));
            renderMap.put("leaseItemBookValue", this.toYuan(total));
        } else {
            renderMap.put("leaseItemBookValueCN", "             ");
            renderMap.put("leaseItemBookValue", "             ");
        }

        // 签字（样式1）
        StringBuilder stringBuilder1 = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder1.append("丙方（盖章）：").append(contractTenantryList.get(0).getLesseeName()).append("\n\n    ");
            stringBuilder1.append("法定代表人/授权代表：");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder1.append("丙方").append(i).append("（盖章）：").append(contractTenantry.getLesseeName()).append("\n\n    ");
                stringBuilder1.append("法定代表人/授权代表：");
                if (i != contractTenantryList.size()) {
                    stringBuilder1.append("\n\n    ");
                }
            }
        }
        renderMap.put("leaseSignTextStyle1", stringBuilder1.toString());

        // 租赁物清单表
        renderMap.put("leaseItemTable", this.renderLeaseItemTable(contractLeaseItemList));

        // 签字（样式2）
        StringBuilder stringBuilder2 = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder2.append("丙方 （盖章）：").append(contractTenantryList.get(0).getLesseeName()).append("\n\n    ");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder2.append("丙方 ").append(i).append("（盖章）：").append(contractTenantry.getLesseeName()).append("\n\n    ");
                if (i != contractTenantryList.size()) {
                    stringBuilder2.append("\n\n    ");
                }
            }
        }
        renderMap.put("leaseSignTextStyle2", stringBuilder2.toString());

        // 签字（样式3）
        StringBuilder stringBuilder3 = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder3.append("承租人名称（盖章）：").append(contractTenantryList.get(0).getLesseeName()).append("\n\n    ");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder3.append("承租人名称").append(i).append("（盖章）：").append(contractTenantry.getLesseeName()).append("\n\n    ");
                if (i != contractTenantryList.size()) {
                    stringBuilder3.append("\n\n    ");
                }
            }
        }
        renderMap.put("leaseSignTextStyle3", stringBuilder3.toString());

        // 签字（样式4）
        StringBuilder stringBuilder4 = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder4.append("承租人（盖章）：").append(contractTenantryList.get(0).getLesseeName()).append("\n\n    ");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder4.append("承租人").append(i).append("（盖章）：").append(contractTenantry.getLesseeName()).append("\n\n    ");
                if (i != contractTenantryList.size()) {
                    stringBuilder4.append("\n\n    ");
                }
            }
        }
        renderMap.put("leaseSignTextStyle4", stringBuilder4.toString());

        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "1-1.直租买卖合同（可根据实际情况修改）.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractSubTypeEnum.ZL_ZZ_DEAL.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        return contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-1.直租买卖合同（可根据实际情况修改）.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-1.直租买卖合同（可根据实际情况修改）.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
