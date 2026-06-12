package cn.zswltech.mithras.projectprocess.application.support;

import java.util.Collection;
import java.util.Map;

public interface ProjectProcessDictionaryPort {

    Map<String, String> addressCode2Display(Collection<String> addressCodes);
}
