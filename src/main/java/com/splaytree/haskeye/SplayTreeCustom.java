package com.splaytree.haskeye;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class SplayTreeCustom<T extends Comparable<T>> extends AbstractSet<T> {

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

    @Override
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


    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (!this.contains(0)) return false;
        T t = (T) o;
        Node node = findDel(t);
        if (node.left.value == t) {
            Node i = node.left.right;
            node.left = node.left.left;
            //Передать ветвь i кому-то
            findEmptyRight().right = i;
        } else {
            Node i = node.left.left;
            node.right = node.right.right;
            //Передать ветвь i кому-то
            if (i != null) {
                findEmptyLeft().left = i;
            }
        }
        return true;
    }


    @Override
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
            Node i = findEmptyLeft(start.left);
            Node j = findEmptyLeft(start.right);
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
            Node i = findEmptyLeft(start.left);
            Node j = findEmptyLeft(start.right);
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
        return find(root, value);
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

    //Stack for the iterator realization
    Stack<Node> stack;

    public class BinaryTreeIterator implements Iterator<T> {

        private BinaryTreeIterator() {
            // Init added
            Stack stack = new Stack<Node>();
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
        }


        public boolean hasNext() {
            return !stack.isEmpty();
        }


        public T next() {
            Node node = stack.pop();
            @SuppressWarnings("unchecked")
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
                Node n = (find(next()));
                @SuppressWarnings("unchecked")
                T t = (T) n.value;
                Node node = findDel(t);
                if (node.left.value == n) {
                    Node i = node.left.right;
                    node.left = node.left.left;
                    //Передать ветвь i кому-то
                    findEmptyRight().right = i;
                } else {
                    Node i = node.left.left;
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
