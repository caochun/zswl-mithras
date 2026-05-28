package cn.zswltech.mithras.dto.afterlease;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@ApiModel("租后调整信息表列表-请求体")
public class AfterLeaseAdjustInfoListREQ extends PageReq {

    @ApiModelProperty(value = "租后调整Id")
    private Long id;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private String projCosponsorUserId;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beganTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    /**
     * 业务调整类型，展期，调整还款类型
     */
    @ApiModelProperty(value = "业务调整类型 AfterLeaseAdjustEnum")
    private String afterLeaseAdjustType;

    /**
     * 调整流程状态
     */
    @ApiModelProperty(value = "调整流程状态")
    private String adjustProcessStatus;


}
