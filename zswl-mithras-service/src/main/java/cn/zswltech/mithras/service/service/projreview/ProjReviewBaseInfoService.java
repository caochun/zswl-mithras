package cn.zswltech.mithras.service.service.projreview;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.*;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountProjDetailRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientProjDetailRSP;
import cn.zswltech.mithras.factory.service.RatingAmountService;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.convert.CommonConvert;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewBaseInfoConverter;
import cn.zswltech.mithras.service.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.client.*;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.client.ClientAuthorityMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.dto.ProjReviewListSelectDTO;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ClientAuthority;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.*;
import cn.zswltech.mithras.service.mapper.model.projreview.*;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.client.ClientViewAuthorityEvent;
import cn.zswltech.mithras.service.service.ProjCodeStoreService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.client.ProjClientRoleService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.flow.FlowQueryExtraService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpCommerceInfoLibHandlerImpl;
import cn.zswltech.mithras.service.service.lib.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projfms.impl.ProjReviewStateMachine;
import cn.zswltech.mithras.service.service.projpricing.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.constant.ResultMsg.ONLY_BIZ_DEPT_DO;
import static cn.zswltech.mithras.service.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.EXPIRE;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.NEW;
import static cn.zswltech.mithras.contract.enums.contract.ProjItemStatus.CLOSED;
import static cn.zswltech.mithras.contract.enums.contract.ProjItemStatus.INVALID;
import static cn.zswltech.mithras.service.others.Util.errMithras;

/**
 * @author zhaozhengkang
 * @description 立项基本信息表
 * @date 2022-08-01
 */
@Slf4j
@Service
public class ProjReviewBaseInfoService extends ServiceImpl<ProjReviewBaseInfoMapper, ProjReviewBaseInfo>
        implements ProjReviewUpdateAdvice {

    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ProjEstablishPriceService establishPriceService;
    @Resource
    private ProjReviewPriceService reviewPriceService;
    @Resource
    private ProjReviewBaseInfoConverter baseInfoConverter;
    @Resource
    private ProjReviewPriceConverter priceConverter;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private ProjReviewStateMachine stateMachine;
    @Resource
    private ClientService clientService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoLibHandlerImpl corpCommerceInfoLibHandler;
    @Resource
    private CorpCommerceInfoService commerceInfoService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ProjCodeStoreService projCodeStoreService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private FlowQueryExtraService flowQueryExtraService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private PaymentService paymentService;
    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RatingAmountService ratingAmountService;
    @Resource
    private ProjReviewCashFlowPlanService reviewCashFlowPlanService;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ClientAuthorityMapper clientAuthorityMapper;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;

    public List<ProjReviewBaseInfo> listByIdsOrderByIdDesc(Collection<Long> ids) {
        LambdaQueryWrapper<ProjReviewBaseInfo> query = Wrappers.lambdaQuery();
        query.in(ProjReviewBaseInfo::getId, ids);
        query.orderByDesc(ProjReviewBaseInfo::getId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public ProjReviewBaseInfoAddRSP add(ProjReviewBaseInfoAddREQ req) {
        if (isNotNull(baseMapper.selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .eq(ProjReviewBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("该项目名称存在未关闭的项目评审数据");
        }
        ProjEstablishBaseInfo esBaseInfo = establishBaseInfoService.getById(req.getProjEstablishId());
        if (!RecordStatus.TAKE_EFFECT.name().equals(esBaseInfo.getProjEstablishStatus())) {
            throw new MithrasException("该项目的立项未生效");
        }
        if (ProjProcessState.CHANGING_UN_SUBMIT.name().equals(esBaseInfo.getProjEstablishProcessStatus()) || ProjProcessState.CHANGING_UNDER_APPROVAL.name().equals(esBaseInfo.getProjEstablishProcessStatus())) {
            throw new MithrasException("当前立项数据处于变更未提交或者变更审批中，无法选取");
        }
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            throw new MithrasException("只有项目经理可以创建项目评审");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(esBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        ProjPricingBaseInfo pricingBaseInfo = SpringContextHolder.getBean(ProjPricingBaseInfoService.class).getPricingByReview(req.getProjEstablishId(), null,req.getProjName());
        ProjPricingBaseInfoLib pricingBaseInfoLib = null;
        if(pricingBaseInfo != null){
            pricingBaseInfoLib = SpringContextHolder.getBean(ProjPricingBaseInfoLibService.class).getEffectLatestOne(pricingBaseInfo.getId());
        }
        ProjReviewBaseInfo info;
        List<MaterialsList> copyFileList = null;
        if(pricingBaseInfoLib == null) {
            ProjEstablishPriceDetailRSP priceDetail = establishPriceService.detail(req.getProjEstablishId());
            // 拉取立项基本信息转存
            info = establishToReview(esBaseInfo);
            info.setDeclaredAmount(priceDetail.getDeclaredAmount());
            // 获取客户信息风控行业分类
            Client client = clientService.getById(info.getClientId());
            Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
            Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
            info.setRiskControlIndustryClassify(esBaseInfo.getRiskControlIndustryClassify());
            //更新业务部门负责人，业务分管领导
            OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(info.getBizDeptId());
            if (ObjectUtil.isNotEmpty(bizOrgDO)) {
                info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
                info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
            }
            info.setProjReviewStatus(NEW.name());
            info.setProjReviewProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
            info.setProjEstablishId(esBaseInfo.getId());
            info.setRelationDataType(ReviewRelationDataType.PROJ_ESTABLISH.name());
            // FTP行业分类尝试初始化
            info.setFtpIndustryCategory(Optional.ofNullable(CommonConvert.toFtpIndustryCategory(corpCommerceInfoLib.getRiskControlIndustryClassify())).map(Enum::name).orElse(null));
            baseMapper.insert(info);
            // 拉取报价方案转存
            if (ObjectUtil.isNotEmpty(priceDetail.getAocPriceRSP())) {
                ProjReviewAocPrice aocPrice = priceConverter.esAocRspToEntity(priceDetail.getAocPriceRSP());
                aocPrice.setProjectId(info.getId());
                reviewPriceService.add(aocPrice);
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getFactoringPriceRSP())) {
                ProjReviewFactoringPrice factoringPrice = priceConverter.esFactoringRspToEntity(priceDetail.getFactoringPriceRSP());
                factoringPrice.setProjectId(info.getId());
                reviewPriceService.add(factoringPrice);
            }
            if (ObjectUtil.isNotEmpty(priceDetail.getLeasePriceRSP())) {
                ProjReviewLeasePrice leasePrice = priceConverter.esLeaseRspToEntity(priceDetail.getLeasePriceRSP());
                leasePrice.setProjectId(info.getId());
                reviewPriceService.add(leasePrice);
            }
        } else {
            Long projId = pricingBaseInfo.getId();
            ProjPricingAocPrice pricingAocPrice = SpringContextHolder.getBean(ProjPricingAocPriceService.class).getByProjectId(projId);
            ProjPricingLeasePrice pricingLeasePrice = SpringContextHolder.getBean(ProjPricingLeasePriceService.class).getByProjectId(projId);
            ProjPricingFactoringPrice pricingFactoringPrice = SpringContextHolder.getBean(ProjPricingFactoringPriceService.class).getByProjectId(projId);
            List<ProjPricingCashFlowPlan> pricingCashFlowPlanList = SpringContextHolder.getBean(ProjPricingCashFlowPlanService.class).listByProjPricingId(projId, null);
            // 拉取定价基本信息转存
            info = baseInfoConverter.pricingLibToDetailReview(pricingBaseInfoLib);
            info.setProjReviewStatus(NEW.name());
            info.setProjReviewProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
            info.setProjEstablishId(esBaseInfo.getId());
            info.setRelationDataType(ReviewRelationDataType.PROJ_ESTABLISH.name());
            info.setUpdateTime(null);
            info.setCreateTime(null);
            info.setId(null);
            baseMapper.insert(info);
            // 拉取报价方案转存
            if (ObjectUtil.isNotEmpty(pricingAocPrice)) {
                info.setDeclaredAmount(pricingAocPrice.getApplyCreditAmount());
                ProjReviewAocPrice projPricingAocPrice = priceConverter.reviewAocRspToPricingEntity(pricingAocPrice);
                projPricingAocPrice.setProjectId(info.getId());
                reviewPriceService.add(projPricingAocPrice);
            }
            if (ObjectUtil.isNotEmpty(pricingLeasePrice)) {
                info.setDeclaredAmount(pricingLeasePrice.getApplyCreditAmount());
                ProjReviewLeasePrice projPricingLeasePrice = priceConverter.reviewLeaseRspToPricingEntity(pricingLeasePrice);
                projPricingLeasePrice.setProjectId(info.getId());
                reviewPriceService.add(projPricingLeasePrice);
            }
            if (ObjectUtil.isNotEmpty(pricingFactoringPrice)) {
                info.setDeclaredAmount(pricingFactoringPrice.getApplyCreditAmount());
                ProjReviewFactoringPrice projPricingFactoringPrice = priceConverter.reviewFactoringRspToPricingEntity(pricingFactoringPrice);
                projPricingFactoringPrice.setProjectId(info.getId());
                reviewPriceService.add(projPricingFactoringPrice);
            }
            if (CollectionUtils.isNotEmpty(pricingCashFlowPlanList)){
                List<ProjReviewCashFlowPlan> reviewCashFlowPlanList = pricingCashFlowPlanList.stream().map(item -> {
                    ProjReviewCashFlowPlan reviewCashFlowPlan = BeanUtil.copyProperties(item, ProjReviewCashFlowPlan.class);
                    reviewCashFlowPlan.setId(null);
                    reviewCashFlowPlan.setProjectId(info.getId());
                    return reviewCashFlowPlan;
                }).collect(Collectors.toList());
                reviewCashFlowPlanService.saveBatch(reviewCashFlowPlanList);
            }
        }
        // 写交易结构辅助表
        SpringUtil.getBean(ProjReviewTradeStructureService.class).syncTradeStructure(info.getId());
        // 从项目立项拷贝客户文件
        this.copyFromProjEstablishClientFile(info.getId(), info.getProjEstablishId());
        //维护项目与舆情关系
        flowQueryExtraService.saveClientProjReviewRelation(getRelationClientNames(info), info.getProjName());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_REVIEW,
                        info.getId().toString(),
                        "项目评审-创建")
                )
        );
        return new ProjReviewBaseInfoAddRSP().setId(info.getId()).setBizType(info.getBizType());
    }

    private void copyFromProjEstablishClientFile(Long projReviewId, Long projEstablishId) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_ESTABLISH_CLIENT.name());
        query.eq(MaterialsList::getBelongId, projEstablishId);
        List<MaterialsList> sourceList = materialsListService.list(query);
        if (CollectionUtil.isEmpty(sourceList)) {
            return;
        }
        List<MaterialsList> targetList = new LinkedList<>();
        sourceList.forEach(item -> {
            String bizPath = BusinessModuleEnum.PROJ_REVIEW_CLIENT.name() + "/" + projReviewId + "/" + item.getMaterialsType();
            if (StrUtil.isNotBlank(item.getMaterialSubType())) {
                bizPath = bizPath + "/" + item.getMaterialSubType();
            }
            String newFilePath = ossClient.getBasePath() + bizPath;
            String newOssFilename = bizPath + "/" + item.getFilename();
            try {
                ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
            } catch (Exception e) {
                log.error("项目评审从项目立项拷贝客户文件发生异常[projReviewId:{}, item:{}]", projReviewId, JSONUtil.toJsonStr(item), e);
                // 拷贝失败的话就不存文件记录了
                return;
            }
            MaterialsList newMaterial = new MaterialsList();
            newMaterial.setBelongId(projReviewId);
            newMaterial.setBusinessType(BusinessModuleEnum.PROJ_REVIEW_CLIENT.name());
            newMaterial.setMaterialsType(item.getMaterialsType());
            newMaterial.setMaterialSubType(item.getMaterialSubType());
            newMaterial.setOssFilename(newOssFilename);
            newMaterial.setSuffix(item.getSuffix());
            newMaterial.setFilename(item.getFilename());
            newMaterial.setFilePath(newFilePath);
            newMaterial.setSystemGenerate(item.getSystemGenerate());
            newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
            targetList.add(newMaterial);
        });
        if (CollectionUtil.isNotEmpty(targetList)) {
            materialsListService.saveBatch(targetList);
        }
    }

    private void copyFromGroupProjReviewClientFile(Long projReviewId, Long groupProjReviewId) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW_CLIENT.name());
        query.eq(MaterialsList::getBelongId, groupProjReviewId);
        List<MaterialsList> sourceList = materialsListService.list(query);
        if (CollectionUtil.isEmpty(sourceList)) {
            return;
        }
        List<MaterialsList> targetList = new LinkedList<>();
        sourceList.forEach(item -> {
            String bizPath = BusinessModuleEnum.PROJ_REVIEW_CLIENT.name() + "/" + projReviewId + "/" + item.getMaterialsType();
            if (StrUtil.isNotBlank(item.getMaterialSubType())) {
                bizPath = bizPath + "/" + item.getMaterialSubType();
            }
            String newFilePath = ossClient.getBasePath() + bizPath;
            String newOssFilename = bizPath + "/" + item.getFilename();
            try {
                ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
            } catch (Exception e) {
                log.error("项目评审从授信评审拷贝客户文件发生异常[projReviewId:{}, item:{}]", projReviewId, JSONUtil.toJsonStr(item), e);
                // 拷贝失败的话就不存文件记录了
                return;
            }
            MaterialsList newMaterial = new MaterialsList();
            newMaterial.setBelongId(projReviewId);
            newMaterial.setBusinessType(BusinessModuleEnum.PROJ_REVIEW_CLIENT.name());
            newMaterial.setMaterialsType(item.getMaterialsType());
            newMaterial.setMaterialSubType(item.getMaterialSubType());
            newMaterial.setOssFilename(newOssFilename);
            newMaterial.setSuffix(item.getSuffix());
            newMaterial.setFilename(item.getFilename());
            newMaterial.setFilePath(newFilePath);
            newMaterial.setSystemGenerate(item.getSystemGenerate());
            newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
            targetList.add(newMaterial);
        });
        if (CollectionUtil.isNotEmpty(targetList)) {
            materialsListService.saveBatch(targetList);
        }
    }

    private Set<String> getRelationClientNames(ProjReviewBaseInfo info) {
        Set<String> clientNames = new HashSet<>();
        if (StringUtil.isNotEmpty(info.getLesseeInfo())) {
            clientNames.addAll(JSON.parseArray(info.getLesseeInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(info.getCreditorInfo())) {
            clientNames.addAll(JSON.parseArray(info.getCreditorInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(info.getDebtorInfo())) {
            clientNames.addAll(JSON.parseArray(info.getDebtorInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        if (StringUtil.isNotEmpty(info.getGuaranteeInfo())) {
            clientNames.addAll(JSON.parseArray(info.getGuaranteeInfo(), ClientInfo.class).stream().map(ClientInfo::getClientName).collect(Collectors.toList()));
        }
        return clientNames;
    }

    @Transactional(rollbackFor = Throwable.class)
    public ProjReviewBaseInfoAddRSP addByGroupCredit(ProjReviewBaseInfoAddByGroupCreditREQ req) {
        if (isNotNull(baseMapper.selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .eq(ProjReviewBaseInfo::getProjName, req.getProjName())))) {
            throw new MithrasException("该项目名称存在未关闭的项目评审数据");
        }
        GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(req.getGroupCreditReviewId());
        if (Objects.isNull(groupCreditReviewBaseInfo)) {
            throw new MithrasException("集团授信评审不存在");
        }
        if (!RecordStatus.TAKE_EFFECT.name().equals(groupCreditReviewBaseInfo.getGroupCreditReviewStatus())) {
            throw new MithrasException("该集团授信评审未生效");
        }
        if (GroupCreditReviewProcessStatus.CHANGING_UN_SUBMIT.name().equals(groupCreditReviewBaseInfo.getGroupCreditReviewProcessStatus()) || GroupCreditReviewProcessStatus.CHANGING_UNDER_APPROVAL.name().equals(groupCreditReviewBaseInfo.getGroupCreditReviewProcessStatus())) {
            throw new MithrasException("当前集团授信评审数据处于变更未提交或者变更审批中，无法选取");
        }
        if (!SpringUtil.getBean(ClientAuthorityService.class).currentUserHasManagerAuth(groupCreditReviewBaseInfo.getClientId())) {
            throw new MithrasException("无所选客户管护权，无权进行操作");
        }
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();
        if (isNull(bizOrgDO)) {
            throw new MithrasException(ONLY_BIZ_DEPT_DO);
        }
        if (!sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
            throw new MithrasException("只有项目经理可以创建项目评审");
        }
        Client client = clientMapper.selectById(req.getClientId());
        if (Objects.isNull(client)) {
            throw new MithrasException("用信客户不存在");
        }
        if (!ClientType.CORPORATION.name().equals(client.getClientType())) {
            throw new MithrasException("只有法人客户可发起用信");
        }
        String gcrRemainAmountLockKey = CacheEnum.GROUP_CREDIT_REVIEW_REMAIN_AMOUNT_LOCK.buildKey(req.getGroupCreditReviewId());
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(gcrRemainAmountLockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            if (groupCreditReviewService.getRemainCreditAmount(groupCreditReviewBaseInfo.getId()) <= 0) {
                throw new MithrasException("该授信可用额度已小于等于0，不可发起新的项目评审");
            }
        } finally {
            redisDistLock.unlock(gcrRemainAmountLockKey);
        }
        ProjReviewBaseInfo info = groupCreditReviewToReview(groupCreditReviewBaseInfo);
        info.setClientId(req.getClientId());
        info.setBizType(req.getBizType());
        info.setProjName(req.getProjName());
        //自动设置项目主办、业务部门，业务部门负责人，业务部门分管领导
        info.setProjSponsorUserId(AccountUtil.getLoginInfo().getId());
        info.setBizDeptId(bizOrgDO.getId());
        info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
        info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
        Long seq = projCodeStoreService.maxSeqId(req.getBizType()) + 1;
        info.setProjCode(projCodeStoreService.generateProjCode(req.getBizType(), seq));
        info.setDeclaredAmount(0L);
        // 不同业务 填充主承租人或债权人
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientId(client.getId());
        clientInfo.setClientType(client.getClientType());
        clientInfo.setClientName(client.getClientName());
        if (CharSequenceUtil.equalsAny(req.getBizType(), ZL.name(), ZZ.name())) {
            info.setLesseeInfo(JSON.toJSONString(ListUtil.toList(clientInfo)));
        } else if (CharSequenceUtil.equalsAny(req.getBizType(), BL.name(), ZR.name())) {
            info.setCreditorInfo(JSON.toJSONString(ListUtil.toList(clientInfo)));
        }
        //填充企业性质
        info.setEnterpriseNature(getEnterpriseNatureById(client.getId()));
        // FTP行业分类尝试初始化
        List<CorpCommerceInfo> corpCommerceInfoList = commerceInfoService.findByClientId(client.getId());
        if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
            info.setFtpIndustryCategory(Optional.ofNullable(CommonConvert.toFtpIndustryCategory(corpCommerceInfoList.get(0).getRiskControlIndustryClassify())).map(Enum::name).orElse(null));
        }
        baseMapper.insert(info);
        projCodeStoreService.add(req.getBizType(), seq, info.getProjCode());
        // 初始化报价方案
        if (CharSequenceUtil.equalsAny(req.getBizType(), ZL.name(), ZZ.name())) {
            ProjReviewLeasePrice leasePrice = new ProjReviewLeasePrice();
            leasePrice.setProjectId(info.getId());
            leasePrice.setProjectApprovalAmount(groupCreditReviewBaseInfo.getProjectApprovalAmount());
//            leasePrice.setApplyCreditAmount(0L);
            reviewPriceService.add(leasePrice);
        } else if (CharSequenceUtil.equalsAny(req.getBizType(), BL.name())) {
            ProjReviewFactoringPrice factoringPrice = new ProjReviewFactoringPrice();
            factoringPrice.setProjectId(info.getId());
//            factoringPrice.setApplyCreditAmount(0L);
            reviewPriceService.add(factoringPrice);
        } else if (CharSequenceUtil.equalsAny(req.getBizType(), ZR.name())) {
            ProjReviewAocPrice aocPrice = new ProjReviewAocPrice();
            aocPrice.setProjectId(info.getId());
//            aocPrice.setApplyCreditAmount(0L);
            reviewPriceService.add(aocPrice);
        }
        // 从授信评审同步文件
        LambdaQueryWrapper<MaterialsList> mQuery = Wrappers.lambdaQuery();
        mQuery.eq(MaterialsList::getBelongId, info.getGroupCreditReviewId());
        mQuery.eq(MaterialsList::getBusinessType, BusinessModuleEnum.GROUP_CREDIT_REVIEW.name());
        List<MaterialsList> mList = SpringUtil.getBean(MaterialsListService.class).list(mQuery);
        if (CollectionUtil.isNotEmpty(mList)) {
            mList.forEach(e -> {
                e.setId(null);
                e.setBelongId(info.getId());
                e.setBusinessType(BusinessModuleEnum.PROJ_REVIEW.name());
            });
            SpringUtil.getBean(MaterialsListService.class).saveBatch(mList);
        }
        // 写交易结构辅助表
        SpringUtil.getBean(ProjReviewTradeStructureService.class).syncTradeStructure(info.getId());
        this.copyFromGroupProjReviewClientFile(info.getId(), groupCreditReviewBaseInfo.getId());
        /*if(req.isCreatePricing()) {
            ProjPricingBaseInfoAddByGroupCreditREQ pricingReq = BeanUtil.copyProperties(req, ProjPricingBaseInfoAddByGroupCreditREQ.class);
            pricingReq.setCreateReview(false);
            SpringContextHolder.getBean(ProjPricingBaseInfoService.class).addByGroupCredit(pricingReq);
        }*/
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_REVIEW,
                        info.getId().toString(),
                        "项目评审-创建")
                )
        );
        return new ProjReviewBaseInfoAddRSP().setId(info.getId()).setBizType(info.getBizType());
    }

    private Set<Long> ensureChangeClient(ProjReviewBaseInfo oldInfo, ProjReviewBaseInfo newInfo) {
        // FIXME 代码有待整理
        List<ClientInfo> newPersonInfoList = new LinkedList<>();
        if (StrUtil.isNotBlank(newInfo.getLesseeInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getLesseeInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getGuaranteeInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getGuaranteeInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getMortgagorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getMortgagorInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getPledgorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getPledgorInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getCreditorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getCreditorInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(newInfo.getDebtorInfo())) {
            newPersonInfoList.addAll(JSONUtil.toList(newInfo.getDebtorInfo(), ClientInfo.class));
        }
        if (Objects.isNull(oldInfo)) {
            // 说明是新创建，返回全部
            return newPersonInfoList.stream().map(ClientInfo::getClientId).collect(Collectors.toSet());
        }
        List<ClientInfo> oldPersonInfoList = new LinkedList<>();
        if (StrUtil.isNotBlank(oldInfo.getLesseeInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getLesseeInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getGuaranteeInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getGuaranteeInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getMortgagorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getMortgagorInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getPledgorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getPledgorInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getCreditorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getCreditorInfo(), ClientInfo.class));
        }
        if (StrUtil.isNotBlank(oldInfo.getDebtorInfo())) {
            oldPersonInfoList.addAll(JSONUtil.toList(oldInfo.getDebtorInfo(), ClientInfo.class));
        }
        // 比对得到发生变化的客户
        Set<Long> oldClientIds = oldPersonInfoList.stream().map(ClientInfo::getClientId).collect(Collectors.toSet());
        Set<Long> newClientIds = newPersonInfoList.stream().map(ClientInfo::getClientId).collect(Collectors.toSet());
        Set<Long> diff1 = oldClientIds.stream().filter(e -> !newClientIds.contains(e)).collect(Collectors.toSet());
        Set<Long> diff2 = newClientIds.stream().filter(e -> !oldClientIds.contains(e)).collect(Collectors.toSet());
        Set<Long> result = new HashSet<>();
        result.addAll(diff1);
        result.addAll(diff2);
        return result;
    }

    private void refreshClientMaterials(Long projReviewId, Set<Long> clientIds) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return;
        }
        // 先移除老数据
        materialsListService.remove(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, projReviewId)
                .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW_CLIENT.name())
                .in(MaterialsList::getSourceBusinessKey, clientIds.stream().map(Object::toString).collect(Collectors.toSet()))
        );
        List<Client> clientList = clientService.listByIds(clientIds);
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
        List<String> typeList = new LinkedList<>();
        typeList.addAll(CorporationClientMaterialTypeEnum.needCopyType());
        typeList.addAll(NormalClientMaterialTypeEnum.needCopyType());
        query.in(MaterialsList::getMaterialsType, typeList);
        query.and(innerQuery -> {
            for (Client client : clientList) {
                innerQuery.or().eq(MaterialsList::getBelongId, client.getId());
                if (Objects.nonNull(client.getBelongSponsorId())) {
                    innerQuery.eq(BaseModel::getCreateBy, client.getBelongSponsorId());
                }
            }
        });
        List<MaterialsList> mList = materialsListService.list(query);
        if (CollectionUtil.isNotEmpty(mList)) {
            List<MaterialsList> todoList = new LinkedList<>();
            mList.forEach(item -> {
                // 业务申请书和征信授权书特殊处理，无需带入
                if (Objects.equals(item.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.CREDIT_LETTER.name())) {
                    return;
                } else if (Objects.equals(item.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.LEASE_APPLICATION.name())) {
                    return;
                }
                String bizPath = BusinessModuleEnum.PROJ_REVIEW_CLIENT.name() + "/" + projReviewId + "/" + item.getMaterialsType();
                if (StrUtil.isNotBlank(item.getMaterialSubType())) {
                    bizPath = bizPath + "/" + item.getMaterialSubType();
                }
                String newFilePath = ossClient.getBasePath() + bizPath;
                String newOssFilename = bizPath + "/" + item.getFilename();
                try {
                    ossClient.copy(ossClient.getBasePath() + item.getOssFilename(), ossClient.getBasePath() + newOssFilename);
                } catch (Exception e) {
                    log.error("项目评审拷贝客户资料发生异常[projReviewId:{}, item:{}]", projReviewId, JSONUtil.toJsonStr(item), e);
                    // 拷贝失败的话就不存文件记录了
                    return;
                }
                MaterialsList newMaterial = new MaterialsList();
                newMaterial.setBelongId(projReviewId);
                newMaterial.setBusinessType(BusinessModuleEnum.PROJ_REVIEW_CLIENT.name());
                newMaterial.setMaterialsType(item.getMaterialsType());
                newMaterial.setMaterialSubType(item.getMaterialSubType());
                newMaterial.setOssFilename(newOssFilename);
                newMaterial.setSuffix(item.getSuffix());
                newMaterial.setFilename(item.getFilename());
                newMaterial.setFilePath(newFilePath);
                newMaterial.setSystemGenerate(item.getSystemGenerate());
                newMaterial.setSourceBusinessKey(item.getBelongId().toString());
                todoList.add(newMaterial);
            });
            if (CollectionUtil.isNotEmpty(todoList)) {
                materialsListService.saveBatch(todoList);
            }
        }
    }

    private ProjReviewBaseInfo groupCreditReviewToReview(GroupCreditReviewBaseInfo groupCreditReviewBaseInfo) {
        ProjReviewBaseInfo projReviewBaseInfo = new ProjReviewBaseInfo();
//        projReviewBaseInfo.setMainId(groupCreditReviewBaseInfo.getMainId());
        projReviewBaseInfo.setProjectType(groupCreditReviewBaseInfo.getProjectType());
        projReviewBaseInfo.setProjBackground(groupCreditReviewBaseInfo.getProjBackground());
        projReviewBaseInfo.setProjSponsorUserId(groupCreditReviewBaseInfo.getProjSponsorUserId());
        projReviewBaseInfo.setProjCosponsorUserIds(groupCreditReviewBaseInfo.getProjCosponsorUserIds());
        projReviewBaseInfo.setBizDeptId(groupCreditReviewBaseInfo.getBizDeptId());
        projReviewBaseInfo.setBizDeptLeaderId(groupCreditReviewBaseInfo.getBizDeptLeaderId());
        projReviewBaseInfo.setBizDivisionLeaderId(groupCreditReviewBaseInfo.getBizDivisionLeaderId());
        projReviewBaseInfo.setRiskControlManagerId(groupCreditReviewBaseInfo.getRiskControlManagerId());
        projReviewBaseInfo.setLegalManagerUserId(groupCreditReviewBaseInfo.getLegalManagerUserId());
        projReviewBaseInfo.setRelationDataType(ReviewRelationDataType.GROUP_CREDIT_REVIEW.name());
        projReviewBaseInfo.setGroupCreditReviewId(groupCreditReviewBaseInfo.getId());
        projReviewBaseInfo.setProjReviewStatus(NEW.name());
        projReviewBaseInfo.setProjReviewProcessStatus(ProjProcessState.NEW_UN_SUBMIT.name());
        return projReviewBaseInfo;

    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(ProjReviewBaseInfoModifyREQ req) {
        saveCheck(req.getId());
        ProjReviewBaseInfo originalInfo = baseMapper.selectById(req.getId());
        // 如果有对应合同则不允许变更承租人
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(originalInfo.getId());
        if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
            Set<Long> contractIds = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractIds(contractIds);
            if (CollectionUtil.isNotEmpty(contractTenantryList)) {
                List<Long> contractClientIds = contractTenantryList.stream().map(ContractTenantry::getLesseeId).distinct().collect(Collectors.toList());
                Set<Long> temp = new HashSet<>();
                if (CollectionUtil.isNotEmpty(req.getLesseeInfo())) {
                    temp.addAll(req.getLesseeInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toSet()));
                }
                if (CollectionUtil.isNotEmpty(req.getCreditorInfo())) {
                    temp.addAll(req.getCreditorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toSet()));
                }
                if (CollectionUtil.isNotEmpty(req.getDebtorInfo())) {
                    temp.addAll(req.getDebtorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toSet()));
                }
                List<Long> projReviewClientIds = new LinkedList<>(temp);
                Assert.isTrue(projReviewClientIds.size() == contractClientIds.size(), () -> MithrasException.newException("合同的承租人/债权人/债务人与项目评审中不一致，请检查"));
                contractClientIds.sort(Comparator.comparing(e -> e));
                projReviewClientIds.sort(Comparator.comparing(e -> e));
                for (int i = 0; i < contractClientIds.size(); i++) {
                    Long c1 = contractClientIds.get(i);
                    Long c2 = projReviewClientIds.get(i);
                    Assert.isTrue(Objects.equals(c1, c2), () -> MithrasException.newException("合同的承租人/债权人/债务人与项目评审中不一致，请检查"));
                }
            }
        }
        //评审对应的客户id，跟承租人和债权人对应的
        if (ZL.name().equals(req.getBizType()) || ZZ.name().equals(originalInfo.getBizType())) {
            Long clientId = req.getLesseeInfo().get(0).getClientId();
            if (ObjectUtil.notEqual(clientId, originalInfo.getClientId())) {
                ProjReviewBaseInfo updateInfo = new ProjReviewBaseInfo();
                updateInfo.setClientId(clientId);
                updateInfo.setId(originalInfo.getId());
                updateInfo.setEnterpriseNature(getEnterpriseNatureById(clientId));
                baseMapper.updateById(updateInfo);
            }
        }
        if (BL.name().equals(originalInfo.getBizType()) || ZR.name().equals(originalInfo.getBizType())) {
            Long clientId = req.getCreditorInfo().get(0).getClientId();
            if (ObjectUtil.notEqual(clientId, originalInfo.getClientId())) {
                ProjReviewBaseInfo updateInfo = new ProjReviewBaseInfo();
                updateInfo.setClientId(clientId);
                updateInfo.setId(originalInfo.getId());
                updateInfo.setEnterpriseNature(getEnterpriseNatureById(clientId));
                baseMapper.updateById(updateInfo);
            }
        }


        /*---------------------参数转换----------------------*/
        ProjReviewBaseInfo info = baseInfoConverter.modifyREQtoEntity(req);
        //防止code被更新
        info.setProjCode(null);
        //更新业务部门负责人，业务分管领导
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(originalInfo.getBizDeptId());
        if (ObjectUtil.isNotEmpty(bizOrgDO)) {
            info.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
            info.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
        }
        baseMapper.updateAnnotationIncludeNullById(info);
        // 写交易结构辅助表
        SpringUtil.getBean(ProjReviewTradeStructureService.class).syncTradeStructure(info.getId());
        // 刷新客户资料
        this.refreshClientMaterials(req.getId(), this.ensureChangeClient(originalInfo, this.getById(req.getId())));
        recordStatus(req.getId());
        //维护项目与舆情关系
        flowQueryExtraService.saveClientProjReviewRelation(getRelationClientNames(info), info.getProjName());
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.PROJ_REVIEW,
                        info.getId().toString(),
                        "项目评审-修改")
                )
        );
    }

    //更新部门领导信息
    @Transactional(rollbackFor = Throwable.class)
    public void renewLeader(Long projReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = baseMapper.selectById(projReviewId);
        if (ObjectUtil.isEmpty(projReviewBaseInfo)) {
            return;
        }
        OrgDO bizOrgDO = sysUserService.geBizDeptByOrgId(projReviewBaseInfo.getBizDeptId());
        if (isNotNull(bizOrgDO)) {
            LambdaUpdateWrapper<ProjReviewBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ProjReviewBaseInfo::getId, projReviewBaseInfo.getId());
            updateWrapper.set(ProjReviewBaseInfo::getBizDeptLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
            updateWrapper.set(ProjReviewBaseInfo::getBizDivisionLeaderId, sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
            this.baseMapper.update(null, updateWrapper);
        }
    }

    /**
     * 对项目立项流程审批通过的客户，如项目立项流程生效后的X天内项目评审流程中部门分管领导未审批通过，则系统自动释放该客户为公海客户
     *
     * @param day           天数
     * @param projReviewIds 项目评审ids
     * @param isNotice      是否通知 ture通知 false 修改
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void noticeOrModifyClientTypeByReview(int day, List<Long> projReviewIds, boolean isNotice) {
        //查询项目评审
        Set<Long> projEstablishIds = null;
        if (ObjectUtil.isNotEmpty(projReviewIds)) {
            projEstablishIds = baseMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                    .in(ProjReviewBaseInfo::getId, projReviewIds)
                    .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), RecordStatus.EXPIRE.name())).stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet());
        }
        //到期立项
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = establishBaseInfoService.dayBeforeEffect(day, projEstablishIds, isNotice);
        if (CollectionUtil.isEmpty(projEstablishBaseInfos)) {
            //没有过期的项目立项，直接结束
            return;
        }
        Map<Long, ProjEstablishBaseInfo> allProjEstablishMap = projEstablishBaseInfos.stream().collect(Collectors.toMap(ProjEstablishBaseInfo::getId, e -> e));
        Set<Long> allProjEstablishIds = projEstablishBaseInfos.stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toSet());
        Set<Long> allClientIds = projEstablishBaseInfos.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toSet());
        List<ProjReviewBaseInfo> notEffectProjReview;
        //查询所以涉及到的项目评审
        List<ProjReviewBaseInfo> effectProjReviewBaseInfos = baseMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .in(ProjReviewBaseInfo::getClientId, allClientIds)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()));
        //1.已有生效的评审，可以直接去除
        effectProjReviewBaseInfos.stream().filter(review -> RecordStatus.TAKE_EFFECT.name().equals(review.getProjReviewStatus())).forEach(review -> {
            allProjEstablishIds.remove(review.getProjEstablishId());
            allClientIds.remove(review.getClientId());
        });
        //2.无生效到要查询流程有没有过部门分管领导
        notEffectProjReview = effectProjReviewBaseInfos.stream().filter(review -> !RecordStatus.TAKE_EFFECT.name().equals(review.getProjReviewStatus()) && allClientIds.contains(review.getClientId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(notEffectProjReview)) {
            Map<Long, Long> reviewId2EstablishIdMap = notEffectProjReview.stream()
                    .filter(Objects::nonNull)
                    .filter(a -> Objects.nonNull(a.getProjEstablishId()))
                    .collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId));
            //查询项目评审创建流程
            ProcessPageReq req = new ProcessPageReq();
            req.setPageIndex(1);
            req.setPageSize(Integer.MAX_VALUE);
            req.setModelKey(ProcessModelTypeEnum.ProjReviewCreateFlow.name());
            req.setBusinessKeyList(notEffectProjReview.stream().map(ProjReviewBaseInfo::getId).map(String::valueOf).collect(Collectors.toList()));
            //查询立项审批中的流程
            cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(req);
            //查询每个流程到历史
            if (ObjectUtil.isNotEmpty(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
                processRespPage.getContents().forEach(processResp -> {
                    ProcessHistoryReq historyReq = new ProcessHistoryReq();
                    historyReq.setPageIndex(1);
                    historyReq.setPageSize(Integer.MAX_VALUE);
                    historyReq.setProcessInstanceId(processResp.getProcessInstanceId());
                    //查询立项审批中的流程
                    cn.zswltech.flow.core.util.Page<ProcessHistoryResp> history = flowProcessApiService.history(historyReq);
                    if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getContents())) {
                        for (ProcessHistoryResp content : history.getContents()) {//已经过过部门分管领导，且领导同意
                            if ("userTask_bizDivisionLeader".equals(content.getTaskActivityId()) && ApprovalButtonTypeEnum.AGREE.name().equals(content.getType())) {
                                ProjEstablishBaseInfo projEstablishBaseInfo = allProjEstablishMap.get(reviewId2EstablishIdMap.get(Long.valueOf(processResp.getBusinessKey())));
                                if (ObjectUtil.isNotEmpty(projEstablishBaseInfo)) {
                                    allProjEstablishIds.remove(projEstablishBaseInfo.getId());
                                    allClientIds.remove(projEstablishBaseInfo.getClientId());
                                }
                                break;
                            }
                        }
                    }
                });
            }
        }
        //逾期项目评审
        List<ProjEstablishBaseInfo> overdueProjEstablish = allProjEstablishIds.stream().map(allProjEstablishMap::get).collect(Collectors.toList());
        //处理符合条件的项目
        if (isNotice && !allProjEstablishIds.isEmpty()) {
            //发消息
            noticeProjEstablishSponsorUser(overdueProjEstablish, "客户%s30天后若仍未发起项目评审流程且经部门分管领导审批通过");
        }
//        if (!isNotice && !allClientIds.isEmpty()) {
//            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
//                    .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
//                    .in(ClientAuthority::getClientId, allClientIds)
//                    .eq(ClientAuthority::getDeleted, 0));
//            List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, allClientIds));
//            for (Client client : clientList) {
//                for (ClientAuthority clientAuthority : clientAuthorityList) {
//                    if (client.getId().equals(clientAuthority.getClientId())) {
//                        releaseClient(client, clientAuthority);
//                    }
//                }
//            }
//            //todo 调整客户类型
//            //clientService.updateClientAuth(allClientIds, ClientAuthEnum.HIGH_SEAS.name());
//        }
    }

    /**
     * 对项目评审流程审批通过的客户，如超过365天未实际投放，则系统自动释放该客户为公海客户
     *
     * @param day           天数
     * @param projReviewIds 项目评审ids
     * @param isNotice      是否通知 ture通知 false 修改
     **/
    public void noticeOrModifyClientTypeByPayment(int day, Set<Long> projReviewIds, boolean isNotice) {
        List<ProjReviewBaseInfo> projReviewBaseInfos = dayBeforeEffect(day, projReviewIds, isNotice);
        if (ObjectUtil.isEmpty(projReviewBaseInfos)) {
            return;
        }
        //查找有实际投放项目
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
        if (ObjectUtil.isEmpty(contractBaseInfos)) {
            return;
        }
        Map<Long, List<ContractBaseInfo>> projReviewContractMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, List<PaymentBaseInfo>> contractPaymentBaseMap = paymentService.getPaymentByContract(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        Map<Long, ProjReviewBaseInfo> review2EstablishMap = projReviewBaseInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getProjEstablishId())).collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e, (a, b) -> a));
        Set<Long> allProjEstablishId = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet());
        Set<Long> allClients = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getClientId).collect(Collectors.toSet());
        projReviewContractMap.forEach((reviewId, contracts) -> {
            //查找合同对应付款
            if (ObjectUtil.isNotEmpty(contracts)) {
                contracts.forEach(contract -> {
                    if (ObjectUtil.isNotEmpty(contractPaymentBaseMap.get(contract.getId()))) {
                        //有付款申请通过
                        ProjReviewBaseInfo projReviewBaseInfo = review2EstablishMap.get(reviewId);
                        if (ObjectUtil.isNotEmpty(projReviewBaseInfo)) {
                            allProjEstablishId.remove(projReviewBaseInfo.getProjEstablishId());
                            allClients.remove(projReviewBaseInfo.getClientId());
                        }

                    }
                });
            }
        });
        //处理符合条件的项目评审
        if (isNotice && !allProjEstablishId.isEmpty()) {
            //通知
            List<ProjEstablishBaseInfo> projEstablishBaseInfos = establishBaseInfoService.listByIds(allProjEstablishId);
            noticeProjEstablishSponsorUser(projEstablishBaseInfos, "客户%s30天后若仍未进行投放");
        }
//        if (!isNotice && !allClients.isEmpty()) {
//            //修改客户权限类型
//            //clientService.updateClientAuth(allClients, ClientAuthEnum.HIGH_SEAS.name());
//            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
//                    .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
//                    .in(ClientAuthority::getClientId, allClients)
//                    .eq(ClientAuthority::getDeleted, 0));
//            List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, allClients));
//            for (Client client : clientList) {
//                for (ClientAuthority clientAuthority : clientAuthorityList) {
//                    if (client.getId().equals(clientAuthority.getClientId())) {
//                        releaseClient(client, clientAuthority);
//                    }
//                }
//            }
//        }
    }

    /**
     * 查询立项day天前生效的项目评审
     **/
    public List<ProjReviewBaseInfo> dayBeforeEffect(int day, Set<Long> projReviewIds, boolean isDay) {
        LocalDate localDate = LocalDate.now().minusDays(day + 1);
        Date beganDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(localDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(BusinessModuleEnum.PROJ_REVIEW.getModelKeyList());
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (flowRespPage == null || flowRespPage.getContents() == null) {
            return ListUtil.empty();
        }
        //已经到期合同
        List<ProcessResp> processResps = flowRespPage.getContents().stream().filter(base -> ObjectUtil.isNotEmpty(base.getEndTime()) && (isDay ? (base.getEndTime().before(beganDate) && endDate.before(base.getEndTime())) : base.getEndTime().before(beganDate))).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(processResps)) {
            return ListUtil.empty();
        }
        Set<Long> projReviewEndIds = processResps.stream().map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(projReviewEndIds) && ObjectUtil.isNotEmpty(projReviewIds)) {
            projReviewEndIds.removeIf(e -> !projReviewIds.contains(e));
        }
        if (ObjectUtil.isEmpty(projReviewEndIds)) {
            return ListUtil.empty();
        }
        //查询今天过期的立项
        return baseMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name())
                .in(ProjReviewBaseInfo::getId, projReviewEndIds));
    }


    /**
     * 查询立项day天前生效的项目评审
     **/
    public List<ContractBaseInfo> dayBeforeContractEffect(int day, Set<Long> tempContractIds, boolean isDay) {
        LocalDate localDate = LocalDate.now().minusDays(day + 1);
        Date beganDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(localDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.ContractEarlySettleFlow.name(), ProcessModelTypeEnum.ContractNormalSettleFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(Integer.MAX_VALUE);
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (flowRespPage == null || flowRespPage.getContents() == null) {
            return ListUtil.empty();
        }
        //已经到期合同
        List<ProcessResp> processResps = flowRespPage.getContents().stream().filter(base -> ObjectUtil.isNotEmpty(base.getEndTime())
                && (isDay ? (base.getEndTime().before(beganDate) && endDate.before(base.getEndTime())) : base.getEndTime().before(beganDate))).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(processResps)) {
            return ListUtil.empty();
        }
        Set<Long> contractIds = processResps.stream().map(ProcessResp::getBusinessKey).map(Long::valueOf).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(contractIds) && ObjectUtil.isNotEmpty(tempContractIds)) {
            contractIds.removeIf(e -> !tempContractIds.contains(e));
        }
        if (ObjectUtil.isEmpty(contractIds)) {
            return ListUtil.empty();
        }
        //查询今天过期的立项
        return contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name())
                .in(ContractBaseInfo::getId, contractIds));
    }

    //客户释放通知
    private void noticeProjEstablishSponsorUser(List<ProjEstablishBaseInfo> projEstablishBaseInfos, String relation) {
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(projEstablishBaseInfos.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toList()));
        projEstablishBaseInfos.forEach(projEstablishBaseInfo -> {
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统通知");
            messageAddREQ.setMessageType(MessageTypeEnum.CLIENT_RELEASE.name());
            messageAddREQ.setRelation(String.format(relation, clientId2Name.get(projEstablishBaseInfo.getClientId())));
            messageAddREQ.setContent(String.valueOf(projEstablishBaseInfo.getId()));
            messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.CLIENT_RELEASE.pcUrl, projEstablishBaseInfo.getId()));
            messageAddREQ.setTo(Collections.singletonList(projEstablishBaseInfo.getProjSponsorUserId()));
            messageAddREQ.setNeedOa(true);
            messageAddREQ.setNoticeSource(NoticeSourceENUM.CLIENT_RELEASE.name());
            messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
        });
    }
    //客户释放通知
   /* private void noticeProjReviewSponsorUser(List<ProjReviewBaseInfo> projReviewBaseInfos){
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getClientId).collect(Collectors.toList()));
        projReviewBaseInfos.forEach(projReviewBaseInfo -> {
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统通知");
            messageAddREQ.setMessageType(MessageTypeEnum.CLIENT_RELEASE.name());
            messageAddREQ.setRelation(String.format("客户%s30天后若仍未进行投放", clientId2Name.get(projReviewBaseInfo.getClientId())));
            messageAddREQ.setContent(String.valueOf(projReviewBaseInfo.getProjEstablishId()));
            messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.CLIENT_RELEASE.pcUrl, projReviewBaseInfo.getProjEstablishId()));
            messageAddREQ.setTo(Collections.singletonList(projReviewBaseInfo.getProjSponsorUserId()));
            messageAddREQ.setNeedQa(true);
            messageAddREQ.setNoticeSource(NoticeSourceENUM.CLIENT_RELEASE.name());
            messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
        });
    }*/


    public Page<ProjReviewBaseInfoListRSP> list(ProjReviewBaseInfoListREQ req) {
        ProjReviewListSelectDTO dto = baseInfoConverter.listREQtoSelectDTO(req);
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        Page<ProjReviewBaseInfo> pageData = baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<ProjReviewBaseInfoListRSP> resPage = new ArrayList<>(pageData.getRecords().size());
        for (ProjReviewBaseInfo record : pageData.getRecords()) {
            ProjReviewBaseInfoListRSP rsp = baseInfoConverter.entityToListRsp(record);
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
        for (ProjReviewBaseInfoListRSP rsp : resPage) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get)
                        .collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }
        return new Page<ProjReviewBaseInfoListRSP>()
                .setCurrent(pageData.getCurrent())
                .setRecords(resPage)
                .setSize(pageData.getSize())
                .setTotal(pageData.getTotal());
    }

    /**
     * 详情接口
     *
     * @return ProjReviewBaseInfoDetailRSP
     */
    public ProjReviewBaseInfoDetailRSP detail(Long id, String processInstanceId) {
        ProjReviewBaseInfo record = baseMapper.selectById(id);
        // 如果是新建且未提交审批，需要在获取详情的时候自动更新一下评级数据
        if (Objects.equals(record.getProjReviewStatus(), NEW.name()) && !Objects.equals(record.getProjReviewProcessStatus(), ProjProcessState.NEW_UNDER_APPROVAL.name())) {
            ProjReviewBaseInfoUpdateRatingREQ updateRatingREQ = new ProjReviewBaseInfoUpdateRatingREQ();
            updateRatingREQ.setId(id);
            ProjReviewBaseInfoUpdateRatingRSP updateRatingRSP = this.updateRating(updateRatingREQ);
            if (Objects.nonNull(updateRatingRSP.getRatingClientId())) {
                record.setEvaluationSubjectRatingScoreId(updateRatingRSP.getRatingClientId());
            }
            if (StrUtil.isNotBlank(updateRatingRSP.getRatingFinalScore())) {
                record.setEvaluationSubjectRatingScore(updateRatingRSP.getRatingFinalScore());
            }
            if (Objects.nonNull(updateRatingRSP.getRatingMainLesseeId())) {
                record.setMainLesseeRatingScoreId(updateRatingRSP.getRatingMainLesseeId());
            }
            if (StrUtil.isNotBlank(updateRatingRSP.getRatingMainLesseeScore())) {
                record.setMainLesseeRatingScore(updateRatingRSP.getRatingMainLesseeScore());
            }
        }
        // 填充风险敞口
        record.setLesseeInfo(projReviewService.setExposureRisk(record.getLesseeInfo()));
        record.setPledgorInfo(projReviewService.setExposureRisk(record.getPledgorInfo()));
        record.setGuaranteeInfo(projReviewService.setExposureRisk(record.getGuaranteeInfo()));
        record.setMortgagorInfo(projReviewService.setExposureRisk(record.getMortgagorInfo()));
        record.setDebtorInfo(projReviewService.setExposureRisk(record.getDebtorInfo()));
        record.setCreditorInfo(projReviewService.setExposureRisk(record.getCreditorInfo()));

        ProjReviewBaseInfoDetailRSP rsp = baseInfoConverter.entityToDetailRSP(record);
        //fill name
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());
        sysUserIds.add(rsp.getRiskControlManagerId());
        sysUserIds.add(rsp.getLegalManagerUserId());
        deptIds.add(rsp.getBizDeptId());
        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        rsp.setAssignor(record.getAssignor());
        rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setBizDeptLeaderName(sysUserMap.get(rsp.getBizDeptLeaderId()));
        rsp.setBizDivisionLeaderName(sysUserMap.get(rsp.getBizDivisionLeaderId()));
        rsp.setRiskControlManagerName(sysUserMap.get(rsp.getRiskControlManagerId()));
        rsp.setLegalManagerName(sysUserMap.get(rsp.getLegalManagerUserId()));
        if (ObjectUtil.isNotEmpty(processInstanceId)) {
            ProcessResp process = projReviewService.findProcessByProcessInstanceId(processInstanceId);
            rsp.setProcessModel(process.getModelKey());
        }
        rsp.setIsProjSponsor(Objects.equals(AccountUtil.getLoginInfo().getId(), rsp.getProjSponsorUserId()));
        if (ObjectUtil.isNotEmpty(rsp)) {
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, ListUtil.toList(rsp.getProvince(), rsp.getCity(), rsp.getDistrict())))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
            StringBuilder st = new StringBuilder();
            if (ObjectUtil.isNotNull(nameMap.get(rsp.getProvince()))) {
                st.append(nameMap.get(rsp.getProvince()));
            }
            if (ObjectUtil.isNotNull(nameMap.get(rsp.getCity()))) {
                st.append(nameMap.get(rsp.getCity()));
            }
            if (ObjectUtil.isNotNull(nameMap.get(rsp.getDistrict()))) {
                st.append(nameMap.get(rsp.getDistrict()));
            }
            rsp.setAreaName(st.toString());
            //填充主体名称
            if(ObjectUtil.isNotEmpty(rsp.getEvaluationSubjectId())){
                rsp.setEvaluationSubjectName(id2NameService.clientId2NameSingle(rsp.getEvaluationSubjectId()));
            }
        }
        ProcessResp processResp = projReviewService.findRelatedProcess(id);
        rsp.setCurAssigneeIds(processResp != null ? processResp.getCurAssigneeIds() : null);

        List<Long> projCosponsorUserIds = Optional.ofNullable(rsp.getProjCosponsorUserIds())
                .orElseGet(ArrayList::new).stream().collect(Collectors.toList());
        projCosponsorUserIds.add(rsp.getProjSponsorUserId());
        Date canSeeStartTime = getProjManagerOperateTime(id);
        Date ratingUpdateTime = record.getRatingUpdateTime();
        if(ratingUpdateTime != null) {
            // 取最新的日期
            canSeeStartTime = ratingUpdateTime.compareTo(Optional.ofNullable(canSeeStartTime).orElse(new Date(0L))) >= 0 ? ratingUpdateTime : canSeeStartTime;
        }
//        RatingClientProjDetailRSP clientRSP = ratingClientService.getEffectRating(90, rsp.getEvaluationSubjectId(), null, canSeeStartTime);
        RatingAmountProjDetailRSP amountRSP = ratingAmountService.getEffectRating(null,rsp.getId(), null, canSeeStartTime);
//        // 主承租人评级字段
//        RatingClientProjDetailRSP mainClientRSP = null;
//        if(!Objects.equals(rsp.getEvaluationSubjectId(), rsp.getClientId())){
//            mainClientRSP = ratingClientService.getEffectRating(90, rsp.getClientId(), null, canSeeStartTime);
//        }else{
//            mainClientRSP = clientRSP;
//        }
//        rsp.setRatingClientId();
//        rsp.setRatingFinalScore(clientRSP.getRatingFinalScore());
//        rsp.setRatingMainClientId(mainClientRSP.getId());
//        rsp.setRatingMainFinalScore(mainClientRSP.getRatingFinalScore());
        rsp.setRatingAmountId(amountRSP.getId());
        rsp.setRatingQuota(amountRSP.getRatingQuota());
        return rsp;
    }

    private Date getProjManagerOperateTime(Long id){
        // 查最新的流程
        ProcessResp processRespAndPass = projReviewService.findRelatedAndPassProcess(id);
        if(processRespAndPass == null){
            return null;
        }
        // 查最近一次项目经理操作的时间点
        ProcessHistoryReq historyReq = new ProcessHistoryReq();
        historyReq.setPageIndex(1);
        historyReq.setPageSize(Integer.MAX_VALUE);
        historyReq.setProcessInstanceId(processRespAndPass.getProcessInstanceId());
        cn.zswltech.flow.core.util.Page<ProcessHistoryResp> history = flowProcessApiService.history(historyReq);
        Date finalDate = processRespAndPass.getStartTime();
        if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getContents())) {
            for (ProcessHistoryResp content : history.getContents()) {
                if ("CHFQR".equals(content.getType()) || "BHFQR".equals(content.getType()) || "BHFQR_ZJDW".equals(content.getType())) {
                    finalDate = content.getOperateTime();
                }
            }
        }
        return finalDate;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void disable(ProjReviewBaseInfoRemoveREQ req) {
        if (CollUtil.isEmpty(req.getIds())) {
            return;
        }
        //check if could be close
        List<ProjReviewBaseInfo> infos = baseMapper.selectBatchIds(req.getIds());
        for (ProjReviewBaseInfo info : infos) {
            if (ObjectUtil.equal(info.getProjReviewStatus(), RecordStatus.CLOSED.name()) || ObjectUtil.equals(info.getProjReviewStatus(), RecordStatus.EXPIRE.name())) {
                throw new MithrasException(String.format("项目[%s]评审，请勿重复关闭", info.getProjName()));
            }
            if (!couldBeClosed(info.getId())) {
                errMithras(true, String.format("该项目评审已创建合同，[%s]不能关闭", info.getProjName()));
            }
            stateMachine.execute(ProjContext.of(info, ProjEvent.DISABLE, info.getProcessStatus()));
        }
        // 关闭评审 需要结束审批流程 放在所有校验之后做 因为会发消息等
        for (Long id : req.getIds()) {
            ProcessResp processResp = projReviewService.findRelatedProcess(id);
            if (Objects.nonNull(processResp)) {
                ExecutionProcessBaseREQ cancelProcessReq = new ExecutionProcessBaseREQ();
                cancelProcessReq.setProcessInstanceId(processResp.getProcessInstanceId());
                cancelProcessReq.setMessage("因关闭项目评审，审批自动取消");
                executionService.cancelProcess(cancelProcessReq);
            }
            // 通知客户权限变更
            ApplicationContextUtil.getApplicationContext().publishEvent(
                    new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                            BusinessModuleEnum.PROJ_REVIEW,
                            id.toString(),
                            "项目评审-关闭")
                    )
            );
        }
        ProjReviewBaseInfo toUpdate = new ProjReviewBaseInfo();
        toUpdate.setProjReviewStatus(RecordStatus.CLOSED.name());
        baseMapper.update(toUpdate, Wrappers.<ProjReviewBaseInfo>lambdaUpdate().in(ProjReviewBaseInfo::getId, req.getIds()));
        projClientRoleService.projReviewFinish(req.getIds());
    }

    /**
     * 立项模糊查询
     *
     * @param req
     * @return
     */
    public Map<String, ProjEstablishVagueListRSP> vagueQuery(ProjEstablishVagueListREQ req) {
        Long id = AccountUtil.getLoginInfo().getId();
       /* LambdaQueryWrapper<ProjReviewBaseInfo> queryWrapper = Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjSponsorUserId, id)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .eq(ObjectUtil.isNotNull(req.getProjReviewStatus()), ProjReviewBaseInfo::getProjReviewStatus, req.getProjReviewStatus());
        if (req.getProjVagueName() != null) {
            queryWrapper.like(ProjReviewBaseInfo::getProjName, req.getProjVagueName());
        }
        queryWrapper.orderByDesc(ProjReviewBaseInfo::getId);
        List<ProjReviewBaseInfo> projReviewBaseInfos = baseMapper.selectList(queryWrapper);
*/

        LambdaQueryWrapper<ProjPricingBaseInfo> queryWrapper = Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                .eq(ProjPricingBaseInfo::getProjSponsorUserId, id)
                .notIn(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                .eq(ObjectUtil.isNotNull(req.getProjReviewStatus()), ProjPricingBaseInfo::getProjPricingStatus, req.getProjReviewStatus());
        if (req.getProjVagueName() != null) {
            queryWrapper.like(ProjPricingBaseInfo::getProjName, req.getProjVagueName());
        }
        queryWrapper.orderByDesc(ProjPricingBaseInfo::getId);
        List<ProjPricingBaseInfo> projPricingBaseInfos = projPricingBaseInfoService.list(queryWrapper);

        if (CollectionUtil.isEmpty(projPricingBaseInfos)) {
            return Collections.emptyMap();
        }
        Set<Long> clientIds = projPricingBaseInfos.stream().map(ProjPricingBaseInfo::getClientId).collect(Collectors.toSet());
        List<Client> clientList = clientService.listByClientIds(clientIds);
        Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, e -> e));
        Map<String, ProjEstablishVagueListRSP> projEstablishBaseInfoMap = new HashMap<>();
        for (ProjPricingBaseInfo baseInfo : projPricingBaseInfos) {
            ProjEstablishVagueListRSP one = BeanUtil.copyProperties(baseInfo, ProjEstablishVagueListRSP.class);
            one.setId(baseInfo.getProjReviewId());
            if (Objects.nonNull(baseInfo.getClientId())) {
                Client client = clientMap.get(baseInfo.getClientId());
                if (Objects.nonNull(client)) {
                    one.setClientNames(Collections.singletonList(client.getClientName()));
                }
            }
            projEstablishBaseInfoMap.put(one.getProjName(), one);
        }
        return projEstablishBaseInfoMap;
    }

    private boolean couldBeClosed(Long id) {
        // 被合同模块关联 则不可关闭
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjReviewId, id)
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                .last("LIMIT 1")
        );
        return Objects.isNull(contractBaseInfo);
    }

    public ProjReviewBaseInfo getById(Long id) {
        return baseMapper.selectById(id);
    }

    public void updateDeclaredAmount(Long projectId, Long declaredAmount) {
        baseMapper.updateDeclaredAmount(projectId, declaredAmount);
    }

    public ProcessResp findRelatedProcess(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projReviewId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_REVIEW.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }


    /**
     * 此转换使用mapstruct存在隐患，因此只复制所需的字段
     *
     * @param establishBaseInfo
     * @return ProjReviewBaseInfo
     */
    private ProjReviewBaseInfo establishToReview(ProjEstablishBaseInfo establishBaseInfo) {
        if (establishBaseInfo == null) {
            return null;
        }
        ProjReviewBaseInfo projReviewBaseInfo = new ProjReviewBaseInfo();
        projReviewBaseInfo.setClientId(establishBaseInfo.getClientId());
        projReviewBaseInfo.setBizType(establishBaseInfo.getBizType());
        projReviewBaseInfo.setProjName(establishBaseInfo.getProjName());
        projReviewBaseInfo.setProjCode(establishBaseInfo.getProjCode());
        projReviewBaseInfo.setLeaseTypes(establishBaseInfo.getLeaseTypes());
        projReviewBaseInfo.setProjSource(establishBaseInfo.getProjSource());
        projReviewBaseInfo.setFundsPurpose(establishBaseInfo.getFundsPurpose());
        projReviewBaseInfo.setProjBackground(establishBaseInfo.getProjBackground());
        projReviewBaseInfo.setAssignor(establishBaseInfo.getAssignor());
        projReviewBaseInfo.setLesseeInfo(establishBaseInfo.getLesseeInfo());
        projReviewBaseInfo.setCreditorInfo(establishBaseInfo.getCreditorInfo());
        projReviewBaseInfo.setDebtorInfo(establishBaseInfo.getDebtorInfo());
        projReviewBaseInfo.setGuaranteeInfo(establishBaseInfo.getGuaranteeInfo());
        projReviewBaseInfo.setPledgorInfo(establishBaseInfo.getPledgorInfo());
        projReviewBaseInfo.setMortgagorInfo(establishBaseInfo.getMortgagorInfo());
        projReviewBaseInfo.setProjSponsorUserId(establishBaseInfo.getProjSponsorUserId());
        projReviewBaseInfo.setProjCosponsorUserIds(establishBaseInfo.getProjCosponsorUserIds());
        projReviewBaseInfo.setBizDeptId(establishBaseInfo.getBizDeptId());
        projReviewBaseInfo.setBizDeptLeaderId(establishBaseInfo.getBizDeptLeaderId());
        projReviewBaseInfo.setBizDivisionLeaderId(establishBaseInfo.getBizDivisionLeaderId());
        projReviewBaseInfo.setEvaluationSubjectId(establishBaseInfo.getEvaluationSubjectId());
        projReviewBaseInfo.setRegionalProjectClassify(establishBaseInfo.getRegionalProjectClassify());
        projReviewBaseInfo.setCountry(establishBaseInfo.getCountry());
        projReviewBaseInfo.setProvince(establishBaseInfo.getProvince());
        projReviewBaseInfo.setCity(establishBaseInfo.getCity());
        projReviewBaseInfo.setDistrict(establishBaseInfo.getDistrict());
        projReviewBaseInfo.setSupplierInfo(establishBaseInfo.getSupplierInfo());
//        if (ObjectUtil.isNotEmpty(establishBaseInfo.getRiskControlManagerId())) {
//            List<Long> RiskControlManagerIds = JSON.parseObject(establishBaseInfo.getRiskControlManagerId(), new TypeReference<List<Long>>() {
//            });
//            projReviewBaseInfo.setRiskControlManagerId(RiskControlManagerIds.get(0));
//        }
        // 在项目评审创建时，项目评审详情中「风控经理」字段自动取值评审对应立项审批流程中作为审批人的风控经理
        ProcessPageReq req = new ProcessPageReq();
        req.setPageIndex(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.PASS.getType()));
        req.setModelKey(ProcessModelTypeEnum.ProjEstablishCreateFlow.name());
        req.setBusinessKeyList(Arrays.asList(String.valueOf(establishBaseInfo.getId())));
        //查询立项审批中的流程
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(req);
        if (ObjectUtil.isNotEmpty(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
            processRespPage.getContents().stream()
//                    .filter(processResp -> processResp.getCurTaskActivityIds().equals("userTask_riskManager"))
                    .findFirst().ifPresent(processResp -> {
                projReviewBaseInfo.setRiskControlManagerId(Long.valueOf(processResp.getLastOperatorId()));
            });
        }

        //根据第一个承租人/债权人区分
        projReviewBaseInfo.setEnterpriseNature(getEnterpriseNatureById(establishBaseInfo.getClientId()));
        return projReviewBaseInfo;
    }

    public List<ProjReviewBaseInfo> listByClients(List<Long> clientIdList) {
        return this.getBaseMapper().selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .in(ProjReviewBaseInfo::getClientId, clientIdList)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
        );
    }


    public ProjReviewBaseInfo getReviewByPricing(Long establishId, Long groupReviewId,String projName) {
        return this.getBaseMapper().selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(establishId != null, ProjReviewBaseInfo::getProjEstablishId, establishId)
                .eq(groupReviewId != null, ProjReviewBaseInfo::getGroupCreditReviewId, groupReviewId)
                .eq(ProjReviewBaseInfo::getProjName, projName)
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
        );
    }

    public ProjReviewBaseInfo getReviewByPricing(ProjPricingBaseInfo pricingBaseInfo) {
        return getReviewByPricing(pricingBaseInfo.getProjEstablishId(), pricingBaseInfo.getGroupCreditReviewId(), pricingBaseInfo.getProjName());
    }



    //查找有关联的承租人，担保人
    public List<ProjReviewBaseInfo> listRelationByClients(Long clientId) {
        return projReviewBaseInfoMapper.listRelationByClients(clientId);
    }

    private String getEnterpriseNatureById(Long clientId) {
        CorpCommerceInfo detail = commerceInfoService.detail(clientId, null);
        if (ObjectUtil.isNull(detail)) {
            return null;
        }
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibHandler.queryLatestDataByOriginId(detail.getId());
        return ObjectUtil.isNotNull(corpCommerceInfoLib) ? corpCommerceInfoLib.getEnterpriseNature() : null;
    }

    public List<ProjReviewBaseInfo> listByProjEstablishIds(List<Long> projEstablishIdList) {
        if (CollUtil.isEmpty(projEstablishIdList)) {
            return new ArrayList<>();
        }
        return this.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, CLOSED.name(), INVALID.name(), EXPIRE.name())
                .in(ProjReviewBaseInfo::getProjEstablishId, projEstablishIdList));
    }


    /**
     * 获得客户管护权后，超过60天仍未有立项审批通过的项目，系统自动释放该客户权限
     *
     * @param day           天数
     * @param projReviewIds 项目评审ids
     * @param isNotice      是否通知 ture通知 false 修改
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void releaseClientProjEstablish(int day, List<Long> projReviewIds, boolean isNotice) {
        LocalDate localDate = LocalDate.now().minusDays(day+1);
        Date beganDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(localDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                .eq(ClientAuthority::getDeleted, 0)
                .gt(ClientAuthority::getUpdateTime, beganDate)
                .lt(ClientAuthority::getUpdateTime, endDate));
        if (clientAuthorityList == null || clientAuthorityList.isEmpty()) {
            return;
        }
        List<Long> clientIds = clientAuthorityList.stream().map(ClientAuthority::getClientId).distinct().collect(Collectors.toList());
        List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIds));
        for (Client client : clientList) {
            for (ClientAuthority clientAuthority : clientAuthorityList) {
                if (client.getId().equals(clientAuthority.getClientId())) {
                    List<ProjEstablishBaseInfo> projEstablishBaseInfoList = released(client, clientAuthority);
                    if (projEstablishBaseInfoList != null && !projEstablishBaseInfoList.isEmpty()) {
                        List<Long> ids = projEstablishBaseInfoList.stream().map(ProjEstablishBaseInfo::getId).distinct().collect(Collectors.toList());
                        ProjReviewBaseInfoRemoveREQ req = new ProjReviewBaseInfoRemoveREQ();
                        req.setIds(ids);
                        disable(req);
                    }
                }
            }
        }
    }



    /**
     * 合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
     *
     * @param day           天数
     * @param projReviewIds 项目评审ids
     * @param isNotice      是否通知 ture通知 false 修改
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void releaseClientContractSettle(int day, List<Long> projReviewIds, boolean isNotice) {
        List<ContractBaseInfo> contractBaseInfoList = dayBeforeContractEffect(day, null, true);
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            //没有过期的项目立项，直接结束
            return;
        }
        Map<Long, List<ContractBaseInfo>> contractBaseInfoMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        Set<Long> nonRemoveClientids = new HashSet<>();
        Map<Long, List<ContractBaseInfo>> resMap = new HashMap<>();
        for (Map.Entry<Long, List<ContractBaseInfo>> entry : contractBaseInfoMap.entrySet()) {
            Long clientId = entry.getKey();
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getClientId, clientId));
            if (ObjectUtil.isEmpty(contractBaseInfos)) {
                continue;
            }
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                if (!ContractStatus.SETTLE.name().equalsIgnoreCase(contractBaseInfo.getContractStatus())) {
                    nonRemoveClientids.add(clientId);
                    break;
                }
            }
            resMap.putIfAbsent(clientId, new ArrayList<>());
            resMap.get(clientId).addAll(contractBaseInfos);
        }
        Set<Long> keySet = contractBaseInfoMap.keySet();
        if (!nonRemoveClientids.isEmpty()) {
            keySet.removeAll(nonRemoveClientids);
        }

        Set<Long> allClientIds = new HashSet<>();
        for (Long clientId : keySet) {
            LocalDateTime latestTime = null;
            List<ContractBaseInfo> contractBaseInfos = resMap.get(clientId);
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                LocalDateTime date = contractBaseInfo.getSettleTime();
                if (latestTime == null || (latestTime != null && date.isAfter(latestTime))) {
                    latestTime = date;
                }
            }
            List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .in(ProjEstablishBaseInfo::getClientId, clientId));
            for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
                if (projEstablishBaseInfo.getCreateTime().isAfter(latestTime) && RecordStatus.TAKE_EFFECT.name().equalsIgnoreCase(projEstablishBaseInfo.getProjEstablishStatus())) {
                    break;
                }
            }
            allClientIds.add(clientId);
        }
        if (!isNotice && !allClientIds.isEmpty()) {
            List<ClientAuthority> clientAuthorityList = clientAuthorityMapper.selectList(Wrappers.<ClientAuthority>lambdaQuery()
                    .eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel())
                    .in(ClientAuthority::getClientId, allClientIds)
                    .eq(ClientAuthority::getDeleted, 0));
            List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getId, allClientIds));
            for (Client client : clientList) {
                for (ClientAuthority clientAuthority : clientAuthorityList) {
                    if (client.getId().equals(clientAuthority.getClientId())) {
                        releaseClient(client, clientAuthority);
                    }
                }
            }
            //todo 调整客户类型
            //clientService.updateClientAuth(allClientIds, ClientAuthEnum.HIGH_SEAS.name());
        }
    }


    private List<ProjEstablishBaseInfo> released(Client client, ClientAuthority clientAuthority) {
        boolean released = true;
        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getClientId, client.getId()));
        if (projEstablishBaseInfoList.isEmpty()) {
            releaseClient(client, clientAuthority);
            return projEstablishBaseInfoList;
        }
        //客户N个项目有一个生效就不释放
        for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
            if (RecordStatus.TAKE_EFFECT.name().equalsIgnoreCase(projEstablishBaseInfo.getProjEstablishStatus())) {
                released = false;
            }
        }
        if (released) {
            releaseClient(client, clientAuthority);
            return projEstablishBaseInfoList;
        }
        return null;
    }

    private void releaseClient(Client client, ClientAuthority clientAuthority) {
        Long startUserId = clientAuthority.getUserId();
        //更改管护权为查看权
        clientAuthority.setLevel(ClientLevelEnum.VIEW.getLevel());
        clientAuthorityMapper.updateById(clientAuthority);
        //更改客户释放状态
        client.setClientStatus(ClientStatus.NEW.name());
        client.setIsReleased(YesOrNoNumberEnum.YES.getCode());
        client.setBelongDeptId(null);
        client.setBelongSponsorId(null);
        clientMapper.updateAnnotationIncludeNullById(client);
    }

    public ProjReviewBaseInfoUpdateRatingRSP updateRating(ProjReviewBaseInfoUpdateRatingREQ req) {
        ProjReviewBaseInfoUpdateRatingRSP rsp = new ProjReviewBaseInfoUpdateRatingRSP();
        Date date = new Date();
        ProjReviewBaseInfo reviewBaseInfo = this.getById(req.getId());
        if(reviewBaseInfo != null){
//            if(reviewBaseInfo.getEvaluationSubjectId() == null){
//                throw new MithrasException("评估主体不得为空");
//            }
            RatingClientProjDetailRSP flushAfterClient = ratingClientService.getEffectRating(90, reviewBaseInfo.getEvaluationSubjectId(), null, date);
            RatingClientProjDetailRSP flushAfterMainLessee = ratingClientService.getEffectRating(90, reviewBaseInfo.getClientId(), null, date);
            RatingAmountProjDetailRSP flushAfterAmount = ratingAmountService.getEffectRating(90 ,reviewBaseInfo.getId(),null, date);
            LambdaUpdateWrapper<ProjReviewBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(ProjReviewBaseInfo::getId, req.getId());
            updateWrapper.set(ProjReviewBaseInfo::getRatingUpdateTime, new Date());
            boolean isUpdate = false;
            if(flushAfterClient != null){
                updateWrapper.set(ProjReviewBaseInfo::getEvaluationSubjectRatingScoreId, flushAfterClient.getId());
                updateWrapper.set(ProjReviewBaseInfo::getEvaluationSubjectRatingScore, flushAfterClient.getRatingFinalScore());
                if(req.getRatingClientId() != null && !Objects.equals(flushAfterClient.getId(), req.getRatingClientId())){
                    isUpdate = true;
                }
                rsp.setRatingClientId(flushAfterClient.getId());
                rsp.setRatingFinalScore(flushAfterClient.getRatingFinalScore());
            }
            if (flushAfterMainLessee != null) {
                updateWrapper.set(ProjReviewBaseInfo::getMainLesseeRatingScoreId, flushAfterMainLessee.getId());
                updateWrapper.set(ProjReviewBaseInfo::getMainLesseeRatingScore, flushAfterMainLessee.getRatingFinalScore());
                isUpdate = true;
                rsp.setRatingMainLesseeId(flushAfterMainLessee.getId());
                rsp.setRatingMainLesseeScore(flushAfterMainLessee.getRatingFinalScore());
            }
            if(flushAfterAmount != null){
                if(req.getRatingAmountId() != null && !Objects.equals(flushAfterAmount.getId(), req.getRatingAmountId())){
                    isUpdate = true;
                }
                rsp.setRatingAmountId(flushAfterAmount.getId());
                rsp.setRatingQuota(flushAfterAmount.getRatingQuota());
            }
            this.update(updateWrapper);
            // 更新状态
            if(isUpdate && Objects.equals(reviewBaseInfo.getProcessStatus(), ProjProcessState.NEW_APPROVAL_PASS.name())){
                recordStatus(req.getId());
            }
        }
        return rsp;
    }
}