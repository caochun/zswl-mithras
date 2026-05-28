package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ResolutionTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import com.alibaba.fastjson.JSONObject;
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
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author zx
 * @date 2024/3/30
 * @description 决议文件
 */
@Slf4j
@Component
public class ContractResolutionGuarantorRender extends AbstractContractRender<JSONObject> {

    @Resource
    protected MaterialsListService materialsListService;
    @Autowired
    private ContractLeasePriceService leasePriceService;

    @Override
    public String render(OutputStream outputStream, JSONObject data) throws Exception {
        List<ContractTenantry> contractTenantryList = data.getObject("contractTenantry", List.class);
        String resolutionType = data.getString("resolutionType");
        Map<String, Object> renderMap = new HashMap<>();
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(resolutionType);
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的决议类型");
        }
        String clientName = data.getString("clientName");
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-决议文件", resolutionTypeEnum.display + "（担保人）" + GlobalConstants.OFFICE_WORD_SUFFIX);
        switch (resolutionTypeEnum) {
            case SHAREHOLDERS_RESOLUTION:
            case SHAREHOLDER_DECISION:
            case DIRECTORS_RESOLUTION:
            case EXECUTE_DIRECTOR_RESOLUTION:
                List<Long> lesseeIdList = contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());
                List<Client> clientMap = businessDataRepository.getClientList(lesseeIdList);
                Optional<ContractTenantry> contractTenantryOptional = contractTenantryList.stream().filter(contractTenantry -> LesseeTypeEnum.MAIN_LESSSEE.name().equals(contractTenantry.getLesseeType())).findFirst();
                contractTenantryOptional.ifPresent(contractTenantry -> {
                    data.put("contractId",contractTenantry.getContractId());
                    ContractLeasePrice contractLeasePrice = leasePriceService.detail(new ContractPriceDetailREQ(contractTenantry.getContractId()));
                    Integer leaseMonthCount = contractLeasePrice.getLeaseMonthCount();
                    // 查询项目批复金额
                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractTenantry.getContractId());
                    ProjReviewLeasePrice projReviewLeasePrice = SpringUtil.getBean(ProjReviewLeasePriceService.class).getByProjectId(contractBaseInfo.getProjReviewId());
                    if (Objects.nonNull(projReviewLeasePrice)) {
                        renderMap.put(RenderParameterKeyHolder.PROJECT_APPROVAL_AMOUNT, Objects.isNull(projReviewLeasePrice.getProjectApprovalAmount()) ? "/" : NumberUtil.decimalFormat(",##0.##", projReviewLeasePrice.getProjectApprovalAmount() / 10000));
                    }
                    renderMap.put(RenderParameterKeyHolder.GUARANTEE_NAME, clientName);
                    renderMap.put(RenderParameterKeyHolder.LESSEE_NAME, clientMap.stream().map(Client::getClientName).collect(Collectors.joining("、")));
                    renderMap.put(RenderParameterKeyHolder.LEASE_YEAR, Objects.isNull(leaseMonthCount) ? "/" : NumberUtil.decimalFormat("0.##", new BigDecimal(leaseMonthCount).divide(new BigDecimal(12), 2, RoundingMode.HALF_UP)));
                    renderMap.put(RenderParameterKeyHolder.GUARANTEE_CONTRACT_CODE, data.get("guarantorContractCode"));
                });
                this.renderSampleSignature(data);
                break;
            default: {
                throw new MithrasException("不支持的决议类型");
            }
        }
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream);
        template.render(renderMap);
        template.writeAndClose(outputStream);
        return resolutionTypeEnum.display + "（担保人）- " + clientName + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    /**
     * @description 生成签字样本合同
     * @since 2024/3/30 18:52
     */
    public void renderSampleSignature(JSONObject data) throws Exception {
        Long contractId = data.getLong("contractId");
        String clientName = data.getString("clientName");
        String resolutionType = data.getString("resolutionType");
        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put(RenderParameterKeyHolder.GUARANTEE_NAME, clientName);
        renderMap.put(RenderParameterKeyHolder.CLIENT_NAME, clientName);
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(resolutionType);
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的保证人类型");
        }
        InputStream inputStream;
        String fileName;
        switch (resolutionTypeEnum) {
            case SHAREHOLDERS_RESOLUTION:
            case SHAREHOLDER_DECISION:
                fileName = "股东会成员签字样本";
                inputStream = getBean(FileTemplateService.class).getTemplate("合同-决议文件", fileName + GlobalConstants.OFFICE_WORD_SUFFIX);
                break;
            case DIRECTORS_RESOLUTION:
            case EXECUTE_DIRECTOR_RESOLUTION:
                fileName = "董事会成员签字样本";
                inputStream = getBean(FileTemplateService.class).getTemplate("合同-决议文件", fileName + GlobalConstants.OFFICE_WORD_SUFFIX);
                break;
            default: {
                throw new MithrasException("不支持的决议类型");
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream);
        template.render(renderMap);
        template.writeAndClose(os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        // 保存文件
        try {
            materialsListService.add(is, fileName + "（担保人）-" + clientName + GlobalConstants.OFFICE_WORD_SUFFIX, contractId, ContractTypeEnum.RESOLUTION_FILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成决议文件（签字样本）发生未知异常[{}]", contractId, e);
            throw new MithrasException("生成决议文件（签字样本）发生未知异常");
        } finally {
            is.close();
            os.close();
        }
    }

    @Override
    protected Set<Long> signClientIds(JSONObject jsonObject) {
        Long id = jsonObject.getLong("clientId");
        if (Objects.isNull(id)) {
            return null;
        }
        return new HashSet<>(ListUtil.of(id));
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(JSONObject data) {
        FileTemplate fileTemplate = getFileTemplate(data);
        return Optional.of(fileTemplate).map(e ->Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private static FileTemplate getFileTemplate(JSONObject data) {
        String resolutionType = data.getString("resolutionType");
        ResolutionTypeEnum resolutionTypeEnum = ResolutionTypeEnum.of(resolutionType);
        if (Objects.isNull(resolutionTypeEnum)) {
            throw new MithrasException("未知的保证人类型");
        }
        String fileName;
        FileTemplate fileTemplate;
        switch (resolutionTypeEnum) {
            case SHAREHOLDERS_RESOLUTION:
            case SHAREHOLDER_DECISION:
                fileName = "股东会成员签字样本";
                fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-决议文件", fileName + GlobalConstants.OFFICE_WORD_SUFFIX);
                break;
            case DIRECTORS_RESOLUTION:
            case EXECUTE_DIRECTOR_RESOLUTION:
                fileName = "董事会成员签字样本";
                fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-决议文件", fileName + GlobalConstants.OFFICE_WORD_SUFFIX);
                break;
            default: {
                throw new MithrasException("不支持的决议类型");
            }
        }
        return fileTemplate;
    }

    @Override
    protected String customTemplateKey(JSONObject jsonObject) {
        FileTemplate fileTemplate = getFileTemplate(jsonObject);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }


    private static class RenderParameterKeyHolder {
        // 承租人
        public static final String LESSEE_NAME = "lesseeName";
        // 租期年份
        public static final String LEASE_YEAR = "leaseYear";
        // 合同金额
        public static final String PROJECT_APPROVAL_AMOUNT = "projectApprovalAmount";
        // 客户名称
        public static final String GUARANTEE_NAME = "guaranteeName";
        // 客户名称（签字样本用）
        public static final String CLIENT_NAME = "clientName";
        // 担保合同编号
        public static final String GUARANTEE_CONTRACT_CODE = "guaranteeContractCode";
    }
}
