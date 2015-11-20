package apriori;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * Title - Hash Based Apriori Implementation in JAVA
 * @author rajmani arya
 */
public class HashBasedApriori {
	public static int min_sup=2;
	public static int buckets=7;
    static HashMap<String, Integer> hmap, finalMap;
    
    static ArrayList<ArrayList<String>> bucket;
    static int [] bucketCount;
    
    public static boolean has_infrequent_itemset(String str) {
        String s [] = str.split(" ");
        for(int i=0; i<s.length; i++){
            String subset="";
            for(int j=0; j<s.length; j++){
                if(i!=j)
                    subset += s[j]+" ";
            }
            subset = subset.trim();
            if(!finalMap.containsKey(subset))
                return true;
        }
        return false;
    }
    public static String Union(String s1, String s2) {
        TreeSet<String> st = new TreeSet<String>();
        st.addAll(Arrays.asList(s1.split(" ")));
        st.addAll(Arrays.asList(s2.split(" ")));
        String str="";
        if(s1.split(" ").length != 1 &&
            st.size() == s1.split(" ").length+s2.split(" ").length)
            return null;
        for(String s:st)
            str += s + " ";
        return str.trim();
    }
    public static TreeSet<String> AprioriGen() {
        TreeSet<String> al = new TreeSet<String>();
        ArrayList<String> join = new ArrayList<String>();
        Set<Entry<String, Integer>> st = finalMap.entrySet();
        for(Entry<String, Integer> en : st){
            join.add(en.getKey());
        }
        for(int i=0; i<join.size(); i++) {
            for(int j=i+1; j<join.size(); j++){
                String un = Union(join.get(i), join.get(j));
                //if(un != null && !has_infrequent_itemset(un))
                    //al.add(un);
                //System.out.println(un);
                if(un == null) continue;
                String tokens[] = un.split(" ");
                int k=0;
                for(String token:tokens) {
                	k += k*10+Integer.parseInt(token);
                }
                int buck = k%buckets;
                if(bucket.get(buck).contains(un)){
                	bucketCount[buck]++;
                } else {
                	bucket.get(buck).add(un);
                	bucketCount[buck]++;
                }
                
            }
        }
        for(int i=0; i<buckets; i++){
        	if(bucketCount[i] >= min_sup) {
        		for(String un: bucket.get(i)){
        			if(!has_infrequent_itemset(un)){
        				al.add(un);
        			}
        		}
        	}
        }
        bucketClear();
        return al;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        hmap = new HashMap<String, Integer>();
        finalMap = new HashMap<String, Integer>();
        bucket = new ArrayList<>();
        bucketCount = new int[buckets];
        /**
         * Initialize bucket and bucket count
         */
        for(int i=0; i<buckets; i++) {
        	bucket.add(new ArrayList<>());
        	bucketCount[i]=0;
        }
        
        GenerateFirstItemSet();
        for(int k=2; !finalMap.isEmpty(); k++) {
            TreeSet<String> ap = AprioriGen();
            finalMap.clear();
            FileReader fr = new FileReader("transaction.dat");
            Scanner sc = new Scanner(fr);
            while(sc.hasNextLine()) {
                String [] subset = sc.nextLine().split(" ");
                for(String s:ap) {
                    if(hasSubset(subset, s)){
                        if(finalMap.containsKey(s)) 
                            finalMap.put(s, finalMap.get(s)+1);
                        else finalMap.put(s, 1);
                    }
                }
            }
            fr.close();
            sc.close();
            mapClear();
            System.out.println("---- frequent itemset of size "+ k +" -----");
            printMap();
        }
    }
    public static void mapClear() {
        hmap.clear();
        Set<Entry<String, Integer>> st = finalMap.entrySet();
        for(Entry<String, Integer> e : st) {
             if(e.getValue() >= min_sup)
                 hmap.put(e.getKey(), e.getValue());
        }
        finalMap = hmap;
        hmap = new HashMap<String, Integer>();
    }
    
    public static void bucketClear() {
    	bucket.clear();
    	for(int i=0; i<buckets; i++) {
        	bucket.add(new ArrayList<>());
        	bucketCount[i]=0;
        }
    }
    
    public static void printMap() {
        Set<Entry<String, Integer>> st = finalMap.entrySet();
        for(Entry<String, Integer> e : st) {
             System.out.println(e.getKey()+ " -> "+e.getValue());
        }
    }
    
    public static void GenerateFirstItemSet() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("transaction.txt");
        Scanner sc = new Scanner(fr);
        while(sc.hasNextLine()) {
            String str[] = sc.nextLine().split(" ");
            for(int i=1; i<str.length; i++)
                if(finalMap.containsKey(str[i])) 
                    finalMap.put(str[i], finalMap.get(str[i])+1);
                else finalMap.put(str[i], 1);
        }
        fr.close();
        sc.close();
        mapClear();
        System.out.println("----frequent itemset of size 1-----");
        printMap();
    }

    private static boolean hasSubset(String[] subset, String s) {
        String test[] = s.split(" ");
        int i=0,j=0;
        for(i=1; i<subset.length && j<test.length; i++) {
            if(subset[i].equals(test[j])) j++;
            else{
                if(Integer.parseInt(subset[i]) > Integer.parseInt(test[j]))
                    return false;
            }
        }
        return j == test.length;
    }
    
}
