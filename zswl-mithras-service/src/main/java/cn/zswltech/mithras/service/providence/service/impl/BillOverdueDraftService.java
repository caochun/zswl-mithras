package cn.zswltech.mithras.service.providence.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.third.providence.entity.BillOverdue;
import cn.zswltech.mithras.third.providence.entity.BillOverdueDraft;
import cn.zswltech.mithras.third.providence.mapper.BillOverdueDraftMapper;
import cn.zswltech.mithras.service.providence.req.BillOverdueReq;
import cn.zswltech.mithras.service.providence.rsp.BillOverdueRsp;
import cn.zswltech.mithras.service.providence.service.BillService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zswltec.providence.dto.base.PageR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/10/24
 * @description
 */
@Slf4j
@Service
public class BillOverdueDraftService extends ServiceImpl<BillOverdueDraftMapper, BillOverdueDraft> {
    @Transactional(rollbackFor = Throwable.class)
    public void parseAndImport(MultipartFile multipartFile, String busiDate) {
        List<BillOverdue> billOverdueList = BillServiceImpl.parsePdf(multipartFile, busiDate);
        List<BillOverdueDraft> copyList = BeanUtil.copyToList(billOverdueList, BillOverdueDraft.class);
        this.getBaseMapper().delete(new LambdaQueryWrapper<BillOverdueDraft>().eq(BillOverdueDraft::getBusiDate, busiDate));
        this.saveBatch(copyList);
    }

    public PageR<BillOverdueRsp> pageList(BillOverdueReq req) {
        Page<BillOverdueDraft> page = new Page<>(req.getPage(),req.getPageSize());
        String busiDate = req.getBusiDate();
        LambdaQueryWrapper<BillOverdueDraft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BillOverdueDraft::getBusiDate);
        if (ObjectUtil.isNotNull(busiDate)) {
            queryWrapper.eq(BillOverdueDraft::getBusiDate, busiDate);
        }
        if (ObjectUtil.isNotNull(req.getOrgName())) {
            queryWrapper.like(BillOverdueDraft::getOrgName, req.getOrgName());
        }
        Page<BillOverdueDraft> overduePage = this.getBaseMapper().selectPage(page, queryWrapper);
        return PageR.of(BeanUtil.copyToList(overduePage.getRecords(), BillOverdueRsp.class), overduePage.getTotal(), overduePage.getPages(), req.getPage(), req.getPageSize());
    }

    public void clear() {
        this.remove(Wrappers.lambdaQuery());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void effect() {
        // 拷贝到生效数据表
        List<BillOverdueDraft> draftList = this.list();
        if (CollectionUtil.isEmpty(draftList)) {
            throw new MithrasException("请导入数据后再提交！");
        }
        // 类型转换
        List<BillOverdue> list = BeanUtil.copyToList(draftList, BillOverdue.class);
        list.forEach(BaseModel::reset);
        // 保存
        String busiDate = list.get(0).getBusiDate();
        SpringUtil.getBean(BillService.class).refreshData(list, busiDate);
        // 删除草稿
        SpringUtil.getBean(BillOverdueDraftService.class).clear();
    }
}
