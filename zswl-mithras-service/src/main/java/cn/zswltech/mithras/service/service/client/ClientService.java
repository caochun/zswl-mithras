package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowExecutionApiService;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.execution.ExecutionProcessBaseReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.enums.TaskBusinessStatusEnum;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.client.*;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoAddREQ;
import cn.zswltech.mithras.dto.client.share.DataShareREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.job.dto.ClientAsLesseeInfoDTO;
import cn.zswltech.mithras.service.job.dto.ClientAsMessageInfoDTO;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckPlanBaseMapper;
import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.service.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.service.mapper.client.ClientCreateRecordMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.corp.*;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.dto.ClientListParam;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.*;
import cn.zswltech.mithras.service.mapper.model.*;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.datashare.DataShareMerchants;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.*;
import cn.zswltech.mithras.service.mapper.normal.NormalBankAccountMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalSpouseMapper;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.ClientRiskExposureResolver;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.bo.ClientAuthBO;
import cn.zswltech.mithras.service.service.bo.ClientBusinessHistoryBO;
import cn.zswltech.mithras.service.service.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.service.service.margin.MarginBaseInfoService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishTradeStructureService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewTradeStructureService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.service.service.share.DataShareMerchantsService;
import cn.zswltech.mithras.service.service.share.DataShareService;
import cn.zswltech.mithras.third.tianyancha.application.TycService;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasBaseInfo;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasRelatedEnterpriseInfo;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasShareholderInfo;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.service.constant.MithrasConstants.ERR_IN_TRANSFER;
import static cn.zswltech.mithras.service.constant.ResultMsg.*;
import static cn.zswltech.mithras.service.enums.JobEnum.*;
import static cn.zswltech.mithras.service.enums.client.ClientStatus.NEW;
import static cn.zswltech.mithras.service.others.Const.*;
import static cn.zswltech.mithras.service.others.MithrasException.err;

/**
 * @author junke
 */
@Slf4j
@Service
public class ClientService extends ServiceImpl<ClientMapper, Client> implements FlowEndEventProcessor, ClientRiskExposureResolver {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private CorpShareholderInfoMapper shareholderInfoMapper;
    @Resource
    private CorpShareHolderInfoService corpShareHolderInfoService;
    @Resource
    private CorpRelatedEnterpriseMapper relatedEnterpriseMapper;
    @Resource
    private CorpRelatedEnterpriseService corpRelatedEnterpriseService;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private TycService tycService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource(name = "userServiceAPI")
    private UserService userService;
    @Resource
    private SysUserService sysUserService;
    @Autowired
    private DataShareMerchantsService dataShareMerchantsService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ProjEstablishService projEstablishService;
    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private CorpAddressInfoMapper addressInfoMapper;
    @Resource
    private CorpAddressInfoService corpAddressInfoService;
    @Resource
    private CorpContactInfoMapper contactInfoMapper;
    @Resource
    private CorpSubjectItemMapper subjectItemMapper;
    @Resource
    private CorpBankAccountMapper bankAccountMapper;
    @Resource
    private CorpBankAccountService corpBankAccountService;
    @Resource
    private CorpBondInfoMapper bondInfoMapper;
    @Resource
    private CorpBondInfoService corpBondInfoService;
    @Resource
    private CorpAddressInfoLibMapper addressInfoLibMapper;
    @Resource
    private CorpCommerceInfoLibMapper commerceInfoLibMapper;
    @Resource
    private CorpRelatedEnterpriseLibMapper relatedEnterpriseLibMapper;
    @Resource
    private CorpContactInfoLibMapper contactInfoLibMapper;
    @Resource
    private CorpShareholderInfoLibMapper shareholderInfoLibMapper;
    @Resource
    private CorpBankAccountLibMapper bankAccountLibMapper;
    @Resource
    private CorpBondInfoLibMapper bondInfoLibMapper;
    @Resource
    private NormalBaseInfoMapper normalBaseInfoMapper;
    @Resource
    private NormalBankAccountMapper normalBankAccountMapper;
    @Resource
    private NormalSpouseMapper normalSpouseMapper;
    @Resource
    private NormalBaseInfoLibMapper normalBaseInfoLibMapper;
    @Resource
    private NormalBankAccountLibMapper normalBankAccountLibMapper;
    @Resource
    private NormalSpouseLibMapper normalSpouseLibMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private CorpCommerceInfoMapper corpCommerceInfoMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ProjReviewLeasePriceLibService projReviewLeasePriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService projReviewFactoringPriceLibService;
    @Resource
    private ProjReviewAocPriceLibService projReviewAocPriceLibService;
    @Resource
    private ProjReviewPriceConverter projReviewPriceConverter;
    @Resource
    private ClientBusinessHistoryService clientBusinessHistoryService;
    @Resource
    private SystemConfigMapper systemConfigMapper;
    @Resource
    private DataShareService dataShareService;
    @Resource
    private CorpContactInfoService corpContactInfoService;
    @Resource
    private NewCorpCommerceInfoMapper newCorpCommerceInfoMapper;
    @Resource
    private NewCorpCommerceInfoLibMapper newCorpCommerceInfoLibMapper;
    @Resource
    private NewCorpContactInfoMapper newCorpContactInfoMapper;
    @Resource
    private NewCorpContactInfoLibMapper newCorpContactInfoLibMapper;
    @Resource
    private NewCorpAddressInfoMapper newCorpAddressInfoMapper;
    @Resource
    private NewCorpAddressInfoLibMapper newCorpAddressInfoLibMapper;
    @Resource
    private NewCorpBondInfoMapper newCorpBondInfoMapper;
    @Resource
    private NewCorpBondInfoLibMapper newCorpBondInfoLibMapper;
    @Resource
    private NewCorpShareholderInfoMapper newCorpShareholderInfoMapper;
    @Resource
    private NewCorpShareholderInfoLibMapper newCorpShareholderInfoLibMapper;
    @Resource
    private NewCorpRelatedEnterpriseMapper newCorpRelatedEnterpriseMapper;
    @Resource
    private NewCorpRelatedEnterpriseLibMapper newCorpRelatedEnterpriseLibMapper;
    @Resource
    private NewCorpBankAccountMapper newCorpBankAccountMapper;
    @Resource
    private NewCorpBankAccountLibMapper newCorpBankAccountLibMapper;
    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private NewAfterLeaseCheckPlanClientMapper newAfterLeaseCheckPlanClientMapper;
    @Resource
    private NewAfterLeaseCheckPlanBaseMapper newAfterLeaseCheckPlanBaseMapper;
    @Resource
    private ClientAuthorityUtil clientAuthorityUtil;
    @Resource
    private ClientUserRefService clientUserRefService;
    @Resource
    private ClientService clientService;
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupReviewBaseInfoService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private AfterLeaseCheckPlanClientService checkPlanClientService;
    @Resource
    private NewAfterLeaseCheckPlanBaseMapper leaseCheckPlanBaseMapper;
    @Resource
    private FundFinancingBaseInfoMapper financingBaseInfoMapper;
    @Resource
    private FundDirectFinancingBaseInfoMapper directFinancingBaseInfoMapper;
    @Resource
    private FundReceiptRepayBaseInfoMapper repayBaseInfoMapper;
    @Resource
    private FundReceiptRepayCashFlowMapper receiptRepayCashFlowMapper;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private ClientChangeCheckService clientChangeCheckService;
    @Resource
    private FlowExecutionApiService executionApiService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private CommonProcessPrepareService prepareService;


    public void tryReleaseClient(Client client) {
        log.info("准备执行客户释放逻辑[clientName:{}]", client.getClientName());
        Long clientId = client.getId();
        // 获取当前管护权信息
        ClientAuthority clientAuthority = clientAuthorityService.getSpecificClientManagerAuthority(clientId);
        // 收集各阶段业务信息
        ClientAsLesseeInfoDTO clientAsLesseeInfoDTO = this.collectInfo(clientId, clientAuthority);
        // 判断所有释放条件
        // 管护权获取时间
        // 判断一下当前时间和权限获取时间是否小于获取管护权时间限制
        int managerAuthLimitDays = 60;
        long acquireAuthDays = LocalDateTimeUtil.between(clientAuthority.getCreateTime().toLocalDate().atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
        if (acquireAuthDays <= managerAuthLimitDays) {
            log.info("不释放客户-获取管护权未超过{}天", managerAuthLimitDays);
            return;
        }
        // 立项阶段条件 - 项目经理通过【客户权限创建申请】、【客户权限变更申请】、【客户移交】的方式获得客户管护权后，超过60天仍未有立项审批通过的项目，系统自动释放该客户权限
        boolean projEstablishCondition = this.projEstablishStage(clientAuthority, clientAsLesseeInfoDTO, managerAuthLimitDays);
        // 评审阶段条件1 - 项目立项流程审批通过生效后，超过90天项目评审流程中部门分管领导未审批通过，则系统系统自动释放该客户权限
        boolean projReviewCondition1 = this.projReviewStage1(clientAsLesseeInfoDTO);
        // 评审阶段条件2 - 项目评审流程审批通过的客户，如超过365天未实际投放，则系统系统自动释放该客户权限
        boolean projReviewCondition2 = this.projReviewStage2(clientAsLesseeInfoDTO);
        // 合同阶段条件 - 合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
        Set<Long> canViewUserIds = new HashSet<>();
        boolean contractCondition = this.contractStage(clientAsLesseeInfoDTO, canViewUserIds);
        // 兜底逻辑
        Map<Long, Long> map = this.clientStockRiskExposureMap(Collections.singletonList(clientId));
        boolean riskExposure = Optional.ofNullable(map.get(clientId)).orElse(0L) > 0;
        if (riskExposure) {
            // 有敞口的一定不释放
            log.info("不释放客户-风险敞口大于0");
            return;
        }
        // 满足一个条件即释放客户
        log.info("客户释放判断结果[立项:{}, 评审1:{}, 评审2:{}, 合同:{}]", projEstablishCondition, projReviewCondition1, projReviewCondition2, contractCondition);
        if (projEstablishCondition || projReviewCondition1 || projReviewCondition2 || contractCondition) {
            // 客户释放前进行二次判断（二次判断作为逻辑漏洞的补丁，是后续补充的，为了不改动原有已经覆盖绝大多数场景的逻辑，故不直接在原逻辑上改动）
            // 补丁用于解决在后一阶段才被纳入交易结构的场景（比如说立项没有，评审才有）
            boolean release = this.releaseClientDoubleCheck(client, clientAuthority);
            if (!release) {
                log.info("客户释放二次检查结果[{}]", release);
                return;
            }
            String batchNo = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
            this.releaseClient(batchNo, clientId, canViewUserIds);
            log.info("释放客户完成[batch:{}, clientId:{}, canViewUserIds:{}]", batchNo, clientId, JSONUtil.toJsonStr(canViewUserIds));
        }
    }

    public void trySendClientNotice(Client client) {
        log.info("准备检查项目进展[clientName:{}]", client.getClientName());
        Long clientId = client.getId();
        // 收集各阶段业务信息
        ClientAsMessageInfoDTO clientAsMessageInfoDTO = this.collectMessageInfo(clientId);
        // 获取当前管护权信息
        ClientAuthority clientAuthority = clientAuthorityService.getSpecificClientManagerAuthority(clientId);
        //项目无进展
        projNoProgress(client, clientAuthority, clientAsMessageInfoDTO);
    }

    private void projNoProgress(Client client, ClientAuthority clientAuthority, ClientAsMessageInfoDTO clientAsMessageInfoDTO) {
        int limitDays = 31;
        LocalDate startDate = clientAuthority.getCreateTime().toLocalDate();
        // 判断一下当前时间和权限获取时间是否小于limitDays
        long acquireAuthDays = LocalDateTimeUtil.between(startDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
        if (acquireAuthDays < limitDays) {
            log.info("projEstablishStage-获取管护权未超过{}天", limitDays);
            return;
        }
        // 客户获取管护权30天没有立项
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = clientAsMessageInfoDTO.getProjEstablishBaseInfoList();
        if (CollectionUtil.isEmpty(projEstablishBaseInfoList)) {
            if (acquireAuthDays == limitDays) {
                log.info("projEstablishStage-释放客户-获取管护权超过{}天但没有找到任何立项", limitDays);
                sendNoticeWithoutEstablish(client, clientAuthority);
                return;
            }
        }
        //立项新建30天没有审批通过
        List<ProjEstablishBaseInfo> filterList = projEstablishBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.NEW.name()))
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(filterList)) {
            for (ProjEstablishBaseInfo projEstablishBaseInfo : filterList) {
                long days = LocalDateTimeUtil.between(projEstablishBaseInfo.getCreateTime(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitDays) {
                    sendNoticeEstablishNoApproval(client, clientAuthority, projEstablishBaseInfo);
                    return;
                }
            }
        }
        //立项审批通过30天没有新建评审
        List<ProjEstablishBaseInfo> effectList = projEstablishBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name()))
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId()))
                .collect(Collectors.toList());
        if (!effectList.isEmpty()
                && (clientAsMessageInfoDTO.getProjReviewBaseInfoList() == null || clientAsMessageInfoDTO.getProjReviewBaseInfoList().isEmpty())) {
            // 遍历判断
            for (ProjEstablishBaseInfo projEstablishBaseInfo : filterList) {
                // 生效立项找流程通过时间
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setBusinessKey(projEstablishBaseInfo.getId().toString());
                processPageReq.setModelKey(ProcessModelTypeEnum.ProjEstablishCreateFlow.name());
                processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
                if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                    continue;
                }
                ProcessResp processResp = processResult.getContents().get(0);
                if (Objects.isNull(processResp.getEndTime())) {
                    continue;
                }
                LocalDate establishPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
                // 判断立项生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitDays) {
                    sendNoticeEstablishNoApproval(client, clientAuthority, projEstablishBaseInfo);
                    return;
                }
            }
        }
        //项目评审30天没有审批通过
        List<ProjReviewBaseInfo> projReviewBaseInfoList = clientAsMessageInfoDTO.getProjReviewBaseInfoList();
        List<ProjReviewBaseInfo> reviewFilterList = projReviewBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjReviewStatus(), RecordStatus.NEW.name()))
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(reviewFilterList)) {
            for (ProjReviewBaseInfo projReviewBaseInfo : reviewFilterList) {
                long days = LocalDateTimeUtil.between(projReviewBaseInfo.getCreateTime(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitDays) {
                    sendNoticeReviewNoApproval(client, clientAuthority, projReviewBaseInfo);
                    return;
                }
            }
        }
        //评审审批通过30天没有新建合同
        List<ProjReviewBaseInfo> effectReviewList = projReviewBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name()))
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId())).collect(Collectors.toList());
        if (!effectList.isEmpty()
                && (clientAsMessageInfoDTO.getContractBaseInfoList() == null || clientAsMessageInfoDTO.getContractBaseInfoList().isEmpty())) {
            // 遍历判断
            for (ProjReviewBaseInfo projReviewBaseInfo : effectReviewList) {
                // 生效立项找流程通过时间
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setBusinessKey(projReviewBaseInfo.getId().toString());
                processPageReq.setModelKey(ProcessModelTypeEnum.ProjReviewCreateFlow.name());
                processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
                if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                    continue;
                }
                ProcessResp processResp = processResult.getContents().get(0);
                if (Objects.isNull(processResp.getEndTime())) {
                    continue;
                }
                LocalDate establishPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
                // 判断评审生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitDays) {
                    sendNoticeReviewNoApproval(client, clientAuthority, projReviewBaseInfo);
                    return;
                }
            }
        }
        //合同创建30天没有审批通过
        List<ContractBaseInfo> contractBaseInfoList = clientAsMessageInfoDTO.getContractBaseInfoList();
        List<ContractBaseInfo> newContractList = contractBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getContractStatus(), ContractStatus.NEW.name()))
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newContractList)) {
            for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                long days = LocalDateTimeUtil.between(contractBaseInfo.getCreateTime(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitDays) {
                    sendNoticeContractNoApproval(client, clientAuthority, contractBaseInfo);
                    return;
                }
            }
        }
        //合同审批通过30天没有付款申请
        List<ContractBaseInfo> effectContractList = contractBaseInfoList.stream()
                .filter(e -> Objects.equals(e.getContractStatus(), ContractStatus.TAKE_EFFECT.name()))
                .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId())).collect(Collectors.toList());
        if (!effectList.isEmpty()
                && (clientAsMessageInfoDTO.getPaymentBaseInfoList() == null || clientAsMessageInfoDTO.getPaymentBaseInfoList().isEmpty())) {
            // 遍历判断
            for (ContractBaseInfo contractBaseInfo : effectContractList) {
                // 生效立项找流程通过时间
                ProcessPageReq processPageReq = new ProcessPageReq();
                processPageReq.setBusinessKey(contractBaseInfo.getId().toString());
                processPageReq.setModelKey(ProcessModelTypeEnum.ContractCreateFlow.name());
                processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
                if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                    continue;
                }
                ProcessResp processResp = processResult.getContents().get(0);
                if (Objects.isNull(processResp.getEndTime())) {
                    continue;
                }
                LocalDate establishPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
                // 判断评审生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitDays) {
                    sendNoticeContractNoApproval(client, clientAuthority, contractBaseInfo);
                    return;
                }
            }
        }
        //付款申请30天没有审批通过
        List<PaymentBaseInfo> paymentBaseInfoList = clientAsMessageInfoDTO.getPaymentBaseInfoList();
        if (paymentBaseInfoList != null && !paymentBaseInfoList.isEmpty()) {
            List<PaymentBaseInfo> newPaymentList = paymentBaseInfoList.stream()
                    .filter(e -> Objects.equals(e.getPaymentStatus(), PaymentStatusEnum.NEW.name()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newPaymentList)) {
                for (PaymentBaseInfo paymentBaseInfo : newPaymentList) {
                    long days = LocalDateTimeUtil.between(paymentBaseInfo.getCreateTime(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                    if (days == limitDays) {
                        sendNoticePaymentNoApproval(client, clientAuthority, paymentBaseInfo);
                        return;
                    }
                }
            }
        }

        //付款申请通过30天没有核销
        if (paymentBaseInfoList != null && !paymentBaseInfoList.isEmpty()) {
            List<PaymentBaseInfo> effectPaymentList = paymentBaseInfoList.stream().filter(e -> Objects.equals(e.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name())).collect(Collectors.toList());
            if (!effectList.isEmpty()
                    && (clientAsMessageInfoDTO.getPaymentActualDetailList() == null || clientAsMessageInfoDTO.getPaymentActualDetailList().isEmpty())) {
                // 遍历判断
                for (PaymentBaseInfo paymentBaseInfo : effectPaymentList) {
                    // 生效立项找流程通过时间
                    ProcessPageReq processPageReq = new ProcessPageReq();
                    processPageReq.setBusinessKey(paymentBaseInfo.getId().toString());
                    processPageReq.setModelKey(ProcessModelTypeEnum.PaymentCreateFlow.name());
                    processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                    cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
                    if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                        continue;
                    }
                    ProcessResp processResp = processResult.getContents().get(0);
                    if (Objects.isNull(processResp.getEndTime())) {
                        continue;
                    }
                    LocalDate establishPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
                    // 判断评审生效是否已经超过limitDays天
                    long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                    if (days == limitDays) {
                        sendNoticePaymentNoApproval(client, clientAuthority, paymentBaseInfo);
                        return;
                    }
                }
            }
        }
    }


    public void newAfterLeaseCheckPlanClient(int limitedDays) {
        List<NewAfterLeaseCheckPlanBase> newAfterLeaseCheckPlanClientList = leaseCheckPlanBaseMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanBase::getPlanStatus, AfterLeaseCheckPlanStatusEnum.CHECKING.name()));
        if (newAfterLeaseCheckPlanClientList.isEmpty()) {
            log.info("租后检查计划状态没检查中");
            return;
        }
        List<NewAfterLeaseCheckPlanBase> res = new ArrayList<>();
        for (NewAfterLeaseCheckPlanBase leaseCheckPlanBase : newAfterLeaseCheckPlanClientList) {
            LocalDate deadline = leaseCheckPlanBase.getDeadLine();
            if (deadline == null) continue;
            // 判断评审生效是否已经超过limitDays天
            long days = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), deadline.atStartOfDay(), ChronoUnit.DAYS);
            if (days == limitedDays) {
                res.add(leaseCheckPlanBase);
            }
        }
        if (res.isEmpty()) {
            return;
        }
        List<Long> planIds = res.stream().map(NewAfterLeaseCheckPlanBase::getId).collect(Collectors.toList());
        LambdaQueryWrapper<NewAfterLeaseCheckPlanClient> query = Wrappers.lambdaQuery();
        query.in(NewAfterLeaseCheckPlanClient::getPlanId, planIds);
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = checkPlanClientService.list(query);
        if (CollectionUtil.isEmpty(checkPlanClientList)) {
            log.info("租后检查计划状态为检查中的客户为空");
            return;
        }
        Map<Long, List<NewAfterLeaseCheckPlanClient>> map = checkPlanClientList.stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanClient::getPlanId));
        for (NewAfterLeaseCheckPlanBase planBase : res) {
            sendNoticeLeaseCheckPlanBase(planBase, map.get(planBase.getId()));
        }
    }


    public void accountExpireNotice(int limitedDays) {
        List<UserDO> userDOList = userService.selectAll();
        for (UserDO userDO : userDOList) {
            LocalDate expireDate = LocalDateTimeUtil.of(userDO.getExpiration()).toLocalDate();
            // 判断评审生效是否已经超过limitDays天
            long days = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), expireDate.atStartOfDay(), ChronoUnit.DAYS);
            if (days == limitedDays) {
                sendNoticeExpireAccount(userDO);
            }
        }
    }


    public void fundReceiptRepayNotice(int limitedDays) {
        //查询间融
        List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        if (!fundFinancingBaseInfoList.isEmpty()) {
            Set<Long> financingFilterIds = fundFinancingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
            Map<Long, FundFinancingBaseInfo> financingBaseInfoMap = fundFinancingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e));
            List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoList = repayBaseInfoMapper.selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getFinancingId, financingFilterIds));
            List<Long> receiptRepayIds = fundReceiptRepayBaseInfoList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
            Map<Long, FundReceiptRepayBaseInfo> receiptRepayBaseInfoMap = fundReceiptRepayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, e -> e));
            List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlowList = receiptRepayCashFlowMapper.selectList(
                    Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                            .in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayIds)
                            .orderByAsc(FundReceiptRepayCashFlow::getRepayDate));
            Map<Long, List<FundReceiptRepayCashFlow>> receiptRepayMap = fundReceiptRepayCashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId));
            for (FundReceiptRepayCashFlow fundReceiptRepayCashFlow : fundReceiptRepayCashFlowList) {
                LocalDate expireDate = LocalDateTimeUtil.of(fundReceiptRepayCashFlow.getRepayDate()).toLocalDate();
                // 判断评审生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), expireDate.atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitedDays && !CashFlowState.WRITTEN_OFF.name().equals(fundReceiptRepayCashFlow.getWriteOffState())) {
                    sendNoticeReceiptRepayCash(financingBaseInfoMap.get(fundReceiptRepayCashFlow.getFinancingId()), fundReceiptRepayCashFlow, receiptRepayBaseInfoMap.get(fundReceiptRepayCashFlow.getReceiptRepayId()));
                }
            }
        }
        //直融查询
        List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = directFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .eq(FundDirectFinancingBaseInfo::getObsolete, false));
        if (!fundDirectFinancingBaseInfoList.isEmpty()) {
            Set<Long> directFinancingFilterIds = fundDirectFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toSet());
            Map<Long, FundDirectFinancingBaseInfo> directFinancingBaseInfoMap = fundDirectFinancingBaseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e));
            List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfoList = repayBaseInfoMapper.selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getFinancingId, directFinancingFilterIds));
            List<Long> receiptRepayIds = fundReceiptRepayBaseInfoList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
            Map<Long, FundReceiptRepayBaseInfo> receiptRepayBaseInfoMap = fundReceiptRepayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, e -> e));
            List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlowList = receiptRepayCashFlowMapper.selectList(
                    Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                            .in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayIds)
                            .orderByAsc(FundReceiptRepayCashFlow::getRepayDate));
            Map<Long, List<FundReceiptRepayCashFlow>> receiptRepayMap = fundReceiptRepayCashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId));
            for (FundReceiptRepayCashFlow fundReceiptRepayCashFlow : fundReceiptRepayCashFlowList) {
                LocalDate expireDate = LocalDateTimeUtil.of(fundReceiptRepayCashFlow.getRepayDate()).toLocalDate();
                // 判断评审生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), expireDate.atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitedDays && !CashFlowState.WRITTEN_OFF.name().equals(fundReceiptRepayCashFlow.getWriteOffState())) {
                    sendNoticeDirectReceiptRepayCash(directFinancingBaseInfoMap.get(fundReceiptRepayCashFlow.getFinancingId()), fundReceiptRepayCashFlow, receiptRepayBaseInfoMap.get(fundReceiptRepayCashFlow.getReceiptRepayId()));
                }
            }
        }
    }


    public void projSettle(int limitedDay) {
        LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
        contractQuery.eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name());
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractQuery);
        Map<Long, List<ContractBaseInfo>> groupByContract = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId())
                    .isNotNull(CollectionBaseInfo::getCode)
                    .orderByDesc(CollectionBaseInfo::getPlanCollectionDate));
            if (!collectionBaseInfoList.isEmpty()) {
                LocalDate establishPassDate = LocalDateTimeUtil.of(collectionBaseInfoList.get(0).getPlanCollectionDate()).toLocalDate();
                // 判断评审生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), establishPassDate.atStartOfDay(), ChronoUnit.DAYS);
                if (days == limitedDay) {
                    sendNoticeContractSeattle(contractBaseInfo, establishPassDate);
                }
            }
        }
    }

    private void sendNoticeWithoutEstablish(Client client, ClientAuthority clientAuthority) {
        Long deptId = null;
        Long belongSponsorId = clientAuthority.getUserId();
        List<OrgDO> deptList = sysUserService.getSpecificUserDeptList(belongSponsorId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        } else if (!deptList.isEmpty()) {
            deptId = deptList.get(0).getId();
        }
        Long finalDeptId = deptId;
        Long projmanager = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.projmanager.name());
        Long businesshead = sysUserService.getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Set<Long> newUserIds = new HashSet<>();
        newUserIds.add(clientAuthority.getUserId());
        newUserIds.add(projmanager);
        newUserIds.add(businesshead);
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(new ArrayList<>(newUserIds));
        addRequest.setPcurl(String.format("/customer/maintain/detail/%s?clientType=%s&domesticOrAbroad=%s",
                client.getId(), client.getClientType(), client.getDomesticOrAbroad()));
        addRequest.setContent(client.getClientName());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(String.format("%s已在客户创建阶段停留超过30天",
                client.getClientName()));
        addRequest.setMessageType(MessageTypeEnum.PROJECT_NO_PROGRESS.name());
        addRequest.setNoticeSource(NoticeSourceENUM.BIZ_REMINDER.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }

    private void sendNoticeEstablishNoApproval(Client client, ClientAuthority clientAuthority, ProjEstablishBaseInfo projEstablishBaseInfo) {
        Set<Long> newUserIds = new HashSet<>();
        newUserIds.add(projEstablishBaseInfo.getProjSponsorUserId());
        newUserIds.add(projEstablishBaseInfo.getBizDeptLeaderId());
        List<String> riskControlManagerIds = new ArrayList<>();
        riskControlManagerIds.addAll(JSON.parseObject(projEstablishBaseInfo.getRiskControlManagerId(), new TypeReference<List<String>>() {
                }));
        if (!riskControlManagerIds.isEmpty()) {
            for (String riskControlManagerId : riskControlManagerIds) {
                newUserIds.add(Long.valueOf(riskControlManagerId));
            }
        }
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(new ArrayList<>(newUserIds));
        addRequest.setPcurl(String.format("/project/establishment/detail/%s",
                projEstablishBaseInfo.getId()));
        addRequest.setContent(projEstablishBaseInfo.getProjName());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(String.format("%s已在项目立项阶段停留超过30天",
                client.getClientName()));
        addRequest.setMessageType(MessageTypeEnum.PROJECT_NO_PROGRESS.name());
        addRequest.setNoticeSource(NoticeSourceENUM.BIZ_REMINDER.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }


    public void sendNoticeReviewNoApproval(Client client, ClientAuthority clientAuthority, ProjReviewBaseInfo projReviewBaseInfo) {
        Set<Long> newUserIds = new HashSet<>();
        newUserIds.add(projReviewBaseInfo.getProjSponsorUserId());
        newUserIds.add(projReviewBaseInfo.getBizDeptLeaderId());
        newUserIds.add(projReviewBaseInfo.getRiskControlManagerId());
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(new ArrayList<>(newUserIds));
        addRequest.setPcurl(String.format("/project/review/detail/%s",
                projReviewBaseInfo.getId()));
        addRequest.setContent(projReviewBaseInfo.getProjName());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(String.format("%s已在项目评审阶段停留超过30天",
                client.getClientName()));
        addRequest.setMessageType(MessageTypeEnum.PROJECT_NO_PROGRESS.name());
        addRequest.setNoticeSource(NoticeSourceENUM.BIZ_REMINDER.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }


    public void sendNoticeContractNoApproval(Client client, ClientAuthority clientAuthority, ContractBaseInfo contractBaseInfo) {
        Set<Long> newUserIds = new HashSet<>();
        newUserIds.add(contractBaseInfo.getProjSponsorUserId());
        newUserIds.add(contractBaseInfo.getBizDeptLeaderId());
        newUserIds.add(contractBaseInfo.getRiskControlManagerId());
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(new ArrayList<>(newUserIds));
        addRequest.setPcurl(String.format("/contract/list/detail/%s",
                contractBaseInfo.getId()));
        addRequest.setContent(contractBaseInfo.getContractCode());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(String.format("%s已在合同管理阶段停留超过30天",
                client.getClientName()));
        addRequest.setMessageType(MessageTypeEnum.PROJECT_NO_PROGRESS.name());
        addRequest.setNoticeSource(NoticeSourceENUM.BIZ_REMINDER.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }

    private void sendNoticePaymentNoApproval(Client client, ClientAuthority clientAuthority, PaymentBaseInfo paymentBaseInfo) {
        Set<Long> newUserIds = new HashSet<>();
        newUserIds.add(clientAuthority.getUserId());
        newUserIds.add(paymentBaseInfo.getConBizDeptLeaderId());
        newUserIds.add(paymentBaseInfo.getConRiskControlManagerId());
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(new ArrayList<>(newUserIds));
        addRequest.setPcurl(String.format("/cpm/paymentApplication/detail/%s",
                paymentBaseInfo.getId()));
        addRequest.setContent(paymentBaseInfo.getPaymentCode());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(String.format("%s已在付款申请阶段停留超过30天",
                client.getClientName()));
        addRequest.setMessageType(MessageTypeEnum.PROJECT_NO_PROGRESS.name());
        addRequest.setNoticeSource(NoticeSourceENUM.BIZ_REMINDER.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }


    private void sendNoticeContractSeattle(ContractBaseInfo contractBaseInfo, LocalDate establishPassDate) {
        Client client = clientMapper.selectById(contractBaseInfo.getClientId());
        Set<Long> newUserIds = new HashSet<>();
        newUserIds.add(contractBaseInfo.getProjSponsorUserId());
        newUserIds.add(contractBaseInfo.getBizDeptLeaderId());
        OrgDO jhcwb = orgDOMapper.queryByCode("JHCWB");
        Long financialmanagerId = SpringContextHolder.getBean(SysUserService.class).getUserIdByOrgJob(jhcwb.getId(), JobEnum.financialmanager.name());
        newUserIds.add(financialmanagerId);
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(new ArrayList<>(newUserIds));
        addRequest.setPcurl(String.format("/contract/list/detail/%s",
                contractBaseInfo.getId()));
        addRequest.setContent(contractBaseInfo.getContractCode());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(String.format("%s的%s合同即将于%s(最后一期租金应收日)结清，请注意!",
                client.getClientName(), contractBaseInfo.getContractCode(), establishPassDate));
        addRequest.setMessageType(MessageTypeEnum.PROJECT_SETTLE.name());
        addRequest.setNoticeSource(NoticeSourceENUM.BIZ_REMINDER.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }


    private void sendNoticeLeaseCheckPlanBase(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase, List<NewAfterLeaseCheckPlanClient> planClientList) {
        for (NewAfterLeaseCheckPlanClient planClient : planClientList) {
            Set<Long> newUserIds = new HashSet<>();
            newUserIds.add(planClient.getBelongSponsorId());
            Long businessHeadId = sysUserService.getUserIdByOrgJob(planClient.getBelongDeptId(), JobEnum.businesshead.name());
            newUserIds.add(businessHeadId);
            MessageAddREQ addRequest = new MessageAddREQ();
            addRequest.setFrom("系统通知");
            addRequest.setTo(new ArrayList<>(newUserIds));
            addRequest.setPcurl(String.format("/afterLease/checkPlan/commonTemplate/%s",
                    planClient.getId()));
            addRequest.setContent(newAfterLeaseCheckPlanBase.getPlanName());
            addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
            addRequest.setNeedOa(false);
            addRequest.setRelation(String.format("距%s截止已不足15天，请在%s前完成租后检查任务",
                    newAfterLeaseCheckPlanBase.getPlanName(), newAfterLeaseCheckPlanBase.getDeadLine()));
            addRequest.setMessageType(MessageTypeEnum.AFTER_LEASE_CHECK.name());
            addRequest.setNoticeSource(NoticeSourceENUM.AFTER_LEASE_CHECK.name());
            messageService.sendMessage(messageConver.reqToMessage(addRequest));
        }
    }


    private void sendNoticeReceiptRepayCash(FundFinancingBaseInfo fundFinancingBaseInfo, FundReceiptRepayCashFlow fundReceiptRepayCashFlow, FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo) {
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(Collections.singletonList(fundFinancingBaseInfo.getFundManagerId()));
        addRequest.setPcurl(String.format("/financial/payment/detail/%s",
                fundReceiptRepayBaseInfo.getId()));
        addRequest.setContent(fundReceiptRepayBaseInfo.getReceiptRepayCode());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        List<FundOrganization> organizationList = organizationService.getByFinancingId(fundFinancingBaseInfo.getId());
        Long total = Optional.ofNullable(fundReceiptRepayCashFlow.getPrincipleAmount()).orElse(0L) + Optional.ofNullable(fundReceiptRepayCashFlow.getInterestAmount()).orElse(0L);
        addRequest.setRelation(String.format("%s第%s应还%s元需在%s还款，请关注",
                CollectionUtil.isNotEmpty(organizationList) ? Optional.ofNullable(organizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null) : null, fundReceiptRepayCashFlow.getPhase(), total, fundReceiptRepayCashFlow.getRepayDate()));
        addRequest.setMessageType(MessageTypeEnum.FUND_RECEIPT_REPAY.name());
        addRequest.setNoticeSource(NoticeSourceENUM.FUND_RECEIPT_REPAY.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }

    private void sendNoticeDirectReceiptRepayCash(FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, FundReceiptRepayCashFlow fundReceiptRepayCashFlow, FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo) {
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(Collections.singletonList(fundDirectFinancingBaseInfo.getFundManagerId()));
        addRequest.setPcurl(String.format("/financial/payment/detail/%s",
                fundReceiptRepayBaseInfo.getId()));
        addRequest.setContent(fundReceiptRepayBaseInfo.getReceiptRepayCode());
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        Long total = fundReceiptRepayCashFlow.getPrincipleAmount() + fundReceiptRepayCashFlow.getInterestAmount();
        addRequest.setRelation(String.format("%s第%s应还%s元需在%s还款，请关注",
                fundDirectFinancingBaseInfo.getProductName(), fundReceiptRepayCashFlow.getPhase(), total, fundReceiptRepayCashFlow.getRepayDate()));
        addRequest.setMessageType(MessageTypeEnum.FUND_RECEIPT_REPAY.name());
        addRequest.setNoticeSource(NoticeSourceENUM.FUND_RECEIPT_REPAY.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }

    public void sendNoticeExpireAccount(UserDO userDO) {
        List<Long> userIds = sysUserService.queryJobUserIds(JobEnum.InformationTechnologyPost.name());
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                MessageAddREQ addRequest = new MessageAddREQ();
                addRequest.setFrom("系统通知");
                addRequest.setTo(Collections.singletonList(userId));
                //addRequest.setPcurl("/permission/user");
                addRequest.setContent(userDO.getUserName());
                addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
                addRequest.setNeedOa(false);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String expireDate = dateFormat.format(userDO.getExpiration());
                addRequest.setRelation(String.format("用户名：%s 登陆账号：%s即将于%s过期，请提前处理",
                        userDO.getUserName(), userDO.getAccount(), expireDate));
                addRequest.setMessageType(MessageTypeEnum.ACCOUNT_EXPIRE.name());
                addRequest.setNoticeSource(NoticeSourceENUM.ACCOUNT_EXPIRE.name());
                messageService.sendMessage(messageConver.reqToMessage(addRequest));
            }
        }
    }

    private boolean projEstablishStage(ClientAuthority clientAuthority, ClientAsLesseeInfoDTO clientAsLesseeInfoDTO, int limitDays) {
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList();
        if (CollectionUtil.isNotEmpty(projEstablishBaseInfoList)) {
            List<ProjEstablishBaseInfo> filterList = projEstablishBaseInfoList.stream()
                    .filter(e -> Objects.equals(e.getProjSponsorUserId(), clientAuthority.getUserId()))
                    .filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(filterList)) {
                // 2026-01-26去掉该逻辑，存在该逻辑会导致唯一的合同结清后第2天客户就被释放了，实际合同结清超过90天没有新的生效立项才会释放
//                // 生效的立项需要剔除对应合同是结清的再判断一次
//                filterList.removeIf(e -> {
//                    List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByProjEstablishIds(Collections.singletonList(e.getId()));
//                    if (CollectionUtil.isEmpty(projReviewBaseInfos)) {
//                        return false;
//                    }
//                    List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
//                    if (CollectionUtil.isEmpty(contractBaseInfos)) {
//                        return false;
//                    }
//                    for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
//                        if (!Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) && !Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())) {
//                            return false;
//                        }
//                    }
//                    return true;
//                });
                if (CollectionUtil.isNotEmpty(filterList)) {
                    log.info("projEstablishStage-不释放客户-获取管护权{}天存在生效的立项", limitDays);
                    return false;
                }
            }
        }
        log.info("projEstablishStage-释放客户-获取管护权{}天没有找到任何生效的立项", limitDays);
        return true;
    }

    private boolean projReviewStage1(ClientAsLesseeInfoDTO clientAsLesseeInfoDTO) {
        int limitDays = 90;
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList();
        if (CollectionUtil.isEmpty(projEstablishBaseInfoList)) {
            log.info("projReviewStage1-不释放客户-不存在任何立项");
            return false;
        }
        // 过滤出生效的立项
        List<ProjEstablishBaseInfo> filterList = projEstablishBaseInfoList.stream().filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("projReviewStage1-不释放客户-不存在任何生效的立项");
            return false;
        }
        // 生效的立项需要剔除对应合同是结清的再判断一次
        filterList.removeIf(e -> {
            List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByProjEstablishIds(Collections.singletonList(e.getId()));
            if (CollectionUtil.isEmpty(projReviewBaseInfos)) {
                return false;
            }
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            if (CollectionUtil.isEmpty(contractBaseInfos)) {
                return false;
            }
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                if (!Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) && !Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())) {
                    return false;
                }
            }
            return true;
        });
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("projReviewStage1-不释放客户-不存在无结清合同的生效的立项");
            return false;
        }
        // 遍历判断
        for (ProjEstablishBaseInfo projEstablishBaseInfo : filterList) {
            // 生效立项找流程通过时间
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(projEstablishBaseInfo.getId().toString());
            processPageReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name()));
            processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
            if (Objects.isNull(processResult) || CollectionUtil.isEmpty(processResult.getContents())) {
                // 数据异常？生效了怎么会没有流程
                log.info("projReviewStage1-不释放客户-生效立项没有找到对应流程[projEstablishId:{}]", projEstablishBaseInfo.getId());
                return false;
            }
            // 结束时间倒排
            processResult.getContents().sort(Comparator.comparing(ProcessResp::getEndTime).reversed());
            ProcessResp processResp = processResult.getContents().get(0);
            if (Objects.isNull(processResp.getEndTime())) {
                // 数据异常？生效了怎么会没有流程通过时间
                log.info("projReviewStage1-不释放客户-生效立项无法确定流程通过时间[projEstablishId:{}]", projEstablishBaseInfo.getId());
                return false;
            }
            LocalDate establishPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
            // 找评审
            List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.listByProjEstablishIds(Collections.singletonList(projEstablishBaseInfo.getId()));
            if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
                // 判断立项生效是否已经超过limitDays天
                long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                if (days <= limitDays) {
                    log.info("projReviewStage1-不释放客户-存在生效未超过{}天的立项[projEstablishId:{}]", limitDays, projEstablishBaseInfo.getId());
                    return false;
                }
                continue;
            }
            ProjReviewBaseInfo review = projReviewBaseInfoList.get(0);
            // 找评审创建流程
            ProcessPageReq reviewProcessReq = new ProcessPageReq();
            reviewProcessReq.setBusinessKey(review.getId().toString());
            reviewProcessReq.setModelKey(ProcessModelTypeEnum.ProjReviewCreateFlow.name());
            reviewProcessReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.RUNNING.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> reviewResult = flowTaskApiService.queryProcess(reviewProcessReq);
            if (Objects.nonNull(reviewResult) && CollectionUtil.isNotEmpty(reviewResult.getContents())) {
                ProcessResp pr = reviewResult.getContents().get(0);
                // 找流程实例中部门分管领导最早一次通过时间
                TaskSystemPageReq taskSystemPageReq = new TaskSystemPageReq();
                taskSystemPageReq.setProcessInstanceId(pr.getProcessInstanceId());
                taskSystemPageReq.setActivityId("userTask_bizDivisionLeader");
                cn.zswltech.flow.core.util.Page<TaskResp> taskRespPage = flowTaskApiService.querySystemTask(taskSystemPageReq);
                if (Objects.nonNull(taskRespPage) && CollectionUtil.isNotEmpty(taskRespPage.getContents())) {
                    List<TaskResp> taskRespList = taskRespPage.getContents();
                    // 只取审批通过的任务
                    taskRespList.removeIf(e -> !Objects.equals(e.getTaskStatus(), TaskBusinessStatusEnum.PASS.getStatus()));
                    // 排序
                    taskRespList.sort(Comparator.comparing(TaskResp::getTaskEndTime));
                    if (CollectionUtil.isNotEmpty(taskRespList)) {
//                        LocalDate bizLeaderPassDate = LocalDateTimeUtil.of(taskRespList.get(0).getTaskEndTime()).toLocalDate();
//                        long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), bizLeaderPassDate.atStartOfDay(), ChronoUnit.DAYS);
//                        if (days <= limitDays) {
//                            log.info("projReviewStage1-不释放客户-存在生效的立项在{}天内通过分管领导节点", limitDays);
//                            return false;
//                        }
                        // 说明是存量业务，已经通过的就不再判断时间
                        log.info("projReviewStage1-不释放客户-分管领导已经审批通过");
                        return false;
                    }
                } else {
                    if (Objects.equals(review.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name())) {
                        log.info("projReviewStage1-不释放客户-虽然没找到{}天内通过分管领导节点的数据，但是评审已经生效，考虑历史数据不进行释放", limitDays);
                        return false;
                    }
                    // 判断立项生效是否已经超过limitDays天
                    long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                    if (days <= limitDays) {
                        log.info("projReviewStage1-不释放客户-存在生效未超过{}天的立项[projEstablishId:{}]", limitDays, projEstablishBaseInfo.getId());
                        return false;
                    }
                }
            } else {
                if (Objects.equals(review.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name())) {
                    log.info("projReviewStage1-不释放客户-虽然没找到评审对应的在途流程，但是评审已经生效，考虑历史数据不进行释放");
                    return false;
                }
            }
            // 判断立项生效是否已经超过limitDays天
            long days = LocalDateTimeUtil.between(establishPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
            if (days <= limitDays) {
                log.info("projReviewStage1-不释放客户-存在生效未超过{}天的立项[projEstablishId:{}]", limitDays, projEstablishBaseInfo.getId());
                return false;
            }
        }
        log.info("projReviewStage1-释放客户-所有生效的立项在{}天内都没有通过分管领导节点", limitDays);
        return true;
    }

    private boolean projReviewStage2(ClientAsLesseeInfoDTO clientAsLesseeInfoDTO) {
        int limitDays = 365;
        List<ProjReviewBaseInfo> projReviewBaseInfoList = clientAsLesseeInfoDTO.getProjReviewBaseInfoList();
        if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
            log.info("projReviewStage2-不释放客户-不存在评审");
            return false;
        }
        // 过滤出生效评审
        List<ProjReviewBaseInfo> filterList = projReviewBaseInfoList.stream().filter(e -> Objects.equals(e.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name())).collect(Collectors.toList());
        if (filterList.size() != projReviewBaseInfoList.size()) {
            // 说明有新建的评审，不符合释放客户的逻辑判断，释放客户逻辑判断需要都是生效评审
            log.info("projReviewStage2-不释放客户-存在非生效的评审");
            return false;
        }
        // 剔除对应合同结清的评审
        filterList.removeIf(e -> {
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(e.getId()));
            if (CollectionUtil.isEmpty(contractBaseInfos)) {
                return false;
            }
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                if (!Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) && !Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())) {
                    return false;
                }
            }
            return true;
        });
        if (CollectionUtil.isEmpty(filterList)) {
            log.info("projReviewStage2-不释放客户-不存在非结清合同的生效评审");
            return false;
        }
        // 遍历判断生效评审
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            // 生效评审找流程通过时间
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(projReviewBaseInfo.getId().toString());
            processPageReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()));
            processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
            if (Objects.nonNull(processResult) && CollectionUtil.isNotEmpty(processResult.getContents())) {
                // 按照结束时间倒排
                processResult.getContents().sort(Comparator.comparing(ProcessResp::getEndTime).reversed());
                ProcessResp processResp = processResult.getContents().get(0);
                if (Objects.nonNull(processResp.getEndTime())) {
                    LocalDate reviewPassDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
                    // 找评审对应的合同
                    List<ContractBaseInfo> cbiList = contractBaseInfoService.listByProjReviewIds(Collections.singletonList(projReviewBaseInfo.getId()));
                    if (CollectionUtil.isNotEmpty(cbiList)) {
                        // 找实际投放
                        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByContractIds(cbiList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));
                        if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
                            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
                            PaymentActualDetail paymentActualDetail = paymentActualDetailList.get(0);
                            LocalDate earliestPaidDate = paymentActualDetail.getPaidInDate();
                            long days = LocalDateTimeUtil.between(reviewPassDate.atStartOfDay(), earliestPaidDate.atStartOfDay(), ChronoUnit.DAYS);
                            if (days <= limitDays) {
                                log.info("projReviewStage2-不释放客户-存在生效的评审在{}天内发生了实际投放[projReviewId:{}, paymentActualDetailId:{}]", limitDays, projReviewBaseInfo.getId(), paymentActualDetail.getId());
                                return false;
                            }
                        }
                    }
                    // 判断评审生效是否已经超过limitDays天
                    long days = LocalDateTimeUtil.between(reviewPassDate.atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
                    if (days <= limitDays) {
                        log.info("projReviewStage2-不释放客户-存在生效未超过{}天的评审[projReviewId:{}]", limitDays, projReviewBaseInfo.getId());
                        return false;
                    }
                }
            } else {
                log.info("不释放客户-没有找到评审对应的流程[projReviewId:{}]", projReviewBaseInfo.getId());
                return false;
            }
        }
        log.info("projReviewStage2-释放客户-所有生效评审超过{}天都没有发生实际投放", limitDays);
        return true;
    }

    private boolean contractStage(ClientAsLesseeInfoDTO clientAsLesseeInfoDTO, Set<Long> canViewUserIds) {
        int limitDays = 90;
        List<ContractBaseInfo> contractBaseInfoList = clientAsLesseeInfoDTO.getContractBaseInfoList();
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            log.info("contractStage-不释放客户-不存在合同");
            return false;
        }
        List<ContractBaseInfo> filterList = contractBaseInfoList.stream()
                .filter(e -> (Objects.equals(e.getContractStatus(), ContractStatus.SETTLE.name()) || Objects.equals(e.getContractProcessStatus(), ContractProcessStatusEnum.SETTLE_PASS.name())))
                .collect(Collectors.toList());
        if (contractBaseInfoList.size() != filterList.size()) {
            // 说明存在新建、生效或者起租状态的合同，不符合释放客户的判断逻辑，需要所有合同都是结清状态
            log.info("contractStage-不释放客户-存在非结清的合同");
            return false;
        }
        long lastSettleTimestamp = 0L;
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            canViewUserIds.add(contractBaseInfo.getProjSponsorUserId());
            // 找结清流程
            ProcessPageReq processPageReq = new ProcessPageReq();
            processPageReq.setBusinessKey(contractBaseInfo.getId().toString());
            processPageReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.ContractEarlySettleFlow.name(), ProcessModelTypeEnum.ContractNormalSettleFlow.name()));
            processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
            if (Objects.nonNull(processResult) && CollectionUtil.isNotEmpty(processResult.getContents())) {
                List<ProcessResp> list = processResult.getContents();
                list.sort(Comparator.comparing(ProcessResp::getEndTime).reversed());
                lastSettleTimestamp = Math.max(lastSettleTimestamp, list.get(0).getEndTime().getTime());
            }
        }
//        // 说明所有合同都已经走过了结清审批，取最晚的一个结清时间看90天内是否有立项通过的项目
//        LocalDate startDate = LocalDateTimeUtil.of(lastSettleTimestamp).toLocalDate();
//        LocalDate endDate = startDate.plusDays(limitDays);
//        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList();
//        if (CollectionUtil.isNotEmpty(projEstablishBaseInfoList)) {
//            for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
//                // 找立项创建流程
//                ProcessPageReq processPageReq = new ProcessPageReq();
//                processPageReq.setBusinessKey(projEstablishBaseInfo.getId().toString());
//                processPageReq.setModelKey(ProcessModelTypeEnum.ProjEstablishCreateFlow.name());
//                processPageReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
//                cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq);
//                if (CollectionUtil.isNotEmpty(processResult.getContents())) {
//                    ProcessResp processResp = processResult.getContents().get(0);
//                    if (Objects.nonNull(processResp.getEndTime())) {
//                        LocalDate processEndDate = LocalDateTimeUtil.of(processResp.getEndTime()).toLocalDate();
//                        if (startDate.isBefore(processEndDate) && endDate.isAfter(processEndDate)) {
//                            log.info("contractStage-不释放客户-合同结清审批通过后{}天内存在审批通过的立项[processInstanceId:{}]", limitDays, processResp.getProcessInstanceId());
//                            return false;
//                        }
//                    }
//                }
//            }
//        }
        // 判断一下最晚的结清时间到现在是否已经超过limitDays天
        long days = LocalDateTimeUtil.between(LocalDateTimeUtil.of(lastSettleTimestamp).toLocalDate().atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
        if (days <= limitDays) {
            log.info("contractStage-不释放客户-合同结清审批通过还未超过{}天", limitDays);
            return false;
        }
        // 判断一下除结清合同对应的项目之外是否有生效的立项（一般翻单会在结清当前合同之前做）
        List<Long> projReviewIds = contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList());
        List<ProjReviewBaseInfo> prbiList = projReviewBaseInfoService.listByIds(projReviewIds);
        if (CollectionUtil.isNotEmpty(prbiList)) {
            Set<Long> candidateIdList = prbiList.stream().filter(e -> Objects.nonNull(e.getProjEstablishId())).map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(clientAsLesseeInfoDTO.getProjEstablishBaseInfoList())) {
                Set<Long> ids = clientAsLesseeInfoDTO.getProjEstablishBaseInfoList().stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toSet());
                ids.removeIf(candidateIdList::contains);
                if (CollectionUtil.isNotEmpty(ids)) {
                    log.info("contractStage-不释放客户-存在结清合同对应项目之外其他生效的立项");
                    return false;
                }
            }
        }
        log.info("contractStage-释放客户-合同结清审批通过后{}天不存在审批通过的立项", limitDays);
        return true;
    }

    public ClientAsLesseeInfoDTO collectInfo(Long clientId, ClientAuthority managerAuth) {
        ClientAsLesseeInfoDTO clientAsLesseeInfoDTO = new ClientAsLesseeInfoDTO();
        // 立项
        LambdaQueryWrapper<ProjEstablishBaseInfo> projEstablishQuery = Wrappers.lambdaQuery();
        projEstablishQuery.eq(ProjEstablishBaseInfo::getProjSponsorUserId, managerAuth.getUserId());
        projEstablishQuery.notIn(ProjEstablishBaseInfo::getProjEstablishStatus, ListUtil.toList(RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()));
        clientAsLesseeInfoDTO.setProjEstablishBaseInfoList(projEstablishBaseInfoService.list(projEstablishQuery));
        projEstablishQuery.ne(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.CLOSED.name());
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoService.list(projEstablishQuery);
        // 剔除交易结构中没有当前客户的立项
        projEstablishBaseInfoList.removeIf(e -> {
            boolean isLessee = this.existSpecificClient(clientId, e.getLesseeInfo());
            boolean isCreditor = this.existSpecificClient(clientId, e.getCreditorInfo());
            boolean isDebtor = this.existSpecificClient(clientId, e.getDebtorInfo());
            boolean isGuarantee = this.existSpecificClient(clientId, e.getGuaranteeInfo());
            boolean isMortgage = this.existSpecificClient(clientId, e.getMortgagorInfo());
            boolean isPledge = this.existSpecificClient(clientId, e.getPledgorInfo());
            return !isLessee && !isCreditor && !isDebtor && !isGuarantee && !isMortgage && !isPledge;
        });
        clientAsLesseeInfoDTO.setProjEstablishBaseInfoList(projEstablishBaseInfoList);
        // 评审
        LambdaQueryWrapper<ProjReviewBaseInfo> projReviewQuery = Wrappers.lambdaQuery();
        projReviewQuery.eq(ProjReviewBaseInfo::getProjSponsorUserId, managerAuth.getUserId());
        projReviewQuery.notIn(ProjReviewBaseInfo::getProjReviewStatus, ListUtil.toList(RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()));
        clientAsLesseeInfoDTO.setProjReviewBaseInfoList(projReviewBaseInfoService.list(projReviewQuery));
        projReviewQuery.ne(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name());
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.list(projReviewQuery);
        // 剔除交易结构中没有当前客户的评审
        projReviewBaseInfoList.removeIf(e -> {
            boolean isLessee = this.existSpecificClient(clientId, e.getLesseeInfo());
            boolean isCreditor = this.existSpecificClient(clientId, e.getCreditorInfo());
            boolean isDebtor = this.existSpecificClient(clientId, e.getDebtorInfo());
            boolean isGuarantee = this.existSpecificClient(clientId, e.getGuaranteeInfo());
            boolean isMortgage = this.existSpecificClient(clientId, e.getMortgagorInfo());
            boolean isPledge = this.existSpecificClient(clientId, e.getPledgorInfo());
            return !isLessee && !isCreditor && !isDebtor && !isGuarantee && !isMortgage && !isPledge;
        });
        clientAsLesseeInfoDTO.setProjReviewBaseInfoList(projReviewBaseInfoList);
        // 合同
        LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
        contractQuery.eq(ContractBaseInfo::getProjSponsorUserId, managerAuth.getUserId());
        contractQuery.notIn(ContractBaseInfo::getContractStatus, ListUtil.of(ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractQuery);
        // 剔除交易结构中没有当前客户的合同
        contractBaseInfoList.removeIf(e -> {
            boolean isTenantry = SpringUtil.getBean(ContractTenantryService.class).existSpecificClient(clientId, e.getId());
            boolean isGuarantee = SpringUtil.getBean(ContractGuarantorService.class).existSpecificClient(clientId, e.getId());
            boolean isMortgage = SpringUtil.getBean(ContractMortgageService.class).existSpecificClient(clientId, e.getId());
            boolean isPledge = SpringUtil.getBean(ContractPledgeService.class).existSpecificClient(clientId, e.getId());
            return !isTenantry && !isGuarantee && !isMortgage && !isPledge;
        });
        clientAsLesseeInfoDTO.setContractBaseInfoList(contractBaseInfoList);
        return clientAsLesseeInfoDTO;
    }

    private boolean existSpecificClient(Long clientId, String json) {
        if (StrUtil.isBlank(json)) {
            return false;
        }
        List<ClientInfo> clientInfoList = JSONUtil.toList(json, ClientInfo.class);
        if (CollectionUtil.isEmpty(clientInfoList)) {
            return false;
        }
        for (ClientInfo clientInfo : clientInfoList) {
            if (Objects.equals(clientInfo.getClientId(), clientId)) {
                return true;
            }
        }
        return false;
    }

    private ClientAsMessageInfoDTO collectMessageInfo(Long clientId) {
        ClientAsMessageInfoDTO clientAsMessageInfoDTO = new ClientAsMessageInfoDTO();
        // 立项
        LambdaQueryWrapper<ProjEstablishBaseInfo> projEstablishQuery = Wrappers.lambdaQuery();
        projEstablishQuery.eq(ProjEstablishBaseInfo::getClientId, clientId);
        projEstablishQuery.notIn(ProjEstablishBaseInfo::getProjEstablishStatus, ListUtil.toList(RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()));
        clientAsMessageInfoDTO.setProjEstablishBaseInfoList(projEstablishBaseInfoService.list(projEstablishQuery));
        // 评审
        LambdaQueryWrapper<ProjReviewBaseInfo> projReviewQuery = Wrappers.lambdaQuery();
        projReviewQuery.eq(ProjReviewBaseInfo::getClientId, clientId);
        projReviewQuery.ne(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name());
        clientAsMessageInfoDTO.setProjReviewBaseInfoList(projReviewBaseInfoService.list(projReviewQuery));
        // 合同
        LambdaQueryWrapper<ContractBaseInfo> contractQuery = Wrappers.lambdaQuery();
        contractQuery.eq(ContractBaseInfo::getClientId, clientId);
        contractQuery.notIn(ContractBaseInfo::getContractStatus, ListUtil.of(ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()));
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(contractQuery);
        clientAsMessageInfoDTO.setContractBaseInfoList(contractBaseInfoList);
        //付款
        List<Long> contractBaseInfoIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfoList = new ArrayList<>();
        if (!contractBaseInfoIds.isEmpty()) {
            LambdaQueryWrapper<PaymentBaseInfo> paymentQuery = Wrappers.lambdaQuery();
            paymentQuery.in(PaymentBaseInfo::getContractId, contractBaseInfoIds);
            paymentQuery.notIn(PaymentBaseInfo::getPaymentStatus, ListUtil.of(PaymentStatusEnum.CLOSED.name()));
            paymentBaseInfoList = paymentBaseInfoService.list(paymentQuery);
            clientAsMessageInfoDTO.setPaymentBaseInfoList(paymentBaseInfoList);
        }
        //核销
        List<PaymentActualDetail> paymentActualDetailList = new ArrayList<>();
        if (!paymentBaseInfoList.isEmpty()) {
            LambdaQueryWrapper<PaymentActualDetail> paymentActualQuery = Wrappers.lambdaQuery();
            paymentActualQuery.in(PaymentActualDetail::getPaymentId, paymentBaseInfoList);
            paymentActualDetailList = paymentActualDetailService.list(paymentActualQuery);
            clientAsMessageInfoDTO.setPaymentActualDetailList(paymentActualDetailList);
        }
        return clientAsMessageInfoDTO;
    }

    private void releaseClient(String batchNo, Long clientId, Collection<Long> canViewUserIds) {
        // 查询是否有审批中的客户变更流程
        ProcessPageReq processPageReq = new ProcessPageReq();
        // 理论上只会存在一个
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setModelKey(ProcessModelTypeEnum.ClientModifyFlow.name());
        processPageReq.setBusinessKey(clientId.toString());
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> result = taskApiService.queryProcess(processPageReq);
        ProcessResp targetProcess = CollectionUtil.isEmpty(result.getContents()) ? null : result.getContents().get(0);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                // 客户状态变更
                Client client = clientService.getById(clientId);
                if (Objects.isNull(client)) {
                    throw new MithrasException("客户信息不存在");
                }
                // 公海客户不处理（理论上公海没有管护权，不会进到这里，为了防止数据异常，这里判断一下）
                if (clientAuthorityUtil.isIntraGroupCollaboration(clientId)) {
                    return;
                }
                // 关闭在途的客户变更流程
                if (Objects.nonNull(targetProcess)) {
                    ExecutionProcessBaseREQ r = new ExecutionProcessBaseREQ();
                    r.setProcessInstanceId(targetProcess.getProcessInstanceId());
                    r.setMessage("客户释放，流程自动关闭");
                    SpringUtil.getBean(ExecutionService.class).rejectAll(r);
                }
                Client updateClient = new Client();
                updateClient.setId(client.getId());
                updateClient.setBelongSponsorId(null);
                updateClient.setBelongDeptId(null);
                updateClient.setClientStatus(ClientStatus.NEW.name());
                updateClient.setIsReleased(YesOrNoNumberEnum.YES.getCode());
                this.getBaseMapper().updateAnnotationIncludeNullById(updateClient);
                // 移除该客户的管护权和查看权
                LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
                query.eq(ClientAuthority::getClientId, clientId);
                query.in(ClientAuthority::getLevel, ListUtil.of(ClientLevelEnum.MANAGE.getLevel(), ClientLevelEnum.VIEW.getLevel()));
                clientAuthorityService.remove(query);
                // 保留查看权的用户新增查看权
                if (CollectionUtil.isNotEmpty(canViewUserIds)) {
                    List<ClientAuthority> clientAuthorityList = canViewUserIds.stream().map(e -> {
                        ClientAuthority clientAuthority = new ClientAuthority();
                        clientAuthority.setClientId(clientId);
                        clientAuthority.setUserId(e);
                        clientAuthority.setDeptId(Optional.ofNullable(sysUserService.getBizDeptByUserId(e)).map(OrgDO::getId).orElse(null));
                        clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
                        clientAuthority.setSourceId(batchNo);
                        clientAuthority.setSourceBusinessType("CLIENT_RELEASE");
                        return clientAuthority;
                    }).collect(Collectors.toList());
                    clientAuthorityService.saveBatch(clientAuthorityList);
                }
                // 移除符合条件的客户副本数据
                LambdaQueryWrapper<ClientUserRef> clientUserRefQuery = Wrappers.lambdaQuery();
                clientUserRefQuery.eq(ClientUserRef::getClientId, clientId);
                clientUserRefQuery.in(ClientUserRef::getLastOperateType, ListUtil.of(ClientUserRef.OperateTypeEnum.INIT.name(), ClientUserRef.OperateTypeEnum.READ.name()));
                List<ClientUserRef> clientUserRefList = clientUserRefService.list(clientUserRefQuery);
                if (CollectionUtil.isNotEmpty(clientUserRefList)) {
                    for (ClientUserRef clientUserRef : clientUserRefList) {
                        clientAuthorityUtil.clearNewData(clientId, clientUserRef.getUserId());
                    }
                }
                // 自动关闭在途评级流程
                // 流程id
                List<String> clientProcessIdList = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                                .eq(BizProcessData::getClientId, clientId))
                        .stream().map(BizProcessData::getProcessInstanceId).distinct()
                        .collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(clientProcessIdList)){
                    ProcessPageReq processPageReq2 = new ProcessPageReq();
                    processPageReq2.setProcessInstanceIdList(clientProcessIdList);
                    processPageReq2.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.RatingClientCreateFlow.name(), ProcessModelTypeEnum.RatingClientUpdateFlow.name()));
                    processPageReq2.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.RUNNING.getType()));
                    cn.zswltech.flow.core.util.Page<ProcessResp> processResult = flowTaskApiService.queryProcess(processPageReq2);
                    if (Objects.nonNull(processResult) && CollectionUtil.isNotEmpty(processResult.getContents())) {
                        List<ProcessResp> list = processResult.getContents();
                        list.sort(Comparator.comparing(ProcessResp::getEndTime).reversed());
                        for(ProcessResp processResp : list){
                            ExecutionProcessBaseReq req = new ExecutionProcessBaseReq();
                            req.setProcessInstanceId(processResp.getProcessInstanceId());
                            req.setHandlerId(processResp.getStartUserId());
                            req.setMessage("客户已释放，系统自动关闭评级流程。");
                            executionApiService.cancel(req);
                        }
                    }
                }

                //待提交评级数据改为已关闭状态
                LambdaQueryWrapper<RatingClient> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(RatingClient::getClientId, clientId);
                List<RatingClient> ratingClients = ratingClientService.list(lambdaQueryWrapper);
                Set<Long> ratingClientIds = new HashSet<>();
                if(CollectionUtil.isNotEmpty(ratingClients)){
                    ratingClients.forEach(e -> {
                        if(StringUtils.isEmpty(e.getProcessStatus())||e.getProcessStatus().equals(ProcessStatus.UN_SUBMIT.name())){
                            e.setProcessStatus(ProcessStatus.CANCEL.name());
                        }
                        ratingClientIds.add(e.getId());
                    });
                    log.info("释放客户权限评级关闭[ratingClientIds:{}]", ratingClientIds);
                    ratingClientService.updateBatchById(ratingClients);
                }
                if(CollectionUtil.isNotEmpty(ratingClientIds)){
                    //待提交的流程表设为关闭
                    LambdaQueryWrapper<CommonProcessPrepare> wrapper = Wrappers.<CommonProcessPrepare>lambdaQuery()
                            .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name())
                            .in(CommonProcessPrepare::getBusinessId, ratingClientIds)
                            .in(CommonProcessPrepare::getProcessType, ListUtil.of(ProcessModelTypeEnum.RatingClientCreateFlow.name(), ProcessModelTypeEnum.RatingClientUpdateFlow.name()))
                            .orderByDesc(CommonProcessPrepare::getId);
                    List<CommonProcessPrepare> commonProcessPrepares = prepareService.list(wrapper);
                    if (CollectionUtil.isNotEmpty(commonProcessPrepares)) {
                        commonProcessPrepares.forEach(e -> {
                            log.info("释放客户权限评级关闭commonProcessPrepares[Id:{}]", e.getId());
                            e.setStatus(CommonProcessPrepareStatus.CLOSED.name());
                        });
                        prepareService.updateBatchById(commonProcessPrepares);
                    }
                }
            } catch (Exception e) {
                log.error("释放客户权限发生异常[batchNo:{}, clientId:{}, canViewUserIds:{}]", batchNo, clientId, JSONUtil.toJsonStr(canViewUserIds), e);
                transactionStatus.setRollbackOnly();
            }
        });

    }

    public PageR<ClientListRSP> listNoAuthority(ClientListREQ req) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        Page<Client> dbPage = clientMapper.listNoAuthority(new Page<>(req.getPage(), req.getPageSize()), req.getClientName(), currentUserId);
        if (CollectionUtil.isEmpty(dbPage.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<ClientListRSP> list = BeanUtil.copyToList(dbPage.getRecords(), ClientListRSP.class);
        return PageR.of(list, dbPage.getTotal(), req.getPage(), req.getPageSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean tryInitNewClientInfoOrIgnore(Long clientId, Long userId, List<InfoModule> moduleList) {
        // 加锁
        String lockKey = CacheEnum.CLIENT_USER_REF_CREATE_LOCK.buildKey(BusinessModuleEnum.CLIENT.name(), clientId);
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 10000L);
        if (!getLockFlag) {
            throw new MithrasException("系统繁忙，请稍后重试");
        }
        try {
            int count = clientUserRefService.countByClientUser(clientId, userId);
            if (count > 0) {
                return false;
            }
            clientUserRefService.create(clientId, userId);
            LambdaQueryWrapper<ClientUserRef> query = Wrappers.lambdaQuery();
            query.eq(ClientUserRef::getClientId, clientId);
            query.eq(ClientUserRef::getLastOperateType, ClientUserRef.OperateTypeEnum.WRITE.name());
            query.orderByDesc(ClientUserRef::getLastOperateTime);
            query.last(StringUtil.mysqlLimitOne());
            ClientUserRef clientUserRef = clientUserRefService.getOne(query);
            if (Objects.nonNull(clientUserRef)) {
                ClientCopyInfoBO clientCopyInfoBO = ClientCopyInfoBO.builder()
                        .clientId(clientId)
                        .dbUserId(clientUserRef.getUserId())
                        .currentUserId(userId)
                        .moduleList(moduleList)
                        .build();
                clientAuthorityUtil.copyFromNewToNew(clientCopyInfoBO);
            }
        } catch (Exception e) {
            log.error("初始化客户副本数据发生异常[clientId:{}, userId:{}]", clientId, userId, e);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return true;
    }

    public List<Client> listByClientIds(Collection<Long> clientIds) {
        return clientMapper.selectBatchIds(clientIds);
    }

    public Page<Client> list(ClientListREQ req) {
        LocalDateTime from = isNull(req.getCreateDateFrom()) ? null : req.getCreateDateFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getCreateDateTo()) ? null : req.getCreateDateTo().plusDays(1).atStartOfDay();

        LocalDateTime updateFrom = isNull(req.getUpdateDateFrom()) ? null : req.getUpdateDateFrom().atStartOfDay();
        LocalDateTime updateTo = isNull(req.getUpdateDateTo()) ? null : req.getUpdateDateTo().plusDays(1).atStartOfDay();
        ClientListParam listParam = new ClientListParam();
        // 如果当前用户是项目经理且不是部门负责人
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name()) && !sysUserService.currentUserIsSpecificJob(businesshead.name())) {
            listParam.setProjManagerId(Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null));
            if (Objects.equals("query", req.getScene())) {
                listParam.setLevels(ListUtil.of(ClientLevelEnum.VIEW.getLevel(), ClientLevelEnum.APPLY.getLevel(), ClientLevelEnum.MANAGE.getLevel()));
            } else if (Objects.equals("other", req.getScene())) {
                listParam.setLevels(ListUtil.of(ClientLevelEnum.APPLY.getLevel(), ClientLevelEnum.MANAGE.getLevel()));
            } else {
                listParam.setLevels(ListUtil.of(ClientLevelEnum.MANAGE.getLevel()));
            }
        }
        if (StringUtils.isNotBlank(req.getClientName())) {
            listParam.setClientCode(req.getClientCode())
                    .setClientName(req.getClientName())
                    .setClientType(req.getClientType())
                    .setIndustryType(req.getIndustryType())
                    .setCreateFrom(from)
                    .setCreateTo(to)
                    .setUpdateFrom(updateFrom)
                    .setUpdateTo(updateTo)
                    .setClientStatus(req.getClientStatus())
                    .setProcessStatus(req.getProcessStatus())
                    .setIsGroup(req.getIsGroup())
                    .setEffected(req.getEffected());
            return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
        } else {
            listParam.setClientCode(req.getClientCode())
                    .setClientName(req.getClientName())
                    .setClientType(req.getClientType())
                    .setIndustryType(req.getIndustryType())
                    .setCreateFrom(from)
                    .setCreateTo(to)
                    .setUpdateFrom(updateFrom)
                    .setUpdateTo(updateTo)
                    .setClientStatus(req.getClientStatus())
                    .setProcessStatus(req.getProcessStatus())
                    .setCreateById(req.getCreateBy())
                    .setCreateByDeptId(req.getCreateByDeptId())
                    .setIsGroup(req.getIsGroup())
                    .setEffected(req.getEffected())
                    .setBelongDeptId(req.getBelongDeptId())
                    .setBelongSponsorId(req.getBelongSponsorId());
        }
        // 如果当前用户是业务负责人 且 不是分管领导
        if (sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name()) && !sysUserService.currentUserIsSpecificJob(JobEnum.leaderincharge.name())) {
            List<Long> deptIds = sysUserService.canViewDeptIds();
            if (!isEmpty(deptIds)) {
                listParam.setBelongDeptId(deptIds.get(0));
            }
        }
        // 如果当前用户是非业务部门人员
        return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
    }

    public Page<Client> newPageList(ClientListREQ req) {
        LocalDateTime from = isNull(req.getCreateDateFrom()) ? null : req.getCreateDateFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getCreateDateTo()) ? null : req.getCreateDateTo().plusDays(1).atStartOfDay();

        LocalDateTime updateFrom = isNull(req.getUpdateDateFrom()) ? null : req.getUpdateDateFrom().atStartOfDay();
        LocalDateTime updateTo = isNull(req.getUpdateDateTo()) ? null : req.getUpdateDateTo().plusDays(1).atStartOfDay();
        ClientListParam listParam = null;
        if (StringUtils.isNotBlank(req.getClientName())) {
            listParam = new ClientListParam()
                    .setClientCode(req.getClientCode())
                    .setClientName(req.getClientName())
                    .setClientType(req.getClientType())
                    .setIndustryType(req.getIndustryType())
                    .setCreateFrom(from)
                    .setCreateTo(to)
                    .setUpdateFrom(updateFrom)
                    .setUpdateTo(updateTo)
                    .setClientStatus(Objects.equals(req.getClientStatus(), ClientStatus.RELEASE.name()) ? null : req.getClientStatus())
                    .setProcessStatus(req.getProcessStatus())
                    .setReleased(Objects.equals(req.getClientStatus(), ClientStatus.RELEASE.name()))
                    .setEffected(req.getEffected());
            // 输入查询条件查询列表的时候增加特殊处理逻辑
            List<Long> targetDeptIds = sysUserService.canViewDeptIds();
            if (Objects.nonNull(targetDeptIds)) {
                if (CollectionUtil.isEmpty(targetDeptIds)) {
                    // 进一步判断是否是项目经理
                    if (sysUserService.currentUserIsSpecificJob(projmanager.name())) {
                        listParam.setQueryConditionUserId(AccountUtil.getLoginInfo().getId());
                    } else {
                        // 说明没有可看部门的数据，直接填充一个不可能的值
                        listParam.setQueryConditionDeptIds(Collections.singletonList(-10086L));
                    }
                } else {
                    listParam.setQueryConditionDeptIds(targetDeptIds);
                }
            }
            return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
        } else {
            listParam = new ClientListParam()
                    .setClientCode(req.getClientCode())
                    .setClientName(req.getClientName())
                    .setClientType(req.getClientType())
                    .setIndustryType(req.getIndustryType())
                    .setCreateFrom(from)
                    .setCreateTo(to)
                    .setUpdateFrom(updateFrom)
                    .setUpdateTo(updateTo)
                    .setClientStatus(Objects.equals(req.getClientStatus(), ClientStatus.RELEASE.name()) ? null : req.getClientStatus())
                    .setProcessStatus(req.getProcessStatus())
                    .setCreateById(req.getCreateBy())
                    .setCreateByDeptId(req.getCreateByDeptId())
                    .setReleased(Objects.equals(req.getClientStatus(), ClientStatus.RELEASE.name()))
                    .setEffected(req.getEffected())
                    .setBelongDeptId(req.getBelongDeptId())
                    .setBelongSponsorId(req.getBelongSponsorId());
        }
        // 需要按照不同登陆角色处理
        Set<Long> targetClientIds = new HashSet<>();
        // 默认先填一个不可能的值，如果后续逻辑没有往里填充说明没有可看的客户
        targetClientIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        // 自然人看自己创建的
        List<Client> normalList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getClientType, ClientType.NORMAL.name()).eq(BaseModel::getCreateBy, currentUserId));
        if (CollectionUtil.isNotEmpty(normalList)) {
            targetClientIds.addAll(normalList.stream().map(Client::getId).collect(Collectors.toSet()));
        }
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new Page<>(req.getPage(), req.getPageSize());
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
        }
        // 项目经理
        if (CollectionUtil.isNotEmpty(projmanagerList)) {
            Set<Long> projmanagerTargetClientIds = this.findTargetClientIdsByUserId(currentUserId);
            if (CollectionUtil.isNotEmpty(projmanagerTargetClientIds)) {
                targetClientIds.addAll(projmanagerTargetClientIds);
            }
        }
        // 业务负责人
        if (CollectionUtil.isNotEmpty(businessheadList)) {
            List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> businessheadTargetClientIds = this.findTargetClientIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                targetClientIds.addAll(businessheadTargetClientIds);
            }
        }
        // 分管领导
        if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
            List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
            Set<Long> leaderinchargeTargetClientIds = this.findTargetClientIdsByDeptIds(deptIds);
            if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                targetClientIds.addAll(leaderinchargeTargetClientIds);
            }
        }
        // 填充条件
        listParam.setTargetClientIds(targetClientIds);
        return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
    }

    public Set<Long> findTargetClientIdsByDeptIds(List<Long> deptIds) {
        // 部门所有新建（同个客户多版本则看最新的）+部门所有管护权+部门释放未被别人占用的+部门所有申办权的
        Set<Long> targetClientIds = new HashSet<>();
        // 有权限的客户信息
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(
                Wrappers.<ClientAuthority>lambdaQuery()
                        .in(ClientAuthority::getDeptId, deptIds)
        );
        // 管护权和申办权一类，查看权一类
        Set<Long> twoOrThreeClientIds = new HashSet<>();
        Set<Long> oneClientIds = new HashSet<>();
        clientAuthorityList.forEach(e -> {
            if (Objects.equals(e.getLevel(), ClientLevelEnum.MANAGE.getLevel())) {
                twoOrThreeClientIds.add(e.getClientId());
            }
            if (Objects.equals(e.getLevel(), ClientLevelEnum.APPLY.getLevel())) {
                twoOrThreeClientIds.add(e.getClientId());
            }
            if (Objects.equals(e.getLevel(), ClientLevelEnum.VIEW.getLevel())) {
                oneClientIds.add(e.getClientId());
            }
        });
        // 管护权和申办权的
        if (CollectionUtil.isNotEmpty(twoOrThreeClientIds)) {
            targetClientIds.addAll(twoOrThreeClientIds);
        }
        // 部门新建的客户
        List<ClientCreateRecord> clientCreateRecordList = SpringUtil.getBean(ClientCreateRecordMapper.class).selectList(
                Wrappers.<ClientCreateRecord>lambdaQuery().in(ClientCreateRecord::getDeptId, deptIds)
        );
        if (CollectionUtil.isNotEmpty(clientCreateRecordList)) {
            Set<Long> myNewClientIds = clientCreateRecordList.stream().map(ClientCreateRecord::getClientId).collect(Collectors.toSet());
            List<Client> myNew = clientMapper.selectList(
                    Wrappers.<Client>lambdaQuery()
                            .eq(Client::getClientStatus, ClientStatus.NEW.name())
                            .eq(Client::getIsReleased, YesOrNoNumberEnum.NO.getCode())
                            .in(Client::getId, myNewClientIds)
            );
            if (CollectionUtil.isNotEmpty(myNew)) {
                targetClientIds.addAll(myNew.stream().map(Client::getId).collect(Collectors.toSet()));
            }
        }
        // 释放未被别人占用的（可以理解为有查看权但是客户状态是新建被释放）
        if (CollectionUtil.isNotEmpty(oneClientIds)) {
            List<Client> myRelease = clientMapper.selectList(
                    Wrappers.<Client>lambdaQuery()
                            .eq(Client::getClientStatus, ClientStatus.NEW.name())
                            .eq(Client::getIsReleased, YesOrNoNumberEnum.YES.getCode())
                            .in(Client::getId, oneClientIds)
            );
            if (CollectionUtil.isNotEmpty(myRelease)) {
                targetClientIds.addAll(myRelease.stream().map(Client::getId).collect(Collectors.toSet()));
            }
        }
        return targetClientIds;
    }

    public Set<Long> findTargetClientIdsByUserId(Long currentUserId) {
        // 自己新建+管护权的+释放未被别人占用的+申办权的
        Set<Long> targetClientIds = new HashSet<>();
        // 有权限的客户信息
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(
                Wrappers.<ClientAuthority>lambdaQuery()
                        .eq(ClientAuthority::getUserId, currentUserId)
        );
        // 管护权和申办权一类，查看权一类
        Set<Long> twoOrThreeClientIds = new HashSet<>();
        Set<Long> oneClientIds = new HashSet<>();
        clientAuthorityList.forEach(e -> {
            if (Objects.equals(e.getLevel(), ClientLevelEnum.MANAGE.getLevel())) {
                twoOrThreeClientIds.add(e.getClientId());
            }
            if (Objects.equals(e.getLevel(), ClientLevelEnum.APPLY.getLevel())) {
                twoOrThreeClientIds.add(e.getClientId());
            }
            if (Objects.equals(e.getLevel(), ClientLevelEnum.VIEW.getLevel())) {
                oneClientIds.add(e.getClientId());
            }
        });
        // 管护权和申办权的
        if (CollectionUtil.isNotEmpty(twoOrThreeClientIds)) {
            targetClientIds.addAll(twoOrThreeClientIds);
        }
        // 自己新建的客户
        List<ClientCreateRecord> clientCreateRecordList = SpringUtil.getBean(ClientCreateRecordMapper.class).selectList(
                Wrappers.<ClientCreateRecord>lambdaQuery().eq(ClientCreateRecord::getUserId, currentUserId)
        );
        if (CollectionUtil.isNotEmpty(clientCreateRecordList)) {
            Set<Long> myNewClientIds = clientCreateRecordList.stream().map(ClientCreateRecord::getClientId).collect(Collectors.toSet());
            List<Client> myNew = clientMapper.selectList(
                    Wrappers.<Client>lambdaQuery()
                            .eq(Client::getClientStatus, ClientStatus.NEW.name())
//                            .eq(Client::getIsReleased, YesOrNoNumberEnum.NO.getCode())
                            .in(Client::getId, myNewClientIds)
            );
            if (CollectionUtil.isNotEmpty(myNew)) {
                targetClientIds.addAll(myNew.stream().map(Client::getId).collect(Collectors.toSet()));
            }
            // 补充一下生效的自己创建的公海客户
            List<CorpCommerceInfo> corpCommerceInfoList = corpCommerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery().in(ClientBaseModel::getClientId, myNewClientIds).eq(CorpCommerceInfo::getRiskControlIndustryClassify, RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
            if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
                Set<Long> ghClientIds = corpCommerceInfoList.stream().map(ClientBaseModel::getClientId).collect(Collectors.toSet());
                List<Client> ghClient = clientMapper.selectList(
                        Wrappers.<Client>lambdaQuery().eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name()).in(Client::getId, ghClientIds)
                );
                if (CollectionUtil.isNotEmpty(ghClient)) {
                    targetClientIds.addAll(ghClient.stream().map(Client::getId).collect(Collectors.toSet()));
                }
            }
        }
        // 释放未被别人占用的

        if (CollectionUtil.isNotEmpty(oneClientIds)) {
            List<Client> myRelease = clientMapper.selectList(
                    Wrappers.<Client>lambdaQuery()
                            .eq(Client::getClientStatus, ClientStatus.NEW.name())
                            .eq(Client::getIsReleased, YesOrNoNumberEnum.YES.getCode())
                            .in(Client::getId, oneClientIds)
            );
            if (CollectionUtil.isNotEmpty(myRelease)) {
                targetClientIds.addAll(myRelease.stream().map(Client::getId).collect(Collectors.toSet()));
            }
        }
        return targetClientIds;
    }

    public List<Client> newList(ClientListREQ req) {
        LocalDateTime from = isNull(req.getCreateDateFrom()) ? null : req.getCreateDateFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getCreateDateTo()) ? null : req.getCreateDateTo().plusDays(1).atStartOfDay();

        LocalDateTime updateFrom = isNull(req.getUpdateDateFrom()) ? null : req.getUpdateDateFrom().atStartOfDay();
        LocalDateTime updateTo = isNull(req.getUpdateDateTo()) ? null : req.getUpdateDateTo().plusDays(1).atStartOfDay();
        ClientListParam listParam = null;
        if (StringUtils.isNotBlank(req.getClientName())) {
            listParam = new ClientListParam()
                    .setClientCode(req.getClientCode())
                    .setClientName(req.getClientName())
                    .setClientType(req.getClientType())
                    .setIndustryType(req.getIndustryType())
                    .setCreateFrom(from)
                    .setCreateTo(to)
                    .setUpdateFrom(updateFrom)
                    .setUpdateTo(updateTo)
                    .setClientStatus(req.getClientStatus())
                    .setProcessStatus(req.getProcessStatus())
                    .setEffected(req.getEffected());
            Page<Client> data = clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
            return data.getRecords();
        } else {
            listParam = new ClientListParam()
                    .setClientCode(req.getClientCode())
                    .setClientName(req.getClientName())
                    .setClientType(req.getClientType())
                    .setIndustryType(req.getIndustryType())
                    .setCreateFrom(from)
                    .setCreateTo(to)
                    .setUpdateFrom(updateFrom)
                    .setUpdateTo(updateTo)
                    .setClientStatus(req.getClientStatus())
                    .setProcessStatus(req.getProcessStatus())
                    .setCreateById(req.getCreateBy())
                    .setCreateByDeptId(req.getCreateByDeptId())
                    .setEffected(req.getEffected())
                    .setBelongDeptId(req.getBelongDeptId())
                    .setBelongSponsorId(req.getBelongSponsorId());
        }

        // 如果当前用户是项目经理
        /*if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            listParam.setBelongSponsorId(AccountUtil.getLoginInfo().getId());
        }
        // 如果当前用户是业务负责人 且 不是分管领导
        if (sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name()) && !sysUserService.currentUserIsSpecificJob(JobEnum.leaderincharge.name())) {
            List<Long> deptIds = sysUserService.canViewDeptIds();
            if (!isEmpty(deptIds)) {
                listParam.setBelongDeptId(deptIds.get(0));
            }
        }*/
        // 如果当前用户是非业务部门人员
        //return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);

        // 如果当前用户是非业务部门人员
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        Page<Client> data = clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
        List<Client> res = new ArrayList<>();
        List<Client> clientList = data.getRecords();
        Long userId = AccountUtil.getLoginInfo().getId();
        // 如果当前用户是项目经理
        if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            for (Client client : clientList) {
                boolean isIntraGroup = clientAuthorityUtil.isIntraGroupCollaboration(client.getId());
                if (isIntraGroup) {
                    res.add(client);
                    continue;
                }
                ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                        .eq(ClientAuthority::getClientId, client.getId())
                        .eq(ClientAuthority::getUserId, userId)
                        .eq(ClientAuthority::getDeleted, 0));
                if (clientAuthorityUtil.isNewClient(client)
                    && client.getCreateBy().equals(userId)){
                    res.add(client);
                } else if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                        && clientAuthority != null
                        && clientAuthority.getLevel().equals(ClientLevelEnum.MANAGE.getLevel())) {
                        res.add(client);
                } else if (clientAuthorityUtil.isReleasedClient(client)) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (clientAuthorityList.isEmpty()
                            && (client.getBelongSponsorId() == null || userId.equals(client.getBelongSponsorId()))) {
                        res.add(client);
                    }
                } else if (clientAuthority != null && clientAuthority.getLevel().equals(ClientLevelEnum.APPLY.getLevel())) {
                    res.add(client);
                }
            }
        } else if (sysUserService.currentUserIsSpecificJob(JobEnum.businesshead.name())) {
            List<OrgDO> orgDOList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.leaderincharge.name());
            Set<Long> deptIdSet = orgDOList.stream().map(OrgDO::getId).collect(Collectors.toSet());
            for (Client client : clientList) {
                boolean isIntraGroup = clientAuthorityUtil.isIntraGroupCollaboration(client.getId());
                if (isIntraGroup) {
                    res.add(client);
                    continue;
                }
                if (clientAuthorityUtil.isNewClient(client)
                    && deptIdSet.contains(client.getBelongDeptId())) {
                    res.add(client);
                } else if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                        && deptIdSet.contains(client.getBelongDeptId())) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (!clientAuthorityList.isEmpty()) {
                        res.add(client);
                    }
                } else if (clientAuthorityUtil.isReleasedClient(client)
                        && deptIdSet.contains(client.getBelongDeptId())) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (clientAuthorityList.isEmpty()) {
                        res.add(client);
                    }
                } else if (deptIdSet.contains(client.getBelongDeptId())) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.APPLY.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (!clientAuthorityList.isEmpty()) {
                        res.add(client);
                    }
                }
            }
        } else if (sysUserService.currentUserIsSpecificJob(JobEnum.leaderincharge.name())) {
            // 分管领导查看处理人为所管理部门的任务
            List<OrgDO> orgDOList = sysUserService.listOrgByJob(AccountUtil.getLoginInfo().getId(), JobEnum.leaderincharge.name());
            Set<Long> deptIdSet = orgDOList.stream().map(OrgDO::getId).collect(Collectors.toSet());
            for (Client client : clientList) {
                boolean isIntraGroup = clientAuthorityUtil.isIntraGroupCollaboration(client.getId());
                if (isIntraGroup) {
                    res.add(client);
                    continue;
                }
                if (clientAuthorityUtil.isNewClient(client)
                        && deptIdSet.contains(client.getBelongDeptId())) {
                    res.add(client);
                } else if (ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())
                        && deptIdSet.contains(client.getBelongDeptId())) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (!clientAuthorityList.isEmpty()) {
                        res.add(client);
                    }
                } else if (clientAuthorityUtil.isReleasedClient(client)
                        && deptIdSet.contains(client.getBelongDeptId())) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (clientAuthorityList.isEmpty()) {
                        res.add(client);
                    }
                } else if (deptIdSet.contains(client.getBelongDeptId())) {
                    List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                            .eq(ClientAuthority::getClientId, client.getId())
                            .eq(ClientAuthority::getLevel, ClientLevelEnum.APPLY.getLevel())
                            .eq(ClientAuthority::getDeleted, 0));
                    if (!clientAuthorityList.isEmpty()) {
                        res.add(client);
                    }
                }
            }
        }
        return res;
    }

    public void fillOtherInfo(List<ClientListRSP> list, Boolean showApprovalFlag) {
        Set<String> industryCodeList = new HashSet<>(list.size());
        Set<Long> creatorIdList = new HashSet<>(list.size());
        Set<Long> deptIdList = new HashSet<>(list.size() / 2);
        Set<Long> clientList = new HashSet<>(list.size());
        //行业名称
        list.forEach(e -> {
            clientList.add(e.getId());
            industryCodeList.add(e.getIndustryType());
            creatorIdList.add(e.getCreateBy());
            creatorIdList.add(e.getBelongSponsorId());
            deptIdList.add(e.getCreateByDept());
            deptIdList.add((e.getBelongDeptId()));
        });
        if (CollectionUtil.isNotEmpty(clientList)) {
            Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null);
            if (Objects.nonNull(currentUserId)) {
                // 当前用户创建记录
                List<ClientCreateRecord> clientCreateRecords = SpringUtil.getBean(ClientCreateRecordService.class)
                        .list(Wrappers.<ClientCreateRecord>lambdaQuery()
                                .eq(ClientCreateRecord::getUserId, currentUserId)
                                .in(ClientCreateRecord::getClientId, clientList)
                        );
                Set<Long> createClientIds = clientCreateRecords.stream().map(ClientCreateRecord::getClientId).collect(Collectors.toSet());
//                // 当前用户管护权记录
//                List<ClientAuthority> clientManagerAuthList = clientAuthorityMapper.selectList(
//                        Wrappers.<ClientAuthority>lambdaQuery()
//                                .eq(ClientAuthority::getUserId, currentUserId)
//                                .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
//                );
//                Set<Long> managerClientIds = clientManagerAuthList.stream().map(ClientAuthority::getClientId).collect(Collectors.toSet());
                list.forEach(e -> {
                    if (Objects.equals(e.getClientStatus(), ClientStatus.TAKE_EFFECT.name())) {
                        // 生效不允许删除
                        e.setCanDelete(YesOrNoNumberEnum.NO.getCode());
                    } else {
                        if (Objects.equals(e.getClientType(), ClientType.NORMAL.name())) {
                            e.setCanDelete(Objects.equals(e.getCreateBy(), currentUserId) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
                        } else {
                            // 释放的不允许删除
                            if (Objects.equals(e.getIsReleased(), YesOrNoNumberEnum.YES.getCode())) {
                                e.setCanDelete(YesOrNoNumberEnum.NO.getCode());
                            } else {
                                e.setCanDelete(createClientIds.contains(e.getId()) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode());
                            }
                        }
                    }
                });
            }
        }
        if (!industryCodeList.isEmpty()) {
            Map<String, String> industryMap = industryTypeMapper.selectList(Wrappers.<IndustryType>lambdaQuery().in(IndustryType::getCode, industryCodeList))
                    .stream().collect(Collectors.toMap(IndustryType::getCode, IndustryType::getDisplay));
            list.forEach(e -> {
                e.setIndustryTypeName(industryMap.get(e.getIndustryType()));
            });
        }
        Map<Long, String> nameMap = id2NameService.sysUserId2Name(creatorIdList);
        Map<Long, String> orgNameMap = id2NameService.deptId2Name(deptIdList);
        list.forEach(e -> {
            e.setBelongSponsorName(nameMap.get(e.getBelongSponsorId()));
            e.setBelongDeptName(orgNameMap.get(e.getBelongDeptId()));
            e.setCreatorName(nameMap.get(e.getCreateBy()));
            e.setCreatorDeptName(orgNameMap.get(e.getCreateByDept()));
            e.setProcessStatusName(Optional.ofNullable(ClientProcessStatus.of(e.getProcessStatus())).map(ClientProcessStatus::display).orElse(null));
        });
        // 是否需要变更的标志
        if (Boolean.TRUE.equals(showApprovalFlag)) {
            list.forEach(e -> {
                e.setNeedApprovalFlag(false);
                if (ClientProcessStatus.UN_SUBMIT.name().equals(e.getProcessStatus())) {
                    boolean relatedProjReviewFlag = projReviewService.clientRelatedProjReview(e.getId());
                    e.setNeedApprovalFlag(relatedProjReviewFlag);
                    e.setProcessStatusName(e.getProcessStatusName() + (relatedProjReviewFlag ? "（待提交审批）" : "（无需审批）"));
                }
            });
        }

        if (CollUtil.isNotEmpty(clientList)) {
            List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()).in(ProjReviewBaseInfo::getClientId, clientList));
            if (CollUtil.isNotEmpty(projReviewBaseInfos)) {
                Map<Long, List<ProjReviewBaseInfo>> reviewMap = projReviewBaseInfos.stream().collect(Collectors.groupingBy(ProjReviewBaseInfo::getClientId));
                List<Long> reviewIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
                Map<Long, ProjReviewPriceDetailRSP> reviewPriceMap = getReviewPriceMap(reviewIds);
                list.forEach(e -> {
                    List<ProjReviewBaseInfo> infoList = reviewMap.get(e.getId());
                    if (CollUtil.isNotEmpty(infoList)) {
                        for (ProjReviewBaseInfo projReviewBaseInfo : infoList) {
                            ProjReviewPriceDetailRSP projReviewPriceDetailRSP = reviewPriceMap.get(projReviewBaseInfo.getId());
                            if (projReviewPriceDetailRSP != null) {
                                e.setApplyCreditAmount(LongUtil.null2zero(e.getApplyCreditAmount()) + LongUtil.null2zero(projReviewPriceDetailRSP.getApprovedAmount()));
                            }
                        }
                    }
                    e.setApplyCreditAmount(Optional.ofNullable(e.getApplyCreditAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::longValue).orElse(0L));
                });
            }

            List<Long> ids = list.stream().map(ClientListRSP::getId).collect(Collectors.toList());
            Map<Long, Long> clientStockRiskExposureMap = this.clientStockRiskExposureMap(ids);
            Map<Long, Long> clientRemainingPrincipalMap = this.getClientRemainingPrincipalMap(ids);
            for (ClientListRSP e : list) {
                e.setStockRiskExposure(LongUtil.null2zero(clientStockRiskExposureMap.get(e.getId())));
                e.setLastPrincipal(Optional.ofNullable(clientRemainingPrincipalMap.get(e.getId())).map(Util::mithrasLong2BigDecimal).map(BigDecimal::longValue).orElse(0L));
                e.setStockRiskExposure(Optional.ofNullable(e.getStockRiskExposure()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::longValue).orElse(0L));
            }
        }
        //如果敞口<0,则设置为0
        list.forEach(e -> {
            if (e.getStockRiskExposure() != null && e.getStockRiskExposure() < 0) {
                e.setStockRiskExposure(0L);
            }
        });
        // 补充权限数据
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null);
        boolean isProjmanager = sysUserService.userIsSpecificJob(currentUserId, projmanager.name());
        boolean isBusinesshead = sysUserService.userIsSpecificJob(currentUserId, businesshead.name());
        boolean isLeaderincharge = sysUserService.userIsSpecificJob(currentUserId, leaderincharge.name());
        if (CollectionUtil.isNotEmpty(clientList) && isProjmanager && !isBusinesshead && !isLeaderincharge) {
            LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
            query.in(ClientAuthority::getClientId, clientList);
            query.eq(ClientAuthority::getUserId, currentUserId);
            List<ClientAuthority> clientAuthorityList = clientAuthorityService.list(query);
            Map<Long, List<ClientAuthority>> clientAuthorityMap = clientAuthorityList.stream().collect(Collectors.groupingBy(ClientAuthority::getClientId));
            list.forEach(e -> {
                List<ClientAuthority> clientAuthorities = clientAuthorityMap.get(e.getId());
                if (CollectionUtil.isNotEmpty(clientAuthorities)) {
                    clientAuthorities.sort(Comparator.comparing(ClientAuthority::getLevel).reversed());
                    ClientLevelEnum level = ClientLevelEnum.findByLevel(clientAuthorities.get(0).getLevel());
                    e.setMaxAuthority(Optional.ofNullable(level).map(ClientLevelEnum::display).orElse("未知的权限"));
                }
            });
        }

    }

    public Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds) {
        return this.clientStockRiskExposureMap(clientIds, null);
    }

    /**
     * 批量获取客户的风险敞口, 理论上所有的地方的风险敞口都可以使用这个计算，返回的金额是数据库，需要转化单位自己转
     *
     * @param clientIds 客户id集合
     * @return 客户ID和风险敞口映射
     */
    public Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds, LocalDate targetDate) {
        Map<Long, Long> stockRiskExposureMap = new HashMap<>();
        //存续合同，即生效+起租
        List<String> statusList = ListUtil.toList(ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name());
        //查询客户下合同
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, clientIds)
                .in(ContractBaseInfo::getContractStatus, statusList)
        );
        if (CollUtil.isNotEmpty(contractBaseInfos)) {
            Map<Long, List<ContractBaseInfo>> contractMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
            List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            List<PaymentActualDetail> actualDetails = paymentActualDetailService.getBaseMapper().selectList(Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).in(PaymentActualDetail::getContractId, contractIds));
            Map<Long, List<PaymentActualDetail>> casMap = new HashMap<>();
            if (CollUtil.isNotEmpty(actualDetails)) {
                casMap = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
            }
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name())).in(CollectionBaseInfo::getContractId, contractIds));
            Map<Long, List<CollectionBaseInfo>> ccsMap = new HashMap<>();
            if (CollUtil.isNotEmpty(actualDetails)) {
                ccsMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
            }
            Map<Long, Long> marginBalancesMap = marginBaseInfoService.getMarginBalances(contractIds);
            for (Long clientId : clientIds) {
                // 临时存当前客户的剩余本金
                Long e = 0L;
                List<ContractBaseInfo> infos = contractMap.get(clientId);
                List<Long> currentMarginBalances = new ArrayList<>();
                if (CollUtil.isNotEmpty(infos)) {
                    for (ContractBaseInfo info : infos) {
                        if (Objects.nonNull(marginBalancesMap.get(info.getId()))) {
                            currentMarginBalances.add(marginBalancesMap.get(info.getId()));
                        }
                        List<PaymentActualDetail> actualDetails1 = casMap.get(info.getId());
                        if (CollUtil.isNotEmpty(actualDetails1)) {
                            for (PaymentActualDetail paymentActualDetail : actualDetails1) {
                                if (Objects.nonNull(targetDate) && Objects.nonNull(paymentActualDetail.getPaidInDate()) && paymentActualDetail.getPaidInDate().isAfter(targetDate)) {
                                    // 如果指定了目标日期则跳过目标日期之后的
                                    continue;
                                }
                                e += LongUtil.null2zero(paymentActualDetail.getPaidInAmount());
                            }
                        }
                        List<CollectionBaseInfo> collectionBaseInfoList = ccsMap.get(info.getId());
                        if (CollUtil.isNotEmpty(collectionBaseInfoList)) {
                            for (CollectionBaseInfo baseInfo : collectionBaseInfoList) {
                                if (Objects.nonNull(targetDate) && Objects.nonNull(baseInfo.getCollectionDate()) && baseInfo.getCollectionDate().isAfter(targetDate)) {
                                    // 如果指定了目标日期则跳过目标日期之后的
                                    continue;
                                }
                                Long amount;
                                if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem())) {
                                    amount = baseInfo.getCollectionAmount();
                                } else {
                                    amount = baseInfo.getCollectionPrincipal();
                                }
                                e -= LongUtil.null2zero(amount);
                            }
                        }
                    }
                }
                long totalMargin = 0;
                if (CollUtil.isNotEmpty(currentMarginBalances)) {
                    totalMargin = currentMarginBalances.stream().mapToLong(Long::longValue).sum();
                }
                stockRiskExposureMap.put(clientId, LongUtil.null2zero(e - totalMargin));
            }
        }
        return stockRiskExposureMap;
    }

    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds) {
        return this.getClientRemainingPrincipalMap(clientIds, null);
    }

    /**
     * 获取客户的剩余本金
     *
     * @param clientIds
     * @return
     */
    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds, LocalDate targetDate) {
        Map<Long, Long> clientLastPrincipalMap = new HashMap<>();
        //存续合同，即生效+起租
        List<String> statusList = ListUtil.toList(ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name());
        //查询客户下合同
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.getBaseMapper().selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getClientId, clientIds)
                .in(ContractBaseInfo::getContractStatus, statusList)
        );
        if (CollUtil.isNotEmpty(contractBaseInfos)) {
            Map<Long, List<ContractBaseInfo>> contractMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
            List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            List<PaymentActualDetail> actualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).in(PaymentActualDetail::getContractId, contractIds));
            Map<Long, List<PaymentActualDetail>> casMap = new HashMap<>();
            if (CollUtil.isNotEmpty(actualDetails)) {
                casMap = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
            }
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name())).in(CollectionBaseInfo::getContractId, contractIds));
            Map<Long, List<CollectionBaseInfo>> ccsMap = new HashMap<>();
            if (CollUtil.isNotEmpty(actualDetails)) {
                ccsMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
            }
            for (Long clientId : clientIds) {
                // 临时存当前客户的剩余本金
                Long e = 0L;
                List<ContractBaseInfo> infos = contractMap.get(clientId);
                if (CollUtil.isNotEmpty(infos)) {
                    for (ContractBaseInfo info : infos) {
                        List<PaymentActualDetail> actualDetails1 = casMap.get(info.getId());
                        if (CollUtil.isNotEmpty(actualDetails1)) {
                            for (PaymentActualDetail paymentActualDetail : actualDetails1) {
                                if (Objects.nonNull(targetDate) && Objects.nonNull(paymentActualDetail.getPaidInDate()) && paymentActualDetail.getPaidInDate().isAfter(targetDate)) {
                                    // 如果指定了目标日期则跳过目标日期之后的
                                    continue;
                                }
                                e += LongUtil.null2zero(paymentActualDetail.getPaidInAmount());
                            }
                        }
                        List<CollectionBaseInfo> collectionBaseInfoList = ccsMap.get(info.getId());
                        if (CollUtil.isNotEmpty(collectionBaseInfoList)) {
                            for (CollectionBaseInfo baseInfo : collectionBaseInfoList) {
                                if (Objects.nonNull(targetDate) && Objects.nonNull(baseInfo.getCollectionDate()) && baseInfo.getCollectionDate().isAfter(targetDate)) {
                                    // 如果指定了目标日期则跳过目标日期之后的
                                    continue;
                                }
                                Long amount;
                                if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem())) {
                                    amount = baseInfo.getCollectionAmount();
                                } else {
                                    amount = baseInfo.getCollectionPrincipal();
                                }
                                e -= LongUtil.null2zero(amount);
                            }
                        }
                    }
                }
                clientLastPrincipalMap.put(clientId, LongUtil.null2zero(e));
            }
        }
        return clientLastPrincipalMap;
    }

    public Map<Long, ProjReviewPriceDetailRSP> getReviewPriceMap(List<Long> ids) {
        Set<Long> reviewIds = new HashSet<>(ids);
        Map<Long, ProjReviewPriceDetailRSP> priceMap = new HashMap<>();
        Map<Long, ProjReviewLeasePrice> leasePricesMap = new HashMap<>();
        Map<Long, ProjReviewFactoringPrice> factoringPricesMap = new HashMap<>();
        Map<Long, ProjReviewAocPrice> aocPricesMap = new HashMap<>();
        List<ProjReviewLeasePriceLib> leasePrices = projReviewLeasePriceLibService.listNewestByProjReviewIds(reviewIds);
        if (CollUtil.isNotEmpty(leasePrices)) {
            leasePricesMap = leasePrices.stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, o -> o));
        }
        List<ProjReviewFactoringPriceLib> factoringPrices = projReviewFactoringPriceLibService.listNewestByProjReviewIds(reviewIds);
        if (CollUtil.isNotEmpty(leasePrices)) {
            factoringPricesMap = factoringPrices.stream().collect(Collectors.toMap(ProjReviewFactoringPrice::getProjectId, o -> o));
        }
        List<ProjReviewAocPriceLib> aocPrices = projReviewAocPriceLibService.listNewestByProjReviewIds(reviewIds);
        if (CollUtil.isNotEmpty(leasePrices)) {
            aocPricesMap = aocPrices.stream().collect(Collectors.toMap(ProjReviewAocPrice::getProjectId, o -> o));
        }
        for (Long id : ids) {
            ProjReviewPriceDetailRSP res = new ProjReviewPriceDetailRSP();
            // 处理租赁
            res.setLeasePriceDetailRSP(projReviewPriceConverter.entityToLeaseRsp(leasePricesMap.get(id)));
            // 处理保理
            ProjReviewFactoringPriceRSP projReviewFactoringPriceRSP = projReviewPriceConverter.entityToFactoringRsp(factoringPricesMap.get(id));
            res.setFactoringPriceDetailRSP(projReviewFactoringPriceRSP);
            // 处理债权转让
            ProjReviewAocPriceRSP projReviewAocPriceRSP = projReviewPriceConverter.entityToAocRsp(aocPricesMap.get(id));
            res.setAocPriceDetailRSP(projReviewAocPriceRSP);
            priceMap.put(id, res);
        }
        return priceMap;
    }

    public List<Client> listByClientCodeIsNull() {
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery().isNull(Client::getClientCode));
    }

    public void updateClientCode(List<Client> list) {
        list.stream().forEach(e -> clientMapper.updateById(e));
    }

    public List<Client> getByUserName(String clientName) {
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, clientName));
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long add(Client client, String clientType) {
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        //查客商信息
        DataShareMerchants merchants = dataShareMerchantsService.getMerchants(new DataShareREQ(client.getUscCode()));
        if (!ObjectUtil.isEmpty(merchants)) {
            client.setClientCode(String.valueOf(merchants.getClientId()));//同步客商编号
        }
        client.setCreateByDept(bizOrgDO.getId());
        client.setClientType(clientType);
        client.setClientStatus(NEW.name());
        client.setIsReleased(YesOrNoNumberEnum.NO.getCode());
        client.setProcessStatus(ClientProcessStatus.EFFECT_BLANK.name());
//        //设置所属部门和负责的项目经理为当前用户
//        if (ClientType.NORMAL.name().equals(clientType)) {
//            client.setBelongDeptId(client.getCreateByDept());
//            client.setBelongSponsorId(AccountUtil.getLoginInfo().getId());
//        }
        client.setAuthType(ClientAuthEnum.NO_AFFILIATION.name());
        if (!clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getClientType, ClientType.CORPORATION.name())
                .eq(Client::getClientName, client.getClientName())
        ).isEmpty()) {
            throw new MithrasException(ResultMsg.CLIENT_EXIST);
        }
        try {
            clientMapper.insert(client);
        } catch (DuplicateKeyException e) {
            log.error("客户唯一键重复", e);
            throw new MithrasException(ResultMsg.CLIENT_EXIST);
        }
        return client.getId();
    }

    private void removeCorporation(Client client) {
        List<Long> clientIdList = ListUtil.toList(client.getId());
        Long userId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        boolean highSea = false;
        try {
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
            if (corpCommerceInfoLib != null
                    && RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name().equalsIgnoreCase(corpCommerceInfoLib.getRiskControlIndustryClassify())) {
                highSea = true;
            }
        } catch (MithrasException e) {
            log.error(e.getMessage());
        }
//        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
//                .eq(ClientAuthority::getClientId, client.getId())
//                .eq(ClientAuthority::getUserId, userId)
//                .eq(ClientAuthority::getDeleted, 0));
        if (clientAuthorityUtil.isNewClient(client)) {
            // 新建客户只删除自己的
            clientAuthorityUtil.clearNewData(client.getId(), userId);
            // 如果用户版本的创建记录没有任何数据了，说明没有其他人的版本了，把client主表也删除
            int count = SpringUtil.getBean(ClientCreateRecordService.class).count(Wrappers.<ClientCreateRecord>lambdaQuery().eq(ClientCreateRecord::getClientId, client.getId()));
            if (count == 0) {
                clientMapper.deleteById(client.getId());
            }
        } else {
            throw new MithrasException("当前状态不允许删除");
        }
//        if (clientAuthorityUtil.isEffectClient(client)) {
//            if (clientAuthority != null && ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
//                // 生效客户且操作人是管护权人，删除全部
//                clientMapper.delete(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIdList));
//                //
//                bondInfoMapper.delete(Wrappers.<CorpBondInfo>lambdaQuery().in(CorpBondInfo::getClientId, clientIdList));
//                bondInfoLibMapper.delete(Wrappers.<CorpBondInfoLib>lambdaQuery().in(CorpBondInfoLib::getClientId, clientIdList));
//                newCorpBondInfoLibMapper.delete(Wrappers.<NewCorpBondInfoLib>lambdaQuery()
//                        .in(NewCorpBondInfoLib::getClientId, clientIdList));
//                newCorpBondInfoMapper.delete(Wrappers.<NewCorpBondInfo>lambdaQuery()
//                        .in(NewCorpBondInfo::getClientId, clientIdList));
//                //
//                addressInfoMapper.delete(Wrappers.<CorpAddressInfo>lambdaQuery().in(CorpAddressInfo::getClientId, clientIdList));
//                addressInfoLibMapper.delete(Wrappers.<CorpAddressInfoLib>lambdaQuery().in(CorpAddressInfoLib::getClientId, clientIdList));
//                newCorpAddressInfoLibMapper.delete(Wrappers.<NewCorpAddressInfoLib>lambdaQuery()
//                        .in(NewCorpAddressInfoLib::getClientId, clientIdList));
//                newCorpAddressInfoMapper.delete(Wrappers.<NewCorpAddressInfo>lambdaQuery()
//                        .in(NewCorpAddressInfo::getClientId, clientIdList));
//                //
//                relatedEnterpriseMapper.delete(Wrappers.<CorpRelatedEnterprise>lambdaQuery().in(CorpRelatedEnterprise::getClientId, clientIdList));
//                relatedEnterpriseLibMapper.delete(Wrappers.<CorpRelatedEnterpriseLib>lambdaQuery().in(CorpRelatedEnterpriseLib::getClientId, clientIdList));
//                newCorpRelatedEnterpriseLibMapper.delete(Wrappers.<NewCorpRelatedEnterpriseLib>lambdaQuery()
//                        .in(NewCorpRelatedEnterpriseLib::getClientId, clientIdList));
//                newCorpRelatedEnterpriseMapper.delete(Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
//                        .in(NewCorpRelatedEnterprise::getClientId, clientIdList));
//                //
//                contactInfoMapper.delete(Wrappers.<CorpContactInfo>lambdaQuery().in(CorpContactInfo::getClientId, clientIdList));
//                contactInfoLibMapper.delete(Wrappers.<CorpContactInfoLib>lambdaQuery().in(CorpContactInfoLib::getClientId, clientIdList));
//                newCorpContactInfoLibMapper.delete(Wrappers.<NewCorpContactInfoLib>lambdaQuery()
//                        .in(NewCorpContactInfoLib::getClientId, clientIdList));
//                newCorpContactInfoMapper.delete(Wrappers.<NewCorpContactInfo>lambdaQuery()
//                        .in(NewCorpContactInfo::getClientId, clientIdList));
//                //
//                shareholderInfoMapper.delete(Wrappers.<CorpShareholderInfo>lambdaQuery().in(CorpShareholderInfo::getClientId, clientIdList));
//                shareholderInfoLibMapper.delete(Wrappers.<CorpShareholderInfoLib>lambdaQuery().in(CorpShareholderInfoLib::getClientId, clientIdList));
//                newCorpShareholderInfoLibMapper.delete(Wrappers.<NewCorpShareholderInfoLib>lambdaQuery()
//                        .in(NewCorpShareholderInfoLib::getClientId, clientIdList));
//                newCorpShareholderInfoMapper.delete(Wrappers.<NewCorpShareholderInfo>lambdaQuery()
//                        .in(NewCorpShareholderInfo::getClientId, clientIdList));
//                //
//                bankAccountMapper.delete(Wrappers.<CorpBankAccount>lambdaQuery().in(CorpBankAccount::getClientId, clientIdList));
//                bankAccountLibMapper.delete(Wrappers.<CorpBankAccountLib>lambdaQuery().in(CorpBankAccountLib::getClientId, clientIdList));
//                newCorpBankAccountLibMapper.delete(Wrappers.<NewCorpBankAccountLib>lambdaQuery()
//                        .in(NewCorpBankAccountLib::getClientId, clientIdList));
//                newCorpBankAccountMapper.delete(Wrappers.<NewCorpBankAccount>lambdaQuery()
//                        .in(NewCorpBankAccount::getClientId, clientIdList));
//                //
//                commerceInfoMapper.delete(Wrappers.<CorpCommerceInfo>lambdaQuery().in(CorpCommerceInfo::getClientId, clientIdList));
//                commerceInfoLibMapper.delete(Wrappers.<CorpCommerceInfoLib>lambdaQuery().in(CorpCommerceInfoLib::getClientId, clientIdList));
//                newCorpCommerceInfoMapper.delete(Wrappers.<NewCorpCommerceInfo>lambdaQuery()
//                        .in(NewCorpCommerceInfo::getClientId, clientIdList));
//                newCorpCommerceInfoLibMapper.delete(Wrappers.<NewCorpCommerceInfoLib>lambdaQuery()
//                        .in(NewCorpCommerceInfoLib::getClientId, clientIdList));
//                subjectItemMapper.delete(Wrappers.<CorpSubjectItem>lambdaQuery().in(CorpSubjectItem::getClientId, clientIdList));
//                commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.CLIENT.name()).in(CommonVersion::getMainId, clientIdList));
//                //删除项目经理客户权限
//                clientAuthority.setDeleted(1);
//                clientAuthorityMapper.updateById(clientAuthority);
//            } else {
//                throw new MithrasException(String.format("生效状态客户需要拥有管护权项目经理%s删除", id2NameService.sysUserId2NameSingle(userId)));
//            }
//        }
//        if (clientAuthorityUtil.isReleasedClient(client) || highSea) {
//            // 释放状态和公海都可以删除 清理数据
//            clientMapper.delete(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIdList));
//            bondInfoMapper.delete(Wrappers.<CorpBondInfo>lambdaQuery().in(CorpBondInfo::getClientId, clientIdList));
//            bondInfoLibMapper.delete(Wrappers.<CorpBondInfoLib>lambdaQuery().in(CorpBondInfoLib::getClientId, clientIdList));
//            addressInfoMapper.delete(Wrappers.<CorpAddressInfo>lambdaQuery().in(CorpAddressInfo::getClientId, clientIdList));
//            addressInfoLibMapper.delete(Wrappers.<CorpAddressInfoLib>lambdaQuery().in(CorpAddressInfoLib::getClientId, clientIdList));
//            relatedEnterpriseMapper.delete(Wrappers.<CorpRelatedEnterprise>lambdaQuery().in(CorpRelatedEnterprise::getClientId, clientIdList));
//            relatedEnterpriseLibMapper.delete(Wrappers.<CorpRelatedEnterpriseLib>lambdaQuery().in(CorpRelatedEnterpriseLib::getClientId, clientIdList));
//            contactInfoMapper.delete(Wrappers.<CorpContactInfo>lambdaQuery().in(CorpContactInfo::getClientId, clientIdList));
//            contactInfoLibMapper.delete(Wrappers.<CorpContactInfoLib>lambdaQuery().in(CorpContactInfoLib::getClientId, clientIdList));
//            shareholderInfoMapper.delete(Wrappers.<CorpShareholderInfo>lambdaQuery().in(CorpShareholderInfo::getClientId, clientIdList));
//            shareholderInfoLibMapper.delete(Wrappers.<CorpShareholderInfoLib>lambdaQuery().in(CorpShareholderInfoLib::getClientId, clientIdList));
//            bankAccountMapper.delete(Wrappers.<CorpBankAccount>lambdaQuery().in(CorpBankAccount::getClientId, clientIdList));
//            bankAccountLibMapper.delete(Wrappers.<CorpBankAccountLib>lambdaQuery().in(CorpBankAccountLib::getClientId, clientIdList));
//            commerceInfoMapper.delete(Wrappers.<CorpCommerceInfo>lambdaQuery().in(CorpCommerceInfo::getClientId, clientIdList));
//            commerceInfoLibMapper.delete(Wrappers.<CorpCommerceInfoLib>lambdaQuery().in(CorpCommerceInfoLib::getClientId, clientIdList));
//            bondInfoMapper.delete(Wrappers.<CorpBondInfo>lambdaQuery().in(CorpBondInfo::getClientId, clientIdList));
//            bondInfoLibMapper.delete(Wrappers.<CorpBondInfoLib>lambdaQuery().in(CorpBondInfoLib::getClientId, clientIdList));
//            newCorpBondInfoLibMapper.delete(Wrappers.<NewCorpBondInfoLib>lambdaQuery()
//                    .in(NewCorpBondInfoLib::getClientId, clientIdList));
//            newCorpBondInfoMapper.delete(Wrappers.<NewCorpBondInfo>lambdaQuery()
//                    .in(NewCorpBondInfo::getClientId, clientIdList));
//            //
//            addressInfoMapper.delete(Wrappers.<CorpAddressInfo>lambdaQuery().in(CorpAddressInfo::getClientId, clientIdList));
//            addressInfoLibMapper.delete(Wrappers.<CorpAddressInfoLib>lambdaQuery().in(CorpAddressInfoLib::getClientId, clientIdList));
//            newCorpAddressInfoLibMapper.delete(Wrappers.<NewCorpAddressInfoLib>lambdaQuery()
//                    .in(NewCorpAddressInfoLib::getClientId, clientIdList));
//            newCorpAddressInfoMapper.delete(Wrappers.<NewCorpAddressInfo>lambdaQuery()
//                    .in(NewCorpAddressInfo::getClientId, clientIdList));
//            //
//            relatedEnterpriseMapper.delete(Wrappers.<CorpRelatedEnterprise>lambdaQuery().in(CorpRelatedEnterprise::getClientId, clientIdList));
//            relatedEnterpriseLibMapper.delete(Wrappers.<CorpRelatedEnterpriseLib>lambdaQuery().in(CorpRelatedEnterpriseLib::getClientId, clientIdList));
//            newCorpRelatedEnterpriseLibMapper.delete(Wrappers.<NewCorpRelatedEnterpriseLib>lambdaQuery()
//                    .in(NewCorpRelatedEnterpriseLib::getClientId, clientIdList));
//            newCorpRelatedEnterpriseMapper.delete(Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
//                    .in(NewCorpRelatedEnterprise::getClientId, clientIdList));
//            //
//            contactInfoMapper.delete(Wrappers.<CorpContactInfo>lambdaQuery().in(CorpContactInfo::getClientId, clientIdList));
//            contactInfoLibMapper.delete(Wrappers.<CorpContactInfoLib>lambdaQuery().in(CorpContactInfoLib::getClientId, clientIdList));
//            newCorpContactInfoLibMapper.delete(Wrappers.<NewCorpContactInfoLib>lambdaQuery()
//                    .in(NewCorpContactInfoLib::getClientId, clientIdList));
//            newCorpContactInfoMapper.delete(Wrappers.<NewCorpContactInfo>lambdaQuery()
//                    .in(NewCorpContactInfo::getClientId, clientIdList));
//            //
//            shareholderInfoMapper.delete(Wrappers.<CorpShareholderInfo>lambdaQuery().in(CorpShareholderInfo::getClientId, clientIdList));
//            shareholderInfoLibMapper.delete(Wrappers.<CorpShareholderInfoLib>lambdaQuery().in(CorpShareholderInfoLib::getClientId, clientIdList));
//            newCorpShareholderInfoLibMapper.delete(Wrappers.<NewCorpShareholderInfoLib>lambdaQuery()
//                    .in(NewCorpShareholderInfoLib::getClientId, clientIdList));
//            newCorpShareholderInfoMapper.delete(Wrappers.<NewCorpShareholderInfo>lambdaQuery()
//                    .in(NewCorpShareholderInfo::getClientId, clientIdList));
//            //
//            bankAccountMapper.delete(Wrappers.<CorpBankAccount>lambdaQuery().in(CorpBankAccount::getClientId, clientIdList));
//            bankAccountLibMapper.delete(Wrappers.<CorpBankAccountLib>lambdaQuery().in(CorpBankAccountLib::getClientId, clientIdList));
//            newCorpBankAccountLibMapper.delete(Wrappers.<NewCorpBankAccountLib>lambdaQuery()
//                    .in(NewCorpBankAccountLib::getClientId, clientIdList));
//            newCorpBankAccountMapper.delete(Wrappers.<NewCorpBankAccount>lambdaQuery()
//                    .in(NewCorpBankAccount::getClientId, clientIdList));
//            //
//            commerceInfoMapper.delete(Wrappers.<CorpCommerceInfo>lambdaQuery().in(CorpCommerceInfo::getClientId, clientIdList));
//            commerceInfoLibMapper.delete(Wrappers.<CorpCommerceInfoLib>lambdaQuery().in(CorpCommerceInfoLib::getClientId, clientIdList));
//            newCorpCommerceInfoMapper.delete(Wrappers.<NewCorpCommerceInfo>lambdaQuery()
//                    .in(NewCorpCommerceInfo::getClientId, clientIdList));
//            newCorpCommerceInfoLibMapper.delete(Wrappers.<NewCorpCommerceInfoLib>lambdaQuery()
//                    .in(NewCorpCommerceInfoLib::getClientId, clientIdList));
//            subjectItemMapper.delete(Wrappers.<CorpSubjectItem>lambdaQuery().in(CorpSubjectItem::getClientId, clientIdList));
//            commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.CLIENT.name()).in(CommonVersion::getMainId, clientIdList));
//            //删除项目经理客户权限
//            clientAuthority.setDeleted(1);
//            clientAuthorityMapper.updateById(clientAuthority);
//        }
    }

    private void removeNormal(Client client) {
        clientMapper.deleteById(client.getId());
        normalBaseInfoMapper.delete(Wrappers.<NormalBaseInfo>lambdaQuery().eq(NormalBaseInfo::getClientId, client.getId()));
        normalBaseInfoLibMapper.delete(Wrappers.<NormalBaseInfoLib>lambdaQuery().eq(NormalBaseInfoLib::getClientId, client.getId()));
        normalBankAccountMapper.delete(Wrappers.<NormalBankAccount>lambdaQuery().eq(NormalBankAccount::getClientId, client.getId()));
        normalBankAccountLibMapper.delete(Wrappers.<NormalBankAccountLib>lambdaQuery().eq(NormalBankAccountLib::getClientId, client.getId()));
        normalSpouseMapper.delete(Wrappers.<NormalSpouse>lambdaQuery().eq(NormalSpouse::getClientId, client.getId()));
        normalSpouseLibMapper.delete(Wrappers.<NormalSpouseLib>lambdaQuery().eq(NormalSpouseLib::getClientId, client.getId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        // 关联到立项 或 集团授信立项
        if (projEstablishService.clientRelatedProjEstablish(id) || groupCreditEstablishService.clientRelatedGroupCreditEstablish(id)) {
            throw new MithrasException(ResultMsg.CLIENT_RELATED_PROJECTS);
        }
        Client client = clientMapper.selectById(id);
        if (Objects.isNull(client)) {
            throw new MithrasException("客户主数据不存在");
        }
        if (Objects.equals(ClientType.CORPORATION.name(), client.getClientType())) {
            this.removeCorporation(client);
        } else {
            this.removeNormal(client);
        }
    }

    public ClientSyncRSP sync(Long clientId) {
        ClientSyncRSP rsp = new ClientSyncRSP();
        Client client = clientMapper.selectById(clientId);
        CorpCommerceInfo commerceInfo = commerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, clientId));
        MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo(client.getUscCode());
        CorpCommerceInfoAddREQ fillInfo = new CorpCommerceInfoAddREQ();
        if (isNull(commerceInfo)) {
            copyProperties(mithrasBaseInfo, fillInfo);
        } else if (isNotNull(mithrasBaseInfo)) {
            fillChangedValue(commerceInfo, mithrasBaseInfo, fillInfo);
        }
        if (Objects.nonNull(mithrasBaseInfo)) {
            fillInfo.setIndustryTypeWithParent(mithrasBaseInfo.getIndustryTypeWithParent());
            //名称相同不予展示
            String clientName = client.getClientName();
            //value:()（）
            String symbol = systemConfigMapper.selectOne(Wrappers.<SystemConfig>lambdaQuery()
                    .eq(SystemConfig::getStatus, 1)
                    .eq(SystemConfig::getConfigKey, "symbolsRemove")
            ).getConfigValue();
            for (int i = 0; i < symbol.length(); i++) {
                char value = symbol.charAt(i);
                clientName = clientName.replace(String.valueOf(value), "");
            }
            if (clientName.equals(fillInfo.getClientName())) {
                fillInfo.setClientName(null);
            }

        }
        rsp.setChangedCommerceInfo(fillInfo);
        //法人注册地址变化；注册默认为只有一个
        List<ClientSyncRSP.RegisterAddressChangedItem> addressChangedItems = new ArrayList<>();
        //注册地址
        ClientSyncRSP.RegisterAddressChangedItem registerAddressChangedItem = new ClientSyncRSP.RegisterAddressChangedItem();
//        List<CorpAddressInfo> currentAddressList = corpAddressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
//                .eq(CorpAddressInfo::getClientId, client.getId())
//                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name()));
        List<NewCorpAddressInfo> currentAddressList = SpringUtil.getBean(NewCorpAddressInfoMapper.class)
                .selectList(Wrappers.<NewCorpAddressInfo>lambdaQuery()
                        .eq(CorpAddressInfo::getClientId, client.getId())
                        .eq(NewCorpAddressInfo::getUserId, AccountUtil.getLoginInfo().getId())
                        .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name()));
        //天眼查数据
        if (isNotNull(mithrasBaseInfo)) {
            registerAddressChangedItem.setCountry("156");//默认中国
            registerAddressChangedItem.setCountryName("中国");
            registerAddressChangedItem.setAddressType(CorpAddressType.REGISTRY_ADDRESS.name());
            //
            GeneralDictionary tycProvince = generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
                    .eq(GeneralDictionary::getDictKey, ENUM_TYC_PROVINCE)
                    .eq(GeneralDictionary::getCode, mithrasBaseInfo.getBase())
            );
            if (null != tycProvince) {
                AddressDictionary province = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getDisplay, tycProvince.getDisplay()));
                if (null != province) {
                    registerAddressChangedItem.setProvince(province.getCode());
                    registerAddressChangedItem.setProvinceName(province.getDisplay());
                    AddressDictionary city = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, province.getId())
                            .eq(AddressDictionary::getDisplay, mithrasBaseInfo.getCity()));
                    if (null != city) {
                        registerAddressChangedItem.setCity(city.getCode());
                        registerAddressChangedItem.setCityName(city.getDisplay());
                        AddressDictionary district = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, city.getId())
                                .eq(AddressDictionary::getDisplay, mithrasBaseInfo.getDistrict()));
                        if (null != district) {
                            registerAddressChangedItem.setDistrict(district.getCode());
                            registerAddressChangedItem.setDistrictName(district.getDisplay());
                            registerAddressChangedItem.setRegionCode(district.getCode());
                        }
                    }
                }
            }
            registerAddressChangedItem.setDetail(mithrasBaseInfo.getRegLocation());
            //判断是新增还是修改
            if (currentAddressList.isEmpty()) {
                registerAddressChangedItem.setChangedType(CHANGE_TYPE_ADD);
                addressChangedItems.add(registerAddressChangedItem);
            } else {
                if (notEqual(registerAddressChangedItem.getCountry(), currentAddressList.get(0).getCountry())
                        || notEqual(registerAddressChangedItem.getProvince(), currentAddressList.get(0).getProvince())
                        || notEqual(registerAddressChangedItem.getCity(), currentAddressList.get(0).getCity())
                        || notEqual(registerAddressChangedItem.getDistrict(), currentAddressList.get(0).getDistrict())
                        || notEqual(registerAddressChangedItem.getDetail(), currentAddressList.get(0).getDetail())
                ) {
                    registerAddressChangedItem.setChangedType(CHANGE_TYPE_MODIFY);
                    registerAddressChangedItem.setId(currentAddressList.get(0).getId());
                    addressChangedItems.add(registerAddressChangedItem);
                }
            }
        }
        rsp.setRegisterAddressChangedItemList(PageR.of(addressChangedItems, addressChangedItems.size()));
        //股东信息变化
        List<ClientSyncRSP.ShareholderInfoChangedItem> shareholderInfoChangedItems = new ArrayList<>();
//        Map<String, CorpShareholderInfo> currentShareholderInfoMap = shareholderInfoMapper.selectList(Wrappers.<CorpShareholderInfo>lambdaQuery().eq(CorpShareholderInfo::getClientId, client.getId()))
//                .stream().collect(Collectors.toMap(CorpShareholderInfo::getShareholderName, e -> e));
        Map<String, NewCorpShareholderInfo> currentShareholderInfoMap = SpringUtil.getBean(NewCorpShareholderInfoMapper.class)
                .selectList(Wrappers.<NewCorpShareholderInfo>lambdaQuery()
                        .eq(CorpShareholderInfo::getClientId, client.getId())
                        .eq(NewCorpShareholderInfo::getUserId, AccountUtil.getLoginInfo().getId())
                )
                .stream().collect(Collectors.toMap(CorpShareholderInfo::getShareholderName, e -> e));
        Map<String, MithrasShareholderInfo> tycShareholderInfoMap = tycService.shareholderInfo(client.getUscCode())
                .stream().collect(Collectors.toMap(MithrasShareholderInfo::getShareholderName, e -> e, (t1, t2) -> t2));
        if (!tycShareholderInfoMap.isEmpty()) {
            for (String tycName : tycShareholderInfoMap.keySet()) {
                if (!currentShareholderInfoMap.containsKey(tycName)) {
                    ClientSyncRSP.ShareholderInfoChangedItem item = copyProperties(tycShareholderInfoMap.get(tycName), ClientSyncRSP.ShareholderInfoChangedItem.class);
                    item.setChangedType(CHANGE_TYPE_ADD);
                    shareholderInfoChangedItems.add(item);
                } else {
                    ClientSyncRSP.ShareholderInfoChangedItem fillItem = new ClientSyncRSP.ShareholderInfoChangedItem();
                    List<String> changedFields = fillChangedValue(currentShareholderInfoMap.get(tycName), tycShareholderInfoMap.get(tycName), fillItem);
                    if (isNotEmpty(changedFields)) {
                        BeanUtil.copyProperties(tycShareholderInfoMap.get(tycName), fillItem);
                        fillItem.setId(currentShareholderInfoMap.get(tycName).getId());
                        fillItem.setChangedType(CHANGE_TYPE_MODIFY);
                        fillItem.setChangeFields(changedFields);
                        shareholderInfoChangedItems.add(fillItem);
                    }
                }
            }
            for (String currentName : currentShareholderInfoMap.keySet()) {
                if (!tycShareholderInfoMap.containsKey(currentName)) {
                    ClientSyncRSP.ShareholderInfoChangedItem item = new ClientSyncRSP.ShareholderInfoChangedItem();
                    item.setChangedType(CHANGE_TYPE_DELETE);
                    BeanUtil.copyProperties(currentShareholderInfoMap.get(currentName), item);
                    shareholderInfoChangedItems.add(item);
                }
            }
        }
        rsp.setShareholderInfoChangedItemList(PageR.of(shareholderInfoChangedItems, shareholderInfoChangedItems.size()));
        //related enterprise关联企业变化
        List<ClientSyncRSP.RelatedEnterpriseChangedItem> relatedEnterpriseChangedItems = new ArrayList<>();
//        Map<String, CorpRelatedEnterprise> currentRelatedEnterpriseMap = relatedEnterpriseMapper.selectList(Wrappers.<CorpRelatedEnterprise>lambdaQuery().eq(CorpRelatedEnterprise::getClientId, client.getId()))
//                .stream().collect(Collectors.toMap(CorpRelatedEnterprise::getEnterpriseName, e -> e, (t1, t2) -> t2));
        Map<String, NewCorpRelatedEnterprise> currentRelatedEnterpriseMap = SpringUtil.getBean(NewCorpRelatedEnterpriseMapper.class)
                .selectList(Wrappers.<NewCorpRelatedEnterprise>lambdaQuery()
                        .eq(CorpRelatedEnterprise::getClientId, client.getId())
                        .eq(NewCorpRelatedEnterprise::getUserId, AccountUtil.getLoginInfo().getId())
                )
                .stream().collect(Collectors.toMap(CorpRelatedEnterprise::getEnterpriseName, e -> e, (t1, t2) -> t2));
        Map<String, MithrasRelatedEnterpriseInfo> tycRelatedEnterpriseInfoMap = tycService.relatedEnterpriseInfo(client.getUscCode())
                .stream().collect(Collectors.toMap(MithrasRelatedEnterpriseInfo::getEnterpriseName, e -> e, (t1, t2) -> t2));
        if (!tycRelatedEnterpriseInfoMap.isEmpty()) {
            for (String tycName : tycRelatedEnterpriseInfoMap.keySet()) {
                if (!currentRelatedEnterpriseMap.containsKey(tycName)) {
                    ClientSyncRSP.RelatedEnterpriseChangedItem item = copyProperties(tycRelatedEnterpriseInfoMap.get(tycName), ClientSyncRSP.RelatedEnterpriseChangedItem.class);
                    item.setChangedType(CHANGE_TYPE_ADD);
                    relatedEnterpriseChangedItems.add(item);
                } else {
                    ClientSyncRSP.RelatedEnterpriseChangedItem fillItem = new ClientSyncRSP.RelatedEnterpriseChangedItem();
                    List<String> changedFields = fillChangedValue(currentRelatedEnterpriseMap.get(tycName), tycRelatedEnterpriseInfoMap.get(tycName), fillItem);
                    if (isNotEmpty(changedFields)) {
                        BeanUtil.copyProperties(tycRelatedEnterpriseInfoMap.get(tycName), fillItem);
                        fillItem.setId(currentRelatedEnterpriseMap.get(tycName).getId());
                        fillItem.setChangedType(CHANGE_TYPE_MODIFY);
                        fillItem.setChangeFields(changedFields);
                        relatedEnterpriseChangedItems.add(fillItem);
                    }
                }
            }

            for (String currentName : currentRelatedEnterpriseMap.keySet()) {
                if (!tycRelatedEnterpriseInfoMap.containsKey(currentName)) {
                    ClientSyncRSP.RelatedEnterpriseChangedItem item = new ClientSyncRSP.RelatedEnterpriseChangedItem();
                    item.setChangedType(CHANGE_TYPE_DELETE);
                    BeanUtil.copyProperties(currentRelatedEnterpriseMap.get(currentName), item);
                    relatedEnterpriseChangedItems.add(item);
                }
            }
        }
        rsp.setRelatedEnterpriseChangedItemList(PageR.of(relatedEnterpriseChangedItems, relatedEnterpriseChangedItems.size()));// 调用天眼查服务同步客户的名称信息
        return rsp;

    }

    /**
     * @param currentObj
     * @param tycObj
     * @param fillObj
     * @return 返回发生了变化的字段的名称
     */
    private List<String> fillChangedValue(Object currentObj, Object tycObj, Object fillObj) {
        List<String> fields = new ArrayList<>();
        try {
            Field[] tycObjFields = tycObj.getClass().getDeclaredFields();
            for (Field tycObjField : tycObjFields) {
                tycObjField.setAccessible(true);
                Field currentField;
                try {
                    currentField = currentObj.getClass().getDeclaredField(tycObjField.getName());
                    currentField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    continue;
                }
                Object tycValue = tycObjField.get(tycObj);
                Object currentValue = currentField.get(currentObj);
                if (isNotNull(tycValue)) {
                    if (null != tycValue && isBlank(tycValue.toString())) {
                        tycValue = null;
                    }
                    if (null != currentValue && isBlank(currentValue.toString())) {
                        currentValue = null;
                    }

                    if (ObjectUtil.notEqual(tycValue, currentValue)) {
                        Field fillField;
                        try {
                            fillField = fillObj.getClass().getDeclaredField(tycObjField.getName());
                        } catch (NoSuchFieldException e) {
                            continue;
                        }
                        fillField.setAccessible(true);
                        fillField.set(fillObj, tycValue);
                        fields.add(fillField.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("fill value failed.", e);
        }
        return fields;
    }

    /**
     * 生效 或 提交审批
     *
     * @param clientId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void effect(Long clientId) {
        // 加锁
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.CLIENT.name(), clientId);
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            Client originalInfo = clientMapper.selectById(clientId);
            if (isNull(originalInfo)) {
                throw new MithrasException(RECORD_NOT_EXIST);
            }
            if (clientTransferService.inTransfer(originalInfo.getId())) {
                err(ERR_IN_TRANSFER);
            }
            // 确保安全，先抄表
            clientAuthorityUtil.copyFromNewToOld(ClientCopyInfoBO.builder().clientId(clientId).currentUserId(AccountUtil.getLoginInfo().getId()).build());
            // 是否可提交 简单校验
            if (Objects.nonNull(findRelatedProcess(clientId))) {
                throw new MithrasException("该客户数据变动处于流程中，无法提交数据");
            }
            // 数据变动 全量数据校验 判断数据是否变动 和 最新版本数据对比 如果不存在版本则放行
            ChangeDTO changeDTO = clientVersionService.checkActualChange(clientId);
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }
            SpringContextHolder.getBean(ClientService.class).checkClientOccupy(clientId);
            // 校验数据
            clientVersionService.validateData(clientId);
            checkEffect(originalInfo);
//            //新建客户第一次生效，通知信息岗做相关操作.自然人不创建
//            if (clientAuthorityUtil.isNewClient(originalInfo) && ClientType.CORPORATION.name().equals(originalInfo.getClientType())) {
//                //去集团创建客户
//                DataShareRegisterCustomREQ dataShareRegisterCustomREQ = null;
//                try {
//                    dataShareRegisterCustomREQ = buildRegisterCustom(clientId);
//                    originalInfo.setClientCode(dataShareService.registerCustom(dataShareRegisterCustomREQ));
//                    clientMapper.updateById(originalInfo);
//                } catch (Exception e) {
//                    log.error("集团创建客户失败， {}", dataShareRegisterCustomREQ, e);
//                    sendClientNewMessage(originalInfo);
//                }
//            }
            if (clientAuthorityUtil.isIntraGroupCollaboration(clientId)) {
                // 公海客户需要踢掉归属主办和归属部门并把管护权拿掉
                Client updateClient = new Client();
                updateClient.setId(clientId);
                updateClient.setBelongDeptId(null);
                updateClient.setBelongSponsorId(null);
                clientMapper.updateAnnotationIncludeNullById(updateClient);
                ClientAuthority clientAuthority = SpringUtil.getBean(ClientAuthorityService.class).getSpecificClientManagerAuthority(clientId);
                if (Objects.nonNull(clientAuthority)) {
                    SpringUtil.getBean(ClientAuthorityService.class).removeById(clientAuthority.getId());
                }
            }
            // 判断是否立项 判断是否发生需要审批的变动
            if (projReviewService.clientRelatedProjReview(clientId) && Boolean.TRUE.equals(changeDTO.getNeedApprovalChangeFlag())) {
                startChangeFlow(clientId, originalInfo);
            } else {
                // 未立项
                int infoChange = clientChangeCheckService.checkCommerceInfoChange(clientId);
                log.info("checkCommerceInfoChange 客户：{}结果:{}", clientId, infoChange);
                boolean changeFlag = Objects.equals(1, infoChange);
                log.info("客户：{}是否需要审批结果：{}", clientId, changeFlag);
                if (changeFlag) {
                    startChangeFlow(clientId, originalInfo);
                } else {
                    // 未立项 直接生效 把数据拷到lib表 状态：新建点击确认-无需审批，修改后点击确认-"-"
                    recordClientStatus(clientId, ClientStatus.TAKE_EFFECT, ClientProcessStatus.EFFECT_BLANK);
//                copyNewClientInfoToPrev(clientId);
                    Map<String, Object> extraMap = new HashMap<>();
                    extraMap.put("userId", AccountUtil.getLoginInfo().getId());
                    clientVersionService.recordVersion(clientId, VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL, extraMap);

                }
            }
            //客户管理-联系人信息-邮箱修改-发送通知
            Response<SystemConfigDO> rsp = SpringContextHolder.getBean(SystemConfigService.class).getConfig("emailChangeNotification");
            SystemConfigDO data = rsp.getData();
            List<String> list = Arrays.asList(data.getConfigValue().split(","));
            List<Long> userIds = list.stream().map(Long::valueOf).collect(Collectors.toList());
            //校验联系人信息-邮箱地址是否变动
            ChangeDTO emailUpdate = this.checkEmailUpdate(clientId, changeDTO.getVersion());
            if (Boolean.TRUE.equals(emailUpdate.getChangeFlag())) {
                // 发送通知
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setTo(userIds);
                messageAddREQ.setFlowid(String.valueOf(clientId));
                messageAddREQ.setContent("客户管理-联系人信息-邮箱修改");
                messageAddREQ.setNeedOa(false);
                String clientName = id2NameService.clientId2NameSingle(clientId);
                messageAddREQ.setRelation(String.format("%s联系人信息", clientName));
                messageAddREQ.setNoticeSource(NoticeSourceENUM.CLIENT_MANAGEMENT_EMAIL_CHANGE.name());
                messageAddREQ.setMessageType(MessageTypeEnum.CLIENT_MANAGEMENT_EMAIL_CHANGE_NOTICE.name());
                messageAddREQ.setPcurl(String.format("/customer/maintain/detail/%s", clientId));
                messageAddREQ.setBusinessId(String.valueOf(clientId));
                messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
            }
        } finally {
            redisDistLock.unlock(lockKey);
        }
    }


    private void startChangeFlow(Long clientId, Client originalInfo) {
        // 立项 提交审批
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(FlowConstants.CLIENT_MODIFY_FLOW);
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(clientId));
        startProcessReq.setProcessInstanceName(originalInfo.getClientName());
        startProcessReq.setSubModule(originalInfo.getClientType());
        startProcessReq.setStartUserDeptId(Optional.ofNullable(originalInfo.getCreateByDept()).map(String::valueOf).orElse(null));
        //补充风控经理信息
        Map<String, Object> varMap = new HashMap<>();
        //风控经理 先按部门查询，部门没有查所有
        List<String> riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getRiskManagerIdsOrderByDeptId().get(originalInfo.getBelongDeptId());
        if (CollectionUtil.isEmpty(riskControlManagerIds)) {
            riskControlManagerIds = sysUserService.getAllRiskControlManagerIds().stream().map(String::valueOf).collect(Collectors.toList());
        }
        varMap.put("riskControlManager", riskControlManagerIds);
        startProcessReq.setVariables(varMap);
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, clientId);
        recordClientStatus(clientId, null, ClientProcessStatus.UNDER_APPROVAL);
    }


    private void checkEffect(Client originalInfo) {
        //法人检查
        if(ClientType.CORPORATION.name().equals(originalInfo.getClientType())) {
            if(newCorpShareholderInfoMapper.selectCount(Wrappers.<NewCorpShareholderInfo>lambdaQuery()
            .eq(NewCorpShareholderInfo::getClientId, originalInfo.getId())) <= 0){
                throw new MithrasException("法人客户股东信息不能为空");
            }
        }
    }

    /**
     * 判断某个客户信息是否可保存
     * 不在流程中
     * 可以保存
     * 在流程中
     * 在发起人节点
     * 是发起人
     * 可以保存
     * 不是发起人
     * 不能保存
     * 不在发起人节点
     * 不能保存
     *
     * @param clientId
     * @return
     */
    public boolean canSave(Long clientId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (clientId == null) {
            return false;
        }
        if (Objects.isNull(loginUser)) {
            return false;
        }
        ProcessResp processResp = findRelatedProcess(clientId);
        if (processResp == null) {
            // 运行中流程为空 可以保存
            return true;
        }
        if (!FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds())) {
            // 有运行中流程 不在发起人节点 不能保存
            return false;
        }
        if (!Objects.equals(String.valueOf(loginUser.getId()), processResp.getStartUserId())) {
            // 在发起人节点 不是发起人 不能保存
            return false;
        }
        return true;
    }

    /**
     * 是否可处理生效逻辑
     * 在流程中
     * 不可处理
     * 不在流程中
     * 数据未变动
     * 不可处理
     * 数据有变动
     * 可处理
     *
     * @param clientId
     * @return
     */
    public boolean canEffect(Long clientId) {
        if (clientId == null) {
            return false;
        }
        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            return false;
        }
        // 不存在进行中流程 且 数据状态为生效（新建、待生效）时 才可操作
        return Objects.isNull(findRelatedProcess(clientId))
                && !ClientStatus.TAKE_EFFECT.name().equals(client.getClientStatus());
    }

    /**
     * 寻找客户关联的流程
     *
     * @param clientId
     * @return
     */
    public ProcessResp findRelatedProcess(Long clientId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(clientId));
        processPageReq.setModelKey(FlowConstants.CLIENT_MODIFY_FLOW);
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    /**
     * 流程结束 处理流程状态 并把数据抄到lib表
     *
     * @param clientId
     * @param endType
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long clientId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        recordClientStatus(clientId, ClientStatus.TAKE_EFFECT, processPass ? ClientProcessStatus.APPROVAL_PASS : ClientProcessStatus.APPROVAL_REJECT, startUserId);
        // 确保安全，先抄表
        clientAuthorityUtil.copyFromNewToOld(ClientCopyInfoBO.builder().clientId(clientId).currentUserId(startUserId).build());
        // 通不通过都生成版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("userId", startUserId);
        clientVersionService.recordVersion(clientId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType, extraMap);
        if (!processPass) {
            // 审批拒绝 回滚数据
            clientVersionService.reset(clientId);
        }
        // 本次事件提交后，异步发送指标计算事件
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
                metricComputeEventBus.post(metricComputeEvent);
            }
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordClientStatus(Long clientId,
                                   ClientStatus clientStatus, ClientProcessStatus clientProcessStatus) {
        recordClientStatus(clientId, clientStatus, clientProcessStatus, null);
    }

    /**
     * 更新客户状态
     *
     * @param clientStatus
     * @param clientProcessStatus
     */
    @Transactional(rollbackFor = Throwable.class)
    public void recordClientStatus(Long clientId,
                                   ClientStatus clientStatus, ClientProcessStatus clientProcessStatus, Long userId) {
        if (clientId == null || (clientStatus == null && clientProcessStatus == null)) {
            return;
        }
        LambdaUpdateWrapper<Client> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Client::getId, clientId);
        if (clientStatus != null) {
            updateWrapper.set(Client::getClientStatus, clientStatus.name());
        }
        if (clientProcessStatus != null) {
            updateWrapper.set(Client::getProcessStatus, clientProcessStatus.name());
        }
        if (userId != null) {
            updateWrapper.set(Client::getUpdateBy, userId);
        }
        updateWrapper.set(Client::getUpdateTime, LocalDateTime.now());
        clientMapper.update(null, updateWrapper);
    }

    /**
     * 客户按钮状态（是否可保存、是否可提交审批、显示确认还是提交审批）
     *
     * @param id
     * @return
     */
    public ClientButtonStatusRSP queryClientButtonStatus(Long id) {
        Client originalInfo = clientMapper.selectById(id);
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ClientButtonStatusRSP rsp = new ClientButtonStatusRSP();
        // 是否可保存 统一显示正常按钮 操作后报错
        //rsp.setCanSaveFlag(canSave(id) ? 1 : 0);
        rsp.setCanSaveFlag(1);
        // 生效按钮样式 有立项 则为提交审批
        rsp.setEffectButtonStyle(projReviewService.clientRelatedProjReview(id) ? 2 : 1);
        // 是否可提交审批 或 生效 统一显示正常按钮 操作后报错
        // rsp.setCanEffectFlag(canEffect(id) ? 1 : 0);
        rsp.setCanEffectFlag(1);
        return rsp;
    }

    public UserButtonStatusRSP queryUserButtonStatus(Long id) {
        UserButtonStatusRSP rsp = new UserButtonStatusRSP();
        //当前用户是否可以移交客户
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(currentUserId);
        rsp.setCanTransferClient(jobList.stream()
                .anyMatch(e -> projmanager.name().equals(e) || JobEnum.businesshead.name().equals(e) || JobEnum.leaderincharge.name().equals(e)));
        return rsp;

    }


    public String getBatchNumber() {
        return LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
    }


    public List<Client> getNormalByCertList(Collection<String> certList) {
        if (CollUtil.isEmpty(certList)) {
            return new ArrayList<>();
        }
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getClientType, ClientType.NORMAL.name()).in(Client::getCertNumber, certList));

    }

    public Client getById(Long clientId) {
        return clientMapper.selectById(clientId);
    }

    public List<Client> get(Client condition) {
        return clientMapper.selectList(Wrappers.lambdaQuery(condition));
    }

    public List<Client> bySponsorIdAndDeptId(Long belongSponsorId, Long belongDeptId) {
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getBelongSponsorId, belongSponsorId)
                .eq(isNotNull(belongDeptId), Client::getBelongDeptId, belongDeptId)
        );
    }

    /**
     * 查询系统内的金控关联方客户
     *
     * @param uscds
     * @return
     */
    public List<Client> listRelatedClient(Set<String> uscds) {
        if (CollUtil.isEmpty(uscds)) {
            return new ArrayList<>();
        }
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name())
                .in(Client::getUscCode, uscds));
    }

    /*public void updateClientAuth(Long clientId) {
        Client client = baseMapper.selectById(clientId);
        if (ObjectUtil.isEmpty(client)) {
            return;
        }
        ClientAuthBO clientAuthByProj = getClientAuthByProj(clientId, null);
        client.setBelongSponsorId(clientAuthByProj.getProjSponsorUserId());
        client.setBelongDeptId(clientAuthByProj.getBizDeptId());
        if (ClientAuthEnum.OTHER_EXCLUSIVE.equals(clientAuthByProj.getClientAuthEnum())) {
            clientAuthByProj.setClientAuthEnum(ClientAuthEnum.EXCLUSIVE);
        }
        client.setAuthType(clientAuthByProj.getClientAuthEnum().name());
        clientMapper.updateAnnotationIncludeNullById(client);
    }*/

    /*public void updateClientAuth(Set<Long> clientIds, String authType) {
        if(CollUtil.isEmpty(clientIds)){
            return;
        }
        LambdaUpdateWrapper<Client> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Client::getAuthType, authType);
        updateWrapper.set(Client::getBelongSponsorId, null);
        updateWrapper.set(Client::getBelongDeptId, null);
        updateWrapper.in(Client::getId,clientIds);
        baseMapper.update(null, updateWrapper);
    }*/


    /**
     * 天眼查查询客户姓名，法人，股东等信息
     **/
    @Transactional(rollbackFor = Throwable.class)
    public Map<Long, ClientBusinessHistoryBO> compareBusiness(List<Long> clientIds) {
        List<Client> clients = baseMapper.selectBatchIds(clientIds);
        Map<Long, ClientBusinessHistoryBO> map = new HashMap<>();
        clients.stream().filter(client -> ClientType.CORPORATION.name().equals(client.getClientType())).forEach(client -> {
            MithrasBaseInfo mithrasBaseInfo = null;
            if (StrUtil.isNotEmpty(client.getUscCode()) && !"null".equals(client.getUscCode())) {
                mithrasBaseInfo = tycService.baseInfo(client.getUscCode());
            }
            //基本信息都没有，也就不需要查股东信息
            if (mithrasBaseInfo != null) {
                ClientBusinessHistoryBO rsp = new ClientBusinessHistoryBO();
                rsp.setClientId(client.getId());
                rsp.setTycName(mithrasBaseInfo.getTycName());
                rsp.setTycCorpRepresent(mithrasBaseInfo.getCorpRepresent());
                List<MithrasShareholderInfo> shareholderInfos = tycService.shareholderInfo(client.getUscCode());
                rsp.setTycShareHolderInfo(BeanUtil.copyToList(shareholderInfos, cn.zswltech.mithras.client.service.model.MithrasShareholderInfo.class));
                map.put(client.getId(), rsp);
            }
        });
        Collection<ClientBusinessHistoryBO> values = map.values();
        if (CollUtil.isNotEmpty(values)) {
            List<ClientBusinessHistory> histories = values.stream().map(ClientBusinessHistoryBO::getBo).collect(Collectors.toList());
            clientBusinessHistoryService.saveBatch(histories);

        }
        return map;
    }

    /**
     * 判断某个用户是否有该客户的权限,查询项目计算
     *
     * @param clientId 客户id
     * @param userId   用户ID，为空时取当前登录人
     * @return
     **/
    public ClientAuthBO getClientAuthByProj(Long clientId, Long userId) {
        ClientAuthBO clientAuthBO = new ClientAuthBO();
        if (ObjectUtil.isEmpty(userId) && ObjectUtil.isNotEmpty(AccountUtil.getLoginInfo())) {
            userId = AccountUtil.getLoginInfo().getId();
        }
        if (ObjectUtil.isNotEmpty(clientId)) {
            //查询公海客户
            Set<Long> highSeasSet = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .eq(Client::getId, clientId)
                    .eq(Client::getAuthType, ClientAuthEnum.HIGH_SEAS.name())).stream().map(Client::getId).collect(Collectors.toSet());
            // 如果公海，覆盖
            if (ObjectUtil.isNotEmpty(highSeasSet) && highSeasSet.contains(clientId)) {
                clientAuthBO.setClientAuthEnum(ClientAuthEnum.HIGH_SEAS);
                clientAuthBO.setProjEstablishId(null);
                clientAuthBO.setProjSponsorUserId(null);
                clientAuthBO.setBizDeptId(null);
                return clientAuthBO;
            }
            //查询是否有立项生效项目下的主承租人、联合承租人 lesseeInfo、债权人 creditorInfo、债务人 debtorInfo
            ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.listProjByAuthRole(clientId);
            if (ObjectUtil.isEmpty(projEstablishBaseInfo)) {
                clientAuthBO.setClientAuthEnum(ClientAuthEnum.NO_AFFILIATION);
            } else {
                clientAuthBO.setProjEstablishId(projEstablishBaseInfo.getId());
                clientAuthBO.setProjSponsorUserId(projEstablishBaseInfo.getProjSponsorUserId());
                clientAuthBO.setBizDeptId(projEstablishBaseInfo.getBizDeptId());
                if (ObjectUtil.equals(userId, projEstablishBaseInfo.getProjSponsorUserId())) {
                    clientAuthBO.setClientAuthEnum(ClientAuthEnum.EXCLUSIVE);
                } else {
                    clientAuthBO.setClientAuthEnum(ClientAuthEnum.OTHER_EXCLUSIVE);
                }
            }
        } else {
            clientAuthBO.setClientAuthEnum(ClientAuthEnum.OTHER_EXCLUSIVE);
        }
        //默认已经有专属
        return clientAuthBO;
    }

    public List<ProjEstablishBaseInfo> getClientAuthByProj(Long clientId) {
        //查询是否有立项生效项目下的主承租人、联合承租人 lesseeInfo、债权人 creditorInfo、债务人 debtorInfo
        return projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getClientId, clientId));
    }

    //客户是否有权限查询用户信息
    public boolean checkClientAuth(Long clientId, String sourceScene) {
        if (AuthCheckSourceSceneEnum.AFTER_LEASE_CHECK_REPORT.name().equals(sourceScene)) {
            // 租后检查模块的请求暂不做权限校验
            return true;
        }
        ClientApplyOccupyREQ req = new ClientApplyOccupyREQ();
        req.setClientId(clientId);
        ClientApplyOccupyRSP rsp = SpringUtil.getBean(ClientAuthorityService.class).checkOccupy(req);
        if (Objects.isNull(rsp) && StrUtil.isNotBlank(rsp.getMessage())) {
            log.warn("无权限查看[{}]", rsp.getMessage());
            return false;
        }
        return true;
    }

    //客户是否有权限查询用户信息
    /*public boolean checkClientUpdateAuth(Long clientId){
        AccountVO loginUser = AccountUtil.getLoginInfo();
        Client client = baseMapper.selectById(clientId);
        if(ObjectUtil.isEmpty(client)){
           throw new MithrasException(USER_NOT_LOGIN);
        }

        if(Objects.equals(client.getAuthType(), ClientAuthEnum.HIGH_SEAS.name()) || Objects.equals(client.getAuthType(),
                ClientAuthEnum.NO_AFFILIATION.name()) || Objects.equals(client.getBelongSponsorId(), loginUser.getId())){
            return true;
        }
        throw new MithrasException(String.format("该客户已被%s占用", id2NameService.deptId2NameSingle(client.getBelongDeptId())));

    }*/
    //客户是否可以被选做承租人、联合承租人、债权人、债务人，无权限阻断
    public void checkClientOccupy(Long clientId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        checkClientOccupy(clientId, loginUser.getId());
    }

    public void checkClientOccupy(Long clientId, Long userId) {
        if (Objects.isNull(userId)) {
            throw new MithrasException(USER_NOT_LOGIN);
        }
        Client client = baseMapper.selectById(clientId);
        if (ObjectUtil.isEmpty(client)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, clientId)
                .eq(ClientAuthority::getDeleted, 0));
        if (clientAuthorityList != null && !clientAuthorityList.isEmpty()) {
            for (ClientAuthority clientAuthority : clientAuthorityList) {
                if (ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()
                        && !clientAuthority.getUserId().equals(userId)) {
                    throw new MithrasException(String.format("该客户%s已被%s-%s占用", client.getClientName(),
                            id2NameService.deptId2NameSingle(client.getBelongDeptId()), id2NameService.sysUserId2NameSingle(client.getBelongSponsorId())));
                }
            }
        }
        /*if (Objects.equals(client.getAuthType(), ClientAuthEnum.HIGH_SEAS.name()) || Objects.equals(client.getAuthType(),
                ClientAuthEnum.NO_AFFILIATION.name()) || Objects.equals(client.getBelongSponsorId(), userId) || ObjectUtil.isEmpty(client.getAuthType())) {
            return;
        }*/
        return;
    }


    public Set<Long> getSpecifyProvinceClientIds(List<String> provinceCodes) {
        return list(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name())
                .in(Client::getProvinceOfAffiliation, provinceCodes))
                .stream().map(Client::getId).collect(Collectors.toSet());
    }

    public Set<Long> getNotInSpecifyProvinceClientIds(List<String> provinceCodes) {
        return list(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name())
                .notIn(Client::getProvinceOfAffiliation, provinceCodes))
                .stream().map(Client::getId).collect(Collectors.toSet());

    }

    //检查联系人信息-邮箱是否已更新
    private ChangeDTO checkEmailUpdate(Long clientId, String version) {
        List<CorpContactInfo> contactInfos = contactInfoMapper.selectList(new QueryWrapper<CorpContactInfo>()
                .eq("client_id", clientId));
        LambdaQueryWrapper<CorpContactInfoLib> query = Wrappers.lambdaQuery();
        query.eq(CorpContactInfoLib::getClientId, clientId);
        query.eq(CorpContactInfoLib::getVersion, version);
        query.last(StringUtil.mysqlLimitOne());
        List<CorpContactInfoLib> versionList = contactInfoLibMapper.selectList(query);
        // 检查联系人信息-邮箱是否已更新
        ChangeDTO changeDTO = new ChangeDTO();
        if (ObjectUtil.isNotEmpty(contactInfos) && ObjectUtil.isNotEmpty(versionList)) {
            for (CorpContactInfo contactInfo : contactInfos) {
                for (CorpContactInfoLib contactInfoLib : versionList) {
                    if (Objects.equals(contactInfoLib.getMail(), contactInfo.getMail())) {
                        changeDTO.setChangeFlag(false);
                    } else {
                        changeDTO.setChangeFlag(true);
                    }
                }
            }
        }
        return changeDTO;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void release(ClientReleaseREQ req) {
        Client client = clientMapper.selectById(req.getClientId());
        if (isNull(client)) {
            throw new MithrasException("所选客户为空");
        }
        if (!ClientStatus.TAKE_EFFECT.name().equalsIgnoreCase(client.getClientStatus())) {
            throw new MithrasException("所选客户不为生效状态");
        }
        Long startUserId = Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, client.getId())
                .eq(ClientAuthority::getDeleted, 0));
        if (clientAuthorityList != null && !clientAuthorityList.isEmpty()) {
            for (ClientAuthority clientAuthority : clientAuthorityList) {
                if (ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()
                        && !clientAuthority.getUserId().equals(startUserId)) {
                    throw new MithrasException(String.format("该客户%s已被%s-%s占用，请选%s释放", client.getClientName(),
                            id2NameService.deptId2NameSingle(client.getBelongDeptId()),
                            id2NameService.sysUserId2NameSingle(client.getBelongSponsorId()), id2NameService.sysUserId2NameSingle(client.getBelongSponsorId())));
                }
            }
        }

        ClientAuthority clientAuthority = clientAuthorityMapper.selectOne(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getClientId, client.getId())
                .eq(ClientAuthority::getUserId, startUserId)
                .eq(ClientAuthority::getDeleted, 0));
        if (clientAuthority != null && ClientLevelEnum.MANAGE.getLevel() == clientAuthority.getLevel()) {
            clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
            clientAuthorityMapper.updateById(clientAuthority);
        }

        client.setClientStatus(ClientStatus.NEW.name());
        client.setIsReleased(YesOrNoNumberEnum.YES.getCode());
        client.setBelongDeptId(null);
        client.setBelongSponsorId(null);
        clientMapper.updateAnnotationIncludeNullById(client);
    }

    public List<ClientAppProjQueryRSP> queryProjEstablishDetail(Long clientId) {
        List<ClientAppProjQueryRSP> res = new ArrayList<>();
//        List<ProjEstablishBaseInfo> projEstablishBaseInfoList;
//        // 查询处于交易结构中的立项数据
//        Set<Long> projEstablishIds = SpringUtil.getBean(ProjEstablishTradeStructureService.class).listProjEstablishIdsByClientId(clientId);
//        if (CollectionUtil.isEmpty(projEstablishIds)) {
//            projEstablishBaseInfoList = Collections.emptyList();
//        } else {
//            //项目立项
//            LambdaQueryWrapper<ProjEstablishBaseInfo> projEstablishQuery = Wrappers.lambdaQuery();
////            projEstablishQuery.eq(ProjEstablishBaseInfo::getClientId, clientId);
//            projEstablishQuery.in(ProjEstablishBaseInfo::getId, projEstablishIds);
//            projEstablishQuery.ne(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.CLOSED.name());
//            projEstablishBaseInfoList = projEstablishBaseInfoService.list(projEstablishQuery);
//        }
//
//        if (CollectionUtil.isNotEmpty(projEstablishBaseInfoList)) {
//            // 过滤出新建的项目立项
//            List<ProjEstablishBaseInfo> newList = projEstablishBaseInfoList.stream().filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.NEW.name())).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(newList)) {
//                for (ProjEstablishBaseInfo projEstablishBaseInfo : newList) {
//                    ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
//                    appProjQueryRSP.setProjCode(projEstablishBaseInfo.getProjCode());
//                    appProjQueryRSP.setProjName(projEstablishBaseInfo.getProjName());
//                    res.add(appProjQueryRSP);
//                }
//            }
//            //过滤出生效的项目立项
//            List<ProjEstablishBaseInfo> effectEstablishList = projEstablishBaseInfoList.stream().filter(e -> Objects.equals(e.getProjEstablishStatus(), RecordStatus.TAKE_EFFECT.name())).collect(Collectors.toList());
//            for (ProjEstablishBaseInfo projEstablishBaseInfo : effectEstablishList) {
//                List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.listByProjEstablishIds(Collections.singletonList(projEstablishBaseInfo.getId()));
//                if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
//                    // 有立项还无评审
//                    ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
//                    appProjQueryRSP.setProjCode(projEstablishBaseInfo.getProjCode());
//                    appProjQueryRSP.setProjName(projEstablishBaseInfo.getProjName());
//                    res.add(appProjQueryRSP);
//                } else {
//                    ProjReviewBaseInfo review = projReviewBaseInfoList.get(0);
//                    if (RecordStatus.NEW.name().equalsIgnoreCase(review.getProjReviewStatus())) {
//                        ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
//                        appProjQueryRSP.setProjCode(projEstablishBaseInfo.getProjCode());
//                        appProjQueryRSP.setProjName(projEstablishBaseInfo.getProjName());
//                        res.add(appProjQueryRSP);
//                    }
//                }
//            }
//        }
        // 单体项目立项和评审从交易结构辅助表取数据
        List<ProjEstablishBaseInfo> establishBaseInfoList;
        Set<Long> projEstablishIds = SpringUtil.getBean(ProjEstablishTradeStructureService.class).listProjEstablishIdsByClientId(clientId);
        if (CollectionUtil.isNotEmpty(projEstablishIds)) {
            establishBaseInfoList = SpringUtil.getBean(ProjEstablishBaseInfoService.class).listByIds(projEstablishIds);
            // 只取主办或者协办是自己的
            establishBaseInfoList.removeIf(e -> !owner(e.getProjSponsorUserId(), e.getProjCosponsorUserIds()));
        } else {
            establishBaseInfoList = Collections.emptyList();
        }
        List<ProjReviewBaseInfo> reviewBaseInfoList;
        Set<Long> projReviewIds = SpringUtil.getBean(ProjReviewTradeStructureService.class).listProjReviewIdsByClientId(clientId);
        if (CollectionUtil.isNotEmpty(projReviewIds)) {
            reviewBaseInfoList = projReviewBaseInfoService.listByIds(projReviewIds);
            // 只取主办或者协办是自己的
            reviewBaseInfoList.removeIf(e -> !owner(e.getProjSponsorUserId(), e.getProjCosponsorUserIds()));
        } else {
            reviewBaseInfoList = Collections.emptyList();
        }
        // 按照项目编号合并（立项和评审正常情况下项目编号是一致的）
        Map<String, String> map = new HashMap<>();
        for (ProjReviewBaseInfo projReviewBaseInfo : reviewBaseInfoList) {
            if (Objects.equals(projReviewBaseInfo.getProjReviewStatus(), RecordStatus.CLOSED.name())) {
                continue;
            }
            map.put(projReviewBaseInfo.getProjCode(), projReviewBaseInfo.getProjName());
        }
        for (ProjEstablishBaseInfo projEstablishBaseInfo : establishBaseInfoList) {
            if (Objects.equals(projEstablishBaseInfo.getProjEstablishStatus(), RecordStatus.CLOSED.name())) {
                continue;
            }
            map.put(projEstablishBaseInfo.getProjCode(), projEstablishBaseInfo.getProjName());
        }
        if (CollectionUtil.isNotEmpty(map)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                res.add(new ClientAppProjQueryRSP(entry.getValue(), entry.getKey()));
            }
        }

        //集团授信评审
        LambdaQueryWrapper<GroupCreditEstablishBaseInfo> groupEstablishQuery = Wrappers.lambdaQuery();
        groupEstablishQuery.eq(GroupCreditEstablishBaseInfo::getClientId, clientId);
        groupEstablishQuery.ne(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, RecordStatus.CLOSED.name());
        List<GroupCreditEstablishBaseInfo> groupCreditEstablishBaseInfoList = groupEstablishBaseInfoService.list(groupEstablishQuery);
        // 只取主办或者协办是自己的
        groupCreditEstablishBaseInfoList.removeIf(e -> !owner(e.getProjSponsorUserId(), e.getProjCosponsorUserIds()));
        if (CollectionUtil.isNotEmpty(groupCreditEstablishBaseInfoList)) {
            // 过滤出新建的集团立项
            List<GroupCreditEstablishBaseInfo> newList = groupCreditEstablishBaseInfoList.stream().filter(e -> Objects.equals(e.getGroupCreditEstablishStatus(), RecordStatus.NEW.name())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                for (GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo : newList) {
                    ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
                    appProjQueryRSP.setProjCode(groupCreditEstablishBaseInfo.getProjCode());
                    appProjQueryRSP.setProjName(groupCreditEstablishBaseInfo.getProjName());
                    res.add(appProjQueryRSP);
                }
            }
            //过滤出生效的集团立项
            List<GroupCreditEstablishBaseInfo> effectGroupEstablishBaseInfoList = groupCreditEstablishBaseInfoList.stream().filter(e -> Objects.equals(e.getGroupCreditEstablishStatus(), RecordStatus.TAKE_EFFECT.name())).collect(Collectors.toList());
            for (GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo : effectGroupEstablishBaseInfoList) {
                //集团授信评审
                LambdaQueryWrapper<GroupCreditReviewBaseInfo> groupReviewQuery = Wrappers.lambdaQuery();
                groupReviewQuery.eq(GroupCreditReviewBaseInfo::getGroupCreditEstablishId, groupCreditEstablishBaseInfo.getId());
                groupReviewQuery.ne(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, RecordStatus.CLOSED.name());
                List<GroupCreditReviewBaseInfo> groupCreditReviewBaseInfoList = groupReviewBaseInfoService.list(groupReviewQuery);
                if (CollectionUtil.isEmpty(groupCreditReviewBaseInfoList)) {
                    ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
                    appProjQueryRSP.setProjCode(groupCreditEstablishBaseInfo.getProjCode());
                    appProjQueryRSP.setProjName(groupCreditEstablishBaseInfo.getProjName());
                    res.add(appProjQueryRSP);
                } else {
                    GroupCreditReviewBaseInfo groupCreditReview = groupCreditReviewBaseInfoList.get(0);
                    if (RecordStatus.NEW.name().equalsIgnoreCase(groupCreditReview.getGroupCreditReviewStatus())) {
                        ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
                        appProjQueryRSP.setProjCode(groupCreditReview.getProjCode());
                        appProjQueryRSP.setProjName(groupCreditReview.getProjName());
                        res.add(appProjQueryRSP);
                    } else if (RecordStatus.TAKE_EFFECT.name().equalsIgnoreCase(groupCreditReview.getGroupCreditReviewStatus())) {
                        LambdaQueryWrapper<ProjReviewBaseInfo> projReviewBaseQuery = Wrappers.lambdaQuery();
                        projReviewBaseQuery.eq(ProjReviewBaseInfo::getGroupCreditReviewId, groupCreditReview.getId());
                        projReviewBaseQuery.ne(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name());
                        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.list(projReviewBaseQuery);
                        if (CollectionUtil.isEmpty(projReviewBaseInfoList)) {
                            ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
                            appProjQueryRSP.setProjCode(groupCreditReview.getProjCode());
                            appProjQueryRSP.setProjName(groupCreditReview.getProjName());
                            res.add(appProjQueryRSP);
                        } else {
                            ProjReviewBaseInfo review = projReviewBaseInfoList.get(0);
                            if (RecordStatus.NEW.name().equalsIgnoreCase(review.getProjReviewStatus())) {
                                ClientAppProjQueryRSP appProjQueryRSP = new ClientAppProjQueryRSP();
                                appProjQueryRSP.setProjCode(review.getProjCode());
                                appProjQueryRSP.setProjName(review.getProjName());
                                res.add(appProjQueryRSP);
                            }
                        }
                    }
                }
            }
        }
        return res;
    }


    public List<ClientAppPlanQueryRSP> queryCheckPlanDetail(Long clientId) {
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = newAfterLeaseCheckPlanClientMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .eq(NewAfterLeaseCheckPlanClient::getClientId, clientId)
                .orderByDesc(NewAfterLeaseCheckPlanClient::getPlanId));
        if (CollectionUtil.isEmpty(checkPlanClientList)) {
            return new ArrayList<>();
        }
        List<Long> planIds = checkPlanClientList.stream().map(NewAfterLeaseCheckPlanClient::getPlanId).collect(Collectors.toList());
        List<NewAfterLeaseCheckPlanBase> planBaseList = newAfterLeaseCheckPlanBaseMapper.selectList(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                .in(NewAfterLeaseCheckPlanBase::getId, planIds)
                .in(NewAfterLeaseCheckPlanBase::getPlanStatus, ListUtil.toList(AfterLeaseCheckPlanStatusEnum.NEW, AfterLeaseCheckPlanStatusEnum.CHECKING)));
        if (CollectionUtil.isEmpty(planBaseList)) {
            return new ArrayList<>();
        }
        List<ClientAppPlanQueryRSP> res = new ArrayList<>();
        for (NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase : planBaseList) {
            ClientAppPlanQueryRSP clientAppPlanQueryRSP = new ClientAppPlanQueryRSP();
            clientAppPlanQueryRSP.setCheckPlanId(newAfterLeaseCheckPlanBase.getId());
            clientAppPlanQueryRSP.setCheckPlanName(newAfterLeaseCheckPlanBase.getPlanName());
            res.add(clientAppPlanQueryRSP);
        }
        return res;
    }






    public Page<Client> groupList(ClientListREQ req) {
        LocalDateTime from = isNull(req.getCreateDateFrom()) ? null : req.getCreateDateFrom().atStartOfDay();
        LocalDateTime to = isNull(req.getCreateDateTo()) ? null : req.getCreateDateTo().plusDays(1).atStartOfDay();
        LocalDateTime updateFrom = isNull(req.getUpdateDateFrom()) ? null : req.getUpdateDateFrom().atStartOfDay();
        LocalDateTime updateTo = isNull(req.getUpdateDateTo()) ? null : req.getUpdateDateTo().plusDays(1).atStartOfDay();
        ClientListParam listParam = new ClientListParam();
        listParam.setClientCode(req.getClientCode())
                .setClientName(req.getClientName())
                .setClientType(req.getClientType())
                .setIndustryType(req.getIndustryType())
                .setCreateFrom(from)
                .setCreateTo(to)
                .setUpdateFrom(updateFrom)
                .setUpdateTo(updateTo)
                .setClientStatus(req.getClientStatus())
                .setProcessStatus(req.getProcessStatus())
                .setIsGroup(req.getIsGroup())
                .setEffected(req.getEffected());
        return clientMapper.myList(new Page<>(req.getPage(), req.getPageSize()), listParam);
    }

    private boolean owner(Long sponsorUserId, String cosponsorUserIds) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (StrUtil.isNotBlank(cosponsorUserIds)) {
            List<Long> ids = JSONUtil.toList(cosponsorUserIds, Long.class);
            if (CollectionUtil.isNotEmpty(ids) && ids.contains(currentUserId)) {
                return true;
            }
        }
        if (Objects.equals(sponsorUserId, currentUserId)) {
            return true;
        }
        return false;
    }

    private boolean releaseClientDoubleCheck(Client client, ClientAuthority clientAuthority) {
        Set<Long> projEstablishIds = new HashSet<>();
        Set<Long> projReviewIds = new HashSet<>();
        // 合同
        Set<Long> contractIds = SpringUtil.getBean(ContractTradeStructureService.class).listContractIdsByClientId(client.getId());
        List<ContractBaseInfo> contractBaseInfoList;
        if (CollectionUtil.isNotEmpty(contractIds)) {
//            contractBaseInfoList = contractBaseInfoService.listByIds(contractIds);
            // 查询项目主办为管护权人的合同信息
            contractBaseInfoList = contractBaseInfoService.list(
                    Wrappers.<ContractBaseInfo>lambdaQuery()
                            .in(ContractBaseInfo::getId, contractIds)
                            .eq(ContractBaseInfo::getProjSponsorUserId, clientAuthority.getUserId())
            );
            projReviewIds.addAll(contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet()));
        } else {
            contractBaseInfoList = Collections.emptyList();
        }
        // 评审
        List<ProjReviewBaseInfo> projReviewBaseInfoList;
        projReviewIds.addAll(SpringUtil.getBean(ProjReviewTradeStructureService.class).listProjReviewIdsByClientId(client.getId()));
        if (CollectionUtil.isNotEmpty(projReviewIds)) {
//            projReviewBaseInfoList = projReviewBaseInfoService.listByIds(projReviewIds);
            // 查询项目主办为管护权人的评审信息
            projReviewBaseInfoList = projReviewBaseInfoService.list(
                    Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                            .in(ProjReviewBaseInfo::getId, projReviewIds)
                            .eq(ProjReviewBaseInfo::getProjSponsorUserId, clientAuthority.getUserId())
            );
            projEstablishIds.addAll(projReviewBaseInfoList.stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet()));
        } else {
            projReviewBaseInfoList = Collections.emptyList();
        }
        // 立项
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList;
        projEstablishIds.addAll(SpringUtil.getBean(ProjEstablishTradeStructureService.class).listProjEstablishIdsByClientId(client.getId()));
        if (CollectionUtil.isNotEmpty(projEstablishIds)) {
//            projEstablishBaseInfoList = projEstablishBaseInfoService.listByIds(projEstablishIds);
            // 查询项目主办为管护权人的立项信息
            projEstablishBaseInfoList = projEstablishBaseInfoService.list(
                    Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                            .in(ProjEstablishBaseInfo::getId, projEstablishIds)
                            .eq(ProjEstablishBaseInfo::getProjSponsorUserId, clientAuthority.getUserId())
            );
        } else {
            projEstablishBaseInfoList = Collections.emptyList();
        }
        // 重新过一遍释放逻辑
        ClientAsLesseeInfoDTO clientAsLesseeInfoDTO = new ClientAsLesseeInfoDTO();
        clientAsLesseeInfoDTO.setProjEstablishBaseInfoList(projEstablishBaseInfoList);
        clientAsLesseeInfoDTO.setProjReviewBaseInfoList(projReviewBaseInfoList);
        clientAsLesseeInfoDTO.setContractBaseInfoList(contractBaseInfoList);
        // 立项阶段条件 - 项目经理通过【客户权限创建申请】、【客户权限变更申请】、【客户移交】的方式获得客户管护权后，超过60天仍未有立项审批通过的项目，系统自动释放该客户权限
        boolean projEstablishCondition = this.projEstablishStage(clientAuthority, clientAsLesseeInfoDTO, 60);
        // 评审阶段条件1 - 项目立项流程审批通过生效后，超过90天项目评审流程中部门分管领导未审批通过，则系统系统自动释放该客户权限
        boolean projReviewCondition1 = this.projReviewStage1(clientAsLesseeInfoDTO);
        // 评审阶段条件2 - 项目评审流程审批通过的客户，如超过365天未实际投放，则系统系统自动释放该客户权限
        boolean projReviewCondition2 = this.projReviewStage2(clientAsLesseeInfoDTO);
        // 合同阶段条件 - 合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
        Set<Long> canViewUserIds = new HashSet<>();
        boolean contractCondition = this.contractStage(clientAsLesseeInfoDTO, canViewUserIds);
        log.info("客户释放二次判断结果[立项:{}, 评审1:{}, 评审2:{}, 合同:{}]", projEstablishCondition, projReviewCondition1, projReviewCondition2, contractCondition);
        return projEstablishCondition || projReviewCondition1 || projReviewCondition2 || contractCondition;
    }
}
