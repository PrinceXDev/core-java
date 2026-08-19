import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Collections Framework — the everyday data structures behind every backend
 * ----------------------------------------------------------------------------
 * Arrays are fixed-size and low-level. Collections are resizable, richer, and
 * this is what you will actually use 95% of the time in real code.
 *
 * The 3 shapes you must know:
 *   List : ordered, allows duplicates, index-based access -> ArrayList, LinkedList
 *   Set  : no duplicates, no guaranteed order (unless you pick a sorted one) -> HashSet, TreeSet
 *   Map  : key -> value pairs, keys unique -> HashMap, TreeMap
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        listBasics();
        arrayListVsLinkedList();
        setRemovesDuplicates();
        mapKeyValuePairs();
        iteratingSafely();
    }

    // -----------------------------------------------------------------
    // 1. List: ordered, duplicates allowed, access by index
    // -----------------------------------------------------------------
    static void listBasics() {
        System.out.println("--- List basics ---");
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Apple"); // duplicates are fine in a List
        System.out.println("list = " + fruits);
        System.out.println("first = " + fruits.get(0)); // index-based access
        fruits.remove("Banana");
        System.out.println("after remove = " + fruits);
    }

    // -----------------------------------------------------------------
    // 2. ArrayList vs LinkedList: pick based on the operation you do most
    // -----------------------------------------------------------------
    static void arrayListVsLinkedList() {
        System.out.println("\n--- ArrayList vs LinkedList ---");
        // ArrayList: backed by a resizable array -> fast random access (get(i)),
        // slower insert/remove in the middle (has to shift elements)
        List<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        System.out.println("ArrayList good for: fast get(index) -> " + arrayList.get(0));

        // LinkedList: backed by nodes with next/prev pointers -> fast insert/remove
        // at the ends, slower random access (must walk the chain)
        LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.addFirst(10);
        linkedList.addLast(20);
        System.out.println("LinkedList good for: fast addFirst/addLast -> " + linkedList);

        for (Integer number : linkedList) {
            System.out.println(number);
        }

        System.out.println("======================================");

        LinkedList<String> history = new LinkedList<String>();
        history.addLast("Home");
        history.addLast("Products");
        history.addLast("Cart");

        for (String h : history) {
            System.out.println(h);
        }
    }

    // -----------------------------------------------------------------
    // 3. Set: automatically rejects duplicates
    // -----------------------------------------------------------------
    static void setRemovesDuplicates() {
        System.out.println("\n--- Set removes duplicates ---");
        Set<String> hashSet = new HashSet<>(); // no ordering guarantee
        hashSet.add("Java");
        hashSet.add("Python");
        hashSet.add("Java"); // ignored - already present
        System.out.println("HashSet (no order guarantee) = " + hashSet);

        Set<Integer> treeSet = new TreeSet<>(); // always sorted ascending
        treeSet.add(5);
        treeSet.add(1);
        treeSet.add(3);
        System.out.println("TreeSet (sorted) = " + treeSet);
    }

    // -----------------------------------------------------------------
    // 4. Map: key -> value, keys are unique (adding same key overwrites value)
    // -----------------------------------------------------------------
    static void mapKeyValuePairs() {
        System.out.println("\n--- Map key-value pairs ---");
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Prince", 25);
        ages.put("Asha", 30);
        ages.put("Prince", 26); // overwrites the previous value for "Prince"
        System.out.println("ages = " + ages);
        System.out.println("Prince's age = " + ages.get("Prince"));
        System.out.println("contains 'Asha'? " + ages.containsKey("Asha"));

        Map<String, Integer> sortedAges = new TreeMap<>(ages); // sorted by key
        System.out.println("sorted by key = " + sortedAges);
    }

    // -----------------------------------------------------------------
    // 5. Iterator: the safe way to remove items WHILE looping
    // (a plain for-each loop throws ConcurrentModificationException if you
    // call list.remove() inside it)
    // -----------------------------------------------------------------
    static void iteratingSafely() {
        System.out.println("\n--- safe removal with Iterator ---");
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        Iterator<Integer> it = numbers.iterator();
        while (it.hasNext()) {
            int n = it.next();
            if (n % 2 == 0) {
                it.remove(); // safe - removes via the iterator, not the list directly
            }
        }
        System.out.println("odds only = " + numbers);
    }
}
