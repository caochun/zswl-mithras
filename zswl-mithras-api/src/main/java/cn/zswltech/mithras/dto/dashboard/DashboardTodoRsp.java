package cn.zswltech.mithras.dto.dashboard;

import lombok.Data;

/**
 * 任务列表查询
 *
 * @author zhouning
 * @date 2022/7/28 3:19 PM
 */
@Data
public class DashboardTodoRsp {

    private String tabName;
    private Integer value;
    private String startUserId;

    public DashboardTodoRsp(String tabName, String startUserId, Integer value) {
        this.tabName = tabName;
        this.startUserId = startUserId;
        this.value = value;
    }
}
