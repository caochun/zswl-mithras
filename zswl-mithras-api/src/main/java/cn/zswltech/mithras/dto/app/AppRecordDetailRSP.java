package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("融租易APP拜访记录详情-返回体")
public class AppRecordDetailRSP {

    @ApiModelProperty("拜访记录id")
    private Long id;

    @NotNull
    @ApiModelProperty("客户名字")
    private String clientName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户分类: 法人")
    private String clientType;

    @ApiModelProperty("拜访方式")
    private String visitWay;

    @ApiModelProperty("拜访类型")
    private String visitType;

    @ApiModelProperty("拜访阶段")
    private String visitPhase;

    @ApiModelProperty("打卡地点/补卡地点")
    private String checkInLocation;

    @ApiModelProperty("拜访人id")
    private Long userId;

    @ApiModelProperty("拜访人名字")
    private String createdName;

    @ApiModelProperty("拜访记录状态")
    private String status;

    @ApiModelProperty("拜访时间")
    private LocalDateTime checkInDate;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("租后检查计划名称")
    private String checkPlanName;

    @ApiModelProperty("上传照片路径")
    private List<String> fileUrls;
}
