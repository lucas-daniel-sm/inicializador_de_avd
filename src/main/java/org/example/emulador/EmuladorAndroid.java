package org.example.emulador;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EmuladorAndroid {
    private final Path emulador;

    public EmuladorAndroid(Path path) {
        this.emulador = path;
    }

    public void executeAndListen(Consumer<String> onListening, Runnable onFinalize, boolean paralelo, String... params) {
        executeAndListen(onListening, onFinalize, paralelo, (Boolean::booleanValue), params);
    }

    public void executeAndListen(Consumer<String> onListening, Runnable finalizado, boolean paralelo,
                                 final Predicate<Boolean> stop, String... params) {
        Runnable function = () -> {
            try (final var inputStream = executarComandoNoEmuladorStream(params)) {
                final var scaner = new Scanner(inputStream);
                while (stop.test(scaner.hasNextLine())) {
                    onListening.accept(scaner.nextLine());
                }
                if (finalizado != null) finalizado.run();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                ex.printStackTrace();
            }
        };

        if (paralelo) new Thread(function).start();
        else function.run();
    }


    public Process executarComandoNoEmulador(String... params) throws IOException {

        StringBuilder stringBuilder = new StringBuilder(this.emulador.toString());

        Arrays.stream(params).map((" ")::concat).forEach(stringBuilder::append);

        return Runtime.getRuntime().exec(stringBuilder.toString());
    }

    public InputStream executarComandoNoEmuladorStream(String... params) throws IOException {
        return executarComandoNoEmulador(params).getInputStream();
    }
}
