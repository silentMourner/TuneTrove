package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SampleController {

	@FXML
	private Button importPlaylist;

	@FXML
	private Button createPlaylist;

	@FXML
	private Button deletePlaylist;

	@FXML
	void createPlaylist(ActionEvent event) {
		String path = Main.getConfiguration().getPlaylistPath() + "/";
		TextInputDialog inTxt = new TextInputDialog();
		inTxt.setTitle("Creazione Playlist");
		inTxt.setHeaderText("Inserisci il nome della playlist");
		inTxt.setContentText("Nome della Playlist:");
		Optional<String> res = inTxt.showAndWait();
		res.ifPresent(playlist -> {
			File pl = new File(path + playlist);
			if (pl.mkdir()) {
				showAlert("Success!", "Playlist creata correttamente");
				Main.getConfiguration().getObs().add(playlist);
			} else {
				showAlert("Error!", "Errore nella creazione della playlist");
			}
		});
	}

	public static void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void importPlaylist(ActionEvent event) {
	    DirectoryChooser directoryChooser = new DirectoryChooser();
	    directoryChooser.setTitle("Select Playlist Folder");
	    File selectedDirectory = directoryChooser.showDialog(null);

	    if (selectedDirectory != null) {
	        String destinationFolderPath = Main.getConfiguration().getPlaylistPath();
	        String newPlaylistFolderName = selectedDirectory.getName();

	        File destinationFolder = new File(destinationFolderPath);

	        try {
	            File newPlaylistFolder = new File(destinationFolderPath, newPlaylistFolderName);
	            if (!newPlaylistFolder.exists()) {
	                newPlaylistFolder.mkdirs();
	            }

	            File[] playlistFiles = selectedDirectory.listFiles();
	            if (playlistFiles != null) {
	                for (File file : playlistFiles) {
	                    if (file.isFile() && (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav"))) {
	                        Files.copy(file.toPath(), new File(newPlaylistFolder, file.getName()).toPath());
	                    }
	                }
	            }

	            Main.getConfiguration().getObs().add(newPlaylistFolderName);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	@FXML
	void deletePlaylist(ActionEvent event) {

		String file = Main.getConfiguration().getListView().getSelectionModel().getSelectedItem();
		File playlist = new File(Main.getConfiguration().getPlaylistPath() + "/" + file);

		ButtonType conferma = new ButtonType("Conferma");
		ButtonType annulla = new ButtonType("Annulla");

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Rimuovi Playlist");
		alert.setHeaderText(null);
		alert.setContentText("Una volta eliminata la playlist perderai tutti i file contenuti in essa!");

		alert.getButtonTypes().setAll(conferma, annulla);

		alert.showAndWait().ifPresent(response -> {
			if (response == conferma) {
				if (file != null) {

					try {
						deleteDirectory(playlist);
						Main.getConfiguration().getObs().remove(file);
						showAlert("Rimozione Playlist", "Playlist rimossa con successo");
					} catch (Exception e) {
						showAlert("Rimozione Playlist", "Errore durante la rimozione della playlist");
					}

				} else {
					showAlert("Attenzione", "Prima seleziona una playlist");
				}
			} else if (response == annulla) {
				showAlert("Attenzione", "Azione annullata");
			}
		});

	}

	public static void deleteDirectory(File directory) throws Exception {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
			directory.delete();
		}
	}

}
