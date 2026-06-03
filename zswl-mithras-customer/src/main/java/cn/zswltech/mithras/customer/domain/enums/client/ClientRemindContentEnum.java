package cn.zswltech.mithras.customer.domain.enums.client;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author junke
 */
@Getter
@AllArgsConstructor
public enum ClientRemindContentEnum implements PullDown{



    EMAIL_01("未有立项审批通过的项目", "EMAIL", "01"),
    EMAIL_02("未发起项目评审且经部门分管领导审批通过", "EMAIL", "02"),
    EMAIL_03("未实际投放", "EMAIL", "03"),
    EMAIL_04("未有新的立项审批通过的项目", "EMAIL", "04"),

    MESSAGE_01("若7天后仍未立项审批通过", "MESSAGE", "01"),
    MESSAGE_02("若7天后仍未发起项目评审且经部门分管领导审批通过", "MESSAGE", "02"),
    MESSAGE_03("若7天后仍未进行投放", "MESSAGE", "03"),
    MESSAGE_04("若7天后仍未发起新的立项且审批通过", "MESSAGE", "04");

    private final String display;
    private final String type;
    private final String source;

    @Override
    public String display() {
        return display;
    }

    public static ClientRemindContentEnum getContentEnum(String type, String source) {
        for (ClientRemindContentEnum value : ClientRemindContentEnum.values()) {
            if (value.getType().equals(type) && value.getSource().equals(source)) {
                return value;
            }
        }
        return null;
    }


    public static ClientRemindContentEnum of(String name) {
        for (ClientRemindContentEnum value : ClientRemindContentEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }


    public static Map<String,ClientRemindContentEnum> getAllMap(){
        Map<String,ClientRemindContentEnum> map = new HashMap<>();
        for (ClientRemindContentEnum value : ClientRemindContentEnum.values()) {
            map.put(value.name(),value);
        }
       return map;
    }

}
