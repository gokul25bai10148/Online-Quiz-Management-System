import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
abstract class User {
    String username;
    String password;
    int score;
    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.score = 0;
    }
    abstract String getRole();
}
class Student extends User {
    Student(String username, String password) {
        super(username, password);
    }
    String getRole() {
        return "Student";
    }
}
class Admin extends User {
    Admin(String username, String password) {
        super(username, password);
    }
    String getRole() {
        return "Admin";
    }
}
class QuizQuestion {
    String question;
    String[] options;
    int correctAnswer;
    QuizQuestion(String question, String[] options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
}
class LoginException extends Exception {
    LoginException(String message) {
        super(message);
    }
}
class QuizTimer extends Thread {
    int seconds = 15;
    boolean running = true;
    public void run() {
        while (running && seconds > 0) {
            try {
                Thread.sleep(1000);
                seconds--;
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    void stopTimer() {
        running = false;
    }
}
public class QuizVerse {
    static Scanner input = new Scanner(System.in);
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<QuizQuestion> questions = new ArrayList<>();
    public static void main(String[] args) {
        createAdmin();
        loadQuestions();
        while (true) {
            System.out.println("\n========== QUIZVERSE ==========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(input.nextLine());
            if (choice == 1) {
                registerUser();
            } 
            else if (choice == 2) {
                loginUser();
            } 
            else if (choice == 3) {
                System.out.println("Thank you for using QuizVerse!");
                break;
            } 
            else {
                System.out.println("Please enter a valid choice.");
            }
        }
    }
    static void createAdmin() {
        users.add(new Admin("admin", "1234"));
    }
    static void registerUser() {
        System.out.println("\n----- Registration -----");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        for (User user : users) {
            if (user.username.equals(username)) {
                System.out.println("Username already exists.");
                return;
            }
        }
        users.add(new Student(username, password));
        System.out.println("Registration successful!");
    }
    static void loginUser() {
        System.out.println("\n----- Login -----");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();
        try {
            User user = checkLogin(username, password);
            if (user instanceof Admin) {
                adminMenu();
            } 
            else {
                studentMenu((Student) user);
            }
        } catch (LoginException e) {
            System.out.println(e.getMessage());
        }
    }
    static User checkLogin(String username, String password)
            throws LoginException {
        for (User user : users) {
            if (user.username.equals(username)
                    && user.password.equals(password)) {
                return user;
            }
        }
        throw new LoginException("Wrong username or password.");
    }
    static void studentMenu(Student student) {
        while (true) {
            System.out.println("\n----- Student Menu -----");
            System.out.println("1. Start Quiz");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(input.nextLine());
            if (choice == 1) {
                startQuiz(student);
            } 
            else if (choice == 2) {
                showLeaderboard();
            } 
            else if (choice == 3) {
                System.out.println("Logged out.");
                return;
            } 
            else {
                System.out.println("Invalid choice.");
            }
        }
    }
    static void adminMenu() {
        System.out.println("\n----- Admin Panel -----");
        System.out.println("Total questions: "
                + questions.size());
        System.out.println("\nQuestions available:");
        for (QuizQuestion q : questions) {
            System.out.println("- " + q.question);
        }
    }
    static void startQuiz(Student student) {
        ArrayList<QuizQuestion> quizQuestions =
                new ArrayList<>(questions);
        Collections.shuffle(quizQuestions);
        int score = 0;
        System.out.println("\n===== QUIZ STARTED =====");
        System.out.println("You have 15 seconds for each question.");
        for (int i = 0; i < quizQuestions.size(); i++) {
            QuizQuestion q = quizQuestions.get(i);
            System.out.println("\nQuestion " + (i + 1));
            System.out.println(q.question);
            for (int j = 0; j < q.options.length; j++) {
                System.out.println((j + 1) + ". "
                        + q.options[j]);
            }
            QuizTimer timer = new QuizTimer();
            timer.start();
            long startTime = System.currentTimeMillis();
            System.out.print("Your answer: ");
            int answer;
            try {
                answer = Integer.parseInt(input.nextLine()) - 1;
            } catch (Exception e) {
                answer = -1;
            }
            long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
            timer.stopTimer();
            if (timeTaken >= 15) {
                System.out.println("Time is up!");
            } 
            else if (answer == q.correctAnswer) {
                int points = 100 - ((int) timeTaken * 5);
                if (points < 20) {
                    points = 20;
                }
                score += points;
                System.out.println("Correct answer!");
                System.out.println("You got " + points + " points.");
            } 
            else {
                System.out.println("Wrong answer!");
            }
        }
        student.score += score;
        System.out.println("\n===== QUIZ FINISHED =====");
        System.out.println("Score in this quiz: " + score);
        System.out.println("Your total score: "
                + student.score);
    }
    static void showLeaderboard() {
        ArrayList<Student> students = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        Collections.sort(students,
                (a, b) -> b.score - a.score);
        System.out.println("\n===== LEADERBOARD =====");
        if (students.size() == 0) {
            System.out.println("No students have played yet.");
            return;
        }
        int position = 1;
        for (Student student : students) {
            System.out.println(position + ". "
                    + student.username
                    + " - "
                    + student.score);
            position++;
        }
    }
    static void loadQuestions() {
        questions.add(new QuizQuestion(
                "Which keyword is used for inheritance in Java?",
                new String[]{
                        "implements",
                        "extends",
                        "inherit",
                        "super"
                },
                1
        ));
        questions.add(new QuizQuestion(
                "What does JVM stand for?",
                new String[]{
                        "Java Virtual Machine",
                        "Java Variable Machine",
                        "Java Visual Machine",
                        "Java Verified Machine"
                },
                0
        ));
        questions.add(new QuizQuestion(
                "Which keyword is used to create an object?",
                new String[]{
                        "class",
                        "object",
                        "new",
                        "create"
                },
                2
        ));
        questions.add(new QuizQuestion(
                "Which OOP concept hides data?",
                new String[]{
                        "Inheritance",
                        "Polymorphism",
                        "Encapsulation",
                        "Abstraction"
                },
                2
        ));
        questions.add(new QuizQuestion(
                "Which method is used to start a thread?",
                new String[]{
                        "run()",
                        "start()",
                        "begin()",
                        "execute()"
                },
                1
        ));
    }
}