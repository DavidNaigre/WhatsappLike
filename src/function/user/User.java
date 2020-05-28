package function.user;

public class User {
    private static String identifiant, mail, id;

    public static void setId(String id) {
        User.id = id;
    }
    public static void setIdentifiant(String identifiant) {
        User.identifiant = identifiant;
    }
    public static void setMail(String mail) {
        User.mail = mail;
    }
    public static String getId() {
        return id;
    }
    public static String getIdentifiant() {
        return identifiant;
    }
    public static String getMail() {
        return mail;
    }
}
