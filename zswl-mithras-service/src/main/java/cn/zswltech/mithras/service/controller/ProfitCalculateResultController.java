package cn.zswltech.mithras.service.controller;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.ProfitCalculateResultApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ProfitCalculateResultListREQ;
import cn.zswltech.mithras.dto.ProfitCalculateResultListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.exporter.ProfitCalculateExcelExporter;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.ProfitCalculateResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Slf4j
@RestController
public class ProfitCalculateResultController implements ProfitCalculateResultApi {
    @Resource
    private ProfitCalculateResultService profitCalculateResultService;
    @Resource
    private ProfitCalculateExcelExporter profitCalculateExcelExporter;
    @Resource
    private HttpServletResponse httpServletResponse;
    
    @Override
    public R<ProfitCalculateResultListRSP> pageList(@Valid ProfitCalculateResultListREQ req) {
        return R.ok(profitCalculateResultService.pageList(req));
    }

    @Override
    public void exportExcel(@Valid ProfitCalculateResultListREQ req) {
        req.setPage(1);
        req.setPageSize(5000);
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("会计利润测算表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            profitCalculateExcelExporter.exportExcel(profitCalculateResultService.listExcelModel(req), httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出会计利润测算表发生未知异常[req:{}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("导出会计利润测算表发生未知异常");
        }
    }
}
