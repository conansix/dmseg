package xyz.dowenliu.npl.dmseg.segment;

import xyz.dowenliu.npl.dmseg.core.Segmenter;

/**
 * 分词器工厂接口
 *
 * @author liufl
 * @since 1.0.0
 */
public interface SegmenterFactory {
    /**
     * 生产一个全新的分词器
     *
     * @return 一个分词器对象
     */
    Segmenter create();
}
