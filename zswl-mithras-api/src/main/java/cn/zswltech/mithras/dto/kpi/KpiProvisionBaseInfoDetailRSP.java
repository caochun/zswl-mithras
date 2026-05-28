package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表列表-返回体")
public class KpiProvisionBaseInfoDetailRSP {

    /**
     * 创建月份
     */
    @ApiModelProperty(value = "创建月份")
    private LocalDate provisionDate;

    /**
     * 数据列表
     */
    @ApiModelProperty(value = "数据列表")
    private PageR<KpiProvisionBaseInfoBody> provisionBaseInfoList;

    @Data
    public class KpiProvisionBaseInfoBody{
        /**
         * id
         */
        @ApiModelProperty(value = "id")
        private Long id;

        private Long receiptId;

        /**
         * 借据编号
         */
        @ApiModelProperty("借据编号")
        private String receiptCode;

        /**
         * 业务类型。租赁、保理、转租赁
         */
        @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
        private String bizType;

        /**
         * 业务类型。租赁、保理、转租赁
         */
        @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
        private String bizTypeName;

        /**
         * 租赁类型。直租、回租、经营性租赁
         */
        @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
        private String leaseType;

        /**
         * 项目类别
         */
        @ApiModelProperty(value = "项目类别")
        private String projClassify;

        /**
         * 项目类别
         */
        @ApiModelProperty(value = "项目类别")
        private String projClassifyName;

        /**
         * 利润所属部门id
         */
        @ApiModelProperty(value = "利润所属部门id")
        private Long profitBelongDeptId;

        /**
         * 利润所属部门name
         */
        @ApiModelProperty(value = "利润所属部门name")
        private String profitBelongDeptName;

        /**
         * 项目主办id
         */
        @ApiModelProperty(value = "项目主办id")
        private Long clientId;

        /**
         * 客户name
         */
        @ApiModelProperty(value = "客户name")
        private String clientName;

        /**
         * 合同id
         */
        @ApiModelProperty(value = "合同id")
        private Long contractId;

        /**
         * 合同编号
         */
        @ApiModelProperty(value = "合同编号")
        private String contractCode;

        /**
         * 到期日
         */
        @ApiModelProperty(value = "到期日")
        private LocalDate endDate;

        /**
         *剩余期限
         **/
        private String residualMaturity;

        /**
         * 剩余本金
         */
        @ApiModelProperty(value = "剩余本金")
        private Long remainingPrincipal;

        /**
         * 保证金余额
         */
        @ApiModelProperty(value = "保证金余额")
        private Long earnestBalance;

        /**
         * 应计利息
         */
        @ApiModelProperty(value = "应计利息")
        private Long accruedInterest;

        /**
         * 下期租金
         */
        @ApiModelProperty("下期租金")
        private Long nextRent;

        /**
         * 敞口
         */
        @ApiModelProperty(value = "敞口")
        private Long exposure;

        /**
         * 风险等级
         */
        @ApiModelProperty(value = "风险等级")
        private String riskLevel;

        /**
         * 计提比例
         */
        @ApiModelProperty(value = "计提比例")
        private Long withdrawalRatio;

        /**
         * 计提比例-配置
         */
        @ApiModelProperty(value = "计提比例-配置")
        private String withdrawalRatioConfig;

        /**
         * 计提比例-手动修改
         */
        @ApiModelProperty(value = "计提比例-手动修改")
        private Long withdrawalRatioHand;

        /**
         * 本月风险余额
         */
        @ApiModelProperty(value = "本月风险余额")
        private Long profitCurrent;

        /**
         * 上月风险余额
         */
        @ApiModelProperty(value = "上月风险余额")
        private Long profitTotal;

        /**
         * 本月风险金计提/转回
         */
        @ApiModelProperty(value = "本月风险金计提/转回")
        private Long bonusCurrent;

        /**
         * 备注
         */
        @ApiModelProperty("备注")
        private String remark;

    }


}
