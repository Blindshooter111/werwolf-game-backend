package roles;

public class Armor extends Spieler{
    public Armor(String name){
        super(name, "roles.Armor");
    }

    public void verlieben(Spieler sp1, Spieler sp2){
        sp1.lover = sp2;
        sp2.lover = sp1;
    }
}
