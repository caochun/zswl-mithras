package cn.zswltech.mithras.dashboard.application.todo;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardTodoApplicationService {

    List<DashboardTodoMyTodoProcessTaskRSP> todoPageList(DashboardTodoREQ req);

    PageR<DashboardTodoProcessRSP> myProcessApplyPageList(DashboardTodoREQ req);

    PageR<DashboardTodoProcessRSP> myProcessDoingPageList(DashboardTodoREQ req);

    PageR<DashboardTodoProcessRSP> myProcessFinishPageList(DashboardTodoREQ req);

    PageR<DashBoardProcessCcListRSP> myReceiveCcList(DashboardTodoREQ req);

    DashboardTodoCountRSP myTabCount();
}
