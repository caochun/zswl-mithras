package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易APP拜访打卡-请求体")
public class AppCheckInREQ {

    @ApiModelProperty(value = "文件列表")
    @NotEmpty(message = "文件列表不能为空")
    private List<MultipartFile> files;

//    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

    @NotNull
    @ApiModelProperty("客户名字")
    private String clientName;

    @ApiModelProperty("拜访方式")
    private String visitWay;

    @ApiModelProperty("拜访类型")
    private String visitType;

    @ApiModelProperty("拜访阶段")
    private String visitPhase;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("租后检查计划id")
    private Long checkPlanId;

    @ApiModelProperty("租后检查计划名称")
    private String checkPlanName;

    @ApiModelProperty("打卡时间")
    private LocalDateTime checkInDate;

    @ApiModelProperty("打卡地点")
    private String checkInLocation;
}
