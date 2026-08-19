# Java Collections — The Decision Path

> Use this, not memory. Ask the questions top-down and the answer falls out.

## The naming formula

Every collection name is `Prefix` + `Suffix`.

- **Suffix** = *what shape my data is* (List / Set / Map / Queue)
- **Prefix** = *how it's built* (Array / Linked / Hash / Tree / Concurrent)

Learn the two halves and you can name a class you've never used.

## Step 1 — Suffix: what am I storing?

| Your need | Suffix | Duplicates? | Ordered? |
| --- | --- | --- | --- |
| A numbered row of things | `List` | Yes | Yes, by index |
| A bag of unique things | `Set` | No | Depends on prefix |
| Key → Value lookup | `Map` | Keys unique | Depends on prefix |
| A waiting line | `Queue` / `Deque` | Yes | By in/out rules |

Mnemonic: **L·S·M·Q** — List = position, Set = uniqueness, Map = label, Queue = order of service.

`Map` is **not** a `Collection`. It's a separate branch of the hierarchy — the one structural fact worth memorising outright.

## Step 2 — Prefix: how should it behave?

| Prefix | Structure | You get | Cost |
| --- | --- | --- | --- |
| `Array…` | Resizable array | Fast random access `get(i)` | Slow insert/remove in middle |
| `Linked…` | Node chain | Fast add/remove at ends | No random access |
| `Hash…` | Hash table | O(1) find, **no order** | Needs `equals` / `hashCode` |
| `Tree…` | Red-black tree | **Sorted**, O(log n) | Needs `Comparable` / `Comparator` |
| `Concurrent…` | Lock-striped | Thread-safe without locking | Slight overhead |

## Step 3 — Multiply them

```
Hash   + Map          -> HashMap             fast key lookup, order undefined
Tree   + Map          -> TreeMap             same, but keys kept sorted
Hash   + Set          -> HashSet             unique items, order undefined
Tree   + Set          -> TreeSet             unique items, sorted
Array  + List         -> ArrayList           indexed access
Linked + List         -> LinkedList          cheap add/remove at head/tail
Linked + Hash + Map   -> LinkedHashMap       hash speed + insertion order kept
Concurrent + Hash + Map -> ConcurrentHashMap HashMap, thread-safe
```

Never used `LinkedHashSet`? You already know it: unique items, remembered in insertion order.

## The decision path

```
Do I have key -> value pairs?
|
+-- YES -> Map
|     +-- just need it fast              -> HashMap             <- 90% default
|     +-- need keys sorted / ranges      -> TreeMap
|     +-- need insertion order kept      -> LinkedHashMap
|     +-- shared across threads          -> ConcurrentHashMap
|
+-- NO -> single values
      |
      +-- must duplicates be blocked?
            |
            +-- YES -> Set
            |     +-- fast                -> HashSet            <- default
            |     +-- sorted              -> TreeSet
            |     +-- insertion order     -> LinkedHashSet
            |
            +-- NO -> List / Queue
                  +-- index access, loops -> ArrayList          <- default
                  +-- heavy add/remove at ends -> LinkedList / ArrayDeque
                  +-- FIFO line           -> ArrayDeque
                  +-- "most urgent first" -> PriorityQueue
```

### The three defaults

`ArrayList` · `HashMap` · `HashSet`

Start there **always**. Move off the default only when something forces you:

- to a `Tree…` when you need **sorting**
- to a `Linked…` when you need **insertion order** remembered, or cheap **end insertion**
- to a `Concurrent…` when **threads share** it

## The trap worth knowing

- `Hash…` needs correct `hashCode()` **and** `equals()` on your objects. Get it wrong and duplicates silently sneak into your `HashSet`.
- `Tree…` needs `Comparable` (or a `Comparator` passed in). Get it wrong and you get `ClassCastException`.

Remember them as a pair: **Hash → hashing contract. Tree → ordering contract.**

## One line to keep

> **Suffix = what I need. Prefix = how fast / in what order.**
> Default to `ArrayList` / `HashMap` / `HashSet`; swap the prefix only when order or threads force you to.
