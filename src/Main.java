import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    enum Type {
        KEEP, DELETE, INSERT
    }

    static class Edit {
        Type type;
        int index;

        Edit(Type type, int index) {
            this.type = type;
            this.index = index;
        }
    }

    static class Line {
        byte[] bytes;

        Line(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    static List<Line> readLines(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        List<Line> lines = new ArrayList<>();

        int start = 0;

        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\n') {
                lines.add(new Line(Arrays.copyOfRange(data, start, i)));
                start = i + 1;
            }
        }

        if (start < data.length) {
            lines.add(new Line(Arrays.copyOfRange(data, start, data.length)));
        }

        return lines;
    }

    static boolean equal(Line a, Line b) {
        return Arrays.equals(a.bytes, b.bytes);
    }

    static List<Edit> myersLines(List<Line> a, List<Line> b) {
        int n = a.size();
        int m = b.size();

        int max = n + m;
        int offset = max + 1;

        int[] v = new int[2 * max + 3];
        Arrays.fill(v, 0);

        List<int[]> trace = new ArrayList<>();

        int finalD = 0;

        outer:
        for (int d = 0; d <= max; d++) {
            for (int k = -d; k <= d; k += 2) {
                int index = offset + k;
                int x;

                if (k == -d) {
                    x = v[index + 1];
                } else if (k == d) {
                    x = v[index - 1] + 1;
                } else if (v[index - 1] < v[index + 1]) {
                    x = v[index + 1];
                } else {
                    x = v[index - 1] + 1;
                }

                int y = x - k;

                while (x < n && y < m && equal(a.get(x), b.get(y))) {
                    x++;
                    y++;
                }

                v[index] = x;

                if (x >= n && y >= m) {
                    trace.add(Arrays.copyOf(v, v.length));
                    finalD = d;
                    break outer;
                }
            }

            trace.add(Arrays.copyOf(v, v.length));
        }

        List<Edit> reversed = new ArrayList<>();

        int x = n;
        int y = m;

        for (int d = finalD; d > 0; d--) {
            int[] previous = trace.get(d - 1);

            int k = x - y;
            int previousK;

            if (k == -d || (k != d
                    && previous[offset + k - 1] < previous[offset + k + 1])) {
                previousK = k + 1;
            } else {
                previousK = k - 1;
            }

            int previousX = previous[offset + previousK];
            int previousY = previousX - previousK;

            while (x > previousX && y > previousY) {
                reversed.add(new Edit(Type.KEEP, x - 1));
                x--;
                y--;
            }

            if (x == previousX) {
                reversed.add(new Edit(Type.INSERT, y - 1));
                y--;
            } else {
                reversed.add(new Edit(Type.DELETE, x - 1));
                x--;
            }
        }

        while (x > 0 && y > 0) {
            reversed.add(new Edit(Type.KEEP, x - 1));
            x--;
            y--;
        }

        while (x > 0) {
            reversed.add(new Edit(Type.DELETE, x - 1));
            x--;
        }

        while (y > 0) {
            reversed.add(new Edit(Type.INSERT, y - 1));
            y--;
        }

        List<Edit> result = new ArrayList<>();

        for (int i = reversed.size() - 1; i >= 0; i--) {
            result.add(reversed.get(i));
        }

        return result;
    }

    static void writeLine(BufferedOutputStream out, byte prefix, byte[] line)
            throws IOException {
        out.write(prefix);
        out.write(line);
        out.write('\n');
    }

    static int[] codePoints(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        return s.codePoints().toArray();
    }

    static List<Edit> myersChars(int[] a, int[] b) {
        int n = a.length;
        int m = b.length;

        int max = n + m;
        int offset = max + 1;

        int[] v = new int[2 * max + 3];
        Arrays.fill(v, 0);

        List<int[]> trace = new ArrayList<>();

        int finalD = 0;

        outer:
        for (int d = 0; d <= max; d++) {
            for (int k = -d; k <= d; k += 2) {
                int index = offset + k;
                int x;

                if (k == -d) {
                    x = v[index + 1];
                } else if (k == d) {
                    x = v[index - 1] + 1;
                } else if (v[index - 1] < v[index + 1]) {
                    x = v[index + 1];
                } else {
                    x = v[index - 1] + 1;
                }

                int y = x - k;

                while (x < n && y < m && a[x] == b[y]) {
                    x++;
                    y++;
                }

                v[index] = x;

                if (x >= n && y >= m) {
                    trace.add(Arrays.copyOf(v, v.length));
                    finalD = d;
                    break outer;
                }
            }

            trace.add(Arrays.copyOf(v, v.length));
        }

        List<Edit> reversed = new ArrayList<>();

        int x = n;
        int y = m;

        for (int d = finalD; d > 0; d--) {
            int[] previous = trace.get(d - 1);

            int k = x - y;
            int previousK;

            if (k == -d || (k != d
                    && previous[offset + k - 1] < previous[offset + k + 1])) {
                previousK = k + 1;
            } else {
                previousK = k - 1;
            }

            int previousX = previous[offset + previousK];
            int previousY = previousX - previousK;

            while (x > previousX && y > previousY) {
                reversed.add(new Edit(Type.KEEP, x - 1));
                x--;
                y--;
            }

            if (x == previousX) {
                reversed.add(new Edit(Type.INSERT, y - 1));
                y--;
            } else {
                reversed.add(new Edit(Type.DELETE, x - 1));
                x--;
            }
        }

        while (x > 0 && y > 0) {
            reversed.add(new Edit(Type.KEEP, x - 1));
            x--;
            y--;
        }

        while (x > 0) {
            reversed.add(new Edit(Type.DELETE, x - 1));
            x--;
        }

        while (y > 0) {
            reversed.add(new Edit(Type.INSERT, y - 1));
            y--;
        }

        List<Edit> result = new ArrayList<>();

        for (int i = reversed.size() - 1; i >= 0; i--) {
            result.add(reversed.get(i));
        }

        return result;
    }

    static String ranges(boolean[] changed) {
        List<String> result = new ArrayList<>();

        int i = 0;

        while (i < changed.length) {
            if (!changed[i]) {
                i++;
                continue;
            }

            int start = i;
            i++;

            while (i < changed.length && changed[i]) {
                i++;
            }

            result.add(start + "-" + i);
        }

        if (result.isEmpty()) {
            return ".";
        }

        return String.join(",", result);
    }

    static String highlightRanges(byte[] oldLine, byte[] newLine) {
        int[] oldChars = codePoints(oldLine);
        int[] newChars = codePoints(newLine);

        List<Edit> edits = myersChars(oldChars, newChars);

        boolean[] oldChanged = new boolean[oldChars.length];
        boolean[] newChanged = new boolean[newChars.length];

        for (Edit edit : edits) {
            if (edit.type == Type.DELETE) {
                oldChanged[edit.index] = true;
            } else if (edit.type == Type.INSERT) {
                newChanged[edit.index] = true;
            }
        }

        return ranges(oldChanged) + " | " + ranges(newChanged);
    }

    static void outputLines(
            BufferedOutputStream out,
            List<Line> a,
            List<Line> b,
            List<Edit> edits,
            boolean highlight
    ) throws IOException {

        int i = 0;

        while (i < edits.size()) {
            Edit edit = edits.get(i);

            if (edit.type == Type.KEEP) {
                writeLine(out, (byte) ' ', a.get(edit.index).bytes);
                i++;
                continue;
            }

            List<Edit> deletes = new ArrayList<>();
            List<Edit> inserts = new ArrayList<>();

            while (i < edits.size() && edits.get(i).type != Type.KEEP) {
                Edit current = edits.get(i);

                if (current.type == Type.DELETE) {
                    deletes.add(current);
                } else {
                    inserts.add(current);
                }

                i++;
            }

            for (Edit delete : deletes) {
                writeLine(out, (byte) '-', a.get(delete.index).bytes);
            }

            for (int j = 0; j < inserts.size(); j++) {
                Edit insert = inserts.get(j);

                writeLine(out, (byte) '+', b.get(insert.index).bytes);

                if (highlight && j < deletes.size()) {
                    String ranges = highlightRanges(
                            a.get(deletes.get(j).index).bytes,
                            b.get(insert.index).bytes
                    );

                    String output = "? " + ranges + "\n";
                    out.write(output.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    public static void main(String[] args) {
        boolean known = args.length == 3
                && (args[0].equals("lines") || args[0].equals("highlight"));

        if (!known) {
            System.err.println("usage: Main lines|highlight A_PATH B_PATH");
            System.exit(2);
        }

        String command = args[0];
        Path aPath = Path.of(args[1]);
        Path bPath = Path.of(args[2]);

        try {
            List<Line> a = readLines(aPath);
            List<Line> b = readLines(bPath);

            List<Edit> edits = myersLines(a, b);

            BufferedOutputStream out =
                    new BufferedOutputStream(System.out);

            outputLines(
                    out,
                    a,
                    b,
                    edits,
                    command.equals("highlight")
            );

            out.flush();

        } catch (IOException e) {
            System.exit(2);
        }
    }
}
