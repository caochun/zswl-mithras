package cn.zswltech.mithras.service.service;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.ProcAttentionRecordMapper;
import cn.zswltech.mithras.service.mapper.model.ProcAttentionRecord;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程关注记录
 *
 * @author wangchuanhao
 * @date 2022/12/9 1:06 PM
 */
@Service
public class ProcAttentionRecordService extends ServiceImpl<ProcAttentionRecordMapper, ProcAttentionRecord> {

    /**
     * 记录关注情况
     * @param processInstanceId
     * @param userId
     * @param attentionType
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordAttention(String processInstanceId, Long userId, @Nullable Integer attentionType) {
        if (Objects.isNull(attentionType)) {
            return;
        }
        ProcAttentionRecord attentionRecord = ProcAttentionRecord.builder()
                .processInstanceId(processInstanceId)
                .userId(userId)
                .attentionType(attentionType)
                .build();
        baseMapper.insertOrUpdate(attentionRecord);
    }

    /**
     * 判断是否关注了流程
     * @param processInstanceId
     * @param userId
     * @return
     */
    public Integer findAttentionType(String processInstanceId, Long userId) {
        return Optional.ofNullable(baseMapper.selectOne(Wrappers.<ProcAttentionRecord>lambdaQuery()
                        .eq(ProcAttentionRecord::getProcessInstanceId, processInstanceId)
                        .eq(ProcAttentionRecord::getUserId, userId)
                        .last("LIMIT 1")
                )).map(ProcAttentionRecord::getAttentionType)
                .orElse(YesOrNoNumberEnum.NO.getCode());
    }

    /**
     * 查找关注了流程的人的id列表
     * @param processInstanceId
     * @return
     */
    public List<Long> listAttentionUserIdList(String processInstanceId) {
        return baseMapper.selectList(Wrappers.<ProcAttentionRecord>lambdaQuery()
                .eq(ProcAttentionRecord::getProcessInstanceId, processInstanceId)
                .gt(ProcAttentionRecord::getAttentionType, 0))
                .stream().map(ProcAttentionRecord::getUserId).collect(Collectors.toList());
    }

}
