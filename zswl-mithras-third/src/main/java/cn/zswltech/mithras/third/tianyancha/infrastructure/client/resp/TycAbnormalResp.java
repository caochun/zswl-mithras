package cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 经营异常
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:56 PM
 */
@Data
public class TycAbnormalResp extends TycListBaseResp<TycAbnormalResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 移出⽇期
         */
        private String removeDate;

        /**
         * 列⼊异常名录原因
         */
        private String putReason;

        /**
         * 决定列⼊异常名录部⻔(作出决定机关)
         */
        private String putDepartment;

        /**
         * 移出部⻔
         */
        private String removeDepartment;

        /**
         * 移除异常名录原因
         */
        private String removeReason;

        /**
         * 列⼊⽇期
         */
        private String putDate;
    }

}
