package cn.zswltech.mithras.service.mapper.liquiditymanage;

import cn.zswltech.mithras.service.mapper.model.liquiditymanage.FundFinancingAccountSetting;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.dto.AccountSettingListQueryDTO;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.dto.AccountSettingListResultDTO;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

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
