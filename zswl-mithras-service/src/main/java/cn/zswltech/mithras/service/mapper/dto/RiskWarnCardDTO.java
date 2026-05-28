package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

/**
 * @ClassName RiskControlWarnMonitorPageWarnDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/12/24 17:52
 * @Version 1.0
 **/
@Data
public class RiskWarnCardDTO {
    /**
     * 客户名称
     */
    private String cardCode;

    private Integer amount;
}
