package cn.zswltech.mithras.application.orchestration.contract.file.generator;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractBizTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractMainShipTradeZLHZRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractMainShipZLHZRender;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractMainZLHZRender;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.file.AbstractContractGenerate;
import cn.zswltech.mithras.contract.application.file.ContractGenerateAction;
import cn.zswltech.mithras.contract.application.file.SharedResources;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description 租赁-回租 合同文本生成器
 * 可生成 主合同、主合同附件、咨询合同、保证合同、抵押合同
 */
@Slf4j
@Component
public class ZLHZContractGenerator extends AbstractContractGenerate {
    @Resource
    private ContractMainZLHZRender contractMainZLHZRender;
    @Resource
    private ContractMainShipZLHZRender contractMainShipZLHZRender;
    @Resource
    private ContractMainShipTradeZLHZRender contractMainShipTradeZLHZRender;

    private final String BIZ_TYPE = ContractBizTypeEnum.ZLHZ.name();

    @Override
    public void generate(ContractBaseInfo contractBaseInfo) throws Exception {
        contractAutoGenerate(contractBaseInfo,BIZ_TYPE);
//        // 主合同
//        mainContract(contractBaseInfo);
//        // 咨询合同
//        consultingContract(contractBaseInfo);
//        // 保证合同
//        guarantorContract(contractBaseInfo);
//        // 决议文件
//        resolutionContract(contractBaseInfo);
//        // 抵押合同
//        mortgageContract(contractBaseInfo);
//        // 质押合同
//        pledgeContract(contractBaseInfo);
//        // 租赁物文件
//        leaseItemContract(contractBaseInfo);
//        // 基础资料
//        baseProfileContract(contractBaseInfo);
    }

    /**
     * 基础资料
     * @param contractBaseInfo
     */
    private void baseProfileContract(ContractBaseInfo contractBaseInfo) {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                generateBaseProfileContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成基础资料发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成基础资料发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成基础资料发生未知异常";
            }
        });
        checkMessage(stringCompletableFuture);
    }

    /**
     * 租赁物文件
     * @param contractBaseInfo
     */
    private void leaseItemContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generateLeaseItemContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成租赁物文件发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成租赁物文件发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成租赁物文件发生未知异常";
            }
        }));
    }

    /**
     * 质押合同
     * @param contractBaseInfo
     */
    private void pledgeContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generatePledgeContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成质押合同发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成质押合同发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成质押合同发生未知异常";
            }
        }));
    }

    /**
     * 抵押合同
     * @param contractBaseInfo
     */
    private void mortgageContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generateMortgageContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成抵押合同发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成抵押合同发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成抵押合同发生未知异常";
            }
        }));
    }

    /**
     * 决议文件
     * @param contractBaseInfo
     */
    private void resolutionContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generateResolutionContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成决议文件发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成决议文件发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成决议文件发生未知异常";
            }
        }));
    }

    /**
     * 保证合同
     * @param contractBaseInfo
     */
    private void guarantorContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generateGuarantorContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成保证合同发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成保证合同发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成保证合同发生未知异常";
            }
        }));
    }

    /**
     * 咨询合同
     * @param contractBaseInfo
     */
    private void consultingContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                generateConsultingContract(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成咨询合同发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成咨询合同发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成咨询合同发生未知异常";
            }
        }));
    }

    /**
     * 主合同
     * @param contractBaseInfo
     */
    private void mainContract(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                // 项目评审风控行业分类=水上运输业的需要特殊处理
                if (SpringUtil.getBean(ProjReviewService.class).projReviewIsWaterTransport(contractBaseInfo.getProjReviewId())) {
                    generateWaterTransportMainContract(contractBaseInfo);
                    generateWaterTransportTradeContract(contractBaseInfo);
                } else {
                    generateMainContract(contractBaseInfo);
                }
                return null;
            } catch (MithrasException e) {
                return "生成主合同发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成主合同发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成主合同发生未知异常";
            }
        }));
    }

    private void generateWaterTransportMainContract(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainShipZLHZRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainShipZLHZRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainShipZLHZRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateWaterTransportTradeContract(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainShipTradeZLHZRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainShipTradeZLHZRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainShipTradeZLHZRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成买卖合同发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成买卖合同发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateMainContract(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainZLHZRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainZLHZRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainZLHZRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void mainContractAttachment(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                // 项目评审风控行业分类=水上运输业的不需要生成主合同附件
                if (SpringUtil.getBean(ProjReviewService.class).projReviewIsWaterTransport(contractBaseInfo.getProjReviewId())) {
                    return null;
                }
                generateMainContractAttachment(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成主合同附件发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成主合同附件发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成主合同附件发生未知异常";
            }
        }));
    }

    private void generateMainContractAttachment(ContractBaseInfo contractBaseInfo) throws Exception {
        // 租赁物清单
        this.generaMainContractLeaseItem(contractBaseInfo);
        // 租赁附表（概算表）
        this.generateMainContractEstimateRent(contractBaseInfo);
        // 实际租金支付表
        this.generateMainContractActualRent(contractBaseInfo);
        // 资产所有权转移确认书
        this.generateMainContractAssetsOwnerChange(contractBaseInfo);
        // 资产所有权转移确认书
        this.generateMainContractLeaseItemQualifiedConfirm(contractBaseInfo);
        // 收款确认书
        this.generateMainContractCollectionConfirm(contractBaseInfo);
    }

    /**
     * 主合同附件-租赁物清单
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    private void generaMainContractLeaseItem(ContractBaseInfo contractBaseInfo) throws Exception {
        List<ContractLeaseItem> contractLeaseItemList = businessDataRepository.listContractLeaseItem(contractBaseInfo.getId());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(contractLeaseItemList)) {
            // 租赁物清单为空 不生成该文件
            return;
        }

        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainLeaseItemRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.LEASE_ITEM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainLeaseItemRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainLeaseItemRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同附件-租赁物清单发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同附件-租赁物清单发生未知异常");
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
     * 主合同附件-概算租金/支付表
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    private void generateMainContractEstimateRent(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainRentEstimateRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ESTIMATE_RENT.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainRentEstimateRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainRentEstimateRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同附件-概算租金/支付表发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同附件-概算租金/支付表发生未知异常");
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
     * 主合同附件-实际租金/支付表
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    private void generateMainContractActualRent(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainRentActualRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ACTUAL_RENT.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainRentActualRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainRentActualRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同附件-实际租金/支付表发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同附件-实际租金/支付表发生未知异常");
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
     * 主合同附件-资产所有权转移确认书
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    private void generateMainContractAssetsOwnerChange(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainAssetsOwnerChangeRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ASSETS_OWNER_CONFIRM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainAssetsOwnerChangeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainAssetsOwnerChangeRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同附件-资产所有权转移确认书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同附件-资产所有权转移确认书发生未知异常");
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
     * 主合同附件-资产所有权转移确认书
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    private void generateMainContractLeaseItemQualifiedConfirm(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainLeaseItemQualifiedConfirmRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.LEASE_ITEM_QUALIFIED_CONFIRM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainLeaseItemQualifiedConfirmRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainLeaseItemQualifiedConfirmRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同附件-租赁物合格接收确认书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同附件-租赁物合格接收确认书发生未知异常");
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
     * 主合同附件-收款确认书
     *
     * @param contractBaseInfo 合同基本信息
     * @throws Exception 任何异常
     */
    private void generateMainContractCollectionConfirm(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractMainCollectionConfirmRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.COLLECTION_CONFIRM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractMainCollectionConfirmRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractMainCollectionConfirmRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成主合同附件-收款确认书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成主合同附件-收款确认书发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                is.close();
            }
            if (!Objects.isNull(os)) {
                os.close();
            }
        }
    }

    @Override
    @PostConstruct
    public void init() {
        Map<String, Map<String, ContractGenerateAction<ContractBaseInfo>>> sharedMap = SharedResources.sharedMap;
        Map<String, ContractGenerateAction<ContractBaseInfo>> map = new ConcurrentHashMap<>();
        map.put(ContractTypeEnum.MAIN_CONTRACT.name(), this::mainContract);
        map.put(ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), this::mainContractAttachment);
        map.put(ContractTypeEnum.CONSULTING_CONTRACT.name(), this::consultingContract);
        map.put(ContractTypeEnum.GUARANTEE_CONTRACT.name(), this::guarantorContract);
        map.put(ContractTypeEnum.RESOLUTION_FILE.name(), this::resolutionContract);
        map.put(ContractTypeEnum.MORTGAGE_CONTRACT.name(), this::mortgageContract);
        map.put(ContractTypeEnum.PLEDGE_CONTRACT.name(), this::pledgeContract);
        map.put(ContractTypeEnum.LEASE_ITEM_FILE.name(), this::leaseItemContract);
        map.put(ContractTypeEnum.BASE_PROFILE.name(), this::baseProfileContract);
        map.put(ContractTypeEnum.OTHER_CONTRACT.name(), this::generateOther);
        sharedMap.put(BIZ_TYPE,map);
    }
}
