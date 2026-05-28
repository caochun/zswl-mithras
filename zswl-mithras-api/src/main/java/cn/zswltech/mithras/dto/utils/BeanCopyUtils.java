package cn.zswltech.mithras.dto.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtils {

    /**
     * 根据sourceList中元素的值，复制生成含有targetElemClass的List
     * @param sourceList 数据源List
     * @param targetElemClass 新的List元素的Class
     * @return
     * @throws Exception
     */
    public static <S, T> List<T> generatorList(List<S> sourceList, Class<T> targetElemClass){
        if(sourceList == null){
            return new ArrayList<T>() ;
        }
        List<T> target = new ArrayList<T>();
        sourceList.forEach(s ->{
            T t = generatorObject(s, targetElemClass);
            target.add(t);
        });
        return target;
    }
    /**
     * 同 generatorList
     * @param sourceArray
     * @param targetElemClass
     * @param <S>
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <S, T> T[] generatorArray(S[] sourceArray, Class<T> targetElemClass) {
        T[] targetArray = (T[]) Array.newInstance(targetElemClass, sourceArray.length);
        for(int i = 0; i < sourceArray.length; ++i){
            T t = generatorObject(sourceArray[i], targetElemClass);
            targetArray[i] = t;
        }
        return targetArray;
    }

    /**
     * 根据source中的属性值，生成targetClass对应的Object
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T generatorObject(Object source, Class<T> targetClass){
        if(source == null){
            return null;
        }
        T t = null;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(source, t);
        return t;
    }
}
