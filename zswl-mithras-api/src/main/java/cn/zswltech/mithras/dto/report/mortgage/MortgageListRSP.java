package cn.zswltech.mithras.dto.report.mortgage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 征信报送-抵押表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-抵押表返回值")
public class MortgageListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务标识")
    private String businessKey;

    /**
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty("借据编号")
    private String paymentApplyCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("借据本金")
    private Long applyPaymentAmount;

    @ApiModelProperty("抵押合同编号")
    private String mortgageContractCode;

    @ApiModelProperty("抵押合同编号(简)")
    private String mortgageContractCode2;

    @ApiModelProperty("抵押人类型")
    private String mortgageType;

    @ApiModelProperty("抵押人名称")
    private String mortgageName;

    @ApiModelProperty("抵押人身份标识类型")
    private String mortgageIdType;

    @ApiModelProperty("抵押人身份标识号码")
    private String mortgageId;

    @ApiModelProperty("最高额担保标识（0否，1是）")
    private Integer maxFlag;

    @ApiModelProperty("评估机构类型")
    private String appraisalCompanyType;

    @ApiModelProperty("评估日期")
    private LocalDate assessedDate;

    @ApiModelProperty("抵押物描述")
    private String mortgageDescribe;

    @ApiModelProperty("序号")
    private String sequence;

    @ApiModelProperty("抵押物种类")
    private String type;

    @ApiModelProperty("抵押物识别号类型")
    private String modelType;

    @ApiModelProperty("抵押物唯一识别号")
    private String model;

    @ApiModelProperty("评估价值")
    private Long assessedValue;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;
}
