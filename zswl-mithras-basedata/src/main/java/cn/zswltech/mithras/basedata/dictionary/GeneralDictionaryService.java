package cn.zswltech.mithras.basedata.dictionary;

import cn.zswltech.mithras.basedata.mapper.model.GeneralDictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author yangxiong
 * @since 2023-08-14
 */
public interface GeneralDictionaryService extends IService<GeneralDictionary> {

    /**
     *   根据原有名字返回正确文本
     * @return List<String>正确的名字
     */
    List<String> getNameList();
}
