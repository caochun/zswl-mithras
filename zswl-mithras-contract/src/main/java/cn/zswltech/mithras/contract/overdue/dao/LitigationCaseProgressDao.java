package cn.zswltech.mithras.contract.overdue.dao;

import cn.zswltech.mithras.contract.overdue.mapper.LitigationCaseProgressMapper;
import cn.zswltech.mithras.contract.overdue.mapper.model.LitigationCaseProgress;
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