package cn.zswltech.mithras.service.service.materialsfile.export;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.FileExportEnum;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.others.MithrasException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName FileModelCheck
 * 各模块文件检查
 * @Author jackerhe
 * @Date 2022/11/20 11:43 上午
 * @Version 1.0
 **/
public abstract class ExportHandle<T extends ExcelModel, F extends AbstractSimpleExcelExporter> {

    @Resource
    private HttpServletResponse httpServletResponse;

    //获取需要导出的文件
    public abstract String getBusinessType();

    //简单校验
    public void check(FileExportREQ req) {
        FileExportEnum fileExportEnum = Optional.ofNullable(FileExportEnum.getByName(req.getBusinessType())).orElseThrow(() -> new MithrasException(ResultMsg.UNSUPPORT_TYPE));
    }

    /**
     * 默认为获取 {@link FileExportEnum#getDisplay()}
     **/
    public String getExportName() {
        return FileExportEnum.getByName(getBusinessType()).getDisplay();
    }

    //默认.xlsx
    protected String getExportSUFFIX() {
        return GlobalConstants.OFFICE_EXCEL_SUFFIX;
    }

    //获取导出实现类
    public abstract F getExportSimpleExporter();

    public void exec(FileExportREQ req) throws IOException {
        F exportSimpleExporter = getExportSimpleExporter();
        if(ObjectUtil.isEmpty(exportSimpleExporter)) {
            throw new MithrasException(ResultMsg.UNSUPPORT_TYPE);
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(getExportName() + getExportSUFFIX() ,
                        StandardCharsets.UTF_8.name()));
        exportSimpleExporter.exportExcel(req2ExportList(req), httpServletResponse.getOutputStream());
    }
    //自行查询符合条件的数据
    public abstract List<T> req2ExportList(FileExportREQ req);

}
