package roles;

public class Hexe extends Spieler{
    private boolean healed;
    private boolean killed;
    public Hexe(String name){
        super(name, "roles.Hexe");
    }

    public void healingPotion(Spieler sp){
        sp.isAlive = true;
    }
    public void poison(Spieler sp){
        sp.isAlive = false;
    }
}
