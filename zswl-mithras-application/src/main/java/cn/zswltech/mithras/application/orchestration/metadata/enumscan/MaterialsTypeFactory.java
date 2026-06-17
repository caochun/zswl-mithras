package cn.zswltech.mithras.application.orchestration.metadata.enumscan;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 文件类型转换
 *
 * @author wangchuanhao
 * @date 2023/1/4 4:20 PM
 */
public class MaterialsTypeFactory {

    private static final Map<String, Map<String, String>> map = new HashMap<>();

    static {
        try {
            String[] packages = new String[]{"cn.zswltech.mithras.application.orchestration.auth"};
            for (String aPackage : packages) {
                Set<Class> scan = ClassScanner.scan(aPackage);
                for (Class<?> aClass : scan) {
                    if(IMaterialsTypeConvert.class.isAssignableFrom(aClass)) {
                        Method method = aClass.getMethod("values");
                        IMaterialsTypeConvert[] invoke = (IMaterialsTypeConvert[]) method.invoke(null);
                        for (IMaterialsTypeConvert convert : invoke) {
                            Map<String, String> mtMap = map.computeIfAbsent(convert.businessModule(), k -> new HashMap<>());
                            mtMap.put(convert.name(), convert.display());
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String convert(String businessModule, String materialsType){
        return Optional.ofNullable(map.get(businessModule)).map(mtMap -> mtMap.get(materialsType)).orElse(materialsType);
    }

}
