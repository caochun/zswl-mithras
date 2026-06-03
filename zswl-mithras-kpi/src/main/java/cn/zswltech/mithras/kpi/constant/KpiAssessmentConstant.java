package cn.zswltech.mithras.kpi.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/9
 * @description
 */
public interface KpiAssessmentConstant {
    // 综合管理部，领导层（领导层、评审会、风委会、董事会）
    List<String> CAN_VIEW_ORG = Arrays.asList("ZHGLB", "LDC", "XMPSWYH", "FXGLWYH", "DSH");
}
