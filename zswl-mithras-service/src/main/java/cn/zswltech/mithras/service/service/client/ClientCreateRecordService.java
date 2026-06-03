package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientCreateRecordMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientCreateRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/9/9
 * @description
 */
@Slf4j
@Service
public class ClientCreateRecordService extends ServiceImpl<ClientCreateRecordMapper, ClientCreateRecord> {
    @Resource
    private SysUserService sysUserService;

    public void create(Long clientId, Long userId) {
        List<OrgDO> orgList = sysUserService.getSpecificUserDeptList(userId);
        if (CollectionUtil.isEmpty(orgList)) {
            throw new MithrasException("当前用户没有所属部门");
        }
        Long deptId = orgList.stream().filter(e -> Objects.equals(e.getType(), OrgConstants.BUSINESS_DEPT)).findFirst().map(OrgDO::getId).orElse(null);
        if (Objects.isNull(deptId)) {
            throw new MithrasException("当前用户没有所属业务部门");
        }
        ClientCreateRecord record = new ClientCreateRecord();
        record.setUserId(userId);
        record.setDeptId(deptId);
        record.setClientId(clientId);
        record.setUniqueCode(LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN));
        try {
            this.save(record);
        } catch (Exception e) {
            // FIXME 这里可能因为并发而保存不成功，但不会影响业务逻辑，不打error，后续可优化
            log.info("客户数据副本新增创建记录发生异常[clientId:{}, userId:{}, message:{}]", clientId, userId, e.getMessage());
        }
    }

    public boolean hasRecord(Long clientId, Long userId) {
        LambdaQueryWrapper<ClientCreateRecord> query = Wrappers.lambdaQuery();
        query.eq(ClientCreateRecord::getClientId, clientId);
        query.eq(ClientCreateRecord::getUserId, userId);
        return this.count(query) > 0;
    }

    public void removeByClientUser(Long clientId, Long userId) {
        LambdaQueryWrapper<ClientCreateRecord> query = Wrappers.lambdaQuery();
        query.eq(ClientCreateRecord::getClientId, clientId);
        query.eq(ClientCreateRecord::getUserId, userId);
        this.remove(query);
    }
}
