package cn.zswltech.mithras.collection.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

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
