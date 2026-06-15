package cn.zswltech.mithras.margin.application.port;

import cn.zswltech.mithras.margin.application.port.model.MarginContractInfo;

public interface MarginContractInfoPort {
    MarginContractInfo getLatestContractInfo(Long contractId);
}
