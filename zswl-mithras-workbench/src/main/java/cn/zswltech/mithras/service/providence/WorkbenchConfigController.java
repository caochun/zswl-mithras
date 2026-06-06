package cn.zswltech.mithras.service.providence;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.service.providence.req.WorkbenchConfig;
import cn.zswltech.mithras.service.providence.service.WorkbenchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2025/1/10 15:11
 */
@RestController
@Slf4j
public class WorkbenchConfigController {

    @Resource
    private WorkbenchService workbenchService;

    /**
     * 保存工作台配置
     * @param workbenchConfig
     * @return
     */
    @PostMapping("/workbenchConfig/store")
    public R storeWorkbench(@RequestBody WorkbenchConfig workbenchConfig){
        workbenchService.sotre(workbenchConfig);
        return R.ok();
    }

    /**
     * 读取工作配置
     */
    @PostMapping("/workbenchConfig/read")
    public R<String> readWorkbench(){
        return R.ok(workbenchService.readWorkbench());
    }

}
