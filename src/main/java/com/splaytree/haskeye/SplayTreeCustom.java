package com.splaytree.haskeye;

import com.sun.org.apache.xpath.internal.objects.XNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

//AbstractMap implement
public class SplayTreeCustom<T extends Comparable<T>> extends AbstractSet<T> {
    //Stack for iterator
    ArrayDeque<Node<T>> stack = new ArrayDeque<>();

    public static class Node<T> {
        public Node(T value) {
            this.value = value;
            this.key = value.hashCode();
        }

        T value = null;

        int key = 0;

        Node<T> parent = null;

        Node<T> left = null;

        Node<T> right = null;
    }

    public Node<T> root = null;

    public int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    @Override
    public Iterator iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {

    }

    @Override
    public Object[] toArray() {
        return stack.toArray();
    }

    @Override
    public boolean add(T t) {
        // Create a new Node and initialize it
        Node<T> newNode = new Node<T>(t);
        newNode.key = t.hashCode();
        if (root == null) {
            root = newNode;
            size++;
            stack.add(newNode);
            return true;
        } else {
            if (!this.contains(t)) {
                addRecursive(root, t);
                size++;
                stack.add(newNode);
                return true;
            }
        }
        return false;
    }

    private Node<T> addRecursive(Node<T> current, T value) {
        if (current == null) {
            return new Node(value);
        }

        if (value.hashCode() < current.key) {
            current.left = addRecursive(current.left, value);
        } else if (value.hashCode() > current.key) {
            current.right = addRecursive(current.right, value);
        } else {
            // value already exists
            return current;
        }
        return current;
    }


    @Override
    public boolean containsAll(Collection c) {
        boolean b = true;
        for (T t : (Iterable<T>) c) {
            if (!this.contains(t)) b = false;
        }
        return b;
    }

    @Override
    public boolean addAll(Collection c) {
        boolean b = true;
        for (T t : (Iterable<T>) c) {
            b = this.add(t);
            if (!b) break;
        }
        return b;
    }


    @Override
    public void clear() {
        stack.clear();
        root = null;
        size = 0;
    }


    public int height() {
        return height(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    //Переделан как Вы сказали, по примерам с geeksforgeeks

    public boolean remove(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) throw new NullPointerException("Дерево пусто");

        if (size <= 1) {
            clear();
            return true;
        }

        find((T) o);
        if (root.value == o) {
            if (root.left == null) {
                root.right.parent = null;
                root = root.right;
                size -= 1;
                return true;
            } else {
                //Разделение на два поддерева пуиём разрыва связи левого потомка root ним и splay
                //исключительно внутри левого поддерева максимального его эл-та, что приводит к созданию поддерева
                //для присоединения к нему правого поддерева с удалением связей со старым root из оного
                T newRoot = findMaxFrom(root.left);
                Node<T> right = root.right;
                root.left.parent = null;
                find(newRoot);
                right.parent = root;
                root.right = right;
                size -= 1;
                return true;
            }
        } else return false;
    }

    public T findMaxFrom(Node<T> node) {
        int maxKey;
        T max = findRecursiveMax(node.right, node.value);
        return (max);
    }

    private T findRecursiveMax(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        if (node.key > value.hashCode()) value = node.value;
        if (node.right == null) return value;
        else
            return findRecursiveMax(node.right, value);
    }


    public boolean contains(Object o) {
        return containsRecursive(root, o.hashCode());
    }

    private boolean containsRecursive(Node current, int key) {
        if (current == null) {
            return false;
        }
        if (key == current.key) {
            return true;
        }
        return key < current.key
                ? containsRecursive(current.left, key)
                : containsRecursive(current.right, key);
    }

    //Finding node with empty left branch
    private Node<T> findEmptyLeft() {
        if (root == null) throw new NoSuchElementException();
        return findEmptyLeft(root);
    }

    private Node<T> findEmptyLeft(Node<T> start) {
        if (start.left == null) {
            return start;
        } else {
            Node<T> i = findEmptyLeft(start.left);
            Node<T> j = findEmptyLeft(start.right);
            if (height(i) <= height(j))
                return i;
            else return j;
        }
    }

    //Finding node with empty right branch
    private Node<T> findEmptyRight() {
        if (root == null) throw new NoSuchElementException();
        return findEmptyRight(root);
    }

    private Node<T> findEmptyRight(Node<T> start) {
        if (start.right == null) {
            return start;
        } else {
            Node<T> i = findEmptyLeft(start.left);
            Node<T> j = findEmptyLeft(start.right);
            if (height(i) <= height(j))
                return i;
            else return j;
        }
    }

    //Finding node to be removed
    private Node<T> findDel(T value) {
        if (root == null) return null;
        return findDel(root, value);
    }

    private Node<T> findDel(Node<T> start, T value) {
        int comparisonLeft = value.compareTo(start.left.value);
        int comparisonRight = value.compareTo(start.left.value);
        if (start.left != null)
            if (comparisonLeft == 0) {
                return start;
            } else return findDel(start.left, value);
        if (start.right != null)
            if (comparisonRight == 0) {
                return start;
            } else return findDel(start.right, value);
        throw new NoSuchElementException();
    }

    public Node<T> find(T value) {
        Node<T> splayNode = findRecursive(root, value);
        splay(splayNode);
        root = splayNode;
        return (root);
    }

    private Node<T> findRecursive(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        if (node.left == null && node.right == null)
            return node;
        if (value.hashCode() < node.key) {
            return findRecursive(node.left, value);
        } else if (value.hashCode() > node.key) {
            return findRecursive(node.right, value);
        } else {
            return node;
        }
    }


    //Splay block next

    /**
     * Splay реалтзован поворотами при проходе от найденной вершины вверх вплоть до root
     * И последующей заменой, соответсвенно его на данную вершину
     * В реализации осуществляется проход по пути от корня к целевой вершине и/или обратно.
     * А значит по  Лемме, путь состоит из O(log(n)) вершин.
     * Обработка каждой вершины имеет сложность O(1).
     * Таким образом, сложность приведенных выше операции splay — O(log(n))
     */
    Node<T> grandParent(Node<T> node) {
        return node.parent.parent;
    }

    private void rotateLeft(Node<T> node) {
        Node<T> parent = node.parent;
        Node<T> rightOne = node.right;
        if (parent != null)
            if (parent.left == node)
                parent.left = rightOne;
            else
                parent.right = rightOne;
        Node<T> tmp = rightOne.left;
        rightOne.left = node;
        node.right = tmp;
        node.parent = rightOne;
        rightOne.parent = parent;
        if (node.right != null)
            node.parent = node;
    }

    private void rotateRight(Node<T> node) {
        Node<T> parent = node.parent;
        Node<T> leftOne = node.left;
        if (parent != null)
            if (parent.right == node)
                parent.right = leftOne;
            else
                parent.left = leftOne;
        Node<T> tmp = leftOne.right;
        leftOne.right = node;
        node.left = tmp;
        node.parent = leftOne;
        leftOne.parent = parent;
        if (node.left != null)
            node.parent = node;
    }

    private void splay(Node<T> node) {
        if (size > 1) {
            while (node.parent != null)
                if (node == node.parent.left) {
                    if (grandParent(node) == null)
                        rotateRight(node.parent);
                    else if (node.parent == grandParent(node).left) {
                        rotateRight(grandParent(node));
                        rotateRight(node.parent);
                    } else {
                        rotateRight(node.parent);
                        rotateLeft(node.parent);
                    }
                } else {
                    if (grandParent(node) == null)
                        rotateLeft(node.parent);
                    else if (node.parent == grandParent(node).right) {
                        rotateLeft(grandParent(node));
                        rotateLeft(node.parent);
                    } else {
                        rotateLeft(node.parent);
                        rotateRight(node.parent);
                    }
                }
        }
    }


    //Iterator block next
    //Stack for the iterator realization


    public class BinaryTreeIterator implements Iterator<T> {
        T value = null;
        Node<T> node = null;

        private BinaryTreeIterator() {
        }


        public boolean hasNext() {
            return stack.isEmpty();
        }


        public T next() {
            if (hasNext()) {
                Node<T> node = stack.pop();
                value = node.value;
                this.node = node;
                return (T) node.value;
            } else throw new NoSuchElementException();
        }


        public void remove() {
            T value = node.value;
            if (value == null) {
                throw new IllegalArgumentException();
            }
            if (root == null) throw new NullPointerException("Дерево пусто");

            find(value);
            if (root.value == value) {
                if (root.left == null) {
                    root.right.parent = null;
                    root = root.right;
                    size -= 1;
                } else {
                    T newRoot = findMaxFrom(root.left);
                    Node<T> right = root.right;
                    root.left.parent = null;
                    find(newRoot);
                    right.parent = root;
                    root.right = right;
                    size -= 1;
                }
            }
        }
    }
}
