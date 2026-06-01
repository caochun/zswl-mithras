package cn.zswltech.mithras.service.repository.tyc.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 天眼查接口 通用返回
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:09 PM
 */
@Data
public class TycBaseResp {

    @JSONField(name = "error_code")
    private Integer errorCode;

    private String reason;

}
