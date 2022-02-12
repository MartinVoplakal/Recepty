package cz.voplakal.recepty;

public class Testovaci {
    public Testovaci(String jmeno) {
        this.jmeno = jmeno;
    }

    String jmeno;

    public String getJmeno() {
        return jmeno;
    }

//    public static void main(String[] args) {
//        ArrayList<String> arrayList = new ArrayList<String>();
//        arrayList.add("Ahoj");
//        arrayList.add("aaaaaabbbbccc");
//        for (String t : arrayList) {
//            System.out.println(t);
//        }
//        System.out.println("filter");
//        String filter = "ab";
//        for (int i = 0; i < arrayList.size(); i++) {
//            String recept = (String) arrayList.get(i);
//            if (!recept.toLowerCase().contains(filter)) {
//                System.out.println(recept);
//            }
//        }
//    }
}
