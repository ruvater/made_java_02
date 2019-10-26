import java.lang.reflect.Array;
import java.util.*;

public class SimpleHashMap<K, V> implements SimpleMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K,V>[] table;
    private int size = 0;
    private final float loadFactor;

    SimpleHashMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    private SimpleHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    private SimpleHashMap(int initialCapacity, float loadFactor) {
        table = (Node<K, V>[]) Array.newInstance(Node.class, initialCapacity);
        Arrays.fill(table, null);
        this.loadFactor = loadFactor;
    }

    public V get(K key) {
        int index = Math.abs(key.hashCode()) % table.length;
        Node<K, V> head = table[index];
        if (head == null) return null;

        Node<K, V> node = findNode(head, key);

        return node != null ? node.getValue() : null;
    }

    public V put(K key, V value) {
        if (size + 1 >= table.length * loadFactor) {
            expandTable(table.length * 2);
        }

        return putToTable(table, key, value);
    }

    public V remove(K key) {
        int index = Math.abs(key.hashCode()) % table.length;
        Node<K, V> head = table[index];
        if (head == null) return null;

        if (head.getKey().equals(key)) {
            table[index] = head.getNext();
            size--;
            return head.getValue();
        }
        while (head.getNext() != null) {
            Node<K, V> next = head.getNext();
            if (next.getKey().equals(key)) {
                head.setNext(next.getNext());
                size--;
                return next.getValue();
            } else {
                head = next;
            }
        }

        return null;
    }

    public boolean contains(K key) {
        int index = getTableIndex(table, key.hashCode());
        Node<K, V> head = table[index];
        if (head == null) return false;

        Node<K, V> node = findNode(head, key);

        return node != null;
    }

    public int size() {
        return size;
    }

    public Set<K> keySet() {
        Iterator<Map.Entry<K, V>> iterator = new EntryIterator();

        Set<K> set = new HashSet<>();
        while (iterator.hasNext()) {
            set.add(iterator.next().getKey());
        }

        return set;
    }

    public Collection<V> values() {
        Iterator<Map.Entry<K, V>> iterator = new EntryIterator();

        Collection<V> collection = new ArrayList<>();
        while (iterator.hasNext()) {
            collection.add(iterator.next().getValue());
        }

        return collection;
    }

    private int getTableIndex(Node<K, V>[] table, int hashCode) {
        return Math.abs(hashCode) % table.length;
    }

    private V putToTable(Node<K, V>[] tableToPut, K key, V value) {
        int index = getTableIndex(tableToPut, key.hashCode());

        Node<K, V> nodeCursor = tableToPut[index];
        if (nodeCursor != null) {
            while (true) {
                if (nodeCursor.getKey().equals(key)) {
                    V oldValue = nodeCursor.getValue();
                    nodeCursor.setValue(value);
                    return oldValue;
                }
                if (nodeCursor.getNext() != null) {
                    nodeCursor = nodeCursor.getNext();
                } else {
                    Node<K, V> newNode = new Node<>(key, value);
                    nodeCursor.setNext(newNode);
                    size++;
                    return null;
                }
            }
        } else {
            tableToPut[index] = new Node<>(key, value);
            size++;
            return null;
        }
    }


    private void expandTable(int newCapacity) {
        Node<K, V>[] expandedTable = (Node<K, V>[]) Array.newInstance(Node.class, newCapacity);
        Arrays.fill(expandedTable, null);

        Iterator<Map.Entry<K, V>> iterator = new EntryIterator();

        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();

            putToTable(expandedTable, entry.getKey(), entry.getValue());
        }

        table = expandedTable;
    }

    private Node<K, V> findNode(Node<K, V> head, K key) {
        Node<K, V> currentNode = head;
        while (currentNode != null) {
            if (currentNode.getKey().equals(key)) {
                return currentNode;
            } else {
                currentNode = currentNode.getNext();
            }
        }

        return null;
    }

    final class EntryIterator implements Iterator<Map.Entry<K,V>> {
        private int tableCursor = 0;
        Node<K, V> nodeCursor = table[tableCursor];
        private int processed = 0;

        @Override
        public boolean hasNext() {
            return processed < size;
        }

        public final Map.Entry<K,V> next() {
            processed++;

            while (table[tableCursor] == null) {
                tableCursor++;
            }

            Node<K, V> head = table[tableCursor];

            if (head.getNext() != null) {
                nodeCursor = head.getNext();
            } else {
                tableCursor++;
            }

            return head;
        }
    }

    static class Node<K, V> implements Map.Entry<K,V> {
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        Node<K, V> getNext() {
            return next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        void setNext(Node<K, V> next) {
            this.next = next;
        }
    }
}
