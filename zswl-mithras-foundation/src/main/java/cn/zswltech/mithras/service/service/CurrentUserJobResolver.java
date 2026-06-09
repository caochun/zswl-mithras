package cn.zswltech.mithras.service.service;

import java.util.List;

public interface CurrentUserJobResolver {

    List<String> queryUserJobList(Long userId);

    boolean currentUserIsSpecificJob(String... jobNames);
}
