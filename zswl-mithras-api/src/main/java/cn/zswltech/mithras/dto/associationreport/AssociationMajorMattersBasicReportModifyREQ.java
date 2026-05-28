package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 重大事项报告表-基本信息
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
@ApiModel("重大事项报告表-基本信息编辑-请求体")
public class AssociationMajorMattersBasicReportModifyREQ {

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
    * 填报人联系方式
    */
    @ApiModelProperty(value = "填报人联系方式")
    private String inftContMode;

    /**
    * 企业名称
    */
    @ApiModelProperty(value = "企业名称")
    private String corpName;

    /**
    * 法定资本(万元)
    */
    @ApiModelProperty(value = "法定资本(万元)")
    private Double leglCptl;

    /**
    * 营业地址
    */
    @ApiModelProperty(value = "营业地址")
    private String busiAddr;

    /**
    * 公司法人名称
    */
    @ApiModelProperty(value = "公司法人名称")
    private String corpLegpName;

    /**
    * 分支机构数量
    */
    @ApiModelProperty(value = "分支机构数量")
    private Integer brchInsNum;

    /**
    * 董事长姓名
    */
    @ApiModelProperty(value = "董事长姓名")
    private String chrmName;

    /**
    * 总经理姓名
    */
    @ApiModelProperty(value = "总经理姓名")
    private String gmgrName;

    /**
    * 联系方式
    */
    @ApiModelProperty(value = "联系方式")
    private String contMode;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;



}
