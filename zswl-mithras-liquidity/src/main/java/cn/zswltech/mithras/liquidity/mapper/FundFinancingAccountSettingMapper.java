package cn.zswltech.mithras.liquidity.mapper;

import cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquidity.dto.AccountSettingListQueryDTO;
import cn.zswltech.mithras.liquidity.dto.AccountSettingListResultDTO;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import java.util.List;

/**
 * <p>
 * 回款账户配置表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
public interface FundFinancingAccountSettingMapper extends CustomBaseMapper<FundFinancingAccountSetting> {

    List<AccountSettingListResultDTO> queryList(AccountSettingListQueryDTO queryDTO);

}
