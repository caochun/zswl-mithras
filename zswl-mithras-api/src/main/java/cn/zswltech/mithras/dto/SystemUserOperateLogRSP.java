package cn.zswltech.mithras.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@Data
public class SystemUserOperateLogRSP {
    private Long id;
    private Long userId;
    private String userName;
    private String url;
    private LocalDateTime operateTime;
    private String reqData;
}
