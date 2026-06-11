package cn.zswltech.mithras.contract.application.file;

@FunctionalInterface
public interface ContractGenerateAction<T> {

    void accept(T value) throws Throwable;
}
