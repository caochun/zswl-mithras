package cn.zswltech.mithras.dto.capital;

import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/19/11:32
 * @description
 */
@Data
public class BankFlowProcessingCenterProjDetailRSP {

    /**
     * 对应模块记录ID
     */
    private Long modelId;

    /**
     * 应付金额
     */
    private Long shouldPayAmount;

    /**
     * 应付时间
     */
    private String shouldPayTime;

    /**
     * 未付金额
     */
    private Long noPayAmount;

    /**
     * 已核销金额
     */
    private List<String> writeOffedAmount;
}
