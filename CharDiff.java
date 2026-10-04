import java.util.*;

public class CharDiff {

    // ANSI color codes
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String RESET = "\u001B[0m";

    static class Edit {
        String type;
        char ch;

        Edit(String type, char ch) {
            this.type = type;
            this.ch = ch;
        }
    }

    // Myers character-level diff
    static List<Edit> myersCharDiff(String A, String B) {

        int N = A.length();
        int M = B.length();

        int max = N + M;
        int offset = max;

        List<int[]> trace = new ArrayList<>();

        int[] V = new int[2 * max + 1];

        for (int D = 0; D <= max; D++) {

            trace.add(V.clone());

            for (int k = -D; k <= D; k += 2) {

                int x;

                if (k == -D ||
                    (k != D &&
                     V[offset + k - 1] <
                     V[offset + k + 1])) {

                    x = V[offset + k + 1];

                } else {

                    x = V[offset + k - 1] + 1;
                }

                int y = x - k;

                while (x < N &&
                       y < M &&
                       A.charAt(x) == B.charAt(y)) {

                    x++;
                    y++;
                }

                V[offset + k] = x;

                if (x >= N && y >= M) {

                    return backtrack(
                        A,
                        B,
                        trace,
                        D,
                        offset
                    );
                }
            }
        }

        return new ArrayList<>();
    }

    static List<Edit> backtrack(
            String A,
            String B,
            List<int[]> trace,
            int D,
            int offset) {

        List<Edit> result = new ArrayList<>();

        int x = A.length();
        int y = B.length();

        for (int d = D; d > 0; d--) {

            int[] V = trace.get(d);

            int k = x - y;

            int prevK;

            if (k == -d ||
                (k != d &&
                 V[offset + k - 1] <
                 V[offset + k + 1])) {

                prevK = k + 1;

            } else {

                prevK = k - 1;
            }

            int prevX = V[offset + prevK];
            int prevY = prevX - prevK;

            // Keep matching characters
            while (x > prevX && y > prevY) {

                result.add(
                    new Edit(
                        "KEEP",
                        A.charAt(x - 1)
                    )
                );

                x--;
                y--;
            }

            // Insert
            if (x == prevX) {

                result.add(
                    new Edit(
                        "INSERT",
                        B.charAt(y - 1)
                    )
                );

                y--;

            } else {

                // Delete
                result.add(
                    new Edit(
                        "DELETE",
                        A.charAt(x - 1)
                    )
                );

                x--;
            }
        }

        while (x > 0 && y > 0) {

            result.add(
                new Edit(
                    "KEEP",
                    A.charAt(x - 1)
                )
            );

            x--;
            y--;
        }

        Collections.reverse(result);

        return result;
    }

    static void printHighlightedDiff(String A, String B) {

        List<Edit> result =
            myersCharDiff(A, B);

        StringBuilder oldLine =
            new StringBuilder();

        StringBuilder newLine =
            new StringBuilder();

        for (Edit edit : result) {

            if (edit.type.equals("KEEP")) {

                oldLine.append(edit.ch);
                newLine.append(edit.ch);

            } else if (edit.type.equals("DELETE")) {

                oldLine.append(
                    RED + edit.ch + RESET
                );

            } else {

                newLine.append(
                    GREEN + edit.ch + RESET
                );
            }
        }

        System.out.println("- " + oldLine);
        System.out.println("+ " + newLine);
    }

    public static void main(String[] args) {

        if (args.length < 2) {

            System.out.println(
                "Usage: java CharDiff \"old text\" \"new text\""
            );

            return;
        }

        String A = args[0];
        String B = args[1];

        printHighlightedDiff(A, B);
    }
}