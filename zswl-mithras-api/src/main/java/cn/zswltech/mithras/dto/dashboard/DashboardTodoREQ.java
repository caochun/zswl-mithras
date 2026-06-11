package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;


/**
 * 任务列表查询
 *
 * @author zhouning
 * @date 2022/7/28 3:19 PM
 */
@Data
public class DashboardTodoREQ extends PageReq {
    private String account;

    private String processName;
    //流程ID
    private String processInstanceId;
    //发起人
    private String startUserId;
}
