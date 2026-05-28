package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * @description 高管信息一览表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("高管信息一览表(流程节点记录版本表)列表-返回体")
public class AssociationSeniorExecutiveInfoLibListRSP {

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
    * 姓名
    */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
    * 证件号码
    */
    @ApiModelProperty(value = "证件号码")
    private String certNum;

    /**
    * 现任职务
    */
    @ApiModelProperty(value = "现任职务")
    private String currDutyCode;

    /**
    * 任职时间
    */
    @ApiModelProperty(value = "任职时间")
    private LocalDateTime aoffTime;

    /**
    * 批复文号
    */
    @ApiModelProperty(value = "批复文号")
    private String aprvFileNum;

    /**
    * 最高学历
    */
    @ApiModelProperty(value = "最高学历")
    private String highEduCode;

    /**
    * 毕业院校
    */
    @ApiModelProperty(value = "毕业院校")
    private String gradScho;

    /**
    * 就读专业
    */
    @ApiModelProperty(value = "就读专业")
    private String spjt;

    /**
    * 从事金融/经济工作时间
    */
    @ApiModelProperty(value = "从事金融/经济工作时间")
    private String haveFinlTime;

    /**
    * 联系电话
    */
    @ApiModelProperty(value = "联系电话")
    private String contTel;

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

    /**
    * 逻辑删除标识
    */
    @ApiModelProperty(value = "逻辑删除标识")
    private Integer deleted;

}
