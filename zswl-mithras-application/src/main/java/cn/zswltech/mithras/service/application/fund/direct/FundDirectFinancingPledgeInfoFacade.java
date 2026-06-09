package cn.zswltech.mithras.service.application.fund.direct;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.funddirect.application.directfinancing.FundDirectFinancingPledgeInfoApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingPledgeInfoMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author zhaozhengkang
 * @description 直接融资-质押明细
 * @date 2023-06-17
 */
@Slf4j

@Service
public class FundDirectFinancingPledgeInfoFacade implements FundDirectFinancingPledgeInfoApplicationService {

    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<Void> add(FundDirectFinancingPledgeInfoAddREQ req) {
        fundDirectFinancingPledgeInfoService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING", mapperClass = FundDirectFinancingPledgeInfoMapper.class)
    public R<Void> modify(FundDirectFinancingPledgeInfoModifyREQ req) {
        fundDirectFinancingPledgeInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundDirectFinancingPledgeInfoListRSP>> list(FundDirectFinancingPledgeInfoListREQ req) {
        return R.ok(fundDirectFinancingPledgeInfoService.list(req));
    }

    @Override
    public R<FundDirectFinancingPledgeInfoDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return R.ok(fundDirectFinancingPledgeInfoService.detail(req.getId()));
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingPledgeInfoService.remove(req.getId());
        return R.ok();
    }

    @Override
    public void exportExcel(FundDirectFinancingPledgeInfoListREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("关联合同明细" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            fundDirectFinancingPledgeInfoService.exportExcel(httpServletResponse.getOutputStream(), req.getFinancingId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出质押明细表发生未知异常", e);
            throw new MithrasException("导出质押明细表发生未知异常");
        }
    }


}