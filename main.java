import java.util.*;

class Graf {
    private Map<Character, Map<Character, Integer>> wierzcholki;

    public Graf() {
        this.wierzcholki = new HashMap<>();
    }

    public void dodajWierzcholek(char wierzcholek) {
        if (!wierzcholki.containsKey(wierzcholek)) {
            wierzcholki.put(wierzcholek, new HashMap<>());
        }
    }

    public void dodajKrawedz(char wierzcholek1, char wierzcholek2, int waga) {
        dodajWierzcholek(wierzcholek1);
        dodajWierzcholek(wierzcholek2);
        wierzcholki.get(wierzcholek1).put(wierzcholek2, waga);
        wierzcholki.get(wierzcholek2).put(wierzcholek1, waga);
    }

    public Map<Character, Integer> getSasiadow(char wierzcholek) {
        return wierzcholki.getOrDefault(wierzcholek, new HashMap<>());
    }

    // Algorytm Kruskala
    public List<Krawedz> kruskal() {
        List<Krawedz> krawedzie = new ArrayList<>();
        for (Map.Entry<Character, Map<Character, Integer>> entry : wierzcholki.entrySet()) {
            char wierzcholek1 = entry.getKey();
            for (Map.Entry<Character, Integer> sasiad : entry.getValue().entrySet()) {
                char wierzcholek2 = sasiad.getKey();
                int waga = sasiad.getValue();
                if (wierzcholek1 < wierzcholek2) {
                    krawedzie.add(new Krawedz(wierzcholek1, wierzcholek2, waga));
                }
            }
        }

        Collections.sort(krawedzie);

        UnionFind uf = new UnionFind(wierzcholki.keySet());
        List<Krawedz> mst = new ArrayList<>();

        for (Krawedz krawedz : krawedzie) {
            if (uf.find(krawedz.wierzcholek1) != uf.find(krawedz.wierzcholek2)) {
                uf.union(krawedz.wierzcholek1, krawedz.wierzcholek2);
                mst.add(krawedz);
            }
        }
        return mst;
    }

    // Algorytm Prima
    public List<Krawedz> prim() {
        PriorityQueue<Krawedz> pq = new PriorityQueue<>();
        Set<Character> visited = new HashSet<>();
        List<Krawedz> mst = new ArrayList<>();

        char start = wierzcholki.keySet().iterator().next();
        visited.add(start);
        for (Map.Entry<Character, Integer> entry : wierzcholki.get(start).entrySet()) {
            pq.add(new Krawedz(start, entry.getKey(), entry.getValue()));
        }

        while (!pq.isEmpty()) {
            Krawedz krawedz = pq.poll();
            if (!visited.contains(krawedz.wierzcholek2)) {
                visited.add(krawedz.wierzcholek2);
                mst.add(krawedz);

                for (Map.Entry<Character, Integer> entry : wierzcholki.get(krawedz.wierzcholek2).entrySet()) {
                    if (!visited.contains(entry.getKey())) {
                        pq.add(new Krawedz(krawedz.wierzcholek2, entry.getKey(), entry.getValue()));
                    }
                }
            }
        }
        return mst;
    }

    // Znajdowanie minimalnej liczby chromatycznej
    public int minimalnaLiczbaChromatyczna() {
        Map<Character, Integer> kolorowanie = new HashMap<>();
        for (Character wierzcholek : wierzcholki.keySet()) {
            kolorowanie.put(wierzcholek, -1);
        }

        for (Character wierzcholek : wierzcholki.keySet()) {
            boolean[] dostepneKolory = new boolean[wierzcholki.size()];
            Arrays.fill(dostepneKolory, true);

            for (Character sasiad : wierzcholki.get(wierzcholek).keySet()) {
                if (kolorowanie.get(sasiad) != -1) {
                    dostepneKolory[kolorowanie.get(sasiad)] = false;
                }
            }

            int kolor;
            for (kolor = 0; kolor < dostepneKolory.length; kolor++) {
                if (dostepneKolory[kolor]) {
                    break;
                }
            }
            kolorowanie.put(wierzcholek, kolor);
        }

        int maksymalnyKolor = 0;
        for (int kolor : kolorowanie.values()) {
            if (kolor > maksymalnyKolor) {
                maksymalnyKolor = kolor;
            }
        }
        return maksymalnyKolor + 1;
    }

    class Krawedz implements Comparable<Krawedz> {
        char wierzcholek1, wierzcholek2;
        int waga;

        Krawedz(char wierzcholek1, char wierzcholek2, int waga) {
            this.wierzcholek1 = wierzcholek1;
            this.wierzcholek2 = wierzcholek2;
            this.waga = waga;
        }

        @Override
        public int compareTo(Krawedz inna) {
            return Integer.compare(this.waga, inna.waga);
        }

        @Override
        public String toString() {
            return wierzcholek1 + " - " + wierzcholek2 + ": " + waga;
        }
    }

    class UnionFind {
        private Map<Character, Character> parent;
        private Map<Character, Integer> rank;

        UnionFind(Set<Character> wierzcholki) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for (char wierzcholek : wierzcholki) {
                parent.put(wierzcholek, wierzcholek);
                rank.put(wierzcholek, 0);
            }
        }

        public char find(char wierzcholek) {
            if (parent.get(wierzcholek) != wierzcholek) {
                parent.put(wierzcholek, find(parent.get(wierzcholek)));
            }
            return parent.get(wierzcholek);
        }

        public void union(char wierzcholek1, char wierzcholek2) {
            char root1 = find(wierzcholek1);
            char root2 = find(wierzcholek2);
            if (root1 != root2) {
                if (rank.get(root1) > rank.get(root2)) {
                    parent.put(root2, root1);
                } else if (rank.get(root1) < rank.get(root2)) {
                    parent.put(root1, root2);
                } else {
                    parent.put(root2, root1);
                    rank.put(root1, rank.get(root1) + 1);
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Graf graf = new Graf();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Podaj liczbę krawędzi:");
        int liczbaKrawedzi = scanner.nextInt();

        System.out.println("Podaj krawędzie w formacie: wierzcholek1 wierzcholek2 waga");
        for (int i = 0; i < liczbaKrawedzi; i++) {
            char wierzcholek1 = scanner.next().charAt(0);
            char wierzcholek2 = scanner.next().charAt(0);
            int waga = scanner.nextInt();
            graf.dodajKrawedz(wierzcholek1, wierzcholek2, waga);
        }

        System.out.println("Minimalne Drzewo Rozpinające (Kruskal):");
        List<Graf.Krawedz> mstKruskal = graf.kruskal();
        for (Graf.Krawedz krawedz : mstKruskal) {
            System.out.println(krawedz);
        }

        System.out.println("Minimalne Drzewo Rozpinające (Prim):");
        List<Graf.Krawedz> mstPrim = graf.prim();
        for (Graf.Krawedz krawedz : mstPrim) {
            System.out.println(krawedz);
        }

        int liczbaChromatyczna = graf.minimalnaLiczbaChromatyczna();
        System.out.println("Minimalna liczba chromatyczna: " + liczbaChromatyczna);

        scanner.close();
    }
}
