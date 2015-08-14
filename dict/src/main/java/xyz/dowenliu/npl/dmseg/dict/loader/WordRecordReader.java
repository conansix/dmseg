package xyz.dowenliu.npl.dmseg.dict.loader;

import xyz.dowenliu.npl.dmseg.dict.Word;

import java.util.NoSuchElementException;

/**
 * 字典词记录集读取器
 *
 * @author liufl
 * @since 1.0.0
 */
public interface WordRecordReader {
    /**
     * 是否存在下一词记录
     *
     * @return 是 {@code true} ，否 {@code false}
     */
    boolean hasNextWord();

    /**
     * 取下一词记录
     *
     * @return 下一词记录
     * @throws NoSuchElementException 不存在下一词记录
     */
    Word nextWord() throws NoSuchElementException;
}
