package cn.zswltech.mithras.contract.overdue.application.collection;

import java.io.InputStream;

public interface CollectionLetterMaterialStore {

    void removeGeneratedCollectionLetters(Long actionId);

    void addGeneratedCollectionLetter(InputStream inputStream, String fileName, Long actionId);
}
