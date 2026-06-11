package cn.zswltech.mithras.ftp.newftp.service.draft;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingModifyREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpFinancingCostPricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpFinancingCostPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpFinancingCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpFinancingCostPricingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpFinancingCostPricingLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 融资成本定价
 * @date 2023-05-21
 */
@Service
public class NewFtpFinancingCostPricingDraftService extends ServiceImpl<NewFtpFinancingCostPricingDraftMapper, NewFtpFinancingCostPricingDraft> {

    @Resource
    private NewFtpFinancingCostPricingConfigService financingCostPricingConfigService;
    @Resource
    private NewFtpBaseInfoService baseInfoService;
    @Resource
    private NewFtpFinancingCostPricingLibService newFtpFinancingCostPricingLibService;

    /**
     * 添加或者更新
     */
    @Transactional(rollbackFor = Throwable.class)
    public void add(Long mainId) {
        NewFtpBaseInfo byId = baseInfoService.getById(mainId);
        if (ObjectUtil.isEmpty(byId)) {
            throw new MithrasException("ftp记录不存在");
        }
        //删除编辑区存在数据
        this.remove(Wrappers.<NewFtpFinancingCostPricingDraft>lambdaQuery().eq(NewFtpFinancingCostPricingDraft::getFtpId, mainId));

        //存在数据，先全量拷贝
        List<NewFtpFinancingCostPricingConfig> ftpFinancingCostPricingConfigs = financingCostPricingConfigService.list();
        if (ObjectUtil.isEmpty(ftpFinancingCostPricingConfigs)) {
            throw new MithrasException("缺少上个月的融资成本指标，请前往【基础数据】维护！");
        }
        List<NewFtpFinancingCostPricingDraft> list = new ArrayList<>(ftpFinancingCostPricingConfigs.size());
        ftpFinancingCostPricingConfigs.forEach(financingConfig -> {
            NewFtpFinancingCostPricingDraft pricingDraft = BeanUtil.copyProperties(financingConfig, NewFtpFinancingCostPricingDraft.class, "id");
            pricingDraft.setFtpId(mainId);
            list.add(pricingDraft);
        });
        this.saveBatch(list);
    }

    public PageR<NewFtpFinancingCostPricingListRSP> list(NewFtpCommonDetailReq req) {
        //查询条数
        Page<NewFtpFinancingCostPricingDraft> newFtpLprPricingPage;
        if (ObjectUtil.isEmpty(req.getVersion())) {
            newFtpLprPricingPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpFinancingCostPricingDraft>lambdaQuery()
                    .select(NewFtpFinancingCostPricingDraft::getMonth)
                    .eq(NewFtpFinancingCostPricingDraft::getFtpId, req.getMainId())
                    .groupBy(NewFtpFinancingCostPricingDraft::getMonth)
                    .orderByDesc(NewFtpFinancingCostPricingDraft::getMonth));
            //查询所有
            List<LocalDate> months = newFtpLprPricingPage.getRecords().stream().map(NewFtpFinancingCostPricingDraft::getMonth).collect(Collectors.toList());
            if (ObjectUtil.isEmpty(months)) {
                return null;
            }
            List<NewFtpFinancingCostPricingDraft> pricingDraftList = baseMapper.selectList(Wrappers.<NewFtpFinancingCostPricingDraft>lambdaQuery()
                    .eq(NewFtpFinancingCostPricingDraft::getFtpId, req.getMainId())
                    .in(NewFtpFinancingCostPricingDraft::getMonth, months)
                    .orderByDesc(NewFtpFinancingCostPricingDraft::getMonth));
            newFtpLprPricingPage.setRecords(pricingDraftList);
        } else {
            newFtpLprPricingPage = newFtpFinancingCostPricingLibService.getByVersion(req.getMainId(), req.getVersion(), req.getPage(), req.getPageSize());
        }

        //查询所有
        if (ObjectUtil.isEmpty(newFtpLprPricingPage) || ObjectUtil.isEmpty(newFtpLprPricingPage.getRecords())) {
            return null;
        }
        Map<LocalDate, List<NewFtpFinancingCostPricingDraft>> allMap = newFtpLprPricingPage.getRecords()
                .stream().collect(Collectors.groupingBy(NewFtpFinancingCostPricingDraft::getMonth));
        List<NewFtpFinancingCostPricingListRSP> rspList = new ArrayList<>();
        allMap.forEach((k, v) -> {
            NewFtpFinancingCostPricingListRSP rsp = new NewFtpFinancingCostPricingListRSP();
            rsp.setMonth(k);
            rsp.setBodyMap(
                    BeanUtil.copyToList(v, NewFtpFinancingCostPricingListRSP.NewFtpFinancingBody.class).stream()
                            .collect(Collectors.toMap(NewFtpFinancingCostPricingListRSP.NewFtpFinancingBody::getTermRange,
                                    Function.identity(), (a, b) -> a)));
            rspList.add(rsp);
        });
        return PageR.of(rspList, allMap.size(), newFtpLprPricingPage.getTotal(), newFtpLprPricingPage.getCurrent(), newFtpLprPricingPage.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public R<Void> modify(NewFtpFinancingCostPricingModifyREQ req) {
        if (ObjectUtil.isNull(req.getMainId())) {
            throw new MithrasException("ftpId不能为空");
        }
        NewFtpBaseInfo baseInfo = baseInfoService.getById(req.getMainId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LocalDate nowDate = req.getMonth().with(TemporalAdjusters.firstDayOfMonth());
        List<NewFtpFinancingCostPricingDraft> list = this.list(Wrappers.<NewFtpFinancingCostPricingDraft>lambdaQuery()
                .eq(NewFtpFinancingCostPricingDraft::getMonth, nowDate)
                .eq(NewFtpFinancingCostPricingDraft::getFtpId, req.getMainId())
                .orderByAsc(NewFtpFinancingCostPricingDraft::getMonth));
        if (ObjectUtil.isEmpty(list)) {
            throw new MithrasException("无此月数据");
        }
        List<NewFtpFinancingCostPricingDraft> updateList = new ArrayList<>();
        Map<String, NewFtpFinancingCostPricingDraft> ftpFinancingCostMap = list.stream().collect(Collectors.toMap(NewFtpFinancingCostPricingDraft::getTermRange, Function.identity(), (a, b) -> a));
        NewFtpFinancingCostPricingDraft oneFtpLprPricing = ftpFinancingCostMap.get(TermRange.ONE_YEAR.name());
        NewFtpFinancingCostPricingDraft one2ThreeFtpLprPricing = ftpFinancingCostMap.get(TermRange.ONE_TO_THREE_YEARS.name());
        NewFtpFinancingCostPricingDraft three2FiveFtpLprPricing = ftpFinancingCostMap.get(TermRange.MORE_THAN_THREE_YEARS.name());

        if (ObjectUtil.isNotEmpty(oneFtpLprPricing)) {
            oneFtpLprPricing.setCurrentAverage(req.getOneCurrentAverage());
            oneFtpLprPricing.setAnnualAverage(req.getOneAnnualAverage());
            oneFtpLprPricing.setCurrentQuarterAverage(req.getOneCurrentQuarterAverage());
            updateList.add(oneFtpLprPricing);
        } else {
            NewFtpFinancingCostPricingDraft costPricingDraft = new NewFtpFinancingCostPricingDraft();
            costPricingDraft.setCurrentAverage(req.getOneCurrentAverage());
            costPricingDraft.setAnnualAverage(req.getOneAnnualAverage());
            costPricingDraft.setCurrentQuarterAverage(req.getOneCurrentQuarterAverage());
            costPricingDraft.setFtpId(req.getMainId());
            costPricingDraft.setMonth(req.getMonth());
            costPricingDraft.setTermRange(TermRange.ONE_YEAR.name());
            updateList.add(costPricingDraft);
        }
        if (ObjectUtil.isNotEmpty(one2ThreeFtpLprPricing)) {
            one2ThreeFtpLprPricing.setCurrentAverage(req.getThreeCurrentAverage());
            one2ThreeFtpLprPricing.setAnnualAverage(req.getThreeAnnualAverage());
            one2ThreeFtpLprPricing.setCurrentQuarterAverage(req.getThreeCurrentQuarterAverage());
            updateList.add(one2ThreeFtpLprPricing);
        } else {
            NewFtpFinancingCostPricingDraft costPricingDraft = new NewFtpFinancingCostPricingDraft();
            costPricingDraft.setCurrentAverage(req.getThreeCurrentAverage());
            costPricingDraft.setAnnualAverage(req.getThreeAnnualAverage());
            costPricingDraft.setCurrentQuarterAverage(req.getThreeCurrentQuarterAverage());
            costPricingDraft.setFtpId(req.getMainId());
            costPricingDraft.setMonth(req.getMonth());
            costPricingDraft.setTermRange(TermRange.ONE_TO_THREE_YEARS.name());
            updateList.add(costPricingDraft);
        }
        if (ObjectUtil.isNotEmpty(three2FiveFtpLprPricing)) {
            three2FiveFtpLprPricing.setCurrentAverage(req.getFiveCurrentAverage());
            three2FiveFtpLprPricing.setAnnualAverage(req.getFiveAnnualAverage());
            three2FiveFtpLprPricing.setCurrentQuarterAverage(req.getFiveCurrentQuarterAverage());
            updateList.add(three2FiveFtpLprPricing);
        } else {
            NewFtpFinancingCostPricingDraft costPricingDraft = new NewFtpFinancingCostPricingDraft();
            costPricingDraft.setCurrentAverage(req.getFiveCurrentAverage());
            costPricingDraft.setFtpId(req.getMainId());
            costPricingDraft.setMonth(req.getMonth());
            costPricingDraft.setAnnualAverage(req.getFiveAnnualAverage());
            costPricingDraft.setCurrentQuarterAverage(req.getFiveCurrentQuarterAverage());
            costPricingDraft.setTermRange(TermRange.MORE_THAN_THREE_YEARS.name());
            updateList.add(costPricingDraft);
        }
        if (ObjectUtil.isNotEmpty(updateList)) {
            this.saveOrUpdateBatch(updateList);
        }
        return R.ok();
    }
}