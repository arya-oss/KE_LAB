package fptree;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class FPTree {
	private Node root;
	public FPTree() {
		root = new Node();
	}
	/**
	 * @param items is string in which item is separated by whitespace
	 * and it call recursive method insert to insert items in FPTree.
	 */
	public void insert(String [] items) {
		// 0th index is transaction id
		this.root = insert(this.root, items, 1, items.length-1);
	}
	/**
	 * @param T this is the root of tree
	 * @param items is an array of items
	 * this method insert an items into the FPTree.
	 */
	private Node insert(Node T, String [] items, int st, int end ) {
		//System.out.println("processing "+ items[st]);
		if(T.ptrs.containsKey(items[st])) {
			T.ptrs.get(items[st]).count++;
			if(st != end)
				T.ptrs.put(items[st], insert(T.ptrs.get(items[st]), items, st+1, end));
		} else {
			Node _new = new Node();
			_new.count++;
			_new.name = items[st];
			T.ptrs.put(items[st], _new);
			if(st != end)
				T.ptrs.put(items[st], insert(T.ptrs.get(items[st]), items, st+1, end));
		}
		return T;
	}
	public void print() {
		print(this.root);
	}
	private void print(Node T) {
		if(T == null) return;
		System.out.println(T.name+ " " + T.count);
		Set<Entry<String, Node> > set = T.ptrs.entrySet();
		for(Entry<String, Node> en : set) {
			print(en.getValue());
		}
	}
	public static void main(String[] args) throws IOException {
		FPTree tree = new FPTree();
		FileReader fr = new FileReader("transaction.txt");
		String [] transaction = new String[9]; int cnt=0;
		int trans[] = new int[6];
		Arrays.fill(trans, 0);
		Scanner sc = new Scanner(fr);
		while(sc.hasNextLine()) {
			transaction[cnt++] = sc.nextLine();
			String[] str = transaction[cnt-1].split(" ");
			for(int i=1; i<str.length; i++) {
				trans[Integer.parseInt(str[i])]++;
			}
		}
		fr.close();
		sc.close();
		
		for(String t : transaction) {
			String [] str = t.split(" ");
			for(int i=1; i<str.length; i++) {
				for(int j=1; j<str.length-1-i; j++) {
					if(trans[Integer.parseInt(str[j])] < trans[Integer.parseInt(str[j+1])]) {
						String _tmp = str[j];
						str[j] = str[j+1];
						str[j+1] = _tmp;
					}
				}
			}
			tree.insert(str);
		}
		tree.print();
	}

}
