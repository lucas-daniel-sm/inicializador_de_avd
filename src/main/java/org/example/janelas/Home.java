package org.example.janelas;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.example.emulador.AVD;
import org.example.emulador.EmuladorAndroid;

public class Home {
    private Path androidSdk = Paths.get(System.getProperty("user.home"), "AppData/Local/Android/Sdk");
    private EmuladorAndroid emuladorAndroid;
    private ObservableList<AVD> listaAvds;
    private Runnable funcaoFecharApp;

    @FXML
    private ListView<AVD> listView;

    @FXML
    private void initialize() {
        try {
            emuladorAndroid = new EmuladorAndroid(androidSdk);
        } catch (FileNotFoundException e) {
            var alert = new Alert(
                    Alert.AlertType.ERROR,
                    e.getMessage(),
                    new ButtonType("FECHAR", ButtonBar.ButtonData.FINISH),
                    new ButtonType("ALTERAR LOCAL", ButtonBar.ButtonData.APPLY)
            );
            alert.setTitle("Mensagem de erro");
            alert.setHeaderText("Erro nos arquivos");

            var resposta = alert.showAndWait();
            if (resposta.isEmpty() || !resposta.get().getButtonData().equals(ButtonBar.ButtonData.APPLY)) {
                funcaoFecharApp.run();
                return;
            }

            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            this.androidSdk = chooser.showDialog(new Stage()).toPath();
            initialize();
        }

        this.listaAvds = this.listView.getItems();
        atualizarListaAVD();
    }

    @FXML
    private void atualizarListaAVD() {
        final var nomes = new ArrayList<String>();
        emuladorAndroid.executeAndListen(
                nomes::add,
                () -> {
                    var nomesOld = this.listaAvds.stream().map(AVD::toString).collect(Collectors.toList());
                    if (this.listaAvds.size() == nomes.size() && nomes.equals(nomesOld)) return;
                    this.listaAvds.clear();
                    this.listaAvds.addAll(
                            nomes.stream()
                                    .map(nome -> new AVD(this.emuladorAndroid, nome, this::showAlert))
                                    .collect(Collectors.toList())
                    );
                },
                true,
                "-list-avds"
        );
    }

    @FXML
    private void iniciarAVD() {
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

    public void setFuncaoFecharApp(Runnable funcao) {
        this.funcaoFecharApp = funcao;
    }
}
