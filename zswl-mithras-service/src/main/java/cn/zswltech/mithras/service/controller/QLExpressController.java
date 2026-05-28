package cn.zswltech.mithras.service.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.QLExpressApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.QLExpressREQ;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
@Slf4j
@RestController
public class QLExpressController implements QLExpressApi {
    @Resource
    private ExpressRunner expressRunner;

    @Override
    public R<String> execute(@Valid QLExpressREQ req) {
        DefaultContext<String, Object> context = new DefaultContext<>();
        if (CollectionUtil.isNotEmpty(req.getDataList())) {
            for (QLExpressREQ.Data data : req.getDataList()) {
                context.put(data.getKey(), data.getValue());
            }
        }
        if (Objects.nonNull(req.getIsExcelFormula()) && req.getIsExcelFormula()) {
            req.setExpress(req.getExpress().replaceAll("if", "excel_if").replaceAll("and", "excel_and").replaceAll("or", "excel_or"));
        }
        try {
            Object r = expressRunner.execute(req.getExpress(), context, null, true, false);
            return R.ok(r.toString());
        } catch (Exception e) {
            log.error("执行表达式发生异常[{}]", JSONUtil.toJsonStr(req), e);
            return R.fail(String.format("执行发生未知异常[%s]", e.getMessage()));
        }
    }
}
