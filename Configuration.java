package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import application.Main;
import controllers.SampleController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 8L;
	private static final String serFilePath = "config.ser";
	private String playlistPath = null;
	private transient ListView<String> listView;
	private static ObservableList<String> obs;

	public ObservableList<String> getObs() {
		return obs;
	}

	public static void setObs(ObservableList<String> obs) {
		Configuration.obs = obs;
	}

	/**
	 * Creates a folder for storing configurations if it doesn't exist already.
	 */
	public void folderCreator() {
		File dir = new File(this.getPlaylistPath() + "/TuneTrove");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		setPlaylistPath(dir.getAbsolutePath());
	}

	/**
	 * Inizalize the ListView and shows the playlists
	 */
	public void inizializeListView() {
		obs = FXCollections.observableArrayList();

		this.setListView(new ListView<>());

		File dir = new File(Main.getConfiguration().getPlaylistPath() + "/TuneTrove");
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						obs.add(file.getName());
					}
				}
			}
		}

		this.getListView().setItems(obs);
		this.getListView().setPrefSize(200, 260);
		this.getListView().setLayoutX(50);
		this.getListView().setLayoutY(40);
		Main.getRoot().getChildren().add(this.getListView());

	}

	/**
	 * Load a configuration from the '.ser' file. If the files doesen't exist or the
	 * file is empty it creates a new configuration
	 *
	 * @return an Instance of Configuration
	 */
	public Configuration loadConfiguration() {

		File ser = new File(serFilePath);

		if (ser.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serFilePath));
				Configuration c = (Configuration) ois.readObject();
				if (c != null) {
					
					if (c.getPlaylistPath() == null || c.getPlaylistPath().isEmpty()) {
						SampleController.showAlert("Configurazione", "Scegli dove salvare le tue Playlist");
						c.setPlaylistPath(c.choosePlaylistPath());
					}
					
				} else {
					c = new Configuration();
					SampleController.showAlert("Configurazione Iniziale", "Scegli dove salvare le tue Playlist:");
					c.setPlaylistPath(c.choosePlaylistPath());
				}

				ois.close();
				c.saveConfiguration();
				c.folderCreator();
				c.inizializeListView();
				
				return c;

			} catch (Exception e) {
				Configuration c = new Configuration();
				SampleController.showAlert("Configurazione Iniziale", "Scegli dove salvare le tue Playlist:");
				c.setPlaylistPath(c.choosePlaylistPath());
				c.saveConfiguration();
				c.folderCreator();
				c.inizializeListView();
				
				return c;
			}

		} else {
			Configuration c = new Configuration();
			c.choosePlaylistPath();
			c.saveConfiguration();
			c.folderCreator();
			c.inizializeListView();
			return c;
		}
		
		
	}

	/**
	 * Saves the configuration in a '.ser' file
	 */
	public void saveConfiguration() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serFilePath));
			oos.writeObject(this);
			oos.close();
			Main.getConfiguration().setPlaylistPath(this.getPlaylistPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create an instance of Configuration
	 */
	public Configuration() {
	}

	/**
	 * Let the user choose a folder and returns a String with the absolute path of
	 * it
	 *
	 * @return String AbsolutePath
	 */
	public String choosePlaylistPath() {
		DirectoryChooser dirCh = new DirectoryChooser();
		return dirCh.showDialog(null).getAbsolutePath();
	}

	public String getPlaylistPath() {
		return playlistPath;
	}

	public void setPlaylistPath(String playlistPath) {
		this.playlistPath = playlistPath;
	}


	public ListView<String> getListView() {
		return listView;
	}

	public void setListView(ListView<String> listView) {
		this.listView = listView;
	}


}
