package mainpackage;
public abstract class Users {
    protected String username;
    protected String password;
    protected String name;
    protected String surname;
    protected int age;
    protected static int userCounter = 0;

    public Users(String username, String password, String name, String surname, int age) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.age = age;
        Users.userCounter++;
        System.out.println(userCounter + " users created");
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public static int getUserCounter() { return userCounter; }

    // Basic methods
    public void login() {
        System.out.println("User " + username + " logged in");
    }

    public void logout() {
        System.out.println("User " + username + " logged out");
    }
}