package org.example.janelas;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import org.example.emulador.AVD;
import org.example.emulador.EmuladorAndroid;

public class Home {
    private final Path androidSdk = Paths.get(System.getProperty("user.home"), "AppData/Local/Android/Sdk");
    private final EmuladorAndroid emuladorAndroid = new EmuladorAndroid(androidSdk.resolve("emulator/emulator.exe"));
    private ObservableList<AVD> listaAvds;

    @FXML
    private ListView<AVD> listView;

    @FXML
    private void initialize() {
        this.listaAvds = this.listView.getItems();
        atualizar();
    }

    @FXML
    private void atualizar() {
        final var nomes = new ArrayList<String>();
        emuladorAndroid.executeAndListen(
                nomes::add,
                () -> {
                    var nomesOld = this.listaAvds.stream().map(AVD::toString).collect(Collectors.toList());
                    if (this.listaAvds.size() == nomes.size() && nomes.equals(nomesOld)) return;

                    nomesOld.stream().filter(nome -> !nomesOld.contains(nome)).forEach((nome) -> {
                        var any = this.listaAvds.stream().filter((avd) -> avd.toString().equals(nome)).findAny();
                        any.ifPresent(avd -> {
                            avd.finalizar();
                            Platform.runLater(() -> this.listaAvds.remove(avd));
                        });
                    });

                    nomes.stream()
                            .filter(nome -> !nomesOld.contains(nome))
                            .map(nome -> new AVD(this.emuladorAndroid, nome, this::showAlert))
                            .forEach((avd) -> Platform.runLater(() -> this.listaAvds.add(avd)));
                },
                true,
                "-list-avds"
        );
    }

    @FXML
    private void iniciar() {
        if (!this.listaAvds.isEmpty()) {
            AVD selectedItem = this.listView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                new Alert(Alert.AlertType.WARNING, "Nenhum item selecionado").show();
                return;
            }
            selectedItem.iniciar();
        }
    }

    private void showAlert(Map.Entry<String, String> params) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(params.getKey());
            alert.setContentText(params.getValue());
            alert.show();
        });
    }

    public void finalizar() {
        this.listaAvds.forEach(AVD::finalizar);
    }
}
