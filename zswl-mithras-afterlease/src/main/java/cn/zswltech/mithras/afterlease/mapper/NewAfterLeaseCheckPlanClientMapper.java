package cn.zswltech.mithras.afterlease.mapper;


import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListRSP;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
public interface NewAfterLeaseCheckPlanClientMapper extends CustomBaseMapper<NewAfterLeaseCheckPlanClient> {

    Page<AfterLeaseCheckLedgerListRSP> queryAfterLeaseCheckPlanLedgerList(Page<AfterLeaseCheckLedgerListRSP> page, @Param("dto") AfterLeaseCheckLedgerListREQ request);

    List<NewAfterLeaseCheckPlanClient> query2026ApprovePassPlanClient();



}
