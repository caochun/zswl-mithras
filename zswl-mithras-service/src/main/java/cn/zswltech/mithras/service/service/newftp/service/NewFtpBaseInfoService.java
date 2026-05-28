package cn.zswltech.mithras.service.service.newftp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.newftp.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.ftp.FtpBusinessVersion;
import cn.zswltech.mithras.service.enums.ftp.FtpProcessStatus;
import cn.zswltech.mithras.service.enums.newftp.PricingFrequencyEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.newftp.convert.NewFtpBaseInfoConverter;
import cn.zswltech.mithras.service.service.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.service.service.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.service.service.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.service.service.newftp.model.config.*;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpShiborInterestRateDraft;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpShiborInterestRatePricingDraft;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpTreasuryBondYieldDraft;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpTreasuryBondYieldPricingDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.*;
import cn.zswltech.mithras.service.service.newftp.service.config.*;
import cn.zswltech.mithras.service.service.newftp.service.drift.*;
import cn.zswltech.mithras.service.service.newftp.service.lib.*;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.NEW_FTP_GUIDANCE;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@Service
public class NewFtpBaseInfoService extends ServiceImpl<NewFtpBaseInfoMapper, NewFtpBaseInfo> {
    @Resource
    private NewFtpMonthlyDeductionDraftService monthlyDeductionDraftService;
    @Resource
    private NewFtpMonthlyGuidanceDraftService monthlyGuidanceDraftService;
    @Resource
    private NewFtpMonthlyGuidanceExtDraftService monthlyGuidanceExtDraftService;
    @Resource
    private NewFtpQuarterlyBasePricingDraftService quarterlyBasePricingDraftService;
    @Resource
    private NewFtpBaseInfoConverter baseInfoConverter;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private NewFtpDescriptionTextDraftService descriptionTextDraftService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;

    @Resource
    private NewFtpTreasuryBondYieldDraftService newFtpTreasuryBondYieldDraftService;
    @Resource
    private NewFtpShiborInterestRateDraftService newFtpShiborInterestRateDraftService;
    @Resource
    private NewFtpLprPricingDraftService newFtpLprPricingDraftService;
    @Resource
    private NewFtpGuaranteeCostPricingDraftService newFtpGuaranteeCostPricingDraftService;
    @Resource
    private NewFtpFinancingCostPricingDraftService financingCostPricingDraftService;

    @Resource
    private FlowTaskApiService taskApiService;

    public ProcessResp findRelatedProcess(Long mainId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(mainId));
        processPageReq.setModelKeyList(NEW_FTP_GUIDANCE.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public PageR<NewFtpBaseInfoListRSP> list(NewFtpBaseInfoListREQ req) {
        QueryWrapper<NewFtpBaseInfo> queryWrapper = new QueryWrapper<>();
        if (req.getMonth() != null) {
            queryWrapper.eq("month", req.getMonth());
        }
        if (req.getFtpProcessStatus() != null) {
            queryWrapper.eq("ftp_process_status", req.getFtpProcessStatus());
        }
        if (req.getCreateBy() != null) {
            queryWrapper.eq("create_by", req.getCreateBy());
        }
        if (req.getCreateTimeFrom() != null) {
            queryWrapper.ge("create_time", DateUtil.startOfDay(req.getCreateTimeFrom()));
        }
        if (req.getCreateTimeTo() != null) {
            queryWrapper.le("create_time", DateUtil.endOfDay(req.getCreateTimeTo()));
        }
        if (req.getEffectTimeFrom() != null) {
            queryWrapper.ge("effect_time", DateUtil.startOfDay(req.getEffectTimeFrom()));
        }
        if (req.getEffectTimeTo() != null) {
            queryWrapper.le("effect_time", DateUtil.endOfDay(req.getEffectTimeTo()));
        }
        if (!specialUser()) {
            queryWrapper.eq("ftp_record_status", "TAKE_EFFECT");
        }
        queryWrapper.orderByDesc("create_time");
        Page<NewFtpBaseInfo> page = page(new Page<>(req.getPage(), req.getPageSize()), queryWrapper);
        Set<Long> createByIds = page.getRecords().stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(createByIds);

        List<NewFtpBaseInfoListRSP> rspList = page.getRecords().stream().map(info -> {
            NewFtpBaseInfoListRSP rsp = baseInfoConverter.entity2ListRsp(info);
            rsp.setFtpStatus(info.getFtpRecordStatus());
            rsp.setCreateByName(userId2Name.get(info.getCreateBy()));
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(page, rspList);
    }

    @Resource
    private SysUserService sysUserService;

    /**
     * 计划财务部及定价委员会成员
     *
     * @return true
     */
    private boolean specialUser() {
        return sysUserService.currentUserIsSpecificDept("JHCWB", "DJWYH");
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long add(NewFtpBaseInfoAddREQ req) {
        // 查重
        NewFtpBaseInfo duplicateCheck = getOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .eq(NewFtpBaseInfo::getMonth, req.getMonth())
                .eq(NewFtpBaseInfo::getPricingFrequency, req.getPricingFrequency())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(duplicateCheck)) {
            throw new MithrasException("已存在该月份的数据");
        }
        LocalDate thisMonth = req.getMonth();
        NewFtpBaseInfo info = new NewFtpBaseInfo();
        info.setMonth(thisMonth);
        info.setFtpProcessStatus(FtpProcessStatus.NEW_UN_SUBMIT.name());
        info.setFtpRecordStatus(RecordStatus.NEW.name());
        PricingFrequencyEnum frequencyEnum = PricingFrequencyEnum.ofName(req.getPricingFrequency());
        if (frequencyEnum != null) {
            info.setPricingFrequency(frequencyEnum.name());
        }
        if (info.getPricingFrequency() == null) {
            throw new MithrasException("定价频率参数错误，请检查!");
        }
        info.setFtpBusinessVersion(FtpBusinessVersion.getLatestVersion().name());
        baseMapper.insert(info);
        //拉取基础数据
        newFtpLprPricingDraftService.addTreasuryBondYield(info.getId());
        newFtpTreasuryBondYieldDraftService.addTreasuryBondYield(info.getId());
        newFtpShiborInterestRateDraftService.addShiborInterestRate(info.getId());
        //担保成本
        newFtpGuaranteeCostPricingDraftService.add(info.getId());
        //融资成本
        financingCostPricingDraftService.add(info.getId());
        return info.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void calculate(Long mainId) {
        NewFtpBaseInfo baseInfo = getById(mainId);
        if (baseInfo == null) {
            throw new MithrasException("未找到该月份的数据");
        }
        // 计算月度ftp数据
        monthlyGuidanceDraftService.calculate(mainId);
        monthlyGuidanceExtDraftService.calculate(mainId);
        // 计算季度指导数据
        if (PricingFrequencyEnum.QUARTER.name().equals(baseInfo.getPricingFrequency())) {
            quarterlyBasePricingDraftService.calculate(baseInfo);
        }
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
    }

    public void copyConfig() {
        //融资成本
        NewFtpBaseInfo baseInfo = baseMapper.selectOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .isNotNull(NewFtpBaseInfo::getNewestVersion)
                .orderByDesc(NewFtpBaseInfo::getNewestVersion)
                .last(StringUtil.mysqlLimitOne()));

        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        List<NewFtpFinancingCostPricingLib> financingCostPricingLibs = SpringContextHolder.getBean(NewFtpFinancingCostPricingLibService.class)
                .list(Wrappers.<NewFtpFinancingCostPricingLib>lambdaQuery()
                        .eq(NewFtpFinancingCostPricingLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(NewFtpFinancingCostPricingLib::getFtpId, baseInfo.getId())
                        .eq(NewFtpFinancingCostPricingLib::getVersion, baseInfo.getNewestVersion()));

        List<NewFtpFinancingCostPricingConfig> newFtpFinancingCostPricingConfigs = BeanUtil.copyToList(financingCostPricingLibs, NewFtpFinancingCostPricingConfig.class);
        if (CollUtil.isNotEmpty(financingCostPricingLibs)) {
            //删除原始配置
            NewFtpFinancingCostPricingConfigService costPricingConfigService = SpringContextHolder.getBean(NewFtpFinancingCostPricingConfigService.class);
            costPricingConfigService.remove(null);
            costPricingConfigService.saveBatch(newFtpFinancingCostPricingConfigs);
        }

        //担保成本
        List<NewFtpGuaranteeCostPricingLib> guaranteeCostPricingLibs = SpringContextHolder.getBean(NewFtpGuaranteeCostPricingLibService.class)
                .list(Wrappers.<NewFtpGuaranteeCostPricingLib>lambdaQuery()
                        .eq(NewFtpGuaranteeCostPricingLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(NewFtpGuaranteeCostPricingLib::getFtpId, baseInfo.getId())
                        .eq(NewFtpGuaranteeCostPricingLib::getVersion, baseInfo.getNewestVersion()));

        List<NewFtpGuaranteeCostPricingConfig> newFtpGuaranteeCostPricingLibs = BeanUtil.copyToList(guaranteeCostPricingLibs, NewFtpGuaranteeCostPricingConfig.class);
        if (CollUtil.isNotEmpty(guaranteeCostPricingLibs)) {
            //删除原始配置
            NewFtpGuaranteeCostPricingConfigService guaranteeCostPricingConfigService = SpringContextHolder.getBean(NewFtpGuaranteeCostPricingConfigService.class);
            guaranteeCostPricingConfigService.remove(null);
            guaranteeCostPricingConfigService.saveBatch(newFtpGuaranteeCostPricingLibs);
        }

        //十年期国债利率
        List<NewFtpTreasuryBondYieldLib> treasuryBondYieldLibs = SpringContextHolder.getBean(NewFtpTreasuryBondYieldLibService.class)
                .list(Wrappers.<NewFtpTreasuryBondYieldLib>lambdaQuery()
                        .eq(NewFtpTreasuryBondYieldLib::getVersion, baseInfo.getNewestVersion())
                        .eq(NewFtpTreasuryBondYieldLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(NewFtpTreasuryBondYieldDraft::getFtpId, baseInfo.getId()));
        if (CollUtil.isNotEmpty(treasuryBondYieldLibs)) {
            NewFtpTreasuryBondYieldConfigService treasuryBondYieldConfigService = SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class);
            treasuryBondYieldConfigService.remove(null);
            treasuryBondYieldConfigService.saveBatch(BeanUtil.copyToList(treasuryBondYieldLibs, NewFtpTreasuryBondYieldConfig.class));
        }

        List<NewFtpTreasuryBondYieldPricingLib> bondYieldPricingLibs = SpringContextHolder.getBean(NewFtpTreasuryBondYieldPricingLibService.class)
                .list(Wrappers.<NewFtpTreasuryBondYieldPricingLib>lambdaQuery()
                        .eq(NewFtpTreasuryBondYieldPricingLib::getVersion, baseInfo.getNewestVersion())
                        .eq(NewFtpTreasuryBondYieldPricingLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(NewFtpTreasuryBondYieldPricingDraft::getFtpId, baseInfo.getId()));
        if (CollUtil.isNotEmpty(bondYieldPricingLibs)) {
            NewFtpTreasuryBondYieldPricingConfigService yieldPricingConfigService = SpringContextHolder.getBean(NewFtpTreasuryBondYieldPricingConfigService.class);
            yieldPricingConfigService.remove(null);
            yieldPricingConfigService.saveBatch(BeanUtil.copyToList(bondYieldPricingLibs, NewFtpTreasuryBondYieldPricingConfig.class));
        }

        //一年期SHIBOR利率
        List<NewFtpShiborInterestRateLib> shiborInterestRateLibList = SpringContextHolder.getBean(NewFtpShiborInterestRateLibService.class)
                .list(Wrappers.<NewFtpShiborInterestRateLib>lambdaQuery()
                        .eq(NewFtpShiborInterestRateLib::getVersion, baseInfo.getNewestVersion())
                        .eq(NewFtpShiborInterestRateLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(NewFtpShiborInterestRateDraft::getFtpId, baseInfo.getId()));
        if (CollUtil.isNotEmpty(shiborInterestRateLibList)) {
            NewFtpShiborInterestRateConfigService shiborInterestRateConfigService = SpringContextHolder.getBean(NewFtpShiborInterestRateConfigService.class);
            shiborInterestRateConfigService.remove(null);
            shiborInterestRateConfigService.saveBatch(BeanUtil.copyToList(shiborInterestRateLibList, NewFtpShiborInterestRateConfig.class));
        }

        List<NewFtpShiborInterestRatePricingLib> shiborInterestRatePricingLibs = SpringContextHolder.getBean(NewFtpShiborInterestRatePricingLibService.class)
                .list(Wrappers.<NewFtpShiborInterestRatePricingLib>lambdaQuery()
                        .eq(NewFtpShiborInterestRatePricingLib::getVersion, baseInfo.getNewestVersion())
                        .eq(NewFtpShiborInterestRatePricingLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(NewFtpShiborInterestRatePricingDraft::getFtpId, baseInfo.getId()));
        if (CollUtil.isNotEmpty(shiborInterestRatePricingLibs)) {
            NewFtpShiborInterestRatePricingConfigService shiborInterestRatePricingConfigService = SpringContextHolder.getBean(NewFtpShiborInterestRatePricingConfigService.class);
            shiborInterestRatePricingConfigService.remove(null);
            shiborInterestRatePricingConfigService.saveBatch(BeanUtil.copyToList(shiborInterestRatePricingLibs, NewFtpShiborInterestRatePricingConfig.class));
        }
    }

    //获取最新生效的FTP收益率
    public NewFtpMonthlyGuidanceExtDraftDetailRSP getLastFtpMonthlyGuidance() {
        return this.getLastFtpMonthlyGuidance(LocalDate.now());
    }
    public NewFtpMonthlyGuidanceExtDraftDetailRSP getLastFtpMonthlyGuidance(LocalDate lastMonth) {
        NewFtpBaseInfo newFtpBaseInfo = this.baseMapper.selectOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .le(NewFtpBaseInfo::getMonth, lastMonth)
                .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                .orderByDesc(NewFtpBaseInfo::getMonth)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(newFtpBaseInfo)) {
            return null;
        }
        return monthlyGuidanceExtDraftService.detail(newFtpBaseInfo.getId());
    }
}