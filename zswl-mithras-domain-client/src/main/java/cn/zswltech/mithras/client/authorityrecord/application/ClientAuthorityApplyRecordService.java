package cn.zswltech.mithras.client.authorityrecord.application;

import cn.zswltech.mithras.client.authorityrecord.infrastructure.mapper.ClientAuthorityApplyRecordMapper;
import cn.zswltech.mithras.client.authorityrecord.infrastructure.model.ClientAuthorityApplyRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2024/9/13
 * @description
 */
@Slf4j
@Service
public class ClientAuthorityApplyRecordService extends ServiceImpl<ClientAuthorityApplyRecordMapper, ClientAuthorityApplyRecord> {
    public ClientAuthorityApplyRecord findByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<ClientAuthorityApplyRecord> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthorityApplyRecord::getProcessInstanceId, processInstanceId);
        return this.getOne(query);
    }
}
