package cn.zswltech.mithras.contract.overdue.infrastructure.dao;

import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.LitigationCaseProgressMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.LitigationCaseProgress;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @description 诉讼登记案件进展
* @author zhaozhengkang
* @date 2024-10-30
*/
@Service
public class LitigationCaseProgressDao extends ServiceImpl<LitigationCaseProgressMapper, LitigationCaseProgress> {
}