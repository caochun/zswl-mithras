package cn.zswltech.mithras.service.fund.direct.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.fund.direct.converter.FundDirectFinancingSubscriptionDetailConverter;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingSubscriptionDetail;
import cn.zswltech.mithras.service.fund.direct.excel.FundDirectFinancingSubscriptionDetailExcelModel;
import cn.zswltech.mithras.service.fund.direct.excel.FundDirectFinancingSubscriptionDetailExporter;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingSubscriptionDetailMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 直接融资-认购明细
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingSubscriptionDetailService extends ServiceImpl<FundDirectFinancingSubscriptionDetailMapper, FundDirectFinancingSubscriptionDetail> {
    @Resource
    private FundDirectFinancingSubscriptionDetailConverter baseConverter;
    @Resource
    private FundDirectFinancingSubscriptionDetailMapper fundDirectFinancingSubscriptionDetailMapper;
    @Resource
    private FundDirectFinancingSubscriptionDetailExporter subscriptionDetailExporter;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(FundDirectFinancingSubscriptionDetailAddREQ req) {
        FundDirectFinancingSubscriptionDetail info = baseConverter.adReq2Entity(req);
        fundDirectFinancingSubscriptionDetailMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundDirectFinancingSubscriptionDetailModifyREQ req) {
        FundDirectFinancingSubscriptionDetail originalInfo = fundDirectFinancingSubscriptionDetailMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundDirectFinancingSubscriptionDetail info = baseConverter.modifyReq2Entity(req);
        fundDirectFinancingSubscriptionDetailMapper.updateById(info);
    }

    @Resource
    private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;

    public FundDirectFinancingSubscriptionDetailListRSP list(FundDirectFinancingSubscriptionDetailListREQ req) {
        List<FundDirectFinancingSubscriptionDetail> subscriptionDetails = baseMapper.selectList(
                Wrappers.<FundDirectFinancingSubscriptionDetail>lambdaQuery()
                        .eq(FundDirectFinancingSubscriptionDetail::getFinancingId, req.getFinancingId()));
        BigDecimal totalAmount = subscriptionDetails.stream()
                .map(FundDirectFinancingSubscriptionDetail::getSubscriptionLimit)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        FundDirectFinancingSubscriptionDetailListRSP rsp = new FundDirectFinancingSubscriptionDetailListRSP();
        rsp.setTotal(totalAmount.toString());
        Page<FundDirectFinancingSubscriptionDetail> subscriptionDetailPage = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundDirectFinancingSubscriptionDetail>lambdaQuery()
                        .eq(FundDirectFinancingSubscriptionDetail::getFinancingId, req.getFinancingId()));
        List<FundDirectFinancingSubscriptionDetailRSP> rspList = baseConverter.entity2Rsp(subscriptionDetailPage.getRecords());

        Map<Long, String> productNames = fundDirectFinancingProductDetailService.list(
                        Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                                .eq(FundDirectFinancingProductDetail::getFinancingId, req.getFinancingId()))
                .stream().collect(Collectors.toMap(FundDirectFinancingProductDetail::getId, FundDirectFinancingProductDetail::getAbbreviation));

        for (FundDirectFinancingSubscriptionDetailRSP subscriptionDetailRSP : rspList) {
            List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByOrgId(subscriptionDetailRSP.getOrgnizationId());
            int count = 0;
            if(CollectionUtil.isNotEmpty(financingCreditRefList)) {
                count = fundFinancingBaseInfoService.count(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                        .in(FundFinancingBaseInfo::getId, financingCreditRefList.stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList())));
            }
            if (count > 0) {
                subscriptionDetailRSP.setIsCreditOrgnization(true);
            } else {
                subscriptionDetailRSP.setIsCreditOrgnization(false);
            }
            subscriptionDetailRSP.setProductName(productNames.get(subscriptionDetailRSP.getProductId()));
        }
        rsp.setPage(PageR.of(subscriptionDetailPage, rspList));
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(Long id) {
        FundDirectFinancingSubscriptionDetail originalInfo = fundDirectFinancingSubscriptionDetailMapper.selectById(id);
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        fundDirectFinancingSubscriptionDetailMapper.deleteById(id);
    }

    public void exportExcel(ServletOutputStream outputStream, Long financingId) {

        List<FundDirectFinancingSubscriptionDetail> subscriptionDetails = baseMapper.selectList(
                Wrappers.<FundDirectFinancingSubscriptionDetail>lambdaQuery().eq(FundDirectFinancingSubscriptionDetail::getFinancingId, financingId));
        if (ObjectUtil.isEmpty(subscriptionDetails)) {
            return;
        }
        Map<Long, String> productNames = fundDirectFinancingProductDetailService.list(
                        Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                                .eq(FundDirectFinancingProductDetail::getFinancingId, financingId))
                .stream().collect(Collectors.toMap(FundDirectFinancingProductDetail::getId, FundDirectFinancingProductDetail::getAbbreviation));
        for (FundDirectFinancingSubscriptionDetail subscriptionDetail : subscriptionDetails) {
            subscriptionDetail.setProductName(productNames.get(subscriptionDetail.getProductId()));
            List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByOrgId(subscriptionDetail.getOrgnizationId());
            int count = 0;
            if(CollectionUtil.isNotEmpty(financingCreditRefList)) {
                count = fundFinancingBaseInfoService.count(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                        .in(FundFinancingBaseInfo::getId, financingCreditRefList.stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList())));
            }
            if (count > 0) {
                subscriptionDetail.setIsCreditOrg("是");
            } else {
                subscriptionDetail.setIsCreditOrg("否");
            }
        }
        List<FundDirectFinancingSubscriptionDetailExcelModel> excelModelList = baseConverter.entity2ExcelModel(subscriptionDetails);
        excelModelList.forEach(v -> v.setSubscriptionLimit(new BigDecimal(v.getSubscriptionLimit()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString()));
        subscriptionDetailExporter.exportExcel(excelModelList, outputStream);
    }
}