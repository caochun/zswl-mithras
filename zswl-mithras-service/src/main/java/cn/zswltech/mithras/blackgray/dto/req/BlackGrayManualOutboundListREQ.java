package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单人工出库表
 * @author
 * @date 2023-11-29
 */
@Data
@ApiModel("黑灰名单人工出库表列表-请求体")
public class BlackGrayManualOutboundListREQ extends PageReq {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "申请时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date applyTimeFrom;
    @ApiModelProperty(value = "申请时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date applyTimeTo;

    @ApiModelProperty(value = "出库时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date planOutboundTimeFrom;
    @ApiModelProperty(value = "出库时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date planOutboundTimeTo;

    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "审批状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "id")
    private List<Long> ids;

}
