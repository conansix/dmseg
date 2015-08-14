package xyz.dowenliu.npl.dmseg.dict;

import java.util.Map;

/**
 * 树叉类型
 * create at 15-4-30
 *
 * @author liufl
 * @since 1.0.0
 */
public interface Branching<K, T> {
    Map<K, T> getBranches();
}
