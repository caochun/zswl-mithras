package cn.zswltech.mithras.application.orchestration.fund;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import cn.zswltech.mithras.credit.creditlimit.service.CreditLimitManagerService;
import cn.zswltech.mithras.credit.creditlimit.service.CreditLimitService;
import cn.zswltech.mithras.credit.creditlimit.service.bo.*;
import cn.zswltech.mithras.credit.creditlimit.service.port.FundCreditEffectiveStatusService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.convert.TypeConversionWorker;
import cn.zswltech.mithras.fund.application.credit.convert.FundCreditConverter;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.fund.application.credit.FundCreditGuaranteeDetailService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.fund.enums.OrganizationType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.persistence.mapper.credit.FundCreditMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.fund.persistence.model.credit.FundCredit;
import cn.zswltech.mithras.fund.persistence.model.credit.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.fund.persistence.model.credit.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.persistence.model.organization.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.credit.creditlimit.service.CreditBusinessRefService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.foundation.bo.*;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingBaseInfoLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 授信
 * @date 2022-12-13
 */
@Slf4j
@Service
public class FundCreditService extends ServiceImpl<FundCreditMapper, FundCredit> implements FundCreditEffectiveStatusService {
    @Resource
    private FundCreditConverter baseConverter;
    @Resource
    private FundCreditGuaranteeDetailService guaranteeDetailService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private TypeConversionWorker conversionWorker;
    @Resource
    private FundGuaranteeAgencyService guaranteeAgencyService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoLibService financingBaseInfoLibService;
    @Resource
    private FundReceiptRepayCashFlowService receiptRepayCashFlowService;
    @Resource
    private CreditLimitService creditLimitService;
    @Resource
    private CreditBusinessRefService creditBusinessRefService;
    @Resource
    private CreditLimitManagerService creditLimitManagerService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void invalidExpiredFundCredit(LocalDate now) {
        this.update(Wrappers.<FundCredit>lambdaUpdate()
                .lt(FundCredit::getEffectiveDateTo, now)
                .eq(FundCredit::getEffective, Boolean.TRUE)
                .set(FundCredit::getEffective, Boolean.FALSE));
    }

    public List<FundCreditListRSP.FundCreditList> listEffectByOrgId(Long orgId, Boolean effective) {
        LambdaQueryWrapper<FundCredit> query = Wrappers.lambdaQuery();
        query.eq(FundCredit::getOrganizationId, orgId);
        LocalDate now = LocalDate.now();
        query.le(FundCredit::getEffectiveDateFrom, now);
        query.ge(FundCredit::getEffectiveDateTo, now);
        query.orderByDesc(FundCredit::getId);
        query.eq(Objects.nonNull(effective), FundCredit::getEffective, effective);
        List<FundCredit> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
//        Map<Long, LimitDto> creditId2LimitDtoMap = calculateEveryLimit(list.stream().map(FundCredit::getId).collect(Collectors.toSet()));
        Map<Long, CreditLimitDetailBO> creditLimitMap = queryLimitDetailBatch(list, false);
        return list.stream().map(item -> {
            FundCreditListRSP.FundCreditList rsp = baseConverter.entity2ListRsp(item);
            CreditLimitDetailBO creditLimit = creditLimitMap.getOrDefault(item.getId(), new CreditLimitDetailBO());
            rsp.setUsedTotalCreditAmount(creditLimit.getOccupyTotalLimit());
            rsp.setRemainingTotalLimit(item.getTotalCreditLimit() - Optional.ofNullable(creditLimit).map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L));
//            this.calculateCreditLimit(rsp, rsp.getId(), rsp.getTotalCreditLimit());
            return rsp;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long add(FundCreditAddREQ req) {
        // check机构是否存在老的授信
        FundCredit oldCredits = getOne(Wrappers.<FundCredit>lambdaQuery()
                .eq(FundCredit::getOrganizationId, req.getOrganizationId())
                .eq(FundCredit::getEffective, true).last("limit 1"));
        // 插入新的授信
        FundCredit newCredit;
        if (oldCredits != null) {
//            if (oldCredits.getRecyclable() == 1) {
//                throw new MithrasException("该机构已存在已生效的循环授信，请至详情页修改原授信！");
//            }
            newCredit = new FundCredit();
            BeanUtil.copyProperties(oldCredits, newCredit);
            newCredit.setId(null);
            newCredit.setTotalCreditLimit(req.getTotalCreditLimit());
            newCredit.setEffectiveDateFrom(req.getEffectiveDateFrom());
            newCredit.setEffectiveDateTo(req.getEffectiveDateTo());
        } else {
            newCredit = baseConverter.addReq2Entity(req);
        }
        newCredit.setFundManager(AccountUtil.getLoginInfo().getId());
//        Integer totalNow = baseMapper.selectCount(
//                Wrappers.<FundCredit>lambdaQuery().isNotNull(FundCredit::getId));
//        newCredit.setCreditCode("DK" + LocalDate.now().format(df) + String.format("%04d", totalNow + 1));
        newCredit.setEffective(true);
        baseMapper.insert(newCredit);
        // 直接取自增id拼接编号，避免授信被删除后 编号重复问题
        newCredit.setCreditCode("DK" + LocalDate.now().format(df) + String.format("%04d", newCredit.getId()));
        baseMapper.updateById(newCredit);

        // 需要将原有授信失效
        if (oldCredits != null) {
            List<FundCredit> originalCredits = list(Wrappers.<FundCredit>lambdaQuery()
                    .eq(FundCredit::getOrganizationId, req.getOrganizationId())
                    .ne(FundCredit::getId, newCredit.getId()));
            if (CollectionUtil.isNotEmpty(originalCredits)) {
                // 原有授信需要失效
                for (FundCredit originalCredit : originalCredits) {
                    originalCredit.setEffective(false);
                }
                updateBatchById(originalCredits);

                Set<Long> creditIds = originalCredits.stream().map(FundCredit::getId).collect(Collectors.toSet());
                //担保关联更新
                guaranteeDetailService.update(null, Wrappers.<FundCreditGuaranteeDetail>lambdaUpdate()
                        .set(FundCreditGuaranteeDetail::getCreditId, newCredit.getId())
                        .in(FundCreditGuaranteeDetail::getCreditId, creditIds));
                CreditLimitExpireBO expireBO = new CreditLimitExpireBO();
                expireBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
                expireBO.setGrantSubjectKey(String.valueOf(oldCredits.getOrganizationId()));
                expireBO.setBizSourceKey(String.valueOf(oldCredits.getId()));
                creditLimitManagerService.expire(expireBO);
            }
            // 将未结清合同挂在新的授信下
            List<FundFinancingCreditRef> refList = financingCreditRefService.queryByCreditId(oldCredits.getId());
            if(CollectionUtil.isNotEmpty(refList)) {
                List<FundFinancingBaseInfo> noSettleFinancingList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .select(FundFinancingBaseInfo::getId)
                        .ne(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.SETTLE.name())
                        .in(FundFinancingBaseInfo::getId, refList.stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList())));
                if(CollectionUtil.isNotEmpty(noSettleFinancingList)) {
                    financingCreditRefService.updateRef(oldCredits.getId(), newCredit.getId(), noSettleFinancingList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList()));
                }
            }

        }
        return newCredit.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundCreditModifyREQ req) {
        FundCredit originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundCredit info = baseConverter.modifyReq2Entity(req);
        // 批量更新担保详情
        if (ObjectUtil.isNotEmpty(req.getGuaranteeDetail())) {
            Map<Long, FundCreditGuaranteeDetail> map = guaranteeDetailService.getByCreditId(originalInfo.getId())
                    .stream().collect(Collectors.toMap(FundCreditGuaranteeDetail::getId, item -> item));
            for (FundCreditGuaranteeDetailDto detail : req.getGuaranteeDetail()) {
                if (detail.getId() != null && map.containsKey(detail.getId())) {
                    map.remove(detail.getId());
                }
            }
            guaranteeDetailService.removeByIds(map.keySet());
            List<FundCreditGuaranteeDetail> details = baseConverter.detailDto2Entity(req.getGuaranteeDetail());
            details.forEach(item -> {
                if (ObjectUtil.isEmpty(item.getCreditId())) {
                    item.setCreditId(originalInfo.getId());
                }
            });
            guaranteeDetailService.saveOrUpdateBatch(details);
        } else {
            guaranteeDetailService.remove(Wrappers.<FundCreditGuaranteeDetail>lambdaQuery().eq(FundCreditGuaranteeDetail::getCreditId, originalInfo.getId()));
        }
        baseMapper.updateById(info);
        info.setOrganizationId(originalInfo.getOrganizationId());
        creditCreateOrUpdate(info, req.getGuaranteeDetail());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void creditCreateOrUpdate(FundCredit fundCredit, List<FundCreditGuaranteeDetailDto> guaranteeDetail) {
        Long guaranteeLimit = 0L;
        if(CollectionUtil.isNotEmpty(guaranteeDetail)){
            guaranteeLimit = guaranteeDetail.stream().mapToLong(FundCreditGuaranteeDetailDto::getGuaranteeAmount).sum();
        }
        if(queryLimitDetail(fundCredit, false) == null){
            CreditLimitCreateBO creditLimitCreateBO = new CreditLimitCreateBO();
            creditLimitCreateBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            creditLimitCreateBO.setGrantSubjectKey(String.valueOf(fundCredit.getOrganizationId()));
            creditLimitCreateBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
            creditLimitCreateBO.setTotalLimit(fundCredit.getTotalCreditLimit());
            creditLimitCreateBO.setGuaranteeLimit(guaranteeLimit);
            creditLimitCreateBO.setCreditLimit(fundCredit.getTotalCreditLimit() - guaranteeLimit);
            creditLimitCreateBO.setEffectiveDateFrom(fundCredit.getEffectiveDateFrom());
            creditLimitCreateBO.setEffectiveDateTo(fundCredit.getEffectiveDateTo());
            creditLimitCreateBO.setRecyclable(fundCredit.getRecyclable());
            // 非循环授信翻单时，要根据剩余本金重新占用
            if(Objects.equals(fundCredit.getRecyclable(), YesOrNoNumberEnum.NO.getCode())){
                List<FundFinancingCreditRef> refList = financingCreditRefService.queryByCreditId(fundCredit.getId());
                Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(refList.stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList()), FinancingTypeEnum.INDIRECT);
                creditLimitCreateBO.setRemainingAmountMap(remainingAmountMap);
            }
            creditLimitManagerService.create(creditLimitCreateBO);
        }else{
            CreditLimitModifyBO creditLimitModifyBO = new CreditLimitModifyBO();
            creditLimitModifyBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            creditLimitModifyBO.setGrantSubjectKey(String.valueOf(fundCredit.getOrganizationId()));
            creditLimitModifyBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
            creditLimitModifyBO.setTotalLimit(fundCredit.getTotalCreditLimit());
            creditLimitModifyBO.setGuaranteeLimit(guaranteeLimit);
            creditLimitModifyBO.setCreditLimit(fundCredit.getTotalCreditLimit() - guaranteeLimit);
            creditLimitModifyBO.setEffectiveDateFrom(fundCredit.getEffectiveDateFrom());
            creditLimitModifyBO.setEffectiveDateTo(fundCredit.getEffectiveDateTo());
//            creditLimitModifyBO.setRecyclable(fundCredit.getRecyclable());
            creditLimitManagerService.modify(creditLimitModifyBO);
        }

    }

    public FundCreditListRSP list(FundCreditListREQ req) {
        FundCreditListRSP listrsp = new FundCreditListRSP();
        LambdaQueryWrapper<FundCredit> qw = Wrappers.<FundCredit>lambdaQuery()
                .eq(ObjectUtil.isNotNull(req.getEffective()), FundCredit::getEffective, req.getEffective())
                .eq(ObjectUtil.isNotNull(req.getOrganizationId()), FundCredit::getOrganizationId, req.getOrganizationId())
                .ge(ObjectUtil.isNotNull(req.getCreditLimitFrom()), FundCredit::getTotalCreditLimit, req.getCreditLimitFrom())
                .le(ObjectUtil.isNotNull(req.getCreditLimitTo()), FundCredit::getTotalCreditLimit, req.getCreditLimitTo())
                .ge(ObjectUtil.isNotNull(req.getCreditDateFrom()), FundCredit::getEffectiveDateFrom, req.getCreditDateFrom())
                .le(ObjectUtil.isNotNull(req.getCreditDateTo()), FundCredit::getEffectiveDateFrom, req.getCreditDateTo())
                .eq(ObjectUtil.isNotNull(req.getCreateBy()), FundCredit::getCreateBy, req.getCreateBy())
                .ge(ObjectUtil.isNotNull(req.getCreateTimeFrom()), FundCredit::getCreateTime,
                        conversionWorker.startOfDay(req.getCreateTimeFrom()))
                .le(ObjectUtil.isNotNull(req.getCreateTimeTo()), FundCredit::getCreateTime,
                        conversionWorker.endOfDay(req.getCreateTimeTo()));
        if (ObjectUtil.isNotEmpty(req.getOrganizationType())) {
            List<Long> idsByType = organizationService.getIdsByType(req.getOrganizationType());
            if (ObjectUtil.isNotEmpty(idsByType)) {
                qw.in(FundCredit::getOrganizationId, idsByType);
            }
        }
        qw.orderByDesc(BaseModel::getUpdateTime);
        Page<FundCredit> fundCreditPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), qw);
        Page<FundCredit> fundCreditPageAll = baseMapper.selectPage(new Page<>(1, Integer.MAX_VALUE), qw);
        if (ObjectUtil.isEmpty(fundCreditPage.getRecords())) {
            listrsp.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return listrsp;
        }
        // 计算各种额度
        Set<Long> creditIds = fundCreditPageAll.getRecords().stream().map(FundCredit::getId).collect(Collectors.toSet());
        Map<Long, List<FundFinancingCreditRef>> refList = financingCreditRefService.queryBatchByCreditId(creditIds);
        Map<Long, Long> remainingAmountCredit = new HashMap<>();
        if(CollectionUtil.isNotEmpty(refList)) {
            Set<Long> financingIds = refList.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toSet());
            Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIds, FinancingTypeEnum.INDIRECT);
            remainingAmountCredit = refList.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                Set<Long> financingIdSet = Optional.ofNullable(entry.getValue()).map(m -> m.stream()
                        .map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toSet())).orElse(Collections.emptySet());
                return financingIdSet.stream().map(remainingAmountMap::get).filter(Objects::nonNull).mapToLong(f -> f).sum();
            }));
        }
//        Map<Long, LimitDto> creditId2LimitDtoMap = calculateEveryLimit(fundCreditPageAll.getRecords());
        Map<Long, CreditLimitDetailBO> limitDetailMap = queryLimitDetailBatch(fundCreditPageAll.getRecords(), false);
        List<Long> createByIds = new ArrayList<>();
        List<Long> organizationIds = new ArrayList<>();
        fundCreditPageAll.getRecords().forEach(fundCredit -> {
            createByIds.add(fundCredit.getCreateBy());
            organizationIds.add(fundCredit.getOrganizationId());
        });

        Set<Long> pageSet = fundCreditPage.getRecords().stream().map(FundCredit::getId).collect(Collectors.toSet());

        Map<Long, String> fcMap = getCreditBizType(pageSet);

//        creditLimitService.findByThreeKeys()
//        List<FundFinancingBaseInfo> financingBaseInfos = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().in(FundFinancingBaseInfo::getCreditId, pageSet).ne(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CLOSE.name()));
//        Map<Long, List<FundFinancingBaseInfo>> fcMap = new HashMap<>();
//        if (CollUtil.isNotEmpty(financingBaseInfos)) {
//            fcMap = financingBaseInfos.stream().collect(Collectors.groupingBy(FundFinancingBaseInfo::getCreditId));
//        }
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(createByIds);
        Map<Long, FundOrganization> fundOrganizationMap = organizationService.getBaseMapper()
                .selectBatchIds(organizationIds).stream()
                .collect(Collectors.toMap(FundOrganization::getId, item -> item));

        List<FundCreditListRSP.FundCreditList> rspList = new ArrayList<>();
        FundCreditListRSP.Sum sum = new FundCreditListRSP.Sum();
        for (FundCredit fundCredit : fundCreditPageAll.getRecords()) {
            FundCreditListRSP.FundCreditList rsp = baseConverter.entity2ListRsp(fundCredit);
//            // 为了复用代码，计算剩余额度抽取成方法
//            this.calculateCreditLimit(rsp, rsp.getId(), rsp.getTotalCreditLimit());
            rsp.setCreateByName(userId2Name.get(rsp.getCreateBy()));
            if (ObjectUtil.isNotEmpty(fundOrganizationMap.get(rsp.getOrganizationId()))) {
                rsp.setOrganizationName(fundOrganizationMap.get(rsp.getOrganizationId()).getOrganizationName());
            }
            CreditLimitDetailBO limitDetail = limitDetailMap.getOrDefault(fundCredit.getId(), new CreditLimitDetailBO());
            rsp.setTotalCreditLimit(limitDetail.getTotalLimit());
            rsp.setGuaranteeAmount(limitDetail.getGuaranteeLimit());
            rsp.setCreditLimit(limitDetail.getCreditLimit());
            rsp.setUsedTotalCreditAmount(limitDetail.getOccupyTotalLimit());
            rsp.setUsedGuaranteeAmount(limitDetail.getOccupyGuaranteeLimit());
            rsp.setUsedCreditAmount(limitDetail.getOccupyCreditLimit());
            rsp.setRemainingTotalLimit(Optional.ofNullable(limitDetail.getTotalLimit()).orElse(0L) - Optional.ofNullable(limitDetail.getOccupyTotalLimit()).orElse(0L));
            rsp.setRemainingLimit(rsp.getRemainingTotalLimit());
            rsp.setRemainingGuaranteeAmount(Optional.ofNullable(limitDetail.getGuaranteeLimit()).orElse(0L) - Optional.ofNullable(limitDetail.getOccupyGuaranteeLimit()).orElse(0L));
            rsp.setRemainingCreditAmount(Optional.ofNullable(limitDetail.getCreditLimit()).orElse(0L) - Optional.ofNullable(limitDetail.getOccupyCreditLimit()).orElse(0L));
            rsp.setRemainingAmount(remainingAmountCredit.get(fundCredit.getId()));
//            LimitDto limitDto = creditId2LimitDtoMap.getOrDefault(fundCredit.getId(), new LimitDto());
//            if (ObjectUtil.isEmpty(fundCredit.getRecyclable())) {
//                // do nothing
//            } else if (fundCredit.getRecyclable() == 1) {
//                rsp.setUsedTotalCreditAmount(limitDto.getTotalUsedLimit(1));
//                rsp.setUsedGuaranteeAmount(limitDto.getTotalUsedGuaranteeLimit(1));
//                rsp.setUsedCreditAmount(rsp.getUsedTotalCreditAmount() - rsp.getUsedGuaranteeAmount());
//                rsp.setRemainingTotalLimit(fundCredit.getTotalCreditLimit() - rsp.getUsedTotalCreditAmount());
//                rsp.setRemainingCreditAmount(rsp.getCreditLimit() - rsp.getUsedCreditAmount());
//                rsp.setRemainingGuaranteeAmount(rsp.getGuaranteeAmount() - rsp.getUsedGuaranteeAmount());
//                rsp.setRemainingLimit(rsp.getRemainingTotalLimit());
//            } else if (fundCredit.getRecyclable() == 0) {
//                // 计算总额度需要按照可循环的方式来计算
//                rsp.setUsedTotalCreditAmount(limitDto.getTotalUsedLimit(1));
//                rsp.setUsedGuaranteeAmount(limitDto.getTotalUsedGuaranteeLimit(1));
//                rsp.setUsedCreditAmount(rsp.getUsedTotalCreditAmount() - rsp.getUsedGuaranteeAmount());
//                // 计算剩余额度需要按照不可循环的方式来计算
//                rsp.setRemainingTotalLimit(fundCredit.getTotalCreditLimit() - limitDto.getTotalUsedLimit(0));
//                rsp.setRemainingGuaranteeAmount(rsp.getGuaranteeAmount() - limitDto.getTotalUsedGuaranteeLimit(0));
//                rsp.setRemainingCreditAmount(rsp.getRemainingTotalLimit() - rsp.getRemainingGuaranteeAmount());
//                rsp.setRemainingLimit(rsp.getRemainingTotalLimit());
//            }
            LocalDate effectiveDateTo = fundCredit.getEffectiveDateTo();
            if (ObjectUtil.isNotEmpty(effectiveDateTo)) {
                long effectDays = ChronoUnit.DAYS.between(LocalDate.now(), effectiveDateTo);
                if (effectDays > 0) {
                    rsp.setRemainingDays(effectDays);
                } else {
                    rsp.setRemainingDays(0L);
                }
            }
            sum.setTotalCreditLimit(LongUtil.null2zero(sum.getTotalCreditLimit()) + LongUtil.null2zero(rsp.getTotalCreditLimit()));
            sum.setGuaranteeAmount(LongUtil.null2zero(sum.getGuaranteeAmount()) + LongUtil.null2zero(rsp.getGuaranteeAmount()));
            sum.setCreditLimit(LongUtil.null2zero(sum.getCreditLimit()) + LongUtil.null2zero(rsp.getCreditLimit()));
            sum.setUsedTotalCreditAmount(LongUtil.null2zero(sum.getUsedTotalCreditAmount()) + LongUtil.null2zero(rsp.getUsedTotalCreditAmount()));
            sum.setUsedCreditAmount(LongUtil.null2zero(sum.getUsedCreditAmount()) + LongUtil.null2zero(rsp.getUsedCreditAmount()));
            sum.setUsedGuaranteeAmount(LongUtil.null2zero(sum.getUsedGuaranteeAmount()) + LongUtil.null2zero(rsp.getUsedGuaranteeAmount()));
            sum.setRemainingTotalLimit(LongUtil.null2zero(sum.getRemainingTotalLimit()) + LongUtil.null2zero(rsp.getRemainingTotalLimit()));
            sum.setRemainingLimit(LongUtil.null2zero(sum.getRemainingLimit()) + LongUtil.null2zero(rsp.getRemainingLimit()));
            sum.setRemainingCreditAmount(LongUtil.null2zero(sum.getRemainingCreditAmount()) + LongUtil.null2zero(rsp.getRemainingCreditAmount()));
            sum.setRemainingGuaranteeAmount(LongUtil.null2zero(sum.getRemainingGuaranteeAmount()) + LongUtil.null2zero(rsp.getRemainingGuaranteeAmount()));
            sum.setRemainingAmount(LongUtil.null2zero(sum.getRemainingAmount()) + LongUtil.null2zero(rsp.getRemainingAmount()));
            if (pageSet.contains(rsp.getId())) {
                rsp.setCreditType(fcMap.get(rsp.getId()));
                rspList.add(rsp);
            }
        }
        listrsp.setRecords(PageR.of(fundCreditPage, rspList));
        listrsp.setSum(sum);
        return listrsp;
    }

    private Map<Long, String> getCreditBizType(Set<Long> pageSet) {
        Map<Long, List<FundFinancingCreditRef>> creditIdMap = financingCreditRefService.queryBatchByCreditId(pageSet);
        // 保持原逻辑
        if(CollectionUtil.isEmpty(creditIdMap)){
            return Collections.emptyMap();
        }
        List<Long> financingIdList = creditIdMap.values().stream().map(m -> m.get(m.size() - 1)).map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList());
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.listByIds(financingIdList);
        Map<Long, String> businessTypeMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, FundFinancingBaseInfo::getBusinessType));

        return creditIdMap.keySet().stream().collect(Collectors.toMap(m -> m, m ->{
            FundFinancingCreditRef ref = creditIdMap.get(m).get(creditIdMap.get(m).size() - 1 );
            return businessTypeMap.get(ref.getFinancingId());
        }, (m1, m2) -> m1));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FundCreditRemoveREQ req) {
        if (ObjectUtil.isNotEmpty(req.getIds())) {
            baseMapper.deleteBatchIds(req.getIds());
            guaranteeDetailService.remove(Wrappers.<FundCreditGuaranteeDetail>lambdaQuery()
                    .in(FundCreditGuaranteeDetail::getCreditId, req.getIds()));
        }
    }

    public FundCreditDetailRSP detail(Long id) {
        FundCredit fundCredit = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(fundCredit)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundCreditDetailRSP rsp = baseConverter.entity2DetailRsp(fundCredit);
        // 填充授信机构信息
        FundOrganization organization = organizationService.getById(rsp.getOrganizationId());
        rsp.setOrganizationName(organization.getOrganizationName());
        if (OrganizationType.ZL.name().equals(organization.getOrganizationType())) {
            rsp.setOrganizationCode(organization.getUscCode());
        }
        if (OrganizationType.BANK.name().equals(organization.getOrganizationType())) {
            rsp.setOrganizationCode(organization.getInterBankNo());
        }
        // 查询担保明细
        List<FundCreditGuaranteeDetail> byCreditId = guaranteeDetailService.getByCreditId(id);
        List<FundCreditGuaranteeDetailDto> detailDtos = baseConverter.entity2DetailDto(byCreditId);
        detailDtos.forEach(detailDto -> {
            FundGuaranteeAgency guaranteeAgency = guaranteeAgencyService.getById(detailDto.getGuaranteeAgencyId());
            detailDto.setGuaranteeAgencyName(guaranteeAgency.getGuaranteeAgencyName());
            Optional<Long> totalGuaranteeLimit = guaranteeAgencyService.getTotalGuaranteeLimit(detailDto.getGuaranteeAgencyId());
            Map<Long, Long> usedGuaranteeLimit = guaranteeAgencyService.getUsedGuaranteeLimit(Collections.singletonList(detailDto.getGuaranteeAgencyId()));
            if (totalGuaranteeLimit.isPresent()) {
                detailDto.setRemainingLimit(totalGuaranteeLimit.get()
                        - LongUtil.null2zero(usedGuaranteeLimit.get(detailDto.getGuaranteeAgencyId())));
            } else {
                detailDto.setRemainingLimit(0L);
            }
        });
        rsp.setGuaranteeDetail(detailDtos);

        // 填充资金经理相关信息
        String userName = sysUserService.getUserName(fundCredit.getCreateBy());
        rsp.setFundManagerName(userName);

        List<OrgDO> specificUserDeptList = sysUserService.getSpecificUserDeptList(fundCredit.getCreateBy());
        if (ObjectUtil.isNotEmpty(specificUserDeptList)) {
            rsp.setFundManagerDept(specificUserDeptList.get(0).getName());
            Optional<String> specificOrgLeader = sysUserService.getSpecificOrgLeader(specificUserDeptList.get(0).getId());
            specificOrgLeader.ifPresent(rsp::setFundManagerDeptLeader);
        }
        Optional<String> moneyManagerLeaderName = sysUserService.getMoneyManagerLeaderName();
        moneyManagerLeaderName.ifPresent(rsp::setFundManagerDivisionLeader);

        CreditLimitDetailBO creditLimitDetailBO = queryLimitDetail(fundCredit, false);
        rsp.setUsedTotalCreditAmount(Optional.ofNullable(creditLimitDetailBO).map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L));
        return rsp;
    }

    public Integer selectCountByOrgIds(List<Long> organizationIds) {
        return baseMapper.selectCount(Wrappers.<FundCredit>lambdaQuery()
                .in(FundCredit::getOrganizationId, organizationIds));
    }

    /**
     * 计算授信额度
     *
     * @param orgId 授信机构id
     * @return key 为原授信额度，value 为融资已使用额度
     */
    public Pair<Long, Long> queryCreditLimit(Long orgId) {
        FundCredit credit = baseMapper.selectOne(
                Wrappers.<FundCredit>lambdaQuery()
                        .eq(FundCredit::getOrganizationId, orgId)
                        .eq(FundCredit::getEffective, true)
                        .orderByDesc(FundCredit::getCreateTime)
                        .last(StringUtil.mysqlLimitOne()));
        //不存在授信，可以直接返回并创建
        if (ObjectUtil.isEmpty(credit)) {
            return Pair.of(0L, 0L);
        }
        // 存在历史生效的授信，需要判断是否为循环授信
//        FundCredit noRecycleCredit = credits.stream().filter(credit -> credit.getRecyclable() == 0).findFirst().orElse(null);
//        if (ObjectUtil.isEmpty(noRecycleCredit)) {
//            throw new MithrasException("该机构已存在已生效的循环授信，请至详情页修改原授信！");
//        }
        // 融资占用额度
//        Map<Long, LimitDto> limitDtoMap = calculateEveryLimit(Collections.singletonList(noRecycleCredit));
        CreditLimitDetailBO creditLimitDetailBO = queryLimitDetail(credit, true);
        return Pair.of(credit.getTotalCreditLimit(), Optional.ofNullable(creditLimitDetailBO).map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L));
    }

    public Map<Long, Pair<Long, Long>> queryCreditLimitBatch(List<Long> orgIdList) {
        if(CollectionUtil.isEmpty(orgIdList)){
            return Collections.emptyMap();
        }
        List<FundCredit> creditList = baseMapper.selectList(
                Wrappers.<FundCredit>lambdaQuery()
                        .in(FundCredit::getOrganizationId, orgIdList)
                        .eq(FundCredit::getEffective, true));
        //不存在授信，可以直接返回并创建
        if (ObjectUtil.isEmpty(creditList)) {
            return Collections.emptyMap();
        }
        // 占用额度
        Map<Long, CreditLimitDetailBO> creditLimitDetailBOMap = queryLimitDetailBatch(creditList, false);
        Map<Long, List<CreditLimitDetailBO>> groupMap = creditLimitDetailBOMap.values().stream().collect(Collectors.groupingBy(e -> Long.valueOf(e.getGrantSubjectKey())));
        Map<Long, Pair<Long, Long>> result = new HashMap<>();
        for (Map.Entry<Long, List<CreditLimitDetailBO>> entry : groupMap.entrySet()) {
            long total = entry.getValue().stream().filter(e -> Objects.nonNull(e.getTotalLimit())).mapToLong(CreditLimitDetailBO::getTotalLimit).sum();
            long occupy = entry.getValue().stream().filter(e -> Objects.nonNull(e.getOccupyTotalLimit())).mapToLong(CreditLimitDetailBO::getOccupyTotalLimit).sum();
            result.put(entry.getKey(), Pair.of(total, total - occupy));
        }
        return result;
//        return creditLimitDetailBOMap.values().stream().collect(Collectors.toMap(m -> Long.valueOf(m.getGrantSubjectKey()),
//                m -> Pair.of(m.getTotalLimit(), m.getTotalLimit() - m.getOccupyTotalLimit())
//        ));
    }


    @Resource
    private MaterialsListService materialsListService;

    public List<FundMaterialListRSP> fileList(Long belongId) {
        List<MaterialsList> materialsLists = materialsListService.listBy(BusinessModuleEnum.FUND_CREDIT.name(), belongId);
        return materialsLists.stream().map(materialsList -> {
            FundMaterialListRSP rsp = new FundMaterialListRSP();
            rsp.setId(materialsList.getId());
            rsp.setFileName(materialsList.getFilename());
            return rsp;
        }).collect(Collectors.toList());
    }

    public void fileUpload(MultipartFile file, Long belongId, String materialsType) {
        materialsListService.add(file, belongId, materialsType, BusinessModuleEnum.FUND_CREDIT.name());
    }

    public void fileRemove(FundCreditRemoveREQ req) {
        materialsListService.remove(req.getIds());
    }

    private void calculateCreditLimit(FundCreditListRSP.FundCreditList rsp, Long creditId, Long total) {
        List<FundCreditGuaranteeDetail> byCreditId = guaranteeDetailService.getByCreditId(creditId);
        Long totalGuaranteeAmount = 0L;
        for (FundCreditGuaranteeDetail detail : byCreditId) {
            totalGuaranteeAmount += detail.getGuaranteeAmount();
        }
        rsp.setCreditLimit(total - totalGuaranteeAmount);
        rsp.setGuaranteeAmount(totalGuaranteeAmount);
    }


    @Deprecated
    public Map<Long, LimitDto> calculateEveryLimit(List<FundCredit> credits) {
        // 关于循环的额度处理逻辑应在授信模块处理完成，此处不需要再计算
        // 取出所有的不可循环的授信id
//        List<FundCredit> noRecycleCredit = credits.stream().filter(v -> v.getRecyclable() == 0)
//                .collect(Collectors.toList());
        if(CollectionUtil.isEmpty(credits)){
            return Collections.emptyMap();
        }
        Map<Long, CreditLimitDetailBO> longCreditLimitDetailBOMap = queryLimitDetailBatch(credits, true);


//        Map<Long, LimitDto> res = calculateEveryLimit(credits.stream()
//                .map(FundCredit::getId).collect(Collectors.toSet()));
        // 没有不可循环的直接返回
//        if (CollectionUtil.isEmpty(noRecycleCredit)) {
//            return res;
//        }
//        noRecycleCredit.forEach(v -> {
//            if (!res.containsKey(v.getId())) {
//                return;
//            }
//            //取到当前授信生效日期之后的所有融资id，后面执行一个过滤
//            Set<Long> targetFinancingIds = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
//                    .eq(FundFinancingBaseInfo::getCreditId, v.getId())
//                    .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
//                    .ge(FundFinancingBaseInfo::getActualLoanDate, v.getEffectiveDateFrom())).stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
//            LimitDto limitDto = res.get(v.getId());
//            // 目标融资中不包含当前融资id，说明当前融资是在授信之前创建的，需要将其从已使用额度中移除
//            limitDto.getUsedLimit().entrySet().removeIf(entry -> !targetFinancingIds.contains(entry.getKey()));
//            limitDto.getUsedGuaranteeLimit().entrySet().removeIf(entry -> !targetFinancingIds.contains(entry.getKey()));
//            limitDto.getWrittenOffPrincipal().entrySet().removeIf(entry -> !targetFinancingIds.contains(entry.getKey()));
//        });
        return null;
    }

//    public Map<Long, LimitDto> calculateEveryLimit(FundCredit fundCredit, Long ignoreFinancingId) {
//        Map<Long, LimitDto> longLimitDtoMap = calculateEveryLimit(Collections.singleton(fundCredit.getId()));
//        FundFinancingBaseInfo ignoreFinancing = financingBaseInfoService.getById(ignoreFinancingId);
//        if (ObjectUtil.isEmpty(ignoreFinancing.getCreditId())) {
//            return longLimitDtoMap;
//        }
//        LimitDto limitDto = longLimitDtoMap.get(ignoreFinancing.getCreditId());
//        if (ObjectUtil.isNotNull(limitDto)) {
//            limitDto.getWrittenOffPrincipal().remove(ignoreFinancingId);
//        } else {
//            return longLimitDtoMap;
//        }
//        // 判断是否可循环，不可循环需要进一步处理
//        if (Objects.equals(fundCredit.getRecyclable(), YesOrNoNumberEnum.NO.getCode())) {
//            //取到当前授信生效日期之后的所有融资id，后面执行一个过滤
//            Set<Long> targetFinancingIds = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
//                    .eq(FundFinancingBaseInfo::getCreditId, fundCredit.getId())
//                    .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
//                    .ge(FundFinancingBaseInfo::getActualLoanDate, fundCredit.getEffectiveDateFrom())).stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
//            // 目标融资中不包含当前融资id，说明当前融资是在授信之前创建的，需要将其从已使用额度中移除
//            limitDto.getUsedLimit().entrySet().removeIf(entry -> !targetFinancingIds.contains(entry.getKey()));
//            limitDto.getUsedGuaranteeLimit().entrySet().removeIf(entry -> !targetFinancingIds.contains(entry.getKey()));
//            limitDto.getWrittenOffPrincipal().entrySet().removeIf(entry -> !targetFinancingIds.contains(entry.getKey()));
//        }
//        return longLimitDtoMap;
//    }

    /**
     * 计算每个授信机构的各种额度
     *
     * @param creditIds
     * @return
     */
    @Deprecated
    public Map<Long, LimitDto> calculateEveryLimit(Set<Long> creditIds) {
        if (CollectionUtil.isEmpty(creditIds)) {
            return Collections.emptyMap();
        }


//        queryLimitDetailList(creditIds);
        return Collections.emptyMap();

//        Map<Long, LimitDto> limitMap = new HashMap<>(creditIds.size());
//        List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoService.list(
//                Wrappers.<FundFinancingBaseInfo>lambdaQuery()
//                        .in(CollectionUtil.isNotEmpty(creditIds), FundFinancingBaseInfo::getCreditId, creditIds)
//                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
//        if (CollectionUtil.isEmpty(fundFinancingBaseInfoList)) {
//            return Collections.emptyMap();
//        }
//        List<Long> financingIds = fundFinancingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
//        // 查询当前
//        Map<Long, List<FundReceiptRepayCashFlow>> financing2CashFlows = receiptRepayCashFlowService.list(
//                        Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
//                                .in(FundReceiptRepayCashFlow::getFinancingId, financingIds))
//                .stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId));
//        fundFinancingBaseInfoList.stream().collect(Collectors.groupingBy(FundFinancingBaseInfo::getCreditId))
//                .forEach((creditId, list) -> {
//                    LimitDto limitDto = limitMap.getOrDefault(creditId, new LimitDto());
//                    for (FundFinancingBaseInfo financingBaseInfo : list) {
//                        Long financingId = financingBaseInfo.getId();
//                        limitDto.getUsedLimit().put(financingId, LongUtil.null2zero(financingBaseInfo.getFinancingAmount()));
//                        if (ObjectUtil.isNotEmpty(financingBaseInfo.getGuaranteeInfo())) {
//                            long usedGuaranteeLimit = 0L;
//                            Long guaranteeAmount = JSON.parseObject(financingBaseInfo.getGuaranteeInfo(), new TypeReference<List<FundFinancingBaseInfo.GuaranteeInfo>>() {
//                            }).stream().map(FundFinancingBaseInfo.GuaranteeInfo::getGuaranteeAmount).map(LongUtil::null2zero).reduce(0L, Long::sum);
//                            usedGuaranteeLimit += guaranteeAmount;
//                            limitDto.getUsedGuaranteeLimit().put(financingId, usedGuaranteeLimit);
//                        }
//                        long writtenOffPrincipal = 0L;
//                        for (FundReceiptRepayCashFlow cashFlow : financing2CashFlows.getOrDefault(financingId, Collections.emptyList())) {
//                            if (CashFlowState.WRITTEN_OFF.name().equals(cashFlow.getWriteOffState())) {
//                                writtenOffPrincipal += LongUtil.null2zero(cashFlow.getPrincipleAmount());
//                            }
//                        }
//                        limitDto.getWrittenOffPrincipal().put(financingId, writtenOffPrincipal);
//                    }
//                    limitMap.put(creditId, limitDto);
//                });
//        return limitMap;
    }

    public void invalid(FundCreditSingletonIdREQ req) {
        FundCredit fundCredit = this.getById(req.getId());
        if(fundCredit == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(Objects.equals(fundCredit.getEffective(), Boolean.FALSE)){
            throw new MithrasException("该授信已处于失效状态");
        }
        update(Wrappers.<FundCredit>lambdaUpdate().eq(FundCredit::getId, req.getId())
                .set(FundCredit::getEffective, Boolean.FALSE));

        // 将授信关联的额度设为失效
        CreditLimitExpireBO creditLimitExpireBO = new CreditLimitExpireBO(CreditLimitBizTypeEnum.FUND.name(), String.valueOf(fundCredit.getOrganizationId()), String.valueOf(fundCredit.getId()));
        creditLimitManagerService.expire(creditLimitExpireBO);

        CreditLimitExpireBO expireBO = new CreditLimitExpireBO();
        expireBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
        expireBO.setGrantSubjectKey(String.valueOf(fundCredit.getOrganizationId()));
        expireBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
        creditLimitManagerService.expire(expireBO);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void release(Long financingId, Long financingAmount) {
        CreditLimitReleaseBO releaseBO = new CreditLimitReleaseBO();
        releaseBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
        releaseBO.setBizTargetKey(String.valueOf(financingId));
        releaseBO.setAmount(financingAmount);
        releaseBO.setHappenDate(LocalDate.now());
        creditLimitManagerService.release(releaseBO);
    }

    public FundCreditLimitDetailRSP limitDetail(FundCreditSingletonIdREQ req) {
        FundCreditLimitDetailRSP rsp = new FundCreditLimitDetailRSP();
        FundCredit fundCredit = this.getById(req.getId());
        if(fundCredit == null){
            return rsp;
        }
        CreditLimitDetailBO creditLimitDetailBO = queryLimitDetail(fundCredit, true);
        FundOrganization organization = organizationService.getById(fundCredit.getOrganizationId());
        if(creditLimitDetailBO != null && CollectionUtil.isNotEmpty(creditLimitDetailBO.getOccupyDetailList())){
            List<FundCreditLimitDetailRSP.LimitDetail> detailRsp = new ArrayList<>();
            FundCreditLimitDetailRSP.LimitSum limitDetailSum = new FundCreditLimitDetailRSP.LimitSum();
            List<CreditLimitDetailBO.CreditLimitOccupyDetailBO> occupyDetailList = creditLimitDetailBO.getOccupyDetailList();
            List<Long> financingIdList = occupyDetailList.stream().map(CreditLimitDetailBO.CreditLimitOccupyDetailBO::getBizTargetKey).map(Long::valueOf).collect(Collectors.toList());
            Map<Long, FundFinancingBaseInfo> financingBaseInfoMap = financingBaseInfoService.listByIds(financingIdList).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));
            Map<Long, String> userMap = id2NameService.sysUserId2Name(financingBaseInfoMap.values().stream().map(FundFinancingBaseInfo::getCreateBy).collect(Collectors.toList()));
            Map<Long, FundFinancingPlan> financingPlanMap = fundFinancingPlanService.list(Wrappers.<FundFinancingPlan>lambdaQuery().in(FundFinancingPlan::getFinancingId, financingIdList))
                    .stream().collect(Collectors.toMap(FundFinancingPlan::getFinancingId, Function.identity()));
            Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIdList, FinancingTypeEnum.INDIRECT);
            Long financingAmountSum = 0L, guaranteeFinancingAmountSum = 0L, creditFinancingAmountSum = 0L, usedTotalCreditAmountSum = 0L, usedCreditAmountSum = 0L, usedGuaranteeAmountSum = 0L, remainingAmountSum = 0L, remainingCreditAmountSum = 0L, remainingGuaranteeAmountSum = 0L;
            for (CreditLimitDetailBO.CreditLimitOccupyDetailBO occupyDetailBO : occupyDetailList) {
                Long financingId = Long.valueOf(occupyDetailBO.getBizTargetKey());
                FundCreditLimitDetailRSP.LimitDetail limitDetail = new FundCreditLimitDetailRSP.LimitDetail();
                // 基础数据
                FundFinancingBaseInfo baseInfo = financingBaseInfoMap.get(financingId);
                // 报价方案
                FundFinancingPlan financingPlan = financingPlanMap.get(financingId);
                // 担保信息
                Map<Long, List<FundFinancingPlan.GuaranteeInfo>> guaranteeInfoMap = financingPlan.getGuaranteeInfo() != null ? JSON.parseArray(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class).stream()
                        .collect(Collectors.groupingBy(FundFinancingPlan.GuaranteeInfo::getOrganizationId)) : Collections.emptyMap();
                List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = guaranteeInfoMap.get(fundCredit.getOrganizationId());
                // 剩余本金
                Long remainingAmount = remainingAmountMap.get(financingId);
                BigDecimal financingAmountRate = BigDecimal.ONE;
                Long financingAmount;
                if(Objects.equals(baseInfo.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())){
                    Map<Long, FundFinancingBaseInfo.OrganizationInfo> organizationInfoMap = JSON.parseArray(baseInfo.getOrganizationInfo(), FundFinancingBaseInfo.OrganizationInfo.class).stream()
                            .collect(Collectors.toMap(FundFinancingBaseInfo.OrganizationInfo::getOrganizationId, Function.identity()));
                    FundFinancingBaseInfo.OrganizationInfo organizationInfo = organizationInfoMap.get(fundCredit.getOrganizationId());
                    // 当前授信机构的融资金额
                    financingAmount = organizationInfo.getOrganizationAmount();
                    // 当前授信机构的融资金额占比
                    financingAmountRate = BigDecimal.valueOf(financingAmount).divide(organizationInfoMap.values().stream()
                            .map(m -> BigDecimal.valueOf(m.getOrganizationAmount())).reduce(BigDecimal.ZERO, BigDecimal::add), 4, RoundingMode.HALF_UP);
                }else {
                    financingAmount = financingPlan.getFinancingAmount();
                }
                Long guaranteeAmount = Optional.ofNullable(guaranteeInfoList).map(m -> m.stream().mapToLong(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).sum()).orElse(0L);
                // 担保比例
                BigDecimal guaranteeInfoRate = BigDecimal.valueOf(guaranteeAmount).divide(BigDecimal.valueOf(financingAmount), 10, RoundingMode.HALF_UP);

                limitDetail.setFinancingAmount(financingAmount);
                limitDetail.setGuaranteeFinancingAmount(guaranteeAmount);
                limitDetail.setCreditFinancingAmount(financingAmount - guaranteeAmount);
                limitDetail.setUsedTotalCreditAmount(occupyDetailBO.getOccupyLimit());
                limitDetail.setUsedGuaranteeAmount(occupyDetailBO.getOccupyGuaranteeLimit());
                limitDetail.setUsedCreditAmount(occupyDetailBO.getOccupyCreditLimit());
                if(remainingAmount != null) {
                    // 剩余本金按融资金额比例分摊
                    remainingAmount = BigDecimal.valueOf(remainingAmount).multiply(financingAmountRate).longValue();
                    limitDetail.setRemainingAmount(remainingAmount);
                    limitDetail.setRemainingGuaranteeAmount(BigDecimal.valueOf(remainingAmount).multiply(guaranteeInfoRate).longValue());
                    limitDetail.setRemainingCreditAmount(BigDecimal.valueOf(remainingAmount).multiply(BigDecimal.ONE.subtract(guaranteeInfoRate)).longValue());
                }

                limitDetail.setOrganizationId(fundCredit.getOrganizationId());
                limitDetail.setOrganizationName(organization.getOrganizationName());
                limitDetail.setFinancingCode(baseInfo.getFinancingCode());
                limitDetail.setFinancingId(financingId);
                limitDetail.setBorrowDate(baseInfo.getActualLoanDate());
                limitDetail.setExpireDate(baseInfo.getActualExpireDate());
                limitDetail.setCreateBy(baseInfo.getCreateBy());
                limitDetail.setCreateByName(userMap.get(baseInfo.getCreateBy()));
                limitDetail.setCreateTime(baseInfo.getCreateTime());
                limitDetail.setUpdateTime(baseInfo.getUpdateTime());
                limitDetail.setFinancingStatus(baseInfo.getFinancingStatus());
                int contractRate = Optional.ofNullable(financingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(financingPlan.getLprAddPercent()).orElse(0);
                limitDetail.setContractRate((long) contractRate);

                // 总计
                financingAmountSum += limitDetail.getFinancingAmount();
                guaranteeFinancingAmountSum += limitDetail.getGuaranteeFinancingAmount();
                creditFinancingAmountSum += limitDetail.getCreditFinancingAmount();
                usedTotalCreditAmountSum += limitDetail.getUsedTotalCreditAmount();
                usedCreditAmountSum += limitDetail.getUsedCreditAmount();
                usedGuaranteeAmountSum += limitDetail.getUsedGuaranteeAmount();
                if(remainingAmount != null) {
                    remainingAmountSum += limitDetail.getRemainingAmount();
                    remainingCreditAmountSum += limitDetail.getRemainingCreditAmount();
                    remainingGuaranteeAmountSum += limitDetail.getRemainingGuaranteeAmount();
                }
                detailRsp.add(limitDetail);
            }
            if(CollectionUtil.isNotEmpty(detailRsp)) {
                detailRsp = detailRsp.stream().sorted(Comparator.comparing((FundCreditLimitDetailRSP.LimitDetail item) ->
                        Optional.ofNullable(FundFinancingStatusEnum.finaByName(item.getFinancingStatus())).map(FundFinancingStatusEnum::getSort).orElse(100)
                ).reversed().thenComparing(t -> Optional.ofNullable(t.getCreateTime()).orElse(LocalDateTime.now())).reversed()).collect(Collectors.toList());
            }
            limitDetailSum.setUsedTotalCreditAmountSum(creditLimitDetailBO.getOccupyTotalLimit());
            limitDetailSum.setUsedGuaranteeAmountSum(creditLimitDetailBO.getOccupyGuaranteeLimit());
            limitDetailSum.setUsedCreditAmountSum(creditLimitDetailBO.getOccupyCreditLimit());
            limitDetailSum.setRemainingTotalCreditAmountSum(creditLimitDetailBO.getTotalLimit() - creditLimitDetailBO.getOccupyTotalLimit());
            limitDetailSum.setRemainingGuaranteeAmountSum(creditLimitDetailBO.getGuaranteeLimit() - creditLimitDetailBO.getOccupyGuaranteeLimit());
            limitDetailSum.setRemainingCreditAmountSum(creditLimitDetailBO.getCreditLimit() - creditLimitDetailBO.getOccupyCreditLimit());
            rsp.setLimitDetailList(detailRsp);
            rsp.setLimitDetailSum(limitDetailSum);

            FundCreditLimitDetailRSP.LimitDetail limitDetailListSum = new FundCreditLimitDetailRSP.LimitDetail();
            limitDetailListSum.setRemainingCreditAmount(remainingCreditAmountSum);
            limitDetailListSum.setUsedCreditAmount(usedCreditAmountSum);
            limitDetailListSum.setFinancingAmount(financingAmountSum);
            limitDetailListSum.setRemainingAmount(remainingAmountSum);
            limitDetailListSum.setRemainingGuaranteeAmount(remainingGuaranteeAmountSum);
            limitDetailListSum.setUsedGuaranteeAmount(usedGuaranteeAmountSum);
            limitDetailListSum.setCreditFinancingAmount(creditFinancingAmountSum);
            limitDetailListSum.setGuaranteeFinancingAmount(guaranteeFinancingAmountSum);
            limitDetailListSum.setUsedTotalCreditAmount(usedTotalCreditAmountSum);
            rsp.setLimitDetailListSum(limitDetailListSum);
        }
        return rsp;
    }

    @Data
    public static class LimitDto {
        /**
         * 额度总和
         */
        private Long usedLimitTotal;
        /**
         * 担保额度总和
         */
        private Long usedGuaranteeLimitTotal;
        /**
         * 信用额度总和
         */
        private Long usedCreditLimitTotal;


        /**
         * 已用额度
         */
        private Map<Long, Long> usedLimit;
        /**
         * 已用担保额度
         */
        private Map<Long, Long> usedGuaranteeLimit;
        /**
         * 已用信用额度
         */
        private Map<Long, Long> usedCreditLimit;
        /**
         * 已核销本金
         */
        private Map<Long, Long> writtenOffPrincipal;

        public LimitDto() {
            usedLimit = new HashMap<>();
            usedGuaranteeLimit = new HashMap<>();
            usedCreditLimit = new HashMap<>();
            writtenOffPrincipal = new HashMap<>();
        }

        // 额度循环已经交由额度模块维护，此逻辑不需要了
        @Deprecated
        public Long getTotalUsedLimit(int recyclable) {
            Long totalUsed = usedLimit.values().stream().reduce(0L, Long::sum);
            if (recyclable == 1) {
                return totalUsed - writtenOffPrincipal.values().stream().reduce(0L, Long::sum);
            } else {
                return totalUsed;
            }
        }

        public Long getTotalUsedGuaranteeLimit(int recyclable) {
            Long totalUsedGuarantee = usedGuaranteeLimit.values().stream().reduce(0L, Long::sum);
            if (recyclable == 1) {
                long res = 0L;
                for (Map.Entry<Long, Long> entry : usedGuaranteeLimit.entrySet()) {

                    long l = entry.getValue() - new BigDecimal(writtenOffPrincipal.get(entry.getKey()))
                            .multiply(new BigDecimal(entry.getValue()))
                            .divide(new BigDecimal(usedLimit.get(entry.getKey())), 10, RoundingMode.HALF_UP)
                            .longValue();
                    res += l;
                }
                return res;
            } else {
                return totalUsedGuarantee;
            }
        }
    }

    public FundCredit getEffectCreditByOrganizationId(Long organizationId) {
        return getOne(Wrappers.<FundCredit>lambdaQuery().eq(FundCredit::getOrganizationId, organizationId)
                .eq(FundCredit::getEffective, Boolean.TRUE)
                .last(StringUtil.mysqlLimitOne()));
    }

    public Map<Long ,FundCredit> getEffectCreditByOrgIds(Collection<Long> organizationIdList) {
        List<FundCredit> list = list(Wrappers.<FundCredit>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(organizationIdList), FundCredit::getOrganizationId, organizationIdList)
                .orderByDesc(FundCredit::getCreateTime)
                .eq(FundCredit::getEffective, Boolean.TRUE));
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(FundCredit::getOrganizationId, Function.identity(), (m1,m2) -> m1));


    }


    public List<Long> getEffectCreditId() {
        List<FundCredit> list = this.list(Wrappers.<FundCredit>lambdaQuery().eq(FundCredit::getEffective, 1));
        return Optional.ofNullable(list).map(m -> m.stream().map(FundCredit::getId).collect(Collectors.toList())).orElse(Collections.emptyList());
    }


    public CreditLimitDetailBO queryLimitDetail(FundCredit fundCredit, boolean needDetail){
        if(fundCredit == null){
            return null;
        }
        CreditLimitQueryBO creditLimitQueryBO = new CreditLimitQueryBO();
        creditLimitQueryBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
        creditLimitQueryBO.setGrantSubjectKey(String.valueOf(fundCredit.getOrganizationId()));
        creditLimitQueryBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
        return creditLimitManagerService.querySingle(creditLimitQueryBO, needDetail);
    }

    public Map<Long, CreditLimitDetailBO> queryLimitDetailBatch(Collection<FundCredit> fundCreditList, boolean needDetail){
        if(CollectionUtil.isEmpty(fundCreditList)){
            return Collections.emptyMap();
        }
        List<CreditLimitQueryBO> creditLimitQueryBOList = new ArrayList<>();
        for (FundCredit fundCredit : fundCreditList) {
            CreditLimitQueryBO creditLimitQueryBO = new CreditLimitQueryBO();
            creditLimitQueryBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            creditLimitQueryBO.setGrantSubjectKey(String.valueOf(fundCredit.getOrganizationId()));
            creditLimitQueryBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
            creditLimitQueryBOList.add(creditLimitQueryBO);
        }
        return Optional.ofNullable(creditLimitManagerService.queryBatch(creditLimitQueryBOList, needDetail))
                .map(item -> item.stream().collect(Collectors.toMap(m -> Long.valueOf(m.getBizSourceKey()), Function.identity())))
                .orElse(Collections.emptyMap());
    }

}
