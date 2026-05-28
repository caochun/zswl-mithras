package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description 黑灰名单突破业务
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单突破业务列表-请求体")
public class BlackGrayBreakBusinessListREQ extends PageReq {
    /**
     * 申请机构
     */
    @ApiModelProperty(name = "申请机构")
    private String applyOrganization;

    /**
     * 申请部门
     */
    @ApiModelProperty(name = "申请部门")
    private String applyDept;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "申请时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date applyTimeFrom;
    @ApiModelProperty(value = "申请时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date applyTimeTo;

    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "拟开展业务类型")
    private String proposedBusinessType;

    @ApiModelProperty(value = "审批状态")
    private Integer auditStatus;

}
