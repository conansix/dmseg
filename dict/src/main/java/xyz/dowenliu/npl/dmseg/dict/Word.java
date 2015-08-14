package xyz.dowenliu.npl.dmseg.dict;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 词，被字典编纂的元结构
 *
 * @author liufl
 * @since 1.0.0
 */
public class Word implements Serializable, ValueSeg {
    protected final String value;
    /**
     * 词性。此处的词性标注与语言无关，只是为了方便应用逻辑进行词的后续处理，提供一套标记系统。
     * 不使用词性标记不会影响词的匹配与字典的编纂。
     */
    protected Set<String> speeches = Collections.synchronizedSet(new HashSet<>(2, 0.75F));
    protected WordPath path = null;

    /**
     * 构造一个词
     *
     * @param value 词的字面值
     */
    public Word(String value) {
        if (value == null || "".equals(value.trim())) {
            throw new IllegalArgumentException("无法创建一个空词");
        }
        this.value = value;
    }

    /**
     * 构造一个词
     *
     * @param value    词的字面值
     * @param speeches 词性。词性标注与语言无关，只是为了方便应用逻辑进行词的后续处理，提供一套标记系统。
     */
    public Word(String value, String... speeches) {
        if (value == null || "".equals(value.trim())) {
            throw new IllegalArgumentException("无法创建一个空词");
        }
        this.value = value;
        Collections.addAll(this.speeches, speeches);
    }

    /**
     * 获取词的值
     *
     * @return 词值
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * 获取词性集合。
     *
     * @return 词性集合的引用。操作此集合会直接操作词的词性。可能发生线程安全问题。
     */
    @Override
    public Set<String> getSpeeches() {
        return speeches;
    }

    /**
     * 获取词路径
     *
     * @return 词路径
     */
    public WordPath getPath() {
        return path;
    }

    /**
     * 设置词路径
     *
     * @param path 词路径
     */
    public void setPath(WordPath path) {
        this.path = path;
    }
}
