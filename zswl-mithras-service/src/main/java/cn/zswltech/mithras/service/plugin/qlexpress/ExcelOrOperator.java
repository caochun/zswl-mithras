package cn.zswltech.mithras.service.plugin.qlexpress;

import com.ql.util.express.Operator;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
public class ExcelOrOperator extends Operator {
    @Override
    public Object executeInner(Object[] list) {
        for (Object o : list) {
            Boolean b = (Boolean) o;
            if (b) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
