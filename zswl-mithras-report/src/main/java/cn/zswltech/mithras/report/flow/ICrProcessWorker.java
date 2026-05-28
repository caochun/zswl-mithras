package cn.zswltech.mithras.report.flow;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.report.ReportListBaseREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.mapper.model.BatchRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程结束
 *
 * @author wangchuanhao
 * @date 2023/1/12 1:55 PM
 */
public interface ICrProcessWorker<T, Q extends ReportListBaseREQ> {

    /**
     * 1.先把编辑区的插入ProcSnap，如果审批成功，要记录批次号
     * 2.如果审批成功 把编辑区选择报送的数据抄到生效区
     * 3.如果审批成功 把生效区数据全量抄到FullSnap
     * 4.编辑区数据处理 清空流程id、清空审批状态、报送状态恢复
     *
     * @param procBusinessKey
     * @param endType
     * @param startUserId
     * @param processInstanceId
     */
    void processEnd(Long procBusinessKey, Integer endType, Long startUserId, String processInstanceId,
                    LocalDateTime reportTime, @Nullable BatchRecord procSnapRecord, @Nullable BatchRecord fullSnapRecord);

    /**
     * 更新编辑区数据状态
     *
     * @param procBusinessKey
     * @param batchNo
     */
    void submit(Long procBusinessKey, String batchNo);

    /**
     * 所属模块
     *
     * @return
     */
    ReportPageEnum reportPageEnum();

    /**
     * 搜集在流程中的主键列表
     *
     * @return
     */
    int countInProcessData();

    IService<T> getServiceInstance();

    /**
     * 导出专用
     */
    PageR<Map<String, DiffValue>> list(Q req);

    /**
     * 转换器
     *
     * @param value     字段值
     * @param fieldName 字段名
     * @return 字段描述
     */
    String converter(String fieldName, DiffValue value);
}
