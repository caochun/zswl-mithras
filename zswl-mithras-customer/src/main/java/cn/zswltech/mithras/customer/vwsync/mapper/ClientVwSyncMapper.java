package cn.zswltech.mithras.customer.vwsync.mapper;


import cn.zswltech.mithras.customer.vwsync.mapper.model.ClientVwSync;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ClientVwSyncMapper extends BaseMapper<ClientVwSync> {

    List<ClientVwSync> selectAddListByRecord();

    List<ClientVwSync> selectWbClientByRecord();
}
