package cn.zswltech.mithras.service.mapper.afterlease;


import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
