package xyz.dowenliu.npl.dmseg.dict.loader;

import xyz.dowenliu.npl.dmseg.dict.Word;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * 字典词记录集简单按行读取器。每行一个词。不包含词性信息。行中按空白切分，第一个非空部分认为是有效词记录，其他部分被抛弃。
 *
 * @author liufl
 * @since 1.0.0
 */
public class SimpleLineWordRecordReader implements WordRecordReader {
    private String preRead = null;
    private final LineNumberReader reader;

    public SimpleLineWordRecordReader(Reader reader) {
        this.reader = new LineNumberReader(reader);
    }

    @Override
    public boolean hasNextWord() {
        preRead();
        return this.preRead != null && !"".equals(this.preRead);
    }

    private void preRead() {
        String line;
        try {
            line = this.reader.readLine();
        } catch (IOException e) {
            this.preRead = null;
            return;
        }
        if (line == null) {
            this.preRead = null;
            return;
        }
        line = line.trim();
        if ("".equals(line)) {
            this.preRead = null;
            return;
        }
        line = line.split("\\s+")[0];
        this.preRead = line;
    }

    @Override
    public Word nextWord() throws NoSuchElementException {
        Word word = new Word(this.preRead);
        this.preRead = null;
        return word;
    }
}
