package cn.zswltech.mithras.blackgray.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单突破业务
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单突破业务新增-请求体")
public class BlackGrayBreakBusinessAddREQ {

    /**
    * 黑灰名单id
    */
    @ApiModelProperty(value = "黑灰名单id")
    private Long blackGrayId;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String businessType;


    /**
     * 黑灰标识
     */
    @ApiModelProperty(name = "黑灰标识")
    private String blackGrayType;

    /**
    * 拟开展业务类型
    */
    @ApiModelProperty(value = "拟开展业务类型")
    private String proposedBusinessType;

    /**
    * 原计划出库时间
    */
    @ApiModelProperty(value = "原计划出库时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date planOutboundTime;

    /**
    * 拟开展业务规模（万元）
    */
    @ApiModelProperty(value = "拟开展业务规模（万元）")
    private Double proposeBusinessScale;

    /**
    * 申请原因
    */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    @ApiModelProperty(value = "申请文件keys")
    private List<String> applyFileKeys;


}
