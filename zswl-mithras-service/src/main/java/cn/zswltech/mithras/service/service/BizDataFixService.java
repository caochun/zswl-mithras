package cn.zswltech.mithras.service.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.model.RatingClientAreaIndicator;
import cn.zswltech.mithras.factory.model.RatingClientAreaIndicatorConfig;
import cn.zswltech.mithras.factory.model.RatingSnapshot;
import cn.zswltech.mithras.factory.service.RatingClientAreaIndicatorConfigService;
import cn.zswltech.mithras.factory.service.RatingClientAreaIndicatorService;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.factory.service.RatingSnapshotService;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.customer.domain.enums.client.*;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.JointGuaranteeMarkEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.enums.kpi.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProvisionStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.policy.domain.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelImporter;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceProjectProfitDetailReceipt;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.*;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.client.*;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitDetailReceiptService;
import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitDetailService;
import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestDetailRecordService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessDivideService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessService;
import cn.zswltech.mithras.service.service.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.service.kpi.*;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractGuarantorLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractVersionService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.kpi.KpiProjectDistributionWeightLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/12/12
 * @description
 */
@Slf4j
@Service
public class BizDataFixService {
    @Resource
    private ClientService clientService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoLibService projEstablishBaseInfoLibService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractVersionService contractVersionService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private OrgService orgService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;
    @Resource
    private ContractReceiptService contractReceiptService;

    public void fixProjReviewMissData() throws Exception {
        List<Long> targetProjReviewIds = ListUtil.of(748L, 721L, 749L, 711L, 591L, 556L, 561L, 555L, 661L, 596L, 567L, 757L, 621L, 601L, 600L, 518L, 522L, 571L, 652L, 609L, 579L, 533L, 628L, 539L, 615L, 624L, 619L, 650L, 583L, 543L, 630L, 584L, 625L, 511L, 638L, 642L, 643L, 645L, 647L, 690L, 684L, 648L, 673L, 651L, 680L, 678L, 681L, 656L, 655L, 670L, 691L, 710L, 717L, 718L, 730L, 726L, 736L, 742L, 775L, 783L, 767L, 782L, 802L, 794L, 796L, 830L, 824L, 845L, 832L, 848L, 855L, 836L, 850L, 846L, 849L, 859L, 854L, 875L, 886L, 874L, 891L, 904L, 913L, 897L, 890L, 894L, 889L, 893L, 906L, 907L, 921L, 908L, 916L, 925L, 975L, 947L, 935L, 959L, 942L, 939L, 1001L, 952L, 963L, 950L, 980L, 984L, 987L, 1005L, 1016L, 1026L, 1006L, 1022L, 1025L, 1046L, 1056L, 1077L, 1132L, 1088L, 1099L, 1082L, 1091L, 1123L, 1152L, 1150L, 1146L, 1188L, 1153L, 1216L, 1189L, 1213L, 1178L, 1214L, 1247L, 1242L, 1220L, 1227L, 1253L, 1264L, 1282L, 1263L, 1270L);
        List<String> sqlList = new LinkedList<>();
        for (Long projReviewId : targetProjReviewIds) {
            ProjReviewBaseInfo projReviewBaseInfo = SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(projReviewId);
            // 查合同
            List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).selectListByProjId(projReviewId);
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
                List<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                // 查询担保
                List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractIds(contractIds);
                // 担保人（去重后）
                Set<Long> guarantorIds = new HashSet<>();
                if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
                    for (ContractGuarantor contractGuarantor : contractGuarantorList) {
                        if (StrUtil.isNotBlank(contractGuarantor.getGuarantorIds())) {
                            List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
                            if (CollectionUtil.isNotEmpty(ids)) {
                                guarantorIds.addAll(ids);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(guarantorIds)) {
                        List<Client> clientList = SpringUtil.getBean(ClientService.class).listByIds(guarantorIds);
                        List<ClientInfo> clientInfoList = clientList.stream().map(e -> {
                            ClientInfo clientInfo = new ClientInfo();
                            clientInfo.setClientId(e.getId());
                            clientInfo.setClientName(e.getClientName());
                            clientInfo.setClientType(e.getClientType());
                            clientInfo.setStockRiskExposure(0L);
                            return clientInfo;
                        }).collect(Collectors.toList());
                        projReviewBaseInfo.setGuaranteeInfo(JSONUtil.toJsonStr(clientInfoList));
                    }
                }
                // 查询抵押
                List<ContractMortgage> contractMortgageList = SpringUtil.getBean(ContractMortgageService.class).listByContractIds(contractIds);
                // 抵押人（去重后）
                Set<Long> mortgageIds = new HashSet<>();
                if (CollectionUtil.isNotEmpty(contractMortgageList)) {
                    for (ContractMortgage contractMortgage : contractMortgageList) {
                        if (StrUtil.isNotBlank(contractMortgage.getMortgageIds())) {
                            List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
                            if (CollectionUtil.isNotEmpty(ids)) {
                                mortgageIds.addAll(ids);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(mortgageIds)) {
                        List<Client> clientList = SpringUtil.getBean(ClientService.class).listByIds(mortgageIds);
                        List<ClientInfo> clientInfoList = clientList.stream().map(e -> {
                            ClientInfo clientInfo = new ClientInfo();
                            clientInfo.setClientId(e.getId());
                            clientInfo.setClientName(e.getClientName());
                            clientInfo.setClientType(e.getClientType());
                            clientInfo.setStockRiskExposure(0L);
                            return clientInfo;
                        }).collect(Collectors.toList());
                        projReviewBaseInfo.setMortgagorInfo(JSONUtil.toJsonStr(clientInfoList));
                    }
                }
                // 查询质押
                List<ContractPledge> contractPledgeList = SpringUtil.getBean(ContractPledgeService.class).listByContractIds(contractIds);
                // 质押人（去重后）
                Set<Long> pledgeIds = new HashSet<>();
                if (CollectionUtil.isNotEmpty(contractPledgeList)) {
                    for (ContractPledge contractPledge : contractPledgeList) {
                        if (StrUtil.isNotBlank(contractPledge.getPledgeIds())) {
                            List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
                            if (CollectionUtil.isNotEmpty(ids)) {
                                pledgeIds.addAll(ids);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(pledgeIds)) {
                        List<Client> clientList = SpringUtil.getBean(ClientService.class).listByIds(pledgeIds);
                        List<ClientInfo> clientInfoList = clientList.stream().map(e -> {
                            ClientInfo clientInfo = new ClientInfo();
                            clientInfo.setClientId(e.getId());
                            clientInfo.setClientName(e.getClientName());
                            clientInfo.setClientType(e.getClientType());
                            clientInfo.setStockRiskExposure(0L);
                            return clientInfo;
                        }).collect(Collectors.toList());
                        projReviewBaseInfo.setPledgorInfo(JSONUtil.toJsonStr(clientInfoList));
                    }
                }
            } else {
                // 查立项
                ProjEstablishBaseInfo projEstablishBaseInfo = SpringUtil.getBean(ProjEstablishBaseInfoService.class).getById(projReviewBaseInfo.getProjEstablishId());
                if (Objects.nonNull(projEstablishBaseInfo)) {
                    projReviewBaseInfo.setGuaranteeInfo(projEstablishBaseInfo.getGuaranteeInfo());
                    projReviewBaseInfo.setMortgagorInfo(projEstablishBaseInfo.getMortgagorInfo());
                    projReviewBaseInfo.setPledgorInfo(projEstablishBaseInfo.getPledgorInfo());
                }
            }
            String guaranteeInfoParam = StrUtil.isBlank(projReviewBaseInfo.getGuaranteeInfo()) ? "null" : "'" + projReviewBaseInfo.getGuaranteeInfo() + "'";
            String mortgagorInfoParam = StrUtil.isBlank(projReviewBaseInfo.getMortgagorInfo()) ? "null" : "'" + projReviewBaseInfo.getMortgagorInfo() + "'";
            String pledgorInfoParam = StrUtil.isBlank(projReviewBaseInfo.getPledgorInfo()) ? "null" : "'" + projReviewBaseInfo.getPledgorInfo() + "'";
            String sql = String.format("update proj_review_base_info set update_time = update_time, guarantee_info = %s, mortgagor_info = %s, pledgor_info = %s where id = %s;", guaranteeInfoParam, mortgagorInfoParam, pledgorInfoParam, projReviewBaseInfo.getId());
            String sqlLib = String.format("update proj_review_base_info_lib set update_time = update_time, guarantee_info = %s, mortgagor_info = %s, pledgor_info = %s where origin_id = %s;", guaranteeInfoParam, mortgagorInfoParam, pledgorInfoParam, projReviewBaseInfo.getId());
            String sqlPricing = String.format("update proj_pricing_base_info set update_time = update_time, guarantee_info = %s, mortgagor_info = %s, pledgor_info = %s where proj_review_id = %s;", guaranteeInfoParam, mortgagorInfoParam, pledgorInfoParam, projReviewBaseInfo.getId());
            String sqlPricingLib = String.format("update proj_pricing_base_info_lib set update_time = update_time, guarantee_info = %s, mortgagor_info = %s, pledgor_info = %s where proj_review_id = %s;", guaranteeInfoParam, mortgagorInfoParam, pledgorInfoParam, projReviewBaseInfo.getId());
            sqlList.add(sql);
            sqlList.add(sqlLib);
            sqlList.add(sqlPricing);
            sqlList.add(sqlPricingLib);
        }
        Writer writer = FileUtil.getWriter("/Users/dingqi/fix.sql", StandardCharsets.UTF_8, true);
        writer.write(StrUtil.join("\n", sqlList));
        writer.flush();
    }

    public void initHistoryProjectClientMaterial() {
        // 授信立项
        this.groupProjEstablishClient();
        // 授信评审
        this.groupProjReviewClient();
        // 项目立项
        this.projEstablishClient();
        // 项目评审
        this.projReviewClient();
    }

    private void copyClientMaterial(Set<Long> clientIds, Long belongId, String businessType, Long sponsorUserId, Set<Long> theSameUserClientIds) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return;
        }
        for (Long clientId : clientIds) {
            Client client = clientMapper.selectById(clientId);
            if (Objects.isNull(client)) {
                continue;
            }
            LambdaQueryWrapper<MaterialsList> clientMaterialQuery = Wrappers.lambdaQuery();
            clientMaterialQuery.eq(MaterialsList::getBelongId, clientId);
            if (Objects.nonNull(client.getBelongSponsorId())) {
                if (theSameUserClientIds.contains(clientId)) {
                    // 只取主办的数据
                    clientMaterialQuery.eq(BaseModel::getCreateBy, sponsorUserId);
                } else {
                    // 只取管护权人的数据
                    clientMaterialQuery.eq(BaseModel::getCreateBy, client.getBelongSponsorId());
                }
            }
            List<String> typeList = new LinkedList<>();
            typeList.addAll(CorporationClientMaterialTypeEnum.needCopyType());
            typeList.addAll(NormalClientMaterialTypeEnum.needCopyType());
            clientMaterialQuery.in(MaterialsList::getMaterialsType, typeList);
            List<MaterialsList> sourceList = SpringUtil.getBean(MaterialsListService.class).list(clientMaterialQuery);
            if (CollectionUtil.isEmpty(sourceList)) {
                continue;
            }
            List<MaterialsList> targetList = new LinkedList<>();
            for (MaterialsList e : sourceList) {
                if (StrUtil.isNotBlank(e.getSourceBusinessKey())) {
                    // 特殊处理
                    String[] array = e.getSourceBusinessKey().split("@");
                    if (!businessType.startsWith(array[0]) || !Objects.equals(belongId.toString(), array[1])) {
                        continue;
                    }
                }
                MaterialsList m = BeanUtil.copyProperties(e, MaterialsList.class);
                m.reset();
                m.setBelongId(belongId);
                m.setBusinessType(businessType);
                m.setSourceBusinessKey(e.getBelongId().toString());
                m.setCreateBy(sponsorUserId);
                m.setCreateTime(e.getCreateTime());
                targetList.add(m);
            }
            SpringUtil.getBean(MaterialsListService.class).saveBatch(targetList);
        }
    }

    private void groupProjEstablishClient() {
        List<GroupCreditEstablishBaseInfo> groupCreditEstablishBaseInfoList = SpringUtil.getBean(GroupCreditEstablishBaseInfoService.class).list();
        for (GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo : groupCreditEstablishBaseInfoList) {
            // 先查询一下是否有数据，有的话就不处理了
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, groupCreditEstablishBaseInfo.getId());
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name());
            int count = SpringUtil.getBean(MaterialsListService.class).count(query);
            if (count > 0) {
                log.info("授信立项【{}-{}】已存在拷贝后的客户文件，不执行客户文件拷贝操作", groupCreditEstablishBaseInfo.getId(), groupCreditEstablishBaseInfo.getProjName());
                continue;
            }
            this.copyClientMaterial(Collections.singleton(groupCreditEstablishBaseInfo.getClientId()), groupCreditEstablishBaseInfo.getId(), BusinessModuleEnum.GROUP_CREDIT_ESTABLISH_CLIENT.name(), groupCreditEstablishBaseInfo.getProjSponsorUserId(), Collections.singleton(groupCreditEstablishBaseInfo.getClientId()));
        }
    }

    private void groupProjReviewClient() {
        List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfoList = SpringUtil.getBean(GroupCreditReviewBaseInfoService.class).list();
        for (GroupCreditReviewBaseInfo groupCreditReviewBaseInfo : groupCreditReviewBaseInfoList) {
            // 先查询一下是否有数据，有的话就不处理了
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, groupCreditReviewBaseInfo.getId());
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name());
            int count = SpringUtil.getBean(MaterialsListService.class).count(query);
            if (count > 0) {
                log.info("授信评审【{}-{}】已存在拷贝后的客户文件，不执行客户文件拷贝操作", groupCreditReviewBaseInfo.getId(), groupCreditReviewBaseInfo.getProjName());
                continue;
            }
            this.copyClientMaterial(Collections.singleton(groupCreditReviewBaseInfo.getClientId()), groupCreditReviewBaseInfo.getId(), BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name(), groupCreditReviewBaseInfo.getProjSponsorUserId(), Collections.singleton(groupCreditReviewBaseInfo.getClientId()));
        }
    }

    private void projEstablishClient() {
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = SpringUtil.getBean(ProjEstablishBaseInfoService.class).list();
        for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
            // 先查询一下是否有数据，有的话就不处理了
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, projEstablishBaseInfo.getId());
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_ESTABLISH_CLIENT.name());
            int count = SpringUtil.getBean(MaterialsListService.class).count(query);
            if (count > 0) {
                log.info("项目立项【{}-{}】已存在拷贝后的客户文件，不执行客户文件拷贝操作", projEstablishBaseInfo.getId(), projEstablishBaseInfo.getProjName());
                continue;
            }
            Set<Long> theSameUserClientIds = new HashSet<>();
            List<ProjEstablishPersonInfo> persons = new LinkedList<>();
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getLesseeInfo())) {
                List<ProjEstablishPersonInfo> plist = JSONUtil.toList(projEstablishBaseInfo.getLesseeInfo(), ProjEstablishPersonInfo.class);
                plist.forEach(e -> theSameUserClientIds.add(e.getClientId()));
                persons.addAll(plist);
            }
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getGuaranteeInfo())) {
                persons.addAll(JSONUtil.toList(projEstablishBaseInfo.getGuaranteeInfo(), ProjEstablishPersonInfo.class));
            }
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getMortgagorInfo())) {
                persons.addAll(JSONUtil.toList(projEstablishBaseInfo.getMortgagorInfo(), ProjEstablishPersonInfo.class));
            }
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getPledgorInfo())) {
                persons.addAll(JSONUtil.toList(projEstablishBaseInfo.getPledgorInfo(), ProjEstablishPersonInfo.class));
            }
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getCreditorInfo())) {
                List<ProjEstablishPersonInfo> plist = JSONUtil.toList(projEstablishBaseInfo.getCreditorInfo(), ProjEstablishPersonInfo.class);
                plist.forEach(e -> theSameUserClientIds.add(e.getClientId()));
                persons.addAll(plist);
            }
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getDebtorInfo())) {
                persons.addAll(JSONUtil.toList(projEstablishBaseInfo.getDebtorInfo(), ProjEstablishPersonInfo.class));
            }
            Set<Long> clientIds = persons.stream().map(ProjEstablishPersonInfo::getClientId).collect(Collectors.toSet());
            this.copyClientMaterial(clientIds, projEstablishBaseInfo.getId(), BusinessModuleEnum.PROJ_ESTABLISH_CLIENT.name(), projEstablishBaseInfo.getProjSponsorUserId(), theSameUserClientIds);
        }
    }

    private void projReviewClient() {
        List<ProjReviewBaseInfo> projReviewBaseInfoList = SpringUtil.getBean(ProjReviewBaseInfoService.class).list();
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            // 先查询一下是否有数据，有的话就不处理了
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, projReviewBaseInfo.getId());
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW_CLIENT.name());
            int count = SpringUtil.getBean(MaterialsListService.class).count(query);
            if (count > 0) {
                log.info("项目评审【{}-{}】已存在拷贝后的客户文件，不执行客户文件拷贝操作", projReviewBaseInfo.getId(), projReviewBaseInfo.getProjName());
                continue;
            }
            Set<Long> theSameUserClientIds = new HashSet<>();
            List<ClientInfo> persons = new LinkedList<>();
            if (StrUtil.isNotBlank(projReviewBaseInfo.getLesseeInfo())) {
                List<ClientInfo> plist = JSONUtil.toList(projReviewBaseInfo.getLesseeInfo(), ClientInfo.class);
                plist.forEach(e -> theSameUserClientIds.add(e.getClientId()));
                persons.addAll(plist);
            }
            if (StrUtil.isNotBlank(projReviewBaseInfo.getGuaranteeInfo())) {
                persons.addAll(JSONUtil.toList(projReviewBaseInfo.getGuaranteeInfo(), ClientInfo.class));
            }
            if (StrUtil.isNotBlank(projReviewBaseInfo.getMortgagorInfo())) {
                persons.addAll(JSONUtil.toList(projReviewBaseInfo.getMortgagorInfo(), ClientInfo.class));
            }
            if (StrUtil.isNotBlank(projReviewBaseInfo.getPledgorInfo())) {
                persons.addAll(JSONUtil.toList(projReviewBaseInfo.getPledgorInfo(), ClientInfo.class));
            }
            if (StrUtil.isNotBlank(projReviewBaseInfo.getCreditorInfo())) {
                List<ClientInfo> plist = JSONUtil.toList(projReviewBaseInfo.getCreditorInfo(), ClientInfo.class);
                plist.forEach(e -> theSameUserClientIds.add(e.getClientId()));
                persons.addAll(plist);
            }
            if (StrUtil.isNotBlank(projReviewBaseInfo.getDebtorInfo())) {
                persons.addAll(JSONUtil.toList(projReviewBaseInfo.getDebtorInfo(), ClientInfo.class));
            }
            Set<Long> clientIds = persons.stream().map(ClientInfo::getClientId).collect(Collectors.toSet());
            this.copyClientMaterial(clientIds, projReviewBaseInfo.getId(), BusinessModuleEnum.PROJ_REVIEW_CLIENT.name(), projReviewBaseInfo.getProjSponsorUserId(), theSameUserClientIds);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void fixRatingClientAreaIndicator() {
        List<RatingClient> all = SpringUtil.getBean(RatingClientService.class).list();
        all.forEach(ratingClient -> {
            // 判断是否已有数据，有的话说明是新数据不用处理
            List<RatingClientAreaIndicator> existList = SpringUtil.getBean(RatingClientAreaIndicatorService.class).listByRatingClient(ratingClient.getId());
            if (CollectionUtil.isNotEmpty(existList)) {
                return;
            }
            try {
                // 初始化数据
                Long areaUniCode = SpringUtil.getBean(RatingClientService.class).getClientAreaUniCode(ratingClient.getClientId(), ratingClient.getModelName());
                SpringUtil.getBean(RatingClientAreaIndicatorService.class).create(ratingClient.getId(), areaUniCode, 2024);
            } catch (Exception e) {
                log.error("客户评级区域指标数据初始化异常[{}]", ratingClient.getClientName(), e);
            }
        });
    }

    @Data
    private static class FieldHolder {
        private String fieldName;
        private String fieldValue;
    }

    public void fixEvaluationSubjectData() throws Exception {
        Set<Long> projReviewIds = new HashSet<>();
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        String template1 = "update proj_review_base_info set evaluation_subject_id = %s, province = '%s', city = '%s', district = '%s' where id = %s;";
        String template2 = "update proj_establish_base_info set evaluation_subject_id = %s, province = '%s', city = '%s', district = '%s' where id = %s;";
        List<Map<String, Object>> dataList = ExcelUtil.getReader(FileUtil.getInputStream("/Users/dingqi/Downloads/存量项目清单(补充评估主体总表).xlsx")).read(0, 1, 398);
        for (Map<String, Object> data : dataList) {
            String contractCode = data.get("合同编号").toString();
            String evaluationSubjectName = data.get("评估主体").toString();
            // 找合同
            ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getOne(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, contractCode).ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name()));
            if (Objects.isNull(contractBaseInfo)) {
                log.error("没有找到{}的合同信息", contractCode);
                continue;
            }
            // 找评审
            ProjReviewBaseInfo projReviewBaseInfo = SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(contractBaseInfo.getProjReviewId());
            if (Objects.isNull(projReviewBaseInfo)) {
                log.error("没有找到{}的评审信息", contractCode);
                continue;
            }
            if (projReviewIds.contains(projReviewBaseInfo.getId())) {
                continue;
            }
            // 找客户
            Client client = SpringUtil.getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, evaluationSubjectName));
            if (Objects.isNull(client)) {
                log.error("没有找到{}的客户信息", evaluationSubjectName);
                continue;
            }
            // 找注册地址
            CorpAddressInfo corpAddressInfo = SpringUtil.getBean(CorpAddressInfoService.class).getOne(Wrappers.<CorpAddressInfo>lambdaQuery().eq(ClientBaseModel::getClientId, client.getId()).eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name()));
            if (Objects.isNull(corpAddressInfo)) {
                log.error("没有找到{}的注册地址", evaluationSubjectName);
                continue;
            }
            // 生成更新语句
            String sql1 = String.format(template1, client.getId(), corpAddressInfo.getProvince(), corpAddressInfo.getCity(), corpAddressInfo.getDistrict(), projReviewBaseInfo.getId());
            stringBuilder1.append(sql1).append("\n");
            if (Objects.nonNull(projReviewBaseInfo.getProjEstablishId())) {
                String sql2 = String.format(template2, client.getId(), corpAddressInfo.getProvince(), corpAddressInfo.getCity(), corpAddressInfo.getDistrict(), projReviewBaseInfo.getProjEstablishId());
                stringBuilder2.append(sql2).append("\n");
            }
            projReviewIds.add(projReviewBaseInfo.getId());
        }
        Writer writer1 = FileUtil.getWriter("/Users/dingqi/review.sql", StandardCharsets.UTF_8, false);
        writer1.write(stringBuilder1.toString());
        writer1.flush();
        Writer writer2 = FileUtil.getWriter("/Users/dingqi/establish.sql", StandardCharsets.UTF_8, false);
        writer2.write(stringBuilder2.toString());
        writer2.flush();
    }

    public void initFinancingActualRepayFromExcel() throws Exception {
        // 单个文件太大了，把sheet拆成了N个文件后处理
        ExcelReader excelReader = ExcelUtil.getReader("/Users/dingqi/Downloads/资金数据初始化/汇总明细表.xlsx");
        List<Map<String, Object>> huizongDetailList = excelReader.read(0, 1, 257);
        if (CollectionUtil.isEmpty(huizongDetailList)) {
            return;
        }
        List<String> updatePlanList = new LinkedList<>();
        List<String> updateDirectBaseInfoList = new LinkedList<>();
        List<String> updateCashFlowList = new LinkedList<>();
        List<String> insertFlowDetailList = new LinkedList<>();
        // 遍历处理
        for (Map<String, Object> dataMap : huizongDetailList) {
            String detailFileName = dataMap.get("银行（主键）").toString();
            String comprehensiveFinancingCost = dataMap.get("实际综合成本").toString();
            String financingCode = dataMap.get("融资编号").toString();
            if (StrUtil.isBlank(financingCode)) {
                log.error("存在必要数据缺失，跳过不处理[{}]", detailFileName);
                continue;
            }
            try {
                Long financingId = null;
                boolean isDirect = true;
                if (financingCode.startsWith("DK")) {
                    // 间融
                    FundFinancingBaseInfo fundFinancingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getOne(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getFinancingCode, financingCode));
                    if (Objects.isNull(fundFinancingBaseInfo)) {
                        log.error("没有找到融资编号为{}的融资数据[{}]", financingCode, detailFileName);
                        continue;
                    }
                    financingId = fundFinancingBaseInfo.getId();
                    isDirect = false;
                    FundFinancingPlan fundFinancingPlan = SpringUtil.getBean(FundFinancingPlanService.class).getOneByFinancingId(fundFinancingBaseInfo.getId());
                    if (Objects.isNull(fundFinancingPlan)) {
                        log.error("没有找到融资编号为{}的融资方案[{}]", financingCode, detailFileName);
                        continue;
                    }
                    if (StrUtil.isNotBlank(comprehensiveFinancingCost)) {
                        BigDecimal b = new BigDecimal(comprehensiveFinancingCost).multiply(BigDecimal.valueOf(1000000));
                        String sql = String.format("update `fund_financing_plan` set `comprehensive_interest_rate` = %s where id = %s;", b.longValue(), fundFinancingPlan.getId());
                        updatePlanList.add(sql);
                    }
                } else if (financingCode.startsWith("ZR")) {
                    // 直融
                    FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).getOne(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().eq(FundDirectFinancingBaseInfo::getFinancingCode, financingCode));
                    if (Objects.isNull(fundDirectFinancingBaseInfo)) {
                        log.error("没有找到融资编号为{}的融资数据[{}]", financingCode, detailFileName);
                        continue;
                    }
                    financingId = fundDirectFinancingBaseInfo.getId();
                    isDirect = true;
                    if (StrUtil.isNotBlank(comprehensiveFinancingCost)) {
                        BigDecimal b = new BigDecimal(comprehensiveFinancingCost).multiply(BigDecimal.valueOf(1000000));
                        String sql = String.format("update `fund_direct_financing_base_info` set `comprehensive_financing_cost` = %s where id = %s;", b.longValue(), fundDirectFinancingBaseInfo.getId());
                        updateDirectBaseInfoList.add(sql);
                    }
                } else {
                    log.error("不符合规则的融资编号[financingCode:{}, fileName:{}]", financingCode, detailFileName);
                    continue;
                }
                // 取对应还款明细Excel
                ExcelReader reader = ExcelUtil.getReader("/Users/dingqi/Downloads/资金数据初始化/" + detailFileName + ".xlsx");
                List<List<Object>> rows = reader.read(3);
                if (CollectionUtil.isEmpty(rows)) {
                    continue;
                }
                if (Objects.isNull(financingId)) {
                    log.error("无法确定融资id，不做还本付息数据的处理[{}]", detailFileName);
                    continue;
                }
                // 查询还本付息主表数据
                LambdaQueryWrapper<FundReceiptRepayBaseInfo> query = Wrappers.lambdaQuery();
                query.eq(FundReceiptRepayBaseInfo::getFinancingId, financingId);
                if (isDirect) {
                    query.eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT");
                } else {
                    query.isNull(FundReceiptRepayBaseInfo::getFinancingType);
                }
                FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).getOne(query);
                if (Objects.isNull(fundReceiptRepayBaseInfo)) {
                    log.error("没有找到对应的还本付息主表数据[financingCode:{}, financingId:{}, isDirect:{}, fileName:{}]", financingCode, financingId, isDirect, detailFileName);
                    continue;
                }
                // 查询还本付息现金流
                Optional<List<FundReceiptRepayCashFlow>> optional = SpringUtil.getBean(FundReceiptRepayCashFlowService.class).listByReceiptRepayId(fundReceiptRepayBaseInfo.getId(), null);
                if (!optional.isPresent()) {
                    log.error("没有找到还本付息现金流数据[receiptRepayId:{}, fileName:{}]", fundReceiptRepayBaseInfo.getId(), detailFileName);
                    continue;
                }
                Map<Integer, FundReceiptRepayCashFlow> cashFlowMap = optional.get().stream().collect(Collectors.toMap(FundReceiptRepayCashFlow::getPhase, e -> e));
                for (int i = 0; i < rows.size(); i++) {
                    try {
                        List<Object> rowData = rows.get(i);
                        String actualRepayDateStr = Optional.ofNullable(rowData.get(23)).map(Object::toString).orElse("");
                        String actualRepayPrincipalStr = Optional.ofNullable(rowData.get(24)).map(Object::toString).orElse("0");
                        String actualRepayInterestStr = Optional.ofNullable(rowData.get(25)).map(Object::toString).orElse("0");
                        if (StrUtil.isBlank(actualRepayDateStr) || Objects.equals("小计", actualRepayDateStr)) {
                            break;
                        }
                        if (StrUtil.isBlank(actualRepayInterestStr) && StrUtil.isBlank(actualRepayPrincipalStr)) {
                            log.error("存在日期不为空但是本金和利息全为空的数据[{}]", detailFileName);
                            continue;
                        }
                        LocalDate actualRepayDate = null;
                        if (actualRepayDateStr.contains("/")) {
                            actualRepayDate = LocalDateTimeUtil.parseDate(actualRepayDateStr.substring(0, Math.min(actualRepayDateStr.length(), 10)), "yyyy/M/d");
                        }
                        if (actualRepayDateStr.contains("-")) {
                            actualRepayDate = LocalDateTimeUtil.parseDate(actualRepayDateStr.substring(0, 10), "yyyy-MM-dd");
                        }

                        if (actualRepayDate.isAfter(LocalDate.of(2024, 9, 30))) {
                            break;
                        }
                        long actualRepayPrincipal = Util.mithrasLongDecimalTwo(new BigDecimal(StrUtil.isBlank(actualRepayPrincipalStr.trim()) ? "0" : actualRepayPrincipalStr).multiply(BigDecimal.valueOf(10000)).longValue());
                        long actualRepayInterest = Util.mithrasLongDecimalTwo(new BigDecimal(StrUtil.isBlank(actualRepayInterestStr.trim()) ? "0" : actualRepayInterestStr).multiply(BigDecimal.valueOf(10000)).longValue());
                        // 找现金流
                        int phase = i + 1;
                        FundReceiptRepayCashFlow cashFlow = cashFlowMap.get(phase);
                        if (Objects.isNull(cashFlow)) {
                            log.error("没有找到对应现金流[fileName:{}, financingCode:{}, phase:{}]", detailFileName, financingCode, phase);
                            continue;
                        }
                        // 查询一下是否有实际核销记录
                        FundReceiptFlowDetail fundReceiptFlowDetail = SpringUtil.getBean(FundReceiptFlowDetailService.class).getByCode(cashFlow.getCashFlowCode());
                        if (Objects.nonNull(fundReceiptFlowDetail)) {
                            continue;
                        }
                        // 比对金额确定核销状态
                        String cashFlowState;
                        long totalAmount = actualRepayPrincipal + actualRepayInterest;
                        if (totalAmount > cashFlow.getRepayAmount()) {
                            cashFlowState = CashFlowState.BEYOND_WRITTEN_OFF.name();
                        } else if (totalAmount == cashFlow.getRepayAmount()) {
                            cashFlowState = CashFlowState.WRITTEN_OFF.name();
                        } else {
                            cashFlowState = CashFlowState.PART_WRITE_OFF.name();
                        }
                        // 更新状态
                        String sql1 = String.format("update `fund_receipt_repay_cash_flow` set `write_off_state` = '%s' where id = %s;", cashFlowState, cashFlow.getId());
                        updateCashFlowList.add(sql1);
                        // 需要生成实际核销记录
                        String sql2 = String.format("insert into `fund_receipt_flow_detail` (`data_source`, `receipt_repay_id`, `settle_method`, `cash_flow_code`, `cash_flow_item`, `cash_flow_date`, `total_amount`, `principal_amount`, `interest_amount`) value ('台账导入', %s, '网银', '%s', 'REPAY', '%s', %s, %s, %s);", fundReceiptRepayBaseInfo.getId(), cashFlow.getCashFlowCode(), LocalDateTimeUtil.format(actualRepayDate, DatePattern.NORM_DATE_PATTERN), actualRepayPrincipal + actualRepayInterest, actualRepayPrincipal, actualRepayInterest);
                        insertFlowDetailList.add(sql2);
                    } catch (Exception e) {
                        log.error("处理单行数据明细发生异常[fileName:{}, i:{}]", detailFileName, i, e);
                    }
                }
            } catch (Exception e) {
                log.error("处理异常[fileName:{}]", detailFileName, e);
            }
        }
        Writer writer1 = FileUtil.getWriter("/Users/dingqi/更新间融实际综合成本.sql", StandardCharsets.UTF_8, false);
        Writer writer2 = FileUtil.getWriter("/Users/dingqi/更新直融实际综合成本.sql", StandardCharsets.UTF_8, false);
        Writer writer3 = FileUtil.getWriter("/Users/dingqi/更新还本付息核销状态.sql", StandardCharsets.UTF_8, false);
        Writer writer4 = FileUtil.getWriter("/Users/dingqi/新增还本付息核销明细.sql", StandardCharsets.UTF_8, false);
        writer1.write(StrUtil.join("\n", updatePlanList));
        writer1.flush();
        writer2.write(StrUtil.join("\n", updateDirectBaseInfoList));
        writer2.flush();
        writer3.write(StrUtil.join("\n", updateCashFlowList));
        writer3.flush();
        writer4.write(StrUtil.join("\n", insertFlowDetailList));
        writer4.flush();
    }

    @Transactional
    public void initClientAuthorityStep1() {
        LambdaQueryWrapper<Client> clientQuery = Wrappers.lambdaQuery();
        clientQuery.eq(Client::getClientType, ClientType.CORPORATION.name());
        clientQuery.eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name());
        clientQuery.isNull(Client::getBelongSponsorId);
        List<Client> clientList = clientMapper.selectList(clientQuery);
        if (CollectionUtil.isEmpty(clientList)) {
            return;
        }
        /*
            从合同开始判断，若存在合同（不区分状态）则管护权给合同主办；若存在多个合同不同主办，则管护权依次分配给合同「起租—生效—新建—结清—作废」的合同主办。
            再判断项目评审，若存在项目评审（不区分状态）则管护权给项目评审主办；若存在多个项目评审不同主办，则管护权依次分配给评审「生效—新建—关闭」的项目评审主办。
            后判断项目立项，若存在项目立项（不区分状态）则管护权给项目立项主办；若存在多个项目立项不同主办，则管护权依次分配给立项「生效—新建—关闭」的项目立项主办。
            若无立项则将客户管护权分配给客户创建人
         */
        for (Client client : clientList) {
            Long clientId = client.getId();
            if (Objects.nonNull(client.getBelongSponsorId())) {
                log.info("{}存在归属主办，跳过不处理", client.getClientName());
                continue;
            }
            CorpCommerceInfo corpCommerceInfo = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(clientId).get(0);
            if (Objects.equals(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name())) {
                log.info("{}作为公海客户，跳过不处理", client.getClientName());
                continue;
            }
            Long userId = this.findUserIdByContract(clientId);
            if (Objects.isNull(userId)) {
                userId = this.findUserIdByProjReview(clientId);
                if (Objects.isNull(userId)) {
                    userId = this.findUserIdByProjEstablish(clientId);
                    if (Objects.isNull(userId)) {
                        userId = client.getCreateBy();
                    }
                }
            }
            if (Objects.isNull(userId)) {
                log.error("无法确定用户ID[clientName:{}]", client.getClientName());
            }
            Long bizDeptId = Optional.ofNullable(SpringUtil.getBean(SysUserService.class).getBizDeptByUserId(userId)).map(OrgDO::getId).orElse(7L);
            client.setBelongSponsorId(userId);
            client.setBelongDeptId(bizDeptId);
            clientMapper.updateById(client);
        }
    }

    private Long findUserIdByProjEstablish(Long clientId) {
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = SpringUtil.getBean(ProjEstablishBaseInfoService.class).listByClients(Collections.singletonList(clientId));
        Map<String, List<ProjEstablishBaseInfo>> projEstablishMap = projEstablishBaseInfoList.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getProjEstablishStatus));
        if (CollectionUtil.isNotEmpty(projEstablishBaseInfoList)) {
            // 懒的增加排序权重，直接代码逻辑强行有序
            if (CollectionUtil.isNotEmpty(projEstablishMap.get(RecordStatus.TAKE_EFFECT.name()))) {
                List<ProjEstablishBaseInfo> prbiList = projEstablishMap.get(RecordStatus.TAKE_EFFECT.name());
                prbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return prbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(projEstablishMap.get(RecordStatus.NEW.name()))) {
                List<ProjEstablishBaseInfo> prbiList = projEstablishMap.get(RecordStatus.NEW.name());
                prbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return prbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(projEstablishMap.get(RecordStatus.CLOSED.name()))) {
                List<ProjEstablishBaseInfo> prbiList = projEstablishMap.get(RecordStatus.CLOSED.name());
                prbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return prbiList.get(0).getProjSponsorUserId();
            }
        }
        return null;
    }

    private Long findUserIdByProjReview(Long clientId) {
        List<ProjReviewBaseInfo> projReviewBaseInfoList = SpringUtil.getBean(ProjReviewBaseInfoService.class).listByClients(Collections.singletonList(clientId));
        Map<String, List<ProjReviewBaseInfo>> projReviewMap = projReviewBaseInfoList.stream().collect(Collectors.groupingBy(ProjReviewBaseInfo::getProjReviewStatus));
        if (CollectionUtil.isNotEmpty(projReviewBaseInfoList)) {
            // 懒的增加排序权重，直接代码逻辑强行有序
            if (CollectionUtil.isNotEmpty(projReviewMap.get(RecordStatus.TAKE_EFFECT.name()))) {
                List<ProjReviewBaseInfo> prbiList = projReviewMap.get(RecordStatus.TAKE_EFFECT.name());
                prbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return prbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(projReviewMap.get(RecordStatus.NEW.name()))) {
                List<ProjReviewBaseInfo> prbiList = projReviewMap.get(RecordStatus.NEW.name());
                prbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return prbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(projReviewMap.get(RecordStatus.CLOSED.name()))) {
                List<ProjReviewBaseInfo> prbiList = projReviewMap.get(RecordStatus.CLOSED.name());
                prbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return prbiList.get(0).getProjSponsorUserId();
            }
        }
        return null;
    }

    private Long findUserIdByContract(Long clientId) {
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByClients(Collections.singletonList(clientId));
        Map<String, List<ContractBaseInfo>> contractMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getContractStatus));
        if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
            // 懒的增加排序权重，直接代码逻辑强行有序
            if (CollectionUtil.isNotEmpty(contractMap.get(ContractStatus.START_RENT.name()))) {
                List<ContractBaseInfo> cbiList = contractMap.get(ContractStatus.START_RENT.name());
                cbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return cbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(contractMap.get(ContractStatus.TAKE_EFFECT.name()))) {
                List<ContractBaseInfo> cbiList = contractMap.get(ContractStatus.TAKE_EFFECT.name());
                cbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return cbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(contractMap.get(ContractStatus.NEW.name()))) {
                List<ContractBaseInfo> cbiList = contractMap.get(ContractStatus.NEW.name());
                cbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return cbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(contractMap.get(ContractStatus.SETTLE.name()))) {
                List<ContractBaseInfo> cbiList = contractMap.get(ContractStatus.SETTLE.name());
                cbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return cbiList.get(0).getProjSponsorUserId();
            }
            if (CollectionUtil.isNotEmpty(contractMap.get(ContractStatus.INVALID.name()))) {
                List<ContractBaseInfo> cbiList = contractMap.get(ContractStatus.INVALID.name());
                cbiList.sort(Comparator.comparing(BaseModel::getCreateTime).reversed());
                return cbiList.get(0).getProjSponsorUserId();
            }
        }
        return null;
    }

    public void initClientAuthorityStep2() {
        // 未经处理的原始权限数据
        List<ClientAuthority> toSaveList = new LinkedList<>();
        // 根据客户管理初始化
        this.initByClient(toSaveList);
        // 根据项目立项初始化
        this.initByProjEstablish(toSaveList);
        // 根据项目评审初始化
        this.initByProjReview(toSaveList);
        // 根据合同管理初始化
        this.initByContract(toSaveList);
        // 整理权限数据
        if (CollectionUtil.isEmpty(toSaveList)) {
            return;
        }
        toSaveList.removeIf(e -> {
            Client client = SpringUtil.getBean(ClientService.class).getById(e.getClientId());
            if (Objects.isNull(client)) {
                log.warn("没有找到对应客户数据[{}]", e.getClientId());
                return true;
            }
            // 踢掉自然人
            if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
                return true;
            }
            // 公海不需要权限
            List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(client.getId());
            if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
                CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
                return Objects.equals(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name());
            }
            return false;
        });
        if (CollectionUtil.isEmpty(toSaveList)) {
            return;
        }
        // 保存数据
        SpringUtil.getBean(ClientAuthorityService.class).saveBatch(toSaveList);
    }

    public void initClientAuthorityStep3() throws Exception {
        LambdaQueryWrapper<Client> query = Wrappers.lambdaQuery();
        query.eq(Client::getClientType, ClientType.CORPORATION.name());
        List<Client> clientList = clientMapper.selectList(query);
        if (CollectionUtil.isEmpty(clientList)) {
            return;
        }
        StringBuilder errorStringBuilder = new StringBuilder();
        for (Client client : clientList) {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    // 初始化数据理论上都会从老表编辑区拷贝一份数据，需要确定的是这份数据给哪个人
                    // 公海客户给创建人，非公海客户中生效的给管护权人（需先执行初始化权限的脚本），新建的给创建人
                    List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(client.getId());
                    CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.size() > 0 ? corpCommerceInfoList.get(0) : null;
                    Long userId;
                    Long bizDeptId;
                    if (Objects.nonNull(corpCommerceInfo) && Objects.equals(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name())) {
                        userId = client.getCreateBy();
                        bizDeptId = Optional.ofNullable(SpringUtil.getBean(SysUserService.class).getBizDeptByUserId(userId)).map(OrgDO::getId).orElse(7L);
                    } else {
                        if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                            // 找管护权
                            ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(client.getId());
                            if (Objects.isNull(clientAuthority)) {
                                throw new MithrasException("管护权不存在");
                            }
                            userId = clientAuthority.getUserId();
                            bizDeptId = Optional.ofNullable(SpringUtil.getBean(SysUserService.class).getBizDeptByUserId(userId)).map(OrgDO::getId).orElse(7L);
                        } else {
                            userId = client.getCreateBy();
                            bizDeptId = Optional.ofNullable(SpringUtil.getBean(SysUserService.class).getBizDeptByUserId(userId)).map(OrgDO::getId).orElse(7L);
                        }
                    }
                    // 生成创建记录
                    ClientCreateRecord clientCreateRecord = new ClientCreateRecord();
                    clientCreateRecord.setClientId(client.getId());
                    clientCreateRecord.setUserId(userId);
                    clientCreateRecord.setDeptId(bizDeptId);
                    clientCreateRecord.setUniqueCode(LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN));
                    SpringUtil.getBean(ClientCreateRecordService.class).save(clientCreateRecord);
                    // 生成关联记录
                    ClientUserRef clientUserRef = new ClientUserRef();
                    clientUserRef.setClientId(client.getId());
                    clientUserRef.setUserId(userId);
                    clientUserRef.setDeptId(bizDeptId);
                    clientUserRef.setLastOperateType(ClientUserRef.OperateTypeEnum.WRITE.name());
                    clientUserRef.setLastOperateTime(LocalDateTime.now());
                    SpringUtil.getBean(ClientUserRefService.class).save(clientUserRef);
                } catch (Exception e) {
                    log.error("初始化客户数据发生异常[clientName:{}]", client.getClientName(), e);
                    errorStringBuilder.append(client.getClientName()).append(", ").append(e.getMessage()).append("\n");
                    transactionStatus.setRollbackOnly();
                }
            });
        }
        OutputStream errorOs = FileUtil.getOutputStream("/Users/mockorz/error-" + System.currentTimeMillis() + ".log");
        errorOs.write(errorStringBuilder.toString().getBytes());
        errorOs.flush();
    }

    private void initByClient(List<ClientAuthority> clientAuthorityList) {
        List<Client> clientList = SpringUtil.getBean(ClientService.class).list();
        for (Client client : clientList) {
            if (Objects.equals(client.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                // 生效客户如果有所属部门及主办则赋予管护权
                if (Objects.nonNull(client.getBelongSponsorId()) && Objects.nonNull(client.getBelongDeptId())) {
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setDeptId(client.getBelongDeptId());
                    clientAuthority.setUserId(client.getBelongSponsorId());
                    clientAuthority.setClientId(client.getId());
                    clientAuthority.setLevel(ClientLevelEnum.MANAGE.getLevel());
                    clientAuthority.setSourceId(client.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_CLIENT");
                    clientAuthorityList.add(clientAuthority);
                }
            }
        }
    }

    private void initByProjEstablish(List<ClientAuthority> clientAuthorityList) {
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = SpringUtil.getBean(ProjEstablishBaseInfoService.class).list();
        for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
            // 协办
            List<Long> projCosponsorUserIds = Optional.ofNullable(projEstablishBaseInfo.getProjCosponsorUserIds()).map(e -> JSONUtil.toList(e, Long.class)).orElse(null);
            // 处理担保人
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getGuaranteeInfo())) {
                List<ClientInfo> clientInfoList = JSONUtil.toList(projEstablishBaseInfo.getGuaranteeInfo(), ClientInfo.class);
                clientInfoList.forEach(e -> {
                    if (!Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        return;
                    }
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getClientId());
                    clientAuthority.setUserId(projEstablishBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(projEstablishBaseInfo.getBizDeptId());
                    clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                    clientAuthority.setSourceId(projEstablishBaseInfo.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_ESTABLISH");
                    clientAuthorityList.add(clientAuthority);
                    // 协办查看权
                    if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                        for (Long userId : projCosponsorUserIds) {
                            clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(e.getClientId());
                            clientAuthority.setUserId(userId);
                            clientAuthority.setDeptId(projEstablishBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                            clientAuthority.setSourceId(projEstablishBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_ESTABLISH");
                            clientAuthorityList.add(clientAuthority);
                        }
                    }
                });
            }
            // 处理抵押人
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getMortgagorInfo())) {
                List<ClientInfo> clientInfoList = JSONUtil.toList(projEstablishBaseInfo.getMortgagorInfo(), ClientInfo.class);
                clientInfoList.forEach(e -> {
                    if (!Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        return;
                    }
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getClientId());
                    clientAuthority.setUserId(projEstablishBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(projEstablishBaseInfo.getBizDeptId());
                    clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                    clientAuthority.setSourceId(projEstablishBaseInfo.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_ESTABLISH");
                    clientAuthorityList.add(clientAuthority);
                    // 协办查看权
                    if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                        for (Long userId : projCosponsorUserIds) {
                            clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(e.getClientId());
                            clientAuthority.setUserId(userId);
                            clientAuthority.setDeptId(projEstablishBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                            clientAuthority.setSourceId(projEstablishBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_ESTABLISH");
                            clientAuthorityList.add(clientAuthority);
                        }
                    }
                });
            }
            // 处理质押人
            if (StrUtil.isNotBlank(projEstablishBaseInfo.getPledgorInfo())) {
                List<ClientInfo> clientInfoList = JSONUtil.toList(projEstablishBaseInfo.getPledgorInfo(), ClientInfo.class);
                clientInfoList.forEach(e -> {
                    if (!Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        return;
                    }
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getClientId());
                    clientAuthority.setUserId(projEstablishBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(projEstablishBaseInfo.getBizDeptId());
                    clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                    clientAuthority.setSourceId(projEstablishBaseInfo.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_ESTABLISH");
                    clientAuthorityList.add(clientAuthority);
                    // 协办查看权
                    if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                        for (Long userId : projCosponsorUserIds) {
                            clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(e.getClientId());
                            clientAuthority.setUserId(userId);
                            clientAuthority.setDeptId(projEstablishBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                            clientAuthority.setSourceId(projEstablishBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_ESTABLISH");
                            clientAuthorityList.add(clientAuthority);
                        }
                    }
                });
            }
        }
    }

    private void initByProjReview(List<ClientAuthority> clientAuthorityList) {
        List<ProjReviewBaseInfo> projReviewBaseInfoList = SpringUtil.getBean(ProjReviewBaseInfoService.class).list();
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            if (Objects.equals(projReviewBaseInfo.getProjReviewStatus(), RecordStatus.CLOSED.name()) || Objects.equals(projReviewBaseInfo.getProjReviewStatus(), RecordStatus.EXPIRE.name())) {
                // 跳过关闭的项目评审
                continue;
            }
            // 协办
            List<Long> projCosponsorUserIds = Optional.ofNullable(projReviewBaseInfo.getProjCosponsorUserIds()).map(e -> JSONUtil.toList(e, Long.class)).orElse(null);
            // 处理担保人
            if (StrUtil.isNotBlank(projReviewBaseInfo.getGuaranteeInfo())) {
                List<ClientInfo> clientInfoList = JSONUtil.toList(projReviewBaseInfo.getGuaranteeInfo(), ClientInfo.class);
                clientInfoList.forEach(e -> {
                    if (!Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        return;
                    }
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getClientId());
                    clientAuthority.setUserId(projReviewBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(projReviewBaseInfo.getBizDeptId());
                    clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                    clientAuthority.setSourceId(projReviewBaseInfo.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_REVIEW");
                    clientAuthorityList.add(clientAuthority);
                    // 协办查看权
                    if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                        for (Long userId : projCosponsorUserIds) {
                            clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(e.getClientId());
                            clientAuthority.setUserId(userId);
                            clientAuthority.setDeptId(projReviewBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                            clientAuthority.setSourceId(projReviewBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_REVIEW");
                            clientAuthorityList.add(clientAuthority);
                        }
                    }
                });
            }
            // 处理抵押人
            if (StrUtil.isNotBlank(projReviewBaseInfo.getMortgagorInfo())) {
                List<ClientInfo> clientInfoList = JSONUtil.toList(projReviewBaseInfo.getMortgagorInfo(), ClientInfo.class);
                clientInfoList.forEach(e -> {
                    if (!Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        return;
                    }
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getClientId());
                    clientAuthority.setUserId(projReviewBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(projReviewBaseInfo.getBizDeptId());
                    clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                    clientAuthority.setSourceId(projReviewBaseInfo.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_REVIEW");
                    clientAuthorityList.add(clientAuthority);
                    // 协办查看权
                    if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                        for (Long userId : projCosponsorUserIds) {
                            clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(e.getClientId());
                            clientAuthority.setUserId(userId);
                            clientAuthority.setDeptId(projReviewBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                            clientAuthority.setSourceId(projReviewBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_REVIEW");
                            clientAuthorityList.add(clientAuthority);
                        }
                    }
                });
            }
            // 处理质押人
            if (StrUtil.isNotBlank(projReviewBaseInfo.getPledgorInfo())) {
                List<ClientInfo> clientInfoList = JSONUtil.toList(projReviewBaseInfo.getPledgorInfo(), ClientInfo.class);
                clientInfoList.forEach(e -> {
                    if (!Objects.equals(e.getClientType(), ClientType.CORPORATION.name())) {
                        return;
                    }
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getClientId());
                    clientAuthority.setUserId(projReviewBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(projReviewBaseInfo.getBizDeptId());
                    clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                    clientAuthority.setSourceId(projReviewBaseInfo.getId().toString());
                    clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_REVIEW");
                    clientAuthorityList.add(clientAuthority);
                    // 协办查看权
                    if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                        for (Long userId : projCosponsorUserIds) {
                            clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(e.getClientId());
                            clientAuthority.setUserId(userId);
                            clientAuthority.setDeptId(projReviewBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                            clientAuthority.setSourceId(projReviewBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_PROJ_REVIEW");
                            clientAuthorityList.add(clientAuthority);
                        }
                    }
                });
            }
        }
    }

    private void initByContract(List<ClientAuthority> clientAuthorityList) {
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).list();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            if (CharSequenceUtil.equalsAny(contractBaseInfo.getContractStatus(), ContractStatus.INVALID.name(), ContractStatus.CLOSED.name())) {
                // 不处理作废或者关闭的合同
                continue;
            }
            // 协办
            List<Long> projCosponsorUserIds = Optional.ofNullable(contractBaseInfo.getProjCosponsorUserIds()).map(e -> JSONUtil.toList(e, Long.class)).orElse(null);
            // 处理承租人/债权人/债务人
            List<ContractTenantry> contractTenantryList = SpringUtil.getBean(ContractTenantryService.class).listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractTenantryList)) {
                contractTenantryList.forEach(e -> {
                    ClientAuthority clientAuthority = new ClientAuthority();
                    clientAuthority.setClientId(e.getLesseeId());
                    clientAuthority.setUserId(contractBaseInfo.getProjSponsorUserId());
                    clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                    if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name())) {
                        // 结清合同保留查看权
                        clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                        clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                        clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                        clientAuthorityList.add(clientAuthority);
                    }
                });
            }
            // 处理担保人
            List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
                contractGuarantorList.forEach(e -> {
                    if (!Objects.equals(e.getGuarantorType(), ClientType.CORPORATION.name())) {
                        // 非法人客户不处理
                        return;
                    }
                    if (StrUtil.isNotBlank(e.getGuarantorIds())) {
                        List<Long> ids = JSONUtil.toList(e.getGuarantorIds(), Long.class);
                        for (Long id : ids) {
                            ClientAuthority clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(id);
                            clientAuthority.setUserId(contractBaseInfo.getProjSponsorUserId());
                            clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                            clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                            clientAuthorityList.add(clientAuthority);
                            // 协办查看权
                            if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                                for (Long userId : projCosponsorUserIds) {
                                    clientAuthority = new ClientAuthority();
                                    clientAuthority.setClientId(id);
                                    clientAuthority.setUserId(userId);
                                    clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                                    clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                                    clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                                    clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                                    clientAuthorityList.add(clientAuthority);
                                }
                            }
                        }
                    }
                });
            }
            // 处理抵押人
            List<ContractMortgage> contractMortgageList = SpringUtil.getBean(ContractMortgageService.class).listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractMortgageList)) {
                contractMortgageList.forEach(e -> {
                    if (!Objects.equals(e.getMortgageType(), ClientType.CORPORATION.name())) {
                        // 非法人客户不处理
                        return;
                    }
                    if (StrUtil.isNotBlank(e.getMortgageIds())) {
                        List<Long> ids = JSONUtil.toList(e.getMortgageIds(), Long.class);
                        for (Long id : ids) {
                            ClientAuthority clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(id);
                            clientAuthority.setUserId(contractBaseInfo.getProjSponsorUserId());
                            clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                            clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                            clientAuthorityList.add(clientAuthority);
                            // 协办查看权
                            if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                                for (Long userId : projCosponsorUserIds) {
                                    clientAuthority = new ClientAuthority();
                                    clientAuthority.setClientId(id);
                                    clientAuthority.setUserId(userId);
                                    clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                                    clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                                    clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                                    clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                                    clientAuthorityList.add(clientAuthority);
                                }
                            }
                        }
                    }
                });
            }
            // 处理质押人
            List<ContractPledge> contractPledgeList = SpringUtil.getBean(ContractPledgeService.class).listByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(contractPledgeList)) {
                contractPledgeList.forEach(e -> {
                    if (!Objects.equals(e.getPledgeType(), ClientType.CORPORATION.name())) {
                        // 非法人客户不处理
                        return;
                    }
                    if (StrUtil.isNotBlank(e.getPledgeIds())) {
                        List<Long> ids = JSONUtil.toList(e.getPledgeIds(), Long.class);
                        for (Long id : ids) {
                            ClientAuthority clientAuthority = new ClientAuthority();
                            clientAuthority.setClientId(id);
                            clientAuthority.setUserId(contractBaseInfo.getProjSponsorUserId());
                            clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                            clientAuthority.setLevel(ClientLevelEnum.APPLY.getLevel());
                            clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                            clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                            clientAuthorityList.add(clientAuthority);
                            // 协办查看权
                            if (CollectionUtil.isNotEmpty(projCosponsorUserIds)) {
                                for (Long userId : projCosponsorUserIds) {
                                    clientAuthority = new ClientAuthority();
                                    clientAuthority.setClientId(id);
                                    clientAuthority.setUserId(userId);
                                    clientAuthority.setDeptId(contractBaseInfo.getBizDeptId());
                                    clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                                    clientAuthority.setSourceId(contractBaseInfo.getId().toString());
                                    clientAuthority.setSourceBusinessType("INIT_FROM_CONTRACT");
                                    clientAuthorityList.add(clientAuthority);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ClientRiskControlExcelModel extends ExcelModel {
        private Long clientId;
        private String clientName;
        private String riskCode;
        private String riskDesc;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importHistoryPolicy() {
        List<Map<String, Object>> rows = ExcelUtil.getReader(FileUtil.getInputStream("/Users/mockorz/Downloads/存量保单统计清单12.8.xlsx")).readAll();
        if (CollectionUtil.isEmpty(rows)) {
            return;
        }
        List<PolicyInfo> toSaveList = new LinkedList<>();
        for (Map<String, Object> rowMap : rows) {
            // 取表格数据
            String level = Optional.ofNullable(rowMap.get("层级")).map(Object::toString).orElse(null);
            String contractCode = Optional.ofNullable(rowMap.get("合同编号")).map(Object::toString).orElse(null);
            if (StrUtil.isNotBlank(contractCode)) {
                contractCode = contractCode.replace("（", "(").replace("）", ")");
            }
            String policyCode = Optional.ofNullable(rowMap.get("*保单编号")).map(Object::toString).orElse(null);
            String policyOrg = Optional.ofNullable(rowMap.get("*保险机构")).map(Object::toString).orElse(null);
            String policyType = Optional.ofNullable(rowMap.get("*险种")).map(Object::toString).orElse(null);
            String policyAmount = Optional.ofNullable(rowMap.get("*保单金额")).map(Object::toString).orElse(null);
            String policyStartDate = Optional.ofNullable(rowMap.get("*保险起始日")).map(Object::toString).orElse(null);
            String policyEndDate = Optional.ofNullable(rowMap.get("*保险到期日")).map(Object::toString).orElse(null);
            String policyRenew = Optional.ofNullable(rowMap.get("*是否续保")).map(Object::toString).orElse(null);
            String remark = Optional.ofNullable(rowMap.get("备注")).map(Object::toString).orElse(null);
            // 判断处理条件
            if (StrUtil.isNotBlank(level) && Objects.equals(level, "1")) {
                // 如果是续保保单，人工处理，需要维护父子关系
                continue;
            }
            if (StrUtil.isBlank(contractCode)) {
                continue;
            }
            // 查询合同信息
            LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
            contractQuery.eq(ContractBaseInfo::getContractCode, contractCode);
            contractQuery.ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name());
            ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getOne(contractQuery);
            if (Objects.isNull(contractBaseInfo)) {
                log.warn("合同数据不存在[{}]", contractCode);
                continue;
            }
            // 封装数据
            PolicyInfo policyInfo = new PolicyInfo();
            policyInfo.setProjId(contractBaseInfo.getProjReviewId());
            policyInfo.setContractId(contractBaseInfo.getId());
            policyInfo.setContractCode(contractBaseInfo.getContractCode());
            policyInfo.setPolicyStatus(PolicyStatusEnum.EFFECT.name());
            policyInfo.setApprovalStatus(PolicyApprovalStatusEnum.NEW_APPROVAL_PASS.name());
            policyInfo.setLevel(0);
            if (StrUtil.isNotBlank(policyCode)) {
                policyInfo.setPolicyCode(policyCode);
            }
            if (StrUtil.isNotBlank(policyAmount)) {
                policyInfo.setPolicyAmount(new BigDecimal(policyAmount).multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (StrUtil.isNotBlank(policyStartDate)) {
                policyInfo.setInsuranceStartDate(LocalDateTimeUtil.parse(policyStartDate, DatePattern.NORM_DATETIME_PATTERN).toLocalDate());
            }
            if (StrUtil.isNotBlank(policyEndDate)) {
                policyInfo.setInsuranceEndDate(LocalDateTimeUtil.parse(policyEndDate, DatePattern.NORM_DATETIME_PATTERN).toLocalDate());
            }
            if (StrUtil.isNotBlank(policyOrg)) {
                policyInfo.setInsuranceCompany(policyOrg);
            }
            if (StrUtil.isNotBlank(policyType)) {
                if (Objects.equals("财产一切险", policyType)) {
                    policyInfo.setPolicyType(PolicyTypeEnum.PROPERTY_ALL_RISKS_INSURANCE.name());
                } else if (Objects.equals("财产综合险", policyType)) {
                    policyInfo.setPolicyType(PolicyTypeEnum.PROPERTY_INSURANCE.name());
                } else if (Objects.equals("工程机械设备综合险", policyType)) {
                    policyInfo.setPolicyType(PolicyTypeEnum.MACHINERY_LOSS_INSURANCE.name());
                } else {
                    policyInfo.setPolicyType(PolicyTypeEnum.OTHER.name());
                }
            }
            if (StrUtil.isNotBlank(policyRenew)) {
                if (Objects.equals("到期续保", policyRenew)) {
                    policyInfo.setRenewInsuranceFlag(PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name());
                    policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.NO.getCode());
                }
                if (Objects.equals("无需续保", policyRenew)) {
                    policyInfo.setRenewInsuranceFlag(PolicyRenewInsuranceEnum.NO_NEED_TO_RENEW.name());
                    policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.YES.getCode());
                }
            }
            if (StrUtil.isNotBlank(remark)) {
                policyInfo.setRemark(remark);
            }
            toSaveList.add(policyInfo);
        }
        if (CollectionUtil.isNotEmpty(toSaveList)) {
            SpringUtil.getBean(PolicyInfoService.class).saveBatch(toSaveList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void contractGuarantorDataFix(Long contractId) {
        // 编辑区数据订正
        List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractId(contractId);
        if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
            // 根据担保合同编号进行分组
            Map<String, List<ContractGuarantor>> contractGuarantorMap = contractGuarantorList.stream().filter(e -> StrUtil.isNotBlank(e.getGuarantorContractCode())).collect(Collectors.groupingBy(ContractGuarantor::getGuarantorContractCode));
            if (CollectionUtil.isNotEmpty(contractGuarantorMap)) {
                List<ContractGuarantor> updateList = new LinkedList<>();
                // 根据同一个担保合同编号下的数据数量来决定是单人保证还是联保
                for (Map.Entry<String, List<ContractGuarantor>> entry : contractGuarantorMap.entrySet()) {
                    List<ContractGuarantor> list = entry.getValue();
                    int size = list.size();
                    for (ContractGuarantor contractGuarantor : list) {
                        if (size == 1) {
                            // 只有一条担保措施的情况要进一步判断单条数据有多少个担保人
                            if (StrUtil.isNotBlank(contractGuarantor.getGuarantorIds())) {
                                JSONArray jsonArray = JSONUtil.parseArray(contractGuarantor.getGuarantorIds());
                                if (jsonArray.size() == 1) {
                                    contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.SINGLE.name());
                                } else {
                                    contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.JOINT.name());
                                }
                            }
                        } else {
                            contractGuarantor.setJointGuaranteeMark(JointGuaranteeMarkEnum.JOINT.name());
                        }
                    }
                    updateList.addAll(list);
                }
                if (CollectionUtil.isNotEmpty(updateList)) {
                    SpringUtil.getBean(ContractGuarantorService.class).updateBatchById(updateList);
                }
            }
        }
        // 版本区数据订正（只订正最新版本）
        ContractBaseInfoLib contractBaseInfoLib = SpringUtil.getBean(ContractBaseInfoLibHandler.class).queryLatestDataByOriginId(contractId);
        if (Objects.isNull(contractBaseInfoLib)) {
            return;
        }
        List<ContractGuarantorLib> contractGuarantorLibList = SpringUtil.getBean(ContractGuarantorLibService.class).listByVersion(contractId, contractBaseInfoLib.getVersion());
        if (CollectionUtil.isNotEmpty(contractGuarantorLibList)) {
            // 根据担保合同编号进行分组
            Map<String, List<ContractGuarantorLib>> contractGuarantorLibMap = contractGuarantorLibList.stream().filter(e -> StrUtil.isNotBlank(e.getGuarantorContractCode())).collect(Collectors.groupingBy(ContractGuarantor::getGuarantorContractCode));
            if (CollectionUtil.isNotEmpty(contractGuarantorLibMap)) {
                List<ContractGuarantorLib> updateList = new LinkedList<>();
                // 根据同一个担保合同编号下的数据数量来决定是单人保证还是联保
                for (Map.Entry<String, List<ContractGuarantorLib>> entry : contractGuarantorLibMap.entrySet()) {
                    List<ContractGuarantorLib> list = entry.getValue();
                    int size = list.size();
                    for (ContractGuarantorLib contractGuarantorLib : list) {
                        if (size == 1) {
                            // 只有一条担保措施的情况要进一步判断单条数据有多少个担保人
                            if (StrUtil.isNotBlank(contractGuarantorLib.getGuarantorIds())) {
                                JSONArray jsonArray = JSONUtil.parseArray(contractGuarantorLib.getGuarantorIds());
                                if (jsonArray.size() == 1) {
                                    contractGuarantorLib.setJointGuaranteeMark(JointGuaranteeMarkEnum.SINGLE.name());
                                } else {
                                    contractGuarantorLib.setJointGuaranteeMark(JointGuaranteeMarkEnum.JOINT.name());
                                }
                            }
                        } else {
                            contractGuarantorLib.setJointGuaranteeMark(JointGuaranteeMarkEnum.JOINT.name());
                        }
                    }
                    updateList.addAll(list);
                }
                if (CollectionUtil.isNotEmpty(updateList)) {
                    SpringUtil.getBean(ContractGuarantorLibService.class).updateBatchById(updateList);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void contractAutoFlowDataFix(ContractBaseInfo contractBaseInfo) {
        Long contractId = contractBaseInfo.getId();
        // 合同数据处理
        // 起租未提交，取消起租
        if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_UNCOMMIT.name())) {
            // 更新状态
            // 回退数据版本
            contractVersionService.reset(contractId);
            // 取消关联付款申请
            if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                paymentBaseInfoService.cancelJoinReceiptByContractId(contractId);
            }
        }
        // 起租审批中，关闭流程。手动操作
        // 新增投放（借据）未提交，取消新增借据（投放）
        if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_RECEIPT_UNCOMMIT.name())) {
            // 回退数据版本
            contractVersionService.reset(contractId);
            // 取消关联付款申请
            if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                paymentBaseInfoService.cancelJoinReceiptByContractId(contractId);
            }
        }
        // 新增投放（借据）审批中，关闭流程。手动操作

        // 付款数据处理
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listEffectPaymentByContractId(contractId);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            if (!Objects.equals(paymentBaseInfo.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name())) {
                continue;
            }
            // 只要有付款核销记录就填充操作日期，操作日期统一用最早的那一天
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentId(paymentBaseInfo.getId());
            if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
                List<LocalDate> list = paymentActualDetailList.stream().filter(e -> Objects.nonNull(e.getPaidInDate())).sorted(Comparator.comparing(PaymentActualDetail::getPaidInDate)).map(PaymentActualDetail::getPaidInDate).collect(Collectors.toList());
                for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
                    paymentActualDetail.setOperationDate(list.get(0));
                }
                paymentActualDetailService.updateBatchById(paymentActualDetailList);
            }
            if (Objects.nonNull(paymentBaseInfo.getReceiptIdFinal())) {
                // 已关联借据的付款申请则变更状态为"已投放"
                paymentBaseInfo.setPaymentStatus(PaymentStatusEnum.FINISHED.name());
            } else {
                // 未关联借据的删除付款申请下的付款核销记录
                List<PaymentActualDetail> toRemovePaymentActualDetailList = paymentActualDetailService.listByPaymentId(paymentBaseInfo.getId());
                if (CollectionUtil.isNotEmpty(toRemovePaymentActualDetailList)) {
                    Set<Long> ids = toRemovePaymentActualDetailList.stream().map(PaymentActualDetail::getId).collect(Collectors.toSet());
                    paymentActualDetailService.removeByIds(ids);
                }
                // 变更付款申请状态
                paymentBaseInfo.setWriteOffStatus(PaymentWriteOffStatus.NO_PAID.name());
            }
            paymentBaseInfoService.updateById(paymentBaseInfo);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importKpiBonusData() {
        int year = 2023;
        int month = 6;
        List<FinanceProjectProfitDetail> financeProjectProfitDetailList = SpringUtil.getBean(FinanceProjectProfitDetailService.class).listHasIncomeContractDetail(year, month);
        if (CollectionUtil.isEmpty(financeProjectProfitDetailList)) {
            return;
        }
        for (FinanceProjectProfitDetail detail : financeProjectProfitDetailList) {
            Long contractId = detail.getContractId();
            KpiProjGuessBaseInfo exist = SpringUtil.getBean(KpiProjGuessBaseInfoService.class).getSpecificDateOne(contractId, year, month);
            if (Objects.nonNull(exist)) {
                log.info("对应月份的绩效数据已经存在，忽略不处理[contractId:{}]", contractId);
                continue;
            }
            log.info("准备计算绩效奖金[contractId:{}]", contractId);
            // 确定项目利润
            FinanceProjectProfitDetail financeProjectProfitDetail = SpringUtil.getBean(FinanceProjectProfitDetailService.class).getSpecificOne(contractId, year, month);
            log.info("确定项目利润结果:{}", JSONUtil.toJsonStr(financeProjectProfitDetail));

            // 查询项目分配基本信息
            KpiProjectDistributionBaseInfoLib distributionBaseInfoLib = SpringUtil.getBean(KpiProjGuessService.class).getDistributionBaseInfoLib(contractId, year, month);

            // 确定分润比
            List<KpiProjectDistributionWeightLib> weightLibList;
            if (Objects.nonNull(distributionBaseInfoLib)) {
                weightLibList = SpringUtil.getBean(KpiProjectDistributionWeightLibService.class).listByMainIdAndVersion(distributionBaseInfoLib.getProjectDistributionId(), distributionBaseInfoLib.getVersion());
            } else {
                weightLibList = Collections.emptyList();
            }
            log.info("确定分润比结果:{}", JSONUtil.toJsonStr(weightLibList));

            // 确定提奖比例
            String awardRadioConfig = null;
            KpiParameterConfig kpiParameterConfig = SpringUtil.getBean(KpiParameterConfigService.class).getOneByConfigCode(KpiParameterConfigCodeEnum.PROJECT_RADIO);
            if (Objects.nonNull(kpiParameterConfig)) {
                awardRadioConfig = kpiParameterConfig.getConfigValue();
            }
            BigDecimal awardRadio = null;
            if (Objects.nonNull(distributionBaseInfoLib)) {
                awardRadio = SpringUtil.getBean(KpiParameterConfigService.class).ensureProjectRadio(awardRadioConfig, distributionBaseInfoLib.getProjClassify(), distributionBaseInfoLib.getContractStartDate(), LocalDate.of(year, month, 1));
            }
            log.info("确定项目提奖比例结果:{}", Optional.ofNullable(awardRadio).map(BigDecimal::toPlainString).orElse(null));

            if (Objects.isNull(financeProjectProfitDetail) || CollectionUtil.isEmpty(weightLibList) || Objects.isNull(awardRadio)) {
                log.info("参与计算绩效奖金的必要条件存在缺失，放弃计算[contractId:{}]", contractId);
                continue;
            }

            // 绩效测算主表数据
            KpiProjGuessBaseInfo toSaveBaseInfo = new KpiProjGuessBaseInfo();
            toSaveBaseInfo.setKpiProjectDistributionId(distributionBaseInfoLib.getProjectDistributionId());
            toSaveBaseInfo.setKpiProjectDistributionVersion(distributionBaseInfoLib.getVersion());
            toSaveBaseInfo.setContractId(contractId);
            toSaveBaseInfo.setCalculateDateYear(year);
            toSaveBaseInfo.setCalculateDateMonth(month);
            toSaveBaseInfo.setAwardRatio(awardRadio.multiply(BigDecimal.valueOf(1000000)).longValue());
            toSaveBaseInfo.setAwardRatioConfig(awardRadioConfig);
            toSaveBaseInfo.setProfitCurrent(financeProjectProfitDetail.getProfitThisMonth());
            toSaveBaseInfo.setProfitTotal(financeProjectProfitDetail.getTotalProfitThisYear());
            toSaveBaseInfo.setBonusCurrent(0L);
            toSaveBaseInfo.setBonusTotal(0L);
            SpringUtil.getBean(KpiProjGuessBaseInfoService.class).save(toSaveBaseInfo);
            // 绩效测算子表数据
            List<KpiProjGuessDivide> kpiProjGuessDivideList = new LinkedList<>();
            for (KpiProjectDistributionWeightLib weightLib : weightLibList) {
                if (StrUtil.isBlank(weightLib.getWeightTarget()) || Objects.isNull(weightLib.getWeightValue())) {
                    continue;
                }
                if (weightLib.getWeightValue() == 0) {
                    continue;
                }
                // 项目奖金 = 项目利润 * 分润比 * 项目提奖比例
                KpiProjGuessDivide divide = new KpiProjGuessDivide();
                divide.setKpiProjGuessId(toSaveBaseInfo.getId());
                divide.setContractId(contractId);
                divide.setDivideYear(year);
                divide.setDivideMonth(month);
                divide.setDivideType(weightLib.getWeightType());
                divide.setDivideTarget(Long.valueOf(weightLib.getWeightTarget()));
                BigDecimal profitThisMonth = BigDecimal.valueOf(financeProjectProfitDetail.getProfitThisMonth()).multiply(BigDecimal.valueOf(weightLib.getWeightValue())).divide(BigDecimal.valueOf(1000000), 10, RoundingMode.HALF_UP);
                BigDecimal bonusThisMonth = profitThisMonth.multiply(awardRadio);
                divide.setProfitCurrent(Util.mithrasLongDecimalTwo(profitThisMonth.longValue()));
                divide.setBonusCurrent(Util.mithrasLongDecimalTwo(bonusThisMonth.longValue()));
                BigDecimal profitThisYear = BigDecimal.valueOf(financeProjectProfitDetail.getTotalProfitThisYear()).multiply(BigDecimal.valueOf(weightLib.getWeightValue())).divide(BigDecimal.valueOf(1000000), 10, RoundingMode.HALF_UP);
                BigDecimal bonusThisYear = profitThisYear.multiply(awardRadio);
                divide.setProfitTotal(Util.mithrasLongDecimalTwo(profitThisYear.longValue()));
                divide.setBonusTotal(Util.mithrasLongDecimalTwo(bonusThisYear.longValue()));
                kpiProjGuessDivideList.add(divide);
                // 更新主表合计值
                toSaveBaseInfo.setBonusCurrent(toSaveBaseInfo.getBonusCurrent() + divide.getBonusCurrent());
                toSaveBaseInfo.setBonusTotal(toSaveBaseInfo.getBonusTotal() + divide.getBonusTotal());
            }
            if (CollectionUtil.isNotEmpty(kpiProjGuessDivideList)) {
                SpringUtil.getBean(KpiProjGuessDivideService.class).saveBatch(kpiProjGuessDivideList);
            }
            SpringUtil.getBean(KpiProjGuessBaseInfoService.class).updateById(toSaveBaseInfo);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importFinanceProvisionData(String fileName, int dataStartRowIndex, LocalDate provisionDate) {
        List<List<Object>> dataList = ExcelUtil.getReader(fileName).read(dataStartRowIndex);
        if (CollectionUtil.isEmpty(dataList)) {
            log.info("没有数据");
            return;
        }
        // 创建主表数据
        KpiProvisionBaseInfo kpiProvisionBaseInfo = new KpiProvisionBaseInfo();
        kpiProvisionBaseInfo.setProvisionDate(provisionDate);
        kpiProvisionBaseInfo.setProvisionStatus(KpiProvisionStatusEnum.EFFECT.name());
        SpringUtil.getBean(KpiProvisionBaseInfoService.class).save(kpiProvisionBaseInfo);
        Long mainId = kpiProvisionBaseInfo.getId();
        // 创建明细表
        List<KpiProvisionDetail> detailList = new LinkedList<>();
        for (List<Object> row : dataList) {
            String contractCode = row.get(4).toString();
            try {
                // 查合同
                LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
                contractQuery.eq(ContractBaseInfo::getContractCode, contractCode);
                contractQuery.notIn(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.CLOSED.name(), ContractStatus.INVALID.name()));
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getOne(contractQuery);
                if (Objects.isNull(contractBaseInfo)) {
                    log.info("{}合同信息不存在", contractCode);
                    continue;
                }
                // 查借据
                String receiptCode = Optional.ofNullable(row.get(14)).map(Object::toString).orElse(null);
                ContractReceipt contractReceipt = null;
                if (StrUtil.isBlank(receiptCode)) {
                    List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId());
                    if (CollectionUtil.isEmpty(contractReceiptList) || contractReceiptList.size() > 1) {
                        log.info("{}不能唯一确定借据，待人工处理", contractCode);
                    } else {
                        contractReceipt = contractReceiptList.get(0);
                    }
                } else {
                    contractReceipt = SpringUtil.getBean(ContractReceiptService.class).getOntByReceiptCode(receiptCode);
                }
                KpiProvisionDetail detail = new KpiProvisionDetail();
                detail.setProvisionId(mainId);
                if (Objects.nonNull(contractReceipt)) {
                    detail.setReceiptId(contractReceipt.getId());
                }
                detail.setBizType(contractBaseInfo.getBizType());
                detail.setLeaseType(contractBaseInfo.getLeaseType());
                Object industryType = row.get(1);
                if (Objects.nonNull(industryType)) {
                    if (Objects.equals("产业类", industryType)) {
                        detail.setProjClassify(KpiProjectClassifyEnum.INDUSTRY.name());
                    } else {
                        detail.setProjClassify(KpiProjectClassifyEnum.PUBLIC.name());
                    }
                }
                detail.setProfitBelongDeptId(contractBaseInfo.getBizDeptId());
                detail.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
                detail.setClientId(contractBaseInfo.getClientId());
                detail.setContractId(contractBaseInfo.getId());
                detail.setContractCode(contractBaseInfo.getContractCode());
                detail.setEndDate(Optional.ofNullable(row.get(5)).map(Object::toString).map(e -> e.substring(0, 10)).map(e -> LocalDateTimeUtil.parseDate(e, "yyyy-MM-dd")).orElse(null));
                detail.setRemainingPrincipal(Optional.ofNullable(row.get(7)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L));
                detail.setEarnestBalance(Optional.ofNullable(row.get(8)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(null));
                detail.setExposure(Optional.ofNullable(row.get(9)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L));
                Object riskLevel = row.get(10);
                if (Objects.equals("关注类", riskLevel)) {
                    detail.setRiskLevel(AssetClassifyResultEnum.ATTENTION.name());
                }
                if (Objects.equals("可疑类", riskLevel)) {
                    detail.setRiskLevel(AssetClassifyResultEnum.SUSPICIOUS.name());
                }
                if (Objects.equals("正常类", riskLevel)) {
                    detail.setRiskLevel(AssetClassifyResultEnum.NORMAL.name());
                }
                detail.setWithdrawalRatio(Optional.ofNullable(row.get(11)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(1000000)).longValue()).orElse(null));
                detail.setProfitCurrent(Optional.ofNullable(row.get(12)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L));
//                detail.setProfitTotal(Optional.ofNullable(row.get(13)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(1000000)).longValue()).orElse(0L));
//                detail.setBonusCurrent(Optional.ofNullable(row.get(14)).map(Object::toString).map(e -> new BigDecimal(e).multiply(BigDecimal.valueOf(1000000)).longValue()).orElse(0L));
                detail.setProvisionDate(provisionDate);
                detailList.add(detail);
            } catch (Exception e) {
                log.error("处理过程发生异常[{}]", contractCode, e);
                throw e;
            }
        }
        SpringUtil.getBean(KpiProvisionDetailService.class).saveBatch(detailList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void importFinanceProfitData(String fileName, int year, int month, int startRow) {
        List<List<Object>> dataList = ExcelUtil.getReader(fileName).read(startRow);
        if (CollectionUtil.isEmpty(dataList)) {
            log.info("没有数据");
            return;
        }
        // 查询上一期数据
        Map<String, FinanceProjectProfitDetail> lastMonthDetailMap;
        Map<String, FinanceProjectProfitDetailReceipt> lastMonthDetailReceiptMap;
        if (month > 1) {
            FinanceProjectProfit lastMonth = SpringUtil.getBean(FinanceProjectProfitService.class).getOneByYearMonth(year, month - 1);
            if (Objects.isNull(lastMonth)) {
                lastMonthDetailMap = Collections.emptyMap();
                lastMonthDetailReceiptMap = Collections.emptyMap();
            } else {
                lastMonthDetailMap = SpringUtil.getBean(FinanceProjectProfitDetailService.class).listByProjectProfitId(lastMonth.getId()).stream().collect(Collectors.toMap(e -> e.getAssessDeptId() + "@" + e.getContractId(), e -> e));
                lastMonthDetailReceiptMap = SpringUtil.getBean(FinanceProjectProfitDetailReceiptService.class).listByProfitId(lastMonth.getId()).stream().collect(Collectors.toMap(e -> e.getAssessDeptId() + "@" + e.getReceiptId(), e -> e));
            }
        } else {
            lastMonthDetailMap = Collections.emptyMap();
            lastMonthDetailReceiptMap = Collections.emptyMap();
        }
        // 项目利润主表数据
        FinanceProjectProfit financeProjectProfit = new FinanceProjectProfit();
        financeProjectProfit.setYear(year);
        financeProjectProfit.setMonth(month);
        financeProjectProfit.setIncomeThisMonth(0L);
        financeProjectProfit.setCostThisMonth(0L);
        financeProjectProfit.setRiskThisMonth(0L);
        financeProjectProfit.setProfitThisMonth(0L);
        financeProjectProfit.setTotalIncomeThisYear(0L);
        financeProjectProfit.setTotalCostThisYear(0L);
        financeProjectProfit.setTotalRiskThisYear(0L);
        financeProjectProfit.setTotalProfitThisYear(0L);
        financeProjectProfit.setTotalAdditionalTaxThisYear(0L);
        financeProjectProfit.setTotalStampTaxThisYear(0L);
        financeProjectProfit.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
        SpringUtil.getBean(FinanceProjectProfitService.class).save(financeProjectProfit);
        // 项目利润子表数据
        Map<String, FinanceProjectProfitDetail> detailMap = new HashMap<>();
        Map<String, FinanceProjectProfitDetailReceipt> detailContractReceiptMap = new HashMap<>();
        Map<String, Long> orgName2IdMap = SpringUtil.getBean(SysUserService.class).listAllDept().stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId));
        for (List<Object> columns : dataList) {
            String contractCode = Optional.ofNullable(columns.get(4)).map(Object::toString).orElse(null);
            if (StrUtil.isBlank(contractCode)) {
                log.info("合同编号为空");
                continue;
            }
            LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
            query.eq(ContractBaseInfo::getContractCode, contractCode);
            query.notIn(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.CLOSED.name(), ContractStatus.INVALID.name(), ContractStatus.NEW.name()));
            List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).list(query);
            if (CollectionUtil.isEmpty(contractBaseInfoList)) {
                log.info("没有找到合同编号为{}的合同信息", contractCode);
                continue;
            }
            if (contractBaseInfoList.size() > 1) {
                log.info("存在多个合同编号为{}的合同信息", contractCode);
                continue;
            }
            String orgName = Optional.ofNullable(columns.get(1)).map(Object::toString).orElse(null);
            Long deptId = orgName2IdMap.get(orgName);
            if (Objects.isNull(deptId)) {
                throw new MithrasException("无法确定部门[" + orgName + "]");
            }
            ContractBaseInfo contractBaseInfo = contractBaseInfoList.get(0);
            // 找借据
            ContractReceipt contractReceipt;
            String receiptCode = null;
            if (columns.size() > 34) {
                receiptCode = Optional.ofNullable(columns.get(34)).map(Object::toString).orElse(null);
            }
            if (StrUtil.isBlank(receiptCode)) {
                // 没有在Excel中标记借据编号的，正常来说系统中应该只有一个借据编号，直接查询
                List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId());
                if (CollectionUtil.isEmpty(contractReceiptList)) {
                    log.info("没有找到借据信息[合同编号：{}]", contractCode);
                    continue;
                }
                if (contractReceiptList.size() > 1) {
                    log.info("存在多个借据信息[合同编号：{}]", contractCode);
                    continue;
                }
                contractReceipt = contractReceiptList.get(0);
            } else {
                contractReceipt = SpringUtil.getBean(ContractReceiptService.class).getOntByReceiptCode(receiptCode);
            }
            String contractStartDate = Optional.ofNullable(columns.get(6)).map(Object::toString).orElse(null);
            //本月收入
            long incomeThisMonth = Optional.ofNullable(columns.get(17)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            long revenueThisMonth = Optional.ofNullable(columns.get(17)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //本年累计收入
            long incomeThisYear = Optional.ofNullable(columns.get(18)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            long costThisMonth = Optional.ofNullable(columns.get(19)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            long costThisYear = Optional.ofNullable(columns.get(20)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            long grossProfitThisMonth = Optional.ofNullable(columns.get(21)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            long totalGrossProfitThisYear = Optional.ofNullable(columns.get(22)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //风险金计提
            long riskThisMonth = Optional.ofNullable(columns.get(26)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //本月风险金余额
            long riskThisYear = Optional.ofNullable(columns.get(23)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //年初风险金余额
            long riskBalanceBeginYear = Optional.ofNullable(columns.get(25)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //累计风险金计提
            long totalRiskBalanceThisYear = Optional.ofNullable(columns.get(27)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //附加税
            long additionalTax = Optional.ofNullable(columns.get(30)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //印花税
            long stampTax = Optional.ofNullable(columns.get(31)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //本年累计利润总额-扣费前
            long totalProfitThisYearBefore = Optional.ofNullable(columns.get(32)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            //本年累计利润总额-扣费后
            long profitThisYear = Optional.ofNullable(columns.get(33)).map(Object::toString).map(e -> new BigDecimal(StrUtil.isBlank(e) ? "0" : e).multiply(BigDecimal.valueOf(10000)).longValue()).orElse(0L);
            // 借据维度数据
            FinanceProjectProfitDetailReceipt detailReceipt = detailContractReceiptMap.get(deptId + "@" + contractReceipt.getId());
            if (Objects.isNull(detailReceipt)) {
                detailReceipt = new FinanceProjectProfitDetailReceipt();
                detailReceipt.setAssessDeptId(deptId);
                detailReceipt.setProjectProfitId(financeProjectProfit.getId());
                detailReceipt.setYear(year);
                detailReceipt.setMonth(month);
                detailReceipt.setContractId(contractReceipt.getContractId());
                detailReceipt.setReceiptId(contractReceipt.getId());
                if (Objects.nonNull(contractReceipt.getReceiptStartDate())) {
                    detailReceipt.setReceiptStartDate(contractReceipt.getReceiptStartDate());
                }
                detailReceipt.setIncomeThisMonth(0L);
                detailReceipt.setRevenueThisMonth(0L);
                detailReceipt.setTotalIncomeThisYear(0L);
                detailReceipt.setCostThisMonth(0L);
                detailReceipt.setTotalCostThisYear(0L);
                detailReceipt.setRiskThisMonth(0L);
                detailReceipt.setTotalRiskThisYear(0L);
                detailReceipt.setProfitThisMonth(0L);
                detailReceipt.setTotalProfitThisYear(0L);
                detailReceipt.setTotalAdditionalTaxThisYear(0L);
                detailReceipt.setTotalStampTaxThisYear(0L);
                detailReceipt.setGrossProfitThisMonth(0L);
                detailReceipt.setTotalGrossProfitThisYear(0L);
                detailReceipt.setRiskBalanceBeginYear(0L);
                detailReceipt.setTotalRiskBalanceThisYear(0L);
                detailReceipt.setTotalProfitThisYearBefore(0L);
            }
            detailReceipt.setIncomeThisMonth(detailReceipt.getIncomeThisMonth() + incomeThisMonth);
            detailReceipt.setRevenueThisMonth(detailReceipt.getRevenueThisMonth() + revenueThisMonth);
            detailReceipt.setTotalIncomeThisYear(detailReceipt.getTotalIncomeThisYear() + incomeThisYear);
            detailReceipt.setCostThisMonth(detailReceipt.getCostThisMonth() + costThisMonth);
            detailReceipt.setTotalCostThisYear(detailReceipt.getTotalCostThisYear() + costThisYear);
            detailReceipt.setGrossProfitThisMonth(detailReceipt.getGrossProfitThisMonth() + grossProfitThisMonth);
            detailReceipt.setTotalGrossProfitThisYear(detailReceipt.getTotalGrossProfitThisYear() + totalGrossProfitThisYear);
            detailReceipt.setRiskThisMonth(detailReceipt.getRiskThisMonth() + riskThisMonth);
            detailReceipt.setTotalRiskThisYear(detailReceipt.getTotalRiskThisYear() + riskThisYear);
            detailReceipt.setRiskBalanceBeginYear(detailReceipt.getRiskBalanceBeginYear() + riskBalanceBeginYear);
            detailReceipt.setTotalRiskBalanceThisYear(detailReceipt.getTotalRiskBalanceThisYear() + totalRiskBalanceThisYear);
            detailReceipt.setTotalProfitThisYearBefore(detailReceipt.getTotalProfitThisYearBefore() + totalProfitThisYearBefore);
            detailReceipt.setTotalProfitThisYear(detailReceipt.getTotalProfitThisYear() + profitThisYear);
            detailReceipt.setTotalAdditionalTaxThisYear(detailReceipt.getTotalAdditionalTaxThisYear() + additionalTax);
            detailReceipt.setTotalStampTaxThisYear(detailReceipt.getTotalStampTaxThisYear() + stampTax);
            detailContractReceiptMap.put(deptId + "@" + contractReceipt.getId(), detailReceipt);
            // 合同维度数据
            FinanceProjectProfitDetail detail = detailMap.get(deptId + "@" + contractBaseInfo.getId());
            if (Objects.isNull(detail)) {
                detail = new FinanceProjectProfitDetail();
                detail.setAssessDeptId(deptId);
                detail.setProjectProfitId(financeProjectProfit.getId());
                detail.setYear(year);
                detail.setMonth(month);
                detail.setContractId(contractBaseInfo.getId());
                if (StrUtil.isNotBlank(contractStartDate)) {
                    detail.setContractStartDate(LocalDateTimeUtil.parseDate(contractStartDate.substring(0, 10), "yyyy-MM-dd"));
                }
                detail.setIncomeThisMonth(0L);
                detail.setRevenueThisMonth(0L);
                detail.setTotalIncomeThisYear(0L);
                detail.setCostThisMonth(0L);
                detail.setTotalCostThisYear(0L);
                detail.setRiskThisMonth(0L);
                detail.setTotalRiskThisYear(0L);
                detail.setProfitThisMonth(0L);
                detail.setTotalProfitThisYear(0L);
                detail.setTotalAdditionalTaxThisYear(0L);
                detail.setTotalStampTaxThisYear(0L);
                detail.setGrossProfitThisMonth(0L);
                detail.setTotalGrossProfitThisYear(0L);
                detail.setRiskBalanceBeginYear(0L);
                detail.setTotalRiskBalanceThisYear(0L);
                detail.setTotalProfitThisYearBefore(0L);
            }
            detail.setIncomeThisMonth(detail.getIncomeThisMonth() + incomeThisMonth);
            detail.setRevenueThisMonth(detail.getRevenueThisMonth() + revenueThisMonth);
            detail.setTotalIncomeThisYear(detail.getTotalIncomeThisYear() + incomeThisYear);
            detail.setCostThisMonth(detail.getCostThisMonth() + costThisMonth);
            detail.setTotalCostThisYear(detail.getTotalCostThisYear() + costThisYear);
            detail.setGrossProfitThisMonth(detail.getGrossProfitThisMonth() + grossProfitThisMonth);
            detail.setTotalGrossProfitThisYear(detail.getTotalGrossProfitThisYear() + totalGrossProfitThisYear);
            detail.setRiskThisMonth(detail.getRiskThisMonth() + riskThisMonth);
            detail.setTotalRiskThisYear(detail.getTotalRiskThisYear() + riskThisYear);
            detail.setRiskBalanceBeginYear(detail.getRiskBalanceBeginYear() + riskBalanceBeginYear);
            detail.setTotalRiskBalanceThisYear(detail.getTotalRiskBalanceThisYear() + totalRiskBalanceThisYear);
            detail.setTotalProfitThisYearBefore(detail.getTotalProfitThisYearBefore() + totalProfitThisYearBefore);
            detail.setTotalProfitThisYear(detail.getTotalProfitThisYear() + profitThisYear);
            detail.setTotalAdditionalTaxThisYear(detail.getTotalAdditionalTaxThisYear() + additionalTax);
            detail.setTotalStampTaxThisYear(detail.getTotalStampTaxThisYear() + stampTax);
            detailMap.put(deptId + "@" + contractBaseInfo.getId(), detail);
        }
        // 倒减得到本月利润
        for (FinanceProjectProfitDetailReceipt detailReceipt : detailContractReceiptMap.values()) {
            FinanceProjectProfitDetailReceipt lastMonthDetailReceipt = lastMonthDetailReceiptMap.get(detailReceipt.getAssessDeptId() + "@" + detailReceipt.getReceiptId());
            if (Objects.nonNull(lastMonthDetailReceipt) && Objects.nonNull(lastMonthDetailReceipt.getTotalProfitThisYear())) {
                detailReceipt.setProfitThisMonth(detailReceipt.getTotalProfitThisYear() - lastMonthDetailReceipt.getTotalProfitThisYear());
            } else {
                detailReceipt.setProfitThisMonth(detailReceipt.getTotalProfitThisYear());
            }
        }
        // 批量保存
        SpringUtil.getBean(FinanceProjectProfitDetailReceiptService.class).saveBatch(detailContractReceiptMap.values());
        // 倒减得到本月利润
        for (FinanceProjectProfitDetail detail : detailMap.values()) {
            FinanceProjectProfitDetail lastMonthDetail = lastMonthDetailMap.get(detail.getAssessDeptId() + "@" + detail.getContractId());
            if (Objects.nonNull(lastMonthDetail) && Objects.nonNull(lastMonthDetail.getTotalProfitThisYear())) {
                detail.setProfitThisMonth(detail.getTotalProfitThisYear() - lastMonthDetail.getTotalProfitThisYear());
            } else {
                detail.setProfitThisMonth(detail.getTotalProfitThisYear());
            }
        }
        // 批量保存
        SpringUtil.getBean(FinanceProjectProfitDetailService.class).saveBatch(detailMap.values());
        // 更新合计值
        Collection<FinanceProjectProfitDetail> detailList = detailMap.values();
        financeProjectProfit.setIncomeThisMonth(detailList.stream().mapToLong(FinanceProjectProfitDetail::getIncomeThisMonth).sum());
        financeProjectProfit.setCostThisMonth(detailList.stream().mapToLong(FinanceProjectProfitDetail::getCostThisMonth).sum());
        financeProjectProfit.setRiskThisMonth(detailList.stream().mapToLong(FinanceProjectProfitDetail::getRiskThisMonth).sum());
        financeProjectProfit.setProfitThisMonth(detailList.stream().mapToLong(FinanceProjectProfitDetail::getProfitThisMonth).sum());
        financeProjectProfit.setTotalIncomeThisYear(detailList.stream().mapToLong(FinanceProjectProfitDetail::getTotalIncomeThisYear).sum());
        financeProjectProfit.setTotalCostThisYear(detailList.stream().mapToLong(FinanceProjectProfitDetail::getTotalCostThisYear).sum());
        financeProjectProfit.setTotalRiskThisYear(detailList.stream().mapToLong(FinanceProjectProfitDetail::getTotalRiskThisYear).sum());
        financeProjectProfit.setTotalProfitThisYear(detailList.stream().mapToLong(FinanceProjectProfitDetail::getTotalProfitThisYear).sum());
        financeProjectProfit.setTotalAdditionalTaxThisYear(detailList.stream().mapToLong(FinanceProjectProfitDetail::getTotalAdditionalTaxThisYear).sum());
        financeProjectProfit.setTotalStampTaxThisYear(detailList.stream().mapToLong(FinanceProjectProfitDetail::getTotalStampTaxThisYear).sum());
        SpringUtil.getBean(FinanceProjectProfitService.class).updateById(financeProjectProfit);
    }

    public void fixClientRiskControlIndustry() {
        List<ClientRiskControlExcelModel> excelDataList = new AbstractSimpleExcelImporter<ClientRiskControlExcelModel>() {

            @Override
            protected ExcelConfig config(ExcelReader excelReader) {
                return new ExcelConfig(2, 3, null);
            }

            @Override
            protected Map<String, String> getHeaderAlias() {
                Map<String, String> header = new HashMap<>();
                header.put("客户名称", "clientName");
                header.put("年度风险策略限额行业类别", "riskDesc");
                return header;
            }

            @Override
            protected CellEditor getCellEditor() {
                return null;
            }

            @Override
            protected Class<ClientRiskControlExcelModel> modelClz() {
                return ClientRiskControlExcelModel.class;
            }
        }.parse(FileUtil.getInputStream("/Users/mockorz/副本228存量项目行业.xlsx"));

        for (ClientRiskControlExcelModel excelModel : excelDataList) {
            RiskControlIndustryClassify r = RiskControlIndustryClassify.findByDisplay(excelModel.getRiskDesc());
            if (Objects.nonNull(r)) {
                excelModel.setRiskCode(r.name());
            }
            LambdaQueryWrapper<Client> query = Wrappers.lambdaQuery();
            query.eq(Client::getClientName, excelModel.getClientName());
            query.last(StringUtil.mysqlLimitOne());
            Client client = clientMapper.selectOne(query);
            if (Objects.nonNull(client)) {
                excelModel.setClientId(client.getId());
            }
        }

        new AbstractSimpleExcelExporter<ClientRiskControlExcelModel>() {

            @Override
            protected LinkedHashMap<String, String> getHeaderAliasMap() {
                LinkedHashMap<String, String> header = new LinkedHashMap<>();
                header.put("clientId", "客户id");
                header.put("clientName", "客户名称");
                header.put("riskCode", "风控行业分类code");
                header.put("riskDesc", "风控行业分类描述");
                return header;
            }

            @Override
            protected Map<Integer, CellStyle> getColumnStyleMap(Workbook workbook) {
                return null;
            }

            @Override
            protected void customStrategy(Workbook workbook) {

            }

            @Override
            protected Class<ClientRiskControlExcelModel> modelClz() {
                return ClientRiskControlExcelModel.class;
            }
        }.exportExcel(excelDataList, FileUtil.getOutputStream("/Users/mockorz/result.xlsx"));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void fixContractActualFinishDate() {
        List<ContractBaseInfo> all = contractBaseInfoService.list();
        if (CollectionUtil.isEmpty(all)) {
            return;
        }
        for (ContractBaseInfo contractBaseInfo : all) {
            List<ContractRentActual> rentActualList = contractRentActualService.listByContract(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(rentActualList)) {
                continue;
            }
            ContractRentActual last = rentActualList.get(rentActualList.size() - 1);
            // 更新编辑区
            ContractBaseInfo toUpdate = new ContractBaseInfo();
            toUpdate.setId(contractBaseInfo.getId());
            toUpdate.setActualFinishDate(last.getCashFlowDate());
            contractBaseInfoService.updateById(toUpdate);
            // 更新最近一次版本数据
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(contractBaseInfo.getId());
            if (Objects.nonNull(contractBaseInfoLib)) {
                ContractBaseInfoLib toUpdateLib = new ContractBaseInfoLib();
                toUpdateLib.setId(contractBaseInfoLib.getId());
                toUpdateLib.setActualFinishDate(last.getCashFlowDate());
                contractBaseInfoLibService.updateById(toUpdateLib);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void fixBizBaseData() {
        // 客户id
        Long clientId = 2560L;
        // 业务部门id
        Long bizDeptId = 6L;
        // 业务负责人id
        Long bizDeptLeaderId = 67L;
        // 分管领导id
        Long bizDivisionLeaderId = 52L;
        // 项目主办id
        Long sponsorId = 67L;
        // 项目协办id
        List<Long> cosponsorIdList = Collections.singletonList(70L);
        // 更新客户信息
        this.doClient(clientId, bizDeptId, sponsorId);
        log.info("跟新客户信息成功[客户id: {}]", clientId);
        // 更新立项信息
        List<Long> projEstablishIdList = this.doProjEstablish(clientId, bizDeptId, bizDeptLeaderId, bizDivisionLeaderId, sponsorId, cosponsorIdList);
        log.info("更新立项信息成功[项目id: {}]", JSONUtil.toJsonStr(projEstablishIdList));
        // 更新评审信息
        List<Long> projectReviewIdList = this.doProjReview(projEstablishIdList, bizDeptId, bizDeptLeaderId, bizDivisionLeaderId, sponsorId, cosponsorIdList);
        log.info("更新评审信息成功[评审id: {}]", JSONUtil.toJsonStr(projectReviewIdList));
        // 更新合同信息
        List<Long> contractIdList = this.doContract(projectReviewIdList, bizDeptId, bizDeptLeaderId, bizDivisionLeaderId, sponsorId, cosponsorIdList);
        log.info("更新合同信息成功[合同id: {}]", JSONUtil.toJsonStr(contractIdList));
    }

    private void doClient(Long clientId, Long bizDeptId, Long sponsorId) {
        Client toUpdate = new Client();
        toUpdate.setId(clientId);
        toUpdate.setBelongDeptId(bizDeptId);
        toUpdate.setBelongSponsorId(sponsorId);
        clientMapper.updateById(toUpdate);
    }

    private List<Long> doProjEstablish(Long clientId, Long bizDeptId, Long bizDeptLeaderId, Long bizDivisionLeaderId, Long sponsorId, List<Long> cosponsorIdList) {
        // 更新编辑区
        LambdaQueryWrapper<ProjEstablishBaseInfo> projEstablishBaseInfoQuery = Wrappers.lambdaQuery();
        projEstablishBaseInfoQuery.eq(ProjEstablishBaseInfo::getClientId, clientId);
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoService.list(projEstablishBaseInfoQuery);
        if (CollectionUtil.isEmpty(projEstablishBaseInfoList)) {
            return null;
        }
        List<Long> projEstablishIdList = new ArrayList<>(projEstablishBaseInfoList.size());
        List<ProjEstablishBaseInfo> toUpdateProjEstablishBaseInfoList = new ArrayList<>(projEstablishBaseInfoList.size());
        for (ProjEstablishBaseInfo old : projEstablishBaseInfoList) {
            ProjEstablishBaseInfo toUpdate = new ProjEstablishBaseInfo();
            toUpdate.setId(old.getId());
            toUpdate.setBizDeptId(bizDeptId);
            toUpdate.setBizDeptLeaderId(bizDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(bizDivisionLeaderId);
            toUpdate.setProjSponsorUserId(sponsorId);
            toUpdate.setProjCosponsorUserIds(JSONUtil.toJsonStr(cosponsorIdList));
            toUpdateProjEstablishBaseInfoList.add(toUpdate);
            projEstablishIdList.add(old.getId());
        }
        projEstablishBaseInfoService.updateBatchById(toUpdateProjEstablishBaseInfoList);
        // 更新版本区
        LambdaQueryWrapper<ProjEstablishBaseInfoLib> projEstablishBaseInfoLibQuery = Wrappers.lambdaQuery();
        projEstablishBaseInfoLibQuery.in(ProjEstablishBaseInfoLib::getOriginId, projEstablishIdList);
        List<ProjEstablishBaseInfoLib> projEstablishBaseInfoLibList = projEstablishBaseInfoLibService.list(projEstablishBaseInfoLibQuery);
        if (CollectionUtil.isNotEmpty(projEstablishBaseInfoLibList)) {
            List<ProjEstablishBaseInfoLib> toUpdateProjEstablishBaseInfoLibList = new ArrayList<>(projEstablishBaseInfoLibList.size());
            for (ProjEstablishBaseInfoLib old : projEstablishBaseInfoLibList) {
                ProjEstablishBaseInfoLib toUpdate = new ProjEstablishBaseInfoLib();
                toUpdate.setId(old.getId());
                toUpdate.setBizDeptId(bizDeptId);
                toUpdate.setBizDeptLeaderId(bizDeptLeaderId);
                toUpdate.setBizDivisionLeaderId(bizDivisionLeaderId);
                toUpdate.setProjSponsorUserId(sponsorId);
                toUpdate.setProjCosponsorUserIds(JSONUtil.toJsonStr(cosponsorIdList));
                toUpdateProjEstablishBaseInfoLibList.add(toUpdate);
            }
            projEstablishBaseInfoLibService.updateBatchById(toUpdateProjEstablishBaseInfoLibList);
        }
        return projEstablishIdList;
    }

    private List<Long> doProjReview(List<Long> projEstablishIds, Long bizDeptId, Long bizDeptLeaderId, Long bizDivisionLeaderId, Long sponsorId, List<Long> cosponsorIdList) {
        if (CollectionUtil.isEmpty(projEstablishIds)) {
            return null;
        }
        // 更新编辑区
        LambdaQueryWrapper<ProjReviewBaseInfo> projReviewBaseInfoQuery = Wrappers.lambdaQuery();
        projReviewBaseInfoQuery.in(ProjReviewBaseInfo::getProjEstablishId, projEstablishIds);
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.list(projReviewBaseInfoQuery);
        if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
            return null;
        }
        List<Long> projReviewIdList = new ArrayList<>(projReviewBaseInfoList.size());
        List<ProjReviewBaseInfo> toUpdateProjReviewBaseInfoList = new ArrayList<>(projReviewBaseInfoList.size());
        for (ProjReviewBaseInfo old : projReviewBaseInfoList) {
            ProjReviewBaseInfo toUpdate = new ProjReviewBaseInfo();
            toUpdate.setId(old.getId());
            toUpdate.setBizDeptId(bizDeptId);
            toUpdate.setBizDeptLeaderId(bizDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(bizDivisionLeaderId);
            toUpdate.setProjSponsorUserId(sponsorId);
            toUpdate.setProjCosponsorUserIds(JSONUtil.toJsonStr(cosponsorIdList));
            toUpdateProjReviewBaseInfoList.add(toUpdate);
            projReviewIdList.add(old.getId());
        }
        projReviewBaseInfoService.updateBatchById(toUpdateProjReviewBaseInfoList);
        // 更新版本区
        LambdaQueryWrapper<ProjReviewBaseInfoLib> projReviewBaseInfoLibQuery = Wrappers.lambdaQuery();
        projReviewBaseInfoLibQuery.in(ProjReviewBaseInfoLib::getOriginId, projReviewIdList);
        List<ProjReviewBaseInfoLib> projReviewBaseInfoLibList = projReviewBaseInfoLibService.list(projReviewBaseInfoLibQuery);
        if (CollectionUtil.isNotEmpty(projReviewBaseInfoLibList)) {
            List<ProjReviewBaseInfoLib> toUpdateProjReviewBaseInfoLibList = new ArrayList<>(projReviewBaseInfoLibList.size());
            for (ProjReviewBaseInfoLib old : projReviewBaseInfoLibList) {
                ProjReviewBaseInfoLib toUpdate = new ProjReviewBaseInfoLib();
                toUpdate.setId(old.getId());
                toUpdate.setBizDeptId(bizDeptId);
                toUpdate.setBizDeptLeaderId(bizDeptLeaderId);
                toUpdate.setBizDivisionLeaderId(bizDivisionLeaderId);
                toUpdate.setProjSponsorUserId(sponsorId);
                toUpdate.setProjCosponsorUserIds(JSONUtil.toJsonStr(cosponsorIdList));
                toUpdateProjReviewBaseInfoLibList.add(toUpdate);
            }
            projReviewBaseInfoLibService.updateBatchById(toUpdateProjReviewBaseInfoLibList);
        }
        return projReviewIdList;
    }

    private List<Long> doContract(List<Long> projectReviewIds, Long bizDeptId, Long bizDeptLeaderId, Long bizDivisionLeaderId, Long sponsorId, List<Long> cosponsorIdList) {
        if (CollectionUtil.isEmpty(projectReviewIds)) {
            return null;
        }
        // 更新编辑区
        LambdaQueryWrapper<ContractBaseInfo> contractBaseInfoQuery = Wrappers.lambdaQuery();
        contractBaseInfoQuery.in(ContractBaseInfo::getProjReviewId, projectReviewIds);
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractBaseInfoQuery);
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return null;
        }
        List<Long> contractIdList = new ArrayList<>(contractBaseInfoList.size());
        List<ContractBaseInfo> toUpdateContractBaseInfoList = new ArrayList<>(contractBaseInfoList.size());
        for (ContractBaseInfo old : contractBaseInfoList) {
            ContractBaseInfo toUpdate = new ContractBaseInfo();
            toUpdate.setId(old.getId());
            toUpdate.setBizDeptId(bizDeptId);
            toUpdate.setBizDeptLeaderId(bizDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(bizDivisionLeaderId);
            toUpdate.setProjSponsorUserId(sponsorId);
            toUpdate.setProjCosponsorUserIds(JSONUtil.toJsonStr(cosponsorIdList));
            toUpdateContractBaseInfoList.add(toUpdate);
            contractIdList.add(old.getId());
        }
        contractBaseInfoService.updateBatchById(toUpdateContractBaseInfoList);
        // 更新版本区
        LambdaQueryWrapper<ContractBaseInfoLib> contractBaseInfoLibQuery = Wrappers.lambdaQuery();
        contractBaseInfoLibQuery.in(ContractBaseInfoLib::getOriginId, contractIdList);
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibService.list(contractBaseInfoLibQuery);
        if (CollectionUtil.isNotEmpty(contractBaseInfoLibList)) {
            List<ContractBaseInfoLib> toUpdateContractBaseInfoLibList = new ArrayList<>(contractBaseInfoLibList.size());
            for (ContractBaseInfoLib old : contractBaseInfoLibList) {
                ContractBaseInfoLib toUpdate = new ContractBaseInfoLib();
                toUpdate.setId(old.getId());
                toUpdate.setBizDeptId(bizDeptId);
                toUpdate.setBizDeptLeaderId(bizDeptLeaderId);
                toUpdate.setBizDivisionLeaderId(bizDivisionLeaderId);
                toUpdate.setProjSponsorUserId(sponsorId);
                toUpdate.setProjCosponsorUserIds(JSONUtil.toJsonStr(cosponsorIdList));
                toUpdateContractBaseInfoLibList.add(toUpdate);
            }
            contractBaseInfoLibService.updateBatchById(toUpdateContractBaseInfoLibList);
        }
        return contractIdList;
    }

    public void importFtpInterestData() {
        List<String> targetContractCodes = ListUtil.of("浙商租【2021】租字第(A-0006)号","浙商租【2021】租字第(JLG-0001)号","浙商租【2022】租字第(C-0007)号","浙商租【2022】租字第(GCJX-C-0001)号","浙商租【2023】租字第(A-0091)号","浙商租【2023】租字第(A-0096)号","浙商租【2023】租字第(A-0097)号","浙商租【2023】租字第(A-0143)号","浙商租【2023】租字第(A-0148)号","浙商租【2023】租字第(A-0188)号","浙商租【2023】租字第(A-0189)号","浙商租【2023】租字第(A-0216)号","浙商租【2023】租字第(C-0001)号","浙商租【2024】租字第(A-0033)号","浙商租【2024】租字第(A-0090)号","浙商租【2024】租字第(A-0097)号","浙商租【2024】租字第(C-0002)号","浙商租【2024】租字第(C-0003)号","浙商租【2024】租字第(C-0004)号","浙商租【2024】租字第(C-0010)号","浙商租【2025】租字第(C-0003)号");
        XSSFWorkbook workbook;
        try {
            IOUtils.setByteArrayMaxOverride(121842406);
            workbook = new XSSFWorkbook(FileUtil.getInputStream("/Users/dingqi/Downloads/预算数据核对/融资租赁业务合同情况总表20250826_副本.xlsx"));
        } catch (IOException e) {
            log.error("解析Excel发生未知异常", e);
            return;
        }
        // 业务情况汇总表
        XSSFSheet sheet = workbook.getSheet("业务合同情况汇总表");
        Map<Integer, MyDataHelper> map = new HashMap<>(512);
        // 遍历超链接整理数据
        log.info("整理分组数据 - 开始...");
        for (XSSFHyperlink xssfHyperlink : sheet.getHyperlinkList()) {
            // 按照行号分组
            int rowIndex = Integer.parseInt(xssfHyperlink.getCellRef().replace("G", "")) - 1;
            // 找到同行的合同编号
            String contractCode = sheet.getRow(rowIndex).getCell(7).getStringCellValue();
            // 找到同行的借据编号
            String receiptCode = sheet.getRow(rowIndex).getCell(8).getStringCellValue();
            // 放入map备用
            map.put(rowIndex, new MyDataHelper(contractCode, receiptCode, xssfHyperlink.getAddress().replace("'", "")));
        }
        log.info("整理分组数据 - 结束...分组结果: {}", JSONUtil.toJsonStr(map));
        // 循环处理每一个合同
        for (Map.Entry<Integer, MyDataHelper> entry : map.entrySet()) {
            String contractCode = entry.getValue().getContractCode();
            if (CollectionUtil.isNotEmpty(targetContractCodes) && !targetContractCodes.contains(contractCode)) {
                log.info("不在指定合同编号集合内，无需处理");
                continue;
            }
            String receiptCode = entry.getValue().getReceiptCode();
            String targetSheetName = entry.getValue().getHyperLink().split("!")[0];
            log.info(">>>>>>>>> {} - 开始", targetSheetName);
            log.info("合同编号: {}", contractCode);
            log.info("借据编号: {}", receiptCode);
            Sheet targetSheet = workbook.getSheet(targetSheetName);
            if (Objects.nonNull(targetSheet)) {
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        doImport(contractCode, receiptCode, targetSheet);
                    } catch (Exception e) {
                        transactionStatus.setRollbackOnly();
                        log.error("导入FTP计息历史数据发生异常[contractCode:{}, receiptCode:{}]", contractCode, receiptCode, e);
                    }
                });
            }
            log.info(">>>>>>>>> {} - 结束", targetSheetName);
        }
    }

    private void doImport(String contractCode, String receiptCode, Sheet sheet) {
        // 查询合同信息
        LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
        contractQuery.eq(ContractBaseInfo::getContractCode, contractCode);
        contractQuery.in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()));
        contractQuery.last(StringUtil.mysqlLimitOne());
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getOne(contractQuery);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同信息不存在"));
        // 确定借据信息
        ContractReceipt contractReceipt;
        if (StrUtil.isNotBlank(receiptCode)) {
            LambdaQueryWrapper<ContractReceipt> query = Wrappers.lambdaQuery();
            query.eq(ContractReceipt::getReceiptCode, receiptCode);
            query.last(StringUtil.mysqlLimitOne());
            contractReceipt = contractReceiptService.getOne(query);
        } else {
            List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractBaseInfo.getId());
            Assert.notEmpty(contractReceiptList, () -> MithrasException.newException("借据信息不存在"));
            Assert.isTrue(contractReceiptList.size() == 1, () -> MithrasException.newException("存在多个借据"));
            contractReceipt = contractReceiptList.get(0);
        }
        Assert.notNull(contractReceipt, () -> MithrasException.newException("借据信息不存在"));
        // 客户信息
        Client client = clientService.getById(contractBaseInfo.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户信息不存在"));
        // FTP计息数据初始化
        FtpInterestBaseInfo ftpInterestBaseInfo = SpringUtil.getBean(FtpInterestBaseInfoService.class).getOneByReceiptId(contractReceipt.getId());
        if (Objects.isNull(ftpInterestBaseInfo)) {
            ftpInterestBaseInfo = new FtpInterestBaseInfo();
            ftpInterestBaseInfo.setContractId(contractBaseInfo.getId());
            ftpInterestBaseInfo.setContractCode(contractBaseInfo.getContractCode());
            ftpInterestBaseInfo.setReceiptId(contractReceipt.getId());
            ftpInterestBaseInfo.setReceiptCode(contractReceipt.getReceiptCode());
            ftpInterestBaseInfo.setClientId(client.getId());
            ftpInterestBaseInfo.setClientName(client.getClientName());
            ftpInterestBaseInfo.setProjReviewId(contractBaseInfo.getProjReviewId());
            ftpInterestBaseInfo.setProjName(StrUtil.isBlank(contractBaseInfo.getProjName()) ? "" : contractBaseInfo.getProjName());
            ftpInterestBaseInfo.setBizDeptId(contractBaseInfo.getBizDeptId());
            ftpInterestBaseInfo.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
            ftpInterestBaseInfo.setFinish(Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
            ftpInterestBaseInfo.setTotalInterestAmount(0L);
            ftpInterestBaseInfoService.save(ftpInterestBaseInfo);
        }
        // FTP计息明细数据初始化
        List<FtpInterestDetailRecord> detailList = new LinkedList<>();
        // 判断是否需要处理票据
        boolean existPJ = false;
        Cell cell = sheet.getRow(0).getCell(16);
        if (Objects.nonNull(cell) && cell.getCellType() == CellType.STRING) {
            existPJ = cell.getStringCellValue().contains("票据");
        }
        // 期初余额单做
        Row beginRow = sheet.getRow(3);
        FtpInterestDetailRecord beginOfTermBalanceRecord = new FtpInterestDetailRecord();
        beginOfTermBalanceRecord.setFtpInterestId(ftpInterestBaseInfo.getId());
        beginOfTermBalanceRecord.setItemText("期初余额");
        beginOfTermBalanceRecord.setInterestDate(LocalDate.of(2025, 1, 1));
        beginOfTermBalanceRecord.setCashOccupy(Util.mithrasLongDecimalTwo((long) (Optional.ofNullable(beginRow.getCell(3)).map(Cell::getNumericCellValue).orElse(0D) * 10000)));
        beginOfTermBalanceRecord.setCashOut(0L);
        beginOfTermBalanceRecord.setCashIn(0L);
        beginOfTermBalanceRecord.setCashInterest(0L);
        beginOfTermBalanceRecord.setCashFtp(0);
        try {
            beginOfTermBalanceRecord.setBillOccupy(Util.mithrasLongDecimalTwo((long) (Optional.ofNullable(beginRow.getCell(13)).map(Cell::getNumericCellValue).orElse(0D) * 10000)));
        } catch (Exception e) {
            beginOfTermBalanceRecord.setBillOccupy(0L);
        }
        beginOfTermBalanceRecord.setBillOut(0L);
        beginOfTermBalanceRecord.setBillIn(0L);
        beginOfTermBalanceRecord.setBillInterest(0L);
        beginOfTermBalanceRecord.setBillFtp(0);
        beginOfTermBalanceRecord.setTotalInterestThisYear(0L);
        detailList.add(beginOfTermBalanceRecord);
        // 遍历初始化20250101到20250826的数据
        // 跳过不需要的行
        List<Integer> ignoreRowIndex = ListUtil.of(35, 64, 65, 97, 128, 160, 191, 223);
        long total = 0L;
        for (int i = 4; i <= 249; i++) {
            if (ignoreRowIndex.contains(i)) {
                continue;
            }
            Row row = sheet.getRow(i);
            // 日期
            LocalDateTime A = row.getCell(0).getLocalDateTimeCellValue();
            // 现金支出
            Double B = Optional.ofNullable(row.getCell(1)).map(Cell::getNumericCellValue).orElse(0D);
            // 现金收入
            Double C = Optional.ofNullable(row.getCell(2)).map(Cell::getNumericCellValue).orElse(0D);
            // 现金占用
            Double D = Optional.ofNullable(row.getCell(3)).map(Cell::getNumericCellValue).orElse(0D);
            // 现金计息
            Double F = Optional.ofNullable(row.getCell(5)).map(Cell::getNumericCellValue).orElse(0D);
            // 现金FTP价格
            Double H = Optional.ofNullable(row.getCell(6)).map(Cell::getNumericCellValue).orElse(0D);
            // 现金调整FTP价格
            Double H_1 = Optional.ofNullable(row.getCell(7)).map(Cell::getNumericCellValue).orElse(0D);
            // 是否逾期
            String I = row.getCell(8).getStringCellValue();
            // 票据支出
            Double L = 0D;
            // 票据收入
            Double M = 0D;
            // 票据占用
            Double N = 0D;
            // 票据计息
            Double P = 0D;
            // 票据FTP价格
            Double Q = 0D;
            if (existPJ) {
                // 票据支出
                L = Optional.ofNullable(row.getCell(11)).map(Cell::getNumericCellValue).orElse(0D);
                // 票据收入
                M = Optional.ofNullable(row.getCell(12)).map(Cell::getNumericCellValue).orElse(0D);
                // 票据占用
                N = Optional.ofNullable(row.getCell(13)).map(Cell::getNumericCellValue).orElse(0D);
                // 票据计息
                P = Optional.ofNullable(row.getCell(15)).map(Cell::getNumericCellValue).orElse(0D);
                // 票据FTP价格
                Q = Optional.ofNullable(row.getCell(16)).map(Cell::getNumericCellValue).orElse(0D);
            }
            // 封装对象
            FtpInterestDetailRecord ftpInterestDetailRecord = new FtpInterestDetailRecord();
            ftpInterestDetailRecord.setFtpInterestId(ftpInterestBaseInfo.getId());
            ftpInterestDetailRecord.setInterestDate(A.toLocalDate());
            ftpInterestDetailRecord.setItemText(LocalDateTimeUtil.format(ftpInterestDetailRecord.getInterestDate(), DatePattern.NORM_DATE_PATTERN));
            ftpInterestDetailRecord.setCashOut(Util.mithrasLongDecimalTwo((long) (B * 10000)));
            ftpInterestDetailRecord.setCashIn(Util.mithrasLongDecimalTwo((long) (C * 10000)));
            ftpInterestDetailRecord.setCashOccupy(Util.mithrasLongDecimalTwo((long) (D * 10000)));
            ftpInterestDetailRecord.setCashInterest(Util.mithrasLongDecimalTwo((long) (F * 10000)));
            total += ftpInterestDetailRecord.getCashInterest();
            ftpInterestDetailRecord.setCashFtp(BigDecimal.valueOf(H).multiply(BigDecimal.valueOf(1000000)).intValue());
            ftpInterestDetailRecord.setBillFtp(BigDecimal.valueOf(Q).multiply(BigDecimal.valueOf(1000000)).intValue());
            ftpInterestDetailRecord.setBillOut((long) (L * 10000));
            ftpInterestDetailRecord.setBillIn((long) (M * 10000));
            ftpInterestDetailRecord.setBillOccupy((long) (N * 10000));
            ftpInterestDetailRecord.setBillInterest((long) (P * 10000));
            total += ftpInterestDetailRecord.getBillInterest();
            ftpInterestDetailRecord.setTotalInterestThisYear(total);
            ftpInterestDetailRecord.setIsOverdue("是".equals(I) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
            if (Objects.equals(ftpInterestDetailRecord.getIsOverdue(), YesOrNoNumberEnum.YES.getCode())) {
                Integer adjustCashFtp = BigDecimal.valueOf(H_1).multiply(BigDecimal.valueOf(1000000)).intValue();
                ftpInterestDetailRecord.setFtpOverdueAdjust(adjustCashFtp - ftpInterestDetailRecord.getCashFtp());
            }
            detailList.add(ftpInterestDetailRecord);
        }
        if (CollectionUtil.isNotEmpty(detailList)) {
            ftpInterestDetailRecordService.saveBatch(detailList);
            // 变更冗余字段
            FtpInterestDetailRecord lastRecord = detailList.get(detailList.size() - 1);
            ftpInterestBaseInfo.setLastUpdateDate(lastRecord.getInterestDate());
            ftpInterestBaseInfo.setLastCashFtp(lastRecord.getCashFtp());
            ftpInterestBaseInfo.setTotalInterestAmount(lastRecord.getTotalInterestThisYear());
            ftpInterestBaseInfoService.updateById(ftpInterestBaseInfo);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class MyDataHelper {
        private final String contractCode;
        private final String receiptCode;
        private final String hyperLink;
    }
}
