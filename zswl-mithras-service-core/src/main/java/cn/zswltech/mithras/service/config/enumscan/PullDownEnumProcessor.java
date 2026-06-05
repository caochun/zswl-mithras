package cn.zswltech.mithras.service.config.enumscan;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.SelectRSP;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 17:33
 */
public class PullDownEnumProcessor {

    private static final Map<String, List<SelectRSP>> pullDown = new HashMap<>(128);

    @SneakyThrows
    public static Map<String, List<SelectRSP>> getPullDown(){
        if(ObjectUtil.isEmpty(pullDown)){
            enumToListMap("cn.zswltech.mithras.service.enums", "cn.zswltech.mithras.report.enums","cn.zswltech.mithras.factory.enums", "cn.zswltech.mithras.blackgray.enums");
        }
        return pullDown;
    }


    private static void enumToListMap(String... packages) throws Throwable {
        for (String aPackage : packages) {
            Set<Class> scan = ClassScanner.scan(aPackage);
            for (Class<?> aClass : scan) {
                if(PullDown.class.isAssignableFrom(aClass)) {
                    Method method = aClass.getMethod("values");
                    PullDown[] invoke = (PullDown[]) method.invoke(null);
                    List<SelectRSP> oneEnum = new ArrayList<>();
                    for (PullDown pullDown : invoke) {
                        SelectRSP oneValue = new SelectRSP();
                        oneValue.setLabel(pullDown.display());
                        oneValue.setValue(pullDown.valueKey());
                        oneValue.setChildSelectName(pullDown.childSelectName());
                        oneValue.setLevel(pullDown.level());
                        oneValue.setState(pullDown.state());
                        oneEnum.add(oneValue);
                    }
                    PullDownExt pullDownExt = aClass.getAnnotation(PullDownExt.class);
                    if (Objects.nonNull(pullDownExt)) {
                        pullDown.put(pullDownExt.value(), oneEnum);
                    } else {
                        pullDown.put(classNameToValueName(aClass.getSimpleName()), oneEnum);
                    }
                }
            }
        }
    }
    private static String classNameToValueName(String className){
        char[] c = className.toCharArray();
        c[0] = (char) (c[0]+(32));
        return new String(c);
    }
}