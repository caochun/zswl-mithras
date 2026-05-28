package cn.zswltech.mithras.service.fund.direct.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.fund.direct.converter.FundDirectFinancingProductDetailConverter;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.service.fund.direct.excel.FundDirectFinancingProductDetailExcelModel;
import cn.zswltech.mithras.service.fund.direct.excel.FundDirectFinancingProductDetailExporter;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingProductDetailMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 直接融资-产品明细
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingProductDetailService extends ServiceImpl<FundDirectFinancingProductDetailMapper, FundDirectFinancingProductDetail> {

    @Resource
    private FundDirectFinancingProductDetailConverter baseConverter;
    @Resource
    private FundDirectFinancingProductDetailExporter productDetailExporter;
    @Resource
    private FundDirectFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private NewFtpBaseInfoService ftpBaseInfoService;

    public List<FundDirectFinancingProductDetail> listByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundDirectFinancingProductDetail> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingProductDetail::getFinancingId, financingId);
        query.orderByAsc(FundDirectFinancingProductDetail::getId);
        return this.list(query);
    }

    public List<FundDirectFinancingProductDetail> listByBatchFinancingId(Set<Long> financingIds) {
        if (ObjectUtil.isEmpty(financingIds)) {
            return ListUtil.empty();
        }
        LambdaQueryWrapper<FundDirectFinancingProductDetail> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingProductDetail::getFinancingId, financingIds);
        query.orderByAsc(FundDirectFinancingProductDetail::getId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundDirectFinancingProductDetailAddREQ req) {
        FundDirectFinancingProductDetail info = baseConverter.addReq2Entity(req);
        //填充ftp收益率
        info.setFtpYieldRate(this.getFtpYieldRate(info.getValueDate(), info.getExpectedExpirationDate()));
        baseMapper.insert(info);

        updateAverageCouponRate(info.getFinancingId());
    }

    private Integer getFtpYieldRate(LocalDate valueDate, LocalDate expectedExpirationDate) {
        NewFtpMonthlyGuidanceExtDraftDetailRSP lastFtpMonthlyGuidance = ftpBaseInfoService.getLastFtpMonthlyGuidance();
        if (ObjectUtil.isEmpty(valueDate) || ObjectUtil.isEmpty(expectedExpirationDate) || ObjectUtil.isEmpty(lastFtpMonthlyGuidance)) {
            return 0;
        }
        long between = ChronoUnit.MONTHS.between(valueDate, expectedExpirationDate);
        return lastFtpMonthlyGuidance.getFtpYieldRate((int) between);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingProductDetailModifyREQ req) {
        FundDirectFinancingProductDetail originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingProductDetail info = baseConverter.modifyReq2Entity(req);
        baseMapper.updateById(info);

        updateAverageCouponRate(originalInfo.getFinancingId());
    }

    public FundDirectFinancingProductDetailListRSP list(FundDirectFinancingProductDetailListREQ req) {
        FundDirectFinancingProductDetailListRSP rsp = new FundDirectFinancingProductDetailListRSP();
        List<FundDirectFinancingProductDetail> all = baseMapper.selectList(
                Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                        .eq(FundDirectFinancingProductDetail::getFinancingId, req.getFinancingId()));
        BigDecimal totalIssuanceAmount = BigDecimal.ZERO;
        BigDecimal totalRemainingPrincipal = BigDecimal.ZERO;
        BigDecimal totalLayeredProportion = BigDecimal.ZERO;
        for (FundDirectFinancingProductDetail productDetail : all) {
            totalIssuanceAmount = totalIssuanceAmount.add(new BigDecimal(productDetail.getIssuanceAmount()));
            totalRemainingPrincipal = totalRemainingPrincipal.add(new BigDecimal(productDetail.getRemainingPrincipal()));
            totalLayeredProportion = totalLayeredProportion.add(new BigDecimal(productDetail.getLayeredProportion()));
        }

        FundDirectFinancingProductDetailRSP total = new FundDirectFinancingProductDetailRSP();
        total.setIssuanceAmount(totalIssuanceAmount.longValue());
        total.setLayeredProportion(totalLayeredProportion.longValue());
        total.setRemainingPrincipal(totalRemainingPrincipal.longValue());
        rsp.setTotal(total);

        Page<FundDirectFinancingProductDetail> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                        .eq(FundDirectFinancingProductDetail::getFinancingId, req.getFinancingId()).orderByAsc(FundDirectFinancingProductDetail::getId));

        if (ObjectUtil.isEmpty(page.getRecords())) {
            rsp.setPage(PageR.empty(req.getPage(), req.getPageSize()));
            return rsp;
        }
        List<FundDirectFinancingProductDetailRSP> rspList = baseConverter.entity2Rsp(page.getRecords());
        rsp.setPage(PageR.of(page, rspList));
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundDirectFinancingProductDetail originalInfo = baseMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        baseMapper.deleteById(id);
        updateAverageCouponRate(originalInfo.getFinancingId());
    }

    public FundDirectFinancingProductDetailRSP detail(FundDirectFinancingSingleIdREQ req) {
        FundDirectFinancingProductDetail productDetail = baseMapper.selectById(req.getId());
        return baseConverter.entity2Rsp(productDetail);
    }

    public void exportExcel(ServletOutputStream outputStream, Long financingId) {
        List<FundDirectFinancingProductDetail> list = list(
                Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                        .eq(FundDirectFinancingProductDetail::getFinancingId, financingId));
        List<FundDirectFinancingProductDetailExcelModel> excelModels = baseConverter.entity2ExcelModel(list);
        for (FundDirectFinancingProductDetailExcelModel excelModel : excelModels) {
            if (ObjectUtil.isNotNull(excelModel.getRating())) {
                excelModel.setIssuanceRate(new BigDecimal(excelModel.getIssuanceRate()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString());
            }
            if (ObjectUtil.isNotNull(excelModel.getIssuanceAmount())) {
                excelModel.setIssuanceAmount(new BigDecimal(excelModel.getIssuanceAmount()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString());
            }
            if (ObjectUtil.isNotNull(excelModel.getRemainingPrincipal())) {
                excelModel.setRemainingPrincipal(new BigDecimal(excelModel.getRemainingPrincipal()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString());
            }
            if (ObjectUtil.isNotNull(excelModel.getLayeredProportion())) {
                excelModel.setLayeredProportion(new BigDecimal(excelModel.getLayeredProportion()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString());
            }
            if (ObjectUtil.isNotNull(excelModel.getAnnualPayCount())) {
                excelModel.setAnnualPayCount(new BigDecimal(excelModel.getAnnualPayCount()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString());
            }
        }

        productDetailExporter.exportExcel(excelModels, outputStream);
    }

    public Map<Long, Long> queryRemainingAmount(Set<Long> financingIds) {
        Map<Long, Long> res = new HashMap<>();
        Map<Long, List<FundDirectFinancingProductDetail>> groupByFinancingId = list(
                Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                        .in(FundDirectFinancingProductDetail::getFinancingId, financingIds))
                .stream()
                .collect(Collectors.groupingBy(FundDirectFinancingProductDetail::getFinancingId));
        groupByFinancingId.forEach((aLong, details) -> {
            BigDecimal totalRemaining = details.stream().map(FundDirectFinancingProductDetail::getRemainingPrincipal)
                    .map(StringUtil::null2Zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            res.put(aLong, totalRemaining.longValue());
        });
        return res;
    }

    public Long calculateAverageCouponRate(Long financingId) {
        List<FundDirectFinancingProductDetail> productDetailList = list(Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                        .eq(FundDirectFinancingProductDetail::getFinancingId, financingId));
        if(CollectionUtil.isEmpty(productDetailList)){
            return null;
        }
        // 票面加权利率 = sum（发行利率*分层占比）
        BigDecimal averageCouponRate = BigDecimal.ZERO;
        for (FundDirectFinancingProductDetail productDetail : productDetailList) {
            if(!Objects.isNull(productDetail.getIssuanceRate()) && !Objects.isNull(productDetail.getLayeredProportion())) {
                BigDecimal multiply = new BigDecimal(Util.toYuanWithoutSplit(productDetail.getIssuanceRate())).multiply(new BigDecimal(Util.toYuanWithoutSplit(productDetail.getLayeredProportion())));
                averageCouponRate = averageCouponRate.add(multiply);
            }
        }
        // 单位为%
        return averageCouponRate.multiply(new BigDecimal("100")).longValue();
    }

    //ftp加权收益率
    public Integer calculateAverageFtpYieldRate(Long financingId) {
        List<FundDirectFinancingProductDetail> productDetailList = list(Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                .eq(FundDirectFinancingProductDetail::getFinancingId, financingId));
        if(CollectionUtil.isEmpty(productDetailList)){
            return null;
        }
        // 票面加权利率 = sum（发行利率*分层占比）
        BigDecimal averageCouponRate = BigDecimal.ZERO;
        Long amount = productDetailList.stream().map(FundDirectFinancingProductDetail::getIssuanceAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
        if (ObjectUtil.equals(amount, 0L)) {
            return 0;
        }
        for (FundDirectFinancingProductDetail productDetail : productDetailList) {
            if(ObjectUtil.isNotEmpty(productDetail.getFtpYieldRate())) {
                BigDecimal multiply = new BigDecimal(LongUtil.null2zero(productDetail.getFtpYieldRate())).multiply(new BigDecimal(Util.toYuanWithoutSplit(LongUtil.null2zero(productDetail.getLayeredProportion()))));
                averageCouponRate = averageCouponRate.add(multiply);
            }
        }
        // 单位为%
        return averageCouponRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).intValue();
    }


    public void updateAverageCouponRate(Long financingId){
        // 更新票面加权利率 + ftp收益率
        Long averageCouponRate = null;
        Integer ftpYieldRate = null;
        try {
            averageCouponRate = calculateAverageCouponRate(financingId);
            ftpYieldRate = calculateAverageFtpYieldRate(financingId);
        }catch (Exception e){
            log.error("{}",e);
            throw new MithrasException("计算票面加权利率发生未知异常");
        }
        financingBaseInfoService.update(Wrappers.<FundDirectFinancingBaseInfo>lambdaUpdate()
                .eq(FundDirectFinancingBaseInfo::getId, financingId)
                .set(FundDirectFinancingBaseInfo::getAverageCouponRate, averageCouponRate)
                .set(FundDirectFinancingBaseInfo::getFtpYieldRate, ftpYieldRate));


        // 票面加权利率更新时 需要同步更新综合资金成本
        financingBaseInfoService.updateFinancingCost(financingId);
    }
}