package cn.zswltech.mithras.service.service.contract.impl;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.ContractCanChangeRSP;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractConstraintREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.*;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.StructuredInterest;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualImportREQ;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingAocPriceRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingFactoringPriceRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingLeasePriceRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.service.convert.contract.ContractBaseInfoConverter;
import cn.zswltech.mithras.service.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.service.convert.contract.ContractTenantryConvert;
import cn.zswltech.mithras.service.convert.projpricing.ProjPricingPriceConverter;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseAdjustEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.payment.domain.enums.LendingMaterialType;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.contract.mapper.dto.ContractListSelectDTO;
import cn.zswltech.mithras.contract.mapper.dto.ContractRentLastTimeDTO;
import cn.zswltech.mithras.contract.mapper.dto.OcContractListDto;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.message.NoticeMessageBody;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.contract.mapper.query.ContractPrincipalQuery;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.contract.overdue.domain.acl.*;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.ContractPriceChangeEvent;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.customer.application.bo.ClientBusinessHistoryBO;
import cn.zswltech.mithras.contract.application.dto.ContractPrincipalBO;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.customer.application.client.ClientBusinessHistoryService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.lib.LibCommonConvert;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractVersionService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.lib.contractcp.ContractCollectionPaymentService;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.customer.service.model.MithrasShareholderInfo;
import cn.zswltech.mithras.service.util.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;


/**
 * @author vico
 * 合同基本信息表
 * @date 2022-08-12
 */
@Service
@Slf4j
public class ContractBaseInfoServiceImpl extends ServiceImpl<ContractBaseInfoMapper, ContractBaseInfo> implements ContractBaseInfoService, LibCommonConvert<ContractBaseInfo, ContractBaseInfoDetailRSP> {

    @Resource
    private ContractVersionService contractVersionService;

    @Autowired
    private ProjReviewBaseInfoLibService reviewBaseInfoLibService;

    @Resource
    private ProjPricingBaseInfoLibService projPricingBaseInfoLibService;

    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;

    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;

    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;

    @Autowired
    private ProjReviewPriceService projReviewPriceService;

    @Resource
    private ProjPricingPriceService pricingPriceService;

    @Resource
    private ContractBaseInfoConverter baseInfoConverter;

    @Resource
    private ContractPriceConverter priceConverter;
    @Resource
    private ProjPricingPriceConverter projPricingPriceConverter;

    @Lazy
    @Autowired
    private ContractPriceService contractPriceService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private ContractTenantryService contractTenantryService;

    @Resource
    private ContractGuarantorService guarantorService;

    @Resource
    private ContractMortgageService mortgageService;

    @Resource
    private ContractPledgeService pledgeService;

    @Autowired
    private ContractCollectionPaymentService contractCollectionPaymentService;

    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;

    @Resource
    private MessageService messageService;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Resource
    private MessageConver messageConvert;

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;

    @Resource
    private FileService fileService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListMapper materialsListMapper;

    @Resource
    private ClientService clientService;
    @Resource
    private ContractService contractService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractLeaseItemService contractLeaseItemService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private ClientBusinessHistoryService clientBusinessHistoryService;
    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Autowired
    private HttpServletResponse response;

    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;

    private final LocalDateTime stockContractDate = LocalDateTime.of(2023, 10, 12, 23, 59);

    private final static int YEAR_DAY = 360;


    @Override
    public ContractBaseInfo getValidOneByContractCode(String contractCode) {
        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfo::getContractCode, contractCode);
        query.notIn(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()));
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Override
    public List<ContractBaseInfo> listAllStartRent() {
        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name());
        return this.list(query);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public synchronized ContractBaseInfoAddRSP add(ContractBaseInfoAddREQ req) {
        if (ProjectBizType.ZL.name().equals(req.getBizType()) || ProjectBizType.ZZ.name().equals(req.getBizType())) {
            Assert.notBlank(req.getLeaseType(), () -> MithrasException.newException("租赁类型不能为空"));
        }
        /*if (isNotNull(baseMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                .eq(ContractBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("该项目存在未关闭的审批");
        }*/
        //拉取基本信息
        ProjReviewBaseInfo projReviewBaseInfo = reviewBaseInfoLibService.getOldEdition(req.getProjReviewId());
        ProjPricingBaseInfo projPricingBaseInfo = null;
        ProjPricingBaseInfo pricingBaseInfo = projPricingBaseInfoService.getPricingByReview(projReviewBaseInfo);
        if (ObjectUtil.isEmpty(pricingBaseInfo)) {
            throw new MithrasException("无生效的项目定价，不可创建合同");
        }
        if (pricingBaseInfo != null) {
            projPricingBaseInfo = projPricingBaseInfoLibService.getOldEdition(pricingBaseInfo.getId());
        }
        if (!Objects.equals(RecordStatus.TAKE_EFFECT.name(), projReviewBaseInfo.getProjReviewStatus()) || projPricingBaseInfo == null || !Objects.equals(RecordStatus.TAKE_EFFECT.name(), projPricingBaseInfo.getProjPricingStatus())) {
            throw new MithrasException("该项目未完成项目评审或项目定价，无法创建合同！");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(projReviewBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        //新需求将客户编号整理到主流程了，客户编号为空不允许合同创建
        Client client = clientService.getById(pricingBaseInfo.getClientId());
        if (Objects.nonNull(client) && CharSequenceUtil.isEmpty(client.getClientCode())) {
            throw new MithrasException(String.format("客户【%s】的客户编号为空，请联系信科岗至客商系统创建该客户。", client.getClientName()));
        }
        //项目生效且不在流程中即可
        if (ObjectUtil.isNotNull(reviewBaseInfoService.findRelatedProcess(projReviewBaseInfo.getId())) || ObjectUtil.isNotNull(projPricingBaseInfoService.findRelatedProcess(projPricingBaseInfo.getId()))) {
            throw new MithrasException("该项目存在未提交或审批中的评审流程，不可提交合同流程！");
        }

        //拉起报价信息
        // ProjReviewPriceDetailRSP priceDetail = projReviewPriceService.oldDetail(req.getProjReviewId());

        //
        ProjPricingPriceDetailRSP priceDetail = pricingPriceService.oldDetail(pricingBaseInfo.getId());
        if(ObjectUtil.isEmpty(priceDetail.getApprovedAmount())) {
            throw new MithrasException("项目批复金额为空，请联系管理员添加");
        }

        //计算本项目下已有合同授信金额
        ContractBaseInfo info = baseInfoConverter.reviewToContract(pricingBaseInfo);
        info.setRemark("");

        // 获取事务定义
        DefaultTransactionDefinition df = new DefaultTransactionDefinition();
        // 设置事务隔离级别，开启新的数据
        df.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 获取事务状态,相当于开启事务
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(df);
        try {
            info.setProjItem(pricingBaseInfo.getProjectClassify());
            info.setCreateTime(null);
            info.setUpdateTime(null);
            info.setUpdateBy(null);
            info.setLeaseType(req.getLeaseType());
            info.setProjReviewId(projReviewBaseInfo.getId());
            info.setContractStatus(ContractStatus.NEW.name());
            info.setContractProcessStatus(ContractProcessStatusEnum.NEW_UNCOMMIT.name());
            //项目授信金额-sum合同金额 + if（额度可循环，sum（已核销的本金、首期租金），0
            if (ObjectUtil.isNotEmpty(priceDetail.getAocPriceDetailRSP())) {
                info.setRemainAvailableQuota(LongUtil.null2zero(priceDetail.getAocPriceDetailRSP().getApprovedAmount()) - getRemainAvailableQuota(null, priceDetail.getAocPriceDetailRSP().getCreditAmountLoop(), selectListByProjId(info.getProjReviewId())));
                info.setCreditAmountLoop(priceDetail.getAocPriceDetailRSP().getCreditAmountLoop());
                info.setApplyCreditAmount(priceDetail.getLeasePriceDetailRSP().getApprovedAmount());
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getFactoringPriceDetailRSP())) {
                info.setRemainAvailableQuota(LongUtil.null2zero(priceDetail.getFactoringPriceDetailRSP().getApprovedAmount()) - getRemainAvailableQuota(null, priceDetail.getFactoringPriceDetailRSP().getCreditAmountLoop(), selectListByProjId(info.getProjReviewId())));
                info.setCreditAmountLoop(priceDetail.getFactoringPriceDetailRSP().getCreditAmountLoop());
                info.setApplyCreditAmount(priceDetail.getLeasePriceDetailRSP().getApprovedAmount());
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getLeasePriceDetailRSP())) {
                info.setRemainAvailableQuota(LongUtil.null2zero(priceDetail.getLeasePriceDetailRSP().getApprovedAmount()) - getRemainAvailableQuota(null, priceDetail.getLeasePriceDetailRSP().getCreditAmountLoop(), selectListByProjId(info.getProjReviewId())));
                info.setApplyCreditAmount(priceDetail.getLeasePriceDetailRSP().getApprovedAmount());
                info.setCreditAmountLoop(priceDetail.getLeasePriceDetailRSP().getCreditAmountLoop());
            }
           /* if (info.getRemainAvailableQuota() <= 0) {
                throw new MithrasException("项目可用金额为0");
            }*/
            //合同编码
            Integer contractYear = Integer.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
            LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
            query.eq(ContractBaseInfo::getBizType, req.getBizType());
            if (StrUtil.isNotBlank(req.getLeaseType()) && Objects.equals(req.getBizType(), ProjectBizType.ZL.name())) {
                query.eq(ContractBaseInfo::getLeaseType, req.getLeaseType());
            }
            query.isNotNull(ContractBaseInfo::getContractCode);
            query.eq(ContractBaseInfo::getContractYear, contractYear);
            query.orderByDesc(ContractBaseInfo::getSequence);
            query.last("limit 1");
            ContractBaseInfo contractBaseInfo = this.getOne(query);
            info.setContractYear(contractYear);
            info.setSequence(ObjectUtil.isEmpty(contractBaseInfo) ? 1 : contractBaseInfo.getSequence() + 1);
            info.setContractCode(ContractUtil.generateMainCode(info.getSequence(), ProjectBizType.of(req.getBizType()), req.getLeaseType()));
            info.setConsultingContractCode(ContractUtil.generateSubContractCode(ContractModelEnum.CONSULT, info.getContractCode(), 1).get(0));
//            info.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
            //更新业务部门负责人，业务分管领导
            OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(info.getBizDeptId());
            if (ObjectUtil.isNotEmpty(bizOrgDO)) {
                info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
                info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
            }
            info.setId(null);
            baseMapper.insert(info);
            // 拉取报价方案转存
            if (ObjectUtil.isNotEmpty(priceDetail.getAocPriceDetailRSP())) {
                ProjPricingAocPriceRSP aocPriceDetailRSP = priceDetail.getAocPriceDetailRSP();
                //ContractAocPrice aocPrice = priceConverter.esAocRspToEntity(aocPriceDetailRSP);
                ContractAocPrice aocPrice = BeanUtil.copyProperties(aocPriceDetailRSP, ContractAocPrice.class);
                aocPrice.setRepayCalcType(aocPriceDetailRSP.getRentalCalcType());
                aocPrice.setContractAmount(aocPriceDetailRSP.getApplyCreditAmount());
                aocPrice.setContractId(info.getId());
                //合同金额 == 剩余授信金额
                aocPrice.setContractAmount(info.getRemainAvailableQuota());
                contractPriceService.add(aocPrice);
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getFactoringPriceDetailRSP())) {
                ProjPricingFactoringPriceRSP factoringPriceDetailRSP = priceDetail.getFactoringPriceDetailRSP();
                //ContractFactoringPrice factoringPrice = priceConverter.esFactoringRspToEntity(factoringPriceDetailRSP);
                ContractFactoringPrice factoringPrice = BeanUtil.copyProperties(factoringPriceDetailRSP, ContractFactoringPrice.class);
                factoringPrice.setRepayCalcType(factoringPriceDetailRSP.getRentalCalcType());
                factoringPrice.setContractAmount(factoringPriceDetailRSP.getApplyCreditAmount());
                factoringPrice.setContractId(info.getId());
                // 合同金额 = 剩余授信金额
                factoringPrice.setContractAmount(info.getRemainAvailableQuota());
                contractPriceService.add(factoringPrice);
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getLeasePriceDetailRSP())) {
                ProjPricingLeasePriceRSP leasePriceDetailRSP = priceDetail.getLeasePriceDetailRSP();
                //ContractLeasePrice leasePrice = priceConverter.esLeaseRspToEntity(leasePriceDetailRSP);
                ContractLeasePrice leasePrice = BeanUtil.copyProperties(leasePriceDetailRSP, ContractLeasePrice.class);
                leasePrice.setContractId(info.getId());
                //合同金额 == 剩余授信金额
                leasePrice.setApplyCreditAmount(info.getRemainAvailableQuota());
                //保存项目相关数据
                leasePrice.setProjCreditAmount(leasePriceDetailRSP.getApprovedAmount());
                leasePrice.setProjEarnestMoney(leasePriceDetailRSP.getEarnestMoney());
                leasePrice.setProjIrrPercent(leasePriceDetailRSP.getIrrPercent());
                leasePrice.setProjDownPayment(leasePriceDetailRSP.getDownPayment());
                leasePrice.setProjConsultingFee(leasePriceDetailRSP.getConsultingFee());
                leasePrice.setProjLeaseMonthCount(leasePriceDetailRSP.getLeaseMonthCount());
                //构建结构化利息
                if (ObjectUtil.isNotEmpty(leasePriceDetailRSP.getFirstInstallmentInterest())) {
                    StructuredInterest structuredInterest = new StructuredInterest();
                    structuredInterest.setPhase(YesOrNoNumberEnum.NO.getCode());
                    structuredInterest.setAmount(leasePriceDetailRSP.getFirstInstallmentInterest());
                    leasePrice.setStructuredInterest(JSONUtil.toJsonStr(CollectionUtil.toList(structuredInterest)));
                }
                contractPriceService.add(leasePrice);
            }
            //保存承租人/债权人/债务人信息（债权人/债务人复用承租人表）
            if (ProjectBizType.ZL.name().equals(pricingBaseInfo.getBizType()) || ProjectBizType.ZZ.name().equals(pricingBaseInfo.getBizType())) {
                contractTenantryService.add(info.getId(), JSON.parseArray(pricingBaseInfo.getLesseeInfo(), ClientInfo.class));
            } else {
                this.doContractTenantryBLZR(info, pricingBaseInfo);
            }
            //保存担保人
            guarantorService.add(info.getId(), JSON.parseArray(pricingBaseInfo.getGuaranteeInfo(), ClientInfo.class));
            //保存抵押人
            mortgageService.add(info.getId(), JSON.parseArray(pricingBaseInfo.getMortgagorInfo(), ClientInfo.class));
            //保存质押人
            pledgeService.add(info.getId(), JSON.parseArray(pricingBaseInfo.getPledgorInfo(), ClientInfo.class));
            // 保存租赁物清单和租赁物文本（仅租赁回租合同）
            if (Objects.equals(req.getBizType(), ProjectBizType.ZL.name()) && Objects.equals(req.getLeaseType(), LeaseType.hui_zu.name())) {
                LeaseItemInfo leaseItemInfo = leaseItemInfoService.getNewestOne(projReviewBaseInfo.getId());
                if (Objects.nonNull(leaseItemInfo)) {
                    contractLeaseItemService.initLeaseItem(info.getId(), projReviewBaseInfo.getId(), leaseItemInfo);
//                    contractLeaseItemService.copyLeaseItemFile(info.getId(), leaseItemInfo);
                }
            }
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("contract save base error.[{}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("保存基本信息失败");
        } finally {
            dataSourceTransactionManager.commit(transaction);
        }
        //通知变更
        ApplicationContextUtil.getApplicationContext().publishEvent(new ContractPriceChangeEvent(this, info.getId()));
        return new ContractBaseInfoAddRSP().setId(info.getId()).setBizType(info.getBizType());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void modify(ContractBaseInfoModifyREQ req) {
        //saveCheck(req.getId());
        /*---------------------参数转换----------------------*/
        ContractBaseInfo info = baseInfoConverter.modifyREQtoEntity(req);
        //防止code被更新
        info.setProjCode(null);
        if (!StrUtil.isBlank(req.getLeaseType()) && StrUtil.isBlank(req.getContractCode())) {
            ContractBaseInfo contractBaseInfo = baseMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getProjReviewId, req.getProjReviewId())
                    .isNotNull(ContractBaseInfo::getContractCode)
                    .eq(ContractBaseInfo::getContractYear, Integer.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))))
                    .orderByDesc(ContractBaseInfo::getSequence)
                    .last("limit 1")
            );
            info.setSequence(ObjectUtil.isEmpty(contractBaseInfo) ? 1 : contractBaseInfo.getSequence() + 1);
            info.setContractCode(ContractUtil.generateMainCode(info.getSequence(), ProjectBizType.of(req.getBizType()), req.getLeaseType()));
            info.setConsultingContractCode(ContractUtil.generateSubContractCode(ContractModelEnum.CONSULT, info.getContractCode(), 1).get(0));
        }
        //更新业务部门负责人，业务分管领导
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(info.getBizDeptId());
        if (ObjectUtil.isNotEmpty(bizOrgDO)) {
            info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
            info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
        }
        baseMapper.updateAnnotationIncludeNullById(info);
        //更改流程状态
        //recordStatus(req.getId());
    }

    @Override
    public void modifyLeaseItem(ContractBaseInfoModifyREQ req) {
        ContractBaseInfo info = this.getById(req.getId());
        if (ObjectUtil.isEmpty(info)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<ContractBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ContractBaseInfo::getLeaseItemTypes, JSONUtil.toJsonStr(req.getLeaseItemTypes()));
        updateWrapper.eq(ContractBaseInfo::getId, req.getId());
        this.update(updateWrapper);
    }

    @Override
    public void modifyLeaseItemByProjReviewId(Long projReviewId, String leaseItems) {
        if (ObjectUtil.isEmpty(projReviewId)) {
            return;
        }
        LambdaUpdateWrapper<ContractBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ContractBaseInfo::getLeaseItemTypes, leaseItems);
        updateWrapper.eq(ContractBaseInfo::getProjReviewId, projReviewId);
        this.update(updateWrapper);
    }

    //更新部门领导信息
    @Transactional(rollbackFor = Throwable.class)
    public void renewLeader(Long contractId) {
        ContractBaseInfo contractBaseInfo = baseMapper.selectById(contractId);
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            return;
        }
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(contractBaseInfo.getBizDeptId());
        if (isNotNull(bizOrgDO)) {
            LambdaUpdateWrapper<ContractBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ContractBaseInfo::getId, contractBaseInfo.getId());
            updateWrapper.set(ContractBaseInfo::getBizDeptLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
            updateWrapper.set(ContractBaseInfo::getBizDivisionLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
            this.baseMapper.update(null, updateWrapper);
        }
    }

    /**
     * 更新支付相关信息
     **/
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updatePay(Long id, Long paymentPlanAmount, LocalDate paymentPlanDate) {
        ContractBaseInfo contractBaseInfo = new ContractBaseInfo();
        contractBaseInfo.setId(id);
        if (!ObjectUtil.isEmpty(paymentPlanAmount)) {
            contractBaseInfo.setApplyCreditAmount(paymentPlanAmount);
        }
        if (!ObjectUtil.isEmpty(paymentPlanDate)) {
            contractBaseInfo.setPaymentPlanDate(paymentPlanDate);
        }
        baseMapper.updateById(contractBaseInfo);
    }

    @Override
    public Page<ContractBaseInfoListRSP> list(ContractBaseInfoListREQ req) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        ContractListSelectDTO dto = baseInfoConverter.listREQtoSelectDTO(req);
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Page<ContractBaseInfo> pageData = baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        List<Long> contractids = pageData.getRecords().stream().map(ContractBaseInfo::getId).collect(Collectors.toList());

        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<ContractBaseInfoListRSP> resPage = new ArrayList<>(pageData.getRecords().size());
        for (ContractBaseInfo record : pageData.getRecords()) {
            ContractBaseInfoListRSP rsp = baseInfoConverter.entityToListRsp(record);
            rsp.setApplyCreditAmount(record.getApplyCreditAmount());
            rsp.setCreateTime(record.getCreateTime().format(dateTimeFormatter));
            rsp.setUpdateTime(record.getUpdateTime().format(dateTimeFormatter));
            rsp.setIsSigned(record.getIsSigned());
            String twoStatue = Optional.ofNullable(ContractChangeTypeEnum.of(record.getContractProcessChangeStatus()))
                    .map(ContractChangeTypeEnum::display)
                    .orElse(Optional.ofNullable(ContractAdvanceEnum.of(record.getContractProcessChangeStatus()))
                            .map(ContractAdvanceEnum::display)
                            .orElse(""));
            ContractProcessStatusEnum cps = ContractProcessStatusEnum.of(record.getContractProcessStatus());
            rsp.setContractProcessName(Optional.ofNullable(cps).map(ContractProcessStatusEnum::display).orElse("-"));
            // 退抵流程的状态不受twoStatue影响
            if(!CharSequenceUtil.equalsAny(rsp.getContractProcessStatus(),
                    ContractProcessStatusEnum.RETREAT_UNCOMIIT.name(),ContractProcessStatusEnum.RETREAT_COMMIT.name(),
                    ContractProcessStatusEnum.RETREAT_CANCEL.name(),ContractProcessStatusEnum.RETREAT_PASS.name(),ContractProcessStatusEnum.RETREAT_REJECT.name())){
                if (ObjectUtil.isNotEmpty(twoStatue)) {
                    rsp.setContractProcessName(String.format("%s(%s)", rsp.getContractProcessName(), twoStatue));
                }
            }
            if (ObjectUtil.isNotEmpty(contractids) && ObjectUtil.isEmpty(rsp.getApplyCreditAmount())) {
                ContractPriceDetailRSP priceDetailRSP = contractPriceService.detail(new ContractPriceDetailREQ(rsp.getId()));
                if (ObjectUtil.isNotNull(ObjectUtil.isNotNull(priceDetailRSP))) {
                    rsp.setApplyCreditAmount(priceDetailRSP.getApplyCreditAmount());
                }
            }
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                sysUserIds.addAll(rsp.getProjCosponsorUserIds());
            }
            sysUserIds.add(rsp.getProjSponsorUserId());
            clientIds.add(record.getClientId());
            deptIds.add(rsp.getBizDeptId());
            resPage.add(rsp);
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (ContractBaseInfoListRSP rsp : resPage) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get)
                        .collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }
        return new Page<ContractBaseInfoListRSP>()
                .setCurrent(pageData.getCurrent())
                .setRecords(resPage)
                .setSize(pageData.getSize())
                .setTotal(pageData.getTotal());
    }

    @Override
    public List<ContractBaseInfoLib> list(Long clientId) {
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        ContractListSelectDTO dto = new ContractListSelectDTO();
        dto.setClientId(clientId);
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        //查询符合条件合同
        List<ContractBaseInfo> newContracts = baseMapper.fuzzyList(dto);
        List<ContractBaseInfoLib> contracts = new ArrayList<>();
        //更新为版本区数据
        Set<Long> contractIds = newContracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        contractIds.forEach(id -> {
            ContractBaseInfoLib contractBaseInfoLib = baseInfoLibHandler.queryLatestDataByOriginId(id);
            if (ObjectUtil.isNotEmpty(contractBaseInfoLib)) {
                contracts.add(contractBaseInfoLib);
            }
        });
        /*
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<ContractBaseInfoListRSP> resList = new ArrayList<>(contracts.size());
        for (ContractBaseInfoLib record : contracts) {
            ContractBaseInfoListRSP rsp = baseInfoConverter.entityToListRsp(record);
            rsp.setApplyCreditAmount(record.getApplyCreditAmount());
            rsp.setCreateTime(record.getCreateTime().format(dateTimeFormatter));
            rsp.setUpdateTime(record.getUpdateTime().format(dateTimeFormatter));
            String twoStatue = Optional.ofNullable(ContractChangeTypeEnum.of(record.getContractProcessChangeStatus()))
                    .map(ContractChangeTypeEnum::display)
                    .orElse(Optional.ofNullable(ContractAdvanceEnum.of(record.getContractProcessChangeStatus()))
                            .map(ContractAdvanceEnum::display)
                            .orElse(""));
            ContractProcessStatusEnum cps = ContractProcessStatusEnum.of(record.getContractProcessStatus());
            rsp.setContractProcessName(Optional.ofNullable(cps).map(ContractProcessStatusEnum::display).orElse("未定义的状态"));
            if(ObjectUtil.isNotEmpty(twoStatue)){
                rsp.setContractProcessName(String.format("%s(%s)", rsp.getContractProcessName(), twoStatue));
            }
            if (ObjectUtil.isEmpty(rsp.getApplyCreditAmount())) {
                //合同未查到项目金额，报价重新查
                //Map<Long, ContractLeasePrice> priceMap = contractPriceService.listByContractIds(contractids);
                ContractPriceDetailRSP priceDetailRSP = contractPriceService.oldDetail(new ContractPriceDetailREQ(rsp.getId()));
                if (ObjectUtil.isNotNull(ObjectUtil.isNotNull(priceDetailRSP))) {
                    rsp.setApplyCreditAmount(priceDetailRSP.getApplyCreditAmount());
                }
            }
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                sysUserIds.addAll(rsp.getProjCosponsorUserIds());
            }
            sysUserIds.add(rsp.getProjSponsorUserId());
            clientIds.add(record.getClientId());
            deptIds.add(rsp.getBizDeptId());
            resList.add(rsp);
        }



        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (ContractBaseInfoListRSP rsp : resList) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get)
                        .collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }*/
        return contracts;
    }

    @Override
    public List<ContractBaseInfo> list(Set<Long> clientIds) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return null;
        }
        return baseMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, clientIds));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean remove(ContractBaseInfoRemoveREQ req) {
        ContractBaseInfo originalInfo = baseMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return baseMapper.deleteById(req.getId()) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 详情接口
     *
     * @return ContractBaseInfoDetailRSP
     */
    @Override
    public ContractBaseInfoDetailRSP detail(ContractBaseInfoDetailREQ req) {
        ContractBaseInfo record = baseMapper.selectById(req.getId());
        ContractBaseInfoDetailRSP rsp = entity2RSP(record);
        // 用最新的值填充客户风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(record.getClientId());
        rsp.setRiskControlIndustryClassify(Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null));
        rsp.setStockContractFlag(getStockContractFlag(record.getProjReviewId()));
        ProcessResp processResp = contractService.findRelatedProcess(req.getId());
        rsp.setCurAssigneeIds(processResp != null ? processResp.getCurAssigneeIds() : null);
        return rsp;
    }

    @Override
    public Integer getStockContractFlag(Long projReviewId) {
        ProjReviewBaseInfo reviewBaseInfo = reviewBaseInfoService.getById(projReviewId);
        if (Objects.isNull(reviewBaseInfo)) {
            return YesOrNoNumberEnum.NO.getCode();
        }
        LeaseItemInfo newestOne = leaseItemInfoService.getNewestOne(projReviewId);
        if (stockContractDate.isAfter(reviewBaseInfo.getCreateTime()) && ObjectUtil.isEmpty(newestOne)) {
            return YesOrNoNumberEnum.YES.getCode();
        } else {
            return YesOrNoNumberEnum.NO.getCode();
        }
    }

    @Override
    public ContractBaseInfoDetailRSP entity2RSP(ContractBaseInfo t) {
        ContractBaseInfoDetailRSP rsp = baseInfoConverter.entityToDetailRSP(t);
        //fill name
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());
        deptIds.add(rsp.getBizDeptId());
        clientIds.add(rsp.getClientId());
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        if(ObjectUtil.isNotEmpty(t.getLeaseItemTypes())) {
            rsp.setLeaseItemTypes(JSONUtil.toList(t.getLeaseItemTypes(), String.class));
        }
        rsp.setBizDeptLeaderName(sysUserMap.get(rsp.getBizDeptLeaderId()));
        rsp.setBizDivisionLeaderName(sysUserMap.get(rsp.getBizDivisionLeaderId()));
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        if (ObjectUtil.isNotEmpty(AccountUtil.getLoginInfo())) {
            rsp.setIsProjSponsor(Objects.equals(AccountUtil.getLoginInfo().getId(), t.getProjSponsorUserId()));
        } else {
            rsp.setIsProjSponsor(Boolean.FALSE);
        }
        rsp.setChangeRemark(t.getChangeRemark());
        rsp.setContractAmount(t.getApplyCreditAmount());
        return rsp;
    }

    @Override
    public ContractBaseInfoDetailRSP editionDetail(ContractBaseInfoDetailREQ req) {
        ContractBaseInfoLib record = baseInfoLibHandler.queryLatestDataByOriginId(req.getId());
        if (ObjectUtil.isEmpty(record)) {
            throw new MithrasException("合同基本信息未查询到版本数据");
        }
        ContractBaseInfo contractBaseInfo = baseInfoLibHandler.actualLib2Entity(record);
        ContractBaseInfoDetailRSP contractBaseInfoDetailRSP = entity2RSP(contractBaseInfo);
        contractBaseInfoDetailRSP.setId(record.getOriginId());
        contractBaseInfoDetailRSP.setEditionId(record.getId());
        // 找到项目批复金额
        ProjReviewPriceDetailRSP detail = projReviewPriceService.detail(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(detail.getLeasePriceDetailRSP())) {
            contractBaseInfoDetailRSP.setApprovedAmount(detail.getLeasePriceDetailRSP().getApprovedAmount());
        } else if (Objects.nonNull(detail.getAocPriceDetailRSP())) {
            contractBaseInfoDetailRSP.setApprovedAmount(detail.getAocPriceDetailRSP().getApprovedAmount());
        } else if (Objects.nonNull(detail.getFactoringPriceDetailRSP())) {
            contractBaseInfoDetailRSP.setApprovedAmount(detail.getFactoringPriceDetailRSP().getApprovedAmount());
        }
        return contractBaseInfoDetailRSP;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateActualLeaseDate(LocalDate actualLeaseDate, Long contractId) {
        ContractBaseInfo contractBaseInfo = this.getById(contractId);
        contractBaseInfo.setActualLeaseDate(actualLeaseDate);
        // 这里还要更新第一个借据的实际起租日期
        contractReceiptMapper.update(null, new LambdaUpdateWrapper<ContractReceipt>()
                .eq(ContractReceipt::getContractId, contractId)
                .eq(ContractReceipt::getIsFirstReceipt, YesOrNoNumberEnum.YES.getCode())
                .set(ContractReceipt::getReceiptStartDate, actualLeaseDate));
        this.updateById(contractBaseInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateContractStatus(ContractStatus status, ContractProcessStatusEnum contractProcessStatus, Long contractId) {
        if (ObjectUtil.isEmpty(status)) {
            return;
        }
        canUpdateContractStatus(status, contractId);
        LambdaUpdateWrapper<ContractBaseInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ContractBaseInfo::getId, contractId);
        wrapper.set(ContractBaseInfo::getContractStatus, status.name());
        if (ContractStatus.SETTLE == status) {
            wrapper.set(ContractBaseInfo::getSettleTime, LocalDateTime.now());
        }
        if (Objects.nonNull(contractProcessStatus)) {
            wrapper.set(ContractBaseInfo::getContractProcessStatus, contractProcessStatus.name());
        }
        baseMapper.update(null, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateContractProcessStatus(String status, String changeStatus, Long contractId) {
        ContractBaseInfo baseInfo = new ContractBaseInfo();
        baseInfo.setId(contractId);
        baseInfo.setContractProcessStatus(status);
        baseInfo.setContractProcessChangeStatus(changeStatus);
        baseMapper.updateById(baseInfo);
    }

    @Override
    public ContractCanChangeRSP canUpdateContractProcessStatus(List<String> status, Long contractId) {
        ContractCanChangeRSP rsp = new ContractCanChangeRSP();
        ContractBaseInfo baseInfo = baseMapper.selectById(contractId);
        if (isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ContractProcessStatusEnum baseType = Optional.ofNullable(baseInfo.getContractProcessStatus()).map(ContractProcessStatusEnum::of).orElseThrow(() -> new MithrasException("合同审批状态不合法"));


        rsp.setStatus(baseInfo.getContractProcessStatus());
        rsp.setTwoStatus(baseInfo.getContractProcessChangeStatus());
        rsp.setCanProcess(false);
        if (isNull(status) || status.size() == 0) {
            rsp.setMessage("查询当前状态");
            return rsp;
        }
//        if(ContractProcessStatusEnum.NEW_COMMIT.equals(baseType) || ContractProcessStatusEnum.START_RENT_COMMIT.equals(baseType) ||
//                ContractProcessStatusEnum.NEW_RECEIPT_COMMIT.equals(baseType) || ContractProcessStatusEnum.CHANGE_COMMIT.equals(baseType) ||
//                ContractProcessStatusEnum.SETTLE_COMMIT.equals(baseType)){
//            throw new MithrasException("该合同处于审批流中，请先完成审批后再操作！");
//        }
        if (ContractProcessStatusEnum.NEW_PASS.equals(baseType) || ContractProcessStatusEnum.NEW_CANCEL.equals(baseType) ||
                ContractProcessStatusEnum.START_RENT_CANCEL.equals(baseType) || ContractProcessStatusEnum.START_RENT_PASS.equals(baseType) ||
                ContractProcessStatusEnum.NEW_RECEIPT_CANCEL.equals(baseType) || ContractProcessStatusEnum.NEW_RECEIPT_PASS.equals(baseType) ||
                ContractProcessStatusEnum.SETTLE_CANCEL.equals(baseType) || ContractProcessStatusEnum.SETTLE_PASS.equals(baseType) ||
                ContractProcessStatusEnum.CHANGE_CANCEL.equals(baseType) || ContractProcessStatusEnum.CHANGE_PASS.equals(baseType)) {
            rsp.setCanProcess(Boolean.TRUE);
            return rsp;
        }
        for (String s : status) {
            if (baseInfo.getContractProcessStatus().equals(s)) {
                rsp.setCanProcess(Boolean.TRUE);
                return rsp;
            }
        }
        //放过发起人节点
        if (processCanChange(contractId)) {
            rsp.setCanProcess(Boolean.TRUE);
            return rsp;
        }
        // 如果当前审批人是运营管理经办是可以修改的
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
        if (allOperateUserJob.containsKey(loginInfo.getId())) {
            rsp.setCanProcess(Boolean.TRUE);
            return rsp;
        }
        rsp.setMessage("合同处于" + baseType.display() + ",请等待结束后操作");
        return rsp;
    }

    //流程是否允许修改
    private Boolean processCanChange(Long contractId) {
        ProcessResp processResp = contractService.findRelatedProcess(contractId);
        if (Objects.isNull(processResp)) {
            // 流程为空 放过
            return true;
        }
        return FlowUtil.isStartUserNode(processResp);
    }

    @Override
    public void constraint(ContractConstraintREQ req) {
        ContractBaseInfo baseInfo = baseMapper.selectById(req.getContractId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AfterLeaseAdjustEnum afterLeaseAdjustEnum;
        if (ContractChangeTypeEnum.EXTENSION.name().equals(req.getChangeType())) {
            afterLeaseAdjustEnum = AfterLeaseAdjustEnum.EXTEND;
        } else if (ContractChangeTypeEnum.CHANGE_REPAY_PLAN.name().equals(req.getChangeType())) {
            afterLeaseAdjustEnum = AfterLeaseAdjustEnum.REPAYMENT;
        } else {
            throw new MithrasException(ResultMsg.UNSUPPORT_TYPE);
        }
        AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoService.adjustBaseLastByprojId(baseInfo.getProjReviewId(), afterLeaseAdjustEnum.name());
        if (ObjectUtil.isNull(afterLeaseAdjustInfo)) {
            throw new MithrasException("请先在【租后管理】申请项目调整！");
        }
        //目前无
        //return ContractCanChangeRSP.builder().canProcess(Boolean.TRUE).build();
    }

    public void canUpdateContractStatus(ContractStatus status, Long contractId) {
        if (ContractStatus.INVALID.equals(status)) {
            if (baseMapper.selectCount(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getId, contractId)
                    .eq(ContractBaseInfo::getContractStatus, ContractStatus.NEW.name())) <= 0) {
                throw new MithrasException("合同已生效，无法作废");
            }
        }
    }

    /**
     * 计算剩余可用额度
     **/
    @Override
    public Long getRemainAvailableQuota(Long ownId, Integer isLoop, List<ContractBaseInfo> contractBaseInfos) {
        if (ObjectUtil.isEmpty(isLoop) || ObjectUtil.isEmpty(contractBaseInfos)) {
            return 0L;
        }
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        List<Long> ids = new ArrayList<>();
        if (ObjectUtil.isEmpty(contractBaseInfos) || contractBaseInfos.size() <= 0) {
            return sum.get();
        }
        contractBaseInfos.forEach(rep -> {
            //排除自己
            if (!(ObjectUtil.isNotEmpty(ownId) && ownId.equals(rep.getId()))) {
                ids.add(rep.getId());
            }
        });
        if (ids.size() == 0) {
            return 0L;
        }
        //其他合同金额
        sum.updateAndGet(v -> v + contractPriceService.sumApplyByContractId(ids));
        log.info("getRemainAvailableQuota other contract amount : {}, ids : {}", sum.get(), ids);

        if (1 == isLoop) {
            //计算核销本金

            /*Map<Long, Pair<Long, Long>> longPairMap = paymentBaseInfoService.calculateCapitalDistributionBatch(ids);
            log.info("getRemainAvailableQuota other contract amount : {}, ids : {}", ids);
            Collection<Pair<Long, Long>> values = longPairMap.values();
            values.stream().forEach(pair -> {
                sum.updateAndGet(v -> v + pair.getValue());
            });*/
            sum.updateAndGet(v -> v - getRentSum(CashFlowItemEnum.RENT.name(), ids));
            //查询首期租金
           /* List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.DOWN_PAYMENT.name())
                    .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                    .in(CollectionBaseInfo::getContractId, ids));
            collectionBaseInfos.forEach(info -> {
                sum.getAndUpdate(v -> v - info.getCollectionAmount());
            });*/
            sum.updateAndGet(v -> v - getRentSum(CashFlowItemEnum.FIRST_RENT.name(), ids));
        }
        return sum.get();
    }

    @Override
    //计算集团授信 授信主体风险敞口
    public Long getGroupCreditStockRiskExposure(Long clientId) {
        if (isNull(clientId)) {
            return 0L;
        }
        log.info("getGroupCreditStockRiskExposure clientId {}", clientId);
        AtomicReference<Long> addSum = new AtomicReference<>(0L);

        List<String> statusList = ListUtil.toList(ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name());
        // 自己是主客户的合同
        List<ContractBaseInfo> needCalContractList = baseMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, clientId)
                .in(ContractBaseInfo::getContractStatus, statusList)
        );
        // 找到自己发起的集团授信
        List<GroupCreditReviewBaseInfo> groupCreditReviewList = groupCreditReviewBaseInfoService.getBaseMapper().selectList(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                .eq(GroupCreditReviewBaseInfo::getClientId, clientId)
                .eq(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, RecordStatus.TAKE_EFFECT.name())
        );
        // 找到这些集团授信发起的项目评审
        List<ProjReviewBaseInfo> projReviewBaseInfoList = groupCreditReviewList.isEmpty() ? new ArrayList<>() :
                reviewBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .eq(ProjReviewBaseInfo::getRelationDataType, ReviewRelationDataType.GROUP_CREDIT_REVIEW.name())
                        .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name())
                        .in(ProjReviewBaseInfo::getGroupCreditReviewId, groupCreditReviewList.stream().map(GroupCreditReviewBaseInfo::getId).collect(Collectors.toSet()))
                );
        // 找到这些项目评审对应的合同
        List<ContractBaseInfo> groupCreditContractList = projReviewBaseInfoList.isEmpty() ? new ArrayList<>() :
                baseMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getProjReviewId, projReviewBaseInfoList.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toSet()))
                        .in(ContractBaseInfo::getContractStatus, statusList)
                );

        needCalContractList.addAll(groupCreditContractList);

        if (CollectionUtils.isEmpty(needCalContractList)) {
            return addSum.get();
        }
        Set<Long> contractIds = new HashSet<>();
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        needCalContractList.forEach(rep -> contractIds.add(rep.getId()));
        List<Long> ids = new ArrayList<>(contractIds);
        log.info("getGroupCreditStockRiskExposure contractids {}", contractIds);
        //合同金额
        sum.updateAndGet(v -> v + contractPriceService.sumApplyByContractId(ids));
        //计算核销租金 -> 修改为核销本金
        sum.updateAndGet(v -> v - getPrincipalSum(ids));
        //计算核销首期租金
        sum.updateAndGet(v -> v - getRentSum(CashFlowItemEnum.FIRST_RENT.name(), ids));
        //核销保证金
        sum.updateAndGet(v -> v - getRentSum(CashFlowItemEnum.EARNEST_MONEY.name(), ids));
        //加上不同模块加点
        sum.updateAndGet(v -> v + addSum.get());
        return sum.get() < 0 ? 0L : sum.get();
    }

    @Override
    //计算客户风险敞口
    public Long getStockRiskExposure(Long clientId, Long mainId, BusinessModuleEnum module) {
        return getStockRiskExposure(clientId, mainId, module, null);
    }

    @Override
    public Long getStockRiskExposure(Long clientId, Long mainId, BusinessModuleEnum module, LocalDate confirmDate) {
        if (isNull(clientId)) {
            return 0L;
        }
        log.info("getStockRiskExposure clientId {}, mainId {}, module {}", clientId, mainId, module);
        AtomicReference<Long> addSum = new AtomicReference<>(0L);
        if (ObjectUtil.isNotEmpty(module)) {
            switch (module) {
                case PROJ_REVIEW:
                    addSum.updateAndGet(v -> v + projReviewPriceService.sumApplyByContractId(Collections.singletonList(mainId)));
                    break;
                case CONTRACT:
                    addSum.updateAndGet(v -> v + contractPriceService.sumApplyByContractId(Collections.singletonList(mainId)));
                    break;
                default:
            }
        }
        //存续合同，即生效+起租
        List<String> statusList = ListUtil.toList(ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name());
        //查询客户下合同，如果是合同id需去除自身id
        List<ContractBaseInfo> baseInfos = baseMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getClientId, clientId)
                .ne(ObjectUtil.isNotNull(mainId), ContractBaseInfo::getId, mainId)
                .in(ContractBaseInfo::getContractStatus, statusList)
        );
        if (ObjectUtil.isEmpty(baseInfos) || baseInfos.size() == 0) {
            return addSum.get();
        }
        Set<Long> contractIds = new HashSet<>();
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        baseInfos.forEach(rep -> contractIds.add(rep.getId()));
        List<Long> ids = new ArrayList<>(contractIds);
        log.info("getStockRiskExposure contractIds {}", contractIds);
        //合同金额
        sum.updateAndGet(v -> v + paymentActualDetailService.calculatePaidAmountByContractIds(ids));
        //修改为批量查询
        Map<Long, Long> marginBalances = marginBaseInfoService.getMarginBalances(ids);
        List<CollectionBaseInfo> infos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getWriteOffStatus, ListUtil.toList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(),
                        CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()))
                .in(CollectionBaseInfo::getContractId, contractIds));
        if (ObjectUtil.isNotEmpty(infos)) {
            for (CollectionBaseInfo baseInfo : infos) {
                switch (CashFlowItemEnum.of(baseInfo.getCashFlowItem())) {
                    case FIRST_RENT:
                        sum.updateAndGet(v -> v - LongUtil.null2zero(baseInfo.getCollectionAmount()));
                        break;
                    case RENT:
                        sum.updateAndGet(v -> v - Math.min(LongUtil.null2zero(baseInfo.getCollectionAmount()), LongUtil.null2zero(baseInfo.getPrincipal())));
                        break;
                    default:
                        break;
                }
            }
        }
        for (Long id : marginBalances.keySet()) {
            sum.updateAndGet(v -> v - LongUtil.null2zero(marginBalances.get(id)));
        }
        //计算核销租金 -> 修改为核销本金
        /*sum.updateAndGet(v -> v - getPrincipalSum(ids));
        //计算核销首期租金
        sum.updateAndGet(v -> v - getRentSum(CashFlowItemEnum.FIRST_RENT.name(), ids));
        //核销保证金
        sum.updateAndGet(v -> v - getRentSum(CashFlowItemEnum.EARNEST_MONEY.name(), ids));*/
        //加上不同模块加点
        sum.updateAndGet(v -> v + addSum.get());
        return sum.get() < 0 ? 0L : sum.get();
    }

    @Override
    public Long getAssetBalance(List<Long> contractIdList, Map<Long, Long> startRentContractMap) {
        Long assetBalance = 0L;
        Set<Long> contractIdSet = contractIdList.stream().collect(Collectors.toSet());
        for (Long contractId : contractIdSet) {
            if (startRentContractMap.containsKey(contractId)) {
                assetBalance += startRentContractMap.get(contractId);
            }
        }
        return assetBalance;
    }

    @Override
    public Map<Long, Long> getStockRiskExposureByClients(Set<Long> clientIds) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return MapUtil.empty();
        }
        //存续合同，即生效+起租
        List<String> statusList = ListUtil.toList(ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name());
        //查询客户下合同
        List<ContractBaseInfo> baseInfos = baseMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, clientIds)
                .in(ContractBaseInfo::getContractStatus, statusList)
        );
        if (CollUtil.isEmpty(baseInfos)) {
            return new HashMap<>();
        }
        Map<Long, List<ContractBaseInfo>> ccMap = baseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        List<Long> contractIds = baseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<PaymentActualDetail> actualDetails = paymentActualDetailService.getBaseMapper().selectList(Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).in(PaymentActualDetail::getContractId, contractIds));
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name())).in(CollectionBaseInfo::getContractId, contractIds));
        Map<Long, List<CollectionBaseInfo>> ccsMap = new HashMap<>();
        if (isNotEmpty(collectionBaseInfos)) {
            ccsMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        }
        Map<Long, List<PaymentActualDetail>> casMap = new HashMap<>();
        if (CollUtil.isNotEmpty(actualDetails)) {
            casMap = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        }
        List<MarginBaseInfo> marginBaseInfos = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getContractId, contractIds));
        Map<Long, MarginBaseInfo> cmMap = marginBaseInfos.stream().collect(Collectors.toMap(MarginBaseInfo::getContractId, o -> o));
        Map<Long, Long> stockRiskExposureMap = new HashMap<>();
        for (Long clientId : clientIds) {
            List<ContractBaseInfo> infos = ccMap.get(clientId);
            AtomicReference<Long> sum = new AtomicReference<>(0L);
            if (isNotEmpty(infos)) {
                for (ContractBaseInfo info : infos) {
                    List<PaymentActualDetail> actualDetails1 = casMap.get(info.getId());
                    if (CollUtil.isNotEmpty(actualDetails1)) {
                        for (PaymentActualDetail paymentActualDetail : actualDetails1) {
                            sum.updateAndGet(v -> v + LongUtil.null2zero(paymentActualDetail.getPaidInAmount()));
                        }
                    }
                    List<CollectionBaseInfo> collections = ccsMap.get(info.getId());
                    if (isNotEmpty(collections)) {
                        for (CollectionBaseInfo collection : collections) {
                            if (CashFlowItemEnum.FIRST_RENT.name().equals(collection.getCashFlowItem())) {
                                sum.updateAndGet(v -> v - LongUtil.null2zero(collection.getCollectionAmount()));
                            } else {
                                sum.updateAndGet(v -> v - LongUtil.null2zero(collection.getCollectionPrincipal()));
                            }
                        }
                    }
                    //核销保证金
                    MarginBaseInfo marginBaseInfo = cmMap.get(info.getId());
                    if (marginBaseInfo != null) {
                        sum.updateAndGet(v -> v - marginBaseInfo.getCollectionAmount());
                    }
                }

            }
            stockRiskExposureMap.put(clientId, sum.get() < 0 ? 0L : sum.get());
        }
        return stockRiskExposureMap;
    }

    //获取合同风险敞口
    @Override
    public Map<Long, Long> getStockRiskExposureByContracts(Set<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return MapUtil.empty();
        }
        //查询客户下合同
        List<ContractBaseInfo> baseInfos = baseMapper.selectBatchIds(contractIds);
        if (CollUtil.isEmpty(baseInfos)) {
            return new HashMap<>();
        }
        List<PaymentActualDetail> actualDetails = paymentActualDetailService.getBaseMapper().selectList(Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).in(PaymentActualDetail::getContractId, contractIds));
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name())).in(CollectionBaseInfo::getContractId, contractIds));
        Map<Long, List<CollectionBaseInfo>> ccsMap = new HashMap<>();
        if (isNotEmpty(collectionBaseInfos)) {
            ccsMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        }
        Map<Long, List<PaymentActualDetail>> casMap = new HashMap<>();
        if (CollUtil.isNotEmpty(actualDetails)) {
            casMap = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        }
        List<MarginBaseInfo> marginBaseInfos = marginBaseInfoMapper.selectList(Wrappers.<MarginBaseInfo>lambdaQuery().in(MarginBaseInfo::getContractId, contractIds));
        Map<Long, MarginBaseInfo> cmMap = marginBaseInfos.stream().collect(Collectors.toMap(MarginBaseInfo::getContractId, o -> o));
        Map<Long, Long> stockRiskExposureMap = new HashMap<>();
        for (Long contractId : contractIds) {
            AtomicReference<Long> sum = new AtomicReference<>(0L);
            List<PaymentActualDetail> actualDetails1 = casMap.get(contractId);
            if (CollUtil.isNotEmpty(actualDetails1)) {
                for (PaymentActualDetail paymentActualDetail : actualDetails1) {
                    sum.updateAndGet(v -> v + LongUtil.null2zero(paymentActualDetail.getPaidInAmount()));
                }
            }
            List<CollectionBaseInfo> collections = ccsMap.get(contractId);
            if (isNotEmpty(collections)) {
                for (CollectionBaseInfo collection : collections) {
                    if (CashFlowItemEnum.FIRST_RENT.name().equals(collection.getCashFlowItem())) {
                        sum.updateAndGet(v -> v - LongUtil.null2zero(collection.getCollectionAmount()));
                    } else {
                        sum.updateAndGet(v -> v - LongUtil.null2zero(collection.getCollectionPrincipal()));
                    }
                }
            }
            //核销保证金
            MarginBaseInfo marginBaseInfo = cmMap.get(contractId);
            if (marginBaseInfo != null) {
                sum.updateAndGet(v -> v - marginBaseInfo.getCollectionAmount());
            }
            stockRiskExposureMap.put(contractId, sum.get() < 0 ? 0L : sum.get());
        }
        return stockRiskExposureMap;
    }

    /**
     * CashFlowItemEnum
     * EARNEST_MONEY("保证金"),
     * FIRST_RENT("首期租金"),
     * OTHERAMOUNT("服务费/咨询费/手续费"),
     * RENT("租金"),
     **/
    private Long getRentSum(String type, List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        sum.updateAndGet(v -> v + contractCollectionPaymentService.collectionAmountByContractIds(ids, CashFlowItemEnum.of(type)));
        // ids.forEach(id -> sum.updateAndGet(v -> v + contractCollectionPaymentService.collectionAmount(id, CashFlowItemEnum.of(type))));
        log.info("getStockRiskExposure getRentSum type : {}, ids : {}, sum {}", type, ids, sum.get());
        return sum.get();
    }

    private Long getPrincipalSum(List<Long> ids) {
        AtomicReference<Long> sum = new AtomicReference<>(0L);
        List<String> writeOffStatus = new ArrayList<>();
        writeOffStatus.add(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name());
        writeOffStatus.add(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, ids)
                .in(CollectionBaseInfo::getWriteOffStatus, writeOffStatus));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return sum.get();
        }
        //部分核销取实际收款金额，核销完毕取本金
        collectionBaseInfos.forEach(base -> sum.updateAndGet(v -> v + Math.min(LongUtil.null2zero(base.getCollectionAmount()), LongUtil.null2zero(base.getPrincipal()))));
        log.info("getStockRiskExposure getPrincipalSum , ids : {}, sum {}", ids, sum.get());
        return sum.get();
    }


    @Override
    public List<ContractBaseInfo> selectListByProjId(Long projId) {
        return baseMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjReviewId, projId)
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void contractSettle(Long contractId) {
        Assert.notNull(contractId, () -> MithrasException.newException("主合同id不能为空"));
        ContractBaseInfo contractBaseInfo = baseMapper.selectById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同信息不存在"));
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.SETTLE.name());
        String clientName = id2NameService.clientId2Name(Collections.singleton(contractBaseInfo.getClientId())).get(contractBaseInfo.getClientId());
        messageAddREQ.setContent(contractBaseInfo.getContractCode());
        messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.SETTLE_PASS.pcUrl, contractBaseInfo.getId(), contractBaseInfo.getContractStatus()));
        Set<Long> to = new HashSet<>();
        if (contractBaseInfo.getProjSponsorUserId() != null) {
            to.add(contractBaseInfo.getProjSponsorUserId());
        }
        if (contractBaseInfo.getBizDeptLeaderId() != null) {
            to.add(contractBaseInfo.getBizDeptLeaderId());
        }
        messageAddREQ.setTo(new ArrayList<>(to));
        messageAddREQ.setNeedOa(true);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.PAYMENT.name());
        MessageModel messageModel = messageConvert.reqToMessage(messageAddREQ);
        NoticeMessageBody bodie = (NoticeMessageBody) messageModel.getBodie();
        // 先校验流程状态，流程状态为结清审批通过才支持修改合同状态，不然的话把合同状态的变更放到审批通过后去做
        if (Objects.equals(ContractProcessStatusEnum.SETTLE_PASS.name(), contractBaseInfo.getContractProcessStatus())) {
            log.info("合同所有收款核销完毕通知结清，执行合同状态变更操作[contractId: {}]", contractId);
            contractLeaseItemService.unbindLeaseItem(contractId);
            this.updateContractStatus(ContractStatus.SETTLE, null, contractId);
            contractVersionService.recordVersion(contractId, VersionTypeEnum.EFFECT, ObjectUtil.isEmpty(AccountUtil.getLoginInfo()) ? 3L : AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
            bodie.setTitle(clientName + "的" + contractBaseInfo.getContractCode() + "的现金流已全部核销，合同已结清。");
        } else {
            bodie.setTitle(clientName + "的" + contractBaseInfo.getContractCode() + "的现金流已全部核销，请发起合同结清流程。");
        }
        try {
            messageService.sendMessage(messageModel);
        } catch (Exception e) {
            log.error("合同结清通知消息发生异常[{}]", JSONUtil.toJsonStr(messageModel), e);
        }
    }

    @Override
    public void updateActualFinishDate(Long contractId, LocalDate actualFinishDate) {
        ContractBaseInfo contractBaseInfo = this.getById(contractId);
        contractBaseInfo.setActualFinishDate(actualFinishDate);
        this.updateById(contractBaseInfo);
    }

    @Override

    public List<ContractBaseInfo> listByClients(List<Long> clientIdList) {
        return this.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, clientIdList)
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
        );
    }

    @Override
    public LocalDate getContractExpirationDate(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return null;
        }
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIds)
                .orderByDesc(CollectionBaseInfo::getPlanCollectionDate)
                .last(cn.zswltech.mithras.service.util.StringUtil.mysqlLimitOne()));
        return ObjectUtil.isNull(collectionBaseInfo) ? null : collectionBaseInfo.getPlanCollectionDate();
    }

    @Override
    public Map<Long, LocalDate> getContractExpirationDateByRent(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return null;
        }
        List<ContractRentLastTimeDTO> contractExpirationDateByRent = contractBaseInfoMapper.getContractExpirationDateByRent(contractIds);
        if (ObjectUtil.isEmpty(contractExpirationDateByRent)) {
            return new HashMap<>();
        }
        return contractExpirationDateByRent.stream().filter(base -> ObjectUtil.isNotEmpty(base.getEndTime())).collect(Collectors.toMap(ContractRentLastTimeDTO::getContractId,
                ContractRentLastTimeDTO::getEndTime, (a, b) -> a));
    }

    @Override
    public LocalDate getFistPaymentDate(Long contractId) {
        PaymentActualDetail paymentActualDetail = paymentActualDetailMapper.selectOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getContractId, contractId)
                .orderByAsc(PaymentActualDetail::getPaidInDate)
                .last(cn.zswltech.mithras.service.util.StringUtil.mysqlLimitOne()));
        return paymentActualDetail == null ? null : paymentActualDetail.getPaidInDate();
    }

    @Override
    public Map<Long, ContractBaseInfo> getMapByContractIds(Collection<Long> contractIds) {
        List<ContractBaseInfo> contractBaseInfoList = this.listByIds(contractIds);
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyMap();
        }
        return contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
    }

    @Override
    public List<ContractBaseInfo> listByProjReviewIds(List<Long> projReviewIds) {
        if (CollUtil.isEmpty(projReviewIds)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjReviewId, projReviewIds)
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name(), ContractStatus.CLOSED.name())
        );
    }

    @Override
    public List<ContractBaseInfo> listInRentContract(Long clientId) {
        // 已放款合同
        LambdaQueryWrapper<PaymentBaseInfo> paymentQuery = Wrappers.lambdaQuery();
        paymentQuery.in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
        paymentQuery.in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.PART_WRITTEN_OFF.name(), PaymentWriteOffStatus.WRITTEN_OFF.name()));
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.list(paymentQuery);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }
        Set<Long> contractIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet());
        // 剩余本金大于0的合同
        ContractPrincipalQuery contractPrincipalQuery = new ContractPrincipalQuery();
        contractPrincipalQuery.setContractIds(contractIds);
        List<ContractPrincipalBO> list = this.getBaseMapper().listContractPrincipal(contractPrincipalQuery);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Iterator<ContractPrincipalBO> iterator = list.iterator();
        while (iterator.hasNext()) {
            ContractPrincipalBO bo = iterator.next();
            long remaining = Optional.ofNullable(bo.getPlanPrincipal()).orElse(0L) - Optional.ofNullable(bo.getActualPrincipal()).orElse(0L);
            if (remaining <= 0) {
                iterator.remove();
            }
        }
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Set<Long> ids = list.stream().map(ContractPrincipalBO::getContractId).collect(Collectors.toSet());
        List<ContractBaseInfo> result = this.listByIds(ids);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        if (Objects.nonNull(clientId)) {
            return result.stream().filter(item -> Objects.equals(clientId, item.getClientId())).collect(Collectors.toList());
        } else {
            return result;
        }
    }

    @Override
    public List<ContractCompareBusinessRSP> compareBusiness(Long contractId, boolean isHistory) {
        //查询历史数据比对
        Map<Long, ClientBusinessHistoryBO> clientBusinessHistoryBOMap;
        //获取合同承租人、担保人
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractId);
        List<ContractGuarantor> contractGuarantorList = guarantorService.listByContractId(contractId);
        List<Long> clientIds = new ArrayList<>();
        List<Long> contractTenantryIds = null;
        List<Long> contractGuarantorIds = null;
        List<ContractCompareBusinessRSP> rsp = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(contractTenantryList)) {
            contractTenantryIds = contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());
            clientIds.addAll(contractTenantryIds);
        }
        if (CollectionUtil.isNotEmpty(contractGuarantorList)) {
            contractGuarantorIds = new ArrayList<>();
            for (ContractGuarantor contractGuarantor : contractGuarantorList) {
                contractGuarantorIds.addAll(JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class));
            }
            clientIds.addAll(contractGuarantorIds);
        }
        if (CollectionUtil.isEmpty(clientIds)) {
            return ListUtil.empty();
        }
        //查询系统内客户信息
        Map<Long, Client> clientMap = clientService.listByIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
        //客户工商信息
        Map<Long, CorpCommerceInfo> corpCommerceMap = corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                .in(CorpCommerceInfo::getClientId, clientIds)).stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> a));
        Map<Long, List<CorpShareholderInfo>> clientShareholderMap = corpShareholderInfoMapper.selectList(Wrappers.<CorpShareholderInfo>lambdaQuery()
                .in(CorpShareholderInfo::getClientId, clientIds)).stream().collect(Collectors.groupingBy(CorpShareholderInfo::getClientId));
        //查询比对信息
        if (isHistory) {
            clientBusinessHistoryBOMap = clientBusinessHistoryService.getHistoryBoByClientIds(clientIds);
        } else {
            //查询天眼查数据
            clientBusinessHistoryBOMap = clientService.compareBusiness(clientIds);
        }
        //比对数据
        //承租人
        if (CollectionUtil.isNotEmpty(contractTenantryIds)) {
            contractTenantryIds.forEach(clientId -> {
                Client client = clientMap.get(clientId);
                if (ClientType.CORPORATION.name().equals(client.getClientType())) {
                    rsp.add(buildContractCompareBusinessRSP(clientBusinessHistoryBOMap.get(clientId), client, corpCommerceMap.get(clientId),
                            clientShareholderMap.get(clientId), "承租人"));
                }
            });
        }
        //担保人
        if (CollectionUtil.isNotEmpty(contractGuarantorIds)) {
            contractGuarantorIds.forEach(clientId -> {
                Client client = clientMap.get(clientId);
                if (ClientType.CORPORATION.name().equals(client.getClientType())) {
                    rsp.add(buildContractCompareBusinessRSP(clientBusinessHistoryBOMap.get(clientId), client, corpCommerceMap.get(clientId),
                            clientShareholderMap.get(clientId), "担保人"));
                }
            });
        }
        return rsp;
    }

    public ContractCompareBusinessRSP buildContractCompareBusinessRSP(ClientBusinessHistoryBO bo, Client client, CorpCommerceInfo corpCommerceInfo,
                                                                      List<CorpShareholderInfo> corpShareholderInfos, String clientType) {
        boolean nodiff = true;
        ContractCompareBusinessRSP rsp = new ContractCompareBusinessRSP();
        rsp.setClientType(clientType);
        rsp.setClientName(client.getClientName());
        rsp.setCorpRepresent(corpCommerceInfo == null ? null : corpCommerceInfo.getCorpRepresent());
        //股东名称
        Set<String> shareholderNameSet = new HashSet<>();
        if (ObjectUtil.isNotNull(bo)) {
            //客户名称
            rsp.setClientTycName(bo.getTycName());
            boolean operationFlag;
            LinkedList<DiffMatchPatch.Diff> clientNameDiffs = CompareUtil.compareStringIgnoreExclude(client.getClientName(), bo.getTycName(), GlobalConstants.MATCH_IGNORE_STR);
            operationFlag = diffHasChanges(clientNameDiffs);
            if (operationFlag) {
                rsp.setClientNameCompare("一致");
            } else {
                nodiff = false;
                rsp.setClientNameCompare(CompareUtil.string2HtmlString(clientNameDiffs, "<span style=\"color:red\">", "</span>"));
            }
            //法人代表信息
            rsp.setCorpTycRepresent(bo.getTycCorpRepresent());
            LinkedList<DiffMatchPatch.Diff> corpRepresentDiffs = CompareUtil.compareStringIgnoreExclude(rsp.getCorpRepresent(), bo.getTycCorpRepresent(), GlobalConstants.MATCH_IGNORE_STR);
            operationFlag = diffHasChanges(corpRepresentDiffs);
            if (operationFlag) {
                rsp.setCorpRepresentCompare("一致");
            } else {
                nodiff = false;
                rsp.setCorpRepresentCompare(CompareUtil.string2HtmlString(corpRepresentDiffs, "<span style=\"color:red\">", "</span>"));
            }
            //比对股东信息
            if (CollectionUtil.isNotEmpty(bo.getTycShareHolderInfo())) {
                Map<String, CorpShareholderInfo> corpShareholderMap = new HashMap<>();
                Map<String, CorpShareholderInfo> corpShareholderWithoutSpecialTextMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(corpShareholderInfos)) {
                    corpShareholderMap = corpShareholderInfos.stream().collect(Collectors.toMap(CorpShareholderInfo::getShareholderName, e -> e, (a, b) -> a));
                    corpShareholderWithoutSpecialTextMap = corpShareholderInfos.stream().collect(Collectors.toMap(e -> StrUtil.removeAll(e.getShareholderName(), GlobalConstants.MATCH_IGNORE_STR), e -> e, (a, b) -> a));
                }
                CorpShareholderInfo corpShareholderInfo;
                String shareholderName;
                String capitalPercent;
                String capitalPercentTyc;
                boolean isConsistent = true;
                for (MithrasShareholderInfo boShareHolderInfo : bo.getTycShareHolderInfo()) {
                    corpShareholderInfo = corpShareholderMap.get(boShareHolderInfo.getShareholderName());
                    if (Objects.isNull(corpShareholderInfo)) {
                        // 找不到的话从去掉特殊字符的map里试试
                        corpShareholderInfo = corpShareholderWithoutSpecialTextMap.get(StrUtil.removeAll(boShareHolderInfo.getShareholderName(), GlobalConstants.MATCH_IGNORE_STR));
                    }
                    shareholderName = Optional.ofNullable(corpShareholderInfo).map(CorpShareholderInfo::getShareholderName).orElse(null);
                    capitalPercentTyc = " " + LongUtil.tenThousand2Dollar(String.valueOf(Optional.ofNullable(boShareHolderInfo.getCapitalPercent()).orElse(0L))).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
                    capitalPercent = " " + LongUtil.tenThousand2Dollar(String.valueOf(Optional.ofNullable(corpShareholderInfo).map(CorpShareholderInfo::getCapitalPercent).orElse(0L))).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
                    List<String> shareHolderInfo = Optional.ofNullable(rsp.getShareHolderInfo()).orElse(new ArrayList<>());
                    if (ObjectUtil.isNotEmpty(shareholderName)) {
                        shareHolderInfo.add(shareholderName + capitalPercent);
                    }
                    rsp.setShareHolderInfo(shareHolderInfo);
                    List<String> shareHolderTycInfo = Optional.ofNullable(rsp.getShareHolderTycInfo()).orElse(new ArrayList<>());
                    shareHolderTycInfo.add(boShareHolderInfo.getShareholderName() + capitalPercentTyc);
                    rsp.setShareHolderTycInfo(shareHolderTycInfo);
                    List<String> shareHolderInfoCompare = Optional.ofNullable(rsp.getShareHolderInfoCompare()).orElse(new ArrayList<>());
                    LinkedList<DiffMatchPatch.Diff> shareholderNameDiffs = CompareUtil.compareStringIgnoreExclude(shareholderName, boShareHolderInfo.getShareholderName(), GlobalConstants.MATCH_IGNORE_STR);
                    LinkedList<DiffMatchPatch.Diff> capitalPercentDiffs = CompareUtil.compareStringIgnoreExclude(capitalPercent, capitalPercentTyc, GlobalConstants.MATCH_IGNORE_STR);
                    //工商信息
                    operationFlag = diffHasChanges(shareholderNameDiffs);
                    if (operationFlag) {
                        operationFlag = diffHasChanges(capitalPercentDiffs);
                    }
                    if (operationFlag) {
                        //shareHolderInfoCompare.add(boShareHolderInfo.getShareholderName() + capitalPercentTyc);
                    } else {
                        nodiff = false;
                        isConsistent = false;
                        shareHolderInfoCompare.add(
                                CompareUtil.string2HtmlString(shareholderNameDiffs, "<span style=\"color:red\">", "</span>") + " " +
                                        CompareUtil.string2HtmlString(capitalPercentDiffs, "<span style=\"color:red\">", "</span>")
                        );
                    }
                    rsp.setShareHolderInfoCompare(shareHolderInfoCompare);
                    shareholderNameSet.add(shareholderName);
                }
                if (isConsistent) {
                    rsp.setShareHolderInfoCompare(ListUtil.toList("一致"));
                }
            }
        } else {
            nodiff = false;
        }
        //补充未比对信息
        if (CollectionUtil.isNotEmpty(corpShareholderInfos)) {
            corpShareholderInfos.forEach(corpShareholderInfo -> {
                if (!shareholderNameSet.contains(corpShareholderInfo.getShareholderName())) {
                    List<String> list = Optional.ofNullable(rsp.getShareHolderInfo()).orElse(new ArrayList<>());
                    list.add(corpShareholderInfo.getShareholderName() + " " + LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(corpShareholderInfo.getCapitalPercent()))).setScale(2,
                            RoundingMode.HALF_UP).toPlainString() + "%");
                    rsp.setShareHolderInfo(list);
                }
            });
        }
        //判断变更
//        if (Boolean.FALSE.equals(checkoutName(rsp.getClientName(), rsp.getClientTycName()))
//                || ObjectUtil.notEqual(rsp.getCorpRepresent(), rsp.getCorpTycRepresent())
//                || ObjectUtil.notEqual(rsp.getShareHolderInfo(), rsp.getShareHolderTycInfo())) {
//            rsp.setChangeFlag(YesOrNoNumberEnum.YES.getCode());
//        } else {
//            rsp.setChangeFlag(YesOrNoNumberEnum.NO.getCode());
//        }
        rsp.setChangeFlag(nodiff ? YesOrNoNumberEnum.NO.getCode() : YesOrNoNumberEnum.YES.getCode());
        return rsp;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean autoAdjustmentRentActual(ContractBaseInfo contractBaseInfo, Long paymentId) {
        ContractPriceDetailRSP priceDetailRSP = contractPriceService.detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
        RepayRateEnum repayRateEnum = RepayRateEnum.of(priceDetailRSP.getRepayRate());
        if (Objects.isNull(repayRateEnum)) {
            throw new MithrasException("还款频率未知");
        }
        int lpr = Optional.ofNullable(priceDetailRSP.getLprPercent()).orElse(0);
        int lprAdd = Optional.ofNullable(priceDetailRSP.getLprAddPercent()).orElse(0);
        //基础利率= （LPR基准值 + LPR加点值）/1000000（百万），保留20位小数，四舍五入
        BigDecimal interestRate = BigDecimal.valueOf(lpr + lprAdd).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP);
        //名义货价
        long nominalPrice = Optional.ofNullable(priceDetailRSP.getNominalPrice()).orElse(0L);
        // 可以系统调整的按照步骤进行调整（复用页面导入接口，此处使用导入时使用的数据结构）
        List<CashFlowExcelModel> rentList = new LinkedList<>();
        // >>>>>>>>>>>>>>>>>>>> step-1、根据规则生成实际租金表（包含实际和标准）<<<<<<<<<<<<<<<<<<<<
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        Integer defaultCollectionDay = paymentBaseInfo.getDefaultCollectionDay();
        if (Objects.isNull(defaultCollectionDay)) {
            throw new MithrasException("收款日没有维护");
        }
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentId(paymentId);
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            throw new MithrasException("没有找到实际核销记录");
        }
        //找到最早的付款明细中的付款日期
        paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
        LocalDate actualPayDate = paymentActualDetailList.get(0).getPaidInDate();
        //第0期还款日期
        LocalDate standardZeroPhaseDate = FinancialUtil.ensureStandardZeroPhaseDate(defaultCollectionDay, actualPayDate, false);
        // 实付金额汇总
        long totalActualPayAmount = paymentActualDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
        //收款明细
        List<CollectionBaseInfo> collectionBaseInfoList = this.listActualCollection(paymentId);
        // 首期租金金额
        long firstRentAmount = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name())).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
        // 实收金额汇总
        long totalCollectionZeroPhaseAmount = collectionBaseInfoList.stream().filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
        CashFlowExcelModel zeroPhaseCashFlow = new CashFlowExcelModel();
        zeroPhaseCashFlow.setCashFlowPhase(0);
        zeroPhaseCashFlow.setCashFlowDate(actualPayDate);
        //第0期现金流金额 = 实收金额-汇总实付
        zeroPhaseCashFlow.setCashFlowAmount(Util.mithrasLong2BigDecimal((totalCollectionZeroPhaseAmount - totalActualPayAmount), 2, RoundingMode.HALF_UP));
        //剩余本金 = 授信金额(实付金额) -首期租金
        zeroPhaseCashFlow.setRemainingPrincipal(Util.mithrasLong2BigDecimal((totalActualPayAmount - firstRentAmount), 2, RoundingMode.HALF_UP));
        rentList.add(zeroPhaseCashFlow);
        // 中间期项数据取概算表
        List<ContractRentEstimate> rentEstimateList = SpringUtil.getBean(ContractRentEstimateService.class).listByContractId(contractBaseInfo.getId(), null);
        if (CollectionUtil.isEmpty(rentEstimateList)) {
            throw new MithrasException("概算租金表数据不存在");
        }
        int rentPhaseCount = rentEstimateList.size();
        // 查询结构化利息
        List<StructuredInterest> structuredInterestList = priceDetailRSP.getStructuredInterestList();
        Map<Integer, Long> structuredMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(structuredInterestList)) {
            structuredMap.putAll(structuredInterestList.stream().collect(Collectors.toMap(StructuredInterest::getPhase, StructuredInterest::getAmount, (a, b) -> b)));
        }
        //一期间隔月份
        int monthAdd = FinancialUtil.getRepayIntervalMonth(repayRateEnum);
        //是否规则还款
        boolean isByRule = RepayRateEnum.isByRule(repayRateEnum.name());
        Map<Integer, ContractRentEstimate> rentEstimateMap = rentEstimateList.stream().collect(Collectors.toMap(ContractRentEstimate::getCashFlowPhase, e -> e));
        boolean isSameDay = false;
        for (int i = 1; i <= rentPhaseCount; i++) {
            //概算表
            ContractRentEstimate currentRentEstimate = rentEstimateMap.get(i);
            CashFlowExcelModel cashFlow = new CashFlowExcelModel();
            cashFlow.setCashFlowPhase(i);
            // 计算当期还款日期
            LocalDate currentCashFlowDate;
            if (isByRule) {
                //规则还款
                currentCashFlowDate = standardZeroPhaseDate.plusMonths(i * monthAdd);
            } else {
                //不规则还款、按概算表取年月--取日的话存在概率异常
                currentCashFlowDate = LocalDate.of(currentRentEstimate.getCashFlowDate().getYear(), currentRentEstimate.getCashFlowDate().getMonthValue(), standardZeroPhaseDate.getDayOfMonth());
            }
            //最后一期特殊处理
            if (i == rentPhaseCount) {
                // 最后一期日期 = 第一笔投放日 + 租赁期限 - 1
                Integer months = priceDetailRSP.getMonthCount();
                if (Objects.nonNull(months)) {
                    currentCashFlowDate = actualPayDate.plusMonths(months).minusDays(1);
                } else {
                    // 如果没有租赁期限就用标准日期兜底吧
                    currentCashFlowDate = currentCashFlowDate.minusDays(1);
                }
            }
            cashFlow.setCashFlowDate(currentCashFlowDate);
            if (Objects.isNull(currentRentEstimate)) {
                rentList.add(cashFlow);
                continue;
            }
            //结构化利息
            long extraInterest = Optional.ofNullable(structuredMap.get(i)).orElse(0L);
            //本金
            cashFlow.setPrincipal(Optional.ofNullable(currentRentEstimate.getPrincipal()).map(e -> Util.mithrasLong2BigDecimal(e, 2, RoundingMode.HALF_UP)).orElse(BigDecimal.valueOf(0)));
            // 第1期和最后1期利息和现金流需要特殊处理
            if ((i == 1) || (i > 1 && i == rentPhaseCount)) {
                if (i == 1) {
                    int actualDay = actualPayDate.getDayOfMonth();
                    int payDay = currentCashFlowDate.getDayOfMonth();
                    isSameDay = Objects.equals(actualDay, payDay);
                }
                //20260107调整 规则还款 还款日与实际起租日为对日 则第一期租金和最后一期租金应与概算表一致，而不是统一按照天数占用计算。
                if (isByRule &&  isSameDay){
                    //当期利息 = 上一期剩余本金*（基础利率/年还款次数）
                    long currentInterest = Optional.ofNullable(currentRentEstimate.getInterest()).orElse(0L);
                    cashFlow.setInterest(Util.mithrasLong2BigDecimal((extraInterest + currentInterest), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
                } else {
                    CashFlowExcelModel lastCashFlow = rentList.get(i - 1);
                    //当期天数 = （上一期还款日期-当前期次】
                    long days = LocalDateTimeUtil.between(lastCashFlow.getCashFlowDate().atStartOfDay(), cashFlow.getCashFlowDate().atStartOfDay(), ChronoUnit.DAYS);
                    // 当期利息 = 上一期剩余本金*（基础利率/360 * 当期天数）
                    BigDecimal interest = lastCashFlow.getRemainingPrincipal().multiply(interestRate.divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(days)));
                    //当前利息
                    cashFlow.setInterest(interest.add(Util.mithrasLong2BigDecimal(extraInterest, 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
                }
            } else {
                long currentInterest = Optional.ofNullable(currentRentEstimate.getInterest()).orElse(0L);
                cashFlow.setInterest(Util.mithrasLong2BigDecimal((extraInterest + currentInterest), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
            }
            cashFlow.setRent(cashFlow.getPrincipal().add(cashFlow.getInterest()));
            cashFlow.setCashFlowAmount(cashFlow.getRent());
            cashFlow.setRemainingPrincipal(Util.mithrasLong2BigDecimal(currentRentEstimate.getRemainingPrincipal(), 2, RoundingMode.HALF_UP));
            if (i == rentPhaseCount) {
                // 最后一期现金流需要用租金 - 保证金 + 名义价款
                long earnest = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name())).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                cashFlow.setCashFlowAmount(cashFlow.getRent().subtract(Util.mithrasLong2BigDecimal(earnest, 2, RoundingMode.HALF_UP)).add(Util.mithrasLong2BigDecimal(nominalPrice, 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
            }
            rentList.add(cashFlow);
        }
        // >>>>>>>>>>>>>>>>>>>> step-2、保存相关数据 <<<<<<<<<<<<<<<<<<<<
        List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            throw new MithrasException("没有找到借据信息");
        }
        ContractReceipt contractReceipt = contractReceiptList.get(0);
        // 复用用户操作接口来保持逻辑一致
        SpringUtil.getBean(ContractService.class).prepareContractOperation(contractBaseInfo.getId(), ContractOperationEnum.START_RENT.name());
        ContractRentActualImportREQ req = new ContractRentActualImportREQ();
        req.setPaymentId(paymentId);
        req.setContractId(contractBaseInfo.getId());
        req.setReceiptId(contractReceipt.getId());
        // 重新查询一下合同信息
        contractBaseInfo = contractBaseInfoMapper.selectById(contractBaseInfo.getId());
        log.info("系统自动生成实际租金表[contractCode:{}, rentList:{}]", contractBaseInfo.getContractCode(), JSONUtil.toJsonStr(rentList));
        contractRentActualService.importExcelBySystem(req, rentList, contractBaseInfo);
        //刷新
        contractReceipt = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId()).get(0);
        // >>>>>>>>>>>>>>>>>>>> step-3、根据step-1中的实际租金表计算IRR <<<<<<<<<<<<<<<<<<<<
        IRRCalculateResultRSP irrCalculateResultRSP = contractRentActualService.calculateIRR(contractReceipt.getId(), false);
        // 补全实际irr
        contractReceipt.setActualIrr(new BigDecimal(irrCalculateResultRSP.getIrr()).multiply(BigDecimal.valueOf(1000000)).intValue());
        contractReceiptMapper.updateById(contractReceipt);
        // >>>>>>>>>>>>>>>>>>>> step-4、比较计算的IRR和最低IRR来决定是否要自动发起流程 <<<<<<<<<<<<<<<<<<<<
//        if (Objects.nonNull(paymentBaseInfo.getLowestIrr())) {
//            BigDecimal lowestIrr = BigDecimal.valueOf(paymentBaseInfo.getLowestIrr()).divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP);
//            BigDecimal irr = new BigDecimal(irrCalculateResultRSP.getIrr());
//            return irr.compareTo(lowestIrr) >= 0;
//        }
//        return false;
        // modify 260202 自动起租，校验合同加权平均IRR是否大于最新通过的付款申请的最低IRR
        return contractService.checkCombinedIrr(contractBaseInfo.getId(), ContractOperationEnum.START_RENT.name(), null);
    }

    private List<CollectionBaseInfo> listActualCollection(Long paymentId) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getPaymentId, paymentId);
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(query);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return Collections.emptyList();
        }
        // 去掉租金（保留首期利息）
        collectionBaseInfoList.removeIf(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()) && Objects.nonNull(e.getPhase()) && e.getPhase() > 0);
        return collectionBaseInfoList;
    }

//    /**
//     * 系统调整租金表
//     **/
//    @Override
//    public boolean autoAdjustmentRentActual(ContractBaseInfo contractBaseInfo, Long paymentId) {
//        if (ObjectUtil.isEmpty(contractBaseInfo)) {
//            return false;
//        }
//        Long contractId = contractBaseInfo.getId();
//        //判断是否可以调整 1）还款频率 ≠不规则分期/⾮分期/空值；2)还款⽅式=等额本⾦ or 等额本息； 3）利息计算⽅式 = 实际利率法。
//        ContractPriceDetailRSP priceDetailRSP = contractPriceService.detail(new ContractPriceDetailREQ(contractId));
//        if (ObjectUtil.isEmpty(priceDetailRSP.getRepayRate()) || !RepayRateEnum.isByRule(priceDetailRSP.getRepayRate())
//                || !StrUtil.equalsAny(priceDetailRSP.getRentalCalcType(), RepayCalcType.DEBJ.name(), RepayCalcType.DEBX.name())
//                || !InterestWayEnum.ACTUAL_RATE.name().equals(priceDetailRSP.getInterestWay())) {
//            return false;
//        }
//        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
//        if (ObjectUtil.isEmpty(paymentBaseInfo)) {
//            return false;
//        }
//        if (ObjectUtil.isEmpty(paymentBaseInfo.getDefaultCollectionDay())) {
//            paymentBaseInfo.setDefaultCollectionDay(paymentBaseInfoService.getDefaultCollectionDay(paymentId));
//        }
//        //查询实际付款金额
//        Long paidAmount = SpringContextUtil.getBean(PaymentActualDetailService.class).calculatePaidAmount(Collections.singletonList(paymentId));
//        if ((paidAmount <= 0)) {
//            return false;
//        }
//        //查询借据信息
//        List<ContractReceipt> contractReceipts = contractReceiptMapper.selectList(Wrappers.<ContractReceipt>lambdaQuery()
//                .eq(ContractReceipt::getContractId, contractId)
//                .last(StringUtil.mysqlLimitOne()));
//        if (ObjectUtil.isEmpty(contractReceipts)) {
//            return false;
//        }
//        //查询已经使用借据
//        List<Long> useReceiptIds = paymentBaseInfoService.listByContractIds(Collections.singletonList(contractId)).stream().map(PaymentBaseInfo::getReceiptId).collect(Collectors.toList());
//        Long receiptId = null;
//        for (ContractReceipt contractReceipt : contractReceipts) {
//            //获取第一个没被使用的借据，理论上只会有一个
//            if (!useReceiptIds.contains(contractReceipt.getId())) {
//                receiptId = contractReceipt.getId();
//                break;
//            }
//        }
//        //查询借据信息
//        ContractReceipt contractReceipt = contractReceiptMapper.selectById(receiptId);
//        if (ObjectUtil.isEmpty(contractReceipt)) {
//            return false;
//        }
//        Long paymentPaidAmount = SpringContextUtil.getBean(PaymentActualDetailService.class).calculatePaidAmount(Collections.singletonList(paymentId));
//        //查询第一次付款
//        PaymentActualDetail paymentActualDetail = paymentActualDetailService.getOne(Wrappers.<PaymentActualDetail>lambdaQuery()
//                .eq(PaymentActualDetail::getPaymentId, paymentId)
//                .gt(PaymentActualDetail::getPaidInAmount, 0)
//                .orderByAsc(PaymentActualDetail::getPaidInDate)
//                .last(StringUtil.mysqlLimitOne()));
//        if (ObjectUtil.isEmpty(paymentActualDetail) || ObjectUtil.isEmpty(paymentActualDetail.getPaidInAmount())) {
//            return false;
//        }
//        //查询租金表
//        List<ContractRentActual> contractRentActuals = contractRentActualService.list(Wrappers.<ContractRentActual>lambdaQuery()
//                .eq(ContractRentActual::getContractId, contractId)
//                .eq(ContractRentActual::getReceiptId, contractReceipt.getId())
//                .orderByAsc(ContractRentActual::getCashFlowPhase)).stream().filter(e -> ObjectUtil.isEmpty(e.getCashFlowCode())).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(contractRentActuals)) {
//            return false;
//        }
//        //获取第一期日期
//        LocalDate paymentDate = paymentActualDetail.getPaidInDate();
//        //还款频率
//        int ruleMonth = RepayRateEnum.getRuleMonth(priceDetailRSP.getRepayRate());
//        if (ruleMonth <= 0) {
//            return false;
//        }
//        //第一期日期
//        LocalDate firstDate = paymentDate.plusDays(paymentBaseInfo.getDefaultCollectionDay() - paymentActualDetail.getPaidInDate().getDayOfMonth()).plusMonths(ruleMonth);
//        //合同利率
//        Integer lprPercent = priceDetailRSP.getLprPercent() == null ? 0 : priceDetailRSP.getLprPercent();
//        Integer lprAddPercent = priceDetailRSP.getLprAddPercent() == null ? 0 : priceDetailRSP.getLprAddPercent();
//        Integer contractLprPercent = lprPercent + lprAddPercent;
//        //获取最大期项
//        Integer maxPhase = contractRentActuals.get(contractRentActuals.size() - 1).getCashFlowPhase();
//        //查询结构化利息
//        List<StructuredInterest> structuredInterestList = priceDetailRSP.getStructuredInterestList();
//        Map<Integer, Long> structuredMap = new HashMap<>();
//        if (CollectionUtil.isNotEmpty(structuredInterestList)) {
//            structuredMap.putAll(structuredInterestList.stream().collect(Collectors.toMap(StructuredInterest::getPhase, StructuredInterest::getAmount, (a, b) -> b)));
//        }
//        Map<Integer, ContractRentActual> rentActualMap = contractRentActuals.stream().collect(Collectors.toMap(ContractRentActual::getCashFlowPhase, e -> e, (a, b) -> b));
//        //补充第0期 不在落0期
//       /* Long zeroInterest = structuredMap.get(0);
//        if (ObjectUtil.isNotEmpty(zeroInterest)) {
//            //获取第0期
//            ContractRentActual rentActual1 = contractRentActuals.get(0);
//            if (rentActual1.getCashFlowPhase() > 0) {
//                {
//                    ContractRentActual rentActual = BeanUtil.copyProperties(contractRentActuals.get(0), ContractRentActual.class, "id");
//                    rentActual.setRent(0L);
//                    rentActual.setInterest(0L);
//                    rentActual.setPrincipal(0L);
//                    rentActual.setCashFlowPhase(0);
//                    rentActual.setRemainingPrincipal(paymentPaidAmount);
//                    contractRentActualService.save(rentActual);
//                    contractRentActuals.add(rentActual);
//                }
//            }
//        }*/
//        for (ContractRentActual rentActual : contractRentActuals) {//调整时间 + 调整金额
//            if (ObjectUtil.equals(rentActual.getCashFlowPhase(), 0)) {
//                //第o期日期
//                rentActual.setCashFlowDate(firstDate.minusMonths(ruleMonth));
//            } else {
//                rentActual.setCashFlowDate(firstDate.plusMonths((long) ruleMonth * (rentActual.getCashFlowPhase() - 1)));
//                if (ObjectUtil.equals(rentActual.getCashFlowPhase(), 1)) {
//                    //第一期重新计算 【投放💰i ✖️ 合同利率/360 ✖️（第x期⽇期-实际投放⽇i）】
//                    rentActual.setInterest(calculateInterest(paidAmount, contractLprPercent, ChronoUnit.DAYS.between(paymentDate, rentActual.getCashFlowDate())).toBigInteger().longValue());
//                    //变更租金
//                    rentActual.setRent(LongUtil.null2zero(rentActual.getInterest()) + LongUtil.null2zero(rentActual.getPrincipal()));
//                } else if (ObjectUtil.equals(rentActual.getCashFlowPhase(), maxPhase)) {
//                    //获取上一期
//                    ContractRentActual preContractRentActual = rentActualMap.get(maxPhase - 1);
//                    //最后一期日期减一天
//                    rentActual.setCashFlowDate(rentActual.getCashFlowDate().minusDays(1));
//                    rentActual.setInterest(calculateInterest(LongUtil.null2zero(preContractRentActual.getRemainingPrincipal()), contractLprPercent, ChronoUnit.DAYS.between(preContractRentActual.getCashFlowDate(), rentActual.getCashFlowDate())).toBigInteger().longValue());
//                    //变更租金
//                    rentActual.setRent(LongUtil.null2zero(rentActual.getInterest()) + LongUtil.null2zero(rentActual.getPrincipal()));
//                }
//            }
//            //生成租金编号
//            rentActual.setCashFlowCode(ContractRentActualServiceImpl.getCashFlowCode(contractReceipt.getReceiptCode(), rentActual.getCashFlowPhase()));
//            //补充结构化利息
//            if (ObjectUtil.isNotEmpty(structuredMap.get(rentActual.getCashFlowPhase())) && rentActual.getCashFlowPhase() < 2) {
//                Long aLong = structuredMap.get(rentActual.getCashFlowPhase());
//                if (ObjectUtil.isNotEmpty(aLong)) {
//                    if (ObjectUtil.equals(rentActual.getCashFlowPhase(), 0)) {
//                        rentActual.setRent(aLong);
//                        rentActual.setInterest(aLong);
//                    } else {
//                        rentActual.setRent(LongUtil.null2zero(rentActual.getRent()) + aLong);
//                        rentActual.setInterest(LongUtil.null2zero(rentActual.getInterest()) + aLong);
//                    }
//                }
//            } else if (ObjectUtil.equals(rentActual.getCashFlowPhase(), maxPhase)) {
//                Long aLong = structuredMap.get(2);
//                if (ObjectUtil.isNotEmpty(aLong)) {
//                    rentActual.setRent(LongUtil.null2zero(rentActual.getRent()) + aLong);
//                    rentActual.setInterest(LongUtil.null2zero(rentActual.getInterest()) + aLong);
//                }
//            }
//        }
//        contractRentActualService.updateBatchById(contractRentActuals);
//        //需关联借据与付款关系
//        ContractRentActualImportREQ rentActualImportREQ = new ContractRentActualImportREQ();
//        rentActualImportREQ.setContractId(contractBaseInfo.getId());
//        rentActualImportREQ.setPaymentId(paymentId);
//        rentActualImportREQ.setReceiptId(receiptId);
//        contractRentActualService.doContractReceipt(rentActualImportREQ, contractBaseInfo);
//        //获取实际irr
//        IRRCalculateResultRSP irrCalculateResultRSP = contractRentActualService.calculateIRR(receiptId, false);
//        if (ObjectUtil.isEmpty(irrCalculateResultRSP)) {
//            return false;
//        }
//        //补全实际irr
//        ContractReceipt receipt = new ContractReceipt();
//        receipt.setId(receiptId);
//        receipt.setActualIrr(new BigDecimal(irrCalculateResultRSP.getIrr()).multiply(BigDecimal.valueOf(1000000)).intValue());
//        contractReceiptMapper.updateById(receipt);
//        //比较最低irr
//        if (new BigDecimal(irrCalculateResultRSP.getIrr()).multiply(BigDecimal.valueOf(100)).compareTo(LongUtil.tenThousand2Dollar(String.valueOf(paymentBaseInfo.getLowestIrr()))) >= 0) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }

    @Override
    public boolean compareActualIRR(Long receiptId, BigDecimal lowestIrr) {
        IRRCalculateResultRSP irrCalculateResultRSP = contractRentActualService.calculateIRR(receiptId, false);
        if (ObjectUtil.isEmpty(irrCalculateResultRSP)) {
            return true;
        }
        if (lowestIrr.compareTo(new BigDecimal(irrCalculateResultRSP.getIrr()).multiply(BigDecimal.valueOf(100))) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<Long, String> contractPulldown(Long clientId) {
        List<ContractBaseInfo> targetContracts =
                list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getClientId, clientId)
                        .eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
        return targetContracts.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getContractCode));
    }

    @Override
    public List<OcContractListDto> ocContractList(Long clientId) {
        List<OcContractListDto> ocContractListDtos = contractBaseInfoMapper.ocContractList(clientId);
        Map<Long, OcContractListDto> ocContractMap = ocContractListDtos.stream()
                .collect(Collectors.toMap(OcContractListDto::getId, item -> item));
        Map<Long, Long> remainingPrincipals = collectionBaseInfoService.sumRemainingUnpaidPrincipalByContract(new ArrayList<>(ocContractMap.keySet()));
        Map<Long, Long> depositBalances = marginBaseInfoService.getDepositBalances(ocContractMap.keySet());
        ocContractMap.forEach((id, item) -> {
            Long remainingPrincipal = remainingPrincipals.getOrDefault(id, 0L);
            Long depositBalance = depositBalances.getOrDefault(id, 0L);
            item.setRemainPrincipal(remainingPrincipal);
            item.setRemainDeposit(depositBalance);
            item.setRiskExposure(remainingPrincipal - depositBalance);
        });
        return new ArrayList<>(ocContractMap.values());
    }

    @Override
    public List<ContractLesseeInfo> getContractLessees(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractBaseInfoMapper.getContractLessees(contractIds);
    }

    @Override
    public List<ContractLesseeInfo> getContractLessees1(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractBaseInfoMapper.getContractLessees1(contractIds);
    }

    @Override
    public List<ContractGuarantorInfo> getContractGuarantors(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        return contractBaseInfoMapper.getContractGuarantors(contractIds);
    }

    @Override
    public List<ContractBaseInfo> listByClientId(Long clientId) {
        return contractBaseInfoMapper.listByClientIds(clientId);
    }

    @Override
    public List<ContractClientInfo> listClientsByContractId(Set<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return Collections.emptyList();
        }
        List<ClientRole> clientRoles = contractBaseInfoMapper.listClientsByContractId(contractIds);
        List<ContractClientInfo> res = new ArrayList<>();
        for (ClientRole clientRole : clientRoles) {
            if ("guarantor".equals(clientRole.getRole())) {
                List<Long> guarantorIds = JSON.parseArray(clientRole.getClientId(), Long.class);
                for (Long guarantorId : guarantorIds) {
                    ContractClientInfo contractClientInfo = new ContractClientInfo();
                    contractClientInfo.setClientId(guarantorId);
                    contractClientInfo.setRole(clientRole.getRole());
                    contractClientInfo.setContractId(clientRole.getContractId());
                    res.add(contractClientInfo);
                }
            } else {
                ContractClientInfo contractClientInfo = new ContractClientInfo();
                contractClientInfo.setClientId(Long.valueOf(clientRole.getClientId()));
                contractClientInfo.setRole(clientRole.getRole());
                contractClientInfo.setContractId(clientRole.getContractId());
                res.add(contractClientInfo);
            }
        }
        if (ObjectUtil.isEmpty(res)) {
            return Collections.emptyList();
        }
        Set<Long> allClientIds = res.stream().map(ContractClientInfo::getClientId).collect(Collectors.toSet());
        Map<Long, Client> clientMap = clientService.list(Wrappers.<Client>lambdaQuery().in(Client::getId, allClientIds)).stream().collect(Collectors.toMap(Client::getId, v -> v));
        for (ContractClientInfo re : res) {
            Client client = clientMap.get(re.getClientId());
            if (ObjectUtil.isEmpty(client)) {
                continue;
            }
            re.setName(clientMap.get(re.getClientId()).getClientName());
            if (client.getClientType().equals(ClientType.NORMAL.name())) {
                re.setCertificateType("身份证");
                re.setCertificateNumber(clientMap.get(re.getClientId()).getCertNumber());
            } else {
                re.setCertificateType("统一社会信用代码");
                re.setCertificateNumber(clientMap.get(re.getClientId()).getUscCode());
            }
        }
        Map<Long, List<ContractClientInfo>> resMap = res.stream().collect(Collectors.groupingBy(ContractClientInfo::getClientId));
        List<ContractClientInfo> resList = new ArrayList<>();
        resMap.forEach((key, value) -> {
            Map<String, List<ContractClientInfo>> roleMap = value.stream().collect(Collectors.groupingBy(ContractClientInfo::getRole));
            ContractClientInfo origin = value.get(0);
            ContractClientInfo contractClientInfo = new ContractClientInfo();
            contractClientInfo.setClientId(origin.getClientId());
            contractClientInfo.setName(origin.getName());
            contractClientInfo.setCertificateType(origin.getCertificateType());
            contractClientInfo.setCertificateNumber(origin.getCertificateNumber());
            if (roleMap.size() > 1) {
                contractClientInfo.setRole("承租人/担保人");
            } else {
                contractClientInfo.setRole("lessee".equals(origin.getRole()) ? "承租人" : "担保人");
            }
            resList.add(contractClientInfo);
        });
        return resList;
    }

    @Override
    public ClientOverdueInfoDto clientOverdueInfo(Long clientId) {
        ClientOverdueInfoDto clientOverdueInfoDto = contractBaseInfoMapper.clientOverdueInfo(clientId);
        if (ObjectUtil.isEmpty(clientOverdueInfoDto)) {
            clientOverdueInfoDto = new ClientOverdueInfoDto();
        }
        Map<Long, Long> riskExposureMap = clientService.clientStockRiskExposureMap(Collections.singletonList(clientId));
        clientOverdueInfoDto.setRiskExposure(riskExposureMap.getOrDefault(clientId, 0L));
        return clientOverdueInfoDto;
    }

    @Override
    public Map<Long, Long> getContractNominalPriceRemain(List<Long> contractIds, LocalDate endDate) {
        Map<Long, Long> rsps = new HashMap<>();
        //查询合同信息
        List<ContractBaseInfo> contractBaseInfos = this.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(isNotEmpty(contractIds), ContractBaseInfo::getId, contractIds)
                .in(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name())
        );

        if (CollectionUtil.isEmpty(contractBaseInfos)) {
            return rsps;
        }
        contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        Map<Long, ContractLeasePrice> contractId2LeasePriceMap = contractPriceService.listByContractIds(contractIds);

        //合同ID，名义价款核销金额
        Map<Long, Long> contractId2CollectionMap = collectionBaseInfoService.sumRemainingByContract(contractIds, endDate, CashFlowItemEnum.NOMINAL_PRICE.name());
        contractBaseInfos.forEach(e -> {
            ContractLeasePrice contractLeasePrice = contractId2LeasePriceMap.get(e.getId());
            if (ObjectUtil.isNotEmpty(contractLeasePrice) && LongUtil.null2zero(contractLeasePrice.getNominalPrice()) > 0) {
                //计算已经核销
                rsps.put(e.getId(), Math.max(LongUtil.null2zero(contractLeasePrice.getNominalPrice()) - LongUtil.null2zero(contractId2CollectionMap.get(e.getId())), 0));
            }
        });
        return rsps;
    }

    @Override
    public void contractListExport(Long clientId) {
        Client client = clientService.getById(clientId);
        try {
            List<OcContractListDto> orginalList = ocContractList(clientId);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("合同编号", "项目名称", "业务类型", "合同金额",
                    "当前逾期天数", "逾期金额", "逾期罚息", "剩余本金", "剩余保证金", "风险敞口", "合同状态", "项目主办", "业务部门"));
            for (OcContractListDto c : orginalList) {
                w.writeRow(ListUtil.of(
                        c.getContractCode(),
                        c.getProjName(),
                        ProjectBizType.valueOf(c.getBizType()).display,
                        c.getContractAmount() == null ? 0 : BigDecimal.valueOf(c.getContractAmount()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getOverdueDays(),
                        c.getOverdueRent() == null ? 0 : BigDecimal.valueOf(c.getOverdueRent()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getLateCharge() == null ? 0 : BigDecimal.valueOf(c.getLateCharge()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getRemainPrincipal() == null ? 0 : BigDecimal.valueOf(c.getRemainPrincipal()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getRemainDeposit() == null ? 0 : BigDecimal.valueOf(c.getRemainDeposit()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        c.getRiskExposure() == null ? 0 : BigDecimal.valueOf(c.getRiskExposure()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP),
                        ContractStatus.valueOf(c.getContractStatus()).display,
                        c.getProjSponsorName(),
                        c.getBizDeptName()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(client.getClientName() + "_合同信息" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出合同发生未知异常", e);
            throw new MithrasException("导出合同发生未知异常");
        }
    }

    //计算实际利息

    private BigDecimal calculateInterest(Long amount, Integer lprPercent, long days) {
        return BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(days)).multiply(BigDecimal.valueOf(lprPercent)).divide(BigDecimal.valueOf(YEAR_DAY), 10, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP);
    }

    private boolean diffHasChanges(LinkedList<DiffMatchPatch.Diff> diffs) {
        for (DiffMatchPatch.Diff base : diffs) {
            if (!DiffMatchPatch.Operation.EQUAL.equals(base.operation)) {
                return false;
            }
        }
        return true;
    }

    private void doContractTenantryBLZR(ContractBaseInfo contractBaseInfo, ProjPricingBaseInfo pricingBaseInfo) {
        List<ContractTenantry> toSaveList = new LinkedList<>();
        String creditorJson = pricingBaseInfo.getCreditorInfo();
        Assert.notBlank(creditorJson, () -> MithrasException.newException("债权人不能为空"));
        List<ClientInfo> creditorList = JSON.parseArray(creditorJson, ClientInfo.class);
        for (ClientInfo clientInfo : creditorList) {
            ContractTenantry contractTenantry = ContractTenantryConvert.toUnfinishedContractTenantry(clientInfo);
            contractTenantry.setContractId(contractBaseInfo.getId());
            contractTenantry.setLesseeType(CreditorDebtorTypeEnum.CREDITOR.name());
            contractTenantry.setStockRiskExposure(this.getStockRiskExposure(contractTenantry.getLesseeId(), contractBaseInfo.getId(), null));
            contractTenantry.setIsReport(1);
            toSaveList.add(contractTenantry);
        }
        String debtorJson = pricingBaseInfo.getDebtorInfo();
        if (StrUtil.isNotBlank(debtorJson)) {
            List<ClientInfo> debtorList = JSON.parseArray(pricingBaseInfo.getDebtorInfo(), ClientInfo.class);
            for (ClientInfo clientInfo : debtorList) {
                ContractTenantry contractTenantry = ContractTenantryConvert.toUnfinishedContractTenantry(clientInfo);
                contractTenantry.setContractId(contractBaseInfo.getId());
                contractTenantry.setLesseeType(CreditorDebtorTypeEnum.DEBTOR.name());
                contractTenantry.setStockRiskExposure(this.getStockRiskExposure(contractTenantry.getLesseeId(), contractBaseInfo.getId(), null));
                contractTenantry.setIsReport(1);
                toSaveList.add(contractTenantry);
            }
        }
        contractTenantryService.saveBatch(toSaveList);
        // 合同交易结构辅助表
        SpringUtil.getBean(ContractTradeStructureService.class).syncTradeStructure(contractBaseInfo.getId(), TradeStructureRoleEnum.LESSEE);
    }

    public void copyLendingMaterial(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        List<MaterialsList> paymentMaterialsLists = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PAYMENT.name())
                .eq(MaterialsList::getBelongId, paymentId)
                .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name()))
        );
        List<MaterialsList> contractMaterialsLists = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.CONTRACT.name())
                .eq(MaterialsList::getBelongId, paymentBaseInfo.getContractId())
                .in(MaterialsList::getMaterialsType, ListUtil.toList(LendingMaterialType.SIGN_PHOTO_VIDEO.name(), LendingMaterialType.LEASE_RELATED.name()))
        );
        for (MaterialsList materialsList : paymentMaterialsLists) {
            if ("APP_CONTRACT".equalsIgnoreCase(materialsList.getSourceBusinessKey())) {
                materialsListMapper.delete(Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getId, materialsList.getId()));
            }
        }
        for (MaterialsList materialsList : contractMaterialsLists) {
            MaterialsList newMaterialsList = BeanUtil.copyProperties(materialsList, MaterialsList.class, "id");
            newMaterialsList.setBelongId(paymentBaseInfo.getId());
            newMaterialsList.setBusinessType(BusinessModuleEnum.PAYMENT.name());
            newMaterialsList.setCreateTime(LocalDateTime.now());
            newMaterialsList.setUpdateTime(LocalDateTime.now());
            materialsListMapper.insert(newMaterialsList);
        }
    }


    private Boolean checkoutName(String clientName, String clientTycName) {
        //客户名称转为字符数组
        if (StringUtils.isBlank(clientName) && StringUtils.isBlank(clientTycName)) {
            return true;
        } else if (StringUtils.isBlank(clientName) || StringUtils.isBlank(clientTycName)) {
            return false;
        }
        char[] clientNameChars = clientName.toCharArray();
        char[] clientTycNameChars = clientTycName.toCharArray();
        if (clientNameChars.length != clientTycNameChars.length) {
            return false;
        }
        for (int i = 0; i < clientNameChars.length; i++) {
            if (ObjectUtil.notEqual(clientNameChars[i], clientTycNameChars[i])) {
                if (!String.valueOf(clientNameChars[i]).matches("[（）()]") || !String.valueOf(clientTycNameChars[i]).matches("[（）()]")) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<ContractBaseInfo> contractSearchList(String contractCode) {
        return contractBaseInfoMapper.contractSearchList(contractCode);
    }
}
