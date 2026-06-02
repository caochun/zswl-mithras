package cn.zswltech.mithras.client.vwsync.infrastructure.mapper;


import cn.zswltech.mithras.client.vwsync.infrastructure.model.ClientVwSync;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ClientVwSyncMapper extends BaseMapper<ClientVwSync> {

    List<ClientVwSync> selectAddListByRecord();

    List<ClientVwSync> selectWbClientByRecord();
}
