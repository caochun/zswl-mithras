package cn.zswltech.mithras.fund.directfinancing.application.convert;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingCollectAccountModifyREQ;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingCollectAccount;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-对方收款账户
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingCollectAccountConverter {

    FundDirectFinancingCollectAccount addReq2Entity(FundDirectFinancingCollectAccountAddREQ req);

    FundDirectFinancingCollectAccount modifyReq2Entity(FundDirectFinancingCollectAccountModifyREQ req);

    List<FundDirectFinancingCollectAccountListRSP> entity2ListRsp(List<FundDirectFinancingCollectAccount> CollectAccounts);
}
