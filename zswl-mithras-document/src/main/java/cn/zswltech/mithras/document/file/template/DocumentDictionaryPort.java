package cn.zswltech.mithras.document.file.template;

import java.util.List;

public interface DocumentDictionaryPort {

    List<String> listCodesByDictKey(String dictKey);

    boolean existsByCode(String code);

    void add(String dictKey, String dictDesc, String code, String display, Integer sort);

    void deleteByDictKeyAndCode(String dictKey, String code);
}
