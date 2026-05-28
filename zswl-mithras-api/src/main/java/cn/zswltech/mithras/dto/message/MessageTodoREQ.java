package cn.zswltech.mithras.dto.message;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;

/**
 * 任务列表查询
 *
 * @author zhouning
 * @date 2022/7/28 3:19 PM
 */
@Data
public class MessageTodoREQ extends PageReq {
    private String account;

}
