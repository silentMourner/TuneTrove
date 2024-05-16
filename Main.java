package application;

import controllers.SampleController;
import controllers.SecondSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import utilities.Configuration;

public class Main extends Application {

	private static AnchorPane root = null;
	private static Configuration c;
	private static Stage secondStage = new Stage();
	private static Scene scene = null;
	private static Stage firstStage;
	
	public static Scene getScene() {
		return scene;
	}

	public static void setScene(Scene scene) {
		Main.scene = scene;
	}

	public static Stage getSecondStage() {
		return secondStage;
	}

	public static void setSecondStage(Stage secondStage) {
		Main.secondStage = secondStage;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load(getClass().getResource("FirstScene.fxml"));
			scene = new Scene(root, 300, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			c = new Configuration();
			c = c.loadConfiguration();
			secondStage = null;
			c.getListView().setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					String selectedItem = c.getListView().getSelectionModel().getSelectedItem();
					if (selectedItem != null) {
						if (secondStage == null) {
							secondStage = SecondSceneController.SecondScene(selectedItem);
						} else {
							SampleController.showAlert("Errore",
									"Impossibile aprire la playlist, chiudi tutte le playlist prima di aprirne una nuova");
						}
					}
				}
			});
			
			primaryStage.setResizable(false);
			primaryStage.setTitle("TuneTrove");
			primaryStage.setScene(scene);
			primaryStage.show();

			setFirstStage(primaryStage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Configuration getConfiguration() {
		return Main.c;
	}

	public static void setConfiguration(Configuration c) {
		Main.c = c;
	}

	public static AnchorPane getRoot() {
		return root;
	}

	public static Stage getFirstStage() {
		return firstStage;
	}

	public static void setFirstStage(Stage firstStage) {
		Main.firstStage = firstStage;
	}

}
