package cn.zswltech.mithras.document.controller.migrate;

import cn.zswltech.mithras.api.MigrateFileApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.document.application.MigrateFileApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MigrateFileController implements MigrateFileApi {

    @Resource
    private MigrateFileApplicationService migrateFileApplicationService;

    @Override
    public R<Void> migrateFileClient(String pw) {
        return migrateFileApplicationService.migrateFileClient(pw);
    }

    @Override
    public R<Void> migrateFileContract(String pw) {
        return migrateFileApplicationService.migrateFileContract(pw);
    }
}
