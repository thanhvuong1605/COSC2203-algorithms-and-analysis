public class IntList {
    private int[] data;
    private int size;

    public IntList(int capacity) {
        data = new int[capacity];
        size = 0;
    }

    public void add(int value) {
        if (size >= data.length) grow();
        data[size++] = value;
    }

    public int get(int index) {
        return data[index];
    }

    public int size() {
        return size;
    }

    private void grow() {
        int[] newData = new int[data.length * 2];
        for (int i = 0; i < data.length; i++) newData[i] = data[i];
        data = newData;
    }
} 