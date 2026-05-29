package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingSubscriptionDetailApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingSubscriptionDetailMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingSubscriptionDetailService;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @description 直接融资-认购明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@RestController
@Slf4j
public class FundDirectFinancingSubscriptionDetailController implements FundDirectFinancingSubscriptionDetailApi {

    @Resource
    private FundDirectFinancingSubscriptionDetailService fundDirectFinancingSubscriptionDetailService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> add(FundDirectFinancingSubscriptionDetailAddREQ req) {
        fundDirectFinancingSubscriptionDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING, mapperClass = FundDirectFinancingSubscriptionDetailMapper.class)
    public R<Void> modify(FundDirectFinancingSubscriptionDetailModifyREQ req) {
        fundDirectFinancingSubscriptionDetailService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundDirectFinancingSubscriptionDetailListRSP> list(FundDirectFinancingSubscriptionDetailListREQ req) {
        return R.ok(fundDirectFinancingSubscriptionDetailService.list(req));
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingSubscriptionDetailService.remove(req.getId());
        return R.ok();
    }

    @Override
    public void exportExcel(FundDirectFinancingProductDetailListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("认购明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            fundDirectFinancingSubscriptionDetailService.exportExcel(httpServletResponse.getOutputStream(), req.getFinancingId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出认购明细表发生未知异常", e);
            throw new MithrasException("导出认购明细表发生未知异常");
        }
    }

}