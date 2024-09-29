package roles;

public class Hure extends Spieler{
    private Spieler bootyCall;
    public Hure(String name){
        super(name, "roles.Hure");
        bootyCall = null;
    }

    public void setBootyCall(Spieler bootyCall) {
        this.bootyCall = bootyCall;
    }
}
