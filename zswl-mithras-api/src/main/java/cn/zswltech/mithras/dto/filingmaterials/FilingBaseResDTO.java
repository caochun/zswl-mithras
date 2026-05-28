package cn.zswltech.mithras.dto.filingmaterials;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author lllin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilingBaseResDTO {
    /**
     * 承租人ids
     */
    private Set<Long> lesseeIds;
    /**
     * 法人ids
     */
    private Set<Long> entIds;
    /**
     * 自然人ids
     */
    private Set<Long> indIds;
}
