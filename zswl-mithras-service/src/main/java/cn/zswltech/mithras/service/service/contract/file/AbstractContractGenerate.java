package cn.zswltech.mithras.service.service.contract.file;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.enums.BaseProfileTypeEnum;
import cn.zswltech.mithras.service.enums.BizClientType;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.excel.model.ContractEntityPledgeItemExcelModel;
import cn.zswltech.mithras.service.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.service.gendoc.render.*;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.zswltech.mithras.service.enums.datacompare.CompareFactoryEnum.contractTenantry;
import cn.zswltech.mithras.contract.core.service.ContractPledgeItemService;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
@Slf4j
public abstract class AbstractContractGenerate implements ContractGenerate {
    @Resource
    private ContractPledgeItemService contractPledgeItemService;
    @Resource
    protected ContractBaseInfoService contractBaseInfoService;
    @Resource
    protected ContractMortgageService contractMortgageService;
    @Resource
    protected ContractGuarantorService contractGuarantorService;
    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    protected ContractMainLeaseItemRender contractMainLeaseItemRender;
    @Resource
    protected ContractMainRentEstimateRender contractMainRentEstimateRender;
    @Resource
    protected ContractMainRentActualRender contractMainRentActualRender;
    @Resource
    protected ContractMainAssetsOwnerChangeRender contractMainAssetsOwnerChangeRender;
    @Resource
    protected ContractMainLeaseItemQualifiedConfirmRender contractMainLeaseItemQualifiedConfirmRender;
    @Resource
    protected ContractMainCollectionConfirmRender contractMainCollectionConfirmRender;
    @Resource
    protected ContractConsultingRender contractConsultingRender;
    @Resource
    protected ContractConsultingAcceptConfirmRender contractConsultingAcceptConfirmRender;
    @Resource
    protected ContractGuarantorRender contractGuarantorRender;
    @Resource
    protected ContractMortgageRender contractMortgageRender;
    @Resource
    protected ContractMortgageItemRender contractMortgageItemRender;
    @Resource
    protected BusinessDataRepository businessDataRepository;
    @Resource
    protected ContractPledgeService contractPledgeService;
    @Resource
    protected ContractPledgeReceivableRender contractPledgeReceivableRender;
    @Resource
    protected ContractPledgeStockRender contractPledgeStockRender;
    @Resource
    protected ContractPledgePaymentLetterRender contractPledgePaymentLetterRender;
    @Resource
    protected ContractPledgeConfirmationRender contractPledgeConfirmationRender;
    @Resource
    protected ContractPledgeProtocolRender contractPledgeProtocolRender;
    @Resource
    protected ContractTenantryService contractTenantryService;
    @Resource
    protected ContractTenantryRender contractTenantryRender;
    @Resource
    protected ContractResolutionRender contractResolutionRender;
    @Resource
    protected ContractResolutionGuarantorRender contractResolutionGuarantorRender;
    @Resource
    protected ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    protected ClientRender clientRender;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    protected ContractSignInfoService contractSignInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private ContractDetailListRender contractDetailListRender;
    @Resource
    private PurposeOfFundsRender purposeOfFundsRender;

    /**
     * 子类通过该方法在项目启动时将生成对应合同方法初始化进 SharedResources.sharedMap中 便于选择性创建合同
     */
    public abstract void init();

    protected void generateConsultingContract(ContractBaseInfo contractBaseInfo) throws Exception {
        Assert.notBlank(contractBaseInfo.getConsultingContractCode(), () -> MithrasException.newException("咨询合同编号不能为空"));
        ContractPriceDetailRSP priceDetailRSP = contractPriceService.detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
        if (priceDetailRSP != null && priceDetailRSP.getConsultingFee() > 0) {
            // 咨询合同
            this.generateConsultingMainContract(contractBaseInfo);
            // 咨询合同附属 接受咨询服务确认函
            this.generateConsultingAcceptConfirm(contractBaseInfo);
        }
    }

    /**
     * 咨询合同主合同
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    protected void generateConsultingMainContract(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractConsultingRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.CONSULTING_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractConsultingRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractConsultingRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成咨询合同主合同发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成咨询合同主合同发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 咨询合同附件-接受咨询服务确认函
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    protected void generateConsultingAcceptConfirm(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractConsultingAcceptConfirmRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.CONSULTING_CONTRACT.name(), ContractSubTypeEnum.ACCEPT_CONSULTING_CONFIRM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractConsultingAcceptConfirmRender, contractBaseInfo ,contractBaseInfo.getId(), fileId, contractConsultingAcceptConfirmRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成咨询合同附件-接受咨询服务确认函发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成咨询合同附件-接受咨询服务确认函发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 校验异步响应
     *
     * @param cfList 异步执行列表
     */
    @SneakyThrows
    protected void checkMessage(List<CompletableFuture<String>> cfList) {
        for (CompletableFuture<String> completableFuture : cfList) {
            String message = completableFuture.get();
            if (Objects.nonNull(message)) {
                throw new MithrasException(message);
            }
        }
    }

    @SneakyThrows
    protected void checkMessage(CompletableFuture<String> completableFuture) {
        String message = completableFuture.get();
        if (Objects.nonNull(message)) {
            throw new MithrasException(message);
        }
    }

    /**
     * 保证合同
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    protected void generateGuarantorContract(ContractBaseInfo contractBaseInfo) throws Exception {
        // 查询保证人记录
        List<ContractGuarantor> contractGuarantorList = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery().eq(ContractGuarantor::getContractId, contractBaseInfo.getId()));
        if (CollectionUtils.isEmpty(contractGuarantorList)) {
            return;
        }
        // 校验合同编号
        for (ContractGuarantor contractGuarantor : contractGuarantorList) {
            if (StrUtil.isBlank(contractGuarantor.getGuarantorContractCode())) {
                throw new MithrasException("保证合同编号不能为空");
            }
        }
        List<String> codeList = contractGuarantorList.stream().map(ContractGuarantor::getGuarantorContractCode).collect(Collectors.toList());
        this.checkCode(codeList, "保证");
        for (int i = 0; i < contractGuarantorList.size(); i++) {
            ContractGuarantor contractGuarantor = contractGuarantorList.get(i);
            if (StringUtils.isEmpty(contractGuarantor.getGuarantorIds())) {
                continue;
            }
            ByteArrayInputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                os = new ByteArrayOutputStream();
                String fileName = contractGuarantorRender.render(os, contractGuarantor);
                // 拼编号
                fileName = "3-" + (i + 1) + "." + fileName;
                is = new ByteArrayInputStream(os.toByteArray());
                Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.GUARANTEE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                contractSignInfoService.saveContractSignInfo(contractGuarantorRender, contractGuarantor, contractBaseInfo.getId(), fileId, contractGuarantorRender.signatories(contractGuarantor));
            } catch (MithrasException e) {
                throw e;
            } catch (Exception e) {
                log.error("生成保证合同发生未知异常[{}]", JSONUtil.toJsonStr(contractGuarantor), e);
                throw new MithrasException("生成保证合同发生未知异常");
            } finally {
                if (!Objects.isNull(is)) {
                    is.close();
                }
                if (!Objects.isNull(os)) {
                    os.close();
                }
            }
        }
    }

    /**
     * 决议文件
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    protected void generateResolutionContract(ContractBaseInfo contractBaseInfo) throws Exception {
        // 查询承租人列表
        List<ContractTenantry> contractTenantryList = contractTenantryService.lambdaQuery()
                .eq(ContractTenantry::getContractId, contractBaseInfo.getId())
                .list();
        if(CollectionUtil.isEmpty(contractTenantryList)){
            return;
        }
        List<ContractTenantry> collect = contractTenantryList.stream().filter(f -> !StringUtils.isEmpty(f.getResolutionType())).collect(Collectors.toList());
        if(contractTenantryList.size() > collect.size()){
            throw new MithrasException("承租人-决议类型不得为空");
        }
        List<ContractTenantry> contractTenantryList2 = collect.stream().filter(f -> !ResolutionTypeEnum.OTHER.name().equals(f.getResolutionType())).collect(Collectors.toList());
        for (ContractTenantry tenantry : contractTenantryList2) {
            ByteArrayInputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                os = new ByteArrayOutputStream();
                String fileName = contractResolutionRender.render(os, tenantry);
                is = new ByteArrayInputStream(os.toByteArray());
                Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.RESOLUTION_FILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                contractSignInfoService.saveContractSignInfo(contractResolutionRender, tenantry, contractBaseInfo.getId(), fileId, contractResolutionRender.signatories(tenantry));
            } catch (MithrasException e) {
                throw e;
            } catch (Exception e) {
                log.error("生成决议文件-承租人发生未知异常[{}]", JSONUtil.toJsonStr(tenantry), e);
                throw new MithrasException("生成决议文件-承租人发生未知异常");
            } finally {
                if (!Objects.isNull(is)) {
                    is.close();
                }
                if (!Objects.isNull(os)) {
                    os.close();
                }
            }
        }
        //  查询担保措施列表
        List<ContractGuarantor> contractGuarantorList = contractGuarantorService.lambdaQuery()
                .eq(ContractGuarantor::getContractId, contractBaseInfo.getId())
                .list();
        if(CollectionUtil.isEmpty(contractGuarantorList)){
            return;
        }
        List<ContractGuarantor> collect1 = contractGuarantorList.stream()
                .filter(f -> ClientType.CORPORATION.name().equals(f.getGuarantorType()))
                .filter(f -> !StringUtils.isEmpty(f.getResolutionType())).collect(Collectors.toList());
        if(contractGuarantorList.stream().filter(c -> ClientType.CORPORATION.name().equals(c.getGuarantorType())).count() > collect1.size()){
            throw new MithrasException("担保人-决议类型不得为空");
        }
        //  主承租人和联合承租人
        List<ContractTenantry> contractTenantries = contractTenantryService.lambdaQuery()
                .eq(ContractTenantry::getContractId, contractBaseInfo.getId())
                .isNotNull(ContractTenantry::getResolutionType)
                .in(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name(), LesseeTypeEnum.JOINT_LESSEE.name())
                .list();
        List<ContractGuarantor> collect2 = collect1.stream().filter(f -> !ResolutionTypeEnum.OTHER.name().equals(f.getResolutionType())).collect(Collectors.toList());
        for (ContractGuarantor contractGuarantor : collect2) {
            if (!ObjectUtils.isEmpty(contractGuarantor.getGuarantorIds()) && !ObjectUtils.isEmpty(contractTenantries)) {
                Set<Long> cIdSet = new HashSet<>(JSONArray.parseArray(contractGuarantor.getGuarantorIds(), Long.class));
                Map<Long, ClientInfo> clientMap = id2NameService.clientId2CLient(cIdSet);
                List<ClientInfo> clientInfos = new ArrayList<>(clientMap.values());
                for (ClientInfo clientInfo : clientInfos) {
                    JSONObject data = new JSONObject();
                    data.put("guarantorContractCode", contractGuarantor.getGuarantorContractCode());
                    data.put("clientId", clientInfo.getClientId());
                    data.put("clientName", clientInfo.getClientName());
                    data.put("contractTenantry", contractTenantries);
                    data.put("resolutionType", contractGuarantor.getResolutionType());
                    ByteArrayInputStream is = null;
                    ByteArrayOutputStream os = null;
                    try {
                        os = new ByteArrayOutputStream();
                        String fileName = contractResolutionGuarantorRender.render(os, data);
                        is = new ByteArrayInputStream(os.toByteArray());
                        Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.RESOLUTION_FILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                        contractSignInfoService.saveContractSignInfo(contractResolutionGuarantorRender, data, contractBaseInfo.getId(), fileId, contractResolutionGuarantorRender.signatories(data));
                    } catch (MithrasException e) {
                        throw e;
                    } catch (Exception e) {
                        log.error("生成决议文件-担保人发生未知异常[{}]", JSONUtil.toJsonStr(contractTenantry), e);
                        throw new MithrasException("生成决议文件-担保人发生未知异常");
                    } finally {
                        if (!Objects.isNull(is)) {
                            is.close();
                        }
                        if (!Objects.isNull(os)) {
                            os.close();
                        }
                    }
                }
            }

        }
    }

    /**
     * 抵押合同套打
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    protected void generateMortgageContract(ContractBaseInfo contractBaseInfo) throws Exception {
        // 查询抵押信息
        List<ContractMortgage> contractMortgageList = contractMortgageService.list(Wrappers.<ContractMortgage>lambdaQuery().eq(ContractMortgage::getContractId, contractBaseInfo.getId()));
        if (CollectionUtils.isEmpty(contractMortgageList)) {
            return;
        }
        // 校验合同编号
        for (ContractMortgage contractMortgage : contractMortgageList) {
            if (StrUtil.isBlank(contractMortgage.getMortgageContractCode())) {
                throw new MithrasException("抵押合同编号不能为空");
            }
        }
        List<String> codeList = contractMortgageList.stream().map(ContractMortgage::getMortgageContractCode).collect(Collectors.toList());
        this.checkCode(codeList, "抵押");
        for (int i = 0; i < contractMortgageList.size(); i++) {
            generateMortgageContract(contractMortgageList.get(i), i + 1);
            generateMortgageItem(contractMortgageList.get(i), i + 1);
        }
    }

    /**
     * 抵押合同
     */
    protected void generateMortgageContract(ContractMortgage contractMortgage, int order) throws Exception {
        if (StringUtils.isEmpty(contractMortgage.getMortgageIds())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMortgageRender.render(os, contractMortgage);
            // 拼编号
            fileName = "4-" + (order) + "." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractMortgage.getContractId(), ContractTypeEnum.MORTGAGE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMortgageRender, contractMortgage, contractMortgage.getContractId(), fileId, contractMortgageRender.signatories(contractMortgage));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成抵押合同发生未知异常[{}]", JSONUtil.toJsonStr(contractMortgage), e);
            throw new MithrasException("生成抵押合同发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 抵押物清单
     */
    protected void generateMortgageItem(ContractMortgage contractMortgage, int order) throws Exception {
        if (StringUtils.isEmpty(contractMortgage.getMortgageIds())) {
            return;
        }
        // 抵押物清单
        List<ContractMortgageItem> contractMortgageItemList = businessDataRepository.listContractMortgageItem(contractMortgage.getId());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(contractMortgageItemList)) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMortgageItemRender.render(os, contractMortgage);
            // 拼编号
            fileName = "4-" + (order) + "-1." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractMortgage.getContractId(), ContractTypeEnum.MORTGAGE_CONTRACT.name(), ContractSubTypeEnum.MORTGAGE_ITEM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMortgageItemRender, contractMortgage, contractMortgage.getContractId(), fileId, contractMortgageItemRender.signatories(contractMortgage));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成抵押物清单发生未知异常[{}]", JSONUtil.toJsonStr(contractMortgage), e);
            throw new MithrasException("生成抵押物清单发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 质押合同套打
     *
     * @param contractBaseInfo
     * @throws Exception
     */
    protected void generatePledgeContract(ContractBaseInfo contractBaseInfo) throws Exception {
        List<ContractPledge> contractPledges = contractPledgeService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractPledges)) {
            return;
        }
        for (ContractPledge contractPledge : contractPledges) {
            if (StrUtil.isBlank(contractPledge.getPledgeContractCode())) {
                throw new MithrasException("质押合同编号不能为空");
            }
        }
        List<String> codeList = contractPledges.stream().map(ContractPledge::getPledgeContractCode).collect(Collectors.toList());
        this.checkCode(codeList, "质押");
        int j = 0;
        int k = 0;
        for (ContractPledge contractPledge : contractPledges) {
            // 质押措施一般数量不多，循环查询子表可以接受吧
            List<ContractPledgeItem> contractPledgeItemList = contractPledgeItemService.listByPledgeId(contractPledge.getId());
            if (CollectionUtil.isEmpty(contractPledgeItemList)) {
                continue;
            }
            String pledgeItemType = contractPledgeItemList.get(0).getCategory();
            // 股权
            if (Objects.equals(pledgeItemType, ContractEntityPledgeItemExcelModel.CategoryEnum.EQUITY.getDisplay())) {
                j++;
                generatePledgeStockItem(contractPledge, j);
                generatePledgePaymentLetterItem(contractPledge, j);
                generatePledgeConfirmationItem(contractPledge, j);
            }
            // 应收账款
            if (Objects.equals(pledgeItemType, ContractEntityPledgeItemExcelModel.CategoryEnum.ACCOUNTS.getDisplay())) {
                k++;
                generatePledgeReceivableItem(contractPledge, k);
                generatePledgeProtocolItem(contractPledge, k);
            }
        }
    }

    /**
     * 质押合同-股权
     *
     * @param contractPledge
     * @param order
     */
    protected void generatePledgeStockItem(ContractPledge contractPledge, int order) throws Exception {
        if (StringUtils.isEmpty(contractPledge.getPledgeIds())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractPledgeStockRender.render(os, contractPledge);
            // 拼编号
            fileName = "5-" + (order) + "." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractPledge.getContractId(), ContractTypeEnum.PLEDGE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractPledgeStockRender, contractPledge, contractPledge.getContractId(), fileId, contractPledgeStockRender.signatories(contractPledge));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成质押合同-股权发生未知异常[{}]", JSONUtil.toJsonStr(contractPledge), e);
            throw new MithrasException("生成质押合同-股权发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 质押合同-指示付款函
     *
     * @param contractPledge
     * @param order
     */
    protected void generatePledgePaymentLetterItem(ContractPledge contractPledge, int order) throws Exception {
        if (StringUtils.isEmpty(contractPledge.getPledgeIds())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractPledgePaymentLetterRender.render(os, contractPledge);
            // 拼编号
            fileName = "5-" + (order) + "-1." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractPledge.getContractId(), ContractTypeEnum.PLEDGE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractPledgePaymentLetterRender, contractPledge, contractPledge.getContractId(), fileId, contractPledgePaymentLetterRender.signatories(contractPledge));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成质押合同-应收账款发生未知异常[{}]", JSONUtil.toJsonStr(contractPledge), e);
            throw new MithrasException("生成质押合同-应收账款权发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 质押合同-确认函
     *
     * @param contractPledge
     * @param order
     */
    protected void generatePledgeConfirmationItem(ContractPledge contractPledge, int order) throws Exception {
        if (StringUtils.isEmpty(contractPledge.getPledgeIds())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractPledgeConfirmationRender.render(os, contractPledge);
            // 拼编号
            fileName = "5-" + (order) + "-2." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractPledge.getContractId(), ContractTypeEnum.PLEDGE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractPledgeConfirmationRender, contractPledge, contractPledge.getContractId(), fileId, contractPledgeConfirmationRender.signatories(contractPledge));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成质押合同-应收账款发生未知异常[{}]", JSONUtil.toJsonStr(contractPledge), e);
            throw new MithrasException("生成质押合同-应收账款权发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 质押合同-应收账款
     *
     * @param contractPledge
     * @param order
     */
    protected void generatePledgeReceivableItem(ContractPledge contractPledge, int order) throws Exception {
        if (StringUtils.isEmpty(contractPledge.getPledgeIds())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractPledgeReceivableRender.render(os, contractPledge);
            // 拼编号
            fileName = "6-" + (order) + "." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractPledge.getContractId(), ContractTypeEnum.PLEDGE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractPledgeReceivableRender, contractPledge, contractPledge.getContractId(), fileId, contractPledgeReceivableRender.signatories(contractPledge));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成质押合同-应收账款发生未知异常[{}]", JSONUtil.toJsonStr(contractPledge), e);
            throw new MithrasException("生成质押合同-应收账款权发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 质押合同-应收账款质押登记协议
     *
     * @param contractPledge
     * @param order
     */
    protected void generatePledgeProtocolItem(ContractPledge contractPledge, int order) throws Exception {
        if (StringUtils.isEmpty(contractPledge.getPledgeIds())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractPledgeProtocolRender.render(os, contractPledge);
            // 拼编号
            fileName = "6-" + (order) + "-1." + fileName;
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractPledge.getContractId(), ContractTypeEnum.PLEDGE_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractPledgeProtocolRender, contractPledge, contractPledge.getContractId(), fileId, contractPledgeProtocolRender.signatories(contractPledge));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成质押合同-应收账款发生未知异常[{}]", JSONUtil.toJsonStr(contractPledge), e);
            throw new MithrasException("生成质押合同-应收账款权发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 租赁物文件-套打
     *
     * @param contractBaseInfo
     */
    protected void generateLeaseItemContract(ContractBaseInfo contractBaseInfo) throws Exception {
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
        String classify = Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null);
        // 非产业类项目无需生成合同
        if (RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(classify) ||
                RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(classify) ||
                RiskControlIndustryClassify.TRAVEL.name().equals(classify)) {
            return;
        }

        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.eq(ContractTenantry::getContractId, contractBaseInfo.getId());
        List<ContractTenantry> contractTenantryList = contractTenantryService.list(query);
        if (CollectionUtils.isEmpty(contractTenantryList)) {
            return;
        }

        for (ContractTenantry contractTenantry : contractTenantryList) {
            if (StringUtils.isEmpty(contractTenantry.getLesseeId())) {
                throw new MithrasException("承租人id不能为空");
            }
            if (StringUtils.isEmpty(contractTenantry.getLeaseItemFileType())) {
                throw new MithrasException("租赁物文件类型不得为空");
            }
        }
        this.checkCode(Collections.singletonList(contractBaseInfo.getContractCode()), "租赁物文件");
        // 租赁物承诺函 只显示一份 - 落款展示所有承租人名称
        List<String> leaseItemNameList = contractTenantryList.stream().map(m -> m.getLesseeName() + "（盖章）").collect(Collectors.toList());
        List<ContractTenantry> leaseItemNameExist = contractTenantryList.stream().filter(f -> ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.name().equals(f.getLeaseItemFileType()) ||
                ContractLeaseItemFileTypeEnum.BOTH.name().equals(f.getLeaseItemFileType())).collect(Collectors.toList());
        // 发票补足承诺函 只显示一份
        List<ContractTenantry> invoiceSupplementList = contractTenantryList.stream().filter(f -> ContractLeaseItemFileTypeEnum.INVOICE_SUPPLEMENT_PROMISE_LETTER.name().equals(f.getLeaseItemFileType()) ||
                ContractLeaseItemFileTypeEnum.BOTH.name().equals(f.getLeaseItemFileType())).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(leaseItemNameExist)) {
            ContractTenantry tenantry = new ContractTenantry();
            tenantry.setLeaseItemFileType(ContractLeaseItemFileTypeEnum.LEASE_ITEM_PROMISE_LETTER.name());
            tenantry.setContractId(contractBaseInfo.getId());
            tenantry.setLesseeName(String.join("\n\n\n\n\n", leaseItemNameList));
            generateLeaseItemPromiseLetter(tenantry);
        }

        if(CollectionUtil.isNotEmpty(invoiceSupplementList)){
//            List<String> supplementNameList = invoiceSupplementList.stream().map(m -> m.getLesseeName() + "（盖章）").collect(Collectors.toList());
            ContractTenantry tenantry = new ContractTenantry();
            tenantry.setLeaseItemFileType(ContractLeaseItemFileTypeEnum.INVOICE_SUPPLEMENT_PROMISE_LETTER.name());
            tenantry.setContractId(contractBaseInfo.getId());
            tenantry.setLesseeName(String.join("\n\n\n\n\n", leaseItemNameList));
            generateInvoicePromiseLetter(tenantry);
        }
    }


    /**
     * 租赁物文件-租赁物承诺函
     *
     * @param contractTenantry
     * @throws Exception
     */
    private void generateLeaseItemPromiseLetter(ContractTenantry contractTenantry) throws Exception {
        if (StringUtils.isEmpty(contractTenantry.getLesseeName())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractTenantryRender.render(os, contractTenantry);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractTenantry.getContractId(), ContractTypeEnum.LEASE_ITEM_FILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractTenantryRender, contractTenantry, contractTenantry.getContractId(), fileId, contractTenantryRender.signatories(contractTenantry));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成租赁物文件-租赁物承诺函发生未知异常[{}]", JSONUtil.toJsonStr(contractTenantry), e);
            throw new MithrasException("生成租赁物文件-租赁物承诺函发生未知异常[{}]");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 租赁物文件-发票补足承诺函
     *
     * @param contractTenantry
     * @throws Exception
     */
    private void generateInvoicePromiseLetter(ContractTenantry contractTenantry) throws Exception {
        if (StringUtils.isEmpty(contractTenantry.getLesseeName())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractTenantryRender.render(os, contractTenantry);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractTenantry.getContractId(), ContractTypeEnum.LEASE_ITEM_FILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractTenantryRender, contractTenantry, contractTenantry.getContractId(), fileId, contractTenantryRender.signatories(contractTenantry));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成租赁物文件-发票补足承诺函发生未知异常[{}]", JSONUtil.toJsonStr(contractTenantry), e);
            throw new MithrasException("生成租赁物文件-发票补足承诺函发生未知异常[{}]");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 基础资料套打
     *
     * @param contractBaseInfo
     */
    protected void generateBaseProfileContract(ContractBaseInfo contractBaseInfo) throws Exception {

        List<Long> guarantorClientIds = contractGuarantorService.list(Wrappers.<ContractGuarantor>lambdaQuery().eq(ContractGuarantor::getContractId, contractBaseInfo.getId()))
                .stream().map(m -> JSONUtil.toList(m.getGuarantorIds(), Long.class)).flatMap(Collection::stream).collect(Collectors.toList());
        List<Long> pledgeClientIds = contractPledgeService.listByContractId(contractBaseInfo.getId())
                .stream().map(m -> JSONUtil.toList(m.getPledgeIds(), Long.class)).flatMap(Collection::stream).collect(Collectors.toList());
        List<Long> mortgageClientIds = contractMortgageService.list(Wrappers.<ContractMortgage>lambdaQuery().eq(ContractMortgage::getContractId, contractBaseInfo.getId()))
                .stream().map(m -> JSONUtil.toList(m.getMortgageIds(), Long.class)).flatMap(Collection::stream).collect(Collectors.toList());
        List<Long> lesseeClientIds = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery().eq(ContractTenantry::getContractId, contractBaseInfo.getId()))
                .stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());

        List<Client> lesseeClientList = Optional.ofNullable(businessDataRepository.getClientList(lesseeClientIds)).orElse(new ArrayList<>());
        List<Client> pledgeClientList = Optional.ofNullable(businessDataRepository.getClientList(pledgeClientIds)).orElse(new ArrayList<>());
        List<Client> mortgageClientList = Optional.ofNullable(businessDataRepository.getClientList(mortgageClientIds)).orElse(new ArrayList<>());
        List<Client> guaranteeClientList = Optional.ofNullable(businessDataRepository.getClientList(guarantorClientIds)).orElse(new ArrayList<>());
        // 抵押与质押统一处理
        pledgeClientList.stream().filter(f -> !mortgageClientList.contains(f)).map(mortgageClientList::add).collect(Collectors.toList());
        // 获取所有法人客户 生成法定代表人证明书
        Set<Client> corporationSet = Stream.of(lesseeClientList, mortgageClientList, guaranteeClientList).flatMap(Collection::stream).filter(f -> ClientType.CORPORATION.name().equals(f.getClientType())).collect(Collectors.toSet());
        // 若一个客户是抵质押人 又是担保人或承租人 则文件无需重复生成
        guaranteeClientList.removeAll(lesseeClientList);
        mortgageClientList.removeAll(lesseeClientList);
        mortgageClientList.removeAll(guaranteeClientList);
        Map<Long, Client> normalClientMap = new HashMap<>();
        // 资料真实性承诺书
        for (Client client : lesseeClientList) {
            client.setContractId(contractBaseInfo.getId());
            client.setBizClientType("other");
            generateBaseProfileCommitment(client);
        }
        for (Client client : mortgageClientList) {
            client.setContractId(contractBaseInfo.getId());
            client.setBizClientType(BizClientType.mortgagor.name());
            generateBaseProfileCommitment(client);
            if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
                normalClientMap.put(client.getId(), client);
            }
        }
        for (Client client : guaranteeClientList) {
            client.setContractId(contractBaseInfo.getId());
            client.setBizClientType(BizClientType.guarantee.name());
            generateBaseProfileCommitment(client);
            if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
                normalClientMap.put(client.getId(), client);
            }
        }
        // 生成廉洁自律告知书
        for (Client client : lesseeClientList) {
            client.setContractId(contractBaseInfo.getId());
            generateBaseProfileHonest(client);
        }
        // 法定代表人证明书
        for (Client client : corporationSet) {
            client.setContractId(contractBaseInfo.getId());
            generateBaseProfileCorporation(client);
        }
        // 个人征信授权书
        if (CollectionUtil.isNotEmpty(normalClientMap)) {
            for (Client client : normalClientMap.values()) {
                ByteArrayInputStream is = null;
                ByteArrayOutputStream os = null;
                try {
                    os = new ByteArrayOutputStream();
                    String fileName = SpringUtil.getBean(ContractNormalCreditAuthRender.class).render(os, client);
                    is = new ByteArrayInputStream(os.toByteArray());
                    materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.BASE_PROFILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
                } catch (MithrasException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("生成基础资料-个人征信授权书发生未知异常[{}]", JSONUtil.toJsonStr(client), e);
                    throw new MithrasException("生成基础资料-个人征信授权书-" + client.getClientName() + "发生未知异常");
                } finally {
                    if (!Objects.isNull(is)) {
                        is.close();
                    }
                    if (!Objects.isNull(os)) {
                        os.close();
                    }
                }
            }
        }
    }


    public void generateOther(ContractBaseInfo contractBaseInfo){
        // 生成合同清单
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generateContractDetailList(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成合同清单发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成合同清单发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成合同清单发生未知异常";
            }
        }));

        // 如果评审风控行业分类=水上运输业，还需要生成承诺函_租赁登记
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                if (SpringUtil.getBean(ProjReviewService.class).projReviewIsWaterTransport(contractBaseInfo.getProjReviewId())) {
                    this.generateShipPromise(contractBaseInfo);
                }
                return null;
            } catch (MithrasException e) {
                return "生成其他-承诺函_租赁登记发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成其他-承诺函_租赁登记发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成其他-承诺函_租赁登记发生未知异常";
            }
        }));

        // 生成款项用途确认函
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                // 用最新的值填充客户风控行业分类
                CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
                String riskControlIndustryClassify = Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null);

                if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name()) &&
                        (Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.PUBLIC_UTILITIES.name()) ||
                                Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.CIVIL_CONSUMPTION.name()) ||
                                Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.TRAVEL.name()))) {
                    this.generatePurposeOfFundsList(contractBaseInfo);
                }
                return null;
            } catch (MithrasException e) {
                return "生成款项用途确认函发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成款项用途确认函发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成款项用途确认函发生未知异常";
            }
        }));

    }

    private void generateShipPromise(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = SpringUtil.getBean(ContractShipPromiseRender.class).render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.OTHER_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            SpringUtil.getBean(ContractSignInfoService.class).saveContractSignInfo(SpringUtil.getBean(ContractShipPromiseRender.class), contractBaseInfo, contractBaseInfo.getId(), fileId, SpringUtil.getBean(ContractShipPromiseRender.class).signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成其他-承诺函_租赁登记发生未知异常[{}]", JSONUtil.toJsonStr(contractBaseInfo), e);
            throw new MithrasException("生成其他-承诺函_租赁登记发生未知异常[{}]");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    private void generateContractDetailList(ContractBaseInfo contractBaseInfo) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(contractBaseInfo.getProjReviewId());
        if (Objects.isNull(projReviewBaseInfo)) {
            throw new MithrasException("没有找到对应的项目评审信息，无法生成合同清单");
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(projReviewBaseInfo);
            jsonObject.set("contractId", contractBaseInfo.getId());
            String fileName = contractDetailListRender.render(os, jsonObject);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.OTHER_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成合同清单发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成合同清单发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭输入流异常", e);
                }
            }
            if (!Objects.isNull(os)) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("关闭输出流异常", e);
                }
            }
        }
    }

    private void generatePurposeOfFundsList(ContractBaseInfo contractBaseInfo) {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = purposeOfFundsRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.OTHER_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成款项用途确认函发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成款项用途确认函发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭输入流异常", e);
                }
            }
            if (!Objects.isNull(os)) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("关闭输出流异常", e);
                }
            }
        }
    }

    /**
     * 基础资料-资料真实性承诺书
     *
     * @param client
     * @throws Exception
     */
    private void generateBaseProfileCommitment(Client client) throws Exception {
        if (StringUtils.isEmpty(client.getClientName())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            client.setContractType(BaseProfileTypeEnum.commitment.name());
            String fileName = clientRender.render(os, client);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, client.getContractId(), ContractTypeEnum.BASE_PROFILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成基础资料-资料真实性承诺书发生未知异常[{}]", JSONUtil.toJsonStr(client), e);
            throw new MithrasException("生成基础资料-资料真实性承诺书发生未知异常[{}]");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 基础资料-法定代表人证明书
     *
     * @param client
     * @throws Exception
     */
    private void generateBaseProfileCorporation(Client client) throws Exception {
        if (StringUtils.isEmpty(client.getClientName())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            client.setContractType(BaseProfileTypeEnum.corporation.name());
            String fileName = clientRender.render(os, client);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, client.getContractId(), ContractTypeEnum.BASE_PROFILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成基础资料-法定代表人证明书发生未知异常[{}]", JSONUtil.toJsonStr(client), e);
            throw new MithrasException("生成基础资料-法定代表人证明书发生未知异常[{}]");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    /**
     * 基础资料-廉洁自律告知书
     *
     * @param client
     * @throws Exception
     */
    private void generateBaseProfileHonest(Client client) throws Exception {
        if (StringUtils.isEmpty(client.getClientName())) {
            return;
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            client.setContractType(BaseProfileTypeEnum.honest.name());
            String fileName = clientRender.render(os, client);
            is = new ByteArrayInputStream(os.toByteArray());
            materialsListService.add(is, fileName, client.getContractId(), ContractTypeEnum.BASE_PROFILE.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成基础资料-廉洁自律告知书发生未知异常[{}]", JSONUtil.toJsonStr(client), e);
            throw new MithrasException("生成基础资料-廉洁自律告知书发生未知异常[{}]");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    private void checkCode(List<String> codeList, String codeTypeDesc) {
        if (CollectionUtils.isEmpty(codeList)) {
            return;
        }
        if (codeList.size() == 1) {
            // 如果是单合同，则合同编号不允许出现序号后缀
            // 通过判断"-"数量来确定是否包含序号后缀
            int count = CharSequenceUtil.count(codeList.get(0), "-");
            if (count != 1) {
                throw new MithrasException("单个" + codeTypeDesc + "合同编号不允许出现序号后缀");
            }
        } else {
            // 多合同，则合同编号需要出现序号后缀且必须连号
            int[] array = new int[codeList.size()];
            for (int i = 0; i < codeList.size(); i++) {
                String code = codeList.get(i);
                int count = CharSequenceUtil.count(code, "-");
                if (count != 2) {
                    throw new MithrasException("多个" + codeTypeDesc + "合同编号需要带有序号后缀");
                }
                // 取出序号
                int index1 = code.lastIndexOf("-");
                int index2 = code.indexOf(")");
                String s = code.substring(index1, index2);
                int sequence = Integer.parseInt(s.substring(1));
                array[i] = sequence;
            }
            // 数组排序
            Arrays.sort(array);
            // 如果是顺序的编号，则差值为1，如果前一个+1不等于后一个，则说明不连续
            for (int i = 0; i < array.length; i++) {
                if (i == array.length - 1) {
                    break;
                }
                if ((array[i] + 1) != array[i + 1]) {
                    throw new MithrasException("多个" + codeTypeDesc + "合同编号后缀序号需连续");
                }
            }
        }
    }

    @SneakyThrows
    protected void contractAutoGenerate(ContractBaseInfo contractBaseInfo, String bizType) {
        Map<String, Map<String, ThrowingConsumer<ContractBaseInfo>>> sharedMap = SharedResources.sharedMap;
        Map<String, ThrowingConsumer<ContractBaseInfo>> consumerMap = sharedMap.get(bizType);
        if (CollectionUtils.isEmpty(consumerMap)) {
            return;
        }
        List<String> generateContractTypeList = contractBaseInfo.getGenerateContractTypeList();
        List<String> msgList = new ArrayList<>();
        List<String> exceptionList = new ArrayList<>();
        for (String contractType : generateContractTypeList) {
            try{
                consumerMap.getOrDefault(contractType, ignored -> {
                }).accept(contractBaseInfo);
            }catch (MithrasException e){
                msgList.add(e.getMessage());
            }catch (Exception e){
                log.error("合同自动生成发生未知异常[contractCode:{}]", contractBaseInfo.getContractCode(), e);
                ContractTypeEnum byName = ContractTypeEnum.getByName(contractType);
                if(byName != null) {
                    exceptionList.add(byName.getDisplay()+"发生未知异常");
                }
            }
        }
        // 优先抛出Exception异常
        if(CollectionUtil.isNotEmpty(exceptionList)){
            throw new MithrasException(exceptionList.get(0));
        }
        if(CollectionUtil.isNotEmpty(msgList)){
            throw new MithrasException(CharSequenceUtil.join("; ", msgList));
        }
    }
}
