package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.*;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.Tables;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2023/8/7
 * @description
 */
@Component
@Slf4j
public class ContractDetailListRender extends AbstractContractRender<JSONObject> {
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    protected MaterialsListService materialsListService;
    @Override
    public String render(OutputStream outputStream, JSONObject jsonObject) throws Exception {
        ProjReviewBaseInfo projReviewBaseInfo = JSONUtil.toBean(jsonObject, ProjReviewBaseInfo.class);
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(projReviewBaseInfo.getId()));
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            throw new MithrasException("没有找到项目对应的合同信息");
        }
        Set<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        // 承租人/债权人
        LambdaQueryWrapper<ContractTenantry> contractTenantryQuery = Wrappers.lambdaQuery();
        contractTenantryQuery.in(ContractTenantry::getContractId, contractIds);
        contractTenantryQuery.in(ContractTenantry::getLesseeType, Arrays.asList(LesseeTypeEnum.MAIN_LESSSEE.name(), LesseeTypeEnum.JOINT_LESSEE.name(), CreditorDebtorTypeEnum.CREDITOR.name()));
        List<ContractTenantry> contractTenantryList = contractTenantryService.list(contractTenantryQuery);
        Map<Long, List<ContractTenantry>> contractTenantryMap = contractTenantryList.stream().collect(Collectors.groupingBy(ContractTenantry::getContractId));
        // 保证合同
        List<ContractGuarantor> contractGuarantorList = contractGuarantorService.listByContractIds(contractIds);
        Map<Long, List<ContractGuarantor>> contractGuarantorMap = contractGuarantorList.stream().collect(Collectors.groupingBy(ContractGuarantor::getContractId));
        // 抵押合同
        List<ContractMortgage> contractMortgageList = contractMortgageService.listByContractIds(contractIds);
        Map<Long, List<ContractMortgage>> contractMortgageMap = contractMortgageList.stream().collect(Collectors.groupingBy(ContractMortgage::getContractId));
        // 质押合同
        List<ContractPledge> contractPledgeList = contractPledgeService.listByContractIds(contractIds);
        Map<Long, List<ContractPledge>> contractPledgeMap = contractPledgeList.stream().collect(Collectors.groupingBy(ContractPledge::getContractId));
        // 客户id
        Set<Long> clientIds = new LinkedHashSet<>();
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            for (ContractTenantry contractTenantry : contractTenantryList) {
                clientIds.add(contractTenantry.getLesseeId());
            }
        }
        if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
            for (ContractGuarantor contractGuarantor : contractGuarantorList) {
                if (StrUtil.isBlank(contractGuarantor.getGuarantorIds())) {
                    continue;
                }
                clientIds.addAll(JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class));
            }
        }
        if (CollectionUtil.isNotEmpty(contractMortgageList)) {
            for (ContractMortgage contractMortgage : contractMortgageList) {
                if (StrUtil.isBlank(contractMortgage.getMortgageIds())) {
                    continue;
                }
                clientIds.addAll(JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class));
            }
        }
        if (CollectionUtil.isNotEmpty(contractPledgeList)) {
            for (ContractPledge contractPledge : contractPledgeList) {
                if (StrUtil.isBlank(contractPledge.getPledgeIds())) {
                    continue;
                }
                clientIds.addAll(JSONUtil.toList(contractPledge.getPledgeIds(), Long.class));
            }
        }
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        // 渲染参数
        Map<String, Object> renderMap = new HashMap<>();
        // 项目名称
        renderMap.put("projectName", projReviewBaseInfo.getProjName());
        // 主合同编号
        String text = StrUtil.join("、", contractBaseInfoList.stream().map(ContractBaseInfo::getContractCode).collect(Collectors.toList()));
        renderMap.put("contractCodeList", text);
        // 合同清单表格
        List<RowRenderData> tableDataList = new LinkedList<>();
        // 表头
        tableDataList.add(Rows.of("序号", "文件名称", "相对方/出具方", "文件份数", "合同编号").center().create());
        // 序号
        int seq = 1;
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
            if (Objects.isNull(projectBizType)) {
                continue;
            }
            // 主合同
            TableContent mainContractTableContent = this.mainContractTableContent(contractBaseInfo, contractTenantryMap.get(contractBaseInfo.getId()), clientMap);
            mainContractTableContent.setSeq(String.valueOf(seq++));
            tableDataList.add(this.transfer(mainContractTableContent));
            // 咨询合同
            if (projectBizType == ProjectBizType.ZL || projectBizType == ProjectBizType.ZZ) {
                // 获取报价方案
                ContractLeasePrice contractLeasePrice = contractLeasePriceService.getByContractId(contractBaseInfo.getId());
                if (Objects.nonNull(contractLeasePrice) && Objects.nonNull(contractLeasePrice.getConsultingFee()) && contractLeasePrice.getConsultingFee() > 0) {
                    TableContent consultingTableContent = this.consultingTableContent(contractBaseInfo, contractTenantryMap.get(contractBaseInfo.getId()), clientMap);
                    consultingTableContent.setSeq(String.valueOf(seq++));
                    tableDataList.add(this.transfer(consultingTableContent));
                }
            }
            // 保证合同
            List<ContractGuarantor> contractGuarantors = contractGuarantorMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractGuarantors)) {
                for (ContractGuarantor contractGuarantor : contractGuarantors) {
                    TableContent guarantorTableContent = this.guarantorTableContent(contractGuarantor, clientMap);
                    guarantorTableContent.setSeq(String.valueOf(seq++));
                    tableDataList.add(this.transfer(guarantorTableContent));
                }
            }
            // 抵押合同
            List<ContractMortgage> contractMortgages = contractMortgageMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractMortgages)) {
                for (ContractMortgage contractMortgage : contractMortgages) {
                    TableContent mortgageTableContent = this.mortgageTableContent(contractMortgage, clientMap);
                    mortgageTableContent.setSeq(String.valueOf(seq++));
                    tableDataList.add(this.transfer(mortgageTableContent));
                }
            }
            // 质押合同
            List<ContractPledge> contractPledges = contractPledgeMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractPledges)) {
                for (ContractPledge contractPledge : contractPledges) {
                    TableContent pledgeTableContent = this.pledgeTableContent(contractPledge, clientMap);
                    pledgeTableContent.setSeq(String.valueOf(seq++));
                    tableDataList.add(this.transfer(pledgeTableContent));
                }
            }
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        TableRenderData tableRenderData = Tables.of(tableDataArray).center().width(16.5, new double[]{1.5, 4.0, 4.0, 3.0, 4.0}).create();
        renderMap.put("contractDetailListTable", tableRenderData);
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(getBean(FileTemplateService.class).getTemplate("合同-合同清单", "合同_其他_合同清单.docx")).render(renderMap);
        template.writeAndClose(outputStream);
        // 生成核验声明书
        if (Objects.equals(projReviewBaseInfo.getBizType(), ProjectBizType.ZL.name())) {
            this.createVerificationStatement(renderMap, jsonObject.getLong("contractId"));
        }
        return "合同清单" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private void createVerificationStatement(Map<String, Object> renderMap, Long contractId) throws Exception {
//        Long contractId = contractFileController.getContractIdThreadLocal().get();
        //  主承租人
        ContractTenantry tenantry = contractTenantryService.lambdaQuery()
                .eq(ContractTenantry::getContractId, contractId)
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .one();
        String lesseeName = Objects.isNull(tenantry) ? "/" : tenantry.getLesseeName();
        // 渲染文档
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(getBean(FileTemplateService.class).getTemplate("合同-合同清单", "核验声明书.docx"));
        //生成后不用填值
        template.render(new HashMap<>());
        template.writeAndClose(os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        // 保存文件
        try {
            materialsListService.add(is, "核验声明书-" + lesseeName + GlobalConstants.OFFICE_WORD_SUFFIX, contractId, ContractTypeEnum.OTHER_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成核验声明书发生未知异常[{}]", JSONUtil.toJsonStr(renderMap), e);
            throw new MithrasException("生成核验声明书发生未知异常");
        } finally {
            is.close();
            os.close();
        }
    }

    private TableContent mainContractTableContent(ContractBaseInfo contractBaseInfo, List<ContractTenantry> contractTenantryList, Map<Long, Client> clientMap) {
        TableContent tableContent = new TableContent();
        if (Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZR.name())) {
            tableContent.setFileName("债权转让合同");
        } else if (Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.BL.name())) {
            tableContent.setFileName("国内保理合同");
        } else {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.jyx_zu.name())) {
                tableContent.setFileName("融资租赁合同（经营性租赁）");
            } else if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                tableContent.setFileName("融资租赁合同（直租）");
            } else {
                tableContent.setFileName("融资租赁合同（回租）");
            }
        }
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            tableContent.setClientName(StrUtil.join("、", this.contractTenantryListText(contractTenantryList, clientMap)));
        }
        tableContent.setContractCode(contractBaseInfo.getContractCode());
        return tableContent;
    }

    private TableContent consultingTableContent(ContractBaseInfo contractBaseInfo, List<ContractTenantry> contractTenantryList, Map<Long, Client> clientMap) {
        TableContent tableContent = new TableContent();
        tableContent.setFileName("租赁结构安排及管理咨询合同");
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            tableContent.setClientName(StrUtil.join("、", this.contractTenantryListText(contractTenantryList, clientMap)));
        }
        tableContent.setContractCode(contractBaseInfo.getConsultingContractCode());
        return tableContent;
    }

    private TableContent guarantorTableContent(ContractGuarantor contractGuarantor, Map<Long, Client> clientMap) {
        TableContent tableContent = new TableContent();
        if (Objects.equals(contractGuarantor.getGuarantorType(), ClientType.NORMAL.name())) {
            tableContent.setFileName("保证合同（自然人）");
        } else {
            tableContent.setFileName("保证合同（法人）");
        }
        if (StrUtil.isNotBlank(contractGuarantor.getGuarantorIds())) {
            List<Long> clientIds = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
            tableContent.setClientName(this.clientNameListText(clientIds, clientMap));
        }
        tableContent.setContractCode(contractGuarantor.getGuarantorContractCode());
        return tableContent;
    }

    private TableContent mortgageTableContent(ContractMortgage contractMortgage, Map<Long, Client> clientMap) {
        TableContent tableContent = new TableContent();
        tableContent.setFileName("抵押合同");
        if (StrUtil.isNotBlank(contractMortgage.getMortgageIds())) {
            List<Long> clientIds = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
            tableContent.setClientName(this.clientNameListText(clientIds, clientMap));
        }
        tableContent.setContractCode(contractMortgage.getMortgageContractCode());
        return tableContent;
    }

    private TableContent pledgeTableContent(ContractPledge contractPledge, Map<Long, Client> clientMap) {
        TableContent tableContent = new TableContent();
        tableContent.setFileName("质押合同");
        if (StrUtil.isNotBlank(contractPledge.getPledgeIds())) {
            List<Long> clientIds = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
            tableContent.setClientName(this.clientNameListText(clientIds, clientMap));
        }
        tableContent.setContractCode(contractPledge.getPledgeContractCode());
        return tableContent;
    }

    private String contractTenantryListText(List<ContractTenantry> contractTenantryList, Map<Long, Client> clientMap) {
        List<String> list = new ArrayList<>(contractTenantryList.size());
        for (ContractTenantry contractTenantry : contractTenantryList) {
            Client client = clientMap.get(contractTenantry.getLesseeId());
            if (Objects.isNull(client)) {
                continue;
            }
            list.add(client.getClientName());
        }
        return StrUtil.join("、", list);
    }

    private String clientNameListText(List<Long> clientIds, Map<Long, Client> clientMap) {
        List<String> clientNames = new ArrayList<>(clientIds.size());
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            if (Objects.isNull(client)) {
                continue;
            }
            clientNames.add(client.getClientName());
        }
        return StrUtil.join("、", clientNames);
    }

    private RowRenderData transfer(TableContent tableContent) {
        return Rows.of(tableContent.seq, tableContent.fileName, tableContent.clientName, tableContent.fileNum, tableContent.contractCode).center().create();
    }

    @Override
    protected Set<Long> signClientIds(JSONObject projReviewBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(JSONObject projReviewBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-合同清单", "合同_其他_合同清单.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(JSONObject projReviewBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-合同清单", "合同_其他_合同清单.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    @Data
    private static class TableContent {
        private String seq;
        private String fileName;
        private String clientName;
        private String fileNum = "3（2，1）";
        private String contractCode;
    }
}
