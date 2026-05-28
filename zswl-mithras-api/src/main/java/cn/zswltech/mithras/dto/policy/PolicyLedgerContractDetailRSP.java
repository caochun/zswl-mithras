package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
@ApiModel("保单台账-合同详情-返回体")
public class PolicyLedgerContractDetailRSP {

    @ApiModelProperty("id")
    private Long id;
    /**
     * 保单编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "合同金额")
    private Long applyCreditAmount;

    /**
     * 实际起租日
     **/
    @ApiModelProperty("实际起租日")
    private LocalDate actualLeaseDate;

    /**
     * 实际结束日
     */
    @ApiModelProperty("实际结束日")
    private LocalDate actualFinishDate;

    @ApiModelProperty("项目名称")
    private String projName;

    /**
     * 客户id
     **/
    @ApiModelProperty("客户id")
    private Long clientId;

    /**
     * 客户name
     **/
    @ApiModelProperty("客户name")
    private String clientName;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;
    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户名列表")
    private List<String> projCosponsorUserNames;

}
