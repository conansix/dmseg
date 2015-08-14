package xyz.dowenliu.npl.dmseg.dict;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 词路径。不论何种语言，词由字组成。字的排列状态构成了词路径。
 * 在大量词路径的集合中显然存在大量的分叉，每一个分叉是一个字。路径的结尾总是一个词的结尾。一个词的路径中可能包含的一个或多个词的路径。
 * 在词路径的分叉处标记了词结尾，以便从词路径集合中恢复一个词。
 * <p>语言中所有词的词路径集合将会发生大量的前部重叠现象，这种情况称之为前缀现象。
 * 显然有许多词成为了其他词的前缀。一个词的与它本身仅相差末尾一个字的前缀称之为紧邻前缀，
 * 如 a 是 an 的紧邻前缀， an 又是 and 的紧邻前缀。
 * 有些词在语言中不存在紧邻前缀词，如： world， 其紧邻前缀的部分为 worl ，是一个无意义的字串。
 * 定义词的紧邻前缀的词路径（不论它是不是一个词）是其词路径的父路径。</p>
 *
 * @author liufl
 * @since 1.0.0
 */
public abstract class WordPath<T extends WordPath> implements Branching<Character, T>, Serializable {
    protected final WordPath parentPath;
    protected Word word = null;
    protected final char fork;

    public WordPath(WordPath parentPath, char fork) {
        this.parentPath = parentPath;
        this.fork = fork;
    }

    /**
     * 获取此词路径的父路径
     *
     * @return 返回其父词路径。没有则返回 {@code null}
     */
    public WordPath getParentPath() {
        return parentPath;
    }

    /**
     * 判断当前路径是否代表一个词的边界
     *
     * @return 是 {@code trie}，否 {@code false}
     */
    public boolean isFinishWord() {
        return this.word != null;
    }

    /**
     * 标记当前路径代表一个词的边界
     *
     * @param speeches 词性列表
     */
    public void wordFinish(String... speeches) {
        this.word = new Word(this.toString(), speeches);
        this.word.setPath(this);
    }

    /**
     * 取消词边界标记
     */
    public void wordUnFinish() {
        this.word = null;
    }

    public Word getWord() {
        return word;
    }

    /**
     * 当前路径的分叉值。
     *
     * @return 分叉值
     */
    public char getFork() {
        return fork;
    }

    @Override
    public String toString() {
        StringBuilder rePath = new StringBuilder();
        rePath.append(this.fork);
        WordPath parent = this.getParentPath();
        while (parent != null) {
            rePath.append(parent.getFork());
            parent = parent.getParentPath();
        }
        return rePath.reverse().toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof WordPath && this.toString().equals(obj.toString());
    }

    public Deque<WordPath> getPathQueue() {
        Deque<WordPath> queue = new LinkedList<>();
        for (WordPath path = this; path != null; path = path.getParentPath()) {
            queue.addFirst(path);
        }
        return queue;
    }
}
