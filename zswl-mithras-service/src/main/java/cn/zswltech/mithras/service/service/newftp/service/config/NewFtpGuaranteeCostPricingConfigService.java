package cn.zswltech.mithras.service.service.newftp.service.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListREQ;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpGuaranteeCostPricingConfigMapper;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpGuaranteeCostPricingConfig;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_guarantee_cost_pricing_config(担保成本配置表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpGuaranteeCostPricingConfigService extends ServiceImpl<NewFtpGuaranteeCostPricingConfigMapper, NewFtpGuaranteeCostPricingConfig> {
    @Resource
    private FundFinancingService fundFinancingService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;


    @Transactional(rollbackFor = Throwable.class)
    public void modify(NewFtpGuaranteeCostPricingModifyREQ req) {
        NewFtpGuaranteeCostPricingConfig originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        originalInfo.setHandCurrentAverage(req.getCurrentAverage());
        baseMapper.updateById(originalInfo);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void flash(LocalDate localDate) {
        NewFtpGuaranteeCostPricingConfig ftpGuaranteeCostPricing = new NewFtpGuaranteeCostPricingConfig();
        ftpGuaranteeCostPricing.setMonth(localDate);
        NewFtpGuaranteeCostPricingConfig oldFtpGuaranteeCostPricing = baseMapper.selectOne(Wrappers.<NewFtpGuaranteeCostPricingConfig>lambdaQuery()
                .eq(NewFtpGuaranteeCostPricingConfig::getMonth, localDate)
                .orderByDesc(NewFtpGuaranteeCostPricingConfig::getId)
                .last(StringUtil.mysqlLimitOne()));
        if(ObjectUtil.isEmpty(oldFtpGuaranteeCostPricing)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //计算本月的
        BigDecimal calculate = calculate(localDate);
        ftpGuaranteeCostPricing.setCurrentAverage(calculate.intValue());
        if(ObjectUtil.isEmpty(oldFtpGuaranteeCostPricing)){
            baseMapper.insert(ftpGuaranteeCostPricing);
        } else {
            ftpGuaranteeCostPricing.setId(oldFtpGuaranteeCostPricing.getId());
            baseMapper.updateById(ftpGuaranteeCostPricing);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addConfig(LocalDate localDate) {
        NewFtpGuaranteeCostPricingConfig ftpGuaranteeCostPricingConfig = new NewFtpGuaranteeCostPricingConfig();
        NewFtpGuaranteeCostPricingConfig oldGuaranteeCostPricingConfig = this.getOne(Wrappers.<NewFtpGuaranteeCostPricingConfig>lambdaQuery()
                .eq(NewFtpGuaranteeCostPricingConfig::getMonth, localDate.with(TemporalAdjusters.firstDayOfMonth()))
                .orderByDesc(NewFtpGuaranteeCostPricingConfig::getId)
                .last(StringUtil.mysqlLimitOne()));
        BigDecimal calculate = calculate(localDate);
        ftpGuaranteeCostPricingConfig.setCurrentAverage(calculate.intValue());
        ftpGuaranteeCostPricingConfig.setMonth(localDate.with(TemporalAdjusters.firstDayOfMonth()));
        if(ObjectUtil.isEmpty(oldGuaranteeCostPricingConfig)){
            this.save(ftpGuaranteeCostPricingConfig);
        } else {
            ftpGuaranteeCostPricingConfig.setId(oldGuaranteeCostPricingConfig.getId());
            this.updateById(ftpGuaranteeCostPricingConfig);
        }
    }


    /**
     * 1）数据范围为：【融资管理】模块，取截止上月底【剩余本金】＞0 的融资数据
     * 2）求 ∑（【担保费率】*【剩余本金】*【担保融资金额】 ÷ 【融资金额】）/∑【剩余本金】
     **/
    public BigDecimal calculate(LocalDate localDate){
        if(ObjectUtil.isEmpty(localDate)){
            return BigDecimal.ZERO;
        }
        //获取上月底
        LocalDateTime enDate = LocalDateTime.of(localDate.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
        FundFinancingListREQ req = new FundFinancingListREQ();
        req.setActualLoanDateTo(enDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        //∑（【担保费率】*【剩余本金】*【担保融资金额】 ÷ 【融资金额】）
        BigDecimal numerator = new BigDecimal(0);
        //∑【剩余本金】
        BigDecimal remainingPrincipal = new BigDecimal(0);
        //NewFtpGuaranteeCostPricingDraft ftpGuaranteeCostPricing = new NewFtpGuaranteeCostPricingDraft();
        //ftpGuaranteeCostPricing.setMonth(localDate);
        //上月所有融资数据数据
        PageR<FundFinancingListRSP.FundFinancingList> fundFinancingListPageR = fundFinancingService.pageList(req).getRecords();
        List<FundFinancingListRSP.FundFinancingList> fundFinancingListRSPS = fundFinancingListPageR.getList().stream().filter(base -> {
            if (FundFinancingStatusEnum.EFFECT.name().equals(base.getFinancingStatus()) || FundFinancingStatusEnum.CARRY_INTEREST.name().equals(base.getFinancingStatus())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        //还款金额
        if(ObjectUtil.isNotEmpty(fundFinancingListRSPS)){
            //计算月末担保额度
            for(FundFinancingListRSP.FundFinancingList fundFinancingListRSP : fundFinancingListRSPS){
                //【担保费率】（目前固定0.5%）*【剩余本金】*【担保融资金额】 ÷ 【融资金额】
                numerator = numerator.add(new BigDecimal("5000").multiply(new BigDecimal(LongUtil.null2zero(fundFinancingListRSP.getLastPrincipal())))
                        .multiply(new BigDecimal(LongUtil.null2zero(fundFinancingListRSP.getGuaranteeFinancingAmount()))).divide(new BigDecimal(LongUtil.null2zero(fundFinancingListRSP.getFinancingAmount())), 10, RoundingMode.HALF_UP));
                //剩余本金
                remainingPrincipal = remainingPrincipal.add(new BigDecimal(LongUtil.null2zero(fundFinancingListRSP.getLastPrincipal())));
            }
        }
        return remainingPrincipal.compareTo(BigDecimal.ZERO) > 0 ? numerator.divide(remainingPrincipal, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public PageR<NewFtpGuaranteeCostPricingListRSP> list(PageReq req) {
        Page<NewFtpGuaranteeCostPricingConfig> newFtpGuaranteeCostPricingPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpGuaranteeCostPricingConfig>lambdaQuery()
                .orderByDesc(NewFtpGuaranteeCostPricingConfig::getMonth));
        List<NewFtpGuaranteeCostPricingListRSP> rspList = new ArrayList<>();
        newFtpGuaranteeCostPricingPage.getRecords().forEach(base -> {
            NewFtpGuaranteeCostPricingListRSP rsp= new NewFtpGuaranteeCostPricingListRSP();
            rsp.setId(base.getId());
            rsp.setMonth(base.getMonth());
            rsp.setCurrentAverage(ObjectUtil.isEmpty(base.getHandCurrentAverage()) ? base.getCurrentAverage() : base.getHandCurrentAverage());
            rspList.add(rsp);
        });
        return PageR.of(rspList, newFtpGuaranteeCostPricingPage.getTotal(), newFtpGuaranteeCostPricingPage.getCurrent(),
                newFtpGuaranteeCostPricingPage.getSize());
    }
}




