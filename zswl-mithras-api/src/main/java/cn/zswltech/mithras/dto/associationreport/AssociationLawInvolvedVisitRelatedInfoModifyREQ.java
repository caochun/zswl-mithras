package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @description 涉法涉讼涉访信息表
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
@ApiModel("涉法涉讼涉访信息表编辑-请求体")
public class AssociationLawInvolvedVisitRelatedInfoModifyREQ {

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
    * 序号
    */
    @ApiModelProperty(value = "序号")
    private String onum;

    /**
    * 信息类别
    */
    @ApiModelProperty(value = "信息类别")
    private String caseClasCode;

    /**
    * 合同名称
    */
    @ApiModelProperty(value = "合同名称")
    private String agmtName;

    /**
    * 合同编号
    */
    @ApiModelProperty(value = "合同编号")
    private String agmtNo;

    /**
    * 涉及金额(元)
    */
    @ApiModelProperty(value = "涉及金额(元)")
    private BigDecimal invlAmt;

    /**
    * 是否销号
    */
    @ApiModelProperty(value = "是否销号")
    private String canbFlag;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;


}
