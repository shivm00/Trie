package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * 
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		
		TrieNode root = new TrieNode(null, null, null);
		if(allWords.length == 0)
			return root;
		
		root.firstChild = new TrieNode(new Indexes(0, 
							(short)(0), 
							(short)(allWords[0].length() - 1)), null, null);
		
		TrieNode ptr = root.firstChild, prev = root.firstChild;
		int sierra = -1, startIdx = -1, endIdx = -1, wordIdx = -1;
		
		for(int i = 1; i < allWords.length; i++) {
			String word = allWords[i];
			
			
			while(ptr != null) {
				startIdx = ptr.substr.startIndex;
				endIdx = ptr.substr.endIndex;
				wordIdx = ptr.substr.wordIndex;
				
				
				if(startIdx > word.length()) {
					prev = ptr;
					ptr = ptr.sibling;
					continue;
				}
				
				sierra = similarUntil(allWords[wordIdx].substring(startIdx, endIdx+1), 
						word.substring(startIdx)); 
				
				if(sierra != -1)
					sierra += startIdx;
				
				if(sierra == -1) { 
					prev = ptr;
					ptr = ptr.sibling;
				}
				else {
					if(sierra == endIdx) { 
						prev = ptr;
						ptr = ptr.firstChild;
					}
					else if (sierra < endIdx){ 
						prev = ptr;
						break;
					}
				}
			}
			
			if(ptr == null) {
				Indexes alpha = new Indexes(i, (short)startIdx, (short)(word.length()-1));
				prev.sibling = new TrieNode(alpha, null, null);
			} else {
				Indexes currentIndexes = prev.substr;
				TrieNode currentFirstChild = prev.firstChild; 
				
				Indexes currWordNewIndexes = new Indexes(currentIndexes.wordIndex, (short)(sierra+1), currentIndexes.endIndex);
				currentIndexes.endIndex = (short)sierra;
				
				prev.firstChild = new TrieNode(currWordNewIndexes, null, null);
				prev.firstChild.firstChild = currentFirstChild;
				prev.firstChild.sibling = new TrieNode(new Indexes((short)i, 
						(short)(sierra+1), 
						(short)(word.length()-1)), 
						null, null);
			}
			
			ptr = prev = root.firstChild;
			sierra = startIdx = endIdx = wordIdx = -1;
		}
		
		return root;
	}
	
	private static int similarUntil(String alpha, String beta){
		int ans = 0;
		while(ans<alpha.length() && 
				ans<beta.length() && 
				alpha.charAt(ans)==beta.charAt(ans)){
			ans++;
		}
		return (ans-1);
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		
		if(root==null){
			return null;
		}
		ArrayList<TrieNode> answer = new ArrayList<TrieNode>();
		TrieNode ptr = root;
		while(ptr!=null){
			if(ptr.substr==null){
				ptr = ptr.firstChild;
			}
			
		String sierra = allWords[ptr.substr.wordIndex];
		String alpha = sierra.substring(0, ptr.substr.endIndex+1);
		if(sierra.startsWith(prefix) || prefix.startsWith(alpha)){
			if(ptr.firstChild!=null){
				answer.addAll(completionList(ptr.firstChild,allWords,prefix));
				ptr=ptr.sibling;
			}else{
				answer.add(ptr);
				ptr=ptr.sibling;
			}
		}else{
			ptr = ptr.sibling;
		}
		}
		
		return answer;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
