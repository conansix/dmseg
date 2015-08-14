package xyz.dowenliu.npl.dmseg.dict;

import java.nio.CharBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在内存使用Hash散列实现的快查字典结构。速度快但需要消耗较多内存。
 * 因使用Hash，有较明显的内存浪费。
 * <p>使用时应注意内存的使用状况，决定是否需要实现自定义字典实现，以解决大字典对存储空间的需求。</p>
 * <p>为减少内存浪费、平衡查询效率并减小初始化过程中内存溢出的可能性，内部使用的Hash节点使用容量8、加载因子0.8初始化，
 * 因此在此字典类实例构建时会频繁的进行rehash操作。要改变此行为可在字典对象初始化后未增加词前使用
 * {@link #setNodeInitialCapacity(int)}、{@link #setNodeLoadFactor(float)}方法修改这两参数。
 * <strong>小心使用这两个方法</strong></p>
 * <p>此类是线程不安全的，不要使用多线程进行字典内容修改操作。</p>
 *
 * @author liufl
 * @since 1.0.0
 */
public final class RamHashedDictionary implements Dictionary<RamHashedDictionary.HashWordPath> {
    private int nodeInitialCapacity = 8;
    private float nodeLoadFactor = 0.8F;
    private int size = 0;
    private String bookTag = "DM-SEG";
    private Map<Character, HashWordPath> branches = Collections.synchronizedMap(new HashMap<>());

    /**
     * 获取Hash节点初始化容量
     *
     * @return Hash节点初始化容量
     */
    public int getNodeInitialCapacity() {
        return nodeInitialCapacity;
    }

    /**
     * 设置Hash节点初始化容量
     * <strong>小心！初始化容量的微小增加也可能导致内存消耗指数性的增长！</strong>
     *
     * @param nodeInitialCapacity Hash节点初始化容量
     */
    public void setNodeInitialCapacity(int nodeInitialCapacity) {
        this.nodeInitialCapacity = nodeInitialCapacity;
    }

    /**
     * 获取Hash节点初始化加载因子
     *
     * @return Hash节点初始化加载因子
     */
    public float getNodeLoadFactor() {
        return nodeLoadFactor;
    }

    /**
     * 设置Hash节点初始化加载因子
     * <strong>较小的值会导致内存溢出的风险同时浪费大量的内存；较大的值会降低查询效率！</strong>
     *
     * @param nodeLoadFactor Hash节点初始化加载因子
     */
    public void setNodeLoadFactor(float nodeLoadFactor) {
        this.nodeLoadFactor = nodeLoadFactor;
    }

    /**
     * 设置字典书名标记
     *
     * @param bookTag 字典书名标记
     */
    public void setBookTag(String bookTag) {
        this.bookTag = bookTag;
    }

    @Override
    public int size() {
        return this.size;
    }

    private HashWordPath branchOf(Map<Character, HashWordPath> branches, char fork) {
        if (branches.isEmpty()) {
            return null;
        }
        return branches.get(fork);
    }

    @Override
    public boolean contains(String word) {
        char[] chars = word.toCharArray();
        HashWordPath path = null;
        Map<Character, HashWordPath> _branches = this.getBranches();
        for (char c : chars) {
            path = this.branchOf(_branches, c);
            if (path == null) {
                return false;
            }
            _branches = path.getBranches();
        }
        return path != null && path.isFinishWord();
    }

    @Override
    public boolean add(String word, String... speeches) {
        boolean create = false;
        char[] chars = word.toCharArray();
        if (chars.length == 0) {
            return true;
        }
        HashWordPath path = null;
        Map<Character, HashWordPath> _branches = this.branches;
        for (char c : chars) {
            HashWordPath expectPath = this.branchOf(_branches, c);
            if (expectPath == null) {
                expectPath = new HashWordPath(path, c);
                _branches.put(c, expectPath);
                create = true;
            }
            path = expectPath;
            _branches = path.getBranches();
        }
        assert path != null;
        if (!path.isFinishWord()) {
            create = true;
        }
        path.wordFinish(speeches);
        if (create) {
            this.size++;
        }
        return true;
    }

    @Override
    public boolean add(Word word) {
        String[] speeches = new String[word.getSpeeches().size()];
        speeches = word.getSpeeches().toArray(speeches);
        return this.add(word.getValue(), speeches);
    }

    @Override
    public boolean remove(String word) {
        char[] chars = word.toCharArray();
        HashWordPath path = null;
        Map<Character, HashWordPath> _branches = this.branches;
        for (char c : chars) {
            path = this.branchOf(_branches, c);
            if (path == null) {
                return false;
            }
            _branches = path.getBranches();
        }
        if (path == null) {
            return false;
        }
        path.wordUnFinish();
        while (!path.isFinishWord()) { // 不是结束位
            if (!path.branches.isEmpty()) { // 但有子路径
                // 不能删除路径
                break;
            }
            char fork = path.getFork();
            path = (HashWordPath) path.getParentPath();
            if (path != null) { // 不是最顶层
                path.branches.remove(fork); // 删除子路径
            } else {
                break; // 到root了
            }
        }
        size--;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<WordPath> withPrefix(WordPath path) {
        if (path == null) {
            return null;
        }
        Deque<WordPath> expectQueue = path.getPathQueue();
        HashWordPath matchPath = null;
        Map<Character, HashWordPath> _branches = this.branches;
        for (WordPath expectPath : expectQueue) {
            matchPath = this.branchOf(_branches, expectPath.getFork());
            if (matchPath == null) {
                return null;
            }
            _branches = matchPath.getBranches();
        }
        if (matchPath == null) {
            return null;
        }
        return matchPath.branches.values().stream().collect(Collectors.toSet());
    }

    @Override
    public List<Word> dictMatch(CharBuffer charBuffer) {
        List<Word> words = new LinkedList<>();
        Map<Character, HashWordPath> _branches = this.branches;
        HashWordPath path;
        while (charBuffer.remaining() > 0) {
            path = branchOf(_branches, charBuffer.get());
            if (path == null) {
                break;
            }
            if (path.isFinishWord()) {
                words.add(path.getWord());
            }
            _branches = path.getBranches();
        }
        return words;
    }

    @Override
    public Map<Character, HashWordPath> getBranches() {
        return this.branches;
    }

    @Override
    public Iterator<HashWordPath> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<HashWordPath> {
        LinkedList<Iterator<HashWordPath>> curStack = new LinkedList<>();
        Iterator<HashWordPath> cur;

        public Itr() {
            cur = RamHashedDictionary.this.branches.values().iterator();
        }

        @Override
        public boolean hasNext() {
            return cur.hasNext();
        }

        @Override
        public HashWordPath next() {
            HashWordPath path = cur.next();
            if (!path.branches.isEmpty()) {
                curStack.push(cur);
                cur = path.branches.values().iterator();
            } else {
                while (!cur.hasNext()) {
                    try {
                        cur = curStack.pop();
                    } catch (NoSuchElementException e) {
                        break;
                    }
                }
            }
            return path;
        }
    }

    @Override
    public String dictionaryBookTag() {
        return this.bookTag;
    }

    /**
     * Hash散列的词路径实现，用于内存存储表示
     */
    public class HashWordPath extends WordPath<HashWordPath> {
        Map<Character, HashWordPath> branches;

        public HashWordPath(WordPath parentPath, char fork) {
            super(parentPath, fork);
            this.branches = Collections
                    .synchronizedMap(new HashMap<>(nodeInitialCapacity, nodeLoadFactor));
        }


        @Override
        public Map<Character, HashWordPath> getBranches() {
            return this.branches;
        }
    }
}
