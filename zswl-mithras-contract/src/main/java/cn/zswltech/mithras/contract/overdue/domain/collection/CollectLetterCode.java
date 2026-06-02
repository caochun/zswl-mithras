package cn.zswltech.mithras.contract.overdue.domain.collection;

import cn.zswltech.mithras.contract.overdue.domain.share.ValueObject;
import lombok.Getter;

import java.io.Serializable;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/29 15:17
 */
@Getter
public class CollectLetterCode implements ValueObject<String>, Serializable {

    private final int year;
    private final int index;
    @Getter
    private String code;

    private final String codePatten = "【%d】催字第(%04d)号";

    public CollectLetterCode(int year, int index) {
        this.year = year;
        this.index = index;
        this.code = String.format(codePatten, year, index);
    }

    public CollectLetterCode nextCode(){
        return new CollectLetterCode(year, index + 1);
    }


    @Override
    public boolean sameValueAs(String other) {
        return this.code.equals(other);
    }

    @Override
    public String copy() {
        return this.code;
    }
}
