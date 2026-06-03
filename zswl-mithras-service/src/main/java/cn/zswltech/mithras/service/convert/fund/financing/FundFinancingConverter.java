package cn.zswltech.mithras.service.convert.fund.financing;

import cn.zswltech.mithras.dto.fund.financing.payaccount.FundFinancingPayAccountBankRSP;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/21 11:47 上午
 **/
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface FundFinancingConverter {

    @Mapping(target = "bankAccountId", source = "id")
    @Mapping(source = "openingDate", target = "accountOpeningDate", qualifiedByName = "toLocalDateForYYYYMMDD")
    FundFinancingPayAccountBankRSP modifyReq2Entity(BaseDataBankAccount req);

}

