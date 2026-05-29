package cn.zswltech.mithras.common.plugin;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

public class CustomSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
      	// 父类的list已经包含了BaseMapper的基础方法。
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        // 添加我们需要增加的自定义方法。
        methodList.add(new InsertListWithId());
        methodList.add(new UpdateAnnotationIncludeNullById());
        return methodList;
    }
}