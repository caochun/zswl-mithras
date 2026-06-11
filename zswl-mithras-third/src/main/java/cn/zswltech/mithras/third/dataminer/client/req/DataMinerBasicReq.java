package cn.zswltech.mithras.third.dataminer.client.req;

import cn.zswltech.mithras.third.dataminer.client.DataMinerApiInfoEnum;

import java.io.Serializable;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
public abstract class DataMinerBasicReq implements Serializable {
    public abstract DataMinerApiInfoEnum dataMinerApiInfo();
}
