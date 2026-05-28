package cn.zswltech.mithras.dto.riskcontrol.opinion;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-返回体")
public class RiskWarnMonitorWarnDetailRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("预警编号")
    private String warnCode;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("客户名称")
    private String chiName;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯],这里显示成红绿灯的图标")
    private Integer warnLevel;

    @ApiModelProperty("链接地址")
    private String linkAddress;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("信息发布日期")
    private LocalDateTime dataTime;

    @ApiModelProperty("主体机构代码")
    private String majorOrgCode;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]，这里显示成五角星个数")
    private Integer warnStar;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    /**
     * 风险类型
     */
    @ApiModelProperty("风险类型")
    private String riskType;

    @ApiModelProperty("处理方式 处置方式 0 关闭， 1 处理")
    private Integer handleResult;

    @ApiModelProperty("处置意见")
    private String advisement;

    // 对接慧眼新增字段
    //慧眼数据唯一标识
    @TableField("source_xinsight_id")
    private Long sourceXinsightId;

    //数据来源: FHC-金控, XINSIGHT-慧眼
    @TableField("data_source")
    private String dataSource;
}
