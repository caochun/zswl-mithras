package cn.zswltech.mithras.dto.rating.ratingamount;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RatingAmountPageRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "proj_code")
    private String projCode;

    @ApiModelProperty(value = "proj_name")
    private String projName;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "主承租人名称")
    private String clientName;

    @ApiModelProperty(value = "主承租人信用代码")
    private String clientUscCode;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "模型编号")
    private String modelCode;

    @ApiModelProperty(value = "项目限额")
    private String projQuota;

    @ApiModelProperty(value = "发起时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "生效时间")
    private LocalDate effectTime;

    @ApiModelProperty(value = "失效时间")
    private LocalDate abandonTime;

    @ApiModelProperty(value = "发起机构")
    private String startOrg;

    @ApiModelProperty(value = "发起人")
    private Long createBy;

    @ApiModelProperty(value = "发起人名称")
    private String createByName;

    @ApiModelProperty(value = "评级状态")
    private String ratingStatus;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @TableField("是否项目制")
    private Boolean projSystem;

    @TableField("是否有实质性租赁物")
    private Boolean materialLeaseItem;


}
