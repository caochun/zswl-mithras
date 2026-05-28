package cn.zswltech.mithras.report.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报送状态
 * 待报送、已报送
 *
 * @author wangchuanhao
 * @date 2023/1/11 5:39 PM
 */
@AllArgsConstructor
@Getter
public enum ReportState {

    /**
     * 待报送
     */
    TO_BE_REPORT,

    /**
     * 已报送
     */
    REPORTED,
    ;

}
