package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用接口后 迁移文件
 *
 * @author wangchuanhao
 * @date 2022/11/26 1:45 PM
 */
public interface MigrateFileApi {

    /**
     * 客户文件迁移
     * @param pw
     * @return
     */
    @GetMapping("/migrate/file/client")
    R<Void> migrateFileClient(@RequestParam String pw);

    /**
     * 合同文件迁移
     * @param pw
     * @return
     */
    @GetMapping("/migrate/file/contract")
    R<Void> migrateFileContract(@RequestParam String pw);

}
