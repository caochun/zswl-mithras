package cn.zswltech.mithras.service.plugin.qlexpress;

import com.ql.util.express.Operator;

/**
 * @author dingqi
 * @date 2023/2/15
 * @description
 */
public class ExcelIfOperator extends Operator {
    @Override
    public Object executeInner(Object[] list) {
        Boolean o1 = (Boolean) list[0];
        Object o2 = list[1];
        Object o3 = list[2];
        return o1 ? o2 : o3;
    }
}
