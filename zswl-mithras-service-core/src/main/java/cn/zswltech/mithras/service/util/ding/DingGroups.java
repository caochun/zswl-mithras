package cn.zswltech.mithras.service.util.ding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luyi
 */
public class DingGroups {

    public static final DingGroup CANG_QIONG = new DingGroup(
            "https://oapi.dingtalk.com/robot/send?access_token=2b53aade9640576e5521740c5db3f8bb1daab9250bc741c7b30193e1fc7921ed",
            "SECf70c27fec09ab48864b82045a261218fdf76b27ee5e501cdf1598a8845c89c95"
    );


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DingGroup {
        private String webhook;
        private String token;
    }
}
