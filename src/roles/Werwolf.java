package roles;

public class Werwolf extends Spieler{
    public Werwolf(String name){
        super(name, "roles.Werwolf");
    }

    public Spieler abstimmen (Spieler sp){
        return sp;
    }
}
