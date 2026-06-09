package cn.zswltech.mithras.projlifecycle.application;

import java.util.List;

public interface ProjLifecycleAuthPort {

    Long currentUserId();

    List<Long> canViewDeptIds();
}
