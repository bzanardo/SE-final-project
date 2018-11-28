import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ScrumBoard extends Application {

	ListView<String> backlogView = new ListView<>();
	ListView<String> firstView = new ListView<>();
	ListView<String> secondView = new ListView<>();
	ListView<String> thirdView = new ListView<>();

	static final DataFormat STRING_LIST = new DataFormat("StringList");

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Label backlogLabel = new Label("BackLog: ");
		Label firstLabel = new Label("Not Started: ");
		Label secondLabel = new Label("In Progress: ");
		Label thirdLabel = new Label("Testing/Review: ");

		backlogView.setPrefSize(200, 200);
		
		firstView.setPrefSize(200, 400);
		firstView.setTranslateX(75);
		firstView.setTranslateY(50);
		
		secondView.setPrefSize(200, 400);
		secondView.setTranslateX(100);
		secondView.setTranslateY(50);
		
		thirdView.setPrefSize(200, 400);
		thirdView.setTranslateX(125);
		thirdView.setTranslateY(50);

		backlogView.getItems().addAll(this.getUserStoryList());

		backlogView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		firstView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		secondView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		thirdView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(10);

		pane.addRow(1, backlogLabel, firstLabel, secondLabel, thirdLabel);
		pane.addRow(2, backlogView, firstView, secondView, thirdView);

		// Add mouse event handlers for the source
		backlogView.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				dragDetected(event, backlogView);
			}
		});

		backlogView.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragOver(event, backlogView);
			}
		});

		backlogView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDropped(event, backlogView);
			}
		});

		backlogView.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDone(event, backlogView);
			}
		});

		// Add mouse event handlers for the target
		firstView.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				dragDetected(event, firstView);
			}
		});

		firstView.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragOver(event, firstView);
			}
		});

		firstView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDropped(event, firstView);
			}
		});

		firstView.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDone(event, firstView);
			}
		});
		
		secondView.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				dragDetected(event, secondView);
			}
		});

		secondView.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragOver(event, secondView);
			}
		});

		secondView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDropped(event, secondView);
			}
		});

		secondView.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDone(event, secondView);
			}
		});
		
		thirdView.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				dragDetected(event, thirdView);
			}
		});

		thirdView.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragOver(event, thirdView);
			}
		});

		thirdView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDropped(event, thirdView);
			}
		});

		thirdView.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDone(event, thirdView);
			}
		});

		Pane root = new Pane();
		root.setPrefSize(1000, 750);
		root.getChildren().addAll(pane);
		// Set the Style of the VBox
		/*
		 * root.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" +
		 * "-fx-border-width: 2;" + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" +
		 * "-fx-border-color: blue;");
		 */

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("SCRUM Tool");
		stage.show();
	}

	private ObservableList<String> getUserStoryList() {
		ObservableList<String> list = FXCollections.<String>observableArrayList();
		UserStory story1 = new UserStory("Create UI", "Bia", "Not Started");
		String name1 = story1.getName();
		list.addAll(name1);

		return list;
	}

	private ObservableList<String> getUserStoryNames(ArrayList<UserStory> list) {
		ObservableList<String> names = FXCollections.<String>observableArrayList();
		for (UserStory story : list) {
			names.add(story.getName());
		}

		return names;
	}

	private void dragDetected(MouseEvent event, ListView<String> listView) {
		int selectedCount = listView.getSelectionModel().getSelectedIndices().size();

		if (selectedCount == 0) {
			event.consume();
			return;
		}

		Dragboard dragboard = listView.startDragAndDrop(TransferMode.MOVE);

		ArrayList<String> selectedItems = this.getSelectedStories(listView);

		ClipboardContent content = new ClipboardContent();

		content.put(STRING_LIST, selectedItems);

		dragboard.setContent(content);
		event.consume();
	}

	private void dragOver(DragEvent event, ListView<String> listView) {
		Dragboard dragboard = event.getDragboard();

		if (event.getGestureSource() != listView && dragboard.hasContent(STRING_LIST)) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}

		event.consume();
	}

	@SuppressWarnings("unchecked")
	private void dragDropped(DragEvent event, ListView<String> listView) {
		boolean dragCompleted = false;

		Dragboard dragboard = event.getDragboard();

		if (dragboard.hasContent(STRING_LIST)) {
			ArrayList<String> list = (ArrayList<String>) dragboard.getContent(STRING_LIST);

			listView.getItems().addAll(list);
			dragCompleted = true;
		}

		event.setDropCompleted(dragCompleted);
		event.consume();
	}

	private void dragDone(DragEvent event, ListView<String> listView) {
		TransferMode tm = event.getTransferMode();

		if (tm == TransferMode.MOVE) {
			removeSelectedStories(listView);
		}

		event.consume();
	}

	private ArrayList<String> getSelectedStories(ListView<String> listView) {
		ArrayList<String> list = new ArrayList<>(listView.getSelectionModel().getSelectedItems());

		return list;
	}

	private void removeSelectedStories(ListView<String> listView) {
		List<String> selectedList = new ArrayList<>();

		for (String story : listView.getSelectionModel().getSelectedItems()) {
			selectedList.add(story);
		}

		listView.getSelectionModel().clearSelection();
		listView.getItems().removeAll(selectedList);
	}

}