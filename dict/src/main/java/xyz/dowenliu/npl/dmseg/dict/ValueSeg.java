package xyz.dowenliu.npl.dmseg.dict;

import java.util.Set;

/**
 * 有值片段
 *
 * @author liufl
 * @since 1.0.0
 */
public interface ValueSeg {
    /**
     * 值
     *
     * @return 值
     */
    String getValue();

    /**
     * 词性标注
     *
     * @return 词性
     */
    Set<String> getSpeeches();
}
