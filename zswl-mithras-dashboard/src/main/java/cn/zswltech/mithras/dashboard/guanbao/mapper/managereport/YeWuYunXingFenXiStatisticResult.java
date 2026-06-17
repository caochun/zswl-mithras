package cn.zswltech.mithras.dashboard.guanbao.mapper.managereport;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/12/12
 * @description
 */
@Data
public class YeWuYunXingFenXiStatisticResult {
    private Long bizDeptId;
    private String bizDeptName;
    private String yearAndMonth;
    private Integer quantity = 0;
    private String processModelType;
    private Long amount = 0L;
}
