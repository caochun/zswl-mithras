package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailSeniorExecutiveInfoRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

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

    @ApiModelProperty(value = "现任职务Display")
    private String currDutyDisplay;

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

    @ApiModelProperty(value = "最高学历Display")
    private String highEduDisplay;

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
