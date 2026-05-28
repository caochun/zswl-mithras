package cn.zswltech.mithras.dto.fileledger;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 资金端归档资料查询条件
 *
 * @author gxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundSideArchivedMaterialsQueryREQ extends PageReq {

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("归档类型")
    private String filingType;

    @ApiModelProperty(value = "项目类别")
    private String projClassify;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty("融资机构id")
    private Long organizationId;

    @ApiModelProperty("融资状态")
    private String financingStatus;

    @ApiModelProperty(value = "资金经理id")
    private Long fundManagerId;

    @ApiModelProperty(value = "归档标识")
    private Boolean archiveFlag;

    @ApiModelProperty(value = "起息日-从")
    private LocalDate carryInterestTimeFrom;

    @ApiModelProperty(value = "起息日-到")
    private LocalDate carryInterestTimeTo;

    @ApiModelProperty(value = "到期日-从")
    private LocalDate endDateFrom;

    @ApiModelProperty(value = "到期日-到")
    private LocalDate endDateTo;

    @ApiModelProperty(value = "归档日期-从")
    private LocalDate archiveDateFrom;

    @ApiModelProperty(value = "归档日期-到")
    private LocalDate archiveDateTo;
}
