package org.example.emulador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AVD {

    private final static String ERRO_AVD_LANCADO_1 = "ERROR: Running multiple emulators with the same AVD is an experimental feature.";
    private final static String ERRO_AVD_LANCADO_2 = "Please use -read-only flag to enable this feature.";

    private final Consumer<Map.Entry<String, String>> showAlert;
    private final List<String> log = new ArrayList<>();
    private final EmuladorAndroid emuladorAndroid;
    private final String nomeDispositivo;
    private boolean finalizar = false;

    public AVD(EmuladorAndroid emuladorAndroid, String nomeDispositivo, Consumer<Map.Entry<String, String>> showAlert) {
        this.emuladorAndroid = emuladorAndroid;
        this.nomeDispositivo = nomeDispositivo;
        this.showAlert = showAlert;
    }

    public void iniciar() {
        emuladorAndroid.executeAndListen(this::leituraLog, null, true, (b) -> b && !finalizar, "-avd", nomeDispositivo);
    }

    public void finalizar() {
        this.finalizar = true;
    }

    @Override
    public String toString() {
        return nomeDispositivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof org.example.emulador.AVD)) return false;
        org.example.emulador.AVD avd = (org.example.emulador.AVD) o;
        return emuladorAndroid.equals(avd.emuladorAndroid) && nomeDispositivo.equals(avd.nomeDispositivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emuladorAndroid, nomeDispositivo);
    }

    private void leituraLog(String log) {
        System.out.println("AVD: \"" + nomeDispositivo + "\": '" + log + '\'');
        this.log.add(log);
        verificarLogs();
    }

    private void verificarLogs() {

        var ultimoIndice = this.log.size() - 1;

        if (this.log.get(ultimoIndice).equals(ERRO_AVD_LANCADO_2)) {
            showAlert.accept(Map.entry("Este emulador já foi iniciado",
                    "AVD:" + nomeDispositivo + "\n" + ERRO_AVD_LANCADO_1 + "\n" + ERRO_AVD_LANCADO_2));
        }
    }


}
