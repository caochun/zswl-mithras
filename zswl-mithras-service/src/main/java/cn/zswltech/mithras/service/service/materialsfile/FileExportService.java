package cn.zswltech.mithras.service.service.materialsfile;

import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportFactory;
import cn.zswltech.mithras.service.service.materialsfile.export.ExportHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @ClassName FileExportService
 * @Description 文件导出统一处理类
 * @Author jackerhe
 * @Date 2024/7/8 10:16 上午
 * @Version 1.0
 **/
@Service
@Slf4j
public class FileExportService {

    @Resource
    private ExportFactory exportFactory;

    //导出
    public void export(FileExportREQ req){
        ExportHandle handler = exportFactory.getHandler(req.getBusinessType());
        try {
            handler.exec(req);
        } catch (IOException e) {
          log.error("FileExportService export error", e);
          throw new MithrasException("导出异常，请稍后重试");
        }
    }

}
