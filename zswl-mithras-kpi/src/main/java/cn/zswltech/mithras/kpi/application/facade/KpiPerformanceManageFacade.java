package cn.zswltech.mithras.kpi.application.facade;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.kpi.application.KpiPerformanceManageApplicationService;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceMainInfoService;
import cn.zswltech.mithras.kpi.service.KpiPerformanceRecordInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yangxiong
 * @date 2024/7/1/10:40
 * @description
 */
@Slf4j
@Service
public class KpiPerformanceManageFacade implements KpiPerformanceManageApplicationService {

    @Resource
    private KpiPerformanceMainInfoService mainInfoService;
    @Resource
    private KpiPerformanceBaseInfoService baseInfoService;
    @Resource
    private KpiPerformanceRecordInfoService recordInfoService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public R<PageR<KpiPerformanceManageMainListRSP>> mainList(KpiPerformanceManageMainListREQ req) {
        return R.ok(mainInfoService.mainList(req));
    }

    @Override
    public R<KpiPerformanceManageMainDetailRSP> mainDetail(KpiPerformanceManageMainDetailREQ req) {
        return R.ok(mainInfoService.mainDetail(req));
    }

    @Override
    public R<Void> add(KpiPerformanceManageAddREQ req) {
        log.info("KpiPerformanceManageAddREQ body: {}", req);
        mainInfoService.add(req);
        return R.ok();
    }

    @Override
    public void export(KpiPerformanceManageExportREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("业绩目标表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            mainInfoService.exportExcel(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出业绩目标表发生未知异常", e);
            throw new MithrasException("导出业绩目标表发生未知异常");
        }
    }

    @Override
    public R<KpiPerformanceManageListRSP> list(KpiPerformanceManageListREQ req) {
        return R.ok(baseInfoService.selectList(req));
    }

    @Override
    public R<Void> modifyStatus(KpiPerformanceManageModifyREQ req) {
        mainInfoService.modifyStatus(req);
        return R.ok();
    }

    @Override
    public R<Void> importKpiPerformance(KpiPerformanceManageImportREQ req) {
        mainInfoService.importKpiPerformance(req);
        return R.ok();
    }
}
