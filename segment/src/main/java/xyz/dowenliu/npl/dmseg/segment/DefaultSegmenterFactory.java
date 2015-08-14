package xyz.dowenliu.npl.dmseg.segment;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.dowenliu.npl.dmseg.core.ReaderTokenizer;
import xyz.dowenliu.npl.dmseg.core.Segmenter;
import xyz.dowenliu.npl.dmseg.core.Token;
import xyz.dowenliu.npl.dmseg.core.tokenizer.DefaultIndexReaderTokenizer;
import xyz.dowenliu.npl.dmseg.core.tokenizer.DefaultQueryReaderTokenizer;
import xyz.dowenliu.npl.dmseg.dict.Dictionary;
import xyz.dowenliu.npl.dmseg.dict.loader.AbstractDictionaryLoader;
import xyz.dowenliu.npl.dmseg.dict.loader.RamHashedDictionaryLoader;
import xyz.dowenliu.npl.dmseg.util.ResourceReaderFactory;

import java.io.*;
import java.util.*;

/**
 * 默认分词器工厂实现
 *
 * @author liufl
 * @since 1.0.0
 */
public class DefaultSegmenterFactory implements SegmenterFactory {
    Logger logger = LoggerFactory.getLogger(getClass());
    private List<String> dictSources = new ArrayList<>();
    private AbstractDictionaryLoader loader = new RamHashedDictionaryLoader();
    private ResourceReaderFactory resourceReaderFactory;

    /**
     * @param dicts 字典文件。
     */
    public DefaultSegmenterFactory(ResourceReaderFactory resourceReaderFactory, String... dicts) {
        this.resourceReaderFactory = Validate.notNull(resourceReaderFactory);
        if (dicts != null) {
            Collections.addAll(this.dictSources, dicts);
        }
    }

    /**
     * 没有字典文件。用于后期手动增加字典的情况。可以用来实现懒加载。
     */
    public DefaultSegmenterFactory(ResourceReaderFactory resourceReaderFactory) {
        this(resourceReaderFactory, new String[0]);
    }

    /**
     * 方便使用DI，预留getter方法
     */
    public List<String> getDictSources() {
        return dictSources;
    }


    /**
     * 方便使用DI，预留setter方法
     */
    public void setDictSources(List<String> dictSources) {
        this.dictSources = dictSources;
    }

    @Override
    public Segmenter create() {
        DefaultSegmenter segmenter = new DefaultSegmenter();
        for (String dictSource : this.dictSources) {
            Dictionary dictionary;
            try {
                Reader resourceReader = this.resourceReaderFactory.createReader(dictSource);
                dictionary = this.loader.readIn(resourceReader);
            } catch (IOException e) {
                logger.warn("加载字典资源失败：" + dictSource, e);
                dictionary = null;
            }
            if (dictionary != null) {
                segmenter.appendDictionary(dictionary);
            }
        }
        return segmenter;
    }

    /**
     * 基于Hash 的默认实现
     */
    public class DefaultSegmenter implements Segmenter {
        List<Dictionary> dictionaries = new ArrayList<>();

        @Override
        public Iterator<Token> indexTokens(Reader reader) throws IOException {
            DefaultIndexReaderTokenizer indexReaderTokenizer = new DefaultIndexReaderTokenizer(reader, 128);
            this.dictionaries.forEach(indexReaderTokenizer::appendDictionary);
            indexReaderTokenizer.init();
            return new Itr(indexReaderTokenizer);
        }

        @Override
        public Iterator<Token> queryTokens(Reader reader) throws IOException {
            DefaultQueryReaderTokenizer queryReaderTokenizer = new DefaultQueryReaderTokenizer(reader, 128);
            this.dictionaries.forEach(queryReaderTokenizer::appendDictionary);
            queryReaderTokenizer.init();
            return new Itr(queryReaderTokenizer);
        }

        @Override
        public void appendDictionary(Dictionary dictionary) {
            this.dictionaries.add(dictionary);
        }

        /**
         * ReaderTokenizer的迭代器封装
         *
         * @author liufl
         * @since 1.0.0
         */
        class Itr implements Iterator<Token> {

            private final ReaderTokenizer readerTokenizer;
            private Token next;

            Itr(ReaderTokenizer readerTokenizer) {
                this.readerTokenizer = readerTokenizer;
            }

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                if (this.readerTokenizer.hasNextToken()) {
                    try {
                        next = this.readerTokenizer.nextToken();
                    } catch (IOException e) {
                        logger.warn("切词失败", e);
                        next = null;
                    }
                }
                return next != null;
            }

            @Override
            public Token next() {
                if (this.hasNext()) {
                    Token _next = next;
                    next = null;
                    return _next;
                }
                throw new NoSuchElementException();
            }
        }
    }
}
