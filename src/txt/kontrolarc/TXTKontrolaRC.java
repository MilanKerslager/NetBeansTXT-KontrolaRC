// Kontrola rodného čísla
package txt.kontrolarc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TXTKontrolaRC {

    public static void main(String[] args) {
        String rodnecislo = "966126/0356"; // náhodně vygenerované RČ
        System.out.print("Rodné číslo " + rodnecislo + " ");
        if (isRCok(rodnecislo)) {
            System.out.println("je v pořádku");
        } else {
            System.out.println("není platné.");
        }
    }

    private static boolean isRCok(String rc) {
        // rozebereme RC na jednotlivé části
        // TODO: ověřit kontrolu RČ se třemi ciframi za lomítkem
        String regex = "^\\s*(\\d\\d)(\\d\\d)(\\d\\d)[ /]*(\\d\\d\\d)(\\d?)\\s*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(rc);
        String rok, mesic, den, ext, kontrolni = null;
        if (m.find()) {
            rok = m.group(1);
            mesic = m.group(2);
            den = m.group(3);
            ext = m.group(4);
            kontrolni = m.group(5);
        } else {
            // nejde-li rozparsovat podle regulárního výrazu, je to chyba
            return false;
        }

        // kontrola dělitelnosti RČ
        int suma = 0;
        // FIXME: vyházet znaky podle regulárního výrazu
        String rodne = rc.replaceAll("/", "");
        for (int i = 0; i <= rodne.length() - 2; i++) {
            // vynecháme lomítka
            if (Character.isDigit(rodne.charAt(i)) == true) {
                // liché pozice přičítáme, sudé odečítáme
                if (i % 2 == 0) {
                    suma += Character.getNumericValue(rodne.charAt(i));
                } else {
                    suma -= Character.getNumericValue(rodne.charAt(i));
                }
            }
        }
        // zbytek po dělení 11 z výše vypočítaného součtu
        int zbytek = suma % 11;
        // pokud je zbytek 10, bude kontrolní (poslední) číslice nula
        if (zbytek == 10) {
            zbytek = 0;
        }
        // zbytek po dělení by měl být roven kontrolní číslici
        if (zbytek != Integer.parseInt(kontrolni)) {
            // když není roven, je to chyba
            return false;
        }

        // doplnění století v roku (rok na 4 čísla)
        int celyrok = Integer.parseInt(rok);
        celyrok += (celyrok < 54) ? 2000 : 1900;

        // korekce měsíce (žena a další speciality zavedené po roce 2003)
        int mes = Integer.parseInt(mesic);
        if (mes > 70 && celyrok > 2003) {
            mes -= 70;
        } else if (mes > 50) {
            mes -= 50;
        } else if (mes > 20 && celyrok > 2003) {
            mes -= 20;
        }

        // korigovaný měsíc musíme mít ve dvou cifrách (01, ... 12)
        mesic = "0" + mes;
        mesic = mesic.substring(mesic.length() - 2);
        // kontrola reálnosti datumu
        // FIXME: 150229 kontrolou projde!!!
        DateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        try {
            fmt.parse("" + celyrok + mesic + den);
        } catch (ParseException ex) {
            // datum není reálný
            return false;
        }

        // TODO: RČ nesmí být v budoucnosti
        // došli jsme až sem, takže žádná chyba nebyla v RČ nalezena
        return true;
    }
}
