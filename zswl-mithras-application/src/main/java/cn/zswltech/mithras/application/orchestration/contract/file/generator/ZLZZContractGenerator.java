package cn.zswltech.mithras.application.orchestration.contract.file.generator;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractBizTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.contractzlzz.*;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.ContractSignInfoService;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingqi
 * @date 2023/4/19
 * @description 租赁-直租合同文本生成器
 */
@Slf4j
@Component
public class ZLZZContractGenerator extends AbstractContractGenerate {
    @Resource
    private ContractZLZZTerminateAgreementRender contractZLZZTerminateAgreementRender;
    @Resource
    private ContractZLZZMainRender contractZLZZMainRender;
    @Resource
    private ContractZLZZDealRender contractZLZZDealRender;
    @Resource
    private ContractZLZZLeaseItemRender contractZLZZLeaseItemRender;
    @Resource
    private ContractZLZZEstimatePayRender contractZLZZEstimatePayRender;
    @Resource
    private ContractZLZZActualPayRender contractZLZZActualPayRender;
    @Resource
    private ContractZLZZLeaseItemAcceptRender contractZLZZLeaseItemAcceptRender;
    @Resource
    private ContractZLZZStartRentRender contractZLZZStartRentRender;
    @Resource
    private ContractZLZZRentPayRender contractZLZZRentPayRender;
    @Resource
    private ContractZLZZSuppleEarnestRender contractZLZZSuppleEarnestRender;
    @Resource
    private ContractZLZZOwnerChangeRender contractZLZZOwnerChangeRender;
    @Resource
    private ContractZLZZRentAdjustRender contractZLZZRentAdjustRender;
    @Resource
    private ContractZLZZShipMainRender contractZLZZShipMainRender;
    @Resource
    private ContractZLZZShipTradeRender contractZLZZShipTradeRender;

    private final String BIZ_TYPE = ContractBizTypeEnum.ZLZZ.name();

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
                    generateZZMainContract(contractBaseInfo);
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

    private void generateZZMainContract(ContractBaseInfo contractBaseInfo) throws Exception {
        // 融资租赁合同（主合同）
        this.generateFinancingLease(contractBaseInfo);
        // 买卖合同
        this.generateDeal(contractBaseInfo);
        // 终止协议
        this.generateTerminateAgreement(contractBaseInfo);
    }

    private void generateWaterTransportMainContract(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZShipMainRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZShipMainRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZShipMainRender.signatories(contractBaseInfo));
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
            String fileName = contractZLZZShipTradeRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZShipTradeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZShipTradeRender.signatories(contractBaseInfo));
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

    private void mainContractAttachment(ContractBaseInfo contractBaseInfo) {
        checkMessage(CompletableFuture.supplyAsync(() -> {
            try {
                // 项目评审风控行业分类=水上运输业的不需要生成主合同附件
                if (SpringUtil.getBean(ProjReviewService.class).projReviewIsWaterTransport(contractBaseInfo.getProjReviewId())) {
                    return null;
                }
                generateZZMainContractAttachment(contractBaseInfo);
                return null;
            } catch (MithrasException e) {
                return "生成主合同附件发生异常[" + e.getMessage() + "]";
            } catch (Exception e) {
                log.error("生成主合同附件发生未知异常[{}]", contractBaseInfo.getId(), e);
                return "生成主合同附件发生未知异常";
            }
        }));
    }

    private void generateZZMainContractAttachment(ContractBaseInfo contractBaseInfo) throws Exception {
        // 租赁物清单
        this.generateLeaseItem(contractBaseInfo);
        // 概算租金及租前息支付表
        this.generateEstimatePay(contractBaseInfo);
        // 实际租金及租前息支付表
        this.generateActualPay(contractBaseInfo);
        // 租赁物接受书
        this.generateLeaseItemAccept(contractBaseInfo);
        // 起租通知书
        this.generateStartRent(contractBaseInfo);
        // 租金及租前息支付通知书
        this.generateRentPay(contractBaseInfo);
        // 补足租赁保证金通知书
        this.generateSuppleEarnest(contractBaseInfo);
        // 所有权转移证书
        this.generateOwnerChange(contractBaseInfo);
        // 租金及租前息调整通知书
        this.generateRentAdjust(contractBaseInfo);
    }

    private void generateTerminateAgreement(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZTerminateAgreementRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), ContractSubTypeEnum.ZL_ZZ_TERMINATE_AGREEMENT.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZTerminateAgreementRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZTerminateAgreementRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成终止协议发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成终止协议发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateFinancingLease(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZMainRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZMainRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZMainRender.signatories(contractBaseInfo));
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

    private void generateDeal(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZDealRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT.name(), ContractSubTypeEnum.ZL_ZZ_DEAL.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZDealRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZDealRender.signatories(contractBaseInfo));
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

    private void generateLeaseItem(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZLeaseItemRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_LEASE_ITEM.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZLeaseItemRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZLeaseItemRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成租赁物清单发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成租赁物清单发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateEstimatePay(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZEstimatePayRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_ESTIMATE_PAY.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZEstimatePayRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZEstimatePayRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成概算租金及租前息支付表发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成概算租金及租前息支付表发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateActualPay(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZActualPayRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_ACTUAL_PAY.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZActualPayRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZActualPayRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成实际租金及租前息支付表发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成实际租金及租前息支付表发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateLeaseItemAccept(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZLeaseItemAcceptRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_LEASE_ITEM_ACCEPT.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZLeaseItemAcceptRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZLeaseItemAcceptRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成租赁物接受书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成租赁物接受书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateStartRent(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZStartRentRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_START_RENT.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZStartRentRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZStartRentRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成起租通知书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成起租通知书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateRentPay(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZRentPayRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_RENT_BEFOREINTEREST_PAY.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZRentPayRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZRentPayRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成租金租前息支付通知书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成租金租前息支付通知书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateSuppleEarnest(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZSuppleEarnestRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_SUPPLE_EARNEST.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZSuppleEarnestRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZSuppleEarnestRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成补足租赁保证金通知书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成补足租赁保证金通知书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateOwnerChange(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZOwnerChangeRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_OWNER_CHANGE.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZOwnerChangeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZOwnerChangeRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成所有权转移证书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成所有权转移证书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    private void generateRentAdjust(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            String fileName = contractZLZZRentAdjustRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractTypeEnum.MAIN_CONTRACT_ATTACHMENT.name(), ContractSubTypeEnum.ZL_ZZ_BEFOREINTEREST_ADJUST.name(), BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractZLZZRentAdjustRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractZLZZRentAdjustRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成租金租前息调整通知书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成租金租前息调整通知书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
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
