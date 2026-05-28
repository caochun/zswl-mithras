package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("保单维护新增-请求体")
public class PolicyLedgerTmpSyncREQ {

    @ApiModelProperty("合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;
   /**
    * 保单编号
    *//*
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    *//**
     * 保险公司名称
     *//*
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    *//**
     * 保单种类
     * PolicyTypeEnum
     *//*
    @ApiModelProperty("保险种类")
    private String policyType;

    *//**
    * 保单金额
    *//*
    @ApiModelProperty(value = "保单金额")
    private Long policyAmount;

    *//**
    * 保险起始日
    *//*
    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDate;

    *//**
    * 保险到期日
    *//*
    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDate;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    *//**
     * 标识信息
     *//*
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    *//**
     * 备注
     *//*
    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty("files")
    private MultipartFile[] files;*/

}
