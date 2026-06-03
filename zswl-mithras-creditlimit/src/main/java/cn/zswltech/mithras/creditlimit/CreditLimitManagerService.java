package cn.zswltech.mithras.creditlimit.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.creditlimit.enums.CreditLimitChangeTypeEnum;
import cn.zswltech.mithras.creditlimit.enums.CreditLimitStatusEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditBusinessRef;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimit;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimitChangeRecord;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimitDetail;
import cn.zswltech.mithras.service.others.LackDataException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.creditlimit.service.CreditBusinessRefService;
import cn.zswltech.mithras.creditlimit.service.bo.*;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/8/27
 * @description 授信额度管理相关接口
 */
@Slf4j
@Service
public class CreditLimitManagerService {
    @Resource
    private CreditLimitService creditLimitService;
    @Resource
    private CreditLimitDetailService creditLimitDetailService;
    @Resource
    private CreditLimitChangeRecordService creditLimitChangeRecordService;
    @Resource
    private CreditBusinessRefService creditBusinessRefService;

    @Transactional(rollbackFor = Throwable.class)
    public void create(CreditLimitCreateBO creditLimitCreateBO) {
        log.info("授信额度管理-创建额度[{}]", JSONUtil.toJsonStr(creditLimitCreateBO));
        // 校验
        validate(creditLimitCreateBO);
        long sum = creditLimitCreateBO.getGuaranteeLimit() + creditLimitCreateBO.getCreditLimit();
        Assert.isTrue(creditLimitCreateBO.getTotalLimit() == sum, () -> MithrasException.newException("担保额度加信用额度不等于总额度"));
        // 保证只有一个生效的授信，先失效其他的
        List<CreditLimit> exist = creditLimitService.listByTwoKeys(creditLimitCreateBO.getBizType(), creditLimitCreateBO.getGrantSubjectKey());
        if (CollectionUtil.isNotEmpty(exist)) {
            exist.forEach(e -> creditLimitService.expire(e.getBizType(), e.getGrantingSubjectKey(), e.getBizSourceKey()));
        }
        // 查询机构下的占用明细
        List<CreditLimitDetail> creditLimitDetailList = creditLimitDetailService.listByGrantingSubjectKey(creditLimitCreateBO.getBizType(), creditLimitCreateBO.getGrantSubjectKey());
        // 非循环授信翻单需要更新合同占用情况
        if(Objects.equals(creditLimitCreateBO.getRecyclable(), YesOrNoNumberEnum.NO.getCode()) && CollectionUtil.isNotEmpty(creditLimitDetailList)) {
            this.updateCreditLimitDetailFromCreateBO(creditLimitCreateBO, creditLimitDetailList);
            creditLimitDetailService.updateBatchById(creditLimitDetailList);
        }
        // 数据模型转换
        CreditLimit creditLimit = this.buildCreditLimitFromCreateBO(creditLimitCreateBO, creditLimitDetailList);
        // 保存数据
        creditLimitService.save(creditLimit);
        // 已有的该机构下的授信与合同的关联关系失效
        creditBusinessRefService.invalidByGrantingSubjectKey(creditLimit.getBizType(), creditLimit.getGrantingSubjectKey());
        // 挂在同机构下还有占用额度的需要关联到新的授信上
        if (CollectionUtil.isNotEmpty(creditLimitDetailList)) {
            List<CreditBusinessRef> insertList = new LinkedList<>();
            for (CreditLimitDetail creditLimitDetail : creditLimitDetailList) {
                // 跳过占用额度是0的
                if (creditLimitDetail.getOccupyTotalLimit() <= 0) {
                    continue;
                }
                CreditBusinessRef creditBusinessRef = new CreditBusinessRef();
                creditBusinessRef.setBizType(creditLimit.getBizType());
                creditBusinessRef.setGrantingSubjectKey(creditLimit.getGrantingSubjectKey());
                creditBusinessRef.setBizSourceKey(creditLimit.getBizSourceKey());
                creditBusinessRef.setBizTargetKey(creditLimitDetail.getBizTargetKey());
                insertList.add(creditBusinessRef);
            }
            if (CollectionUtil.isNotEmpty(insertList)) {
                creditBusinessRefService.saveBatch(insertList);
            }
        }
    }

    private void updateCreditLimitDetailFromCreateBO(CreditLimitCreateBO creditLimitCreateBO, List<CreditLimitDetail> creditLimitDetailList) {
        Map<Long, Long> remainingAmountMap = creditLimitCreateBO.getRemainingAmountMap();
        if(CollectionUtil.isEmpty(remainingAmountMap)){
            return;
        }
        for (CreditLimitDetail creditLimitDetail : creditLimitDetailList) {
            // 担保比例
            BigDecimal guaranteeRate = BigDecimal.valueOf(creditLimitDetail.getOccupyGuaranteeLimit()).divide(BigDecimal.valueOf(creditLimitDetail.getOccupyTotalLimit()), 20, RoundingMode.HALF_UP);

            BigDecimal occupyTotal = BigDecimal.valueOf(LongUtil.null2zero(remainingAmountMap.get(Long.valueOf(creditLimitDetail.getBizTargetKey()))));
            BigDecimal guaranteeOccupy = occupyTotal.multiply(guaranteeRate);
            creditLimitDetail.setOccupyTotalLimit(occupyTotal.longValue());
            creditLimitDetail.setOccupyGuaranteeLimit(guaranteeOccupy.longValue());
            creditLimitDetail.setOccupyCreditLimit(creditLimitDetail.getOccupyTotalLimit() - creditLimitDetail.getOccupyGuaranteeLimit());
        }
    }

    public void modify(CreditLimitModifyBO creditLimitModifyBO) {
        log.info("授信额度管理-修改额度[{}]", JSONUtil.toJsonStr(creditLimitModifyBO));
        validate(creditLimitModifyBO);
        long sum = creditLimitModifyBO.getGuaranteeLimit() + creditLimitModifyBO.getCreditLimit();
        Assert.isTrue(creditLimitModifyBO.getTotalLimit() == sum, () -> MithrasException.newException("担保额度加信用额度不等于总额度"));
        CreditLimit creditLimit = creditLimitService.findByThreeKeys(creditLimitModifyBO.getBizType(), creditLimitModifyBO.getGrantSubjectKey(), creditLimitModifyBO.getBizSourceKey());
        if (Objects.isNull(creditLimit)) {
            throw new MithrasException("授信数据不存在");
        }
        creditLimit.setTotalLimit(creditLimitModifyBO.getTotalLimit());
        creditLimit.setGuaranteeLimit(creditLimitModifyBO.getGuaranteeLimit());
        creditLimit.setCreditLimit(creditLimitModifyBO.getCreditLimit());
        creditLimit.setEffectiveDateFrom(creditLimitModifyBO.getEffectiveDateFrom());
        creditLimit.setEffectiveDateTo(creditLimitModifyBO.getEffectiveDateTo());
        creditLimit.setStatus(this.ensureCreditLimitStatus(creditLimit.getEffectiveDateFrom(), creditLimit.getEffectiveDateTo()).name());
        creditLimitService.updateById(creditLimit);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void occupy(CreditLimitOccupyBO creditLimitOccupyBO) {
        log.info("授信额度管理-占用额度[{}]", JSONUtil.toJsonStr(creditLimitOccupyBO));
        validate(creditLimitOccupyBO);
        if (creditLimitOccupyBO.getAmount() < creditLimitOccupyBO.getGuaranteeAmount()) {
            throw new MithrasException("占用担保额度不能大于占用额度");
        }
        // 查询授信
        CreditLimit creditLimit = creditLimitService.findByThreeKeys(creditLimitOccupyBO.getBizType(), creditLimitOccupyBO.getGrantSubjectKey(), creditLimitOccupyBO.getBizSourceKey());
        if (Objects.isNull(creditLimit)) {
            throw new MithrasException("授信数据不存在");
        }
        if (!Objects.equals(creditLimit.getStatus(), CreditLimitStatusEnum.EFFECTIVE.name())) {
            throw new MithrasException("授信未生效");
        }
        // 计算可用
        long available = creditLimit.getTotalLimit() - creditLimit.getOccupyTotalLimit();
        long availableGuarantee = creditLimit.getGuaranteeLimit() - creditLimit.getOccupyGuaranteeLimit();
        if (creditLimitOccupyBO.getAmount() > available) {
            throw new MithrasException("可用额度不足");
        }
        if (creditLimitOccupyBO.getGuaranteeAmount() > availableGuarantee) {
            throw new MithrasException("可用担保额度不足");
        }
        // 查询关联记录，如果没有的话就生成一个
        CreditBusinessRef creditBusinessRef = creditBusinessRefService.getOneByFourKeys(creditLimitOccupyBO.getBizType(), creditLimitOccupyBO.getGrantSubjectKey(), creditLimitOccupyBO.getBizSourceKey(), creditLimitOccupyBO.getBizTargetKey());
        if (Objects.isNull(creditBusinessRef)) {
            creditBusinessRef = this.buildCreditBusinessRefFromCreditOccupyBO(creditLimitOccupyBO);
            creditBusinessRefService.save(creditBusinessRef);
        }
        // 查询占用明细，如果没有的话就生成一个
        CreditLimitDetail creditLimitDetail = creditLimitDetailService.getOneByThreeKeys(creditLimitOccupyBO.getBizType(), creditLimitOccupyBO.getGrantSubjectKey(), creditLimitOccupyBO.getBizTargetKey());
        if (Objects.isNull(creditLimitDetail)) {
            creditLimitDetail = this.buildCreditLimitDetailFromCreditOccupyBO(creditLimitOccupyBO);
            creditLimitDetailService.save(creditLimitDetail);
        } else {
            // 更新占用
            creditLimitDetail.setOccupyTotalLimit(creditLimitDetail.getOccupyTotalLimit() + creditLimitOccupyBO.getAmount());
            creditLimitDetail.setOccupyGuaranteeLimit(creditLimitDetail.getOccupyGuaranteeLimit() + creditLimitOccupyBO.getGuaranteeAmount());
            creditLimitDetail.setOccupyCreditLimit(creditLimitDetail.getOccupyTotalLimit() - creditLimitDetail.getOccupyGuaranteeLimit());
            creditLimitDetailService.updateById(creditLimitDetail);
        }
        // 保存额度占用变更记录
        creditLimitChangeRecordService.save(this.buildCreditLimitChangeRecordFromCreditOccupyBO(creditLimitOccupyBO));
        // 更新授信占用额度
        creditLimit.setOccupyTotalLimit(creditLimit.getOccupyTotalLimit() + creditLimitOccupyBO.getAmount());
        creditLimit.setOccupyGuaranteeLimit(creditLimit.getOccupyGuaranteeLimit() + creditLimitOccupyBO.getGuaranteeAmount());
        creditLimit.setOccupyCreditLimit(creditLimit.getOccupyTotalLimit() - creditLimit.getOccupyGuaranteeLimit());
        creditLimitService.updateById(creditLimit);
    }

    public void release(CreditLimitReleaseBO creditLimitReleaseBO) {
        log.info("授信额度管理-释放额度[{}]", JSONUtil.toJsonStr(creditLimitReleaseBO));
        validate(creditLimitReleaseBO);
        // 找到占用机构明细
        List<CreditLimitDetail> creditLimitDetailList = creditLimitDetailService.listByBizTargetKey(creditLimitReleaseBO.getBizType(), creditLimitReleaseBO.getBizTargetKey());
        if (CollectionUtil.isEmpty(creditLimitDetailList)) {
            throw new MithrasException("数据异常，没有找到任何占用额度明细");
        }
        Map<String, CreditLimitDetail> creditLimitDetailMap = creditLimitDetailList.stream().collect(Collectors.toMap(CreditLimitDetail::getGrantingSubjectKey, e -> e));
        // 找到所有关联授信
        List<CreditBusinessRef> creditBusinessRefList = creditBusinessRefService.listByBizTargetKey(creditLimitReleaseBO.getBizType(), creditLimitReleaseBO.getBizTargetKey());
        if (CollectionUtil.isEmpty(creditBusinessRefList)) {
            throw new MithrasException("数据异常，没有找到任何关联授信");
        }
        Map<String, CreditBusinessRef> creditBusinessRefMap = creditBusinessRefList.stream().collect(Collectors.toMap(CreditBusinessRef::getGrantingSubjectKey, e -> e));
        // 找到所有授信信息
        List<String> queryKeyList = creditBusinessRefList.stream().map(e -> generateQueryKey(e.getBizType(), e.getGrantingSubjectKey(), e.getBizSourceKey())).collect(Collectors.toList());
        List<CreditLimit> creditLimitList = creditLimitService.listByQueryKeys(queryKeyList);
        if (CollectionUtil.isEmpty(creditLimitList)) {
            throw new MithrasException("数据异常，授信额度数据不存在");
        }
        Map<String, CreditLimit> creditLimitMap = creditLimitList.stream().collect(Collectors.toMap(CreditLimit::getQueryKey, e-> e));
        // 处理额度释放
        List<CreditLimitChangeRecord> insertList = new LinkedList<>();
        List<CreditLimitDetail> updateDetailList = new LinkedList<>();
        List<CreditLimit> updateList = new LinkedList<>();
        long totalOccupy = creditLimitDetailMap.values().stream().mapToLong(CreditLimitDetail::getOccupyTotalLimit).sum();
        for (Map.Entry<String, CreditLimitDetail> entry : creditLimitDetailMap.entrySet()) {
            CreditLimitDetail creditLimitDetail = entry.getValue();
            CreditBusinessRef creditBusinessRef = creditBusinessRefMap.get(creditLimitDetail.getGrantingSubjectKey());
            CreditLimit creditLimit = creditLimitMap.get(this.generateQueryKey(creditBusinessRef.getBizType(), creditBusinessRef.getGrantingSubjectKey(), creditBusinessRef.getBizSourceKey()));
            if (Objects.equals(creditLimit.getRecyclable(), YesOrNoNumberEnum.NO.getCode())) {
                // 不可循环授信无需释放
                continue;
            }
            long releaseAmount = 0L;
            long releaseGuaranteeAmount = 0L;
            // 按各机构占用比例拆分
            BigDecimal rate = BigDecimal.valueOf(entry.getValue().getOccupyTotalLimit()).divide(BigDecimal.valueOf(totalOccupy), 20, RoundingMode.HALF_UP);
            BigDecimal releaseAmountBD = BigDecimal.valueOf(creditLimitReleaseBO.getAmount()).multiply(rate);
            releaseAmount = mithrasLongDecimalTwo(releaseAmountBD.longValue());
            // 如果有担保占用需要拆出释放了多少担保额度
            if (Objects.nonNull(creditLimitDetail.getOccupyGuaranteeLimit()) && creditLimitDetail.getOccupyGuaranteeLimit() > 0) {
                BigDecimal releaseGuaranteeAmountBD = releaseAmountBD.multiply(BigDecimal.valueOf(creditLimitDetail.getOccupyGuaranteeLimit()).divide(BigDecimal.valueOf(creditLimitDetail.getOccupyTotalLimit()), 20, RoundingMode.HALF_UP));
                releaseGuaranteeAmount = mithrasLongDecimalTwo(releaseGuaranteeAmountBD.longValue());
            }
            // 更新授信信息
            CreditLimit update = BeanUtil.copyProperties(creditLimit, CreditLimit.class);
            update.setOccupyTotalLimit(Math.max(0L, update.getOccupyTotalLimit() - releaseAmount));
            update.setOccupyGuaranteeLimit(Math.max(0L, update.getOccupyGuaranteeLimit() - releaseGuaranteeAmount));
            update.setOccupyCreditLimit(update.getOccupyTotalLimit() - update.getOccupyGuaranteeLimit());
            updateList.add(update);
            // 更新占用明细
            CreditLimitDetail updateDetail = BeanUtil.copyProperties(creditLimitDetail, CreditLimitDetail.class);
            updateDetail.setOccupyTotalLimit(Math.max(0L, updateDetail.getOccupyTotalLimit() - releaseAmount));
            updateDetail.setOccupyGuaranteeLimit(Math.max(0L, updateDetail.getOccupyGuaranteeLimit() - releaseGuaranteeAmount));
            updateDetail.setOccupyCreditLimit(updateDetail.getOccupyTotalLimit() - updateDetail.getOccupyGuaranteeLimit());
            updateDetailList.add(updateDetail);
            // 保存变更记录
            CreditLimitChangeRecord changeRecord = new CreditLimitChangeRecord();
            changeRecord.setBizType(creditBusinessRef.getBizType());
            changeRecord.setGrantingSubjectKey(creditBusinessRef.getGrantingSubjectKey());
            changeRecord.setBizSourceKey(creditBusinessRef.getBizSourceKey());
            changeRecord.setBizTargetKey(creditBusinessRef.getBizTargetKey());
            changeRecord.setQueryKey(this.generateQueryKey(creditBusinessRef.getBizType(), creditBusinessRef.getGrantingSubjectKey(), creditBusinessRef.getBizTargetKey()));
            changeRecord.setChangeType(CreditLimitChangeTypeEnum.RELEASE.name());
            changeRecord.setChangeDate(creditLimitReleaseBO.getHappenDate());
            changeRecord.setChangeTotalLimit(releaseAmount);
            changeRecord.setChangeGuaranteeLimit(releaseGuaranteeAmount);
            changeRecord.setChangeCreditLimit(changeRecord.getChangeTotalLimit() - changeRecord.getChangeGuaranteeLimit());
            insertList.add(changeRecord);
        }
        // 落数据
        if (CollectionUtil.isNotEmpty(insertList)) {
            creditLimitChangeRecordService.saveBatch(insertList);
        }
        if (CollectionUtil.isNotEmpty(updateDetailList)) {
            creditLimitDetailService.updateBatchById(updateDetailList);
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            creditLimitService.updateBatchById(updateList);
        }
    }

    public void expire(CreditLimitExpireBO creditLimitExpireBO) {
        log.info("授信额度管理-失效额度[{}]", JSONUtil.toJsonStr(creditLimitExpireBO));
        validate(creditLimitExpireBO);
        creditLimitService.expire(creditLimitExpireBO.getBizType(), creditLimitExpireBO.getGrantSubjectKey(), creditLimitExpireBO.getBizSourceKey());
    }

    public CreditLimitDetailBO querySingle(CreditLimitQueryBO creditLimitQueryBO, boolean hasOccupyDetailList) {
        validate(creditLimitQueryBO);
        List<CreditLimitQueryBO> query = Collections.singletonList(creditLimitQueryBO);
        List<CreditLimitDetailBO> list = this.queryBatch(query, hasOccupyDetailList);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<CreditLimitDetailBO> queryBatch(List<CreditLimitQueryBO> creditLimitQueryBOList, boolean hasOccupyDetailList) {
        creditLimitQueryBOList.forEach(this::validate);
        List<String> queryKeyList = creditLimitQueryBOList.stream().map(e -> generateQueryKey(e.getBizType(), e.getGrantSubjectKey(), e.getBizSourceKey())).collect(Collectors.toList());
        LambdaQueryWrapper<CreditLimit> query = Wrappers.lambdaQuery();
        query.in(CreditLimit::getQueryKey, queryKeyList);
        List<CreditLimit> creditLimitList = creditLimitService.listByQueryKeys(queryKeyList);
        if (CollectionUtil.isEmpty(creditLimitList)) {
            return Collections.emptyList();
        }
        List<CreditLimitDetailBO> result = creditLimitList.stream().map(this::buildCreditLimitDetailBOFromCreditLimit).collect(Collectors.toList());
        if (hasOccupyDetailList) {
            this.fillOccupyDetailList(result);
        }
        return result;
    }

    private CreditLimitDetailBO buildCreditLimitDetailBOFromCreditLimit(CreditLimit creditLimit) {
        CreditLimitDetailBO creditLimitDetailBO = new CreditLimitDetailBO();
        creditLimitDetailBO.setBizType(creditLimit.getBizType());
        creditLimitDetailBO.setGrantSubjectKey(creditLimit.getGrantingSubjectKey());
        creditLimitDetailBO.setBizSourceKey(creditLimit.getBizSourceKey());
        creditLimitDetailBO.setCreditLimit(creditLimit.getCreditLimit());
        creditLimitDetailBO.setGuaranteeLimit(creditLimit.getGuaranteeLimit());
        creditLimitDetailBO.setTotalLimit(creditLimit.getTotalLimit());
        creditLimitDetailBO.setEffectiveDateFrom(creditLimit.getEffectiveDateFrom());
        creditLimitDetailBO.setEffectiveDateTo(creditLimit.getEffectiveDateTo());
        creditLimitDetailBO.setRecyclable(creditLimit.getRecyclable());
        creditLimitDetailBO.setStatus(creditLimit.getStatus());
        creditLimitDetailBO.setOccupyTotalLimit(creditLimit.getOccupyTotalLimit());
        creditLimitDetailBO.setOccupyGuaranteeLimit(creditLimit.getOccupyGuaranteeLimit());
        creditLimitDetailBO.setOccupyCreditLimit(creditLimit.getOccupyCreditLimit());
        return creditLimitDetailBO;
    }

    private void fillOccupyDetailList(List<CreditLimitDetailBO> list) {
        LambdaQueryWrapper<CreditLimitDetail> query = Wrappers.lambdaQuery();
        for (CreditLimitDetailBO creditLimitDetailBO : list) {
            query.or(innerQuery -> {
                innerQuery.eq(CreditLimitDetail::getBizType, creditLimitDetailBO.getBizType());
                innerQuery.eq(CreditLimitDetail::getGrantingSubjectKey, creditLimitDetailBO.getGrantSubjectKey());
            });
        }
        List<CreditLimitDetail> creditLimitDetailList = creditLimitDetailService.list(query);
        Map<String, List<CreditLimitDetail>> map = creditLimitDetailList.stream().collect(Collectors.groupingBy(e -> e.getBizType() + "@" + e.getGrantingSubjectKey()));
        list.forEach(e -> {
            List<CreditLimitDetail> detailList = map.get(e.getBizType() + "@" + e.getGrantSubjectKey());
            if (CollectionUtil.isEmpty(detailList)) {
                e.setOccupyDetailList(Collections.emptyList());
            } else {
                List<CreditLimitDetailBO.CreditLimitOccupyDetailBO> detailBOList = detailList.stream().map(item -> {
                    CreditLimitDetailBO.CreditLimitOccupyDetailBO detailBO = new CreditLimitDetailBO.CreditLimitOccupyDetailBO();
                    detailBO.setBizTargetKey(item.getBizTargetKey());
                    detailBO.setOccupyLimit(item.getOccupyTotalLimit());
                    detailBO.setOccupyGuaranteeLimit(item.getOccupyGuaranteeLimit());
                    detailBO.setOccupyCreditLimit(item.getOccupyCreditLimit());
                    return detailBO;
                }).collect(Collectors.toList());
                e.setOccupyDetailList(detailBOList);
            }
        });
    }

    private CreditLimit buildCreditLimitFromCreateBO(CreditLimitCreateBO creditLimitCreateBO, List<CreditLimitDetail> detailList) {
        CreditLimit creditLimit = new CreditLimit();
        creditLimit.setBizType(creditLimitCreateBO.getBizType());
        creditLimit.setGrantingSubjectKey(creditLimitCreateBO.getGrantSubjectKey());
        creditLimit.setBizSourceKey(creditLimitCreateBO.getBizSourceKey());
        creditLimit.setQueryKey(this.generateQueryKey(creditLimit.getBizType(), creditLimit.getGrantingSubjectKey(), creditLimit.getBizSourceKey()));
        creditLimit.setTotalLimit(creditLimitCreateBO.getTotalLimit());
        creditLimit.setGuaranteeLimit(creditLimitCreateBO.getGuaranteeLimit());
        creditLimit.setCreditLimit(creditLimitCreateBO.getCreditLimit());
        if (CollectionUtil.isEmpty(detailList)) {
            creditLimit.setOccupyTotalLimit(0L);
            creditLimit.setOccupyGuaranteeLimit(0L);
            creditLimit.setOccupyCreditLimit(0L);
        } else {
            long total = 0L;
            long guarantee = 0L;
            long credit = 0L;
            for (CreditLimitDetail creditLimitDetail : detailList) {
                total = total + creditLimitDetail.getOccupyTotalLimit();
                guarantee = guarantee + creditLimitDetail.getOccupyGuaranteeLimit();
                credit = credit + creditLimitDetail.getOccupyCreditLimit();
                creditLimit.setOccupyTotalLimit(total);
                creditLimit.setOccupyGuaranteeLimit(guarantee);
                creditLimit.setOccupyCreditLimit(credit);
            }
        }
        creditLimit.setEffectiveDateFrom(creditLimitCreateBO.getEffectiveDateFrom());
        creditLimit.setEffectiveDateTo(creditLimitCreateBO.getEffectiveDateTo());
        creditLimit.setRecyclable(creditLimitCreateBO.getRecyclable());
        // 根据起止日期决定状态
        creditLimit.setStatus(this.ensureCreditLimitStatus(creditLimit.getEffectiveDateFrom(), creditLimit.getEffectiveDateTo()).name());
        return creditLimit;
    }

    private CreditBusinessRef buildCreditBusinessRefFromCreditOccupyBO(CreditLimitOccupyBO creditLimitOccupyBO) {
        CreditBusinessRef creditBusinessRef = new CreditBusinessRef();
        creditBusinessRef.setBizType(creditLimitOccupyBO.getBizType());
        creditBusinessRef.setGrantingSubjectKey(creditLimitOccupyBO.getGrantSubjectKey());
        creditBusinessRef.setBizSourceKey(creditLimitOccupyBO.getBizSourceKey());
        creditBusinessRef.setBizTargetKey(creditLimitOccupyBO.getBizTargetKey());
        return creditBusinessRef;
    }

    private CreditLimitDetail buildCreditLimitDetailFromCreditOccupyBO(CreditLimitOccupyBO creditLimitOccupyBO) {
        CreditLimitDetail creditLimitDetail = new CreditLimitDetail();
        creditLimitDetail.setBizType(creditLimitOccupyBO.getBizType());
        creditLimitDetail.setGrantingSubjectKey(creditLimitOccupyBO.getGrantSubjectKey());
        creditLimitDetail.setBizTargetKey(creditLimitOccupyBO.getBizTargetKey());
        creditLimitDetail.setQueryKey(this.generateQueryKey(creditLimitDetail.getBizType(), creditLimitDetail.getGrantingSubjectKey(), creditLimitDetail.getBizTargetKey()));
        creditLimitDetail.setOccupyTotalLimit(creditLimitOccupyBO.getAmount());
        creditLimitDetail.setOccupyGuaranteeLimit(creditLimitOccupyBO.getGuaranteeAmount());
        creditLimitDetail.setOccupyCreditLimit(creditLimitDetail.getOccupyTotalLimit() - creditLimitDetail.getOccupyGuaranteeLimit());
        return creditLimitDetail;
    }

    private CreditLimitChangeRecord buildCreditLimitChangeRecordFromCreditOccupyBO(CreditLimitOccupyBO creditLimitOccupyBO) {
        CreditLimitChangeRecord creditLimitChangeRecord = new CreditLimitChangeRecord();
        creditLimitChangeRecord.setBizType(creditLimitOccupyBO.getBizType());
        creditLimitChangeRecord.setGrantingSubjectKey(creditLimitOccupyBO.getGrantSubjectKey());
        creditLimitChangeRecord.setBizSourceKey(creditLimitOccupyBO.getBizSourceKey());
        creditLimitChangeRecord.setBizTargetKey(creditLimitOccupyBO.getBizTargetKey());
        creditLimitChangeRecord.setQueryKey(this.generateQueryKey(creditLimitChangeRecord.getBizType(), creditLimitChangeRecord.getGrantingSubjectKey(), creditLimitChangeRecord.getBizTargetKey()));
        creditLimitChangeRecord.setChangeDate(creditLimitOccupyBO.getHappenDate());
        creditLimitChangeRecord.setChangeType(CreditLimitChangeTypeEnum.OCCUPY.name());
        creditLimitChangeRecord.setChangeTotalLimit(creditLimitOccupyBO.getAmount());
        creditLimitChangeRecord.setChangeGuaranteeLimit(creditLimitOccupyBO.getGuaranteeAmount());
        creditLimitChangeRecord.setChangeCreditLimit(creditLimitChangeRecord.getChangeTotalLimit() - creditLimitChangeRecord.getChangeGuaranteeLimit());
        return creditLimitChangeRecord;
    }

    private CreditLimitStatusEnum ensureCreditLimitStatus(LocalDate from, LocalDate to) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(from)) {
            return CreditLimitStatusEnum.WAIT_EFFECTIVE;
        } else if (now.isAfter(to)) {
            return CreditLimitStatusEnum.INVALID;
        } else {
            return CreditLimitStatusEnum.EFFECTIVE;
        }
    }

    private String generateQueryKey(String bizType, String grantingSubjectKey, String sourceOrTargetKey) {
        return bizType + "@" + grantingSubjectKey + "@" + sourceOrTargetKey;
    }

    private void validate(Object object) {
        Set<ConstraintViolation<Object>> validateResult = Validation.buildDefaultValidatorFactory().getValidator().validate(object);
        if (!validateResult.isEmpty()) {
            for (ConstraintViolation<Object> constraintViolation : validateResult) {
                throw new LackDataException(String.format("%s[%s]", constraintViolation.getMessage(), constraintViolation.getPropertyPath()));
            }
        }
    }

    private Long mithrasLongDecimalTwo(Long value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(10000L))
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(10000L))
                .longValue();
    }
}
