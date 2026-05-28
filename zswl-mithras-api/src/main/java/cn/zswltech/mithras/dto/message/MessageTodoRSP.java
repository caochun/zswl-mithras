package cn.zswltech.mithras.dto.message;

import lombok.Data;

/**
 * 任务列表查询
 *
 * @author zhouning
 * @date 2022/7/28 3:19 PM
 */
@Data
public class MessageTodoRSP {

    private String tabName;
    private Integer value;
    private String startUserId;

    public MessageTodoRSP(String tabName, String startUserId, Integer value) {
        this.tabName = tabName;
        this.startUserId = startUserId;
        this.value = value;
    }
}
