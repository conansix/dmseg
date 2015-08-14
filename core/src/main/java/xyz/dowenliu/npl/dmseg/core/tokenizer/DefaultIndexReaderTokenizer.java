package xyz.dowenliu.npl.dmseg.core.tokenizer;

import org.apache.commons.lang3.math.NumberUtils;
import xyz.dowenliu.npl.dmseg.core.ReaderTokenizer;
import xyz.dowenliu.npl.dmseg.core.Token;

import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的索引用分词器。
 * <br/>切分规则如下：
 * <ul>
 * <li>全文标准切分</li>
 * <li>字典最细粒度切分</li>
 * <li>中文标准切分</li>
 * </ul>
 *
 * @author liufl
 * @since 1.0.0
 */
public class DefaultIndexReaderTokenizer extends ReaderTokenizer {
    /**
     * 创建一个分词提取器
     *
     * @param reader     输入流
     * @param bufferSize 缓冲区长度。
     *                   此长度会影响非字典分区匹配结果，使预想的结果被切分（如果预想结果长度超过缓冲区长度）。
     */
    public DefaultIndexReaderTokenizer(Reader reader, int bufferSize) {
        super(reader, bufferSize);
        this.matchers.add(new AlphaNumMatcher());
        this.matchers.add(new DecimalMatcher());
        this.matchers.add(new StandartCnMatcher());
        this.matchers.add(new UnknownMatcher());
    }

    @Override
    protected void filter() {
        List<Token> tokens = this.preTokens.stream().filter(t -> {
            switch (t.getType()) {
                case WORD:
                    return true;
                case ALPHANUM:
                    if (this.ctx.lastAlphaNumToken != null) {
                        if (this.ctx.lastAlphaNumToken.getEnd() >= t.getEnd()) {
                            // 已经被匹配了
                            return false;
                        }
                    }
                    this.ctx.lastAlphaNumToken = t;
                    if (NumberUtils.isNumber(t.getValue())) {
                        if (this.ctx.lastDecimalToken != null) {
                            if (this.ctx.lastDecimalToken.getEnd() >= t.getEnd()) {
                                // 已经被匹配了
                                return false;
                            }
                        }
                        this.ctx.lastDecimalToken = t;
                    }
                    return true;
                case DECIMAL:
                    if (this.ctx.lastDecimalToken != null) {
                        if (this.ctx.lastDecimalToken.getEnd() >= t.getEnd()) {
                            // 已经被匹配了
                            return false;
                        }
                    }
                    this.ctx.lastDecimalToken = t;
                    return true;
                case CN:
                    // 由CN matcher处理
                    return true;
                default:
                    return true;
            }
        }).collect(Collectors.toList());
        this.preTokens.clear();
        this.preTokens.addAll(tokens);
    }
}
