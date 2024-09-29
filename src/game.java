import roles.*;

import java.util.HashMap;

public class game {

    public game(){
        Armor armor = new Armor("Sp1");
        Seherin seherin = new Seherin("Sp2");
        Hexe hexe = new Hexe("Sp3");
        Hure hure = new Hure("Sp8");
        Werwolf werwolf =new Werwolf("Sp4");
        Werwolf werwolf2 =new Werwolf("Sp5");
        Dorfbewohner dorfbewohner = new Dorfbewohner("Sp6");
        Dorfbewohner dorfbewohner2 = new Dorfbewohner("Sp7");
    }
    public void run(){

    }

    public static void main(String[] args) {
        Armor armor = new Armor("Sp1");
        Seherin seherin = new Seherin("Sp2");
        Hexe hexe = new Hexe("Sp3");
        Hure hure = new Hure("Sp8");
        Werwolf werwolf = new Werwolf("Sp4");
        Werwolf werwolf2 = new Werwolf("Sp5");
        Dorfbewohner dorfbewohner = new Dorfbewohner("Sp6");
        Dorfbewohner dorfbewohner2 = new Dorfbewohner("Sp7");

        HashMap<String, Spieler> spielerListe = new HashMap<String, Spieler>();
        spielerListe.put(armor.getName(), armor);
        spielerListe.put(seherin.getName(), seherin);
        spielerListe.put(hure.getName(), hure);
        spielerListe.put(werwolf.getName(), werwolf);
        spielerListe.put(werwolf2.getName(), werwolf2);
        spielerListe.put(dorfbewohner.getName(), dorfbewohner);
        spielerListe.put(dorfbewohner2.getName(), dorfbewohner2);

        // roles.Armor verliebt zwei roles.Spieler
        System.out.println("roles.Armor macht Sp3 und Sp2 verliebt.");
        armor.verlieben(spielerListe.get("Sp1"), spielerListe.get("Sp2"));

        // roles.Hure setzt ihren BootyCall
        System.out.println("roles.Hure setzt BootyCall auf Sp3.");
        hure.setBootyCall(spielerListe.get("Sp3"));

        // roles.Seherin sieht einen roles.Spieler
        System.out.println("roles.Seherin sieht roles.Spieler Sp1.");
        seherin.sehen(spielerListe.get("Sp1"));

        Spieler gewaehlter1 = new Spieler("dummy", "roles.Dorfbewohner");
        Spieler gewaehlter2 = new Spieler("dummy2", "roles.Dorfbewohner");
        while (!gewaehlter1.getName().equals(gewaehlter2.getName())) {

            System.out.println("Werwolf1 stimmt ab für Sp3.");
            gewaehlter1 = werwolf.abstimmen(spielerListe.get("Sp3"));
            System.out.println("Werwolf2 stimmt ab für Sp1.");
            gewaehlter2 = werwolf2.abstimmen(spielerListe.get("Sp1"));

            System.out.println("neuwahlen\n");

            System.out.println("Werwolf1 stimmt ab für Sp1.");
            gewaehlter1 = werwolf.abstimmen(spielerListe.get("Sp1"));
            System.out.println("Werwolf2 stimmt ab für Sp1.");
            gewaehlter2 = werwolf2.abstimmen(spielerListe.get("Sp1"));
        }

        System.out.println(gewaehlter1.getName() + " wurde als Opfer gewählt und ist nun tot.");
        spielerListe.get(gewaehlter1.getName()).isAlive = false;

        // roles.Hexe rettet das Opfer
        System.out.println("roles.Hexe verwendet den Heiltrank auf roles.Spieler Sp1.");
        hexe.healingPotion(spielerListe.get("Sp1"));
    }
}
