public class Queue {
    State[] data; // State array
    int head, tail; // pointers to the head node and tail node

    public Queue(int capacity) { // default constructor of Queue class  
        data = new State[capacity];
        head = 0;
        tail = 0;
    }

    public void enqueue(State node) { data[tail++] = node; } // add new node at the end of the line, then increase the tail value
    public State dequeue() { return data[head++]; } // return the head node's value, then increase the head value
    public boolean isEmpty() { return head == tail; } // queue is empty if head catches tail
} 