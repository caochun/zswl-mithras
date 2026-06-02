package cn.zswltech.mithras.service.convert.contract;

import cn.zswltech.mithras.dto.contract.depost.ContractDeductRentInfoREQ;
import cn.zswltech.mithras.dto.contract.depost.ContractDeductRentInfoRSP;
import cn.zswltech.mithras.dto.contract.depost.ContractRetreatSubmitREQ;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractDeductRentInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRetreatInfo;
import org.mapstruct.Mapper;

@Mapper(uses = TypeConversionWorker.class, componentModel = "spring")
public interface ContractDeductRentInfoConverter {
    ContractDeductRentInfo reviewToContractDeductRentInfo(ContractDeductRentInfoREQ req);
    ContractDeductRentInfoRSP reviewToContractContractDeductRentInfoRSP(CollectionBaseInfo req);
    ContractRetreatInfo reviewToContractContractRetreatInfo(ContractRetreatSubmitREQ req);
}
