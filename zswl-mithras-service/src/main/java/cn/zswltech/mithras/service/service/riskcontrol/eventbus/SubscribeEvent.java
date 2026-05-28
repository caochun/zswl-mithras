package cn.zswltech.mithras.service.service.riskcontrol.eventbus;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description
 * @date 2021/8/4 3:27 下午
 */
@Data
public class SubscribeEvent {
    private LocalDateTime eventTime;
}
