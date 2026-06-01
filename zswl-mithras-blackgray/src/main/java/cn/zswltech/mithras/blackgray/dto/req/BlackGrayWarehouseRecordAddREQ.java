package cn.zswltech.mithras.blackgray.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表新增-请求体")
public class BlackGrayWarehouseRecordAddREQ {

    /**
    * 企业名称
    */
    @ApiModelProperty(value = "企业名称")
    @NotNull(message = "企业名称不能为空")
    private String enterpriseName;

    /**
    * 统一社会信用代码
    */
    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

    /**
    * 业务类型
    */
    @ApiModelProperty(value = "业务类型")
    @NotNull(message = "业务类型不能为空")
    private String businessType;

    @ApiModelProperty(value = "金融企业业务类型")
    private String customiseBusinessType;

    @ApiModelProperty(value = "任务编号")
    private String taskNum;

    /**
     * 来源
     **/
    @ApiModelProperty(name = "来源 BlackGraySourceEnum")
    @NotNull(message = "来源不能为空")
    private String source;

    /**
    * 观察期
    */
    @ApiModelProperty(value = "观察期")
    private String periodUnderObservation;

    /**
    * 风险规模（万元）
    */
    @ApiModelProperty(value = "风险规模（万元）")
    private BigDecimal riskScale;

    /**
    * 所属集团
    */
    @ApiModelProperty(value = "所属集团")
    private String membershipGroup;

    /**
    * 集团是否纳入黑名单
    */
    @ApiModelProperty(value = "集团是否纳入黑名单 0否 1纳入黑名单")
    private Integer blacklistStatus;

    /**
     * 申请原因
     */
    @ApiModelProperty(value = "申请原因下拉")
    private List<String> applyReasonType;

    /**
    * 申请原因
    */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    /**
    * 黑灰标识
    */
    @ApiModelProperty(value = "黑灰标识")
    @NotNull(message = "黑灰标识不能为空")
    private String blackGrayType;

    /**
    * 集团黑灰标识
    */
    @ApiModelProperty(value = "集团黑灰标识")
    private String groupBlackGrayType;

    /**
     * 入库文件key
     */
    @ApiModelProperty(name = "入库文件key")
    private List<String> warehouseFileKeys;

    /**
     * 整改文件key
     */
    @ApiModelProperty(name = "整改文件key")
    private List<String> rectifyFileKeys;

    @ApiModelProperty(value = "入库时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date warehouseTime;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private String reportFlag;

}
