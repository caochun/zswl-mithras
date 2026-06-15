package cn.zswltech.mithras.associationreport.service.job;

import java.util.List;

/**
 * 金融局报送任务生成待办时依赖的流程准备记录端口。
 */
public interface AssociationReportProcessPrepareService {

    boolean saveTodo(AssociationReportTodoType todoType, Long applyId, String formName, String currentNode, List<Long> assigneeIds);

    boolean hasPendingTodo(AssociationReportTodoType todoType);
}
