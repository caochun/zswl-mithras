package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.contract.mapper.contract.ContractSpecialTradeMapper;
import cn.zswltech.mithras.contract.model.contract.ContractSpecialTrade;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 合同特定交易表（用于辅助征信报送）
 *
 * @author wangchuanhao
 * @date 2022/10/14 1:56 PM
 */
@Service
public class ContractSpecialTraderService extends ServiceImpl<ContractSpecialTradeMapper, ContractSpecialTrade> {
}
