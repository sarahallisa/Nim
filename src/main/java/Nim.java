import java.util.*;

class Move {
    final int row, number;
    static Move of(int row, int number) {
        return new Move(row, number);
    }
    private Move(int row, int number) {
        if (row < 0 || number < 1) throw new IllegalArgumentException();
        this.row = row;
        this.number = number;
    }
    public String toString() {
        return "(" + row + ", " + number + ")";
    }
}

interface NimInterface {
    static boolean isWinning(int... numbers) {
        return Arrays.stream(numbers).reduce(0, (i, j) -> i ^ j) != 0;
    }
    NimInterface play(Move... moves);
    Move nimPerfect();
    boolean historyEmpty();
    NimInterface undo();
    boolean isGameOver();
    String toString();
}

class Nim implements NimInterface {
    private Random r = new Random();
    int[] rows;
    static Stack<Move> history = new Stack<>();

    Move m;
    public static Nim of(int... rows) {
        return new Nim(rows);
    }
    private Nim(int... rows) {
        this.rows = Arrays.copyOf(rows, rows.length);
    }
    private Nim play(Move m) {
        Nim nim = Nim.of(rows);
        nim.rows[m.row] -= m.number;
        return nim;
    }
    public Nim play(Move... moves) {
        Nim nim = this;
        for(Move m : moves) {
            nim = nim.play(m);
            history.push(m);
        }
        assert !nim.historyEmpty();
        return nim;
    }

    public boolean historyEmpty() {
        return history.stream().allMatch(Objects::isNull);
    }

    public Nim undo() {
        Nim nim = Nim.of(rows);
        assert !nim.historyEmpty();
        Move m = history.pop();
        nim.rows[m.row] += m.number;
        return nim;
    }

    public Move randomMove() {
        int row;
        do {
            row = r.nextInt(rows.length);
        } while (rows[row] == 0);
        int number = r.nextInt(rows[row]) + 1;
        return Move.of(row, number);
    }

    /**
     * Use the minimax-Methode.
     * this = take the current situation of the Nim.
     * 0 = start at the beginning of the Nim tree-Graph.
     * true = to go directly into the max-Methode instead of the min-Methode.
     * @return
     */
    public Move bestMove() {
        minimax(this, 0, true);
        return m;
    }

    /**
     * Herr Herzbergs BestMove-Methode.
     * @return
     */
    public Move nimPerfect() {
        if (!NimInterface.isWinning(rows)) return randomMove();
        Move m;
        do {
            m = randomMove();
        } while(NimInterface.isWinning(play(m).rows));
        return m;
    }

    /**
     * To generate every situation of the Nim from the tree-Graph.
     * @return
     */
    public List<Move> generateMove() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) { // row
            for (int j = 1; j <= rows[i]; j++) { // count
                moves.add(Move.of(i,j));
            }
        }
        return moves;
    }
    public int[] getRows() {
        return rows;
    }
    public int minimax(Nim nim, int depth, boolean isMaximizing) {
        int bestValue;

        /**
         * To check if they win.
         * -1 : lost
         * 1 : won
         */
        if (nim.isGameOver()) {
            return isMaximizing ? -1 : 1;
        }
        if (isMaximizing) {
            bestValue = Integer.MIN_VALUE;
            for (Move move : nim.generateMove()) {
                Nim temp = nim.play(move);

                // Monitoring
                System.out.println("Max Move: " + temp);
                int value = minimax(temp, depth + 1, false);
                if (value > bestValue) {
                    bestValue = value;
                    if (depth == 0) { // 0 as desired depth
                        m = move;
                    }
                }
            }
            return bestValue;
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Move move : nim.generateMove()) {
                Nim temp = nim.play(move);

                // Monitoring
                System.out.println("Min Move: " + temp);
                int value = minimax(temp, depth + 1, true);
                if (value < bestValue) {
                    bestValue = value;
                }
            }
            return bestValue;
        }
    }

   public boolean versusBest(Nim nim, boolean player) {
        if (nim.isGameOver()) {
            return !player;
        }
        Nim test = Nim.of(nim.getRows());
        if (player) {
            test = test.play(test.bestMove());
            return versusBest(test, false);
        }
        else {
           test = test.play(test.nimPerfect());
           return versusBest(test, true);
        }
   }

   public static String whoWon(Nim nim) {
        String s = "";
        boolean winner = nim.versusBest(nim, true);
        if (winner) return s = "You won!";
        else return s = "Herzberg won!";
   }

   public static void play1000() {
        int i = 1000;
        int j = 0;
        int x = 0;
        while (i > 0) {
            Nim nim = Nim.of(randomSetup(3, 3, 3));
            if (NimInterface.isWinning(nim.getRows())) j++;
            if (nim.versusBest(nim, true)) x++;
            i--;
        }
        System.out.println("You won " + x + " times out of " + j);
   }
    public boolean isGameOver() {
        return Arrays.stream(rows).allMatch(n -> n == 0);
    }

    public String toString() {
        String s = "";
        for (int n : rows) s += "\n" + "I ".repeat(n);
        return s;
    }

    static int[] randomSetup(int... maxN) {
        Random r = new Random();
        int[] rows = new int[maxN.length];
        for (int i = 0; i < maxN.length; i++) {
            rows[i] = r.nextInt(maxN[i]) + 1;
        }
        return rows;

    }
}