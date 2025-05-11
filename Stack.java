public class Stack {
    State[] data; // State array
    int top; // pointer to top node

    public Stack(int capacity) { // default constructor of Stack class
        data = new State[capacity];
        top = 0;
    }

    public void push(State node) { data[top++] = node; } // add new element to stack by putting it in the top of line
    public State pop() { return data[--top]; } // remove the top, then decrease the pointer 
    public boolean isEmpty() { return top == 0; } // Stack is empty if top is 0
} 