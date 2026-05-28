package cn.zswltech.mithras.dto.fileledger;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资金端归档资料查询返回
 *
 * @author gxy
 */
@Data
public class FundSideArchivedMaterialsQueryRSP {

    @ApiModelProperty("序号")
    private Long sequence;

    @ApiModelProperty("文件归档 id")
    private Long filingMaterialsId;

    @ApiModelProperty("归档类型")
    private String filingType;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty(value = "项目类别/业务类型")
    private String projClassifyOrBizType;

    @ApiModelProperty(value = "产品名称/融资机构名称")
    private String productNameOrOrganizationName;

    @ApiModelProperty("融资状态")
    private String financingStatus;

    @ApiModelProperty(value = "起息日")
    private LocalDate carryInterestTime;

    @ApiModelProperty(value = "到期日")
    private LocalDate endDate;

    @ApiModelProperty(value = "资金经理")
    private String fundManagerName;

    @ApiModelProperty(value = "归档标识")
    private Boolean archiveFlag;

    @ApiModelProperty(value = "归档时间")
    private LocalDateTime archiveDate;
}
