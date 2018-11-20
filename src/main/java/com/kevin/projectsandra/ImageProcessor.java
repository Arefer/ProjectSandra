/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kevin.projectsandra;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author kevin
 */
public class ImageProcessor {
    public static WritableImage applyCanny(BufferedImage img, float threshLow, float threshHigh){
        // Transformar la imagen original a escala de grises
        GrayU8 originalGrayImage = ConvertBufferedImage.convertFrom(img,(GrayU8)null);
        // Imagen resultante en GrayU8
        GrayU8 edgedImage = originalGrayImage.createSameShape();
        
        CannyEdge<GrayU8,GrayS16> canny = FactoryEdgeDetectors.canny(2,true, true, GrayU8.class, GrayS16.class);
        canny.process(originalGrayImage, threshLow, threshHigh, edgedImage);
        
        // Transformar el resultado en BufferedImage
        BufferedImage bufferedResult = VisualizeBinaryData.renderBinary(edgedImage, false, null);
        // Transformar el resultado en WritableImage
        WritableImage result = SwingFXUtils.toFXImage(bufferedResult, null);
        // Cambiar pixeles blancos por negros
        invertImage(result);
        return result;
    }
    
    public static void invertImage(WritableImage img){
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = img.getPixelWriter();
        for(int y = 0; y < img.getHeight(); y++) { 
            for(int x = 0; x < img.getWidth(); x++) {  
               javafx.scene.paint.Color color = pr.getColor(x, y);
               pw.setColor(x, y, color.invert());
            }
        }
    }
    
    public static WritableImage segmentEdges(WritableImage img, int factor){
        if (factor == 0) return img;
        WritableImage imgCopy = copyImage(img);
        PixelWriter pw = imgCopy.getPixelWriter();
        int row = 0;
        int column = 0;
        int counter = 0;
        boolean skip = false;
        while (row < imgCopy.getHeight()){
            int aux = 0;
            while (column < imgCopy.getWidth()){
                pw.setColor(column, row, Color.WHITE);
                if (aux == factor) {
                    column += factor + 1;
                    aux = 0;
                }
                else {
                    column += 1;
                    aux += 1;
                }
            }
            // Si fila es multiplo de factor
            if (counter == factor){
                if (skip) skip = false;
                else skip = true;
                counter = 0;
            }
            if (skip) column = factor;
            else column = 0;
            row += 1;
            counter += 1;
        }
        return imgCopy;
    }
    
    public static WritableImage copyImage(WritableImage img){
        WritableImage result = new WritableImage(
                (int)img.getWidth(), (int)img.getHeight());
        
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = result.getPixelWriter();
        
        for (int row = 0; row < result.getHeight(); row++) {
            for (int column = 0; column < result.getWidth(); column++) {
                pw.setColor(column, row, pr.getColor(column, row));
            }
        }
        
        return result;
    }
    
    public static RenderedImage toRenderedImage(WritableImage img){
        BufferedImage bImg = SwingFXUtils.fromFXImage(img, null);
        RenderedImage rImg = (RenderedImage) bImg;
        return rImg;
    }
}