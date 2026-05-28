package cn.zswltech.mithras.dto.riskcontrol.opinion;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-请求体")
public class RiskControlOpinionMonitorListREQ extends PageReq {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称，支持模糊")
    private String chiName;


    @ApiModelProperty(value = "统一信用社会代码，支持模糊")
    private String creditCode;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题，支持模糊")
    private String title;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]")
    private Integer warnStar;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯]")
    private Integer warnLevel;

    /**
     * 信息发布日期
     */
    @ApiModelProperty(value = "信息发布日期开始")
    private LocalDate publishDateFrom;

    /**
     * 信息发布日期
     */
    @ApiModelProperty(value = "信息发布日期截止")
    private LocalDate publishDateTo;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty(value = "舆情类型")
    private Integer riskType;

}
