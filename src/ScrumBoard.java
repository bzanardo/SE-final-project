import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.util.Pair;

public class ScrumBoard extends Application {
	
	static Scene mainScene;
	
	static HashMap<String, UserStory> stringMap = new HashMap<>();
	static HashMap<String, ListView<String>> listViewMap = new HashMap<>();
	HashMap<ListView<String>, String> statusMap = new HashMap<>();
	static HashMap<String, String> doneStories = new HashMap<>();

	ListView<String> backlogView = new ListView<>();
	ListView<String> firstView = new ListView<>();
	ListView<String> secondView = new ListView<>();
	ListView<String> thirdView = new ListView<>();
	ListView<String> fourthView = new ListView<>();
	
	TextArea textBox = new TextArea();
	Label textBoxLabel = new Label("");
	Button editButton = new Button("Edit Story");
	Button deleteButton = new Button("Delete Story");
	
	UserStory selectedStory = new UserStory(null, 0, null, null);		// story selected by user to be edited
	
	static final DataFormat STRING_LIST = new DataFormat("StringList");
	
	static final String DRAGFROM = "DRAG_FROM";
	static final String DRAGTO = "DRAG_TO";
	static final String CREATE = "CREATE_STORY";
	static final String EDIT = "EDIT_STORY";
	static final String DELETE = "DELETE_STORY";
	static final String SET_STORY_DONE = "SET_DONE";
	
    BufferedReader in;
    PrintWriter out;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stringMap = initializeMap();
		doneStories = initializeDoneStoriesMap();
		
		// initialize listview map
		listViewMap.put("backlogView", backlogView);
		listViewMap.put("firstView", firstView);
		listViewMap.put("secondView", secondView);
		listViewMap.put("thirdView", thirdView);
		listViewMap.put("fourthView", fourthView);
		loadListViewMap();
		
		// initialize status map
		statusMap.put(backlogView, "Backlog");
		statusMap.put(firstView, "Not Started");
		statusMap.put(secondView, "In Progress");
		statusMap.put(thirdView, "Testing/Review");
		statusMap.put(fourthView, "Done");
		
		// Labels
		Label backlogLabel = new Label("BackLog: ");
		Label firstLabel = new Label("Not Started: ");
		Label secondLabel = new Label("In Progress: ");
		Label thirdLabel = new Label("Testing/Review: ");
		Label fourthLabel = new Label("Done: ");
		Label scrumLabel = new Label("Current Sprint");
		
		// Buttons
		Button addButton = new Button("Add New User Story");
		Button burndownChartBtn = new Button("Burndown Chart");
		
		// Set labels' positions
		backlogLabel.setTranslateX(70);
		backlogLabel.setTranslateY(75);
		firstLabel.setTranslateX(130);
		firstLabel.setTranslateY(75);
		secondLabel.setTranslateX(160);
		secondLabel.setTranslateY(75);
		thirdLabel.setTranslateX(-20);
		thirdLabel.setTranslateY(75);
		
		fourthLabel.setTranslateX(70);
		fourthLabel.setTranslateY(75);
		
		
		scrumLabel.setTranslateX(310);
		scrumLabel.setTranslateY(30);
		scrumLabel.setStyle("-fx-font: 20 arial;");
		
		//set textbox position
		textBox.setPrefHeight(300);
		textBox.setPrefWidth(400);
		textBox.setTranslateY(150);
		textBox.setEditable(false);
		
		textBoxLabel.setTranslateY(-20);
		textBoxLabel.setTranslateX(150);
		textBoxLabel.setStyle("-fx-font: 16 arial;");
		
		//Set listviews' postions
		backlogView.setPrefSize(200, 400);
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
		
		fourthView.setPrefSize(200, 400);
		//fourthView.setTranslateX(20);
		fourthView.setTranslateY(75);
		fourthView.setMaxWidth(200);
		
		// Set buttons' positions
		addButton.setTranslateX(25);
		addButton.setTranslateY(520);
		burndownChartBtn.setTranslateX(25);
		burndownChartBtn.setTranslateY(580);
		editButton.setTranslateY(10);
		editButton.setDisable(true);
		deleteButton.setTranslateY(40);
		deleteButton.setDisable(true);
		
		//backlogView.getItems().addAll(this.getUserStoryList());

		backlogView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		firstView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		secondView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		thirdView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		fourthView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		GridPane pane = new GridPane();

		pane.getChildren().add(addButton);
		pane.getChildren().add(burndownChartBtn);
		pane.add(editButton, 3, 4);
		pane.add(deleteButton, 3, 4);
		
		pane.addRow(1, backlogLabel, firstLabel, secondLabel, thirdLabel, fourthLabel);
		pane.addRow(0, scrumLabel);
		pane.add(textBoxLabel, 2, 4);
		
		pane.addRow(3, backlogView, firstView, secondView, thirdView, fourthView);
		pane.add(textBox, 2, 4);

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
		
		fourthView.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				dragDetected(event, fourthView);
			}
		});

		fourthView.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragOver(event, fourthView);
			}
		});

		fourthView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDropped(event, fourthView);
			}
		});

		fourthView.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				dragDone(event, fourthView);
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
		
		fourthView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				expandStory(fourthView);
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
        
        EventHandler<ActionEvent> editStoryEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	 GridPane form = new GridPane();
            	 form.setAlignment(Pos.TOP_LEFT);
            	 form.setHgap(10);
            	 form.setVgap(10);
            	 form.setPadding(new Insets(25, 25, 25, 25));
            	 
            	 Label storyNameLabel = new Label("Story Name:");
            	 TextField tf1 = new TextField();
            	 tf1.setEditable(false);
            	 tf1.setText(selectedStory.getName());	 
            	 form.add(storyNameLabel, 0, 0);
            	 form.add(tf1, 1, 0);
            	 
            	 Label storyPtsLabel = new Label("Story Points:");
            	 TextField tf2 = new TextField();
            	 tf2.setText(Integer.toString(selectedStory.getStoryPoints()));
            	 form.add(storyPtsLabel, 0, 1);
            	 form.add(tf2, 1, 1);
            	 
            	 Label author = new Label("Author:");
            	 TextField tf3 = new TextField();
            	 tf3.setText(selectedStory.getAuthor());
            	 form.add(author, 0, 2);
            	 form.add(tf3, 1, 2);
            	 
            	 Label comment = new Label("Add New Comment:");
            	 TextField tf4 = new TextField();
            	 form.add(comment, 0, 3);
            	 form.add(tf4, 1, 3);
            	 
            	 Button saveStory = new Button("Save");
            	 form.add(saveStory, 0, 4);
            
                 Scene secondScene = new Scene(form, 500, 500);
              
                 // New window (Stage)
                 Stage newWindow = new Stage();
                 newWindow.setTitle("Edit User Story");
                 newWindow.setScene(secondScene);
  
                 // Set position of second window, related to primary window.
                 newWindow.setX(stage.getX() + 200);
                 newWindow.setY(stage.getY() + 100);
  
                 newWindow.show();
                 
                 saveStory.setOnAction(new EventHandler<ActionEvent>() {
 					@Override
 					public void handle(ActionEvent event) {
 						Platform.runLater(new Runnable() {
 							@Override
 							public void run() {
 								editStory(selectedStory.getName(), Integer.valueOf(tf2.getText()), tf3.getText(), tf4.getText());
 							}

 						});
 						out.println(EDIT+","+tf1.getText()+","+tf2.getText()+","+tf3.getText()+tf4.getText());
 						newWindow.close();
 						textBox.clear();
 					}
 				});
            } 
        };
        
        EventHandler<ActionEvent> deleteStoryEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            {
            	ListView<String> listView = getListViewFromStatus(selectedStory.getStatus());
            	Platform.runLater(new Runnable() {
						@Override
						public void run() {
							System.out.println("delete story "+selectedStory.getName()+" from "+getListViewName(listView));
							deleteStory(selectedStory.getName(), listView);
						}
				});
            	out.println(DELETE+","+selectedStory.getName()+","+getListViewName(listView));
            }
        };
        
        EventHandler<ActionEvent> burndownChartEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
            	 final NumberAxis xAxis = new NumberAxis();
            	 xAxis.setLabel("Day in December (10-day sprint in December)");
            	 final NumberAxis yAxis = new NumberAxis();
            	 yAxis.setLabel("Number of tasks left");
            	 yAxis.setScaleY(1);
            	 final LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
            	 chart.setTitle("Burndown Chart");
            	 
            	 XYChart.Series<Number, Number> fixed = new XYChart.Series<Number, Number>();
            	 fixed.setName("Optimal Task Distribution");
            	 XYChart.Series<Number, Number> actual = new XYChart.Series<Number, Number>();
            	 actual.setName("Actual Task Distribution");
            	 
            	 HashMap<Integer, Integer> tasksLeftOnDay = new HashMap<>();
            	 
            	 int numTasks = doneStories.size();
            	 for(int i=0; i<numTasks; i++){
            		 @SuppressWarnings("unchecked")
            		 Map.Entry<String, String> entry = (Map.Entry<String, String>)doneStories.entrySet().toArray()[i];
            		 String dateString = entry.getValue();
            		 int day = Integer.valueOf(dateString.split("/")[1]);
            		 if(!tasksLeftOnDay.containsKey(day)){
            			 tasksLeftOnDay.put(day, numTasks);
            		 }
            		 tasksLeftOnDay.put(day, tasksLeftOnDay.get(day)-1);
            		 
            		 if(numTasks <= 10){
            			 fixed.getData().add(new XYChart.Data<Number, Number>(i, numTasks-i));
            		 }
            	 }
            	 if(numTasks <= 10){
            		 for(int i=numTasks; i<10; i++){
            			 fixed.getData().add(new XYChart.Data<Number, Number>(i, 0));
            		 }
        		 }else{
        			 int tasksPerDay = (int) Math.ceil((double)numTasks/10.0);
        			 for(int i=0; i<10; i++){
            			 fixed.getData().add(new XYChart.Data<Number, Number>(i+1, Math.max(0,tasksPerDay*i)));
        			 }
        		 }
            	 
            	 actual.getData().add(new XYChart.Data<Number, Number>(0, numTasks));
            	 for(Map.Entry<Integer, Integer> entry : tasksLeftOnDay.entrySet()){
            		 for(int i=entry.getKey()-1; i>=0; i--){
	            		 if(tasksLeftOnDay.containsKey(i)){
	            			 tasksLeftOnDay.put(entry.getKey(), tasksLeftOnDay.get(i) - (numTasks-entry.getValue()));
	            			 break;
	            		 }
            		 }
            		 actual.getData().add(new XYChart.Data<Number, Number>(entry.getKey(), entry.getValue()));
            	 }
            
                 Scene secondScene = new Scene(chart, 500, 500);
                 chart.getData().add(fixed);
                 chart.getData().add(actual);
              
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
        
        addButton.setOnAction(createStoryEvent);
        burndownChartBtn.setOnAction(burndownChartEvent);
        editButton.setOnAction(editStoryEvent);
        deleteButton.setOnAction(deleteStoryEvent);

		Pane root = new Pane();
		root.setPrefSize(1200, 750);
		root.getChildren().addAll(pane);
		
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
	
	@SuppressWarnings("unchecked")
	private HashMap<String, UserStory> initializeMap() {
		HashMap<String, UserStory> map = new HashMap<String, UserStory>();
		try {
	         FileInputStream fis = new FileInputStream("Stories.ser");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         map = (HashMap<String, UserStory>) ois.readObject();
	         ois.close();
	         fis.close();
		}catch(FileNotFoundException e){
			System.out.println("No User Story file saved");
		}catch(Exception e){
	       e.printStackTrace();
	    }
		
		/*HashMap<String, UserStory> map = new HashMap<>();
		
		UserStory story1 = new UserStory("Create UI", 1, "Bia", "Not Started");
		UserStory story2 = new UserStory("Story 2", 1, "Dan", "Not Started");
		
		map.put(story1.getName(), story1);
		map.put(story2.getName(), story2);*/
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, String> initializeDoneStoriesMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
	         FileInputStream fis = new FileInputStream("DoneStories.ser");
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         map = (HashMap<String, String>) ois.readObject();
	         ois.close();
	         fis.close();
		}catch(FileNotFoundException e){
			System.out.println("No Done Stories file saved");
		}catch(Exception e){
	       e.printStackTrace();
	    }
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private void loadListViewMap(){
		try{
			FileInputStream fis = new FileInputStream("ListViews.ser");
	        ObjectInputStream ois = new ObjectInputStream(fis);
	        HashMap<String, String[]> obj = (HashMap<String, String[]>)ois.readObject();
	        for(String listViewName : obj.keySet()){
	        	for(String storyName : obj.get(listViewName)){
	        		listViewMap.get(listViewName).getItems().add(storyName);
	        	}
	        }
	        ois.close();
	        fis.close();
		}catch(FileNotFoundException e){
			System.out.println("No ListView file saved");
		}catch(Exception e){
	         e.printStackTrace();
	    }
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
        	System.out.println("Failed to connect to server, exiting");
        	e.printStackTrace();
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
            	else if(commands[0].equals(EDIT)){
            		String storyName = commands[1];
            		Platform.runLater(new Runnable() {	// run on JavaFX main thread
    					@Override
    					public void run() {
    						editStory(storyName, Integer.valueOf(commands[2]), commands[3], commands[4]);
    					}
    				});
            	}
            	
            	else if(commands[0].equals(DELETE)){
            		String storyName = commands[1];
            		Platform.runLater(new Runnable() {	// run on JavaFX main thread
    					@Override
    					public void run() {
    						deleteStory(storyName, listViewMap.get(commands[2]));
    					}
    				});
            	}
            	
            	else if(commands[0].equals(SET_STORY_DONE)){
            		String storyName = commands[1];
            		Platform.runLater(new Runnable() {	// run on JavaFX main thread
    					@Override
    					public void run() {
    						addStoryFromServer(storyName, fourthView);
    						setStoryDone(storyName, commands[2]);
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
		textBox.clear();
		
		if (name == null) {
			textBoxLabel.setText("");
			editButton.setDisable(true);
		}
		
		else {
			editButton.setDisable(false);
			deleteButton.setDisable(false);
			
			UserStory story = stringMap.get(name);
			selectedStory = story;
			textBoxLabel.setText(name);
			textBox.appendText(" Author: " + story.getAuthor() + 
							"\n Story Points: " + story.getStoryPoints() + "\n Status: " 
							+ story.getStatus() + "\n Comments: " + story.getComments());
		}
			
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
	
	private void setStoryDone(String storyName, String date){
		doneStories.put(storyName, date);
	}
	
	private void askForDateDone(String storyName){
		GridPane form = new GridPane();
   	 	form.setAlignment(Pos.TOP_LEFT);
   	 	form.setHgap(10);
   	 	form.setVgap(10);
   	 	form.setPadding(new Insets(25, 25, 25, 25));
   	 	
   	 	ObservableList<String> dates = 
   		    FXCollections.observableArrayList(
   		        "12/01",
   		        "12/02",
   		        "12/03",
   		        "12/04",
   		        "12/05",
   		        "12/06",
   		        "12/07",
   		        "12/08",
   		        "12/09",
   		        "12/10"
   		    );
   	 
   	 	Label dateLabel = new Label("Date task was completed:");
   	 	final ComboBox<String> dropdown = new ComboBox<String>(dates); 
   	 	form.add(dateLabel, 0, 0);
   	 	form.add(dropdown, 1, 0);
   	 
	   	Button doneBtn = new Button("Done");
	   	form.add(doneBtn, 0, 3);
   
		Scene secondScene = new Scene(form, 500, 500);
		 
		Stage newWindow = new Stage();
		newWindow.setTitle("Chose date task "+storyName+" was completed");
		newWindow.setScene(secondScene);
		
		// Set position of second window, related to primary window.
		//newWindow.setX(stage.getX() + 200);
		//newWindow.setY(stage.getY() + 100);
		
		newWindow.show();
		
		doneBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						setStoryDone(storyName, dropdown.getSelectionModel().getSelectedItem());
					}
		
				});
				out.println(SET_STORY_DONE+","+storyName+","+dropdown.getSelectionModel().getSelectedItem());
				newWindow.close();
			}
		});
	}
	

	@SuppressWarnings("unchecked")
	private void dragDropped(DragEvent event, ListView<String> listView) {
		boolean dragCompleted = false;

		Dragboard dragboard = event.getDragboard();

		if (dragboard.hasContent(STRING_LIST)) {
			ArrayList<String> list = (ArrayList<String>) dragboard.getContent(STRING_LIST);
			UserStory story = stringMap.get(list.get(0));
			story.setStatus(statusMap.get(listView)); 
					
			listView.getItems().addAll(list);
			dragCompleted = true;
			
			
			if(listView.equals(fourthView)){
				askForDateDone(list.get(0)); // this calls out.println(SET_STORY_DONE...
			}else{
				out.println(DRAGTO+","+list.get(0)+","+getListViewName(listView));
			}
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
		UserStory newStory = new UserStory(storyName, storyPoints, author, "Backlog");
		stringMap.put(storyName, newStory);
		backlogView.getItems().add(storyName);
	}
	
	private void removeStoryFromServer(String storyName, ListView<String> listView) {
		listView.getSelectionModel().clearSelection();
		listView.getItems().remove(storyName);
	}
	
	private void addStoryFromServer(String storyName, ListView<String> listView){
		listView.getItems().add(storyName);
		UserStory story = stringMap.get(storyName);
		story.setStatus(statusMap.get(listView)); 
	}
	
	private void editStory(String storyName, Integer storyPoints, String author, String comments) {
		UserStory story = stringMap.get(storyName);
		story.setStoryPoints(storyPoints);
		story.setAuthor(author);
		story.setComments(comments);
	}
	
	private void deleteStory(String storyName,  ListView<String> listView) { 	
		listView.getItems().remove(storyName);
    	stringMap.remove(storyName);
    	textBox.clear();
    	textBoxLabel.setText("");
    	selectedStory.setNull();
	}
	
	private ListView<String> getListViewFromStatus(String storyStatus) {
		for (HashMap.Entry<ListView<String>, String> entry : statusMap.entrySet()) {
    		if (entry.getValue().equals(storyStatus)) {
    			return entry.getKey();
    		}
    	}
		
		return null;
	}

	// Override stop method (when window is closed) to persist board
	@Override
	public void stop() {
		try{
			File storiesFile = new File("Stories.ser");
			if(storiesFile.exists())
				storiesFile.delete();
			File listviewsFile = new File("ListViews.ser");
			if(listviewsFile.exists())
				listviewsFile.delete();
			File doneStoriesFile = new File("DoneStories.ser");
			if(doneStoriesFile.exists())
				doneStoriesFile.delete();
			
			FileOutputStream fos = new FileOutputStream("Stories.ser");
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(stringMap);
		    oos.close();
		    fos.close();
		    
		    HashMap<String, String[]> mapOfStrings = new HashMap<String, String[]>();
		    for(String listViewName : listViewMap.keySet()){
		    	String[] tmp = new String[listViewMap.get(listViewName).getItems().size()];
		    	int i = 0;
		    	for(String s : listViewMap.get(listViewName).getItems()){
		    		tmp[i] = s;
		    		i++;
		    	}
		    	mapOfStrings.put(listViewName, tmp);
		    }
		    FileOutputStream fos2 = new FileOutputStream("ListViews.ser");
		    ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
		    oos2.writeObject(mapOfStrings);
		    oos2.close();
		    fos2.close();
		    
		    FileOutputStream fos3 = new FileOutputStream("DoneStories.ser");
		    ObjectOutputStream oos3 = new ObjectOutputStream(fos3);
		    oos3.writeObject(doneStories);
		    oos3.close();
		    fos3.close();
		    
		    System.out.println("Board saved");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
