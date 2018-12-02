import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
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
	
	HashMap<String, UserStory> stringMap = new HashMap<>();
	HashMap<String, ListView<String>> listViewMap = new HashMap<>();

	ListView<String> backlogView = new ListView<>();
	ListView<String> firstView = new ListView<>();
	ListView<String> secondView = new ListView<>();
	ListView<String> thirdView = new ListView<>();
	
	TextArea textBox = new TextArea();

	
	static final DataFormat STRING_LIST = new DataFormat("StringList");

    BufferedReader in;
    PrintWriter out;
    Label msgFromServer = new Label("");

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stringMap = initializeMap();
		
		
		Label backlogLabel = new Label("BackLog: ");
		Label firstLabel = new Label("Not Started: ");
		Label secondLabel = new Label("In Progress: ");
		Label thirdLabel = new Label("Testing/Review: ");
		Label scrumLabel = new Label("Current Sprint");
		
		Button button = new Button("Add New User Story");
		
		textBox.setPrefHeight(300);
		textBox.setPrefWidth(400);
		//textBox.setTranslateX(400);
		textBox.setTranslateY(150);
		textBox.setEditable(false);

		backlogView.setPrefSize(200, 200);
		backlogView.setTranslateY(75);
		backlogView.setMaxWidth(200);
		
		firstView.setPrefSize(200, 400);
		firstView.setTranslateX(75);
		firstView.setTranslateY(75);
		firstView.setMaxWidth(200);
		
		secondView.setPrefSize(200, 400);
		secondView.setTranslateX(100);
		secondView.setTranslateY(75);
		secondView.setMaxWidth(200);
		
		thirdView.setPrefSize(200, 400);
		thirdView.setTranslateX(-75);
		thirdView.setTranslateY(75);
		thirdView.setMaxWidth(200);

		backlogView.getItems().addAll(this.getUserStoryList());

		backlogView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		firstView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		secondView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		thirdView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		GridPane pane = new GridPane();

		pane.getChildren().add(button);
		button.setTranslateX(25);
		button.setTranslateY(520);
		

		pane.addRow(1, backlogLabel, firstLabel, secondLabel, thirdLabel);
		pane.addRow(0, scrumLabel);
		
		backlogLabel.setTranslateX(70);
		backlogLabel.setTranslateY(75);
		firstLabel.setTranslateX(130);
		firstLabel.setTranslateY(75);
		secondLabel.setTranslateX(160);
		secondLabel.setTranslateY(75);
		//thirdLabel.setTranslateX(170);
		thirdLabel.setTranslateY(75);
		
		scrumLabel.setTranslateX(275);
		scrumLabel.setTranslateY(30);
		scrumLabel.setStyle("-fx-font: 20 arial;");
		
		msgFromServer.setTranslateX(100);
		msgFromServer.setTranslateY(600);
		pane.getChildren().add(msgFromServer);
		
		pane.addRow(3, backlogView, firstView, secondView, thirdView);
		//pane.addRow(5, textBox);
		pane.add(textBox, 2, 4);
		
		listViewMap.put("backlogView", backlogView);
		listViewMap.put("firstView", firstView);
		listViewMap.put("secondView", secondView);
		listViewMap.put("thirdView", thirdView);
		

		// handlers
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
		
		backlogView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				expandStory(backlogView);
				
				
			}
		});
		
		firstView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				expandStory(firstView);
				
				
			}
		});
		
		secondView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				expandStory(secondView);
				
				
			}
		});
		
		thirdView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				expandStory(thirdView);
				
				
			}
		});
		
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	 GridPane form = new GridPane();
            	 form.setAlignment(Pos.CENTER);
            	 form.setHgap(10);
            	 form.setVgap(10);
            	 form.setPadding(new Insets(25, 25, 25, 25));

            
                 Scene secondScene = new Scene(form, 500, 500);
              
                 // New window (Stage)
                 Stage newWindow = new Stage();
                 newWindow.setTitle("Add New User Story");
                 newWindow.setScene(secondScene);
  
                 // Set position of second window, related to primary window.
                 newWindow.setX(stage.getX() + 200);
                 newWindow.setY(stage.getY() + 100);
  
                 newWindow.show();
            } 
        }; 
        
        button.setOnAction(event); 
        

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
		
		Thread jfxThread = Thread.currentThread();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					runClient(jfxThread);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private HashMap<String, UserStory> initializeMap() {
		HashMap<String, UserStory> map = new HashMap<>();
		
		UserStory story1 = new UserStory("Create UI", "Bia", "Not Started");
		UserStory story2 = new UserStory("Story 2", "Dan", "Not Started");
		
		map.put(story1.getName(), story1);
		map.put(story2.getName(), story2);
		
		return map;
		
	}

	private void runClient(Thread t) throws IOException {
		
		Thread jfxThread = t;

        // Make connection and initialize streams
        String serverAddress = "localhost";
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if(line != null){
            	// Need to run this on JFX thread
            	Platform.runLater(new Runnable() {
					@Override
					public void run() {
						msgFromServer.setText("Message from server: "+line);
					}
				});
	            
            }
        }
    }

	private ObservableList<String> getUserStoryList() {
		ObservableList<String> list = FXCollections.<String>observableArrayList();
		//UserStory story1 = new UserStory("Create UI", "Bia", "Not Started");
		//UserStory story2 = new UserStory("Story 2", "Dan", "Not Started");
		
		for (String storyName : stringMap.keySet()) {
			list.add(storyName);
			
		}
		
		//list.add(story1.getName());
		//list.add(story2.getName());

		return list;
	}
	
	private void expandStory(ListView<String> view) {
		String name = view.getSelectionModel().getSelectedItem();
		UserStory story = stringMap.get(name);
		textBox.clear();
		textBox.appendText("Name: " + name + "\n" + "Author: " + story.getAuthor());		
			
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
			out.println("Dragged story "+story);
		}

		listView.getSelectionModel().clearSelection();
		listView.getItems().removeAll(selectedList);
	}

}
