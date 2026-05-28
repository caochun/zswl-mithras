package cn.zswltech.mithras.service.providence.service;

import cn.zswltech.mithras.service.providence.req.WorkbenchConfig;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2025/1/10 15:14
 */
public interface WorkbenchService {
    /**
     * 保存工作台配置
     * @param workbenchConfig
     */
    void sotre(WorkbenchConfig workbenchConfig);

    /**
     * 读取工作配置
     * @return
     */
    String readWorkbench();
}
