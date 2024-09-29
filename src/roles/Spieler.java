package roles;

public class Spieler {

    private String name;
    private String role;
    public boolean isAlive;
    public Spieler lover;

    public Spieler(String name, String role){
        this.name = name;
        this.role = role;
        this.isAlive = true;
        this.lover = null;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
