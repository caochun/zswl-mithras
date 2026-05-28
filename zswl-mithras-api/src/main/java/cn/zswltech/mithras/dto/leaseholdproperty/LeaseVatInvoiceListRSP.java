package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/8 20:27
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LeaseVatInvoiceListRSP {

    @ApiModelProperty(value = "发票id")
    private Long id;

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "发票号码")
    private String invoiceNo;

    @ApiModelProperty(value = "开票日期")
    private LocalDate invoiceIssueDate;

    @ApiModelProperty(value = "购买方")
    private String invoicePayerName;

    @ApiModelProperty(value = "销售方")
    private String invoiceSellerName;

    @ApiModelProperty(value = "是否盖章")
    private Boolean existStample;

    @ApiModelProperty(value = "验真结果")
    private String verifyResult;

    @ApiModelProperty(value = "发票状态")
    private String status;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "锁定内容不支持修改")
    private Boolean locked;

    @ApiModelProperty(value = "发票产品信息")
    private List<LeaseVatInvoiceProductRSP> invoiceProductList;

    @ApiModelProperty(value = "是否重复")
    private Boolean isRepeat = false;
}
