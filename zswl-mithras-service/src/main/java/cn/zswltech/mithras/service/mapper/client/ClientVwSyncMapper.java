package cn.zswltech.mithras.service.mapper.client;


import cn.zswltech.mithras.service.mapper.model.client.ClientVwSync;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ClientVwSyncMapper extends BaseMapper<ClientVwSync> {

    List<ClientVwSync> selectAddListByRecord();

    List<ClientVwSync> selectWbClientByRecord();
}
