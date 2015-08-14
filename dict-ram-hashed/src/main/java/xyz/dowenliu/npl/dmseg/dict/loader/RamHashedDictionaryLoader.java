package xyz.dowenliu.npl.dmseg.dict.loader;

import xyz.dowenliu.npl.dmseg.dict.RamHashedDictionary;

import java.io.Reader;

/**
 * @author liufl
 * @since 1.0.0
 */
public class RamHashedDictionaryLoader extends AbstractDictionaryLoader<RamHashedDictionary> {
    @Override
    public WordRecordReader wrapReader(Reader reader) {
        return new SimpleLineWordRecordReader(reader);
    }

    @Override
    public RamHashedDictionary readIn(Reader reader) {
        RamHashedDictionary dictionary = new RamHashedDictionary();
        this.apply(dictionary, reader);
        return dictionary;
    }
}
