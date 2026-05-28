package cn.zswltech.mithras.dto.contract.baseinfo;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 合同基本信息表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同基本信息表列表-请求体")
public class ContractBaseInfoListREQ extends PageReq {
    /**
     * 客户id
     */
    @ApiModelProperty("客户id")
    private Long clientId;

    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String projName;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    private String bizType;

    /**
     * 业务部门
     */
    @ApiModelProperty("业务部门")
    private Long bizDeptId;

    /**
     * 项目主办
     */
    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    /**
     * 项目协办
     */
    @ApiModelProperty("项目协办")
    private Long projCosponsorUserId;

    /**
     * 合同状态
     */
    @ApiModelProperty("合同状态")
    private String contractStatus;

    /**
     * 合同状态多个
     */
    @ApiModelProperty("合同状态")
    private List<String> contractStatuses;

    /**
     * 流程状态
     */
    @ApiModelProperty("流程状态")
    private String contractProcessStatus;

    /**
     * 创建时间从
     */
    @ApiModelProperty("创建时间从")
    private LocalDate createFrom;

    /**
     * 创建时间到
     */
    @ApiModelProperty("创建时间到")
    private LocalDate createTo;

    /**
     * 更新时间从
     */
    @ApiModelProperty("更新时间从")
    private LocalDate updateFrom;

    /**
     * 更新时间到
     */
    @ApiModelProperty("更新时间到")
    private LocalDate updateTo;

    //
    @ApiModelProperty("计划付款金额范围-from")
    private Long planedPaidAmountFrom;
    @ApiModelProperty("计划付款金额范围-to")
    private Long planedPaidAmountTo;
    @ApiModelProperty("计划付款日期范围-from")
    private LocalDate planedPaidDateFrom;
    @ApiModelProperty("计划付款日期范围-to")
    private LocalDate planedPaidDateTo;

}
