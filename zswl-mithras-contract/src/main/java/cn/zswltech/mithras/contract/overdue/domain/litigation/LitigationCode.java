package cn.zswltech.mithras.contract.overdue.domain.litigation;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.overdue.domain.share.ValueObject;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:12
 */
@Getter
public class LitigationCode implements ValueObject<String>, Serializable {
    private String code;
    private final String prefix = "ss-";
    private String dateStr;
    private int index;

    public LitigationCode() {
        dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        index = 1;
        code = prefix + dateStr + String.format("%03d", index);
    }

    public LitigationCode(String dateStr, int index) {
        this.dateStr = dateStr;
        this.index = index;
        code = prefix + dateStr + String.format("%03d", index);
    }

    public LitigationCode(String code) {
        if(ObjectUtil.isEmpty(code)){
            throw new IllegalArgumentException("code is empty");
        }
        if(!code.startsWith(prefix)){
            throw new IllegalArgumentException("code is not start with cs");
        }
        this.dateStr = code.substring(3, 11);
        this.index = Integer.parseInt(code.substring(11));
        this.code = code;
    }

    public LitigationCode nextCode() {
        String nowDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        if(!dateStr.equals(nowDateStr)){
            return new LitigationCode();
        }
        return new LitigationCode(dateStr, index + 1);
    }

    public void setCode(String code){
        this.dateStr = code.substring(3, 11);
        this.index = Integer.parseInt(code.substring(11));
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
}
