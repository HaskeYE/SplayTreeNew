package com.splaytree.haskeye;

import com.sun.org.apache.xpath.internal.objects.XNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SplayTreeCustom<T extends Comparable<T>> implements Collection<T> {
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

    private Node<T> root = null;

    private int size = 0;

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
        int key = t.hashCode();
        // Create a new Node and initialize it
        Node<T> newNode = new Node<T>(t);

        if (!this.contains(t)) {
            if (root == null) {
                root = newNode;
            } else {
                Node<T> focusNode = root;
                Node<T> parent;
                while (true) {
                    parent = focusNode;
                    // Check if the new node should go on
                    // the left side of the parent node
                    if (key < focusNode.key) {
                        // Switch focus to the left child
                        focusNode = focusNode.left;
                        if (focusNode == null) {
                            parent.left = newNode;
                        }
                    } else {
                        focusNode = focusNode.right;
                        if (focusNode == null) {
                            parent.right = newNode;
                        }
                    }
                }
            }
            size++;
            stack.add(newNode);
            return true;
        }
        return false;
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
    public boolean removeAll(Collection c) {
        boolean b = true;
        for (T t : (Iterable<T>) c) {
            b = this.remove(t);
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

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    @Override
    public Stream<T> stream() {
        return null;
    }

    @Override
    public Stream<T> parallelStream() {
        return null;
    }


    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
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


    public boolean remove(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (size == 1) clear();
        if (!this.contains(o)) return false;
        T target = (T) o;
        BinaryTreeIterator i = new BinaryTreeIterator();
        T del = i.next();
        while (del != target) {
            del = i.next();
        }
        remove(i.value);
        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return false;
    }


    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    public boolean contains(Object o) {
        if (size == 0) return false;
        if (size == 1) {
            return root.value == (T) o;
        } else {
            T t = (T) o;
            BinaryTreeIterator i = new BinaryTreeIterator();
            T fin = i.next();
            while (fin != t || !stack.isEmpty()) {
                fin = i.next();
            }
            return fin == t;
        }
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

    private Node<T> find(T value) {
        if (contains(value)) {
            BinaryTreeIterator i = new BinaryTreeIterator();
            T fin = i.next();
            while (fin != value || !stack.isEmpty()) {
                fin = i.next();
            }
            splay(i.node);
            return root;
        } else throw new NoSuchElementException();
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
            if (size == 1) clear();
            if (contains(value)) {

                if (root.left.key > root.right.key) {
                    root.right.parent = root.left;
                    root = root.left;
                } else {
                    root.left.parent = root.right;
                    root = root.right;
                }
                size -= 1;
            } else throw new NoSuchElementException();
        }
    }
}
