package cn.zswltech.mithras.service.service.third.model;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 * @author luyi
 */
@Data
public class TycRsp<T> {
    @Alias("error_code")
    private Integer errorCode;

    private String reason;

    private T result;
}
