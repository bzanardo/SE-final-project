import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import javafx.scene.control.TextField;
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
	
	static Scene mainScene;
	
	HashMap<String, UserStory> stringMap = new HashMap<>();
	HashMap<String, ListView<String>> listViewMap = new HashMap<>();

	ListView<String> backlogView = new ListView<>();
	ListView<String> firstView = new ListView<>();
	ListView<String> secondView = new ListView<>();
	ListView<String> thirdView = new ListView<>();
	
	TextArea textBox = new TextArea();
	
	static final DataFormat STRING_LIST = new DataFormat("StringList");
	
	static final String DRAGFROM = "DRAG_FROM";
	static final String DRAGTO = "DRAG_TO";
	static final String CREATE = "CREATE_STORY";
	
    BufferedReader in;
    PrintWriter out;

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
		
		Button addButton = new Button("Add New User Story");
		Button editButton = new Button("Edit Story");
		
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

		pane.getChildren().add(addButton);
		addButton.setTranslateX(25);
		addButton.setTranslateY(520);
		

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
		
		EventHandler<ActionEvent> createStoryEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	 GridPane form = new GridPane();
            	 form.setAlignment(Pos.TOP_LEFT);
            	 form.setHgap(10);
            	 form.setVgap(10);
            	 form.setPadding(new Insets(25, 25, 25, 25));
            	 
            	 Label storyNameLabel = new Label("Story Name:");
            	 TextField tf1 = new TextField();
            	 form.add(storyNameLabel, 0, 0);
            	 form.add(tf1, 1, 0);
            	 Label storyPtsLabel = new Label("Story Points:");
            	 TextField tf2 = new TextField();
            	 form.add(storyPtsLabel, 0, 1);
            	 form.add(tf2, 1, 1);
            	 Label author = new Label("Author:");
            	 TextField tf3 = new TextField();
            	 form.add(author, 0, 2);
            	 form.add(tf3, 1, 2);
            	 
            	 Button createStory = new Button("Create New Story");
            	 form.add(createStory, 0, 3);
            
                 Scene secondScene = new Scene(form, 500, 500);
              
                 // New window (Stage)
                 Stage newWindow = new Stage();
                 newWindow.setTitle("Add New User Story");
                 newWindow.setScene(secondScene);
  
                 // Set position of second window, related to primary window.
                 newWindow.setX(stage.getX() + 200);
                 newWindow.setY(stage.getY() + 100);
  
                 newWindow.show();
                 
                 createStory.setOnAction(new EventHandler<ActionEvent>() {
 					@Override
 					public void handle(ActionEvent event) {
 						Platform.runLater(new Runnable() {
 							@Override
 							public void run() {
 								createNewStory(tf1.getText(), Integer.valueOf(tf2.getText()), tf3.getText());
 							}
 						});
 						out.println(CREATE+","+tf1.getText()+","+tf2.getText()+","+tf3.getText());
 						newWindow.close();
 					}
 				});
            } 
        }; 
        
        addButton.setOnAction(createStoryEvent); 

		Pane root = new Pane();
		root.setPrefSize(1000, 750);
		root.getChildren().addAll(pane);
		
		// Set the Style of the VBox
		/*
		 * root.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" +
		 * "-fx-border-width: 2;" + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" +
		 * "-fx-border-color: blue;");
		 */

		mainScene = new Scene(root);
		stage.setScene(mainScene);
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
		
		UserStory story1 = new UserStory("Create UI", 1, "Bia", "Not Started");
		UserStory story2 = new UserStory("Story 2", 1, "Dan", "Not Started");
		
		map.put(story1.getName(), story1);
		map.put(story2.getName(), story2);
		
		return map;
		
	}

	private void runClient(Thread t) throws IOException {
        // Make connection and initialize streams
		boolean connectionMade = false;
        String serverAddress = "localhost";
        try{
        	Socket socket = new Socket(serverAddress, 9001);
        	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connectionMade = true;
        } catch(Exception e){
        	e.printStackTrace();
        	System.out.println("Failed to connect to server, exiting");
        	Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((Stage)mainScene.getWindow()).close();
				}
			});
        }

        // Process all messages from server, according to the protocol.
        while (true) {
        	if(!connectionMade){
        		continue;
        	}
        		
            String line = in.readLine();
            if(line != null){
            	String[] commands = line.split(",");
            	if(commands[0].equals(DRAGFROM)){
            		String storyName = commands[1];
            		Platform.runLater(new Runnable() {	// run on JavaFX main thread
    					@Override
    					public void run() {
    						removeStoryFromServer(storyName, listViewMap.get(commands[2]));
    					}
    				});
            	}
            	else if(commands[0].equals(DRAGTO)){
            		String storyName = commands[1];
            		Platform.runLater(new Runnable() {	// run on JavaFX main thread
    					@Override
    					public void run() {
    						addStoryFromServer(storyName, listViewMap.get(commands[2]));
    					}
    				});
            	}
            	else if(commands[0].equals(CREATE)){
            		String storyName = commands[1];
            		Platform.runLater(new Runnable() {	// run on JavaFX main thread
    					@Override
    					public void run() {
    						createNewStory(storyName, Integer.valueOf(commands[2]), commands[3]);
    					}
    				});
            	}
            }
        }
    }

	private ObservableList<String> getUserStoryList() {
		ObservableList<String> list = FXCollections.<String>observableArrayList();

		for (String storyName : stringMap.keySet()) {
			list.add(storyName);
			
		}
	
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
			
			out.println(DRAGTO+","+list.get(0)+","+getListViewName(listView));
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
	
	private String getListViewName(ListView<String> obj){
		for(String s : listViewMap.keySet()){
			if(listViewMap.get(s).equals(obj)){
				return s;
			}
		}
		return null;
	}

	private void removeSelectedStories(ListView<String> listView) {
		List<String> selectedList = new ArrayList<>();

		for (String story : listView.getSelectionModel().getSelectedItems()) {
			selectedList.add(story);
			String listViewName = getListViewName(listView);
			out.println(DRAGFROM+","+story+","+listViewName);
		}

		listView.getSelectionModel().clearSelection();
		listView.getItems().removeAll(selectedList);
	}
	
	private void createNewStory(String storyName, int storyPoints, String author){
		UserStory newStory = new UserStory(storyName, storyPoints, author, "Not Started");
		stringMap.put(storyName, newStory);
		backlogView.getItems().add(storyName);
	}
	
	private void removeStoryFromServer(String storyName, ListView<String> listView) {
		listView.getSelectionModel().clearSelection();
		listView.getItems().remove(storyName);
	}
	
	private void addStoryFromServer(String storyName, ListView<String> listView){
		listView.getItems().add(storyName);
	}

}
