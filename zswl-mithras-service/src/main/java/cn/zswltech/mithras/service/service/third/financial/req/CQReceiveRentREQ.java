package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CQReceiveRentREQ
 * @Description 苍穹租金应收单
 * @Author jackerhe
 * @Date 2022/10/21 4:06 下午
 * @Version 1.0
 **/
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class CQReceiveRentREQ extends CQBaseREQ {

    //申请组织
    private String settleorg;

    //默认‘ar_finarbill_BT_zb’
    //单据类型 默认应收单（总部）
    private String billtype = "ar_finarbill_BT_zb";

    //业务类型
    private String bizType;

    //客户
    private String custome;

    //来源系统 默认融租易系统
    private String cico_srcsystem = "RZY";

    //合同号 contractCode
    private String contractCode;

    //部门
    private String department;


    private List<ReceiveRentBody> entry = new ArrayList<>();

    @Data
    @Accessors(chain = true)
    public static class ReceiveRentBody {

        private String rentActualid;

        private String rentActualCode;

        //利率
        private BigDecimal leaseRate;
        //是否开票
        private String cico_isinvoice;

        //日期
        private String date;

        //期项
        private Integer phase;

        //租金
        private BigDecimal rent;

        //本金
        private BigDecimal principal;

        //利息
        private BigDecimal interest;

        //剩余本金
        private BigDecimal lastAmount;

        //变更状态 0新增 1变更
        private Integer billing;

        /**
         * 变更类型
         * LPR调整:A
         * 提前还款:B
         * 调整还款计划:C
         * 展期:D
         * E 提前结清
         * F 提前结清抵扣
         * G 抵扣
         **/
        private String changeState;

        //租金差额
        private BigDecimal rentdifference;
        //本金差额
        private BigDecimal principaldifference;
        //利息差额
        private BigDecimal interestdifference;

    }

}
