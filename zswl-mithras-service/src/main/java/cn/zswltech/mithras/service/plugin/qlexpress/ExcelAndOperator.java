package cn.zswltech.mithras.service.plugin.qlexpress;

import com.ql.util.express.Operator;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
public class ExcelAndOperator extends Operator {
    @Override
    public Object executeInner(Object[] list) throws Exception {
        for (Object o : list) {
            Boolean b = (Boolean) o;
            if (!b) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
