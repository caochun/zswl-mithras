package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingProductDetailApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingProductDetailMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingProductDetailService;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 直接融资-产品明细
 * @date 2023-06-17
 */
@RestController
@Slf4j
public class FundDirectFinancingProductDetailController implements FundDirectFinancingProductDetailApi {

    @Resource
    private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> add(FundDirectFinancingProductDetailAddREQ req) {
        fundDirectFinancingProductDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING, mapperClass = FundDirectFinancingProductDetailMapper.class)
    public R<Void> modify(FundDirectFinancingProductDetailModifyREQ req) {
        fundDirectFinancingProductDetailService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundDirectFinancingProductDetailListRSP> list(FundDirectFinancingProductDetailListREQ req) {
        return R.ok(fundDirectFinancingProductDetailService.list(req));
    }

    @Override
    public R<FundDirectFinancingProductDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return R.ok(fundDirectFinancingProductDetailService.detail(req));
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingProductDetailService.remove(req.getId());
        return R.ok();
    }

    @Override
    public void exportExcel(FundDirectFinancingProductDetailListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("产品明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            fundDirectFinancingProductDetailService.exportExcel(httpServletResponse.getOutputStream(), req.getFinancingId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出产品明细表发生未知异常", e);
            throw new MithrasException("导出产品明细表发生未知异常");
        }
    }

    @Override
    public R<List<ProductSelectRsp>> select(FundDirectFinancingProductDetailListREQ req) {
        List<FundDirectFinancingProductDetail> list = fundDirectFinancingProductDetailService.list(
                Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                        .eq(FundDirectFinancingProductDetail::getFinancingId, req.getFinancingId()));

        List<ProductSelectRsp> rspList = list.stream().map(detail -> {
            ProductSelectRsp rsp = new ProductSelectRsp();
            rsp.setId(detail.getId());
            rsp.setAbbreviation(detail.getAbbreviation());
            return rsp;
        }).collect(Collectors.toList());

        return R.ok(rspList);
    }


}