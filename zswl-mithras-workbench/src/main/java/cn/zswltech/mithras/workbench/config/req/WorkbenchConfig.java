package cn.zswltech.mithras.workbench.config.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2025/1/10 15:12
 */
@Data
public class WorkbenchConfig implements Serializable {
    // 工作台配置
    @NotNull(message = "用户配置不能为空")
    private String config;

}
