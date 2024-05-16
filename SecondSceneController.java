package controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.stage.Stage;

public class SecondSceneController {

	private static ObservableList<String> obs = FXCollections.observableArrayList();
	private static Button addFile = new Button();
	private static Button delFile = new Button();
	private static Button reproducePlaylist = new Button();
	private static ListView<String> listView = null;
	private static String path = null;
	private static String filePath = null;

	/**
	 * This method loads the list of songs for the specified playlist item. It
	 * retrieves the list of files in the playlist directory and adds only the audio
	 * files (.mp3 or .wav) to the observable list.
	 * 
	 * @param item The name of the playlist item.
	 */
	private static void loadList(String item) {
		File playlist = new File(Main.getConfiguration().getPlaylistPath() + "/" + item);
		File[] files = playlist.listFiles();

		if (files != null) {
			for (File name : files) {
				if (name.getAbsolutePath().endsWith("mp3") || name.getAbsolutePath().endsWith("wav")) {
					obs.add(name.getName());
				}
			}
		}

	}

	/**
	 * This method creates and displays the second scene with the specified item.
	 * The scene displays a list of songs from the playlist corresponding to the
	 * item. Users can add, delete, and play songs from the list, as well as play a
	 * random song.
	 * 
	 * @param item The name of the playlist item.
	 * @return The stage for the second scene.
	 */
	public static Stage SecondScene(String item) {
		AnchorPane root = new AnchorPane();
		path = Main.getConfiguration().getPlaylistPath() + "/" + item + "/";
		obs.clear();

		loadList(item);

		Stage stage = new Stage();
		stage.setTitle(item);
		listView = new ListView<>(obs);
		listView.setPrefSize(250, 300);
		listView.setLayoutX(25);
		listView.setLayoutY(10);
		root.getChildren().add(listView);

		root.getChildren().add(addFile);
		addFile.setOnAction(event -> addSong());
		addFile.setText("Add File");
		addFile.setLayoutX(25);
		addFile.setLayoutY(320);
		addFile.setPrefSize(122, 25);

		root.getChildren().add(delFile);
		delFile.setOnAction(event -> delSong(listView.getSelectionModel().getSelectedItem()));
		delFile.setText("Delete File");
		delFile.setPrefSize(122, 25);
		delFile.setLayoutX(150);
		delFile.setLayoutY(320);

		root.getChildren().add(reproducePlaylist);
		reproducePlaylist.setOnAction(event -> playEntirePlaylist());
		reproducePlaylist.setText("Reproduce Playlist");
		reproducePlaylist.setPrefSize(250, 25);
		reproducePlaylist.setLayoutX(25);
		reproducePlaylist.setLayoutY(350);

		listView.setOnMouseClicked(event -> {
			if (event.getClickCount() >= 2) {
				playSong(listView.getSelectionModel().getSelectedItem());
				setFilePath(listView.getSelectionModel().getSelectedItem());
			}
		});

		Scene scene = new Scene(root, 300, 400);
		scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setX(800);
		stage.setY(200);
		stage.setOnCloseRequest(window -> {
			Main.setSecondStage(null);
		});
		stage.show();

		return stage;
	}

	private static void playEntirePlaylist() {
		File dir = new File(path);
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				Media media = new Media(file.toURI().toString());
				try {
					Desktop.getDesktop().open(file);
					Thread.sleep((long) media.getDuration().toMillis());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * This method handles adding a new song to the system. It opens a dialog window
	 * for selecting an audio file (.mp3 or .wav), then copies the selected file to
	 * the specified destination directory and adds the file name to the observable
	 * songs list (obs). If no file is selected, it displays an alert.
	 */
	public static void addSong() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileNameExtensionFilter("File Audio .mp3 .wav", "wav", "mp3"));
		jfc.showOpenDialog(null);
		File file = jfc.getSelectedFile();

		if (file != null) {
			String destDirectory = path;
			String destFileName = file.getName();
			File dest = new File(destDirectory, destFileName);

			try {
				Files.copy(file.toPath(), dest.toPath());
				obs.add(destFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			SampleController.showAlert("Attenzione!", "Assicurati di selezzionare almeno un file");
		}
	}

	/**
	 * This method handles the deletion of a song from the system. It prompts the
	 * user with a confirmation dialog to confirm the deletion. Once confirmed, the
	 * song is removed from the observable songs list (obs) and deleted from the
	 * filesystem. If no song is selected, it displays an alert.
	 * 
	 * @param song The name of the song to be deleted.
	 */
	private static void delSong(String song) {
		String pathName = path + song;

		ButtonType conferma = new ButtonType("Conferma");
		ButtonType annulla = new ButtonType("Annulla");

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Rimuovi Playlist");
		alert.setHeaderText(null);
		alert.setContentText("Una volta eliminata la canzone non la potrai recuperare!");

		alert.getButtonTypes().setAll(conferma, annulla);

		alert.showAndWait().ifPresent(response -> {
			if (response == conferma) {
				if (song != null) {
					obs.remove(song);
					File file = new File(pathName);
				} else {
					SampleController.showAlert("Attenzione", "Prima seleziona una canzone");
				}
			} else if (response == annulla) {
				SampleController.showAlert("Attenzione", "Azione annullata");
			}
		});
	}

	/**
	 * Plays the specified song.
	 * 
	 * @param song The name or path of the song file to play. If the song file is
	 *             located in the same directory as the code, provide just the file
	 *             name. Otherwise, provide the full path. Example: "song.mp3" or
	 *             "/path/to/song.mp3"
	 */
	public static void playSong(String song) {
		File file = new File(path + song);
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static ListView getListView() {
		return listView;
	}

	public static void setListView(ListView listView) {
		SecondSceneController.listView = listView;
	}

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		SecondSceneController.filePath = filePath;
	}

}
