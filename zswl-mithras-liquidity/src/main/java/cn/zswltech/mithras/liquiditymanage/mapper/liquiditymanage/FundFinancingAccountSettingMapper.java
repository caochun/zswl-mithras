package cn.zswltech.mithras.liquiditymanage.mapper;

import cn.zswltech.mithras.liquiditymanage.mapper.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquiditymanage.mapper.model.dto.AccountSettingListQueryDTO;
import cn.zswltech.mithras.liquiditymanage.mapper.model.dto.AccountSettingListResultDTO;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
