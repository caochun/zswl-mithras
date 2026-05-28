package cn.zswltech.mithras.dto.report.fiveclass;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 征信报送-五级分类表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-五级分类表返回值")
public class FiveClassListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

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

    @ApiModelProperty("五级分类")
    private String fiveClass;

    @ApiModelProperty("五级分类认定日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime identificationDate;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

}
