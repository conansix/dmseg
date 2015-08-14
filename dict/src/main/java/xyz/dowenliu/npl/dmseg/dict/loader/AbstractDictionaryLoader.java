package xyz.dowenliu.npl.dmseg.dict.loader;

import xyz.dowenliu.npl.dmseg.dict.Dictionary;
import xyz.dowenliu.npl.dmseg.dict.Word;

import java.io.Reader;

/**
 * 字典加载器
 *
 * @author liufl
 * @since 1.0.0
 */
public abstract class AbstractDictionaryLoader<T extends Dictionary> {
    /**
     * 将一个普通Reader对象包装成一个WordRecordReader对象
     *
     * @param reader 源
     * @return 包装后的WordRecordReader对象
     */
    public abstract WordRecordReader wrapReader(Reader reader);

    /**
     * 使用指定源读入一个新字典对象
     *
     * @param reader 源
     * @return 不会返回 {@code null} ，如果源无有效输入，返回一个空的字典对象
     */
    public abstract T readIn(Reader reader);

    /**
     * 使用指定输入源作为补丁补充到字典中
     *
     * @param dictionary 已有的字典对象
     * @param reader     源
     */
    public void apply(T dictionary, Reader reader) {
        WordRecordReader wordRecordReader = this.wrapReader(reader);
        while (wordRecordReader.hasNextWord()) {
            Word nextWord = wordRecordReader.nextWord();
            dictionary.add(nextWord);
        }
    }
}
