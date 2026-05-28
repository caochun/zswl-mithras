package cn.zswltech.mithras.dto.filingmaterials;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author lllin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterLeasingRenderDTO {
    /**
     * 移交人
     */
    private Long assetUserId;
    /**
     * 项目编号
     */
    private String projName;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 年份
     */
    private String year;
    /**
     * 期次
     */
    private String phase;
    private String planName;
    private String checkWay;

}
