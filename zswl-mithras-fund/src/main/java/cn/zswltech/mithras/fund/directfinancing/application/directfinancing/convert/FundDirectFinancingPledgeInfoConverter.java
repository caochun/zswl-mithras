package cn.zswltech.mithras.fund.directfinancing.application.directfinancing.convert;

import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPledgeInfoAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPledgeInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPledgeInfoListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingPledgeInfoModifyREQ;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.FundDirectFinancingPledgeInfoExcelModel;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-质押明细
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingPledgeInfoConverter {

    FundDirectFinancingPledgeInfo addReq2Entity(FundDirectFinancingPledgeInfoAddREQ req);

    FundDirectFinancingPledgeInfoListRSP entity2ListRsp(FundDirectFinancingPledgeInfo pledgeInfo);

    FundDirectFinancingPledgeInfoDetailRSP entity2DetailRsp(FundDirectFinancingPledgeInfo fundDirectFinancingPledgeInfo);

    List<FundDirectFinancingPledgeInfoExcelModel> entity2ExcelModel(List<FundDirectFinancingPledgeInfo> list);

    FundDirectFinancingPledgeInfo modifyReq2Entity(FundDirectFinancingPledgeInfoModifyREQ req);
}
