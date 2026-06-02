package cn.zswltech.mithras.client.vwsync.application;


import cn.zswltech.mithras.client.vwsync.infrastructure.mapper.ClientVwSyncMapper;
import cn.zswltech.mithras.client.vwsync.infrastructure.model.ClientVwSync;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientVwSyncService extends ServiceImpl<ClientVwSyncMapper, ClientVwSync> {

}
