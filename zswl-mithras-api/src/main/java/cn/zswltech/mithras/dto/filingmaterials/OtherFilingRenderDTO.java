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
public class OtherFilingRenderDTO {
    /**
     * 移交人
     */
    private Long belongUserId;
    /**
     * 项目编号
     */
    private String projName;

}
