package cn.zswltech.mithras.third.service.dataminer.req;

import cn.zswltech.mithras.third.service.dataminer.DataMinerApiInfoEnum;

import java.io.Serializable;

/**
 * @author dingqi
 * @date 2025/3/17
 * @description
 */
public abstract class DataMinerBasicReq implements Serializable {
    public abstract DataMinerApiInfoEnum dataMinerApiInfo();
}
