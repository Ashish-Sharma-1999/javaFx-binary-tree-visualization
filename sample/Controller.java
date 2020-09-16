package sample;

import javafx.animation.*;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
	@FXML
	Pane graphPane;
	@FXML
	Label addRoot,addMode,displayMode,editMode,dfsMode,bfsMode,bestFirst,moveUp;
	@FXML
	TextField enterNumber;

	static int NODE_RADIUS=50;
	static int EMPTY_NODE_RADIUS=30;
	static int VERT_DIST=100;
	static RadialGradient blackGradientFill= new RadialGradient(0,0,0.5,0.5,0.6,true, CycleMethod.NO_CYCLE,new Stop(0,Color.BLACK),new Stop(1,Color.TRANSPARENT));
	static Color EMPTY_NODE_COLOR=Color.rgb(0,0,255,0.9);
	static Color NODE_COLOR=Color.rgb(255,0,0,0.9);
	static Color ACCESED_NODE=Color.rgb(0,255,0,0.9);
	static RadialGradient NODE_STROKE= new RadialGradient(0,0,0.5,0.5,0.6,true, CycleMethod.NO_CYCLE,new Stop(0.7,Color.CADETBLUE),new Stop(1,Color.TRANSPARENT));
	static Background selected=new Background(new BackgroundFill(Color.rgb(0,200,200,0.2), CornerRadii.EMPTY, Insets.EMPTY));
	static Background stackBG=new Background(new BackgroundFill(Color.rgb(0,0,0,0.6), CornerRadii.EMPTY, Insets.EMPTY));
	static Background ListElementBG=new Background(new BackgroundFill(Color.rgb(255,0,0,0.5), CornerRadii.EMPTY, Insets.EMPTY));
	static Background stackLabelBg=new Background(new BackgroundFill(Color.rgb(0,255,0,0.8), CornerRadii.EMPTY, Insets.EMPTY));
	double stageWidth=1000;
	double distanceFromTop=150;
	public boolean animationRunning=false;
	boolean notEditingData=true;
	boolean rootAllow=true;
	static boolean addNodeMode=true;
	static int zoomIn=0;
	Border whiteBorder=new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	Node rootNode;
	Node tempRootNode;
	Label selectedMode;

	Stack stackdfs;
	Queue queuebfs,outputQueue;
	PriorityQueue priorityQueue;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		selectedMode=addMode;
		enterNumber.setDisable(true);
		selectedMode.setBackground(selected);

		addRoot.setOnMouseClicked(event->{
			if(rootAllow){
				stackdfs=new Stack(100,650);
				queuebfs=new Queue(100,650,"Queue");
				priorityQueue=new PriorityQueue(100,650);
				outputQueue=new Queue(100, 750,"Output");
				//queue bfs
				System.out.println("root node");
				tempRootNode=rootNode=new Node(stageWidth/2,null,0);
				rootAllow=false;
			}
		});
		addMode.setOnMouseClicked(event-> {
			addNodeMode=true;
			addMode.setBackground(selected);
			selectedMode.setBackground(null);
			selectedMode=addMode;
		});
		displayMode.setOnMouseClicked(event-> {
			addNodeMode=false;
			displayMode.setBackground(selected);
			selectedMode.setBackground(null);
			selectedMode=displayMode;
		});
		editMode.setOnMouseClicked(event-> {
			editMode.setBackground(selected);
			selectedMode.setBackground(null);
			selectedMode=editMode;
		});
		dfsMode.setOnMouseClicked(event->{
			SequentialTransition seqTransition=new SequentialTransition();
			Timeline timeline=new Timeline();
			dfsMode.setBackground(selected);
			selectedMode.setBackground(null);
			stackdfs.showStack(true,timeline);
			seqTransition.getChildren().add(timeline);
			rootNode.depthFirstSearch(seqTransition);
			seqTransition.play();
			seqTransition.setOnFinished(SeqEvent-> {
				Timeline timeline1=new Timeline();
				rootNode.afterTravesal();
				dfsMode.setBackground(null);
				selectedMode.setBackground(selected);
				stackdfs.showStack(false,timeline1);
				timeline1.play();
			});
		});
		bfsMode.setOnMouseClicked(event->{
			SequentialTransition seqTransition=new SequentialTransition();
			Timeline timeline=new Timeline();
			bfsMode.setBackground(selected);
			selectedMode.setBackground(null);
			queuebfs.showQueue(true,timeline);
			seqTransition.getChildren().add(timeline);
			rootNode.breadthFirstSearch(seqTransition);
			seqTransition.play();
			seqTransition.setOnFinished(SeqEvent-> {
				Timeline timeline1=new Timeline();
				rootNode.afterTravesal();
				bfsMode.setBackground(null);
				selectedMode.setBackground(selected);
				queuebfs.showQueue(false,timeline1);
				timeline1.play();
			});
		});
		bestFirst.setOnMouseClicked(event->{
			SequentialTransition seqTransition=new SequentialTransition();
			Timeline timeline=new Timeline();
			bestFirst.setBackground(selected);
			selectedMode.setBackground(null);
			priorityQueue.showQueue(true,timeline);
			seqTransition.getChildren().add(timeline);
			enterNumber.setDisable(false);
			enterNumber.setText("");
			enterNumber.setOnKeyTyped(numberevent->bestFirst.setText("Find= "+String.valueOf(getInteger())));
			enterNumber.setOnAction(setevent->{
				notEditingData=true;
				rootNode.bestFirstSearch(seqTransition,getInteger());
				enterNumber.setDisable(true);
				seqTransition.play();
				seqTransition.setOnFinished(SeqEvent-> {
					Timeline timeline1=new Timeline();
					rootNode.afterTravesal();
					bestFirst.setBackground(null);
					selectedMode.setBackground(selected);
					priorityQueue.showQueue(false,timeline1);
					timeline1.play();
				});
			});
		});
		moveUp.setOnMouseClicked(event->{
			zoomOutNode(0).play();
		});
	}

	int getInteger(){
		String temp=enterNumber.getText();
		int number=0;
		char character;
		for(int i=0;i<temp.length();i++){
			character=temp.charAt(i);
			if(character>='0' && character<='9'){
				System.out.println(character);
				number=number*10+(character-'0');
			}
		}
		return number;
	}


	void zoomOutNode(SequentialTransition sequentialTransition){
		if(zoomIn>0){
			Timeline timelinea=new Timeline();
			Timeline timelineb=new Timeline();
			ParallelTransition parallelTransition=new ParallelTransition();
			System.out.println(zoomIn);
			System.out.println("zoomOut");
			zoomIn--;
			tempRootNode.parent.updateLevelOfNodes();
			tempRootNode.parent.displayBelow(stageWidth/2,timelineb);
			tempRootNode.parent.resetToShow();
			tempRootNode.parent.hideNode(timelinea,false);

			timelinea.setDelay(Duration.seconds(0.75));
			parallelTransition.getChildren().addAll(timelinea,timelineb);
			tempRootNode=tempRootNode.parent;
			sequentialTransition.getChildren().add(parallelTransition);
		}
	}
	SequentialTransition zoomOutNode(double delay){
		SequentialTransition sequentialTransition=new SequentialTransition();
		zoomOutNode(sequentialTransition);
		sequentialTransition.setDelay(Duration.seconds(delay));
		return sequentialTransition;
	}

	class ListElement extends Label{
		Node node;
		Rectangle box;
		ListElement nextElement;
		int positionX;
		ListElement(){};
		ListElement(Node parm,ListElement nextEle, double x, double y){
			setListElement(parm,nextEle,x,y);
		}
		
		void setListElement(Node parm,ListElement nextEle, double x, double y){
			node=parm;
			box=new Rectangle(60,50);
			box.setLayoutX(x);
			box.setLayoutY(y);
			box.setFill(Color.rgb(255, 0,0,0.7));
			box.setOpacity(0);
			setOpacity(0);
			setPrefSize(60,50);
			setTranslateX(-30);
			setTranslateY(-25);
			//setPadding(new Insets(5,0,5,0));
			setText(String.valueOf(node.data));
			setTextFill(Color.WHITE);
			setAlignment(Pos.CENTER);
			setFont(new Font(25));

			nextElement=nextEle;

			positionX=(int)x;
			graphPane.getChildren().addAll(box,this);
		}

		void moveElement(Timeline timeline,boolean fill){
			positionX+=fill ? 65 : -65;
			KeyFrame kfx=generateKeyFrames(layoutXProperty(),positionX,0.5);
			KeyFrame kfbx=generateKeyFrames(box.layoutXProperty(),positionX,0.5);
			timeline.getKeyFrames().addAll(kfx,kfbx);

			if(nextElement!=null)
				nextElement.moveElement(timeline,fill);
		}
	}
	
	class PriorityListElement extends ListElement{
		int weight;
		
		PriorityListElement(Node parm,ListElement nextEle, double x, double y, int w){
			weight=w;
			setListElement(parm,nextEle,x,y);
		}
	}
	class Stack{
		ListElement root,ptr;
		Label nameLabel;
		Rectangle stackArea;
		double nodeX,nodeY;
		Stack(double x,double y){
			double labelWidth=200;
			double stackHeight=60;
			nodeX=x-60+labelWidth;
			nodeY=y+5;
			stackArea=new Rectangle(x+labelWidth,y,stageWidth-2*x-labelWidth,stackHeight);
			stackArea.setFill(Color.rgb(0,0,0,0.7));
			stackArea.setOpacity(0);

			nameLabel=new Label("Stack");
			nameLabel.setTextFill(Color.WHITE);
			nameLabel.setAlignment(Pos.CENTER);
			nameLabel.setFont(new Font(25));
			nameLabel.setPrefSize(labelWidth,stackHeight);
			nameLabel.setLayoutY(y);
			nameLabel.setLayoutX(x);
			nameLabel.setBackground(stackLabelBg);
			nameLabel.opacityProperty().bindBidirectional(stackArea.opacityProperty());
			graphPane.getChildren().addAll(nameLabel,stackArea);
		}

		void showStack(boolean show,Timeline timeline){
			KeyFrame kfo=generateKeyFrames(stackArea.opacityProperty(),show ? 1 : 0,1);
			KeyFrame kfoo=generateKeyFrames(outputQueue.queueArea.opacityProperty(),show ? 1 : 0,1);
			timeline.getKeyFrames().addAll(kfo,kfoo);
		}
		void push(Node parm,SequentialTransition sequentialTransition){
			if(parm==null)
				return;
			if(parm.accesedInDFS)
				return;

			Timeline timeline=new Timeline();

			if(parm.filled){
				System.out.println("push"+parm.data);
				parm.accesedInDFS=true;

				ptr=new ListElement(parm,ptr,nodeX,nodeY);
				KeyFrame kf12=generateKeyFrames(ptr.layoutXProperty(),parm.getCenterX(),0);
				KeyFrame kf23 =generateKeyFrames(ptr.layoutYProperty(),parm.getCenterY(),0);
				KeyFrame kf34=generateKeyFrames(ptr.opacityProperty(),1,0.1);
				KeyFrame kflb=generateKeyFrames(ptr.borderProperty(),whiteBorder,0.1);
				KeyFrame kfbo=generateKeyFrames(ptr.box.opacityProperty(),1,0.5,Interpolator.EASE_OUT);
				KeyFrame kfnc=generateKeyFrames(parm.fillProperty(),Color.GREEN,0.5);
				KeyFrame kfrx=generateKeyFrames(ptr.layoutXProperty(),nodeX,0.5);
				KeyFrame kfry=generateKeyFrames(ptr.layoutYProperty(),nodeY,0.5);
				KeyFrame kftx=generateKeyFrames(ptr.translateXProperty(),0,0.5);
				KeyFrame kfty=generateKeyFrames(ptr.translateYProperty(),0,0.5);
				timeline.getKeyFrames().addAll(kf12,kf23,kf34,kflb,kfnc,kfbo,kfrx,kfry,kftx,kfty);

				if(ptr!=null){
					Timeline timeline1=new Timeline();
					ptr.moveElement(timeline1,true);
					sequentialTransition.getChildren().addAll(timeline,timeline1);
				}

			}else{
				KeyFrame kfnc=generateKeyFrames(parm.fillProperty(),Color.GREEN,0.5);
				timeline.getKeyFrames().add(kfnc);
				timeline.setCycleCount(2);
				timeline.setAutoReverse(true);
				sequentialTransition.getChildren().add(timeline);
			}
		}

		Node pop(SequentialTransition sequentialTransition){

			if(ptr!=null){
				System.out.println("pop"+ptr.node.data);
				Timeline timeline=new Timeline();
				KeyFrame kfc=generateKeyFrames(ptr.node.fillProperty(),Color.ORANGE,0.5);
				KeyFrame kfb=generateKeyFrames(ptr.box.opacityProperty(),0,0.5,Interpolator.EASE_IN);
				KeyFrame kfo=generateKeyFrames(ptr.opacityProperty(),0,0.5);
				timeline.getKeyFrames().addAll(kfb,kfo,kfc);
				timeline.setDelay(Duration.seconds(0.5));

				if(ptr!=null){
					Timeline timeline1=new Timeline();
					ptr.moveElement(timeline1,false);
					sequentialTransition.getChildren().add(timeline1);
				}
				sequentialTransition.getChildren().add(timeline);
				Node temp=ptr.node;
				ptr=ptr.nextElement;

				return temp;
			}
			return null;
		}
	}

	class Queue{
		ListElement front,rear;//intially null
		Label nameLabel;
		Rectangle queueArea;
		double nodeX,nodeY;
		Queue(){}
		Queue(double x,double y,String name){
			setQueue(x,y,name);
		}
		void setQueue(double x,double y,String name){
			double labelWidth=200;
			double stackHeight=60;
			nodeX=x-60+labelWidth;
			nodeY=y+5;
			queueArea=new Rectangle(x+labelWidth,y,stageWidth-2*x-labelWidth,stackHeight);
			queueArea.setFill(Color.rgb(0,0,0,0.7));
			queueArea.setOpacity(0);

			nameLabel=new Label(name);
			nameLabel.setTextFill(Color.WHITE);
			nameLabel.setAlignment(Pos.CENTER);
			nameLabel.setFont(new Font(25));
			nameLabel.setPrefSize(labelWidth,stackHeight);
			nameLabel.setLayoutY(y);
			nameLabel.setLayoutX(x);
			nameLabel.setBackground(stackLabelBg);
			nameLabel.opacityProperty().bindBidirectional(queueArea.opacityProperty());
			graphPane.getChildren().addAll(nameLabel,queueArea);
		}
		void printQueue(ListElement ele){
			System.out.println(ele);
			if(ele!=null)
				printQueue(ele.nextElement);
		}

		void showQueue(boolean show,Timeline timeline){
			KeyFrame kfo=generateKeyFrames(queueArea.opacityProperty(),show ? 1 : 0,1);
			KeyFrame kfoo=generateKeyFrames(outputQueue.queueArea.opacityProperty(),show ? 1 : 0,1);
			timeline.getKeyFrames().addAll(kfo,kfoo);
		}
		void addElement(Node parm,SequentialTransition sequentialTransition){
			if(parm==null)
				return;
			/*if(parm.accesedInDFS)
				return;*/

			Timeline timeline=new Timeline();

			if(parm.filled){
				System.out.println("add"+parm.data);
				nodeX+=65;
				if(front==null || rear==null) {
					front=rear=new ListElement(parm,null,nodeX,nodeY);
					System.out.println("front raer null");
				}
				else{
					System.out.println("not null");
					rear.nextElement=new ListElement(parm,null,nodeX,nodeY);
					rear=rear.nextElement;
				}
				KeyFrame kf12=generateKeyFrames(rear.layoutXProperty(),parm.getCenterX(),0.0);
				KeyFrame kf23 =generateKeyFrames(rear.layoutYProperty(),parm.getCenterY(),0.0);
				KeyFrame kf34=generateKeyFrames(rear.opacityProperty(),1,0.1);
				KeyFrame kflb=generateKeyFrames(rear.borderProperty(),whiteBorder,0.1);
				KeyFrame kfbo=generateKeyFrames(rear.box.opacityProperty(),1,0.5,Interpolator.EASE_OUT);
				KeyFrame kfnc=generateKeyFrames(parm.fillProperty(),Color.GREEN,0.5);
				KeyFrame kfrx=generateKeyFrames(rear.layoutXProperty(),nodeX,0.5);
				KeyFrame kfry=generateKeyFrames(rear.layoutYProperty(),nodeY,0.5);
				KeyFrame kftx=generateKeyFrames(rear.translateXProperty(),0,0.5);
				KeyFrame kfty=generateKeyFrames(rear.translateYProperty(),0,0.5);
				timeline.getKeyFrames().addAll(kf12,kf23,kf34,kflb,kfnc,kfbo,kfrx,kfry,kftx,kfty);
				sequentialTransition.getChildren().add(timeline);
			}else{
				KeyFrame kfnc=generateKeyFrames(parm.fillProperty(),Color.GREEN,0.5);
				timeline.getKeyFrames().add(kfnc);
				timeline.setCycleCount(2);
				timeline.setAutoReverse(true);
				sequentialTransition.getChildren().add(timeline);
			}
		}

		Node deleteElement(SequentialTransition sequentialTransition){

			if(front!=null){
				System.out.println("remove"+front.node.data);
				Timeline timeline=new Timeline();
				KeyFrame kfc=generateKeyFrames(front.node.fillProperty(),Color.ORANGE,0.5);
				KeyFrame kfb=generateKeyFrames(front.box.opacityProperty(),0,0.5,Interpolator.EASE_IN);
				KeyFrame kfo=generateKeyFrames(front.opacityProperty(),0,0.5);
				timeline.getKeyFrames().addAll(kfb,kfo,kfc);
				timeline.setDelay(Duration.seconds(0.5));

				if(front!=null){
					Timeline timeline1=new Timeline();
					front.moveElement(timeline1,false);
					nodeX-=65;
					sequentialTransition.getChildren().add(timeline1);
				}
				sequentialTransition.getChildren().add(timeline);
				Node temp=front.node;
				front=front.nextElement;

				return temp;
			}
			return null;
		}
	}
	
	class PriorityQueue extends Queue{
		PriorityQueue(double x,double y){
			setQueue(x,y,"Priority Queue");
		}

		@Override
		void addElement(Node parm, SequentialTransition sequentialTransition) {
			if(parm==null)
				return;

			Timeline timeline=new Timeline();

			if(parm.filled){
				System.out.println("add"+parm.data);
				nodeX=0;
				int w=(int)Math.random()*10;
				PriorityListElement ptr;
				if(front==null || rear==null) {
					front=rear=ptr=new PriorityListElement(parm,null,nodeX,nodeY,w);
					System.out.println("front raer null");
				}
				else{
					System.out.println("not null");
					ptr=(PriorityListElement)front;
					if(w>ptr.weight){
						do{
							nodeX+=65;
							ptr=(PriorityListElement)ptr.nextElement;
						}while(w>ptr.weight);
					}
					ptr.moveElement(timeline,true);
					ptr.nextElement=new PriorityListElement(parm,ptr.nextElement,nodeX,nodeY,w);
				}

				KeyFrame kf12=generateKeyFrames(ptr.layoutXProperty(),parm.getCenterX(),0.0);
				KeyFrame kf23 =generateKeyFrames(ptr.layoutYProperty(),parm.getCenterY(),0.0);
				KeyFrame kf34=generateKeyFrames(ptr.opacityProperty(),1,0.1);
				KeyFrame kflb=generateKeyFrames(ptr.borderProperty(),whiteBorder,0.1);
				KeyFrame kfbo=generateKeyFrames(ptr.box.opacityProperty(),1,0.5,Interpolator.EASE_OUT);
				KeyFrame kfnc=generateKeyFrames(parm.fillProperty(),Color.GREEN,0.5);
				KeyFrame kfrx=generateKeyFrames(ptr.layoutXProperty(),nodeX,0.5);
				KeyFrame kfry=generateKeyFrames(ptr.layoutYProperty(),nodeY,0.5);
				KeyFrame kftx=generateKeyFrames(ptr.translateXProperty(),0,0.5);
				KeyFrame kfty=generateKeyFrames(ptr.translateYProperty(),0,0.5);
				timeline.getKeyFrames().addAll(kf12,kf23,kf34,kflb,kfnc,kfbo,kfrx,kfry,kftx,kfty);
				sequentialTransition.getChildren().add(timeline);
			}else{
				KeyFrame kfnc=generateKeyFrames(parm.fillProperty(),Color.GREEN,0.5);
				timeline.getKeyFrames().add(kfnc);
				timeline.setCycleCount(2);
				timeline.setAutoReverse(true);
				sequentialTransition.getChildren().add(timeline);
			}
		}
	}
	class Node extends Circle {
		int data;
		int level;
		Node leftNode;
		Node rightNode;
		Node parent;
		boolean filled;
		boolean toShow;
		boolean accesedInDFS;
		Line leftSide,rightSide;
		Label dataLabel;
		Circle blackGradient;
		Node(double Position,Node parent,int level){
			accesedInDFS=false;
			this.parent=parent;
			double vertPosition=VERT_DIST*level+distanceFromTop;
			this.level=level;
			filled=false;
			toShow=true;
			setStrokeType(StrokeType.OUTSIDE);
			setStrokeWidth(10);
			setRadius(EMPTY_NODE_RADIUS);
			setFill(EMPTY_NODE_COLOR);
			setCenterX(Position);
			setCenterY(vertPosition);
			leftSide=new Line(Position,vertPosition, Position-1,vertPosition+1);
			rightSide=new Line(Position,vertPosition, Position+1,vertPosition+1);
			leftSide.setStroke(Color.WHITE);
			rightSide.setStroke(Color.WHITE);
			//start point left and right side will always b aligned to center of node
			leftSide.startXProperty().bindBidirectional(centerXProperty());
			leftSide.startYProperty().bindBidirectional(centerYProperty());
			rightSide.startXProperty().bindBidirectional(centerXProperty());
			rightSide.startYProperty().bindBidirectional(centerYProperty());
			//leftside and right side end points will have same y coordinates
			rightSide.endYProperty().bindBidirectional(leftSide.endYProperty());
			rightSide.opacityProperty().bindBidirectional(leftSide.opacityProperty());
			//for dataLabel
			dataLabel=new Label("");
			dataLabel.setPrefSize(60,40);
			dataLabel.setAlignment(Pos.CENTER);
			dataLabel.setFont(new Font(25));
			dataLabel.setTextFill(Color.WHITE);

			//dataLable will always be on the center of the circle
			dataLabel.layoutYProperty().bindBidirectional(centerYProperty());
			dataLabel.layoutXProperty().bindBidirectional(centerXProperty());
			dataLabel.opacityProperty().bindBidirectional(opacityProperty());
			dataLabel.setTranslateX(-30);
			dataLabel.setTranslateY(-20);
			dataLabel.setMouseTransparent(true);

			blackGradient=new Circle();
			blackGradient.setFill(blackGradientFill);
			blackGradient.centerXProperty().bind(centerXProperty());
			blackGradient.centerYProperty().bind(centerYProperty());
			blackGradient.opacityProperty().bind(opacityProperty());
			blackGradient.radiusProperty().bind(radiusProperty());
			blackGradient.setMouseTransparent(true);
			graphPane.getChildren().addAll(leftSide,rightSide,this,blackGradient,dataLabel);

			setOnMouseClicked(event-> mouseOperations());
		}

		int getInteger(){
			String temp=enterNumber.getText();
			int number=0;
			char character;
			for(int i=0;i<temp.length();i++){
				character=temp.charAt(i);
				if(character>='0' && character<='9'){
					System.out.println(character);
					number=number*10+(character-'0');
				}
			}
			return number;
		}

		void editNodeData(){
			notEditingData=false;
			setStroke(NODE_STROKE);
			enterNumber.setDisable(false);
			enterNumber.setText("");
			enterNumber.setOnKeyTyped(event->dataLabel.setText(String.valueOf(getInteger())));
			enterNumber.setOnAction(event->{
				notEditingData=true;
				this.data=getInteger();
				setStroke(Color.TRANSPARENT);
				enterNumber.setDisable(true);
			});
		}

		void fillNode(double position,Timeline timeline){
			int noOfNodes=(int)Math.pow(2,level+2);
			double offset=stageWidth/noOfNodes;
			double leftNodeX=position-offset;
			double rightNodeX=position+offset;
			double nodeY=(level+1)*VERT_DIST+distanceFromTop;

			filled=true;

			leftNode=new Node(leftNodeX,this,level+1);
			rightNode=new Node(rightNodeX,this,level+1);
			//graphPane.getChildren().addAll(leftNode,rightNode,leftNode.dataLabel,rightNode.dataLabel);

			KeyValue kvc=new KeyValue(fillProperty(),NODE_COLOR);
			KeyValue kvr=new KeyValue(radiusProperty(),NODE_RADIUS);
			KeyValue kvllx=new KeyValue(leftSide.endXProperty(),leftNodeX);
			KeyValue kvlrx=new KeyValue(rightSide.endXProperty(),rightNodeX);
			KeyValue kvlry=new KeyValue(rightSide.endYProperty(),nodeY); //left side is already binded

			KeyFrame kfc=new KeyFrame(Duration.millis(500),kvc);
			KeyFrame kfr=new KeyFrame(Duration.millis(500),kvr);
			KeyFrame kfllx=new KeyFrame(Duration.millis(500),kvllx);
			KeyFrame kflry=new KeyFrame(Duration.millis(500),kvlry);
			KeyFrame kflrx=new KeyFrame(Duration.millis(500),kvlrx);
			timeline.getKeyFrames().addAll(kfc,kfr,kfllx,kflrx,kflry);
		}

		void resetToShow(){
			toShow=false;
			if(filled){
				leftNode.resetToShow();
				rightNode.resetToShow();
			}
		}
		void setToShow(){
			toShow=true;
			if(filled){
				leftNode.setToShow();
				rightNode.setToShow();
			}
		}

		void updateLevelOfAboveNodes(int newLevel){
			level=newLevel;
			if(parent!=null){
				parent.updateLevelOfAboveNodes(newLevel-1);
			}
		}
		void updateLevelOfBelowNodes(int newLevel){
			level=newLevel;
			if(filled){
				leftNode.updateLevelOfBelowNodes(newLevel+1);
				rightNode.updateLevelOfBelowNodes(newLevel+1);
			}
		}
		void updateLevelOfNodes(){
			updateLevelOfAboveNodes(0);
			rootNode.updateLevelOfBelowNodes(rootNode.level);
		}

		void displayBelow(double Position, Timeline timeline){
			double vertPosition=VERT_DIST*(level)+distanceFromTop;
			double noOfNodes=Math.pow(2,level+2);
			double offset=stageWidth/noOfNodes;
			double leftNodeX=Position-offset;
			double rightNodeX=Position+offset;
			double nodeY=(level+1)*VERT_DIST+distanceFromTop;
			//traslate circle
			KeyFrame kfcx=generateKeyFrames(centerXProperty(),Position,1);
			KeyFrame kfcy=generateKeyFrames(centerYProperty(),vertPosition,1);

			//move the endpoints of the lines
			KeyFrame kflsex=generateKeyFrames(leftSide.endXProperty(),filled ? leftNodeX : Position - 1,1);
			KeyFrame kfrsex=generateKeyFrames(rightSide.endXProperty(),filled ? rightNodeX : Position + 1,1);
			KeyFrame kflsey=generateKeyFrames(leftSide.endYProperty(),filled ? nodeY : vertPosition + 1,1);

			timeline.getKeyFrames().addAll(kfcx,kfcy,kflsex,kflsey,kfrsex);
			if(filled){
				leftNode.displayBelow(leftNodeX,timeline);
				rightNode.displayBelow(rightNodeX,timeline);
			}
		}

		void hideNode(Timeline timeline,boolean hide){
			if(toShow)
				return;

			Interpolator interpolateNode=hide?Interpolator.EASE_IN:Interpolator.EASE_OUT;
			Interpolator interpolateLine=hide?Interpolator.EASE_OUT:Interpolator.EASE_IN;
			double durationNode=0.5;

			int opacityValue=hide ? 0 : 1;
			if(hide){
				this.onMouseClickedProperty().setValue(event-> System.out.println("mousereset"));
			}else{
				onMouseClickedProperty().setValue(event -> {mouseOperations(); System.out.println("mouseset");});
			}

			KeyFrame kfo=generateKeyFrames(opacityProperty(),opacityValue,durationNode,interpolateNode);
			KeyFrame kfr=generateKeyFrames(radiusProperty(),level<4 && filled ? 50 : 30,durationNode);

			if(level>3){
				hide=true;
				opacityValue=0;
			}
			double durationLine= hide ? 0.3 : 1;

			KeyFrame kflso=generateKeyFrames(leftSide.opacityProperty(),opacityValue,durationLine ,interpolateLine);

			timeline.getKeyFrames().addAll(kfo,kflso,kfr);

			if(filled){
				leftNode.hideNode(timeline,hide);
				rightNode.hideNode(timeline,hide);
			}
		}

		void removeNodeFromGraphPane(){
			if(leftNode.leftNode!=null){
				leftNode.removeNodeFromGraphPane();
			}
			if(rightNode.rightNode!=null){
				rightNode.removeNodeFromGraphPane();
			}
			graphPane.getChildren().removeAll(leftNode,leftNode.leftSide,leftNode.rightSide,leftNode.dataLabel,leftNode.blackGradient);
			graphPane.getChildren().removeAll(rightNode,rightNode.leftSide,rightNode.rightSide,rightNode.dataLabel,rightNode.blackGradient);
			leftNode=null;
			rightNode=null;

		}
		void removeNode(SequentialTransition sequentialTransition){
			Timeline timeline=new Timeline();
			filled=false;
			dataLabel.setText("");
			if(leftNode.leftNode!=null){
				leftNode.removeNode(sequentialTransition);
			}
			if(rightNode.rightNode!=null){
				rightNode.removeNode(sequentialTransition);
			}

			double posX=getCenterX();
			double posY=getCenterY();
			double removingTime=0.5;
			animationRunning=true;
			KeyFrame kfc=generateKeyFrames(fillProperty(),EMPTY_NODE_COLOR,removingTime);
			KeyFrame kfr=generateKeyFrames(radiusProperty(),EMPTY_NODE_RADIUS,removingTime);
			KeyFrame kflc=generateKeyFrames(leftNode.fillProperty(),Color.TRANSPARENT,removingTime);
			KeyFrame kfrc=generateKeyFrames(rightNode.fillProperty(),Color.TRANSPARENT,removingTime);
			KeyFrame kfllx=generateKeyFrames(leftSide.endXProperty(),posX-1,removingTime);
			KeyFrame kflly=generateKeyFrames(rightSide.endXProperty(),posX+1,removingTime);
			KeyFrame kflry=generateKeyFrames(rightSide.endYProperty(),posY+1,removingTime);

			timeline.getKeyFrames().addAll(kfc,kfr,kflc,kfrc,kfllx,kflly,kflry);
			sequentialTransition.getChildren().add(timeline);
		}



		void mouseOperations(){
			if(notEditingData){
				Timeline timeline=new Timeline();
				SequentialTransition sequentialTransition=new SequentialTransition();

				setStroke(NODE_STROKE);
				if(addNodeMode) {
					if (filled) {
						removeNode(sequentialTransition);
						sequentialTransition.play();
						sequentialTransition.setOnFinished(event-> removeNodeFromGraphPane());
					}
					else {
						fillNode(getCenterX(),timeline);
						sequentialTransition.getChildren().add(timeline);
						if(this.level>3 && addNodeMode){
							Timeline timeline1=new Timeline();
							parent.zoomInNode(timeline1);
							sequentialTransition.getChildren().add(timeline1);
						}
						sequentialTransition.play();
						sequentialTransition.setOnFinished(timelineevent ->{
							setStroke(Color.TRANSPARENT);
							editNodeData();
						});
					}

				}else{
					//display node mode
					if(filled) {
						zoomInNode(timeline);
						timeline.play();
						timeline.setOnFinished(timelineevent -> setStroke(Color.TRANSPARENT));
					}
				}
			}
		}

		void zoomInNode(Timeline timeline){
			zoomIn += level;
			tempRootNode = this;
			rootNode.resetToShow();
			setToShow();
			updateLevelOfNodes();
			displayBelow(stageWidth / 2, timeline);  ///hidess all the nodes which are above
			rootNode.hideNode(timeline, true);
			resetToShow();
			hideNode(timeline, false);               ///
		}
		Timeline zoomInNode(){
			Timeline timeline=new Timeline();
			zoomInNode(timeline);
			return timeline;
		}

		void depthFirstSearch(SequentialTransition seqTransition){
			if(rootNode==null){
				return;
			}
			seqTransition.getChildren().add(moveToTop());

			stackdfs.push(rootNode,seqTransition);
			System.out.println(rootNode.level);
			Node temp/*,prevTemp*/;
			while(stackdfs.ptr!=null){
				temp=stackdfs.pop(seqTransition);
				if(temp!=null){
					outputQueue.addElement(temp,seqTransition);
					System.out.println(tempRootNode);
					if(!temp.checkIfTempRootParent()){
						int i=0;
						ParallelTransition parallelTransition=new ParallelTransition();
						while(!temp.checkIfTempRootParent()){
							parallelTransition.getChildren().add(zoomOutNode(i*0.5));
							i++;
						}
						seqTransition.getChildren().add(parallelTransition);
					}

					if(temp.level>2 && (temp.leftNode.filled||temp.rightNode.filled))
						zoomInNode(temp,seqTransition);
					stackdfs.push(temp.leftNode,seqTransition);
					stackdfs.push(temp.rightNode,seqTransition);
					//prevTemp=temp;
				}
			}
			int i=0;
			seqTransition.getChildren().add(moveToTop());
		}

		boolean checkIfTempRootParent(){
			int i=level;
			Node ptr=this;

			System.out.println("level"+level);
			while(i-->0)
				ptr=ptr.parent;

			if(ptr.equals(tempRootNode))
				return true;
			return false;
		}

		ParallelTransition moveToTop(){
			int i=0;
			ParallelTransition parallelTransition=new ParallelTransition();
			while (!rootNode.equals(tempRootNode)){
				i++;
				parallelTransition.getChildren().add(zoomOutNode(i*0.5));
			}
			return parallelTransition;
		}

		void zoomInNode(Node arg,SequentialTransition sequentialTransition){
			if(arg!=null)
				sequentialTransition.getChildren().add(arg.zoomInNode());
		}

		void afterTravesal(){
			if(filled){
				setFill(NODE_COLOR);
				accesedInDFS=false;
				rightNode.afterTravesal();
				leftNode.afterTravesal();
			}
		}

		void breadthFirstSearch(SequentialTransition seqTransition){
			if(rootNode==null){
				return;
			}
			seqTransition.getChildren().add(moveToTop());

			queuebfs.addElement(rootNode,seqTransition);
			//System.out.println(rootNode.level);
			Node temp/*,prevTemp*/;
			while(queuebfs.front!=null){
				queuebfs.printQueue(queuebfs.front);
				temp=queuebfs.deleteElement(seqTransition);
				System.out.println(temp);
				if(temp!=null){
					outputQueue.addElement(temp,seqTransition);
					System.out.println(tempRootNode);
					if(!temp.checkIfTempRootParent()){
						int i=0;
						ParallelTransition parallelTransition=new ParallelTransition();
						while(!temp.checkIfTempRootParent()){
							parallelTransition.getChildren().add(zoomOutNode(i*0.5));
							i++;
						}
						seqTransition.getChildren().add(parallelTransition);
					}

					if(temp.level>2 && (temp.leftNode.filled||temp.rightNode.filled))
						zoomInNode(temp,seqTransition);
					queuebfs.addElement(temp.leftNode,seqTransition);
					queuebfs.addElement(temp.rightNode,seqTransition);
					//prevTemp=temp;
				}
			}
			int i=0;
			seqTransition.getChildren().add(moveToTop());
		}
		
		void bestFirstSearch(SequentialTransition seqTransition, int n){
			if(rootNode==null){
				return;
			}
			seqTransition.getChildren().add(moveToTop());

			priorityQueue.addElement(rootNode,seqTransition);
			//System.out.println(rootNode.level);
			Node temp/*,prevTemp*/;
			while(priorityQueue.front!=null){
				priorityQueue.printQueue(priorityQueue.front);
				temp=priorityQueue.deleteElement(seqTransition);
				System.out.println(temp);
				if(temp!=null){
					if(n==temp.data){
						bestFirst.setText("Found");
						return;
					}
					outputQueue.addElement(temp,seqTransition);
					System.out.println(tempRootNode);
					if(!temp.checkIfTempRootParent()){
						int i=0;
						ParallelTransition parallelTransition=new ParallelTransition();
						while(!temp.checkIfTempRootParent()){
							parallelTransition.getChildren().add(zoomOutNode(i*0.5));
							i++;
						}
						seqTransition.getChildren().add(parallelTransition);
					}

					if(temp.level>2 && (temp.leftNode.filled||temp.rightNode.filled))
						zoomInNode(temp,seqTransition);
					priorityQueue.addElement(temp.leftNode,seqTransition);
					priorityQueue.addElement(temp.rightNode,seqTransition);
					//prevTemp=temp;
				}
			}
			int i=0;
			seqTransition.getChildren().add(moveToTop());
		}
	}

	
	<T> KeyFrame generateKeyFrames(WritableValue<T> target, T endValue, double seconds, Interpolator interpolator){
		KeyValue kv=new KeyValue(target,endValue,interpolator);
		KeyFrame kf=new KeyFrame(Duration.seconds(seconds),kv);
		return kf;
	}
	<T> KeyFrame generateKeyFrames(WritableValue<T> target, T endValue , double seconds){
		return generateKeyFrames(target,endValue,seconds,Interpolator.LINEAR);
	}
}

