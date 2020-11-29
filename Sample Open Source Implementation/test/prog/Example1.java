package test.prog;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ugp.org.javafx.animation.Interpolators;
import ugp.org.javafx.transitions.GeneralTransition;
import ugp.org.javafx.transitions.PropertyTransition;

public class Example1 extends Application
{
	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		Group root = new Group();
		Scene scene = new Scene(root, 900, 480);
		
		Rectangle rect = new Rectangle(200, 50, 100, 100), rect2 = new Rectangle(400, 50, 100, 100), rect3 = new Rectangle(600, 50, 100, 100);
		
		new GeneralTransition(rect, 2000); //Magic...
		rect.getStyleClass().add("rect");
		rect.setId("redRect");

		new GeneralTransition(rect2, 2000); //Magic...
		rect2.getStyleClass().add("rect");
		rect2.setId("greenRect");

		new GeneralTransition(rect3, 2000); //Magic...
		rect3.getStyleClass().add("rect");
		rect3.setId("blueRect");
		
		PropertyTransition<Color> bgTrans = new PropertyTransition<Color>(scene.fillProperty(), 2500, Color.AQUAMARINE, Interpolators.LINEAR);
		bgTrans.playEndlessPulse();
		
		root.getChildren().addAll(rect, rect2, rect3);
		scene.getStylesheets().add("test/prog/styles.css");
		
		primaryStage.setTitle("Animated Rectangles");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
