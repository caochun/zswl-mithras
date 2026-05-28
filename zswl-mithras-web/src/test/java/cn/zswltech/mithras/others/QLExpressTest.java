package cn.zswltech.mithras.others;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.util.StringUtil;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
public class QLExpressTest extends ApplicationTest {
    @Resource
    private ExpressRunner expressRunner;

    @Test
    public void simpleTest() throws Exception {
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("P", 70);
        Object r = expressRunner.execute("(P-60)/30*0.35+0.6", context, null, true, false);
        System.out.println(r);
    }

    @Test
    public void maxTest() throws Exception {
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("P", 200);
        Object r = expressRunner.execute("max(1.2,(P-100)/100+1)", context, null, true, false);
        System.out.println(r);
    }

    @Test
    public void minTest() throws Exception {
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("P", 200);
        Object r = expressRunner.execute("min(1.2,(P-100)/100+1)", context, null, true, false);
        System.out.println(r);
    }

    @Test
    public void excelIfTest() throws Exception {
        String express = "if(T<=3,3,if(and(3<T,T<=5),'[1.5,3)','[2,3)'))";
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("T", 4);
        String formatExpress = StringUtil.formatExcelFormula(express);
        System.out.println(formatExpress);
        Object r = expressRunner.execute(formatExpress, context, null, true, false);
        System.out.println(r);
    }
}
