package cn.zswltech.mithras.customer.externaldata.zhongdeng.application.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoAddREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoModifyREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRemoveREQ;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.mapper.ZhongdengInfoMapper;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.mapper.model.ZhongdengInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.application.ZhongdengInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.foundation.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 3:00 PM
 */
@Service
public class ZhongdengInfoServiceImpl extends ServiceImpl<ZhongdengInfoMapper, ZhongdengInfo> implements ZhongdengInfoService {

    @Resource
    private ZhongdengInfoMapper zhongdengInfoMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(ZhongdengInfoAddREQ req) {
        ZhongdengInfo entity = BeanUtil.copyProperties(req, ZhongdengInfo.class);
        zhongdengInfoMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(ZhongdengInfoModifyREQ req) {
        ZhongdengInfo originalInfo = zhongdengInfoMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        ZhongdengInfo entity = BeanUtil.copyProperties(req, ZhongdengInfo.class);
        zhongdengInfoMapper.updateAnnotationIncludeNullById(entity);
    }

    @Override
    public Page<ZhongdengInfo> list(ExternalPageREQ req) {
        return zhongdengInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<ZhongdengInfo>lambdaQuery().eq(ZhongdengInfo::getClientId, req.getClientId()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(ZhongdengInfoRemoveREQ req) {
        ZhongdengInfo originalInfo = zhongdengInfoMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        zhongdengInfoMapper.deleteById(req.getId());
    }

}
