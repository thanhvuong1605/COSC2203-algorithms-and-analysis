public class PriorityQueue { // Priority Queue (Min-Heap Array), used in the A* method.
    State[] heap;
    int size;

    public PriorityQueue(int capacity) {
        heap = new State[capacity];
        size = 0;
    }

    public void add(State node) { // add new node
        heap[size] = node; 
        siftUp(size); // ensure the lower f is always shifted up.
        size++;
    }

    public State poll() { // remove and return the node at the front of the queue
        State res = heap[0];
        heap[0] = heap[--size];
        siftDown(0);
        return res;
    }

    public boolean isEmpty() { return size == 0; }

    private void siftUp(int i) { // move the lower f to the top 
        while (i > 0) {
            int p = (i - 1) / 2;
            if (heap[i].f() >= heap[p].f()) break;
            swap(i, p);
            i = p;
        }
    }

    private void siftDown(int i) { // move the higher f to the bottom
        while (2 * i + 1 < size) {
            int left = 2 * i + 1;
            int right = left + 1;
            int smallest = left;
            if (right < size && heap[right].f() < heap[left].f()) smallest = right;
            if (heap[i].f() <= heap[smallest].f()) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int i, int j) { // swap two States
        State tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }
}