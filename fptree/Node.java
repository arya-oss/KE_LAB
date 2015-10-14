package fptree;

import java.util.HashMap;

public class Node {
	int count;
	String name;
	HashMap<String, Node> ptrs;
	Node() {
		this.count = 0;
		name = "NULL";
		ptrs = new HashMap<>();
	}
}
