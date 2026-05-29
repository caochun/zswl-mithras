package cn.zswltech.mithras.service.enums.collection;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @create: 2022-08-18
 **/
public enum CollectionRecordWriteOffStatus implements PullDown {
    TO_BE_WRITE_OFF("待核销"),
    WRITTEN_OFF("已核销"),
    IGNORE("忽略");
    public String display;
    CollectionRecordWriteOffStatus(String display){
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }
}
