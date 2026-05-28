package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 重大事项报告表-基本信息
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
@ApiModel("重大事项报告表-基本信息列表-返回体")
public class AssociationMajorMattersBasicReportListRSP {

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
    private BigDecimal leglCptl;

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

    /**
    * 报表周期 | 格式：yyyymm
    */
    @ApiModelProperty(value = "报表周期 | 格式：yyyymm")
    private String reportInstancePeriod;

    /**
    * 批次号 | 从0000递增，最大9999
    */
    @ApiModelProperty(value = "批次号 | 从0000递增，最大9999")
    private String batchNo;

    /**
    * 版本号 | 格式：报表周期版本流水号
    */
    @ApiModelProperty(value = "版本号 | 格式：报表周期版本流水号")
    private String version;

    /**
    * 操作标识 | insert/update
    */
    @ApiModelProperty(value = "操作标识 | insert/update")
    private String op;

    /**
    * 上报时间 | 文件上传时间
    */
    @ApiModelProperty(value = "上报时间 | 文件上传时间")
    private LocalDateTime reportTime;

    /**
    * 写入时间 | 数据库记录时间
    */
    @ApiModelProperty(value = "写入时间 | 数据库记录时间")
    private LocalDateTime writeTime;


}
