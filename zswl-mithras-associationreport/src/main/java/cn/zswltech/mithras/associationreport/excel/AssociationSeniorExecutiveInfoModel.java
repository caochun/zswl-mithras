package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @date 2025/4/22
 * @description 高管信息一览表
 */
@Data
public class AssociationSeniorExecutiveInfoModel extends AssociationReportBaseModel {
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    @ApiModelProperty("row_num")
    private Integer rowNum;

    /**
     * 企业统一社会信用代码
     */
    @ApiModelProperty("unif_soci_cred_code")
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
    private LocalDate aoffTime;

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

}
