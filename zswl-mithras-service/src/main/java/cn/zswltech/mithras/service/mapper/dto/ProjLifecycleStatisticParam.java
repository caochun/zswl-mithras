package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目全周期统计dto
 *
 * @author wangchuanhao
 * @date 2022/12/7 12:53 PM
 */
@Data
public class ProjLifecycleStatisticParam extends BaseAuthDTO {

    /**
     * 计算xx时间之后的新增数量
     */
    private LocalDateTime addLimitTime;

}
