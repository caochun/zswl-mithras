package cn.zswltech.mithras.service.service.fund;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.fund.FundGuaranteeAgencyConverter;
import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.mapper.fund.FundGuaranteeAgencyMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.mapper.model.fund.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.service.mapper.model.fund.FundGuaranteeAgency;
import cn.zswltech.mithras.service.mapper.model.fund.FundGuaranteeInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.service.service.bo.CreditLimitQueryBO;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.third.tianyancha.application.TycService;
import cn.zswltech.mithras.third.tianyancha.application.dto.MithrasBaseInfo;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_agency
 * @date 2022-12-13
 */
@Service
@Slf4j
public class FundGuaranteeAgencyService extends ServiceImpl<FundGuaranteeAgencyMapper, FundGuaranteeAgency> {
    @Resource
    private TycService tycService;
    @Resource
    private FundGuaranteeAgencyConverter mainConverter;
    @Resource
    private FundGuaranteeInfoService fundGuaranteeInfoService;
    @Resource
    private FundCreditGuaranteeDetailService guaranteeDetailService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundOrganizationService fundOrganizationService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final HashSet<String> ignoreFields = new HashSet<String>();


    public FundGuaranteeAgencyService() {
        //忽略比较
        ignoreFields.add("id");
        ignoreFields.add("createTime");
        ignoreFields.add("createBy");
        ignoreFields.add("updateTime");
        ignoreFields.add("updateBy");
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long addAndSync(FundGuaranteeAgencyAddREQ req) {
        MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo(req.getUscCode());
        if (ObjectUtil.isEmpty(mithrasBaseInfo)) {
            throw new MithrasException("获取天眼查信息失败，请确认是否手工录入该客户信息");
        }
        FundGuaranteeAgency toBeInsert = mainConverter.tycInfo2Entity(mithrasBaseInfo);
        if (ObjectUtil.isNotEmpty(toBeInsert.getBizLicenseEndDate())) {
            toBeInsert.setLongTimeLicense(true);
        }
        toBeInsert.setUscCode(req.getUscCode());
        toBeInsert.setGuaranteeAgencyName(req.getGuaranteeAgencyName());
        Integer totalNow = baseMapper.selectCount(
                Wrappers.<FundGuaranteeAgency>lambdaQuery().isNotNull(FundGuaranteeAgency::getId));
        String sb = "DB" + LocalDate.now().format(df) + String.format("%04d", totalNow + 1);
        toBeInsert.setGuaranteeAgencyCode(sb);
        baseMapper.insert(toBeInsert);
        return toBeInsert.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long addHalf(FundGuaranteeAgencyAddREQ req) {
        FundGuaranteeAgency toBeInsert = new FundGuaranteeAgency();
        toBeInsert.setUscCode(req.getUscCode());
        toBeInsert.setGuaranteeAgencyName(req.getGuaranteeAgencyName());
        Integer totalNow = baseMapper.selectCount(
                Wrappers.<FundGuaranteeAgency>lambdaQuery().isNotNull(FundGuaranteeAgency::getId));
        String sb = "DB" + LocalDate.now().format(df) + String.format("%04d", totalNow + 1);
        toBeInsert.setGuaranteeAgencyCode(sb);
        baseMapper.insert(toBeInsert);
        return toBeInsert.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundGuaranteeAgencyModifyREQ req) {
        FundGuaranteeAgency originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundGuaranteeAgency info = mainConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);
    }

    public FundGuaranteeAgencyDetailRSP sync(FundGuaranteeAgencySyncREQ req) {
        FundGuaranteeAgency fundGuaranteeAgency = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(fundGuaranteeAgency)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo(fundGuaranteeAgency.getUscCode());
        if (ObjectUtil.isEmpty(mithrasBaseInfo)) {
            throw new MithrasException("获取天眼查信息失败，请确认是否手工录入该客户信息");
        }
        FundGuaranteeAgency toBeInsert = mainConverter.tycInfo2Entity(mithrasBaseInfo);
        if (mithrasBaseInfo.getBizLicenceLongTerm()) {
            toBeInsert.setBizLicenseEndDate(LocalDate.of(2099, 12, 31));
        }
        return mainConverter.entity2DetailRsp(compareFundGuaranteeAgency(fundGuaranteeAgency, toBeInsert, ignoreFields));
    }

    //比对变化项
    private FundGuaranteeAgency compareFundGuaranteeAgency(FundGuaranteeAgency old, FundGuaranteeAgency now, Set<String> ignoreFields) {
        FundGuaranteeAgency rsp = new FundGuaranteeAgency();
        Field[] declaredFields = old.getClass().getDeclaredFields();
        if (ObjectUtil.isEmpty(ignoreFields)) {
            ignoreFields.add("getSerialVersionUID");
        }
        for (Field field : declaredFields) {
            try {
                Class[] classes = {};
                String firstLetter = field.getName().substring(0, 1).toUpperCase();
                String getter = "get" + firstLetter + field.getName().substring(1);
                Method oldMethod = old.getClass().getMethod(getter, classes);
                Method nowMethod = now.getClass().getMethod(getter, classes);
                if (ignoreFields.contains(getter)) {
                    break;
                }
                if (ObjectUtil.notEqual(oldMethod.invoke(old, classes), nowMethod.invoke(now, classes))) {
                    String settrt = "set" + firstLetter + field.getName().substring(1);
                    Method method = rsp.getClass().getMethod(settrt, String.class);
                    method.invoke(rsp, nowMethod.invoke(now, classes));
                }
            } catch (Exception e) {
                log.warn("未找到方法:{}", e.getMessage());
            }
        }
        return rsp;
    }


    public PageR<FundGuaranteeAgencyListRSP> list(FundGuaranteeAgencyListREQ req) {
        Page<FundGuaranteeAgency> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundGuaranteeAgency>lambdaQuery()
                        .eq(ObjectUtil.isNotEmpty(req.getCreateBy()), BaseModel::getCreateBy, req.getCreateBy())
                        .eq(ObjectUtil.isNotEmpty(req.getGuaranteeAgencyName()),
                                FundGuaranteeAgency::getGuaranteeAgencyName, req.getGuaranteeAgencyName())
                        .ge(ObjectUtil.isNotEmpty(req.getGuaranteeLimitFrom()),
                                FundGuaranteeAgency::getEffectTotalLimit, req.getGuaranteeLimitFrom())
                        .le(ObjectUtil.isNotEmpty(req.getGuaranteeLimitTo()),
                                FundGuaranteeAgency::getEffectTotalLimit, req.getGuaranteeLimitTo())
                        .ge(ObjectUtil.isNotEmpty(req.getGuaranteeDateFrom()),
                                FundGuaranteeAgency::getEffectDateFrom, req.getGuaranteeDateFrom())
                        .le(ObjectUtil.isNotEmpty(req.getGuaranteeDateTo()),
                                FundGuaranteeAgency::getEffectDateTo, req.getGuaranteeDateTo())
                        .orderByDesc(BaseModel::getUpdateTime));
        List<Long> userIds = page.getRecords().stream().map(BaseModel::getCreateBy).collect(Collectors.toList());
        Map<Long, String> id2Name = id2NameService.sysUserId2Name(userIds);

        Map<Long, Long> usedGuaranteeLimit = getUsedGuaranteeLimit(page.getRecords().stream().map(FundGuaranteeAgency::getId).collect(Collectors.toList()));

        List<FundGuaranteeAgencyListRSP> rspList = page.getRecords().stream().map(entity -> {
            FundGuaranteeAgencyListRSP rsp = mainConverter.entity2ListRsp(entity);
            rsp.setCreateByName(id2Name.get(entity.getCreateBy()));
            FundGuaranteeInfo effectOne = fundGuaranteeInfoService.getOne(Wrappers.<FundGuaranteeInfo>lambdaQuery()
                    .eq(FundGuaranteeInfo::getEffective, 1).eq(FundGuaranteeInfo::getAgencyId, entity.getId()));
            if (ObjectUtil.isNotEmpty(effectOne)) {
                StringBuilder sb = new StringBuilder();
                if (ObjectUtil.isNotEmpty(effectOne.getEffectiveTimeFrom())) {
                    sb.append(effectOne.getEffectiveTimeFrom().format(df2));
                }
                sb.append("-");
                if (ObjectUtil.isNotEmpty(effectOne.getEffectiveTimeTo())) {
                    sb.append(effectOne.getEffectiveTimeTo().format(df2));
                }
                rsp.setGuaranteePeriod(sb.toString());
                rsp.setTotalGuaranteeLimit(effectOne.getTotalGuaranteeLimit());
                rsp.setUsedGuaranteeLimit(usedGuaranteeLimit.get(entity.getId()));
                rsp.setRemainingGuaranteeLimit(rsp.getTotalGuaranteeLimit() - rsp.getUsedGuaranteeLimit());
            }
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(page, rspList);
    }

    /**
     * 查询担保机构已使用的担保额度，担保额度是可循还的，所以需要考虑已还的额度。
     *
     * @param guaranteeAgencyId 批量的担保机构id
     * @return key 为担保机构id，value 为已使用的担保额度
     */
    public Map<Long, Long> getUsedGuaranteeLimit(List<Long> guaranteeAgencyId) {
        Map<Long, Long> usedGuaranteeLimit = new HashMap<>();
        // 1.先获取列表中担保机构的担保详情
        List<FundCreditGuaranteeDetail> guaranteeDetails = guaranteeDetailService.list(
                Wrappers.<FundCreditGuaranteeDetail>lambdaQuery()
                        .in(FundCreditGuaranteeDetail::getGuaranteeAgencyId, guaranteeAgencyId));

        if(CollectionUtil.isEmpty(guaranteeDetails)){
            return usedGuaranteeLimit;
        }
        Set<Long> relateCreditIds = guaranteeDetails.stream()
                .map(FundCreditGuaranteeDetail::getCreditId).collect(Collectors.toSet());

        // 2. 获取以上担保详情中的授信的全量担保信息
        List<FundCreditGuaranteeDetail> allGuaranteeDetails = guaranteeDetailService.list(
                Wrappers.<FundCreditGuaranteeDetail>lambdaQuery()
                        .in(FundCreditGuaranteeDetail::getCreditId, relateCreditIds));

        Map<Long, List<FundCreditGuaranteeDetail>> guaranteeDetailsGroup = allGuaranteeDetails.stream()
                .collect(Collectors.groupingBy(FundCreditGuaranteeDetail::getCreditId));

        Map<Long, CreditLimitDetailBO> creditLimitDetailBOMap = fundCreditService.queryLimitDetailBatch(fundCreditService.listByIds(guaranteeDetailsGroup.keySet()), false);

        for (Long aLong : guaranteeAgencyId) {
            usedGuaranteeLimit.put(aLong, 0L);
        }
        creditLimitDetailBOMap.forEach((creditId, limitDetail) -> {
            Long creditUsedGuaranteeLimit = limitDetail.getOccupyGuaranteeLimit();
            if (guaranteeDetailsGroup.containsKey(creditId)) {
                List<FundCreditGuaranteeDetail> details = guaranteeDetailsGroup.get(creditId);
                if (details.size() > 1) {
                    BigDecimal totalGuaranteeLimt = details.stream()
                            .map(FundCreditGuaranteeDetail::getGuaranteeAmount)
                            .map(LongUtil::null2zero)
                            .map(BigDecimal::new)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    for (FundCreditGuaranteeDetail detail : details) {
                        if (usedGuaranteeLimit.containsKey(detail.getGuaranteeAgencyId())) {
                            Long agencyId = detail.getGuaranteeAgencyId();
                            long thisAgencyUsedGuarantee = new BigDecimal(creditUsedGuaranteeLimit).multiply(new BigDecimal(detail.getGuaranteeAmount())).divide(totalGuaranteeLimt, 0, RoundingMode.HALF_UP).longValue();
                            usedGuaranteeLimit.put(agencyId, usedGuaranteeLimit.get(agencyId) + thisAgencyUsedGuarantee);
                        }
                    }
                } else {
                    Long agencyId = details.get(0).getGuaranteeAgencyId();
                    usedGuaranteeLimit.put(agencyId, usedGuaranteeLimit.get(agencyId) + creditUsedGuaranteeLimit);
                }
            }
        });



//        Map<Long, FundCreditService.LimitDto> limitDtos =
//                fundCreditService.calculateEveryLimit(guaranteeDetailsGroup.keySet());
//
//        Map<Long, Long> usedGuaranteeLimit = new HashMap<>();
//        for (Long aLong : guaranteeAgencyId) {
//            usedGuaranteeLimit.put(aLong, 0L);
//        }
//        limitDtos.forEach((creditId, limitDto) -> {
//            Long creditUsedGuaranteeLimit = limitDto.getTotalUsedGuaranteeLimit(1);
//            if (guaranteeDetailsGroup.containsKey(creditId)) {
//                List<FundCreditGuaranteeDetail> details = guaranteeDetailsGroup.get(creditId);
//                if (details.size() > 1) {
//                    BigDecimal totalGuaranteeLimt = details.stream()
//                            .map(FundCreditGuaranteeDetail::getGuaranteeAmount)
//                            .map(LongUtil::null2zero)
//                            .map(BigDecimal::new)
//                            .reduce(BigDecimal.ZERO, BigDecimal::add);
//                    for (FundCreditGuaranteeDetail detail : details) {
//                        if (usedGuaranteeLimit.containsKey(detail.getGuaranteeAgencyId())) {
//                            Long agencyId = detail.getGuaranteeAgencyId();
//                            long thisAgencyUsedGuarantee = new BigDecimal(creditUsedGuaranteeLimit).multiply(new BigDecimal(detail.getGuaranteeAmount())).divide(totalGuaranteeLimt, 0, RoundingMode.HALF_UP).longValue();
//                            usedGuaranteeLimit.put(agencyId, usedGuaranteeLimit.get(agencyId) + thisAgencyUsedGuarantee);
//                        }
//                    }
//                } else {
//                    Long agencyId = details.get(0).getGuaranteeAgencyId();
//                    usedGuaranteeLimit.put(agencyId, usedGuaranteeLimit.get(agencyId) + creditUsedGuaranteeLimit);
//                }
//            }
//        });
        return usedGuaranteeLimit;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(FundGuaranteeAgencyRemoveREQ req) {
        if (ObjectUtil.isNotEmpty(req.getIds())) {
            baseMapper.deleteBatchIds(req.getIds());
        }
    }

    public FundGuaranteeAgencyDetailRSP detail(Long id) {
        FundGuaranteeAgency entity = baseMapper.selectById(id);
        FundGuaranteeAgencyDetailRSP rsp = mainConverter.entity2DetailRsp(entity);
        rsp.setFundManagerName(sysUserService.getUserName(entity.getCreateBy()));
        List<OrgDO> specificUserDeptList = sysUserService.getSpecificUserDeptList(entity.getCreateBy());
        if (ObjectUtil.isNotEmpty(specificUserDeptList)) {
            rsp.setDeptName(specificUserDeptList.get(0).getName());
            Optional<String> moneyManagerLeaderName = sysUserService.getMoneyManagerLeaderName();
            moneyManagerLeaderName.ifPresent(rsp::setDivisionLeaderName);
        }
        return rsp;
    }

    /**
     * 更新担保机构的生效时间和生效总额度
     *
     * @param id
     */
    public void updateGuaranteeAgency(Long id) {
        FundGuaranteeAgency agency = baseMapper.selectById(id);
        FundGuaranteeInfo effectOne = fundGuaranteeInfoService.getOne(Wrappers.<FundGuaranteeInfo>lambdaQuery()
                .eq(FundGuaranteeInfo::getAgencyId, id).eq(FundGuaranteeInfo::getEffective, 1)
                .last("Limit 1"));
        if (ObjectUtil.isNotEmpty(effectOne)) {
            agency.setEffectDateFrom(effectOne.getEffectiveTimeFrom());
            agency.setEffectDateTo(effectOne.getEffectiveTimeTo());
            agency.setEffectTotalLimit(effectOne.getTotalGuaranteeLimit());
        } else {
            agency.setEffectDateFrom(null);
            agency.setEffectDateTo(null);
            agency.setEffectTotalLimit(null);
        }
        baseMapper.updateAnnotationIncludeNullById(agency);
    }

    @XxlJob("updateAllGuaranteeAgency")
    public void updateALlGuaranteeAgency() {
        List<FundGuaranteeAgency> allAgency = baseMapper.selectList(Wrappers.<FundGuaranteeAgency>lambdaQuery().isNotNull(FundGuaranteeAgency::getId));
        allAgency.forEach(agency -> {
            Optional<List<FundGuaranteeInfo>> guaranteeInfos = fundGuaranteeInfoService.selectEffectByAgencyId(agency.getId());
            guaranteeInfos.ifPresent(infos -> {
                FundGuaranteeAgency updateEntity = new FundGuaranteeAgency();
                BeanUtil.copyProperties(agency, updateEntity);
                updateEntity.setEffectTotalLimit(0L);
                infos.forEach(fundGuaranteeInfo -> {
                    updateEntity.setEffectTotalLimit(updateEntity.getEffectTotalLimit() + fundGuaranteeInfo.getTotalGuaranteeLimit());
                    if (fundGuaranteeInfo.getEffectiveTimeTo().isAfter(updateEntity.getEffectDateTo())) {
                        updateEntity.setEffectDateTo(fundGuaranteeInfo.getEffectiveTimeTo());
                    }
                    if (fundGuaranteeInfo.getEffectiveTimeFrom().isBefore(updateEntity.getEffectDateFrom())) {
                        updateEntity.setEffectDateFrom(fundGuaranteeInfo.getEffectiveTimeFrom());
                    }
                });
                baseMapper.updateById(updateEntity);
            });
        });

    }

    public List<FundGuaranteeAgencyListRSP> pulldown(FundGuaranteeAgencyPullDownREQ req) {
        List<Long> orgInnerAgencyIdList = null;
        if(Objects.nonNull(req.getOrganizationId())) {
            List<FundCreditListRSP.FundCreditList> fundCreditLists = fundCreditService.listEffectByOrgId(req.getOrganizationId(), null);
            if(CollectionUtil.isNotEmpty(fundCreditLists)) {
                List<Long> creditIdList = fundCreditLists.stream().map(FundCreditListRSP.FundCreditList::getId).collect(Collectors.toList());
                Map<Long, List<FundCreditGuaranteeDetail>> detailMap = guaranteeDetailService.getByCreditIdList(creditIdList);
                orgInnerAgencyIdList = Optional.ofNullable(detailMap).map(m -> m.values().stream().flatMap(Collection::stream)
                        .map(FundCreditGuaranteeDetail::getGuaranteeAgencyId).collect(Collectors.toList())).orElse(null);
                if (CollectionUtil.isEmpty(orgInnerAgencyIdList)) {
                    return Collections.emptyList();
                }
            }
        }

        List<FundGuaranteeAgencyListRSP> agencyPullRsps =
                mainConverter.entities2ListRsps(baseMapper.selectList(Wrappers.<FundGuaranteeAgency>lambdaQuery()
                        .like(StrUtil.isNotBlank(req.getGuaranteeAgencyName()), FundGuaranteeAgency::getGuaranteeAgencyName, req.getGuaranteeAgencyName())
                        .in(CollectionUtil.isNotEmpty(orgInnerAgencyIdList), FundGuaranteeAgency::getId, orgInnerAgencyIdList)));
        List<Long> agencyIds = agencyPullRsps.stream().map(FundGuaranteeAgencyListRSP::getId).collect(Collectors.toList());
        Map<Long, Long> usedGuaranteeLimit = getUsedGuaranteeLimit(agencyIds);
        agencyPullRsps.forEach(rsp -> {
            rsp.setUsedGuaranteeLimit(usedGuaranteeLimit.get(rsp.getId()));
//            getTotalGuaranteeLimit(rsp.getId()).ifPresent(rsp::setTotalGuaranteeLimit);
            rsp.setRemainingGuaranteeLimit(LongUtil.null2zero(rsp.getTotalGuaranteeLimit())
                    - LongUtil.null2zero(rsp.getUsedGuaranteeLimit()));
        });
        return agencyPullRsps;
    }

    public Optional<Long> getTotalGuaranteeLimit(Long agencyId) {
        FundGuaranteeInfo one = fundGuaranteeInfoService.getOne(Wrappers.<FundGuaranteeInfo>lambdaQuery().eq(FundGuaranteeInfo::getAgencyId, agencyId).eq(FundGuaranteeInfo::getEffective, 1));
        if (ObjectUtil.isNotEmpty(one)) {
            return Optional.ofNullable(one.getTotalGuaranteeLimit());
        } else {
            return Optional.empty();
        }
    }


    public Map<Long, String> getNameByIds(Collection<Long> agencyIdList) {
        if(CollectionUtil.isEmpty(agencyIdList)){
            return Collections.emptyMap();
        }
        List<FundGuaranteeAgency> agencyList = baseMapper.selectBatchIds(agencyIdList);
        return Optional.ofNullable(agencyList).map(m -> m.stream()
                .collect(Collectors.toMap(FundGuaranteeAgency::getId, FundGuaranteeAgency::getGuaranteeAgencyName))
        ).orElse(Collections.emptyMap());
    }

    public FundGuaranteeLimitDetailRSP limitDetail(FundGuaranteeSingletonIdREQ req) {
        FundGuaranteeLimitDetailRSP rsp = new FundGuaranteeLimitDetailRSP();
        FundGuaranteeInfo guaranteeInfo = fundGuaranteeInfoService.getOne(Wrappers.<FundGuaranteeInfo>lambdaQuery()
                .eq(FundGuaranteeInfo::getEffective, 1).eq(FundGuaranteeInfo::getAgencyId, req.getId()));
        // 通过授信获取到关联和合同及其使用额度
        List<Long> creditIdList = guaranteeDetailService.getCreditIdByAgencyIds(Collections.singletonList(req.getId()));
        // 降低代码复杂度，这里直接并行调用授信详情的方法，不单独维护了
        List<FundCreditLimitDetailRSP> creditLimitDetailRspList = creditIdList.parallelStream().map(m -> fundCreditService.limitDetail(new FundCreditSingletonIdREQ(m)))
                .filter(Objects::nonNull).filter(f -> f.getLimitDetailListSum() != null || f.getLimitDetailList() != null || f.getLimitDetailSum() != null).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(creditLimitDetailRspList)){
            FundGuaranteeLimitDetailRSP.LimitSum limitSumRsp = new FundGuaranteeLimitDetailRSP.LimitSum();
            long usedLimitSum = creditLimitDetailRspList.stream().map(FundCreditLimitDetailRSP::getLimitDetailSum).filter(Objects::nonNull).mapToLong(FundCreditLimitDetailRSP.LimitSum::getUsedGuaranteeAmountSum).sum();
            // 过滤担保额度为空的合同,并根据融资状态和创建时间排序
            List<FundGuaranteeLimitDetailRSP.LimitDetail> limitDetailList = creditLimitDetailRspList.stream()
                    .map(FundCreditLimitDetailRSP::getLimitDetailList).filter(Objects::nonNull).flatMap(Collection::stream)
                    .filter(f -> Objects.nonNull(f.getGuaranteeFinancingAmount()) && f.getGuaranteeFinancingAmount() != 0L)
                    .map(m -> {
                        FundGuaranteeLimitDetailRSP.LimitDetail limitDetail = BeanUtil.copyProperties(m, FundGuaranteeLimitDetailRSP.LimitDetail.class);
                        // 此处的剩余担保额度是在剩余本金中的占用额度，取占用额度即可
                        limitDetail.setRemainingGuaranteeAmount(m.getUsedGuaranteeAmount());
                        limitDetail.setRemainingCreditAmount(m.getUsedCreditAmount());
                        return limitDetail;
                    }).sorted(Comparator.comparing((FundGuaranteeLimitDetailRSP.LimitDetail item) ->
                                    Optional.ofNullable(FundFinancingStatusEnum.finaByName(item.getFinancingStatus())).map(FundFinancingStatusEnum::getSort).orElse(100)
                    ).reversed().thenComparing(t -> Optional.ofNullable(t.getCreateTime()).orElse(LocalDateTime.now())).reversed()).collect(Collectors.toList());

            limitSumRsp.setUsedTotalCreditAmountSum(usedLimitSum);
            limitSumRsp.setRemainingTotalCreditAmountSum(Optional.ofNullable(guaranteeInfo.getTotalGuaranteeLimit()).orElse(0L) - usedLimitSum);
            rsp.setLimitDetailList(limitDetailList);
            rsp.setLimitDetailSum(limitSumRsp);

            // 合计
            Long financingAmountSum = 0L ,guaranteeFinancingAmountSum = 0L ,creditFinancingAmountSum = 0L, remainingCreditAmountSum = 0L ,remainingGuaranteeAmountSum = 0L ,remainingAmountSum = 0L;
            for (FundGuaranteeLimitDetailRSP.LimitDetail limitDetail : limitDetailList) {
                financingAmountSum += Optional.ofNullable(limitDetail.getFinancingAmount()).orElse(0L);
                guaranteeFinancingAmountSum += Optional.ofNullable(limitDetail.getGuaranteeFinancingAmount()).orElse(0L);
                creditFinancingAmountSum += Optional.ofNullable(limitDetail.getCreditFinancingAmount()).orElse(0L);
                remainingCreditAmountSum += Optional.ofNullable(limitDetail.getRemainingCreditAmount()).orElse(0L);
                remainingGuaranteeAmountSum += Optional.ofNullable(limitDetail.getRemainingGuaranteeAmount()).orElse(0L);
                remainingAmountSum += Optional.ofNullable(limitDetail.getRemainingAmount()).orElse(0L);
            }
            FundGuaranteeLimitDetailRSP.LimitDetail limitDetailListSum = new FundGuaranteeLimitDetailRSP.LimitDetail();
            limitDetailListSum.setRemainingCreditAmount(remainingCreditAmountSum);
            limitDetailListSum.setFinancingAmount(financingAmountSum);
            limitDetailListSum.setRemainingAmount(remainingAmountSum);
            limitDetailListSum.setRemainingGuaranteeAmount(remainingGuaranteeAmountSum);
            limitDetailListSum.setCreditFinancingAmount(creditFinancingAmountSum);
            limitDetailListSum.setGuaranteeFinancingAmount(guaranteeFinancingAmountSum);
            rsp.setLimitDetailListSum(limitDetailListSum);
        }
        return rsp;

    }
}