package cn.zswltech.mithras.fund.direct.application.directfinancing.convert;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingRepayActualListRSP;
import cn.zswltech.mithras.fund.direct.mapper.model.FundDirectFinancingRepayActual;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingRepayActualConverter {

    FundDirectFinancingRepayActualListRSP entity2ListRsp(FundDirectFinancingRepayActual item);
    
    List<FundDirectFinancingRepayActualListRSP> entity2ListRsp(List<FundDirectFinancingRepayActual> items);
}
