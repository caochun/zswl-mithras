package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.Tables;
import liquibase.pro.packaged.S;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

@Component
public class PurposeOfFundsRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        //  主承租人
        ContractTenantry tenantry = contractTenantryService.lambdaQuery()
                .eq(ContractTenantry::getContractId, contractBaseInfo.getId())
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .one();
        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("fundsPurpose", contractBaseInfo.getFundsPurpose());
        renderMap.put("lesseeName", Objects.isNull(tenantry) ? "/" : tenantry.getLesseeName());
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(getBean(FileTemplateService.class).getTemplate("合同-合同清单", "款项用途确认函.docx")).render(renderMap);
        template.writeAndClose(outputStream);
        return "款项用途确认函" + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-合同清单", "款项用途确认函.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-合同清单", "款项用途确认函.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
