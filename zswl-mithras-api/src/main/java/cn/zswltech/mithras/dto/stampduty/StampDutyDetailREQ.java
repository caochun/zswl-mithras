package cn.zswltech.mithras.dto.stampduty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author luyujie
 * @date 2026/1/22
 * @description 印花税缴纳明细表
 */
@Data
@ApiModel("印花税缴纳明细新增-请求体")
public class StampDutyDetailREQ {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 关联id（合同id、融资id）
     */
    @ApiModelProperty("关联id")
    private Long belongId;

    /**
     * 申报税目名称
     */
    @NotBlank
    @ApiModelProperty(value = "申报税目名称", required = true)
    private String name;

    /**
     * 业务部门id
     */
    @ApiModelProperty("业务部门id")
    private Long belongOrgId;

    /**
     * 业务部门名称
     */
    @ApiModelProperty(value = "业务部门名称", required = true)
    private String belongOrgName;

    /**
     * 客户id、机构id
     */
    @ApiModelProperty("客户id、机构id")
    private Long clientId;

    /**
     * 客户名称/融资机构
     */
    @ApiModelProperty(value = "客户名称/融资机构", required = true)
    private String clientName;

    /**
     * 合同编号/融资编号
     */
    @ApiModelProperty(value = "合同编号/融资编号", required = true)
    private String belongCode;

    /**
     * 借据id
     */
    @ApiModelProperty("借据id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @ApiModelProperty("借据编号")
    private String receiptCode;

    /**
     * 实际起租日
     */
    @ApiModelProperty(value = "实际起租日", required = true)
    private LocalDate startDate;

    /**
     * 不含税租金
     */
    @NotNull
    @ApiModelProperty(value = "不含税租金", required = true)
    private Long rent;

    /**
     * 不含税手续费
     */
    @ApiModelProperty("不含税手续费")
    private Long commission;

    /**
     * 不含税咨询费
     */
    @ApiModelProperty("不含税咨询费")
    private Long consultingFee;

    /**
     * 金额
     */
    @NotNull
    @ApiModelProperty(value = "金额", required = true)
    private Long amount;

    /**
     * 印花税率
     */
    @NotBlank
    @ApiModelProperty(value = "印花税率", required = true)
    private String taxRate;

    /**
     * 印花税
     */
    @NotBlank
    @ApiModelProperty(value = "印花税", required = true)
    private String stampDuty;

    /**
     * 来源类型
     */
    @ApiModelProperty("来源类型")
    private String scoure;

    /**
     * 是否删除(1是、0否)
     */
    @ApiModelProperty("是否删除")
    private String isDelete;

    @ApiModelProperty("create_by")
    private Long createBy;

    @ApiModelProperty("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("update_by")
    private Long updateBy;

    @ApiModelProperty("update_time")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

}
