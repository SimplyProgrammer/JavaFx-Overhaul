package test.prog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import ugp.org.javafx.animation.Interpolators;
import ugp.org.javafx.transitions.GeneralTransition;
import ugp.org.javafx.transitions.PropertyTransition;

public class Example2 extends Application
{
	public static void main(String[] args) 
	{
		launch(args);
	}

	private boolean state = false;
	private Duration d = Duration.seconds(2);
	private List<Transition> chartAnimators = new CopyOnWriteArrayList<>();
	private NumberAxis xA = new NumberAxis("x = t [s]", 0, 1, 0.1), yA = new NumberAxis("y = f(x)", 0, 1, 0.1);
	private LineChart<Number, Number> ch = new LineChart<>(xA, yA);

	/**
	 * DO NOT BE AFRAID OF LONG CODE IN HERE MOST OF IT IS FOR HANDLING CHART GRAPHS...
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage)
	{
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1480, 980);
		root.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
		
		Button b = new Button("Compare");
		b.setTranslateY(10);
		
		TextField t = new TextField();
		t.setTranslateY(10);
		
		//Chart series
		XYChart.Series<Number, Number> lSeries = new XYChart.Series<>("Linear", FXCollections.observableArrayList());
		XYChart.Series<Number, Number> eSeries = new XYChart.Series<>("Ease", FXCollections.observableArrayList());
		XYChart.Series<Number, Number> eInSeries = new XYChart.Series<>("Ease-in", FXCollections.observableArrayList());
		XYChart.Series<Number, Number> eOutSeries = new XYChart.Series<>("Ease-out", FXCollections.observableArrayList());
		XYChart.Series<Number, Number> eInOutSeries = new XYChart.Series<>("Ease-both", FXCollections.observableArrayList());
		XYChart.Series<Number, Number> stpsSeries = new XYChart.Series<>("Steps(5)", FXCollections.observableArrayList());
				
		//Interpolators comparison (important part)
		Rectangle linear = new Rectangle(200, 100, 100, 100), ease = new Rectangle(400, 100, 100, 100), easeIn = new Rectangle(600, 100, 100, 100), easeOut = new Rectangle(600, 100, 100, 100), easeBoth = new Rectangle(600, 100, 100, 100), steps = new Rectangle(600, 100, 100, 100);
		List<GeneralTransition> trans = new ArrayList<>();
		Collections.addAll(trans, 
			Create(linear, lSeries, Interpolators.LINEAR),
			Create(ease, eSeries, Interpolators.EASE),
			Create(easeIn, eInSeries, Interpolators.EASE_IN),
			Create(easeOut, eOutSeries, Interpolators.EASE_OUT),
			Create(easeBoth, eInOutSeries, Interpolators.EASE_IN_OUT),
			Create(steps, stpsSeries, Interpolators.valueOf("steps(5)"))
		);
		
		VBox interpDemo = new VBox(5, new Label("Linear:"), linear, new Label("Ease:"), ease, new Label("Ease-in:"), easeIn, new Label("Ease-out:"), easeOut, new Label("Ease-both:"), easeBoth, new Label("Steps(5):"), steps, new HBox(10, b, t));
		for (int i = 0; i < interpDemo.getChildren().size(); i++)
			if (interpDemo.getChildren().get(i) instanceof Rectangle)
			{
				Rectangle rec = (Rectangle) interpDemo.getChildren().get(i);
				rec.setOnMouseEntered(ev -> rec.setWidth(300));
				rec.setOnMouseExited(ev -> rec.setWidth(100));
				rec.getStyleClass().add("rectIntrpDemo");
			}
			else
				interpDemo.getChildren().get(i).setStyle("-fx-font-weight: bold");
		
		t.textProperty().addListener((obs, oldV, newV) -> 
		{
			try
			{
				d = Duration.valueOf(newV.toLowerCase());
			}
			catch (Exception e)
			{
				try
				{
					d = Duration.seconds(Double.parseDouble(newV));
				}
				catch (Exception e2) 
				{
					Platform.runLater(() -> t.setText(oldV));
				}
			}
			if (d != null)
			{
				xA.setUpperBound(d.toSeconds());
				xA.setTickUnit(d.toSeconds()/10);
				for (GeneralTransition tr : trans)
					tr.setDefaultDuration(d);
			}
		});
		t.setText(d.toSeconds()+"");
		
		b.setOnAction(ev ->
		{
			for (Transition tr : chartAnimators)
			{	
				tr.stop();
				chartAnimators.remove(tr);
			}
			for (int i = 0; i < interpDemo.getChildren().size(); i++)
				if (interpDemo.getChildren().get(i) instanceof Rectangle)
				{	
					if (!state)
						((Rectangle) interpDemo.getChildren().get(i)).setWidth(300);
					else
						((Rectangle) interpDemo.getChildren().get(i)).setWidth(100);
				}
			state = !state;
		});
		
		//Chart stuff
		ch.setTranslateX(550);
		ch.setTranslateY(-20);
		ch.setPrefWidth(900);
		ch.setPrefHeight(900);
		ch.setTitle("Cubic-baziers behavior");
		ch.setAnimated(false);
		ch.setCreateSymbols(false);
		ch.getData().addAll(lSeries, eSeries, eInSeries, eOutSeries, eInOutSeries, stpsSeries);

		//Chart value display and controls
		Tooltip val = new Tooltip() 
		{
			@Override
			public void show(Node n, double x, double y) 
			{
				if (!isShowing())
					super.show(n, x, y);
				else
				{
					setAnchorX(x);
					setAnchorY(y);
				}
			}
		};
		DecimalFormat frm = new DecimalFormat("#.##");
		ch.addEventHandler(MouseEvent.ANY, ev ->
		{
			Point2D pos = new Point2D(ev.getSceneX(), ev.getSceneY());
			double xVal = xA.getValueForDisplay(xA.sceneToLocal(pos).getX()).doubleValue(), yVal = yA.getValueForDisplay(yA.sceneToLocal(pos).getY()).doubleValue();
			
			ch.setCursor(Cursor.CROSSHAIR);
			if (xVal < 0 || xVal > d.toSeconds() || yVal < -0.05 || yVal > 1.05)
			{
				val.hide();
				ch.setCursor(null);
			}
			else
				val.show(ch, ev.getScreenX() + 5, ev.getScreenY() + 5);
	        val.setText("t = " + frm.format(xVal) + "s, f(t) = " + frm.format(yVal));
		});
		ch.setOnMouseClicked(ev -> 
		{
			if (ev.getButton() == MouseButton.MIDDLE)
				for (Series<Number, Number> sr : ch.getData())
					sr.getData().clear();
		});
		
		root.setCenter(new Group(interpDemo, ch));
		scene.getStylesheets().add("test/prog/styles.css");
		
		primaryStage.setTitle("Interpolators aka transition timing functions comparison");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void AnimateSeries(Series<Number, Number> series, Interpolator interp) //Setup chart transitions
	{
		List<Data<Number, Number>> datas = new ArrayList<>();
		datas.add(new Data<Number, Number>(0, 0));
		chartAnimators.add(new Transition(120) 
		{
			{
				setCycleDuration(d);
				setInterpolator(interp);
				playFromStart();
			}
			
			@Override
			protected void interpolate(double frac) 
			{
				series.getData().clear();
				datas.add(new Data<>(getCurrentTime().toSeconds(), frac));
				series.getData().addAll(datas);
			}
		});
	}
	
	public GeneralTransition Create(Rectangle rect, Series<Number, Number> series, Interpolator interp) //Magic part here
	{
		return new GeneralTransition(d, rect.widthProperty())  //Magic
		{
			protected <T> void SetupTransition(PropertyTransition<?> trans, T from, T to) 
			{
				super.SetupTransition(trans, from, to);
				AnimateSeries(series, interp);
			}
		}.setDefaultInterpolator(interp);
	}
}
