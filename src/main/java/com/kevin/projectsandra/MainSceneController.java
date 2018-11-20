package com.kevin.projectsandra;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javax.imageio.ImageIO;

public class MainSceneController implements Initializable {
    
    private static final float THRESH_LOW_DEFAULT = 0.1f; 
    private FileChooser fileChooser;
    
    private SimpleFloatProperty threshHigh;
    
    private SimpleFloatProperty threshHighProperty(){
        return threshHigh;
    }

    private javafx.scene.image.Image originalImage;
    private BufferedImage originalBufferedImage;
    private WritableImage edgedImage;
    private WritableImage finalImage;
    @FXML
    private Slider threshHighSlider; 
    @FXML
    private Slider segmentFactorSlider;
    @FXML
    private BorderPane hb00;
    @FXML
    private BorderPane hb01;
    @FXML
    private ImageView originalImageViewer;
    @FXML
    private ImageView edgedImageViewer;
    @FXML
    private GridPane mainGrid;
    @FXML
    private void openHandler(ActionEvent event) throws MalformedURLException, IOException{
        threshHigh.setValue(0.05f);
        // Seleccionar la imagen
        File file = fileChooser.showOpenDialog(MainApp.mainStage);
        if (file != null){
            // Transformar la imagen seleccionada en BufferedImage
            originalImage = new javafx.scene.image.Image(file.toURI().toString());
            originalBufferedImage = ImageIO.read(file.toURI().toURL());
            edgedImage = ImageProcessor.applyCanny(originalBufferedImage, THRESH_LOW_DEFAULT ,threshHigh.getValue());
            // Mostrar las imagenes
            originalImageViewer.setImage(originalImage);
            edgedImageViewer.setImage(edgedImage);
            }   
    }
    
    @FXML
    private void saveAsHandler(ActionEvent event) throws MalformedURLException, IOException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar imágen");
        FileChooser.ExtensionFilter pngExtensionFilter =
            new FileChooser.ExtensionFilter(
                    "(.png)", "*.png");
        fileChooser.getExtensionFilters().add(pngExtensionFilter);
        fileChooser.setSelectedExtensionFilter(pngExtensionFilter);
        File file = fileChooser.showSaveDialog(MainApp.mainStage);
        // ImageIO.write(ImageProcessor.toRenderedImage(finalImage), "jpg", file);
        ImageIO.write(SwingFXUtils.fromFXImage(finalImage, null), "png", file);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        threshHigh = new SimpleFloatProperty(0f);
        fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir imágen");
        
        threshHighSlider.setShowTickLabels(true);
        threshHighSlider.setShowTickMarks(true);
        segmentFactorSlider.setShowTickLabels(true);
        segmentFactorSlider.setShowTickMarks(true);
        
        threshHighSlider.valueProperty().bindBidirectional(threshHigh);
        
        threshHighSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                if (originalImage != null){
                    edgedImage = ImageProcessor.applyCanny(originalBufferedImage, THRESH_LOW_DEFAULT ,threshHigh.getValue());
                    // Mostrar las imagenes
                    edgedImageViewer.setImage(edgedImage);
                }
            }
        });
        
        segmentFactorSlider.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                if (edgedImage != null){
                    finalImage = ImageProcessor.segmentEdges(edgedImage, (int) Math.round(segmentFactorSlider.valueProperty().getValue()));
                    // Mostrar las imagenes
                    edgedImageViewer.setImage(finalImage);
                }
            }
        });
        
        originalImageViewer.fitWidthProperty().bind(hb00.widthProperty());
        originalImageViewer.fitHeightProperty().bind(hb00.heightProperty());
        
        edgedImageViewer.fitWidthProperty().bind(hb01.widthProperty());
        edgedImageViewer.fitHeightProperty().bind(hb01.heightProperty());
    }    
}