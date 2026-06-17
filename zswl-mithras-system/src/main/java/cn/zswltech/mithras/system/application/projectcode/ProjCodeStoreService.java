package cn.zswltech.mithras.system.application.projectcode;

import cn.zswltech.mithras.system.mapper.ProjCodeStoreMapper;
import cn.zswltech.mithras.system.mapper.model.ProjCodeStore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static cn.hutool.core.util.NumberUtil.decimalFormat;
import static cn.hutool.core.util.RandomUtil.randomInt;

/**
 * 项目编号拆表
 *
 * @author wangchuanhao
 * @date 2022/11/16 10:37 AM
 */
@Service
public class ProjCodeStoreService extends ServiceImpl<ProjCodeStoreMapper, ProjCodeStore> {

    public String generateProjCode(String bizType, Long seq) {
        LocalDate now = LocalDate.now();
        return bizType + now.getYear() + randomInt(0, 10) + decimalFormat("0000", seq);
    }

    /**
     * 查找该业务类型最大编号
     * @param bizType
     * @return
     */
    public Long maxSeqId(String bizType) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(max(type_seq_id),0) as typeSeqId");
        queryWrapper.eq("biz_type", bizType);
        ProjCodeStore info = baseMapper.selectOne(queryWrapper);
        return info.getTypeSeqId();
    }

    /**
     * 记录
     * @param bizType
     * @param seq
     * @param projCode
     */
//    @Transactional(rollbackFor = Exception.class)
    public void add(String bizType, Long seq, String projCode) {
        ProjCodeStore projCodeStore = new ProjCodeStore();
        projCodeStore.setBizType(bizType);
        projCodeStore.setTypeSeqId(seq);
        projCodeStore.setProjCode(projCode);
        baseMapper.insert(projCodeStore);
    }

}
