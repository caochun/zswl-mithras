package cn.zswltech.mithras.third.repository.tyc.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 天眼查接口 列表 通用返回
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:09 PM
 */
@Data
public class TycListBaseResp<T> extends TycBaseResp {

    @JSONField(name = "error_code")
    private Integer errorCode;

    private String reason;

    private Result<T> result;

    @Data
    public static class Result<T> {

        /**
         * 总数
         */
        private Integer total;
        private List<T> items;

    }

}
