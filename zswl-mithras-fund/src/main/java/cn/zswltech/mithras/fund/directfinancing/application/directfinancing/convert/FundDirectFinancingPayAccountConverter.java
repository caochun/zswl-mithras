package cn.zswltech.mithras.fund.directfinancing.application.directfinancing.convert;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPayAccountModifyREQ;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPayAccount;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-还款账户
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingPayAccountConverter {

    FundDirectFinancingPayAccount addReq2Entity(FundDirectFinancingPayAccountAddREQ req);

    FundDirectFinancingPayAccount modifyReq2Entity(FundDirectFinancingPayAccountModifyREQ req);

    List<FundDirectFinancingPayAccountListRSP> entity2ListRsp(List<FundDirectFinancingPayAccount> payAccounts);
}
