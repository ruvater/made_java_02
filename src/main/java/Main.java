public class Main {
    public static void main(String[] args) {
        SimpleMap<String, Integer> map = new SimpleHashMap<>();

        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);

        System.out.println(map.get("2"));
        System.out.println(map.size());

        System.out.println(map.put("2", 4));
        System.out.println(map.size());

        System.out.println(map.remove("2"));
        System.out.println(map.size());
    }
}
