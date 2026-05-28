package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表列表-请求体")
public class BlackGrayWarehouseRecordListREQ extends PageReq {

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

    @ApiModelProperty(value = "任务编号")
    private String taskNum;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**
     * 来源
     **/
    @ApiModelProperty(name = "来源 BlackGraySourceEnum")
    private String source;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "申请时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date applyTimeFrom;
    @ApiModelProperty(value = "申请时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date applyTimeTo;

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


    @ApiModelProperty(value = "审批状态")
    private Integer auditStatus;

    /**
     * 报告机构
     */
    @ApiModelProperty(value = "报告机构")
    private String applyOrganization;

    @ApiModelProperty(value = "所属部门")
    private String applyDept;

    @ApiModelProperty(value = "是否查询历史 0 历史，1其他")
    private Integer isHistory;

    @ApiModelProperty(value = "仅查看待处理 0 待处理，1其他")
    private Integer isPendingProcess;

    @ApiModelProperty(value = "是否在库 0 在库，1其他")
    private Integer isStock;

    private List<Long> ids;


}
