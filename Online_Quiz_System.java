
import java.util.Scanner;

// Stores one quiz question and its options
class Question {
    String question;
    String[] options;
    char correctAnswer;

    Question(String question, String[] options, char correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
}

// Queue implementation using an array
class ArrayQueue {
    private int[] queue;
    private int front, rear, size, capacity;

    ArrayQueue(int capacity) {
        this.capacity = capacity;
        queue = new int[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    boolean isEmpty() {
        return size == 0;
    }

    boolean isFull() {
        return size == capacity;
    }

    void enqueue(int value) {
        if (isFull()) {
            System.out.println("Queue is full!");
            return;
        }
        rear = (rear + 1) % capacity;
        queue[rear] = value;
        size++;
    }

    int dequeue() {
        if (isEmpty()) {
            return -1;
        }
        int value = queue[front];
        front = (front + 1) % capacity;
        size--;
        return value;
    }
}

// Stack implementation using an array
class ArrayStack {
    private int[] stack;
    private int top;
    private int capacity;

    ArrayStack(int capacity) {
        this.capacity = capacity;
        stack = new int[capacity];
        top = -1;
    }

    boolean isEmpty() {
        return top == -1;
    }

    boolean isFull() {
        return top == capacity - 1;
    }

    void push(int value) {
        if (!isFull()) {
            stack[++top] = value;
        }
    }

    int pop() {
        if (isEmpty()) {
            return -1;
        }
        return stack[top--];
    }
}

// Stores a participant's result
class ScoreRecord {
    String name;
    int score;
    int total;

    ScoreRecord(String name, int score, int total) {
        this.name = name;
        this.score = score;
        this.total = total;
    }
}

public class Online_Quiz_System {
    static Scanner sc = new Scanner(System.in);

    // Array of quiz questions
    static Question[] questions = {
        new Question("Which language runs on the JVM?",
            new String[]{"Python", "Java", "C", "HTML"}, 'B'),

        new Question("Which data structure follows FIFO?",
            new String[]{"Stack", "Tree", "Queue", "Graph"}, 'C'),

        new Question("Which data structure follows LIFO?",
            new String[]{"Queue", "Stack", "Array", "Queue and Stack"}, 'B'),

        new Question("Which keyword creates an object in Java?",
            new String[]{"class", "static", "new", "void"}, 'C'),

        new Question("Which sorting algorithm is used in this project?",
            new String[]{"Merge Sort", "Bubble Sort",
                         "Quick Sort", "Heap Sort"}, 'B')
    };

    // Displays the questions and accepts answers
    static ScoreRecord startQuiz() {
        System.out.print("Enter participant name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            name = "Participant";
        }

        int n = questions.length;
        int[] answers = new int[n];

        for (int i = 0; i < n; i++) {
            answers[i] = -1;
        }

        // Queue contains the question indices
        ArrayQueue queue = new ArrayQueue(n);
        for (int i = 0; i < n; i++) {
            queue.enqueue(i);
        }

        // Stacks track backward and forward navigation
        ArrayStack backStack = new ArrayStack(n);
        ArrayStack forwardStack = new ArrayStack(n);

        int current = queue.dequeue();
        boolean submit = false;

        while (!submit) {
            Question q = questions[current];

            System.out.println("\n--------------------------------");
            System.out.println("Question " + (current + 1) + " of " + n);
            System.out.println(q.question);

            for (int i = 0; i < q.options.length; i++) {
                System.out.println((char) ('A' + i) + ". " + q.options[i]);
            }

            if (answers[current] != -1) {
                System.out.println("Your answer: "
                    + (char) ('A' + answers[current]));
            }

            System.out.println("\nEnter A/B/C/D to answer");
            System.out.println("N = Next | P = Previous | S = Submit");
            System.out.print("Your choice: ");

            String input = sc.nextLine().trim().toUpperCase();

            // Save the answer and move forward
            if (input.matches("[A-D]")) {
                answers[current] = input.charAt(0) - 'A';
                current = moveNext(current, backStack,
                                   forwardStack, queue);

                if (current == -1) {
                    submit = true;
                }
            }
            // Go to the next question
            else if (input.equals("N")) {
                int next = moveNext(current, backStack,
                                    forwardStack, queue);
                if (next == -1) {
                    System.out.println("No more questions.");
                    submit = true;
                } else {
                    current = next;
                }
            }
            // Return to the previous question
            else if (input.equals("P")) {
                if (backStack.isEmpty()) {
                    System.out.println("No previous question!");
                } else {
                    forwardStack.push(current);
                    current = backStack.pop();
                }
            }
            // Submit the quiz
            else if (input.equals("S")) {
                submit = true;
            } else {
                System.out.println("Invalid input. Try again.");
            }
        }

        int score = 0;

        for (int i = 0; i < n; i++) {
            if (answers[i] != -1 &&
                answers[i] == questions[i].correctAnswer - 'A') {
                score++;
            }
        }

        // Display the final result and answer review
        System.out.println("\n========== QUIZ RESULT ==========");
        System.out.println("Participant: " + name);
        System.out.println("Score: " + score + "/" + n);
        System.out.printf("Percentage: %.2f%%%n",
                          score * 100.0 / n);

        System.out.println("\n========== ANSWER REVIEW ==========");

        for (int i = 0; i < n; i++) {
            System.out.println("\nQ" + (i + 1) + ": "
                               + questions[i].question);

            if (answers[i] == -1) {
                System.out.println("Your answer: Not answered");
            } else {
                System.out.println("Your answer: "
                    + questions[i].options[answers[i]]);
            }

            char correct = questions[i].correctAnswer;
            System.out.println("Correct answer: " + correct + ". "
                + questions[i].options[correct - 'A']);
        }

        return new ScoreRecord(name, score, n);
    }

    // Gets the next question from forward history or the queue
    static int moveNext(int current, ArrayStack backStack,
                        ArrayStack forwardStack, ArrayQueue queue) {
        if (!forwardStack.isEmpty()) {
            backStack.push(current);
            return forwardStack.pop();
        }

        if (!queue.isEmpty()) {
            backStack.push(current);
            return queue.dequeue();
        }

        return -1;
    }

    // Sorts participants by score using Bubble Sort
    static void showLeaderboard(ScoreRecord[] records, int count) {
        if (count == 0) {
            System.out.println("\nNo quiz results available yet.");
            return;
        }

        // Copy records so the original order is preserved
        ScoreRecord[] sorted = new ScoreRecord[count];

        for (int i = 0; i < count; i++) {
            sorted[i] = records[i];
        }

        // Bubble Sort: highest score first
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (sorted[j].score < sorted[j + 1].score) {
                    ScoreRecord temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }

        System.out.println("\n========== LEADERBOARD ==========");
        System.out.printf("%-6s %-20s %-10s%n",
                          "Rank", "Name", "Score");

        for (int i = 0; i < count; i++) {
            System.out.printf("%-6d %-20s %d/%d%n",
                i + 1, sorted[i].name,
                sorted[i].score, sorted[i].total);
        }
    }

    public static void main(String[] args) {
        ScoreRecord[] records = new ScoreRecord[100];
        int count = 0;

        while (true) {
            System.out.println("\n===== ONLINE QUIZ SYSTEM =====");
            System.out.println("1. Start Quiz");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    if (count == records.length) {
                        System.out.println("Result storage is full!");
                    } else {
                        records[count] = startQuiz();
                        count++;
                    }
                    break;

                case "2":
                    showLeaderboard(records, count);
                    break;

                case "3":
                    System.out.println("Thank you for using the Quiz System!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid menu choice!");
            }
        }
    }
}
