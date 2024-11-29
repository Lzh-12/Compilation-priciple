import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static HashMap<String, Integer> syn = new HashMap<>(); // 关键字
    public static int gnRow = 1; // 行号
    public static Word uWord = new Word(); // 存放当前单词
    public static int gnLocate = 0; // 下一个字符脚标
    public static String strSource; // 输入源
    public static ArrayList<Quaternion> pQuad = new ArrayList<>(); // 存放四元式的列表
    public static int nSuffix = 0; // 临时变量的编号
    public static int nNXQ = 1;
    public static int[] ntc = {1};
    public static int[] nfc = {1};



    // --------------- 去掉空白符号 ----------------
    static int getBlank(String strSource, int i) {
        String ch = String.valueOf(strSource.charAt(i));
        while (ch.equals(" ") || ch.getBytes()[0] == 10)// 10是换行符"\n"的ascii码
        {
            if (ch.getBytes()[0] == 10) {
                gnRow++;
            }
            i = i + 1;
            ch = String.valueOf(strSource.charAt(i));
        }
        return i;
    }

    // ---------------- 判断是否字母 -------------------
    public static boolean isLetter(String ch) {
        return (ch.getBytes()[0] >= "a".getBytes()[0] && ch.getBytes()[0] <= "z".getBytes()[0])
                || (ch.getBytes()[0] >= "A".getBytes()[0] && ch.getBytes()[0] <= "Z".getBytes()[0]);
    }

    // ------------ 判断是否数字 ---------------
    public static boolean isDigit(String ch) {
        return ch.getBytes()[0] >= "0".getBytes()[0] && ch.getBytes()[0] <= "9".getBytes()[0];
    }

    // --------------- 词法分析器 ----------------
    public static void scanner() {
        boolean flag = true;
        int gnLocateStart; // 下一个单词开始的位置
        while (flag) {
            flag = false;

            int i = gnLocate;
            i = getBlank(strSource, i);
            gnLocateStart = i;
            String ch = String.valueOf(strSource.charAt(i));
            i++;
            if (isLetter(ch)) {
                while (isLetter(ch) || isDigit(ch)) {
                    ch = String.valueOf(strSource.charAt(i));
                    i++;
                }
                i--;
                if (syn.get(strSource.substring(gnLocateStart, i)) != null) {
                    uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                } else {
                    uWord.setUWord(50, strSource.substring(gnLocateStart, i), gnRow);
                }
            } else if (isDigit(ch)) {
                while (isDigit(ch)) {
                    ch = String.valueOf(strSource.charAt(i));
                    i++;
                }
                i--;
                if (ch.equals(".")) {
                    i++;
                    ch = String.valueOf(strSource.charAt(i));
                    i++;
                    while (isDigit(ch)) {
                        ch = String.valueOf(strSource.charAt(i));
                        i++;
                    }
                    i--;
                }
                if (syn.get(strSource.substring(gnLocateStart, i)) != null) {
                    uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                } else {
                    uWord.setUWord(51, strSource.substring(gnLocateStart, i), gnRow);
                }
            } else {
                switch (ch) {
                    case "+":
                    case "-":
                    case "*":
                    case "(":
                    case ")":
                    case "{":
                    case "}":
                    case ";":
                    case ",":
                    case "%":
                        uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                        break;
                    case "<":
                    case ">":
                    case "=":
                        ch = String.valueOf(strSource.charAt(i));
                        i++;
                        if (ch.equals("=")) {
                            uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                            break;
                        }
                        i--;
                        uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                        break;
                    case "!":
                        ch = String.valueOf(strSource.charAt(i));
                        i++;
                        if (ch.equals("=")) {
                            uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                            break;
                        } else {
                            System.out.println("词法分析有错误，在第" + gnRow + "行。");
                            flag = true;
                        }
                    case "/":
                        ch = String.valueOf(strSource.charAt(i));
                        i++;
                        if (ch.equals("/") || ch.equals("*")) {
                            flag = true;
                            if (ch.equals("/")) {
                                ch = String.valueOf(strSource.charAt(i));
                                i++;
                                while (!ch.equals("\n")) {
                                    ch = String.valueOf(strSource.charAt(i));
                                    i++;
                                }
                                gnRow++;
                            } else {
                                ch = String.valueOf(strSource.charAt(i));
                                i++;
                                while (!(ch.equals("*") && String.valueOf(strSource.charAt(i)).equals("/"))) {
                                    ch = String.valueOf(strSource.charAt(i));
                                    i++;
                                }
                                i++;
                            }
                            break;
                        }
                        i--;
                        uWord.setUWord(syn.get(strSource.substring(gnLocateStart, i)), strSource.substring(gnLocateStart, i), gnRow);
                        break;
                    case "\0":
                        return;
                    default:
                        System.out.println("词法分析有错误，在第" + gnRow + "行。");
                        flag = true;
                }
            }
            gnLocate = i;
        }
    }

    // 从文件读入, 如果从控制台读入的话，用while (sc.hasNext())来实现换行后继续读入
    public static String input(String strSource) {
        StringBuilder bf = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(strSource));
            String temp;
            while ((temp = br.readLine()) != null) {
                bf.append(temp);
                bf.append("\n"); // 换行符
            }
            bf.append("\0"); // 添加结束符
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
        return new String(bf);
    }

    // --------------------- 打印一个四元式 ---------------------
    public static void printQuaternion() {
        int maxWidth = 15; // 假设我们想要的每列的最大宽度是10
        for (int nLoop = 1; nLoop < nNXQ; nLoop++) {
            // 如果字符串长度小于10，则右侧填充空格，实现居中对齐的效果需要调整长度
            String op = pQuad.get(nLoop).op + ",";
            String argv1 = pQuad.get(nLoop).argv1 + ",";
            String argv2 = pQuad.get(nLoop).argv2 + ",";
            System.out.printf("%-1d:( %-" + maxWidth + "s %-" + maxWidth + "s %-" + maxWidth + "s %-" + 1 + "s)\n",
                    nLoop, op, argv1, argv2, pQuad.get(nLoop).result);
        }
    }


    // ------------------- 输出错误信息 -------------------------
    public static void error(String strError) {
        System.out.println("语法错误，第" + gnRow + "行：缺少 " + strError);
    }

    // -------- 判断当前识别出的单词是否是需要的单词,如果不是则报错，否则扫描下一个单词 --------------
    public static void match(int syn, String strError) {
        if (syn == uWord.getType()) {
            scanner();
        } else {
            error(strError);
        }
    }

    // --------------- 语句块分析函数 --------------------
    public static void statementBlock(int[] nChain) {
        match(syn.get("{"), "{");
        statementSequence(nChain);
        match(syn.get("}"), "}");
    }

    // -------------- 语句序列分析函数 --------------------
    public static void statementSequence(int[] nChain) {
        statement(nChain);
        while (uWord.getType() == 50 || uWord.getType() == syn.get("if") || uWord.getType() == syn.get("while")) {
            bp(nChain[0], nNXQ);
            statement(nChain);
        }
        bp(nChain[0], nNXQ);
    }

    // -------------------- 将t回填到以p为首的四元式链中--------------------
    public static void bp(int p, int t) {
        int w, q = p;
        while (q != 0 && q < pQuad.size()) {
            if (isNumeric(pQuad.get(q).result)) {
                w = Integer.parseInt(pQuad.get(q).result);
            } else {
                w = 0;
            }
            pQuad.get(q).result = t + "";
            q = w;
        }
    }

    // ----------------- 语句分析函数 --------------------
    public static void statement(int[] nChain) {
        String strTemp, eplace;
        int[] nChainTemp = new int[1];
        int nWQUAD;
        int nfcInt;
        switch (uWord.getType()) {
            case 50:
                strTemp = uWord.getWord();
                scanner();
                match(syn.get("="), "=");
                eplace = expression();
                match(syn.get(";"), ";");
                gen("=", eplace, "", strTemp);
                nChain[0] = 0;
                break;
            case 8:
                match(syn.get("if"), "if");
                match(syn.get("("), "(");
                condition(ntc, nfc);
                nfcInt = nfc[0];
                bp(ntc[0], nNXQ);
                match(syn.get(")"), ")");
                statementBlock(nChainTemp);
                bp(nChainTemp[0], nNXQ);
                nChain[0] = nfcInt;
                break;
            case 6:
                match(syn.get("while"), "while");
                nWQUAD = nNXQ;
                match(syn.get("("), "(");
                condition(ntc, nfc);
                nfcInt = nfc[0]; // 因为while里有if时，while里的nfc会被覆盖，那么下方nChain[0] = nfcInt;就得到错误的nChain[0]
                bp(ntc[0], nNXQ);
                match(syn.get(")"), ")");
                statementBlock(nChainTemp);
                bp(nChainTemp[0], nWQUAD);
                strTemp = nWQUAD + "";
                gen("jumpBack", "", "", strTemp);
                nChain[0] = nfcInt;
                break;
        }
    }

    // ----------------- 生成一个四元式 -----------------------
    private static void gen(String op, String argv1, String argv2, String result) {
        Quaternion quaternion = new Quaternion(op, argv1, argv2, result);
        pQuad.add(quaternion);
        nNXQ++;
    }

    // -------------------- 条件表达式分析函数 --------------------
    private static void condition(int[] etc, int[] efc) {
        String opp, eplace1, eplace2;
        String strTemp;
        eplace1 = expression();
        if (uWord.getType() >= syn.get("<") && uWord.getType() <= syn.get("!=")) {
            opp = uWord.getWord();
            scanner();
            eplace2 = expression();
            etc[0] = nNXQ;
            efc[0] = nNXQ + 1;
            strTemp = "jump" + opp;
            gen(strTemp, eplace1, eplace2, "0");
            gen("jumpN", "", "", "0");
        } else {
            error("关系运算符错误");
        }
    }

    // ----------------- 判断是否是数字 ------------------
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // ----------------- 合并p1和p2 ------------------
    private static int merg(int p1, int p2) {
        int p, nResult;
        if (p2 == 0) {
            nResult = p1;
        } else {
            nResult = p = p2;
            while (isNumeric(pQuad.get(p).result) && Integer.parseInt(pQuad.get(p).result) != 0) {
                p = Integer.parseInt(pQuad.get(p).result);
                pQuad.get(p).result = p1 + "";
            }
        }
        return nResult;
    }

    // -------------------- 加减分析函数 --------------------
    public static String expression() {
        String opp, eplace, eplace1, eplace2;
        eplace1 = term();
        eplace = eplace1;
        while (uWord.getType() == syn.get("+") || uWord.getType() == syn.get("-")) {
            opp = uWord.getWord();
            scanner();
            eplace2 = term();
            eplace = newTemp();
            gen(opp, eplace1, eplace2, eplace);
            eplace1 = eplace;
        }
        return eplace;
    }

    // ------------------- 产生一个临时变量 -----------------------
    private static String newTemp() {
        nSuffix++;
        return "T" + nSuffix;
    }

    // -------------------- 乘除分析函数 --------------------
    public static String term() {
        String opp, eplace, eplace1, eplace2;
        eplace = eplace1 = factor();
        while (uWord.getType() == syn.get("*") || uWord.getType() == syn.get("/")) {
            opp = uWord.getWord();
            scanner();
            eplace2 = factor();
            eplace = newTemp();
            gen(opp, eplace1, eplace2, eplace);
            eplace1 = eplace;
        }
        return eplace;
    }

    // -------------------- 括号分析函数 --------------------
    public static String factor() {
        String eplace = "";
        if (uWord.getType() == 50 || uWord.getType() == 51) // 为标识符或整常数时，读下一个单词符号
        {
            eplace = uWord.getWord();
            scanner();
        } else if (uWord.getType() == syn.get("(")) {
            match(syn.get("("), "(");
            eplace = expression();
            if (uWord.getType() == syn.get(")")) {
                match(syn.get(")"), ")");
            }
        }
        return eplace;
    }

    // -------------------- 设置关键字 --------------------
    public static void setSyn() {
        // 关键字
        syn.put("main", 1);
        syn.put("int", 2);
        syn.put("float", 3);
        syn.put("double", 4);
        syn.put("char", 5);
        syn.put("while", 6);
        syn.put("and", 7);
        syn.put("if", 8);
        syn.put("else", 9);
        syn.put("do", 10);
        syn.put("return", 11);
        // 加减乘除余
        syn.put("+", 12);
        syn.put("-", 13);
        syn.put("*", 14);
        syn.put("/", 15);
        syn.put("%", 16);
        // 条件符号
        syn.put("<", 17);
        syn.put("<=", 18);
        syn.put(">", 19);
        syn.put(">=", 20);
        syn.put("==", 21);
        syn.put("!=", 22);
        syn.put("=", 23);
        // 分隔符
        syn.put("(", 24);
        syn.put(")", 25);
        syn.put("{", 26);
        syn.put("}", 27);
        syn.put(";", 28);
        syn.put(",", 29);
        // 注释
        syn.put("//", 30);
        syn.put("/*", 31);
        syn.put("*/", 32);
        // 单词是50, 数字是51
        //结束符//syn.put("\0",33);
    }

    // -------------------- 语法分析 --------------------------
    public static void parse() {
        int[] nChain = new int[1];
        scanner();
        match(syn.get("main"), "main");
        match(syn.get("("), "(");
        match(syn.get(")"), ")");
        statementBlock(nChain);
        printQuaternion();
    }

    // ------------------ 语法分析主函数 -----------------------
    public static void lrParse() {
        pQuad.add(new Quaternion("0", "0", "0", "0"));
        parse();
    }

    // ---------------------- 主函数 ----------------------
    public static void main(String[] args) {
        setSyn();
        String strFileSource = System.getProperty("user.dir") + File.separator + "data.txt";
        // 读取文件
        strSource = input(strFileSource);
        System.out.println("--------------------- 语义分析结果 ----------------------");
        System.out.println("语义四元式：");
        System.out.println("-------------------------------------------------------");
        System.out.println("    操作码         操作数1         操作数2             结果");
        System.out.println("-------------------------------------------------------");
        // 语法分析
        lrParse();

        int maxWidth = 15;
        String op = "endProject,";
        String argv1 =  ",";
        String argv2 = ",";
        String result = "end";
        System.out.printf("%-1d:( %-" + maxWidth + "s %-" + maxWidth + "s %-" + maxWidth + "s %-" + 1 + "s)\n",
                nNXQ, op, argv1, argv2, result);
    }
}
