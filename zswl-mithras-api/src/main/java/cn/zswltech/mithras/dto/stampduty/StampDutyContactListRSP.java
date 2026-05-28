package cn.zswltech.mithras.dto.stampduty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author luyujie
 * @date 2026/1/26
 * @description
 */
@Data
@ApiModel("印花税缴纳明细-合同/融资合同-返回体")
public class StampDutyContactListRSP {
    @ApiModelProperty("列表记录")
    private List<StampDutyContactList> records;

    @Data
    public static class StampDutyContactList {

        @ApiModelProperty("关联id")
        private Long belongId;

        @ApiModelProperty("申报税目名称")
        private String name;

        @ApiModelProperty("业务部门id")
        private Long belongOrgId;

        @ApiModelProperty("业务部门名称")
        private String belongOrgName;

        @ApiModelProperty("客户id、机构id")
        private Long clientId;

        @ApiModelProperty("客户名称/融资机构")
        private String clientName;

        @ApiModelProperty("合同编号/融资编号")
        private String belongCode;

        @ApiModelProperty("借据id")
        private Long receiptId;

        @ApiModelProperty("借据编号")
        private String receiptCode;

        @ApiModelProperty("实际起租日")
        private LocalDate startDate;

        @ApiModelProperty("不含税租金")
        private Long rent;

        @ApiModelProperty("不含税手续费")
        private Long commission;

        @ApiModelProperty("不含税咨询费")
        private Long consultingFee;

        @ApiModelProperty("金额")
        private Long amount;

        @ApiModelProperty("印花税率")
        private String taxRate;

        @ApiModelProperty("印花税")
        private String stampDuty;

    }

}

