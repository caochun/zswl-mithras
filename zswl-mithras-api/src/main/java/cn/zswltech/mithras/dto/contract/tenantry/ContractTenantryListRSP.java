package cn.zswltech.mithras.dto.contract.tenantry;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


import java.util.List;

/**
 * @author vico
 * @description 合同-租赁报价方案表
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-租赁报价方案表列表-返回体")
public class ContractTenantryListRSP extends ListBaseRSP {

    /**
     * 租赁报价方案id
     */
    @ApiModelProperty(value = "租赁报价方案id")
    private Long id;

    /**
     * 所属合同id
     */
    @ApiModelProperty(value = "所属合同id")
    private Long contractId;

    /**
     * 承租人id
     */
    @ApiModelProperty(value = "承租人id")
    private Long lesseeId;

    @ApiModelProperty(value = "承租人信息")
    private ClientInfo lesseeClient;

    /**
     * 承租人类型
     */
    @ApiModelProperty(value = "承租人类型")
    private String lesseeType;

    /**
     * 承租人名称
     */
    @ApiModelProperty(value = "承租人名称")
    private String lesseeName;

    /**
     * 租赁物文件类型
     */
    @ApiModelProperty(value = "租赁物文件类型")
    private String leaseItemFileType;


    /**
     * 决议类型：股东会决议、董事会决议、股东决定、执行董事决定
     */
    @ApiModelProperty(value = "决议类型")
    private String resolutionType;

    /**
     * 决议文件
     */
    @ApiModelProperty(value = "决议文件")
    private List<Long> resolutionFileId;

    /**
     * 存量风险敞口
     */
    @ApiModelProperty(value = "存量风险敞口")
    private Long stockRiskExposure;

    /**
     * 指定联系人
     */
    @ApiModelProperty(value = "指定联系人id")
    private Long contactId;

    @ApiModelProperty(value = "指定联系人姓名")
    private String contactName;

    /**
     * 是否上报征信 0不上报，1上报
     */
    @ApiModelProperty(value = "是否上报征信 0不上报，1上报")
    private Integer isReport;

    /**
     * 章程文件
     */
    List<Long> constitutionFileList;

    /**
     * 租金往来方
     */
    @ApiModelProperty(value = "租金往来方id")
    private String rentConcatAccountId;

    /**
     * 租金往来方
     */
    @ApiModelProperty(value = "租金往来方名称")
    private String rentConcatAccountName;
}
