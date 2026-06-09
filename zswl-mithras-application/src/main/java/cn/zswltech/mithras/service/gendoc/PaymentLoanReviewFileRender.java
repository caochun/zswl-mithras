package cn.zswltech.mithras.service.gendoc;
import cn.zswltech.mithras.contract.core.application.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.application.ContractPledgeService;
import cn.zswltech.mithras.contract.core.application.ContractMortgageService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.MortgageTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.PledgeTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractTenantryLibService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.projectprocess.service.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.service.util.ExcelKeywordReplacerUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import cn.zswltech.mithras.contract.core.application.ContractPriceService;

@Component
public class PaymentLoanReviewFileRender extends AbstractContractRender<PaymentBaseInfo> {
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private ContractTenantryLibService contractTenantryLibService;

    @Override
    public String render(OutputStream outputStream, PaymentBaseInfo paymentBaseInfo) throws Exception {
        Long contractId = paymentBaseInfo.getContractId();
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        Long projReviewId = contractBaseInfo.getProjReviewId();
        // 用最新的值填充客户风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
        String riskControlIndustryClassify = Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null);
        String fileName;
        // 判断客户风控行业分类是否为公用事业、民生消费、旅游行业 真：平台类 假：产业类
        boolean flag = Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.PUBLIC_UTILITIES.name()) ||
                Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.CIVIL_CONSUMPTION.name()) ||
                Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.TRAVEL.name());
        // 合同信息
        ContractBaseInfoDetailREQ contractDetailReq = new ContractBaseInfoDetailREQ();
        contractDetailReq.setId(contractId);
        ContractBaseInfoDetailRSP contractBaseInfoDetailRSP = contractBaseInfoService.editionDetail(contractDetailReq);
        //  租赁报价方案表
        ContractPriceDetailREQ priceDetailReq = new ContractPriceDetailREQ();
        priceDetailReq.setContractId(contractId);
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.editionDetail(priceDetailReq);
        //  租赁报价方案
        ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.lambdaQuery().eq(ProjReviewLeasePrice::getProjectId, projReviewId).one();
        //  合同报价方案
        ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, contractId).one();
        //  已付金额(元)
        Pair<Long, Long> capitalDistribute = paymentBaseInfoService.calculateCapitalDistribution(contractId, paymentBaseInfo.getId());
        //  最新合同
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.lambdaQuery()
                .eq(ContractBaseInfoLib::getOriginId, contractId)
                .orderByDesc(ContractBaseInfoLib::getVersion).last("limit 1").one();

        StringBuilder sBuilder = new StringBuilder();
        if (!ObjectUtils.isEmpty(contractBaseInfoLib)) {
            String version = contractBaseInfoLib.getVersion();
            //  主承租人/债权人-> 联合承租人/债务人
            List<ContractTenantryLib> contractTenantryLibs = contractTenantryLibService.lambdaQuery()
                    .eq(ContractTenantryLib::getContractId, contractId)
                    .eq(ContractTenantryLib::getVersion, version).list();
            if (!ObjectUtils.isEmpty(contractTenantryLibs)) {
                for (ContractTenantryLib contractTenantryLib : contractTenantryLibs) {
                    if (LesseeTypeEnum.MAIN_LESSSEE.name().equalsIgnoreCase(contractTenantryLib.getLesseeType())) {
                        sBuilder.append(contractTenantryLib.getLesseeName());
                    }
                    if (LesseeTypeEnum.JOINT_LESSEE.name().equals(contractTenantryLib.getLesseeType())) {
                        sBuilder.append("、");
                        sBuilder.append("\n");
                        sBuilder.append(contractTenantryLib.getLesseeName());
                    }
                }
            }
        }

        ContractIdListREQ contractIdListREQ = new ContractIdListREQ(contractId);
        //  合同-担保措施列表
        List<ContractGuarantorListRSP> contractGuarantorListRSPS = contractGuarantorService.list(contractIdListREQ);
        String defaultStr = "/";

        // 渲染参数
        Map<String, String> renderMap = new HashMap<>();
        boolean contractBaseInfoDetailRSPFlag = Objects.nonNull(contractBaseInfoDetailRSP);
        // 发起部门
        renderMap.put("bizDeptName", contractBaseInfoDetailRSPFlag ? contractBaseInfoDetailRSP.getBizDeptName() : defaultStr);
        // 项目主办
        renderMap.put("projSponsorUserName", contractBaseInfoDetailRSPFlag ? contractBaseInfoDetailRSP.getProjSponsorUserName() : defaultStr);
        // 承租人
        renderMap.put("clientName", !sBuilder.toString().isEmpty() ? sBuilder.toString() : contractBaseInfoDetailRSP.getClientName());
        // 项目编号
        renderMap.put("projCode", contractBaseInfoDetailRSPFlag ? contractBaseInfoDetailRSP.getProjCode() : defaultStr);
        //  合同编号 融资租赁合同及其附件-售后回租 融资租赁合同及其附件-直接租赁
        if (Objects.equals(LeaseType.zhi_zu.name(), contractBaseInfo.getLeaseType())) {
            renderMap.put("contractCodeZhiZu", contractBaseInfoDetailRSPFlag ? contractBaseInfoDetailRSP.getContractCode() : defaultStr);
            renderMap.put("contractCodeHuiZu", defaultStr);
        }
        if (Objects.equals(LeaseType.hui_zu.name(), contractBaseInfo.getLeaseType())) {
            renderMap.put("contractCodeZhiZu", defaultStr);
            renderMap.put("contractCodeHuiZu", contractBaseInfoDetailRSPFlag ? contractBaseInfoDetailRSP.getContractCode() : defaultStr);
        }


        boolean contractPriceDetailRSPFlag = (Objects.nonNull(contractPriceDetailRSP));
        // 合同金额
        Long applyCreditAmount = contractPriceDetailRSPFlag ? contractPriceDetailRSP.getApplyCreditAmount() : 0L;
        renderMap.put("applyCreditAmount", computeNumber(applyCreditAmount));
        //  名义价款
        Long nominalPrice = contractPriceDetailRSPFlag ? contractPriceDetailRSP.getNominalPrice() : 0L;
        renderMap.put("contractNominalPrice", computeNumber(nominalPrice));
        // 计划付款金额(元)
        Long planedPaidAmount = paymentBaseInfo.getApplyPaymentAmount() != null ? paymentBaseInfo.getApplyPaymentAmount() : 0;
        renderMap.put("planedPaidAmount", computeNumber(planedPaidAmount));

        //  授信金额  评审流程-报价方案-申报授信金额（元）
        renderMap.put("applyCreditAmounts", Objects.nonNull(projReviewLeasePrice) ? computeNumber(projReviewLeasePrice.getProjectApprovalAmount()) : "0");
        //  租赁期限  合同创建流程-报价方案-租赁期限(月)
        renderMap.put("leaseMonthCount", Objects.nonNull(contractLeasePrice) ? contractLeasePrice.getLeaseMonthCount() + "" : defaultStr);
        // 已付金额(元)
        Long capitalDistributeValue = Objects.nonNull(capitalDistribute) ? capitalDistribute.getValue() : 0L;
        renderMap.put("amountPaid", computeNumber(capitalDistributeValue));
        //  剩余可申请金额
        Long remainingApplyAmount = applyCreditAmount - capitalDistributeValue - planedPaidAmount;
        renderMap.put("remainingApplyAmount", computeNumber(remainingApplyAmount));
        //  保证金
        Long earnestMoney = paymentBaseInfo.getEarnestMoney();
        renderMap.put("earnestMoney", computeNumber(earnestMoney));
        //  手续费
        Long commission = paymentBaseInfo.getCommission();
        renderMap.put("commission", computeNumber(commission));
        //  咨询费
        Long consultingFee = paymentBaseInfo.getConsultingFee();
        renderMap.put("consultingFee", computeNumber(consultingFee));
        //  首期利息
        Long firstInstallmentInterest = paymentBaseInfo.getFirstInstallmentInterest();
        renderMap.put("other", computeNumber(firstInstallmentInterest));
        //  首期租金
        renderMap.put("firstInstallmentInterest", computeNumber(paymentBaseInfo.getDownPayment()));
        //  保证合同
        String guaranteeContractClients = !ObjectUtils.isEmpty(contractGuarantorListRSPS) ? contractGuarantorListRSPS.stream().map(contractGuarantorListRSP -> {
            String guarantorContractCode = contractGuarantorListRSP.getGuarantorContractCode();
            List<ClientInfo> guarantorInfo = contractGuarantorListRSP.getGuarantorInfo();
            String clients = guarantorInfo.stream().map(ClientInfo::getClientName).collect(Collectors.joining("、"));
            return "【" + guarantorContractCode + "】" + "-" + clients;
        }).collect(Collectors.joining("\n")) : defaultStr;
        renderMap.put("guaranteeContractClients", guaranteeContractClients);
        // 租赁结构安排及管理咨询合同
        if (Objects.nonNull(consultingFee) && consultingFee > 0) {
            renderMap.put("consultingContractCode", Objects.nonNull(contractBaseInfoLib) ? contractBaseInfoLib.getConsultingContractCode() : contractBaseInfo.getConsultingContractCode());
        } else {
            renderMap.put("consultingContractCode", defaultStr);
        }

        //  运营经办 - 发起人
        renderMap.put("createUserName", sysUserService.getUserName(paymentBaseInfo.getCreateBy()));
        //  运营复核
        List<UserDO> loanreviewpostList = userDOMapper.selectByIds(sysUserService.jobUsers(new HashSet<String>() {{
            add(JobEnum.loanreviewpost.name());
        }}));
        // 发起人不能是审批人
        String loanReviewPostNames = !ObjectUtils.isEmpty(loanreviewpostList) ? loanreviewpostList.stream()
                .filter(user -> !user.getUserName().equals(renderMap.get("createUserName")))
                .map(UserDO::getUserName)
                .collect(Collectors.joining("、")) : defaultStr;
        renderMap.put(JobEnum.loanreviewpost.name(), loanReviewPostNames);
        //  运营负责人
        List<UserDO> headofyyglbList = userDOMapper.selectByIds(sysUserService.jobUsers(new HashSet<String>() {{
            add(JobEnum.headofyyglb.name());
        }}));
        String userNames = !ObjectUtils.isEmpty(headofyyglbList) ? headofyyglbList.stream().map(UserDO::getUserName).collect(Collectors.joining("、")) : defaultStr;
        renderMap.put(JobEnum.headofyyglb.name(), userNames);
        // 公用事业、民生消费、旅游行业为平台类，平台类之外的划分为产业类
        //  合同-抵押措施
        List<ContractMortgageListRSP> contractMortgageListRSPS = contractMortgageService.list(contractIdListREQ);
        //  合同-质押措施
        List<ContractPledgeListRSP> contractPledgeListRSPList = contractPledgeService.list(contractIdListREQ);
        if (flag) {
            fileName = "放款审核表-平台" + GlobalConstants.OFFICE_EXCEL_SUFFIX;

            //抵质押合同
            StringBuilder mortgagePledgeContract = new StringBuilder();
            //  抵押合同
            if (CollectionUtils.isNotEmpty(contractMortgageListRSPS)) {
                for (ContractMortgageListRSP contractMortgageListRSP : contractMortgageListRSPS) {
                    //抵押合同编号
                    String mortgageContractCode = contractMortgageListRSP.getMortgageContractCode();
                    //抵押人名称
                    String clients = contractMortgageListRSP.getMortgageInfo().stream().map(ClientInfo::getClientName).collect(Collectors.joining("、"));
                    mortgagePledgeContract.append("\n").append(mortgageContractCode).append("-").append(clients);
                }
            }
            // 质押合同
            if (CollectionUtils.isNotEmpty(contractPledgeListRSPList)) {
                for (ContractPledgeListRSP contractPledgeListRSP : contractPledgeListRSPList) {
                    //质押合同编号
                    String pledgeContractCode = contractPledgeListRSP.getPledgeContractCode();
                    //质押人名称
                    String clients = contractPledgeListRSP.getPledgeInfo().stream().map(ClientInfo::getClientName).collect(Collectors.joining("、"));
                    mortgagePledgeContract.append("\n").append(pledgeContractCode).append("-").append(clients);
                }
            }
            renderMap.put("mortgagePledgeContract", mortgagePledgeContract.length() > 2 ? mortgagePledgeContract.substring(2) : defaultStr);
        } else {
            fileName = "放款审核表-产业" + GlobalConstants.OFFICE_EXCEL_SUFFIX;

            //抵押类型不为「动产抵押」，质押类型不为「股权质押」「应收账款质押」的合同
            StringBuilder otherContract = new StringBuilder();
            //  抵押合同（适用于动产抵押）
            StringBuilder mortgageContractCodeClients = new StringBuilder();
            //  质押合同（股权）
            StringBuilder equityPledgeContract = new StringBuilder();
            // 质押合同（应收账款）
            StringBuilder accountsReceivablePledgeContract = new StringBuilder();

            //处理抵押合同
            if (CollectionUtils.isNotEmpty(contractMortgageListRSPS)) {
                for (ContractMortgageListRSP contractMortgageListRSP : contractMortgageListRSPS) {
                    //抵押合同编号
                    String mortgageContractCode = contractMortgageListRSP.getMortgageContractCode();
                    //抵押人名称
                    String clients = contractMortgageListRSP.getMortgageInfo().stream().map(ClientInfo::getClientName).collect(Collectors.joining("、"));

                    //  抵押合同（适用于动产抵押）
                    if (MortgageTypeEnum.CHATTEL_MORTGAGE.name().equals(contractMortgageListRSP.getContractMortgageType())) {
                        mortgageContractCodeClients.append("\n").append(mortgageContractCode).append("-").append(clients);
                    } else {
                        //抵押类型不为「动产抵押」
                        otherContract.append("\n").append(mortgageContractCode).append("-").append(clients);
                    }
                }
            }
            //处理质押合同
            if (CollectionUtils.isNotEmpty(contractPledgeListRSPList)) {
                for (ContractPledgeListRSP contractPledgeListRSP : contractPledgeListRSPList) {
                    //质押合同编号
                    String pledgeContractCode = contractPledgeListRSP.getPledgeContractCode();
                    //质押人名称
                    String clients = contractPledgeListRSP.getPledgeInfo().stream().map(ClientInfo::getClientName).collect(Collectors.joining("、"));

                    if (PledgeTypeEnum.EQUITY_PLEDGE.name().equals(contractPledgeListRSP.getContractPledgeType())) {
                        equityPledgeContract.append("\n").append(pledgeContractCode).append("-").append(clients);
                    } else if (PledgeTypeEnum.ACCOUNTS_RECEIVABLE_PLEDGE.name().equals(contractPledgeListRSP.getContractPledgeType())) {
                        accountsReceivablePledgeContract.append("\n").append(pledgeContractCode).append("-").append(clients);
                    } else {
                        //质押类型不为「股权质押」「应收账款质押」的合同
                        otherContract.append("\n").append(pledgeContractCode).append("-").append(clients);
                    }
                }
            }
            //去除前后的换行和“、”
            String mortgageContractCodeClientsStr = StringUtils.isBlank(mortgageContractCodeClients) ? defaultStr : mortgageContractCodeClients.substring(2);
            String equityPledgeContractStr = StringUtils.isBlank(equityPledgeContract) ? defaultStr : equityPledgeContract.substring(2);
            String accountsReceivablePledgeContractStr = StringUtils.isBlank(accountsReceivablePledgeContract) ? defaultStr : accountsReceivablePledgeContract.substring(2);
            String otherContractStr = StringUtils.isBlank(otherContract) ? defaultStr : otherContract.substring(2);

            renderMap.put("mortgageContractCodeClients", mortgageContractCodeClientsStr);
            renderMap.put("equityPledgeContract", equityPledgeContractStr);
            renderMap.put("accountsReceivablePledgeContract", accountsReceivablePledgeContractStr);
            renderMap.put("otherContract", otherContractStr);
        }
        // 渲染文档
        Workbook workbook = new XSSFWorkbook(getBean(FileTemplateService.class).getTemplate("付款-放款底稿", fileName));
        ExcelKeywordReplacerUtil.replaceKeywordInWorkbook(workbook, renderMap);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        return "放款审核表" + GlobalConstants.OFFICE_EXCEL_SUFFIX;
    }

    private String computeNumber(Long num) {
        return Objects.isNull(num) || num == 0L ? "0" : NumberUtil.decimalFormat(",##0.##", new BigDecimal(num).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP));
    }

    @Override
    protected Set<Long> signClientIds(PaymentBaseInfo paymentBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(PaymentBaseInfo paymentBaseInfo) {
        FileTemplate templateRecord = getFileTemplate(paymentBaseInfo);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private FileTemplate getFileTemplate(PaymentBaseInfo paymentBaseInfo) {
        Long contractId = paymentBaseInfo.getContractId();
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        Long projReviewId = contractBaseInfo.getProjReviewId();
        // 用最新的值填充客户风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(contractBaseInfo.getClientId());
        String riskControlIndustryClassify = Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null);
        String fileName;
        // 判断客户风控行业分类是否为公用事业、民生消费、旅游行业 真：平台类 假：产业类
        boolean flag = Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.PUBLIC_UTILITIES.name()) ||
                Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.CIVIL_CONSUMPTION.name()) ||
                Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.TRAVEL.name());
        if (flag) {
            fileName = "放款审核表-平台" + GlobalConstants.OFFICE_EXCEL_SUFFIX;
        }else {
            fileName = "放款审核表-产业" + GlobalConstants.OFFICE_EXCEL_SUFFIX;
        }
        return getBean(FileTemplateService.class).getTemplateRecord("付款-放款底稿", fileName);
    }

    @Override
    protected String customTemplateKey(PaymentBaseInfo paymentBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(paymentBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
