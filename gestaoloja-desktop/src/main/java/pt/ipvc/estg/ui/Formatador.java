package pt.ipvc.estg.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatador {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale LOCALE_NUMEROS = Locale.GERMANY;

    private Formatador() {
    }

    public static String moeda(double valor) {
        return String.format(LOCALE_NUMEROS, "%,.2f EUR", valor);
    }

    public static String dataHora(LocalDateTime data) {
        return data == null ? "" : data.format(DATA_HORA);
    }
}
