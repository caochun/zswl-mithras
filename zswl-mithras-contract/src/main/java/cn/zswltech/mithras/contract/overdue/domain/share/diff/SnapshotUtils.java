package cn.zswltech.mithras.contract.overdue.domain.share.diff;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 16:10
 */
public class SnapshotUtils {
    public static <T extends Serializable> T snapshot(T aggregate) {
        return SerializationUtils.clone(aggregate);
    }
}
