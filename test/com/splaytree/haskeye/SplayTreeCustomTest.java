package com.splaytree.haskeye;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.soap.Node;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class SplayTreeCustomTest {
    private Object Node;

    @Test
    public void treeAdd() {
       SplayTreeCustom<String> tree = new SplayTreeCustom<>();
        tree.add("abc");
        ArrayList<Node> arr = new ArrayList<Node>();
        assertEquals(1, tree.size());
    }

    @Test
    public void treeRemove() {
        SplayTreeCustom<String> tree = new SplayTreeCustom<>();
        tree.add("abc");
        tree.remove("abc");
        assertEquals(0, tree.size());
    }
}
