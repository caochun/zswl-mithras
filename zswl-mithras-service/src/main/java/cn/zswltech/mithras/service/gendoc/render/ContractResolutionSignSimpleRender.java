package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ResolutionTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author BigBear
 * @date 2024/12/26 10:05
 * @description 决议文件签字样本
 */
@Slf4j
@Component
public class ContractResolutionSignSimpleRender extends AbstractContractRender<ContractTenantry> {

    @Resource
    protected MaterialsListService materialsListService;

    @Override
    public String render(OutputStream outputStream, ContractTenantry contractTenantry) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(contractTenantry.getResolutionType());
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的保证人类型");
        }
        String lesseeName = businessDataRepository.getClientNotNull(contractTenantry.getLesseeId()).getClientName();
        renderMap.put(RenderParameterKeyHolder.CLINT_NAME, lesseeName);
        InputStream inputStream;
        String fileName;
        switch (resolutionTypeEnum) {
            case SHAREHOLDERS_RESOLUTION:
                fileName = "股东会成员签字样本";
                break;
            case SHAREHOLDER_DECISION:
                fileName = "股东成员签字样本";
                break;
            case DIRECTORS_RESOLUTION:
            case EXECUTE_DIRECTOR_RESOLUTION:
                fileName = "董事会成员签字样本";
                break;
            default: {
                throw new MithrasException("不支持的决议类型");
            }
        }
        inputStream = getBean(FileTemplateService.class).getTemplate("合同-决议文件", fileName + GlobalConstants.OFFICE_WORD_SUFFIX);

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream);
        template.render(renderMap);
        template.writeAndClose(outputStream);
        return fileName + "（承租人）-" + lesseeName + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractTenantry contractTenantry) {
        return new HashSet<>(ListUtil.of(contractTenantry.getLesseeId()));
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractTenantry contractTenantry) {
        FileTemplate templateRecord = getFileTemplate(contractTenantry);
        return Optional.of(templateRecord).map(e -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), e.getFaceSignShowFlag())).orElse(false);
    }

    private static FileTemplate getFileTemplate(ContractTenantry contractTenantry) {
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(contractTenantry.getResolutionType());
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的保证人类型");
        }
        String fileName;
        switch (resolutionTypeEnum) {
            case SHAREHOLDERS_RESOLUTION:
                fileName = "股东会成员签字样本";
                break;
            case SHAREHOLDER_DECISION:
                fileName = "股东成员签字样本";
                break;
            case DIRECTORS_RESOLUTION:
            case EXECUTE_DIRECTOR_RESOLUTION:
                fileName = "董事会成员签字样本";
                break;
            default: {
                throw new MithrasException("不支持的决议类型");
            }
        }
        return getBean(FileTemplateService.class).getTemplateRecord("合同-决议文件", fileName + GlobalConstants.OFFICE_WORD_SUFFIX);
    }

    @Override
    protected String customTemplateKey(ContractTenantry contractTenantry) {
        FileTemplate fileTemplate = getFileTemplate(contractTenantry);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }


    private static class RenderParameterKeyHolder {
        // 承租人
        public static final String LESSEE_NAME = "lesseeName";
        // 租期年份
        public static final String LEASE_MONTH_COUNT = "leaseMonthCount";
        // 合同金额
        public static final String APPLY_CREDIT_AMOUNT = "applyCreditAmount";
        // 客户名称
        public static final String CLINT_NAME = "clientName";
    }
}
