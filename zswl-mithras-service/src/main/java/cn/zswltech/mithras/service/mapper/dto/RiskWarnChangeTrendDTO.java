package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName RiskControlWarnMonitorPageWarnDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/12/24 17:52
 * @Version 1.0
 **/
@Data
public class RiskWarnChangeTrendDTO {
    /**
     * 客户名称
     */
    private LocalDate date;

    private Integer num;
}
