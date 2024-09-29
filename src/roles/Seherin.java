package roles;

public class Seherin extends Spieler{
    public Seherin(String name){
        super(name, "roles.Seherin");
    }
    public String sehen(Spieler sp){
        return sp.getRole();
    }
}
