package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库列表-请求体")
public class BlackGrayLibraryListREQ extends PageReq {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
//    @NotBlank
    private String unifiedSocialCreditCode;

    @ApiModelProperty(value = "来源 BlackGraySourceEnum")
    private String source;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
//    @NotNull
    private String businessType;

    /**
     * 业务类型列表
     */
    @ApiModelProperty(value = "业务类型列表")
    private List<String> businessTypeList;

    /**
     * 报告机构
     */
    @ApiModelProperty(value = "报告机构")
//    @NotBlank
    private String applyOrganization;

    @ApiModelProperty(value = "所属部门")
    private String applyDept;

    @ApiModelProperty(value = "入库原因")
    private String applyReason;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    /**
     * 数字摘要同步接口专用字段，做增量同步
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "名单类型 BLACK_LIST 黑名单 GRAY_LIST 灰名单")
    private String listType;

    @ApiModelProperty(value = "入库时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date warehouseTimeFrom;
    @ApiModelProperty(value = "入库时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date warehouseTimeTo;

    @ApiModelProperty(value = "出库时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date planOutboundTimeFrom;
    @ApiModelProperty(value = "出库时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date planOutboundTimeTo;

    @ApiModelProperty(value = "1,不降级，0降级")
    private Long reduceStatus;

    @ApiModelProperty(value = "排序")
    private List<OrderBy> orderByList;

    @ApiModelProperty(value = "ids")
    private List<Long> ids;

    @ApiModelProperty(value = "true 单例， false 多个")
    private boolean singleton;

    @Data
    public static class OrderBy{
        @ApiModelProperty(value = "排序字段")
        private String orderByField;
        @ApiModelProperty(value = "是否倒叙 1 倒叙，其他正叙")
        private Integer descFlag;
    }
}
