public class HashSet {
    String[] table;

    public HashSet(int capacity) { table = new String[capacity]; } // default constructor for Hashset

    // Custom hash function to replace String.hashCode()
    private int customHash(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) % table.length;
        }
        return hash;
    }

    int hash(String key) { return customHash(key); }

    private boolean customEquals(String s1, String s2) {
        if (s1.length() != s2.length()) return false;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) return false;
        }
        return true;
    }

    public boolean contains(String key) { // check if the hash Table contains the elements already 
        int idx = hash(key);
        while (table[idx] != null) { 
            if (customEquals(table[idx], key)) return true;
            idx = (idx + 1) % table.length;
        }
        return false;
    }

    public void add(String key) { // add new element (Open Address Hashing - Linear Probing)
        int idx = hash(key);
        while (table[idx] != null) idx = (idx + 1) % table.length; // if the current index is occupied, move to next index
        table[idx] = key; // allocate element to array
    }
} 