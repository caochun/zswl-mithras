package cn.zswltech.mithras.service.overdue.domain.docprinting;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.overdue.domain.share.ValueObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 18:43
 */
@Data
public class PrintingCode implements ValueObject<String>, Serializable {

    private String code;
    private final String prefix = "yy";
    private String dateStr;
    private int index;

    public PrintingCode() {
        dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        index = 1;
        code = prefix + dateStr + String.format("%03d", index);
    }

    public PrintingCode(String dateStr, int index) {
        this.dateStr = dateStr;
        this.index = index;
        code = prefix + dateStr + String.format("%03d", index);
    }

    public PrintingCode(String code) {
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

    public PrintingCode nextCode() {
        String nowDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        if(!dateStr.equals(nowDateStr)){
            return new PrintingCode();
        }
        return new PrintingCode(dateStr, index + 1);
    }

    public void setCode(String code){
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
}
