package cn.zswltech.mithras.riskcontrol.warning;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName RiskControlWarnMonitorPageWarnDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/12/24 17:52
 * @Version 1.0
 **/
@Data
public class RiskControlWarnMonitorPageWarnDTO {

    private List<Long> ids;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称，支持模糊")
    private String chiName;


    @ApiModelProperty(value = "统一信用社会代码，支持模糊")
    private String creditCode;

    @ApiModelProperty(value = "预警编号")
    private String warnCode;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题，支持模糊")
    private String title;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯]")
    private List<Integer> warnLevels;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("所属部门")
    private List<Long> belongDeptIds;

    private Long belongDeptId;
}
