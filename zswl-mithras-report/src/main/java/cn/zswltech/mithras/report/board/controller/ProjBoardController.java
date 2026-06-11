package cn.zswltech.mithras.report.board.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.report.board.service.ProjBoardService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @create: 2022-12-15
 **/

@RestController
public class ProjBoardController {

    @Resource
    private ProjBoardService projBoardService;
    @Resource
    private HttpServletResponse httpServletResponse;


    @PostMapping("/zl/board/export")
    public R<Void> projInfoExport() {
        try {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("项目信息" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projBoardService.boardProjInfoExport(httpServletResponse.getOutputStream());
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("导出看板项目信息发生未知异常");
        }

    }
}
