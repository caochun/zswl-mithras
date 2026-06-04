package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ResolutionTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.contract.core.application.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author zx
 * @date 2024/3/30
 * @description 决议文件
 */
@Slf4j
@Component
public class ContractResolutionRender extends AbstractContractRender<ContractTenantry> {

    @Resource
    protected MaterialsListService materialsListService;
    @Autowired
    private ContractLeasePriceService leasePriceService;
    @Resource
    private ContractResolutionSignSimpleRender contractResolutionSignSimpleRender;

    @Override
    public String render(OutputStream outputStream, ContractTenantry contractTenantry) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(contractTenantry.getResolutionType());
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的决议类型");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractTenantry.getContractId());
        String lesseeName = businessDataRepository.getClientNotNull(contractTenantry.getLesseeId()).getClientName();
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-决议文件", resolutionTypeEnum.display + "（承租人）" + GlobalConstants.OFFICE_WORD_SUFFIX);
        switch (resolutionTypeEnum) {
            case SHAREHOLDERS_RESOLUTION:
            case SHAREHOLDER_DECISION:
            case DIRECTORS_RESOLUTION:
            case EXECUTE_DIRECTOR_RESOLUTION:
                ContractLeasePrice contractLeasePrice = leasePriceService.detail(new ContractPriceDetailREQ(contractTenantry.getContractId()));
                Integer leaseMonthCount = contractLeasePrice.getLeaseMonthCount();
                renderMap.put(RenderParameterKeyHolder.LESSEE_NAME, lesseeName);
                renderMap.put(RenderParameterKeyHolder.LEASE_YEAR, Objects.isNull(leaseMonthCount) ? "/" : NumberUtil.decimalFormat("0.##", new BigDecimal(leaseMonthCount).divide(new BigDecimal(12), 2, RoundingMode.HALF_UP)));
                // 查询项目批复金额
                ProjReviewLeasePrice projReviewLeasePrice = SpringUtil.getBean(ProjReviewLeasePriceService.class).getByProjectId(contractBaseInfo.getProjReviewId());
                if (Objects.nonNull(projReviewLeasePrice)) {
                    renderMap.put(RenderParameterKeyHolder.PROJECT_APPROVAL_AMOUNT, Objects.isNull(projReviewLeasePrice.getProjectApprovalAmount()) ? "/" : NumberUtil.decimalFormat(",##0.##", projReviewLeasePrice.getProjectApprovalAmount() / 10000));
                }
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                String fileName = contractResolutionSignSimpleRender.render(arrayOutputStream, contractTenantry);
                // 保存文件
                try {
                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
                    materialsListService.add(arrayInputStream, fileName, contractTenantry.getContractId(), ContractTypeEnum.RESOLUTION_FILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                } catch (MithrasException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("生成决议文件（签字样本）发生未知异常[{}]", JSONUtil.toJsonStr(contractTenantry), e);
                    throw new MithrasException("生成决议文件（签字样本）发生未知异常");
                } finally {
                    arrayOutputStream.close();
                }
                break;
            default: {
                throw new MithrasException("不支持的决议类型");
            }
        }
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream);
        template.render(renderMap);
        template.writeAndClose(outputStream);
        return resolutionTypeEnum.display + "（承租人）-" + lesseeName + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(contractTenantry.getResolutionType());
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的决议类型");
        }
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-决议文件", resolutionTypeEnum.display + "（承租人）" + GlobalConstants.OFFICE_WORD_SUFFIX);
        return Optional.of(templateRecord).map(e -> Objects.equals(YesOrNoNumberEnum.YES.getCode(), e.getFaceSignShowFlag())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractTenantry contractTenantry) {
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(contractTenantry.getResolutionType());
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的决议类型");
        }
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-决议文件", resolutionTypeEnum.display + "（承租人）" + GlobalConstants.OFFICE_WORD_SUFFIX);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }


    private static class RenderParameterKeyHolder {
        // 承租人
        public static final String LESSEE_NAME = "lesseeName";
        // 租期年份
        public static final String LEASE_YEAR = "leaseYear";
        // 项目批复金额
        public static final String PROJECT_APPROVAL_AMOUNT = "projectApprovalAmount";
        // 客户名称
        public static final String CLINT_NAME = "clientName";
    }
}
