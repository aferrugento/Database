package bd;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public class Protection {

    public static String inputStr(String s, DataInputStream console) {
        String str = "";

        do {
            System.out.print(s);
            try {
                str = console.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (str.equals(""));
        return str;
    }

    public static int inputInt(String s, Scanner sc) {
        System.out.print(s);
        while (!sc.hasNextInt()) {
            System.out.print(s);
            sc.next(); //limpa buffer
        }
        int i = sc.nextInt();
        sc.nextLine();  // limpar buffer para proxima leitura 
        return i;
    }

    /**
     * Funcao que cria uma data
     *
     * @return Data
     */
    public static int[] cria_data(int day, int month, int year) {

        int[] data = new int[3];
        int i, flag;

        int[] meses_31_dias = {1, 3, 5, 7, 8, 10, 12};
        do {
            try {
                data[0] = day;
                data[1] = month;
                data[2] = year;
                flag = 0;

                for (i = 0; i < 7; i++) {
                    if (data[1] == meses_31_dias[i] && data[0] > 31) {
                        data[0] = -1;
                        System.out.print("Date Invalid1\n");
                    }
                    if (data[1] == meses_31_dias[i]) {
                        flag = 1;
                    }
                }

                if (data[0] > 30 && data[1] != 2 && flag == 0) {
                    data[0] = -1;
                    System.out.print("Date Invalid2\n");
                }
            } catch (Exception InputMismatchException) {
                System.out.print("Date Invalid\n");
                data[0] = -1;
            }
        } while (data[0] < 1 || bissexto(data[2]) == 1 && data[0] > 29 && data[1] == 2 || bissexto(data[2]) == 0 && data[0] > 28 && data[1] == 2 || data[1] > 12 || data[1] < 1 || data[2] < 0);

        return data;
    }

    /**
     * Funcao que verifica se um ano � bissexto ou nao
     *
     * @param ano ano a analisar se � bissexto ou nao
     * @return 1 se for bissexto, 0 se nao for
     */
    public static int bissexto(int ano) {
        //Recebe o ano e devolve 1 caso seja bissexto, 0 caso nao seja

        if (ano % 400 == 0) {
            return 1;
        } else if (ano % 100 == 0) {
            return 0;
        } else if (ano % 4 == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public static String insertEmail(String str) {

        if (str.length() < 6 || str.contains("@") == false || str.substring(str.length() - 5).equals("@.com") || str.substring(str.length() - 4).equals("@.pt") || str.charAt(0) == '@') {
            str = "-1";
            System.out.println("Format Invalid");
        } else {
            if (!str.substring(str.length() - 4).equals(".com") && !str.substring(str.length() - 3).equals(".pt")) {
                str = "-1";
            }
        }
        return str;
    }

}
