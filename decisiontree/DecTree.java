package decisiontree;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Scanner;

public class DecTree {
	static class Node {
		String attr_name;
		HashMap<String, Node> branches;
		String classId;
		Node(){
			this.attr_name="default";
		}
	}

	static HashMap<String, ArrayList<String>> hmap;

	public static void main(String[] args) throws FileNotFoundException {
		hmap = new HashMap<>();
		ArrayList<String> attr_list = new ArrayList<>();
		ArrayList<HashMap<String, String>> database = new ArrayList<>();
		HashMap<String, String> _tmp;
		FileReader fr = new FileReader("dectree.dat");
		Scanner sc = new Scanner(fr);
		String str [] = sc.nextLine().split(" ");
		for(String s : str){
			attr_list.add(s);
			hmap.put(s, new ArrayList<>());
		}
		while(sc.hasNextLine()){
			str = sc.nextLine().split(" ");
			_tmp = new HashMap<>();
			for(int i=0; i<str.length; i++){
				_tmp.put(attr_list.get(i), str[i]);
				if(hmap.get(attr_list.get(i)).contains(str[i]) == false){
					hmap.get(attr_list.get(i)).add(str[i]);
				}
			}
			database.add(_tmp);
		}
		sc.close();
		Node root=null;
		root = GenerateTree(root, database, attr_list);
		printTree(root);
	}
	static double info_gain(ArrayList<HashMap<String, String>> db, String attr, ArrayList<String> attr_list) {
		double value=0.0, total;
		for(String str:hmap.get(attr)){
			double nocnt=0, yescnt=0; total=0;
			for(HashMap<String, String> hm: db){
				if(hm.get(attr).equals(str)){
					if(hm.get(attr_list.get(attr_list.size()-1)).equals("yes"))
						yescnt++;
					else nocnt++;
				}
				total++;
			}
			total = (nocnt+yescnt)/total;
			double value1 = yescnt/(nocnt+yescnt);
			double value2  = nocnt/(nocnt+yescnt);
			if(value1 != 0.0){
				value += -1*total*value1*log2(value1);
			}
			if(value2 != 0.0){
				value += -1*total*value2*log2(value2);
			}
		}
		return value;
	}
	static String bestAttribute(ArrayList<HashMap<String, String>> db, ArrayList<String> attr_list){
		String _best = null;
		if(attr_list.size() > 1){
			double yescnt=0, nocnt=0, value=0.0;
			for(HashMap<String, String> hm:db){
				if(hm.get(attr_list.get(attr_list.size()-1)).equals("yes"))
					yescnt++;
				else nocnt++;
			}
			//System.out.println(nocnt+" "+yescnt);
			double value1 = (double)yescnt/(nocnt+yescnt);
			double value2  = (double)nocnt/(nocnt+yescnt);
			if(value1 != 0.0){
				value += -1*value1*log2(value1);
			}
			if(value2 != 0.0){
				value += -1*value2*log2(value2);
			}
			_best=attr_list.get(0);
			double info = value-info_gain(db, _best, attr_list);
			for(int i=1; i<attr_list.size()-1; i++){
				double _tmp_info = value-info_gain(db, attr_list.get(i), attr_list);
				if(info < _tmp_info){
					info = _tmp_info;
					_best = attr_list.get(i);
				}
			}
		}
		return _best;
	}
	static Node GenerateTree(Node root, ArrayList<HashMap<String, String>> db, ArrayList<String> attr_list){
		int yescnt=0, nocnt=0;
		for(HashMap<String, String> hm:db){
			if(hm.get(attr_list.get(attr_list.size()-1)).equals("yes"))
				yescnt++;
			else nocnt++;
		}
		if(yescnt==0 || nocnt==0){
			root = new Node();
			root.classId = (yescnt==0)?"no":"yes";
			return root;
		}
		if(attr_list.size()==1) {
			root = new Node();
			root.classId = (yescnt>=nocnt)?"yes":"no";
			return root;
		}
		String best_attr = bestAttribute(db, attr_list);
		//System.out.println(best_attr);
		attr_list.remove(best_attr);
		ArrayList<String> best_attr_types = new ArrayList<>();
		for(HashMap<String, String> hm:db){
			if(best_attr_types.contains(hm.get(best_attr)) == false){
				best_attr_types.add(hm.get(best_attr));
			}
		}
		root = new Node();
		root.classId="no";
		root.attr_name = best_attr;
		root.branches = new HashMap<>();
		for(String best_attr_type:best_attr_types){
			ArrayList<HashMap<String, String>> _tmp = new ArrayList<>();
			for(HashMap<String, String> hm:db){
				if(hm.get(best_attr).equals(best_attr_type)){
					_tmp.add(hm);
				}
			}
			root.branches.put(best_attr_type, null);
			root.branches.put(best_attr_type, GenerateTree(root.branches.get(best_attr_type), _tmp, attr_list));
		}
		return root;
	}
	static void printTree(Node root) {
		if(root == null) return;
		System.out.println(root.attr_name+ " "+ root.classId);
		if(root.branches == null) return;
		Set<Entry<String, Node>> _set = root.branches.entrySet();
		for(Entry<String, Node> en:_set){
			System.out.println(root.attr_name+" -> "+en.getKey());
			printTree(en.getValue());
		}
	}
	static double log2(double val) {
		return Math.log(val)/Math.log(2);
	}
}
