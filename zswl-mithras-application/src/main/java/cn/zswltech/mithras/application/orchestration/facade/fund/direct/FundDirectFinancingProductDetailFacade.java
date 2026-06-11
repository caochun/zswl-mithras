package cn.zswltech.mithras.application.orchestration.facade.fund.direct;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.direct.application.directfinancing.FundDirectFinancingProductDetailApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.direct.application.auth.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.fund.direct.application.auth.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.fund.direct.mapper.FundDirectFinancingProductDetailMapper;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingProductDetailService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
@Slf4j

@Service
public class FundDirectFinancingProductDetailFacade implements FundDirectFinancingProductDetailApplicationService {

    @Resource
    private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<Void> add(FundDirectFinancingProductDetailAddREQ req) {
        fundDirectFinancingProductDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING", mapperClass = FundDirectFinancingProductDetailMapper.class)
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