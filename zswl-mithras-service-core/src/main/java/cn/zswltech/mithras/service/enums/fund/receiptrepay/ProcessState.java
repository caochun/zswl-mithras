package cn.zswltech.mithras.service.enums.fund.receiptrepay;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流程状态
 *
 * @author wangchuanhao
 * @date 2023/2/20 11:40 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("fundReceiptRepayProcessState")
public enum ProcessState implements PullDown {

    UN_SUBMIT("未提交"),
    COMMIT("审批中"),
    CANCEL("取消流程"),
    REJECT("审批拒绝"),
    PASS("审批通过"),

    ;

    private String display;

    private static Map<String, ProcessState> map;
    static {
        map = Stream.of(ProcessState.values()).collect(Collectors.toMap(ProcessState::name, e -> e));
    }

    public static ProcessState of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
