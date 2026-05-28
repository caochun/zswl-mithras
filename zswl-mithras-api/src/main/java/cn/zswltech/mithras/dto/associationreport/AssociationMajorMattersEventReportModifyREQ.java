package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 重大事项报告表-重大事项报告情况
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
@ApiModel("重大事项报告表-重大事项报告情况编辑-请求体")
public class AssociationMajorMattersEventReportModifyREQ {

    /**
    * 自增主键
    */
    @ApiModelProperty(value = "自增主键")
    private Long id;

    /**
    * 行号 | 同一批次数据从1开始递增
    */
    @ApiModelProperty(value = "行号 | 同一批次数据从1开始递增")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码
    */
    @ApiModelProperty(value = "企业统一社会信用代码")
    private String unifSociCredCode;

    /**
    * 事项名称
    */
    @ApiModelProperty(value = "事项名称")
    private String piecName;

    /**
    * 重大事项说明
    */
    @ApiModelProperty(value = "重大事项说明")
    private String imprPiecExpl;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;


}
