package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2023/9/7 10:48 上午
 **/
@Data
@ApiModel("合同管理-合同比对承租人及担保人工商信息-返回体")
public class ContractCompareBusinessRSP {

    @ApiModelProperty(value = "变更标识 0无变更，1变更")
    private Integer changeFlag;

    @ApiModelProperty(value = "客户类型，承租人or担保人")
    private String clientType;

    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "tyc客户名称")
    private String clientTycName;
    @ApiModelProperty(value = "比对客户名称")
    private String clientNameCompare;

    @ApiModelProperty(value = "法人代表")
    private String corpRepresent;
    @ApiModelProperty(value = "tyc法人代表")
    private String corpTycRepresent;
    @ApiModelProperty(value = "比对法人代表")
    private String corpRepresentCompare;

    @ApiModelProperty(value = "股东信息")
    private List<String> shareHolderInfo;
    @ApiModelProperty(value = "tyc股东信息")
    private List<String> shareHolderTycInfo;
    @ApiModelProperty(value = "比对股东信息")
    private List<String> shareHolderInfoCompare;

}
