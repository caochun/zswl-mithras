package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contractzlzz;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
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
 * @description 租赁-直租-主合同-起租通知书
 */
@Component
public class ContractZLZZStartRentRender extends AbstractContractZLZZRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 承租人
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        List<String> leaseNameList = contractTenantryList.stream().map(ContractTenantry::getLesseeName).collect(Collectors.toList());
        renderMap.put("leaseNameListText", StrUtil.join("/", leaseNameList));

        // 计划起租日
        if (Objects.nonNull(contractBaseInfo.getEstimatedLeaseDate())) {
            renderMap.put("planStartYear", contractBaseInfo.getEstimatedLeaseDate().getYear());
            renderMap.put("planStartMonth", contractBaseInfo.getEstimatedLeaseDate().getMonthValue());
            renderMap.put("planStartDay", contractBaseInfo.getEstimatedLeaseDate().getDayOfMonth());
        } else {
            renderMap.put("planStartYear", "     ");
            renderMap.put("planStartMonth", "   ");
            renderMap.put("planStartDay", "   ");
        }

        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "1-6.起租通知书.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractSubTypeEnum.ZL_ZZ_START_RENT.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-6.起租通知书.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-6.起租通知书.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
