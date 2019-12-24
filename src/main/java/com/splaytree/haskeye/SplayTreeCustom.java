package com.splaytree.haskeye;

import java.util.*;

public abstract class SplayTreeCustom<T extends Comparable<T>> implements Set<T> {
    //Stack for iterator
    Stack<Node<T>> stack;

    private static class Node<T> extends Object {
        final T value;

        Node<T> parent = null;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;


    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<T>(t);
        if (closest == null) {
            root = newNode;
        } else {
            if (comparison < 0) {
                assert closest.left == null;
                closest.left = newNode;
            } else {
                assert closest.right == null;
                closest.right = newNode;
            }
            newNode.parent = closest;
        }
        size++;
        stack.add(newNode);
        return true;
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
        if (!this.contains(0)) return false;
        T target = (T) o;
        BinaryTreeIterator i = new BinaryTreeIterator();
        Node<T> del = (Node<T>) i.next();
        while (del.value != target) {
            del = (Node<T>) i.next();
        }
        i.remove();
        return true;
    }


    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
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
        if (root == null) return null;
        Node<T> founded = find(root, value);
        splay(founded);
        return founded;
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
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

        private BinaryTreeIterator() {
        }


        public boolean hasNext() {
            return !stack.isEmpty();
        }


        public T next() {
            Node<T> node = stack.pop();
            T result = (T) node.value;
            if (node.right != null) {
                node = node.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
            return result;
        }


        public void remove() {
            if (hasNext()) {
                Node<T> n = (find(next()));
                T t = (T) n.value;
                Node<T> node = findDel(t);
                if (node.left.value == n) {
                    Node<T> i = node.left.right;
                    node.left = node.left.left;
                    //Передать ветвь i кому-то
                    findEmptyRight().right = i;
                } else {
                    Node<T> i = node.left.left;
                    node.right = node.right.right;
                    //Передать ветвь i кому-то
                    if (i != null) {
                        findEmptyLeft().left = i;
                    }
                }
            } else throw new NoSuchElementException();
        }
    }
}
