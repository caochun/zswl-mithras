package cn.zswltech.mithras.customer.infohistory.application;

import cn.hutool.core.util.IdUtil;
import cn.zswltech.mithras.dto.client.infohistory.InfoHistoryListREQ;
import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.foundation.enums.InfoOperation;
import cn.zswltech.mithras.customer.infohistory.mapper.InfoHistoryMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.customer.infohistory.mapper.model.InfoHistory;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;

import static cn.hutool.core.util.ObjectUtil.equal;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.foundation.util.Const.INFO_HISTORY_CODE_PREFIX;
import static cn.zswltech.mithras.foundation.util.Const.TABLE_FIELD_ID;

/**
 * @author luyi
 */
@Slf4j
@Service
public class InfoHistoryService {

    @Resource
    private InfoHistoryMapper infoHistoryMapper;

    public void addHistory(Long clientId, Long recordId, InfoModule module, InfoOperation operation, Object originData, Object currentData) {
        if (!changed(originData, currentData)) {
            log.warn("没有数据改变，无需保存变更记录.\n originalData:{}\ncurrentData:{}", toJsonStr(originData), toJsonStr(currentData));
            return;
        }
        InfoHistory h = new InfoHistory();
        h.setClientId(clientId);
        h.setHistoryCode(INFO_HISTORY_CODE_PREFIX + IdUtil.getSnowflakeNextIdStr());
        h.setCurrentData(toJsonStr(currentData));
        h.setModuleCode(module.name());
        h.setModuleRecordId(recordId);
        h.setOperationType(operation.name());
        h.setOriginalData(toJsonStr(originData));
        infoHistoryMapper.insert(h);
    }

    private boolean changed(Object originData, Object currentData) {
        if (originData == currentData) {
            return false;
        }
        if (null == originData) {
            return true;
        }
        if (null == currentData) {
            return true;
        }
        skipBaseFields(originData);
        skipBaseFields(currentData);
        return !equal(toJsonStr(originData), toJsonStr(currentData));

    }

    private void skipBaseFields(Object o) {
        if (o instanceof BaseModel) {
            BaseModel b = (BaseModel) o;
            b.setCreateBy(null);
            b.setCreateTime(null);
            b.setUpdateTime(null);
            b.setUpdateBy(null);
        }
        Field idField;
        try {
            idField = o.getClass().getDeclaredField(TABLE_FIELD_ID);
            if (isNotNull(idField)) {
                idField.setAccessible(true);
                idField.set(o, null);
            }
        } catch (Exception e) {
            log.error("反射清空id Field失败", e);
        }
    }

    public InfoHistory detail(Long id) {
        return infoHistoryMapper.selectById(id);
    }

    public Page<InfoHistory> list(InfoHistoryListREQ req) {
        return infoHistoryMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<InfoHistory>lambdaQuery().eq(InfoHistory::getClientId, req.getClientId())
        );
    }
}
