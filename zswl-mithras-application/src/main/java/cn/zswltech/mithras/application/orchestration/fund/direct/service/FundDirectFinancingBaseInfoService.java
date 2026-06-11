package cn.zswltech.mithras.application.orchestration.fund.direct.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundDirectFinancingMaterialsEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.monthly.enums.StampDutyTypeEnum;
import cn.zswltech.mithras.fund.direct.application.directfinancing.convert.FundDirectFinancingBaseInfoConverter;
import cn.zswltech.mithras.fund.direct.application.directfinancing.FundDirectFinancingAssetPoolService;
import cn.zswltech.mithras.fund.direct.application.directfinancing.FundDirectFinancingPayAccountService;
import cn.zswltech.mithras.fund.direct.mapper.model.*;
import cn.zswltech.mithras.fund.direct.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.mapper.CommonProcessPrepareMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.fund.application.financing.bo.ComprehensiveFinancingCostBO;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowPlanService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.FileService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlyStampDutyService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Service
@Slf4j
public class FundDirectFinancingBaseInfoService
        extends ServiceImpl<FundDirectFinancingBaseInfoMapper, FundDirectFinancingBaseInfo> {
    @Resource
    private FundDirectFinancingBaseInfoConverter baseInfoConverter;
    @Resource
    private FundDirectFinancingAssetPoolService assetPoolService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingAssetPoolService fundDirectFinancingAssetPoolService;
    @Resource
    private FundDirectFinancingFeeDetailService fundDirectFinancingFeeDetailService;
    @Resource
    private FundDirectFinancingPayAccountService fundDirectFinancingPayAccountService;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;
    @Resource
    private FundDirectFinancingSubscriptionDetailService fundDirectFinancingSubscriptionDetailService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private FundReceiptFlowPlanService fundReceiptFlowPlanService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private OssClient ossClient;
    @Resource
    private HttpServletResponse response;
    @Resource
    private FundFinancingService fundFinancingService;
    @Resource
    private FlowTaskApiService taskApiService;


    @Transactional(rollbackFor = Throwable.class)
    public Long add(FundDirectFinancingBaseInfoAddREQ req) {
        // 构造基本信息并插入
        FundDirectFinancingBaseInfo baseInfo = baseInfoConverter.addReq2Entity(req);
        String codePrefix = "ZR" + DateUtil.dateString(LocalDate.now(), DateUtil.DATE_PATTERN);
        Integer todayCount = baseMapper.selectCount(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .le(BaseModel::getCreateTime, DateUtil.endOfDay(LocalDate.now()))
                .ge(BaseModel::getCreateTime, DateUtil.startOfDay(LocalDate.now())));
        baseInfo.setFinancingCode(codePrefix + String.format("%02d", todayCount + 1));

        Long currentUserId = AccountUtil.getLoginInfo().getId();
        baseInfo.setFundManagerId(currentUserId);
        List<OrgDO> orgList;
        orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
        if (CollectionUtil.isEmpty(orgList)) {
            // 资金经理没有找到的话再找一下资金资金经理
            orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.deepmoneymanager.name());
        }
        Assert.notEmpty(orgList, () -> MithrasException.newException("没有找到当前用户作为资金经理所在部门"));
        Long deptId = orgList.get(0).getId();
        baseInfo.setDeptId(deptId);
//        // 固定是鲁桂欣
//        baseInfo.setBizHeaderId(65L);
        List<UserDO> headList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.moneymanagerhead.name());
        if (CollectionUtil.isEmpty(headList)) {
            throw new MithrasException("没有找到<资金业务负责人>岗位对应的人员信息");
        }
        baseInfo.setBizHeaderId(headList.get(0).getId());
        List<Long> leaderIds = sysUserService.queryJobUserIds(JobEnum.financialdirector.name());
        if (CollectionUtil.isNotEmpty(leaderIds)) {
            baseInfo.setLeaderId(leaderIds.get(0));
        }
        baseInfo.setFinancingStatus(FundFinancingStatusEnum.NEW.name());
        baseMapper.insert(baseInfo);

        //构造资产池信息
        FundDirectFinancingAssetPool assetPool = new FundDirectFinancingAssetPool();
        assetPool.setFinancingId(baseInfo.getId());
        assetPoolService.save(assetPool);

        monthlyStampDutyService.saveRecordDirectFin(baseInfo);
        return baseInfo.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingBaseInfoModifyREQ req) {
        FundDirectFinancingBaseInfo originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingBaseInfo baseInfo = baseInfoConverter.modifyReq2Entity(req);
        baseMapper.updateById(baseInfo);
        monthlyStampDutyService.saveRecordDirectFin(baseInfo);

    }

    public PageR<FundDirectFinancingBaseInfoListRSP> list(FundDirectFinancingBaseInfoListREQ req) {
        LambdaQueryWrapper<FundDirectFinancingBaseInfo> qw = Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .eq(ObjectUtil.isNotNull(req.getDirectFinancingType()), FundDirectFinancingBaseInfo::getDirectFinancingType, req.getDirectFinancingType())
                .eq(ObjectUtil.isNotEmpty(req.getFundManagerId()), FundDirectFinancingBaseInfo::getFundManagerId, req.getFundManagerId())
                .like(ObjectUtil.isNotEmpty(req.getProductName()), FundDirectFinancingBaseInfo::getProductName, req.getProductName())
                .ge(ObjectUtil.isNotEmpty(req.getFinancingAmountFrom()), FundDirectFinancingBaseInfo::getFinancingAmount, Optional.ofNullable(req.getFinancingAmountFrom()).map(m -> m / 10000).orElse(null))
                .le(ObjectUtil.isNotEmpty(req.getFinancingAmountTo()), FundDirectFinancingBaseInfo::getFinancingAmount, Optional.ofNullable(req.getFinancingAmountTo()).map(m -> m / 10000).orElse(null))
                .ge(ObjectUtil.isNotEmpty(req.getCreateTimeFrom()), BaseModel::getCreateTime, DateUtil.startOfDay(req.getCreateTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCreateTimeTo()), BaseModel::getCreateTime, DateUtil.endOfDay(req.getCreateTimeTo()))
                .ge(ObjectUtil.isNotEmpty(req.getUpdateTimeFrom()), BaseModel::getUpdateTime, DateUtil.startOfDay(req.getUpdateTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getUpdateTimeTo()), BaseModel::getUpdateTime, DateUtil.endOfDay(req.getUpdateTimeTo()))
                .like(StrUtil.isNotBlank(req.getFinancingCode()), FundDirectFinancingBaseInfo::getFinancingCode, req.getFinancingCode())
                .ge(ObjectUtil.isNotEmpty(req.getCarryInterestTimeFrom()), FundDirectFinancingBaseInfo::getCarryInterestTime, DateUtil.startOfDay(req.getCarryInterestTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCarryInterestTimeTo()), FundDirectFinancingBaseInfo::getCarryInterestTime, DateUtil.endOfDay(req.getCarryInterestTimeTo()))
                .ge(ObjectUtil.isNotEmpty(req.getDurationTimeFrom()), FundDirectFinancingBaseInfo::getDurationTime, DateUtil.startOfDay(req.getDurationTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getDurationTimeTo()), FundDirectFinancingBaseInfo::getDurationTime, DateUtil.endOfDay(req.getDurationTimeTo()))
                .in(ObjectUtil.isNotEmpty(req.getFinancingStatusList()), FundDirectFinancingBaseInfo::getFinancingStatus, req.getFinancingStatusList())
                .orderByDesc(BaseModel::getUpdateTime);
        Page<FundDirectFinancingBaseInfo> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), qw);
        if (ObjectUtil.isEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Set<Long> financingIds = page.getRecords().stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toSet());
        Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIds, FinancingTypeEnum.DIRECT);
        Set<Long> createByIds = page.getRecords().stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> createNames = id2NameService.sysUserId2Name(createByIds);

        List<FundDirectFinancingBaseInfoListRSP> rspList = new ArrayList<>();
        for (FundDirectFinancingBaseInfo record : page.getRecords()) {
            FundDirectFinancingBaseInfoListRSP rsp = baseInfoConverter.entity2ListRsp(record);
            rsp.setCreateByName(createNames.get(record.getCreateBy()));
            rsp.setRemainingAmount(remainingAmountMap.getOrDefault(record.getId(), 0L));
            rsp.setFinancingAmount(rsp.getFinancingAmount() * 10000L);
            rspList.add(rsp);
        }
        return PageR.of(page, rspList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void obsolete(Long id) {
        FundDirectFinancingBaseInfo originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        fundDirectFinancingPledgeInfoService.unLockContract(id);
        originalInfo.setFinancingStatus(financingStatusCheck(originalInfo, FundFinancingStatusEnum.CLOSE));
        originalInfo.setObsolete(true);
        updateById(originalInfo);

    }

    public FundDirectFinancingBaseInfoDetailRSP detail(Long id) {
        FundDirectFinancingBaseInfo baseInfo = baseMapper.selectById(id);
        if(baseInfo == null){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingBaseInfoDetailRSP rsp = baseInfoConverter.entity2DetailRsp(baseInfo);
        Set<Long> userIds = new HashSet<>();
        userIds.add(baseInfo.getFundManagerId());
        userIds.add(baseInfo.getBizHeaderId());
        userIds.add(baseInfo.getLeaderId());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        rsp.setFundManagerName(userNameMap.get(baseInfo.getFundManagerId()));
        rsp.setBizHeaderName(userNameMap.get(baseInfo.getBizHeaderId()));
        rsp.setLeaderName(userNameMap.get(baseInfo.getLeaderId()));
        String deptName = id2NameService.deptId2NameSingle(baseInfo.getDeptId());
        rsp.setDeptName(deptName);
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void sync(Long id) {
        FundDirectFinancingBaseInfo baseInfo = this.getById(id);
        //校验是否可以起息
        financingStatusCheck(baseInfo, FundFinancingStatusEnum.CARRY_INTEREST);
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(String.valueOf(id));
        processPageReq.setModelKey(ProcessModelTypeEnum.DirectFinancingCarryInterestFlow.name());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        if (Objects.nonNull(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
            throw new MithrasException("已有起息流程在审批中");
        }
        //流程发起
        fundFinancingService.startCarryInterest(id,baseInfo.getProductName(),ProcessModelTypeEnum.DirectFinancingCarryInterestFlow.name());
        // 更新状态--20260206版本改为发起流程后起息
//        update(Wrappers.<FundDirectFinancingBaseInfo>lambdaUpdate().eq(FundDirectFinancingBaseInfo::getId, id)
//                .set(FundDirectFinancingBaseInfo::getFinancingStatus, financingStatusCheck(baseInfo, FundFinancingStatusEnum.CARRY_INTEREST)));
//
//        List<FundReceiptRepayBaseInfo> receipt = fundReceiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
//                .eq(FundReceiptRepayBaseInfo::getFinancingId, id)
//                .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT"));
//        Long receiptRepayId;
//        if (ObjectUtil.isEmpty(receipt)) {
//            receiptRepayId = fundReceiptRepayBaseInfoService.addDirectFinancingReceipt(id);
//        } else {
//            receiptRepayId = fundReceiptRepayBaseInfoService.upgradeDirectFinancingReceipt(id);
//        }
//        // 发送弹窗消息给质押合同主办
//        directFinancingPledgeInfoService.sendPopUpMsg(id);
//        // 同步数据至新的统一现金流表，老表保留继续主要用于审批流历史快照及不改动老的一些业务逻辑
//        if (Objects.nonNull(receiptRepayId)) {
//            this.syncToFlowPlan(receiptRepayId);
//        }
//        // 向对应的资金经理推送归档的待办
//        startFileProcessDirectFinancing(baseInfo);
//        // 更新综合融资成本
//        updateFinancingCost(id);
//        //计算ftp
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//               try {
//                   SpringContextHolder.getBean(FtpIncomeBaseInfoService.class).createOrUpdate(baseInfo.getId(), FinancingTypeEnum.DIRECT.name(), null);
//               } catch (Exception e) {
//                   log.error("计算直融ftp收益失败 financingCode = {}, financingId = {}", baseInfo.getFinancingCode(), baseInfo.getId(), e);
//               }
//            }
//        });
    }

    public void syncToFlowPlan(Long receiptRepayId) {
        // 同步融资款
        fundReceiptFlowPlanService.syncFromBorrowing(receiptRepayId);
        // 同步还本付息
        fundReceiptFlowPlanService.syncFromRepay(receiptRepayId);
        // 同步保证金
        fundReceiptFlowPlanService.syncFromDeposit(receiptRepayId);
        // 同步费用项
        fundReceiptFlowPlanService.syncFromExpense(receiptRepayId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        FundReceiptRepayBaseInfo direct = fundReceiptRepayBaseInfoService.getOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, id)
                .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT").last("limit 1"));
        if (ObjectUtil.isNotNull(direct)) {
            throw new MithrasException("该直融已同步至还本付息，无法删除");
        }
        removeById(id);
        // 删子表数据
        fundDirectFinancingAssetPoolService.remove(
                Wrappers.<FundDirectFinancingAssetPool>lambdaQuery()
                        .eq(FundDirectFinancingAssetPool::getFinancingId, id));
        fundDirectFinancingPayAccountService.remove(
                Wrappers.<FundDirectFinancingPayAccount>lambdaQuery()
                        .eq(FundDirectFinancingPayAccount::getFinancingId, id));
        fundDirectFinancingFeeDetailService.remove(
                Wrappers.<FundDirectFinancingFeeDetail>lambdaQuery()
                        .eq(FundDirectFinancingFeeDetail::getFinancingId, id));
        fundDirectFinancingPledgeInfoService.remove(
                Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                        .eq(FundDirectFinancingPledgeInfo::getFinancingId, id));
        fundDirectFinancingRepayActualService.remove(
                Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                        .eq(FundDirectFinancingRepayActual::getFinancingId, id));
        fundDirectFinancingSubscriptionDetailService.remove(
                Wrappers.<FundDirectFinancingSubscriptionDetail>lambdaQuery()
                        .eq(FundDirectFinancingSubscriptionDetail::getFinancingId, id));
        monthlyStampDutyService.removeStampDuty(id, StampDutyTypeEnum.FIN_DIRECT_FIN);
    }

    public FundDirectFinancingBaseInfoListRSP sum(FundDirectFinancingBaseInfoListREQ req) {
        LambdaQueryWrapper<FundDirectFinancingBaseInfo> qw = Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .eq(ObjectUtil.isNotNull(req.getDirectFinancingType()), FundDirectFinancingBaseInfo::getDirectFinancingType, req.getDirectFinancingType())
                .eq(ObjectUtil.isNotEmpty(req.getFundManagerId()), FundDirectFinancingBaseInfo::getFundManagerId, req.getFundManagerId())
                .like(ObjectUtil.isNotEmpty(req.getProductName()), FundDirectFinancingBaseInfo::getProductName, req.getProductName())
                .ge(ObjectUtil.isNotEmpty(req.getFinancingAmountFrom()), FundDirectFinancingBaseInfo::getFinancingAmount, Optional.ofNullable(req.getFinancingAmountFrom()).map(m -> m / 10000).orElse(null))
                .le(ObjectUtil.isNotEmpty(req.getFinancingAmountTo()), FundDirectFinancingBaseInfo::getFinancingAmount, Optional.ofNullable(req.getFinancingAmountTo()).map(m -> m / 10000).orElse(null))
                .ge(ObjectUtil.isNotEmpty(req.getCreateTimeFrom()), BaseModel::getCreateTime, DateUtil.startOfDay(req.getCreateTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCreateTimeTo()), BaseModel::getCreateTime, DateUtil.endOfDay(req.getCreateTimeTo()))
                .ge(ObjectUtil.isNotEmpty(req.getUpdateTimeFrom()), BaseModel::getUpdateTime, DateUtil.startOfDay(req.getUpdateTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getUpdateTimeTo()), BaseModel::getUpdateTime, DateUtil.endOfDay(req.getUpdateTimeTo()))
                .like(StrUtil.isNotBlank(req.getFinancingCode()), FundDirectFinancingBaseInfo::getFinancingCode, req.getFinancingCode())
                .ge(ObjectUtil.isNotEmpty(req.getCarryInterestTimeFrom()), FundDirectFinancingBaseInfo::getCarryInterestTime, DateUtil.startOfDay(req.getCarryInterestTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getCarryInterestTimeTo()), FundDirectFinancingBaseInfo::getCarryInterestTime, DateUtil.endOfDay(req.getCarryInterestTimeTo()))
                .ge(ObjectUtil.isNotEmpty(req.getDurationTimeFrom()), FundDirectFinancingBaseInfo::getDurationTime, DateUtil.startOfDay(req.getDurationTimeFrom()))
                .le(ObjectUtil.isNotEmpty(req.getDurationTimeTo()), FundDirectFinancingBaseInfo::getDurationTime, DateUtil.endOfDay(req.getDurationTimeTo()))
                .in(ObjectUtil.isNotEmpty(req.getFinancingStatusList()), FundDirectFinancingBaseInfo::getFinancingStatus, req.getFinancingStatusList())
                .orderByDesc(BaseModel::getUpdateTime);
        List<FundDirectFinancingBaseInfo> all = baseMapper.selectList(qw);
        Set<Long> financingIds = all.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(financingIds)) {
            // 如果没有融资就返回0
            FundDirectFinancingBaseInfoListRSP sum = new FundDirectFinancingBaseInfoListRSP();
            sum.setRemainingAmount(0L);
            sum.setFinancingAmount(0L);
            return sum;
        }
        Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(financingIds, FinancingTypeEnum.DIRECT);
        BigDecimal remainingAmount = BigDecimal.ZERO;
        BigDecimal financingAmount = BigDecimal.ZERO;
        BigDecimal financingCostSum = BigDecimal.ZERO;
        BigDecimal averageCouponRateSum = BigDecimal.ZERO;
        for (FundDirectFinancingBaseInfo record : all) {
            BigDecimal newRemainingAmount = new BigDecimal(remainingAmountMap.getOrDefault(record.getId(), 0L));
            remainingAmount = remainingAmount.add(newRemainingAmount);
            financingAmount = financingAmount.add(new BigDecimal(record.getFinancingAmount() * 10000L));
            // 综合融资成本合计: sum（剩余本金*综合融资成本）/剩余本金之和
            if(record.getComprehensiveFinancingCost() != null) {
                financingCostSum = financingCostSum.add(newRemainingAmount.multiply(new BigDecimal(record.getComprehensiveFinancingCost())));
            }
            // 票面加权利率
            averageCouponRateSum = averageCouponRateSum.add(new BigDecimal(Optional.ofNullable(record.getAverageCouponRate()).orElse(0L)));
        }
        FundDirectFinancingBaseInfoListRSP sum = new FundDirectFinancingBaseInfoListRSP();
        if(remainingAmount.equals(BigDecimal.ZERO)){
            sum.setComprehensiveFinancingCost(0L);
        }else{
            sum.setComprehensiveFinancingCost(financingCostSum.divide(remainingAmount, 4, RoundingMode.HALF_UP).longValue());
        }
        if(CollectionUtil.isNotEmpty(all)){
            sum.setAverageCouponRate(averageCouponRateSum.divide(new BigDecimal(all.size()), 4, RoundingMode.HALF_UP).longValue());
        }
        sum.setRemainingAmount(remainingAmount.longValue());
        sum.setFinancingAmount(financingAmount.longValue());
        return sum;
    }

    public void settle(FundDirectFinancingSingleIdREQ req) {
        FundDirectFinancingBaseInfo financingBaseInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(financingBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(!Objects.equals(AccountUtil.getLoginInfo().getId() ,financingBaseInfo.getFundManagerId())){
            throw new MithrasException("仅合同对应的资金经理可操作");
        }
        fundDirectFinancingPledgeInfoService.unLockContract(financingBaseInfo.getId());
        financingBaseInfo.setFinancingStatus(financingStatusCheck(financingBaseInfo, FundFinancingStatusEnum.SETTLE));
        updateById(financingBaseInfo);

    }


    /**
     * 校验状态
     * @param baseInfo
     * @param targetStatus 目标状态
     * @return
     */
    private String financingStatusCheck(FundDirectFinancingBaseInfo baseInfo, FundFinancingStatusEnum targetStatus){
        String financingStatus = baseInfo.getFinancingStatus();
        switch (targetStatus){
            case CARRY_INTEREST:
                if(!CharSequenceUtil.equalsAny(financingStatus, FundFinancingStatusEnum.NEW.name(), FundFinancingStatusEnum.CARRY_INTEREST.name())){
                    throw new MithrasException("仅新建、起息合同方可操作");
                }
                break;
            case SETTLE:
                if(!CharSequenceUtil.equals(financingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())){
                    throw new MithrasException("仅起息合同才能结清");
                }
                break;
            case CLOSE:
                if(!CharSequenceUtil.equals(financingStatus, FundFinancingStatusEnum.NEW.name())){
                    throw new MithrasException("仅新建合同才能关闭");
                }
                break;
        }
        return targetStatus.name();
    }

    public void startFileProcessDirectFinancing(FundDirectFinancingBaseInfo directFinancingBaseInfo){

//        String fundManagerName = id2NameService.sysUserId2NameSingle(directFinancingBaseInfo.getFundManagerId());
        CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                .processType(ProcessModelTypeEnum.DirectFinancingRecordFlow.name())
                .businessId(String.valueOf(directFinancingBaseInfo.getId()))
                .formName(String.format("%s的%s万元融资档案归档", directFinancingBaseInfo.getProductName(),
                        Util.toWanYuan(directFinancingBaseInfo.getFinancingAmount() * 10000)))
                .projName(null)
                .clientName(null)
                .currentAssignee(JSON.toJSONString(Collections.singletonList(directFinancingBaseInfo.getFundManagerId())))
                .currentNode("档案归档")
                .applyTime(LocalDateTime.now())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareMapper.insert(prepare);

    }

    /**
     * 更新综合融资成本
     * @param financingId
     */
    public void updateFinancingCost(Long financingId){
        ComprehensiveFinancingCostBO bo = new ComprehensiveFinancingCostBO();
        bo.setFinancingType(FinancingTypeEnum.DIRECT.name());
        bo.setFinancingId(financingId);
        Long contractRate = null;
        try {
            contractRate = receiptRepayBaseInfoService.calculateFundContractRate(bo);
        }catch (MithrasException me){
            log.error("{}",me.getMessage(),  me);
            throw new MithrasException("计算综合融资成本发生异常" + me.getMessage());
        } catch (Exception e){
            log.error("{}",e.getMessage(), e);
            throw new MithrasException("计算综合融资成本发生未知异常");
        }
        this.update(Wrappers.<FundDirectFinancingBaseInfo>lambdaUpdate()
                .eq(FundDirectFinancingBaseInfo::getId, financingId)
                .set(FundDirectFinancingBaseInfo::getComprehensiveFinancingCost, contractRate));
    }

    /**
     * 更新到期日
     * @param financingId
     * @param durationTime
     */
    public void updateDurationTime(Long financingId, LocalDate durationTime) {
        if(financingId == null || durationTime == null){
            return;
        }
        this.update(Wrappers.<FundDirectFinancingBaseInfo>lambdaUpdate()
                .eq(FundDirectFinancingBaseInfo::getId, financingId)
                .set(FundDirectFinancingBaseInfo::getDurationTime, durationTime));
    }

    public void batchDownload(@Valid FundFinancingBatchDownloadREQ req){
        StopWatch st = new StopWatch("资料批量下载");
        st.start("数据查询");
        List<MaterialsList> materialsListList = getBean(MaterialsListMapper.class).selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, req.getId())
                .eq(MaterialsList::getBusinessType, req.getModuleCode())
                .in(CollUtil.isNotEmpty(req.getFileIds()), MaterialsList::getId, req.getFileIds()));
        Assert.notEmpty(materialsListList, () -> MithrasException.newException("没有找到任何文件记录"));
        st.stop();
        //保留原下载校验逻辑
        getBean(FileService.class).checkBatchDownload(req.getModuleCode(),req.getId(),materialsListList.stream().map(MaterialsList::getId).collect(Collectors.toList()));
        st.start("文件压缩下载");
        String rootPath = "资料清单";
        Map<String, List<MaterialsList>> listMap = materialsListList.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        String fileName = getFileName(req.getModuleCode(),req.getId());
        String clientPath ;
        try (OutputStream out = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(rootPath + ".zip", StandardCharsets.UTF_8.name()));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            Set<String> pathSet = Collections.synchronizedSet(new LinkedHashSet<>());
            List<String> pathList = new CopyOnWriteArrayList<>();
            //根据一级文件目录分组
            for (Map.Entry<String, List<MaterialsList>> entry : listMap.entrySet()) {
                String materialsTypeName = getMaterialsTypeName(req.getModuleCode(),entry.getKey());
                clientPath = rootPath + File.separator + materialsTypeName;
                this.download(zipOut, entry.getValue(), clientPath,materialsTypeName,fileName, pathList, pathSet);
            }
            zipOut.finish();
            zipOut.flush();
            out.flush();
            response.flushBuffer();
        } catch (Exception e) {
            log.error("批量下载失败", e);
            throw MithrasException.newException("文件批量下载失败：" + e.getMessage());
        } finally {
            st.stop();
            log.info("资金批量下载 batchDownload {}", st.prettyPrint(TimeUnit.MILLISECONDS));
        }
    }

    public void download(@Valid FileDownloadREQ fileDownloadREQ){
        MaterialsList materialsList = getBean(MaterialsListService.class).getById(fileDownloadREQ.getFileId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应的文件");
        }
        getBean(FileService.class).checkBatchDownload(materialsList.getBusinessType(), materialsList.getBelongId(), Collections.singletonList(materialsList.getId()));
        String overrideFileName = getFileName(materialsList.getBusinessType(), materialsList.getBelongId());
        String materialsTypeName = getMaterialsTypeName(materialsList.getBusinessType(), materialsList.getMaterialsType());
        String fileName = overrideFileName + "-" + materialsTypeName + "-" + materialsList.getFilename();
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            ossClient.downLoad(out, CharSequenceUtil.join("/", materialsList.getOssFilename()));
            log.info("资金资料单个文件下载成功:" + fileName);
        } catch (Exception e) {
            log.error("资金资料单个文件下载失败:{},下载失败原因:{}" + fileName + e.getMessage());
            throw MithrasException.newException("文件下载失败：" + e.getMessage());
        }
    }

    private String getFileName(String businessType,Long id){
        if (Objects.equals(BusinessModuleEnum.FUND_DIRECT_FINANCING.name(), businessType)) {
            //直融
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = this.getById(id);
            Assert.notNull(fundDirectFinancingBaseInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
            return fundDirectFinancingBaseInfo.getFinancingCode() + "-" + fundDirectFinancingBaseInfo.getProductName();
        } else {
            return getBean(FundFinancingBaseInfoService.class).getFileName(id);
        }
    }

    private String getMaterialsTypeName(String businessType,String materialsType){
        if (Objects.equals(BusinessModuleEnum.FUND_DIRECT_FINANCING.name(), businessType)) {
            //直融
            return Optional.ofNullable(FundDirectFinancingMaterialsEnum.getByName(materialsType))
                    .map(FundDirectFinancingMaterialsEnum::getDisplay).orElse("/");
        } else {
            return Optional.ofNullable(FundFinancingMaterialsEnum.getByName(materialsType))
                    .map(FundFinancingMaterialsEnum::getDisplay).orElse("/");
        }
    }

    public void download(ZipOutputStream zipOut, List<MaterialsList> clientMaterials, String clientPath,
                          String materialsTypeName, String overrideFileNamePrefix, List<String> pathList, Set<String> pathSet){
        /*重复文件名替换*/
        getBean(FilingMaterialsService.class).repeatFileNameReplace(clientMaterials);
        for (MaterialsList material : clientMaterials) {
            //文件名重写
            String fileName = overrideFileNamePrefix + "-" + materialsTypeName +"-" + material.getFilename();
            // 拼接文件路径
            String filePath = clientPath + File.separator + fileName;
            // 获取文件流
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ossClient.downLoad(byteArrayOutputStream, CharSequenceUtil.join("/", material.getOssFilename()));
                ZipEntry zEntry = new ZipEntry(filePath);
                zipOut.putNextEntry(zEntry);
                byteArrayOutputStream.writeTo(zipOut);
                zipOut.closeEntry();
                log.info("文件写入zip成功:"+filePath);
            } catch (Exception e){
                log.error("文件写入zip失败:{},写入失败原因:{}"+filePath + e.getMessage());
            }
        }
    }

}
