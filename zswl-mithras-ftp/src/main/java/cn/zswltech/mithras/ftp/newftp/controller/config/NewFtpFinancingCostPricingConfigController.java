package cn.zswltech.mithras.ftp.newftp.controller.config;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.config.NewFtpFinancingCostPricingConfigApi;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingFlashREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingModifyREQ;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import cn.zswltech.mithras.ftp.newftp.application.job.NewFtpPricingJobExecutor;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpFinancingCostPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpFinancingCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpFinancingCostPricingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpFinancingCostPricingDraftService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 融资成本定价配置
 * @date 2023-05-21
 */
@RestController
public class NewFtpFinancingCostPricingConfigController implements NewFtpFinancingCostPricingConfigApi {

    @Resource
    private NewFtpFinancingCostPricingConfigService newFtpFinancingCostPricingConfigService;
    @Resource
    private NewFtpFinancingCostPricingDraftService financingCostPricingDraftService;


    @Override
    public R<Void> modify(NewFtpFinancingCostPricingModifyREQ req) {
        LocalDate nowDate = req.getMonth().with(TemporalAdjusters.firstDayOfMonth());
        List<NewFtpFinancingCostPricingConfig> list = newFtpFinancingCostPricingConfigService.list(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                .eq(NewFtpFinancingCostPricingConfig::getMonth, nowDate)
                .orderByAsc(NewFtpFinancingCostPricingConfig::getMonth));
        if (ObjectUtil.isEmpty(list)) {
            throw new MithrasException("无此月数据");
        }
        List<NewFtpFinancingCostPricingConfig> updateList = new ArrayList<>();
        Map<String, NewFtpFinancingCostPricingConfig> ftpFinancingCostMap = list.stream().collect(Collectors.toMap(NewFtpFinancingCostPricingConfig::getTermRange, Function.identity(), (a, b) -> a));
        NewFtpFinancingCostPricingConfig oneFtpLprPricing = ftpFinancingCostMap.get(TermRange.ONE_YEAR.name());
        NewFtpFinancingCostPricingConfig one2ThreeFtpLprPricing = ftpFinancingCostMap.get(TermRange.ONE_TO_THREE_YEARS.name());
        NewFtpFinancingCostPricingConfig three2FiveFtpLprPricing = ftpFinancingCostMap.get(TermRange.MORE_THAN_THREE_YEARS.name());

        if (ObjectUtil.isNotEmpty(oneFtpLprPricing)) {
            oneFtpLprPricing.setCurrentAverage(req.getOneCurrentAverage());
            oneFtpLprPricing.setAnnualAverage(req.getOneAnnualAverage());
            oneFtpLprPricing.setCurrentQuarterAverage(req.getOneCurrentQuarterAverage());
            updateList.add(oneFtpLprPricing);
        } else {
            NewFtpFinancingCostPricingConfig costPricingDraft = new NewFtpFinancingCostPricingConfig();
            costPricingDraft.setCurrentAverage(req.getOneCurrentAverage());
            costPricingDraft.setAnnualAverage(req.getOneAnnualAverage());
            costPricingDraft.setCurrentQuarterAverage(req.getOneCurrentQuarterAverage());
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
            NewFtpFinancingCostPricingConfig costPricingDraft = new NewFtpFinancingCostPricingConfig();
            costPricingDraft.setCurrentAverage(req.getThreeCurrentAverage());
            costPricingDraft.setAnnualAverage(req.getThreeAnnualAverage());
            costPricingDraft.setCurrentQuarterAverage(req.getThreeCurrentQuarterAverage());
            costPricingDraft.setTermRange(TermRange.ONE_TO_THREE_YEARS.name());
            costPricingDraft.setMonth(req.getMonth());
            updateList.add(costPricingDraft);
        }
        if (ObjectUtil.isNotEmpty(three2FiveFtpLprPricing)) {
            three2FiveFtpLprPricing.setCurrentAverage(req.getFiveCurrentAverage());
            three2FiveFtpLprPricing.setAnnualAverage(req.getFiveAnnualAverage());
            three2FiveFtpLprPricing.setCurrentQuarterAverage(req.getFiveCurrentQuarterAverage());
            updateList.add(three2FiveFtpLprPricing);
        } else {
            NewFtpFinancingCostPricingConfig costPricingDraft = new NewFtpFinancingCostPricingConfig();
            costPricingDraft.setCurrentAverage(req.getFiveCurrentAverage());
            costPricingDraft.setAnnualAverage(req.getFiveAnnualAverage());
            costPricingDraft.setMonth(req.getMonth());
            costPricingDraft.setCurrentQuarterAverage(req.getFiveCurrentQuarterAverage());
            costPricingDraft.setTermRange(TermRange.MORE_THAN_THREE_YEARS.name());
            updateList.add(costPricingDraft);
        }
        if (ObjectUtil.isNotEmpty(updateList)) {
            newFtpFinancingCostPricingConfigService.saveOrUpdateBatch(updateList);
        }
        return R.ok();
    }

    @Override
    public R<PageR<NewFtpFinancingCostPricingListRSP>> financingCostList(PageReq req) {
        return R.ok(newFtpFinancingCostPricingConfigService.list(req));
    }

    @Override
    public R<Void> flash(@Valid NewFtpFinancingCostPricingFlashREQ req) {
        SpringContextHolder.getBean(NewFtpPricingJobExecutor.class).calculateFtpFinancingCostPricing();
        return R.ok();
    }

}
