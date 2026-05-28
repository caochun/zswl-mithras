package cn.zswltech.mithras.service.overdue.domain.collection;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.overdue.domain.share.ValueObject;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/24 20:56
 */
@Getter
public class ActionCode implements ValueObject<String>,Serializable {
    private String code;
    private final String prefix = "cs";
    private String dateStr;
    private int index;

    public ActionCode() {
        dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        index = 1;
        code = prefix + dateStr + String.format("%03d", index);
    }

    public ActionCode(String dateStr, int index) {
        this.dateStr = dateStr;
        this.index = index;
        code = prefix + dateStr + String.format("%03d", index);
    }

    public ActionCode(String code) {
        if(ObjectUtil.isEmpty(code)){
            throw new IllegalArgumentException("code is empty");
        }
        if(!code.startsWith(prefix)){
            throw new IllegalArgumentException("code is not start with cs");
        }
        this.dateStr = code.substring(2, 10);
        this.index = Integer.parseInt(code.substring(10));
        this.code = code;
    }

    public ActionCode nextCode() {
        String nowDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        if(!dateStr.equals(nowDateStr)){
            return new ActionCode();
        }
        return new ActionCode(dateStr, index + 1);
    }

    public void setCode(String code){
        if (ObjectUtil.isEmpty(code)) {
            return;
        }
        this.dateStr = code.substring(2, 10);
        this.index = Integer.parseInt(code.substring(10));
        this.code = code;
    }

    @Override
    public boolean sameValueAs(String other) {
        return Objects.equals(code, other);
    }

    @Override
    public String copy() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionCode)) return false;
        ActionCode that = (ActionCode) o;
        return Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }
}
