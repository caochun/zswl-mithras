package cn.zswltech.mithras.application.orchestration.facade.fund.direct;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingRepayActualApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.directfinancing.application.auth.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualSplitService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Slf4j

@Service
public class FundDirectFinancingRepayActualFacade implements FundDirectFinancingRepayActualApplicationService {

    @Resource
    private FundDirectFinancingRepayActualSplitService fundDirectFinancingRepayActualSplitService;
    @Resource
    private FundDirectFinancingRepayActualService fundDirectFinancingRepayActualService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<FundDirectFinancingRepayActualImportRSP> importExcel(FundDirectFinancingRepayActualImportREQ req) {
        Assert.isTrue(!req.getFile().isEmpty(), () -> MithrasException.newException("文件不能为空"));
        try {
            FundDirectFinancingRepayActualImportRSP importRsp = fundDirectFinancingRepayActualService.importExcelActual(req.getFinancingId(), req.getFile().getInputStream(), req.getIsCheck());
            return R.ok(importRsp);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入实际还款表发生未知异常", e);
            return R.fail("导入实际还款表发生未知异常");
        }
    }

    @Override
    public void exportExcel(FundDirectFinancingRepayActualExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("实际还款表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            fundDirectFinancingRepayActualService.exportExcel(httpServletResponse.getOutputStream(), req.getFinancingId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出实际还款表发生未知异常", e);
            throw new MithrasException("导出实际还款表发生未知异常");
        }
    }

    @Override
    public R<PageR<FundDirectFinancingRepayActualListRSP>> list(FundDirectFinancingRepayActualListREQ req) {
        return R.ok(fundDirectFinancingRepayActualService.list(req));
    }

    @Override
    public R<List<FundDirectRepayActualSplitRSP>> splitList(SingleFinancingIdREQ req) {
        return R.ok(fundDirectFinancingRepayActualSplitService.listSplitRspByFinancingId(req.getFinancingId()));
    }

    @Override
    public R<Void> calculate(SingleFinancingIdREQ req) {
        fundDirectFinancingRepayActualService.generateCashFlow(req.getFinancingId());
        return R.ok();
    }
}