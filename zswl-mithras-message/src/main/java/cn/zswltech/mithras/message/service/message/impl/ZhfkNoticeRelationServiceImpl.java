package cn.zswltech.mithras.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.message.mapper.message.ZhfkNoticeRelation;
import cn.zswltech.mithras.message.mapper.message.ZhfkNoticeRelationMapper;
import cn.zswltech.mithras.message.service.ZhfkNoticeRelationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ZhfkNoticeRelationServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/9/17 11:44 上午
 * @Version 1.0
 **/
@Service
public class ZhfkNoticeRelationServiceImpl extends ServiceImpl<ZhfkNoticeRelationMapper, ZhfkNoticeRelation> implements ZhfkNoticeRelationService {

    @Override
    public List<Long> idToMsgIdList(List<Long> ids) {
        if(ObjectUtil.isEmpty(ids)){
            return null;
        }
        return baseMapper.selectBatchIds(ids).stream().map(ZhfkNoticeRelation::getMessageId).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
    }

    @Override
    public List<ZhfkNoticeRelation> listByNoticeIds(List<Long> noticeIds) {
        if(ObjectUtil.isEmpty(noticeIds)){
            return Collections.emptyList();
        }
        return baseMapper.selectList(Wrappers.<ZhfkNoticeRelation>lambdaQuery()
        .in(ZhfkNoticeRelation::getMessageId, noticeIds));
    }

    @Override
    public Long idToMsgId(Long id) {
        if(ObjectUtil.isNull(id)){
            return null;
        }
        ZhfkNoticeRelation zhfkNoticeRelation = baseMapper.selectById(id);
        if(ObjectUtil.isNotNull(zhfkNoticeRelation)){
            return zhfkNoticeRelation.getMessageId();
        }
        return null;
    }
}
