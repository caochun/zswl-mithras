package cn.zswltech.mithras.dto.riskcontrol.opinion;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-请求体")
public class RiskWarnMonitorWarnListREQ extends PageReq {

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
