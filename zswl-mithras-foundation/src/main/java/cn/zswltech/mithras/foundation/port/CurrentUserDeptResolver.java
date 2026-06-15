package cn.zswltech.mithras.foundation.port;

public interface CurrentUserDeptResolver {

    boolean currentUserIsSpecificDept(String... deptCodes);
}
