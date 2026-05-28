package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 生效
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Data
@ApiModel("客户申办权-请求体")
public class ClientApplyEffectREQ {

    @ApiModelProperty("客户id")
    public Long clientId;

    @ApiModelProperty("客户名称")
    public String clientName;

    @ApiModelProperty("客户所属主办Id")
    public Long belongSponsorId;

    @ApiModelProperty("客户所属主办名字")
    public String belongSponsorName;

    @ApiModelProperty("客户所属部门Id")
    public Long belongDeptId;

    @ApiModelProperty("客户所属部门名字")
    public String belongDeptName;

    @ApiModelProperty("申请人Id")
    public Long applyId;

    @ApiModelProperty("申请人名字")
    public String applyName;

    @ApiModelProperty("申请人所属部门Id")
    public Long applyDeptId;

    @ApiModelProperty("申请人所属部门名字")
    public String applyDeptName;

    @ApiModelProperty("申请权限类型")
    public String authorityLevel;

    @ApiModelProperty("申请原因")
    public String applyReason;

    @ApiModelProperty("模块主id")
    private Long mainId;

    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

    @ApiModelProperty("随机批次号")
    private String batchNo;
}
