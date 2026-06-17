package cn.zswltech.mithras.application.orchestration.document.materialsfile.batchdownload.handle;

import cn.zswltech.mithras.dto.file.FileBatchDownLoadREQ;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.batchdownload.AbstractFileBatchDownload;
import org.springframework.stereotype.Component;

/**
 * @ClassName DefaultBatchDownload
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/13 10:58 上午
 * @Version 1.0
 **/
@Component
public class DefaultBatchDownload extends AbstractFileBatchDownload {
    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.DEFAULT.name();
    }

    @Override
    public void batchDownload(FileBatchDownLoadREQ req) {
        super.batchDownload(req);
    }
}
