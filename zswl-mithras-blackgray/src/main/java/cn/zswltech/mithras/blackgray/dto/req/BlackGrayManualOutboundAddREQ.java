package cn.zswltech.mithras.blackgray.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单人工出库表
 * @author
 * @date 2023-11-29
 */
@Data
@ApiModel("黑灰名单人工出库表新增-请求体")
public class BlackGrayManualOutboundAddREQ {


    @ApiModelProperty(value = "黑灰名单ID")
    @NotNull(message = "申请突破信息不能为空")
    private Long blackGrayId;

    /**
     * 企业名称
     */
    @ApiModelProperty(name = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(name = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    /**
     * 业务类型
     */
    @ApiModelProperty(name = "`业务类型`")
    private String businessType;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(name = "黑灰标识")
    private String blackGrayType;

    /**
     * 入库时间
     */
    @ApiModelProperty(name = "入库时间")
    @JsonFormat(timezone = "UTC+8", pattern = "yyyy-MM-dd")
    private Date warehouseTime;

    /**
     * 出库时间
     */
    @ApiModelProperty(name = "出库时间")
    @JsonFormat(timezone = "UTC+8", pattern = "yyyy-MM-dd")
    private Date planOutboundTime;


    /**
     * 申请原因
     */
    @ApiModelProperty(name = "申请原因")
    private List<Reason> applyReason;


    /**
     * 申请文件keys
     */
    @ApiModelProperty(name = "申请文件keys")
    private List<String> applyFileKeys;

    @ApiModelProperty(value = "出库说明")
    private String message;


    @Data
    public static class Reason{
        private Long id;

        @ApiModelProperty(value = "申请原因")
        private String applyReasonType;

        @ApiModelProperty(value = "申请原因名称")
        private String applyReasonName;

        @ApiModelProperty(value = "0 不选中，1选中")
        private Integer status;

        @ApiModelProperty(value = "风险规模")
        private String riskScale;

        /**
         * 入库时间
         */
        @ApiModelProperty(name = "入库时间")
        @JsonFormat(timezone = "UTC+8", pattern = "yyyy-MM-dd")
        private Date warehouseTime;

        /**
         * 出库时间
         */
        @ApiModelProperty(name = "出库时间")
        @JsonFormat(timezone = "UTC+8", pattern = "yyyy-MM-dd")
        private Date planOutboundTime;

        /**
         * 业务类型
         */
        @ApiModelProperty(name = "`业务类型`")
        private String businessType;

        /**
         * 黑灰标识
         */
        @ApiModelProperty(name = "黑灰标识")
        private String blackGrayType;


    }

}
