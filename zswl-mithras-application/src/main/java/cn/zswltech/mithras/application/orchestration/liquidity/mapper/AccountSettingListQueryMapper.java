package cn.zswltech.mithras.application.orchestration.liquidity.mapper;

import cn.zswltech.mithras.liquidity.dto.AccountSettingListQueryDTO;
import cn.zswltech.mithras.liquidity.dto.AccountSettingListResultDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountSettingListQueryMapper {

    List<AccountSettingListResultDTO> queryList(AccountSettingListQueryDTO queryDTO);
}
