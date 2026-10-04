import java.util.*;
import java.nio.file.*;

public class Diff {

    // =========================
    // COLORS
    // =========================

    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";

    static final String DARK_RED = "\u001B[38;5;124m";
    static final String DARK_GREEN = "\u001B[38;5;28m";

    static final String RESET = "\u001B[0m";


    // =========================
    // LINE EDIT
    // =========================

    static class Edit {

        String type;
        String line;

        Edit(String type, String line) {
            this.type = type;
            this.line = line;
        }
    }


    // =========================
    // PART A: MYERS LINE DIFF
    // =========================

    static List<Edit> myersDiff(
            List<String> A,
            List<String> B) {

        int N = A.size();
        int M = B.size();

        int max = N + M;

        if (max == 0) {
            return new ArrayList<>();
        }

        int offset = max;

        int[] V = new int[2 * max + 1];

        List<int[]> trace = new ArrayList<>();


        for (int D = 0; D <= max; D++) {

            trace.add(V.clone());

            for (int k = -D; k <= D; k += 2) {

                int x;

                if (k == -D ||
                    (k != D &&
                     V[offset + k - 1]
                     < V[offset + k + 1])) {

                    x = V[offset + k + 1];

                } else {

                    x = V[offset + k - 1] + 1;
                }


                int y = x - k;


                while (x < N &&
                       y < M &&
                       A.get(x).equals(B.get(y))) {

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


    // =========================
    // LINE BACKTRACK
    // =========================

    static List<Edit> backtrack(
            List<String> A,
            List<String> B,
            List<int[]> trace,
            int D,
            int offset) {

        List<Edit> result =
            new ArrayList<>();

        int x = A.size();
        int y = B.size();


        for (int d = D; d > 0; d--) {

            int[] V = trace.get(d);

            int k = x - y;

            int prevK;


            if (k == -d ||
                (k != d &&
                 V[offset + k - 1]
                 < V[offset + k + 1])) {

                prevK = k + 1;

            } else {

                prevK = k - 1;
            }


            int prevX =
                V[offset + prevK];

            int prevY =
                prevX - prevK;


            // KEEP
            while (x > prevX &&
                   y > prevY) {

                result.add(
                    new Edit(
                        "KEEP",
                        A.get(x - 1)
                    )
                );

                x--;
                y--;
            }


            // INSERT
            if (x == prevX) {

                result.add(
                    new Edit(
                        "INSERT",
                        B.get(y - 1)
                    )
                );

                y--;

            } else {

                // DELETE
                result.add(
                    new Edit(
                        "DELETE",
                        A.get(x - 1)
                    )
                );

                x--;
            }
        }


        // Remaining KEEP
        while (x > 0 &&
               y > 0) {

            result.add(
                new Edit(
                    "KEEP",
                    A.get(x - 1)
                )
            );

            x--;
            y--;
        }


        // Remaining DELETE
        while (x > 0) {

            result.add(
                new Edit(
                    "DELETE",
                    A.get(x - 1)
                )
            );

            x--;
        }


        // Remaining INSERT
        while (y > 0) {

            result.add(
                new Edit(
                    "INSERT",
                    B.get(y - 1)
                )
            );

            y--;
        }


        Collections.reverse(result);

        return result;
    }


    // =========================
    // CHARACTER EDIT
    // =========================

    static class CharEdit {

        String type;
        char ch;

        CharEdit(String type, char ch) {
            this.type = type;
            this.ch = ch;
        }
    }


    // =========================
    // PART B: MYERS CHARACTER DIFF
    // =========================

    static List<CharEdit> myersCharDiff(
            String A,
            String B) {

        int N = A.length();
        int M = B.length();

        int max = N + M;

        if (max == 0) {
            return new ArrayList<>();
        }

        int offset = max;

        int[] V = new int[2 * max + 1];

        List<int[]> trace =
            new ArrayList<>();


        for (int D = 0; D <= max; D++) {

            trace.add(V.clone());

            for (int k = -D; k <= D; k += 2) {

                int x;


                if (k == -D ||
                    (k != D &&
                     V[offset + k - 1]
                     < V[offset + k + 1])) {

                    x = V[offset + k + 1];

                } else {

                    x = V[offset + k - 1] + 1;
                }


                int y = x - k;


                while (x < N &&
                       y < M &&
                       A.charAt(x)
                       == B.charAt(y)) {

                    x++;
                    y++;
                }


                V[offset + k] = x;


                if (x >= N &&
                    y >= M) {

                    return charBacktrack(
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


    // =========================
    // CHARACTER BACKTRACK
    // =========================

    static List<CharEdit> charBacktrack(
            String A,
            String B,
            List<int[]> trace,
            int D,
            int offset) {

        List<CharEdit> result =
            new ArrayList<>();

        int x = A.length();
        int y = B.length();


        for (int d = D; d > 0; d--) {

            int[] V = trace.get(d);

            int k = x - y;

            int prevK;


            if (k == -d ||
                (k != d &&
                 V[offset + k - 1]
                 < V[offset + k + 1])) {

                prevK = k + 1;

            } else {

                prevK = k - 1;
            }


            int prevX =
                V[offset + prevK];

            int prevY =
                prevX - prevK;


            // KEEP CHARACTER
            while (x > prevX &&
                   y > prevY) {

                result.add(
                    new CharEdit(
                        "KEEP",
                        A.charAt(x - 1)
                    )
                );

                x--;
                y--;
            }


            // INSERT CHARACTER
            if (x == prevX) {

                result.add(
                    new CharEdit(
                        "INSERT",
                        B.charAt(y - 1)
                    )
                );

                y--;

            } else {

                // DELETE CHARACTER
                result.add(
                    new CharEdit(
                        "DELETE",
                        A.charAt(x - 1)
                    )
                );

                x--;
            }
        }


        // Remaining KEEP
        while (x > 0 &&
               y > 0) {

            result.add(
                new CharEdit(
                    "KEEP",
                    A.charAt(x - 1)
                )
            );

            x--;
            y--;
        }


        // Remaining DELETE
        while (x > 0) {

            result.add(
                new CharEdit(
                    "DELETE",
                    A.charAt(x - 1)
                )
            );

            x--;
        }


        // Remaining INSERT
        while (y > 0) {

            result.add(
                new CharEdit(
                    "INSERT",
                    B.charAt(y - 1)
                )
            );

            y--;
        }


        Collections.reverse(result);

        return result;
    }


    // =========================
    // DELETED LINE HIGHLIGHT
    // =========================

    static String highlightDeletedLine(
            String oldLine,
            String newLine) {

        List<CharEdit> edits =
            myersCharDiff(
                oldLine,
                newLine
            );

        StringBuilder result =
            new StringBuilder();


        for (CharEdit edit : edits) {

            // Only KEEP and DELETE
            // belong on deleted line

            if (edit.type.equals("DELETE")) {

                result.append(DARK_RED);
                result.append(edit.ch);
                result.append(RESET);

            } else if (edit.type.equals("KEEP")) {

                result.append(edit.ch);
            }
        }


        return RED +
               "- " +
               result +
               RESET;
    }


    // =========================
    // INSERTED LINE HIGHLIGHT
    // =========================

    static String highlightInsertedLine(
            String oldLine,
            String newLine) {

        List<CharEdit> edits =
            myersCharDiff(
                oldLine,
                newLine
            );

        StringBuilder result =
            new StringBuilder();


        for (CharEdit edit : edits) {

            // Only KEEP and INSERT
            // belong on inserted line

            if (edit.type.equals("INSERT")) {

                result.append(DARK_GREEN);
                result.append(edit.ch);
                result.append(RESET);

            } else if (edit.type.equals("KEEP")) {

                result.append(edit.ch);
            }
        }


        return GREEN +
               "+ " +
               result +
               RESET;
    }


    // =========================
    // MAIN
    // =========================

    public static void main(
            String[] args) throws Exception {

        if (args.length < 2) {

            System.out.println(
                "Usage: java Diff fileA fileB"
            );

            return;
        }


        List<String> A =
            Files.readAllLines(
                Paths.get(args[0])
            );

        List<String> B =
            Files.readAllLines(
                Paths.get(args[1])
            );


        List<Edit> result =
            myersDiff(A, B);


        for (int i = 0;
             i < result.size();
             i++) {

            Edit current =
                result.get(i);


            // =========================
            // KEEP
            // =========================

            if (current.type.equals("KEEP")) {

                System.out.println(
                    "  " +
                    current.line
                );
            }


            // =========================
            // DELETE + INSERT
            // =========================

            else if (
                current.type.equals("DELETE") &&
                i + 1 < result.size() &&
                result.get(i + 1)
                      .type
                      .equals("INSERT")
            ) {

                Edit deleted =
                    current;

                Edit inserted =
                    result.get(i + 1);


                // Deleted line
                System.out.println(
                    highlightDeletedLine(
                        deleted.line,
                        inserted.line
                    )
                );


                // Inserted line
                System.out.println(
                    highlightInsertedLine(
                        deleted.line,
                        inserted.line
                    )
                );


                // INSERT already printed
                i++;
            }


            // =========================
            // DELETE ONLY
            // =========================

            else if (
                current.type.equals("DELETE")
            ) {

                System.out.println(
                    RED +
                    "- " +
                    current.line +
                    RESET
                );
            }


            // =========================
            // INSERT ONLY
            // =========================

            else {

                System.out.println(
                    GREEN +
                    "+ " +
                    current.line +
                    RESET
                );
            }
        }
    }
}