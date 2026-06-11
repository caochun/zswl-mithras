package cn.zswltech.mithras.foundation.persistence.plugin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 批量插入 并且有id
 *
 * @author wangchuanhao
 * @date 2022/6/22 9:57 PM
 */
public interface CustomBaseMapper<T> extends BaseMapper<T> {

    void insertListWithId(List<T> list);

    int updateAnnotationIncludeNullById(T t);

}
