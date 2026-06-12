package cn.zswltech.mithras.application.orchestration.workflow.process.prepare.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataExchangeRate;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.persistence.model.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.basedata.service.BaseDataExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/9/25
 * @description
 */
@Slf4j
@Component
public class BaseDataExchangeRateCommitHandle extends AbstractFlowCommitHandle {
    // 该待办没有流程，自定义一个key，前端需要用于区分
    public static final String PROCESS_TYPE = "baseDataExchangeRateTodo";

    @Resource
    private BaseDataExchangeRateService baseDataExchangeRateService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, PROCESS_TYPE);
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        // 查询所有的草稿数据
        List<BaseDataExchangeRate> draftList = baseDataExchangeRateService.queryDraftByYearMonth(prepare.getApplyTime().getYear(), prepare.getApplyTime().getMonthValue());
        if (CollectionUtil.isNotEmpty(draftList)) {
            // 校验数据是否完整
            for (BaseDataExchangeRate exchangeRate : draftList) {
                if (Objects.isNull(exchangeRate.getExchangeRate()) || Objects.isNull(exchangeRate.getTargetDate())) {
                    throw new MithrasException("请补全汇率相关数据后再提交");
                }
                exchangeRate.setIsDraft(YesOrNoNumberEnum.NO.getCode());
            }
            // 生效草稿数据
            baseDataExchangeRateService.updateBatchById(draftList);
        }
        return "";
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        // 删除待办所属月份的草稿数据
        List<BaseDataExchangeRate> list = baseDataExchangeRateService.queryDraftByYearMonth(prepare.getApplyTime().getYear(), prepare.getApplyTime().getMonthValue());
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<Long> ids = list.stream().map(BaseDataExchangeRate::getId).collect(Collectors.toList());
        baseDataExchangeRateService.removeByIds(ids);
    }
}
