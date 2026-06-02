package cn.zswltech.mithras.contract.overdue.infrastructure.dao;

import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.LitigationDefendantMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.LitigationDefendant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @description 诉讼等级被告信息
* @author zhaozhengkang
* @date 2024-10-30
*/
@Service
public class LitigationDefendantDao extends ServiceImpl<LitigationDefendantMapper, LitigationDefendant> {
}