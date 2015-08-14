package xyz.dowenliu.npl.dmseg.util;

import java.io.IOException;
import java.io.Reader;

/**
 * 资源（字符型）Reader工厂接口声明
 * create at 15-4-29
 *
 * @author liufl
 * @since 1.0.0
 */
public interface ResourceReaderFactory {
    Reader createReader(String resource) throws IOException;
}
