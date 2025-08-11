package SystemOperators;

import DataObjects.FractalImageBuffer;
import DataObjects.ColourRGB;

import processing.core.PApplet;
import processing.event.MouseEvent;

import com.jogamp.opengl.GL2;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;



/*********************
 * 
 * @author elang
 * 
 * mouse controls:
 * left click to translate
 * right click to enter "preview mode"
 * - zoom in and out with the preview window with the mouse wheel
 * - right click to exit this mode
 * - left click to prepare to zoom to the area previewed, box will be outlined in green
 * - left click again to zoom into this area, preview mode will be disengaged once new image rendered
 * - right click to just go back to preview mode
 * 
 ********************/
public class Renderer extends PApplet{

    //openGL contexes and objects
    PJOGL pgl;
    GL2 gl2;
    
    //buffers
    private FractalImageBuffer picture;
    private FractalImageBuffer previewImage;
    
    //handlers
    private Controller controller;
    private InputHandler inputHandler;
    
    private ColourRGB previewWindowBorder = new ColourRGB(1,1,1); //border for the zoom window preview
    
    @Override
    public void settings(){
        size(800,800, PApplet.P3D);
        PJOGL.profile = 1;
        PJOGL.setIcon(".\\icon.png");
        
        noSmooth();
    }//end settings
    
    @Override
    public void setup(){
        
        getSurface().setTitle("Fractal Viewer V2");
        
        //setup gl2 access    
        PGraphicsOpenGL pg = (PGraphicsOpenGL)g;
        System.out.println(PGraphicsOpenGL.OPENGL_VERSION);

        pgl = (PJOGL) beginPGL();
        gl2 = pgl.gl.getGL2();

        colorMode(RGB,1.0f);
        //colorMode(HSB,1.0f);
        picture = new FractalImageBuffer(width);
        previewImage = new FractalImageBuffer(width/5); //new FractalImageBuffer( (int)((width/5)/0.75) );//  w/o scaling in plot image
        controller = new Controller(this);
        
        inputHandler = new InputHandler(controller, this);
        
        getFocus(); //some focusing issues with the processing window, need focus so the user isn't confused

        noLoop();//documentation says this should be the last line in setup
              
    }//end setup
    
    private void getFocus(){
        if(!focused){
           ((java.awt.Canvas) surface.getNative()).requestFocus();//request it
           getFocus(); //recurse this method to check for focus again
         }//end if
    }//end getFocus
    
    @Override
    public void draw(){
        
        //draw different things depending on the view mode
        switch(controller.getViewMode()){
            
            case full -> {
                System.out.println("rendering...");
                System.out.println(formatRenderTime(controller.setImage(picture)) +"\nZoom: "+controller.getZoom());
                
                plotImage(picture);
                
                controller.updateGUI();
            }//end full case
            
            case preview -> {
                controller.fractalPreviewSegment(previewImage,
                        mouseX, 
                        mouseY, 
                        width, height);
                plotImage(previewImage); //keep plotting the preview image as the user moves their mouse
                borderPreviewWindow();
            }//end preview case
            
            case exitPreview -> {
                controller.setViewMode(Controller.ViewModes.full); 
                
                plotImage(picture); //once image is replotted we can go back to full mode
             
                noLoop(); //now that it's reset don't need to iterate anymore
            }//end exitPreview case
            
            case transition -> {
                plotImage(previewImage); //keep the image on screen so the user knows where they're gonna go
                
                borderPreviewWindow(); 
                
                noLoop(); //no more looping needed
            }//end transition case
            
        }//end swtich
      
    }//end draw

    //formats a string to display the render time to the user
    private String formatRenderTime(long time){
        double seconds = ((double)(time))*1E-9d; //convert from nano seconds to seconds
  
        return seconds > 60d ?  seconds/60d+" minutes" :  seconds + " seconds" ; //if longer than 60 seconds, show as minutes
  
    }//end formatTime
    
    private void plotImage(FractalImageBuffer source){
      //Vp resolution = image resolution
        ColourRGB pixel; //pixel to plot
        
        gl2.glBegin(GL2.GL_POINTS);
        
        for(int y=0; y < source.getDimension(); y++)
            for(int x=0; x < source.getDimension(); x++){
                pixel=source.get(x, y);
                stroke(pixel.red(), pixel.green(), pixel.blue());

               gl2.glColor3f(pixel.red(), pixel.green(), pixel.blue());
               gl2.glVertex2i(x, y);
            }//end nested for

        gl2.glEnd();
    }//end plotImage

    @Override
    public void keyPressed(){
        
        //if the key function returns true then the key pressed calls redraw
        if(inputHandler.handleKeyPress(key)) 
            redraw();
        
    }//end keyPressed 
    
    @Override
    public void mousePressed(){
  
        inputHandler.handleMouseClick(mouseX, mouseY, mouseButton);
        redraw(); 
                

    }//end mousePressed   
    
    @Override
    public void mouseWheel(MouseEvent event){
        inputHandler.handleMouseWheel(event);
    }//end mouseWheel
            
    
    protected void setupForPreviewMode(){
        loop(); //re start the draw loop running
       
        previewWindowBorder.assign(1, 1, 1); //border it in white
    }//end setupForPreviewMode
    
    protected void setupForTransitionMode(){
        //set a green outline on the preview to alert user that they can zoom in there
        previewWindowBorder.assign(0, 1, 0);
    }//end setupForTransitionMode
    
    private void borderPreviewWindow(){

        //two colouring settings for the window
        gl2.glColor3f(previewWindowBorder.red(), previewWindowBorder.green(), previewWindowBorder.blue());
        
        gl2.glLineWidth(2.0f);
        gl2.glBegin(GL2.GL_LINES);
            
            gl2.glVertex2i(0, previewImage.getDimension() + 1);
            gl2.glVertex2i(previewImage.getDimension() + 1, previewImage.getDimension() + 1);
            
            gl2.glVertex2i(previewImage.getDimension() + 1, 0);
            gl2.glVertex2i(previewImage.getDimension() + 1, previewImage.getDimension() + 1);
        
        gl2.glEnd();
        gl2.glLineWidth(1.0f);
    }//end borderPreviewWindow
    
    //returns pointer, this is for save method to access
    protected FractalImageBuffer getDisplayedFractal(){
        return picture;
    }//get displayed factal
    
    public static void main(String[] args) {
        
        //run the processing sketch
         Renderer r = new Renderer();
         PApplet.runSketch(new String[]{""}, r);
         
    }//end main
    
}//end class