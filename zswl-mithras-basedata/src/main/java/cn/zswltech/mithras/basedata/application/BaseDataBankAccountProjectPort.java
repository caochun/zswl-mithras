package cn.zswltech.mithras.basedata.application;

import cn.zswltech.mithras.dto.basedata.ContractAccountPayListREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListRSP;

import java.util.List;

public interface BaseDataBankAccountProjectPort {

    List<ContractAccountPayListRSP> nameList(ContractAccountPayListREQ req);
}
