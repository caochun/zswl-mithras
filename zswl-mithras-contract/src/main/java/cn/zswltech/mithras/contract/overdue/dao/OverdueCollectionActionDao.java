package cn.zswltech.mithras.contract.overdue.dao;

import cn.zswltech.mithras.contract.overdue.mapper.LetterIndexMapper;
import cn.zswltech.mithras.contract.overdue.mapper.OverdueCollectionActionMapper;
import cn.zswltech.mithras.contract.overdue.mapper.model.OverdueCollectionAction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:36
 */
@Service
public class OverdueCollectionActionDao extends ServiceImpl<OverdueCollectionActionMapper, OverdueCollectionAction> {

    @Resource
    private LetterIndexMapper letterIndexMapper;
    public Integer findCollectLetterIndex(int year) {
        return letterIndexMapper.findCollectLetterIndex(year);
    }

    public void incrementCollectLetterIndex(int year,int count) {
        letterIndexMapper.incrementCollectLetterIndex(year,count);
    }
}
