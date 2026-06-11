package cn.zswltech.mithras.dto.creditreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 征信报告-未结清信贷及授信信息表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-未结清信贷及授信信息表编辑-请求体")
public class CreditReportUnsettledSummaryModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 查询编号
    */
    @ApiModelProperty(value = "查询编号")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @ApiModelProperty(value = "征信报告基本表id")
    private Long creditReportId;

    /**
    * 款项模块
    */
    @ApiModelProperty(value = "款项模块")
    private String paymentModule;

    /**
    * 款项类型-短期-贴现
    */
    @ApiModelProperty(value = "款项类型-短期-贴现")
    private String paymentType;

    /**
    * 款项分类-正常，关注-不良-合计
    */
    @ApiModelProperty(value = "款项分类-正常，关注-不良-合计")
    private String fundClassification;

    /**
    * 账户数
    */
    @ApiModelProperty(value = "账户数")
    private Integer accountNumber;

    /**
    * 账户余额
    */
    @ApiModelProperty(value = "账户余额")
    private Double accountAmount;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除，1-已删除")
    private Integer deleted;

}
