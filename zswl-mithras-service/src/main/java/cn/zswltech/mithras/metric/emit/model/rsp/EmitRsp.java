package cn.zswltech.mithras.metric.emit.model.rsp;

import lombok.Data;

/**
 * @author yibin
 */
@Data
public class EmitRsp<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T data;
}
