package cn.zswltech.mithras.creditlimit.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.creditlimit.mapper.CreditBusinessRefMapper;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditBusinessRef;
import cn.zswltech.mithras.creditlimit.mapper.model.CreditLimitDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description
 */
@Slf4j
@Service
public class CreditBusinessRefService extends ServiceImpl<CreditBusinessRefMapper, CreditBusinessRef> {

    public void invalidByGrantingSubjectKey(String bizType, String grantingSubjectKey) {
        List<CreditBusinessRef> list = this.listByGrantingSubjectKey(bizType, grantingSubjectKey);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        list.forEach(e -> {
            e.setEffective(YesOrNoNumberEnum.NO.getCode());
        });
        this.updateBatchById(list);
    }

    public List<CreditBusinessRef> listByGrantingSubjectKey(String bizType, String grantingSubjectKey) {
        LambdaQueryWrapper<CreditBusinessRef> query = Wrappers.lambdaQuery();
        query.eq(CreditBusinessRef::getBizType, bizType);
        query.eq(CreditBusinessRef::getGrantingSubjectKey, grantingSubjectKey);
        return this.list(query);
    }

    public List<CreditBusinessRef> listByBizTargetKey(String bizType, String bizTargetKey) {
        LambdaQueryWrapper<CreditBusinessRef> query = Wrappers.lambdaQuery();
        query.eq(CreditBusinessRef::getBizType, bizType);
        query.eq(CreditBusinessRef::getEffective, YesOrNoNumberEnum.YES.getCode());
        query.eq(CreditBusinessRef::getBizTargetKey, bizTargetKey);
        return this.list(query);
    }

    public CreditBusinessRef getOneByFourKeys(String bizType, String grantingSubjectKey, String bizSourceKey, String bizTargetKey) {
        LambdaQueryWrapper<CreditBusinessRef> query = Wrappers.lambdaQuery();
        query.eq(CreditBusinessRef::getBizType, bizType);
        query.eq(CreditBusinessRef::getGrantingSubjectKey, grantingSubjectKey);
        query.eq(CreditBusinessRef::getBizSourceKey, bizSourceKey);
        query.eq(CreditBusinessRef::getBizTargetKey, bizTargetKey);
        return this.getOne(query);
    }

    public Map<Long, List<CreditBusinessRef>> getBySourceIds(Collection<Long> sourceIdList, CreditLimitBizTypeEnum bizTypeEnum){
        List<CreditBusinessRef> list = this.list(Wrappers.<CreditBusinessRef>lambdaQuery()
                .eq(CreditBusinessRef::getBizType, bizTypeEnum.name())
                .in(CollectionUtil.isNotEmpty(sourceIdList), CreditBusinessRef::getBizSourceKey, sourceIdList));
        return Optional.ofNullable(list).map(item ->
                list.stream().collect(Collectors.groupingBy(m -> Long.valueOf(m.getBizSourceKey())))
        ).orElse(Collections.emptyMap());

    }

    public Map<Long, List<CreditBusinessRef>> getBySubjectIds(Collection<Long> subjectIdList, CreditLimitBizTypeEnum bizTypeEnum){
        List<CreditBusinessRef> list = this.list(Wrappers.<CreditBusinessRef>lambdaQuery()
                .eq(CreditBusinessRef::getBizType, bizTypeEnum.name())
                .in(CollectionUtil.isNotEmpty(subjectIdList), CreditBusinessRef::getGrantingSubjectKey, subjectIdList));
        return Optional.ofNullable(list).map(item ->
                list.stream().collect(Collectors.groupingBy(m -> Long.valueOf(m.getGrantingSubjectKey())))
        ).orElse(Collections.emptyMap());

    }



    public List<Long> getBySourceId(Long sourceId, CreditLimitBizTypeEnum bizTypeEnum){
        return Optional.ofNullable(this.list(Wrappers.<CreditBusinessRef>lambdaQuery()
                        .select(CreditBusinessRef::getBizTargetKey)
                        .eq(CreditBusinessRef::getBizType, bizTypeEnum.name())
                        .eq(CreditBusinessRef::getBizSourceKey, sourceId)))
                .map(m -> m.stream().map(CreditBusinessRef::getBizTargetKey).map(Long::valueOf).collect(Collectors.toList()))
                .orElse(Collections.emptyList());

    }


    /**
     *
     * @param targetId
     * @param bizTypeEnum
     * @return {key: subjectKey, value: { key: sourceKey, value: list}}
     */
    public Map<Long, Map<Long, List<CreditBusinessRef>>> getByTargetId(Long targetId, CreditLimitBizTypeEnum bizTypeEnum){
        List<CreditBusinessRef> list = this.list(Wrappers.<CreditBusinessRef>lambdaQuery()
                .eq(CreditBusinessRef::getBizType, bizTypeEnum.name())
                .eq(CreditBusinessRef::getBizTargetKey, targetId));
        return Optional.ofNullable(list).map(m -> m.stream()
                .collect(Collectors.groupingBy(c -> Long.valueOf(c.getGrantingSubjectKey()),
                Collectors.groupingBy(c -> Long.valueOf(c.getBizSourceKey()))))
        ).orElse(Collections.emptyMap());



    }


}
