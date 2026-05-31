package cn.zswltech.mithras.service.mapper.message;

import cn.zswltech.mithras.service.enums.MessageType;
import lombok.Data;

import java.util.Map;

/**
 * 合同起租提醒消息
 *
 * @author: jackerhe
 * @date: 2023/4/26 3:46 下午
 **/
@Data
public class PopUpNotificationBody implements MessageBody {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 业务id CONTRACT_RENT  /  FINANCE_PLEDGE_ADD  还是放在attachment里面
     */
//    private String popUpType;

    /**
     * 业务id
     */
    private Map<String, Object> attachment;


    @Override
    public String getSendType() {
        return MessageType.POPUP.getType();
    }

}
