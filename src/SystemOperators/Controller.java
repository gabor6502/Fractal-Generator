package SystemOperators;

import DataObjects.FractalImageBuffer;
import DataObjects.ColourRGB;
import DataObjects.ComplexNumber;
import GUI.FracGUI;
import com.jogamp.newt.opengl.GLWindow;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


public class Controller {
    
    //**********ENUMS*************
    public enum ColouringMethods{EscapeTimes, Potential, NormalMap, White, NormalMapCustom}//colouring the set methods (normalMapCustom is WIP)
    public enum AAMethods{none, supersampling}
    public enum ViewModes{full, transition, preview, exitPreview}; //viewing the fractals in full or in preview mode, or transitioning between  
    
    //**********CONSTANTS**********
    private static float BOUNDS = 2f; //bounds of M, BS
    private static float ZOOM_VAL = 1.1f; //value to zoom in by
    
    private static float PREVIEW_ZOOM_FACTOR = 1/12.50f;
    
    private static double PREVIEW_ZOOM_LBOUND = 2e-2;
    private static double PREVIEW_ZOOM_UBOUND = 0.1;

    //**********INSTANCE VARIABLES********
    private FractalGenerator fg;
    private Renderer renderer;
    private FracGUI gui;
    
    private ColouringMethods colourMethod;
    private AAMethods antialiasing;
    private ViewModes viewMode;
    
    private double zoom=1;
    private double transX=0;
    private double transY=0;
    
    private double previewZoomX=0;
    private double previewZoomY=0;
    
    private double previewZoomFactor=PREVIEW_ZOOM_FACTOR;
    
    private int iterations=700;
     
    private float rOffset;
    private float gOffset;
    private float bOffset;
    
    //for escape time colouring
    private int palletteLength = 16;
    
    //for potential colouring
    //try K values of 0.03, 0.1, 0.3, 1, 3, 10, 30, 100, 300, 1000, 3000 and 10000
    private float K = 1f;
    private double[] pCoefs; //coefficients for use by colouring with potential
    
    //for normal map effect
    private ComplexNumber lightVector; //vector of coming light
    private float lightHeight = 1.5f; //height of light source in world
 
    
    //for custom normal map effects EXPERIMENTAL STILL UNDER DEV
    private ComplexNumber[] lightVectors = {new ComplexNumber(Math.cos(Math.PI/4), Math.sin(Math.PI/4)), 
                                            new ComplexNumber(-Math.cos(Math.PI/4), -Math.sin(Math.PI/4))};
    private float[] lightHeights = {1.5f, 0e-8f};
    private float[][] intensities = { {1,1,1}, {1,0,0}};
    private ColourRGB fracColour = new ColourRGB(0,0,0);
    
    private int supersampleFactor=3; //factor to supersample by
    
    //instead of allocating these every single time they're needed, keep them ready to use
    //each complex number represnets a peice of memory that looks like {r,i}
    //the doubles r and  i are modified and accessed when needed
    private ComplexNumber c = new ComplexNumber();
    private ComplexNumber z = new ComplexNumber();
    private ComplexNumber _z = new ComplexNumber(); 
    private ComplexNumber u = new ComplexNumber();
    
    //bailout is WIP
    private double bailout = 1e6;//1e6; //the smaller this number, the less actual mandelbrot points are coloured.
    
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss"); 
    private LocalDateTime now;
    private DecimalFormat df = new DecimalFormat("#.#####"); //5 sig figs shown for render time
    
    private int renderCount=0; //how many images this intance rendered (showed to user)
    //**********CONSTRUCTORS**************
    public Controller(Renderer r){
        //steup instance variables
        fg = new FractalGenerator();
        renderer = r;
        
        colourMethod = ColouringMethods.EscapeTimes;
        antialiasing = AAMethods.none;
        viewMode = ViewModes.full;
        
         //generates random floats in [0, 1]
        rOffset = (float)Math.random();
        gOffset = (float)Math.random();
        bOffset = (float)Math.random();
        
        pCoefs = new double[]{1*(1/Math.log(2)), 1/(3*Math.sqrt(2))*(1/Math.log(2)), 1/(7*Math.pow(3,1/8))*(1/Math.log(2))};
        
        lightVector = new ComplexNumber(Math.cos(Math.PI/4), Math.sin(Math.PI/4)); //polar coords of a "normal" complex number
        
        //do this last once this controller is setup
        gui = new FracGUI(this);
        //gui.setVisible(false);
        viewGUI();
    }//end init constructor
    
    //*********ACCESSESORS***************
    public boolean antialiasing(){
        return antialiasing == AAMethods.supersampling;
    }//end antialiasing
    
    public int getIterations(){
        return iterations;
    }//end getIterations
    
    public int getCurrentColouringMethod(){
        int i=0;//index of method
        boolean found=false; //flag to kill loop
        
        //quick lin search returning index of current method
        ColouringMethods[] methods = ColouringMethods.values();
        for(; i < methods.length && !found; i++)
            found = (methods[i] == colourMethod);

        return --i;
    }//end getCurrentColouringMethod
    
    public int getCurrentFractal(){
        return fg.getCurrentFractalIndex();
    }//end getCurrentFractalIndex
    
    public ViewModes getViewMode(){ 
        return viewMode; 
    }//end get view modes
    
    public double getZoom(){  
        return zoom;  
    } //end get zoom
    
    public int getSupersampleFactor(){
        return supersampleFactor;
    }//end getSupersampleFactor
    
    public double getPreviewZoomFactor(){
        return previewZoomFactor;
    }//end getPreviewZoomFactor
    
    //*********MUTATORS**************
    
    public void setColourMethod(int index){
        try{
            colourMethod = ColouringMethods.values()[index];
        }catch(IndexOutOfBoundsException e){
            
        }//end try catch
    }//end setColourMethod
    
    public void setFractal(int index){
        fg.setFractal(index);
    }//end setFractal
    
    protected void viewGUI(){
        
        GLWindow window = (GLWindow)renderer.getSurface().getNative(); //accesses a GLWindow pointer for the PApplet
        
        //get the PApplet's location on screen this via the GLWindow, set the gui to appear beside it
        gui.setLocation(window.getX()+(window.getWidth()-(window.getWidth()/4)), window.getY()); 

        gui.setVisible(true);
        
    }//end viewGUI

    
    //USED BY PREVIEW MODE ONLY, bascially saves coords to zoom to
    public void preaprePreviewTranslateZoom(double x, double y){
        previewZoomX=x;
        previewZoomY=y;
    }//end preparePreviewTranslate
    
    //completes the process of translating then zooming
    public void engagePreviewTranslateZoom(double w, double h){
        this.setToMousePos(previewZoomX, previewZoomY, w,h);
        setZoom(zoom*previewZoomFactor);
    }//end engagePreviewTranslate
    
    //takes values from the mouse wheel, if it changes change the zoom up or down depending on sign
    public void previewZoomChange(float MWpos){
       //https://processing.org/reference/mouseWheel_.html
       // + when wheel moved towards user
       // - when when wheel moved away from user
       double nextZoom;
       
        if(viewMode == ViewModes.preview){ //only have this change during preview mode
            if(MWpos < 0){ //zoom in if mouse moved away
                nextZoom = previewZoomFactor / 1.1;
                if(nextZoom > PREVIEW_ZOOM_LBOUND) //don't zoom too deep, mouse goes wild, plus want to encourage exploration for the user
                    previewZoomFactor = nextZoom;
            }//end if
            else{ //zoom out if moved toward
                nextZoom = previewZoomFactor * 1.1;
                if(nextZoom <= PREVIEW_ZOOM_UBOUND)
                  previewZoomFactor = nextZoom;
            }//end else
        }//end if
    }//end previewZoomChange
    
    public void setViewMode(ViewModes vm){
        viewMode = vm;
    }//end setViewMode
    
    public void setSupersampleFactor(int f){
        supersampleFactor=f;
    }//end setSupersampleFactor
    
    public void setAAmethod(AAMethods aa){
        antialiasing = aa;
    }//end setAAmethod
    
    public void zoomIn(){
        zoom /= ZOOM_VAL;
    }//end zoom in
    
    public void zoomOut(){
        zoom *= ZOOM_VAL;
    }//end zoom out
    
    public void helpMessage(){
        
        JOptionPane.showMessageDialog(null, 
              "Keyboard Controls\n"
            + "---------------------------------\n"
            + "zoom in/out: +/-\n"
            + "toggle antialiasing: a\n"
            + "bring up gui: g\n"
            + "\nMouse Controls\n"  
            + "---------------------------------\n"
            + "left click to translate\n"
            + "right click to enter \"preview mode\"\n"
            + "While in preview mode:\n"
            + " - zoom the preview window in and out with the mouse wheel\n"
            + " - right click to exit this mode\n"
            + " - left click to prepare to zoom to the area previewed, box will be outlined in green\n"
            + " - left click again to zoom into this area, preview mode will be disengaged once the new image is rendered\n"
            + " - right click to go back to preview mode",
                "Help", JOptionPane.INFORMATION_MESSAGE);
        
    }//end helpMessage
    
    public void save(){//runs a save routine for the image on screen
        String name=""; //path or name for user to save file to
        boolean success=true; //set to true, will be proven wrong if an exception occurs
 
            try{
               name = JOptionPane.showInputDialog(null, "Enter the name of this save image: ", "Save File", JOptionPane.PLAIN_MESSAGE);

               if(name!=null && !name.equals(""))  //make sure user didn't enter nothing or hit exit
                   saveImage(name);
                   //renderer.save(System.getProperty("user.home")+"\\Desktop\\MandelSaves\\"+path+".png"); //processing's save
              else
                   success=false; //else, erroneuos input, no saved file, set flag

            }catch(NullPointerException npe){ //catch exception, no input
               success=false;//set flag
             }//end try catch
      
             //display messages to user if success or not
            if(success)
              JOptionPane.showMessageDialog(null,"File saved to: "+System.getProperty("user.home")+"\\Desktop\\MandelSaves\\"+name,"Save", JOptionPane.INFORMATION_MESSAGE);
              else
            JOptionPane.showMessageDialog(null, "An error occured when saving file", "Save", JOptionPane.ERROR_MESSAGE);

    }//end save
    
    
    private void saveImage(String name){

        File image = new File(System.getProperty("user.home")+"\\Desktop\\MandelSaves\\"+name+".png"); //the file to write the image to, set to desktop for now
        File testDir = new File(image.getParent()); //for checking if the folder "MandelSaves" exists

        BufferedImage bImg = new BufferedImage(renderer.width, renderer.height, BufferedImage.TYPE_INT_RGB); //the image to write to

  
        if(!testDir.exists()) //if this folder we want to save to doesn't exist
          testDir.mkdir(); //make it
  
        createBufferedImage(bImg); //assemble the image from the current picture on screen
  
         try{
           ImageIO.write(bImg, "PNG", image); //write the image to the file
         
           }catch(IOException ioe){
              System.out.println("FAILED TO WRITE TO FILE");
           }//end try catch
 
    }//end void

    private void createBufferedImage(BufferedImage bImg){
       Color currentColor; //current color to write to file to (nessecary for interfacing with bufferedImage's methods)
       FractalImageBuffer picture = renderer.getDisplayedFractal();
       
       //since we're  (assumed to be) using an integer colour mode here, going to have to convert from the [0f,1f] mode to [0i,255i] 
       for(int x=0; x < bImg.getWidth(); x++)
         for(int y=0; y < bImg.getHeight(); y++){
            currentColor = new Color((int)(picture.get(x, y).red()*255), 
                                     (int)(picture.get(x, y).green()*255), 
                                     (int)(picture.get(x, y).blue()*255)); //assign the current colour, convert to the [0,255] space
            bImg.setRGB(x,y, currentColor.getRGB()); //set the colour at this x y
        }//end nested for
    }//end createBufferedImage
   
    
    
    public void toggleAA(){

        if(antialiasing == AAMethods.none){
            antialiasing = AAMethods.supersampling;
        }//end if
        else{
            antialiasing = AAMethods.none;
            //renderer.colorMode(RGB, 1.0f);
        }//end else
            
    }//end toggleAA
    
    //sets mouse click position as center/translation
    public void setToMousePos(double x, double y, double w, double h){

        //converts to the Pr*V setup
        if(antialiasing == AAMethods.none){ //ONLY MOVE W/ MOUSE W/OUT AA ON speed issue
          transX += (((x - (w/2)) / (w/4)) * zoom);
          transY += (((y - (h/2)) / (h/4)) * zoom);
        }//end if
        
    }//end setTranslation
    
    public void setTranslation(double tx, double ty){
        transX = tx;
        transY = ty;
    }//end set translation
    
    public void setZoom(double z){
        if(z > 0.0) zoom = z;
    }//end set zoom
    
    public void setIterations(int iter){
        iterations=iter;
    }//end setIterations
    //*********FRACTAL IMAGE GENERATION METHODS********
    public long setImage(FractalImageBuffer target, int x, int y, int w, int h){//sets up whatever needs to be drawn
            return this.fractalPreviewSegment(target, x, y, w, h);
    }//end setImage
        
    public long setImage(FractalImageBuffer target){
        long time; //render time from image
        now = LocalDateTime.now();  //get the current time of the day the render is started on
        
        gui.notify(++renderCount+") ["+dtf.format(now)+"]\nRendering...");
        time = fractal(target);   
        gui.notify("Completed\nTime: "+formatRenderTime(time)+"\n");
        
        //renderCount++; //increment number of full renders
        
        return time;
    }//end setImage overloaded
    
      //formats a string to display the render time to the user
    private String formatRenderTime(long time){
        final double MIN = 60; //1 minute is 60 seconds
        
        double seconds = ((double)(time))*1E-9d; //convert from nano seconds to seconds
        String formatted;
        
        //formatting logic
        if(seconds > MIN)
            formatted = df.format(seconds/MIN) + " minutes";
        else
            formatted = df.format(seconds) + " seconds";
        
        return formatted;
    }//end formatTime
    
    private long fractal(FractalImageBuffer target){
        long renderTime=0;
        
        switch(antialiasing){
            case none -> renderTime = render(target);
            case supersampling -> renderTime = renderSupersampled(target);
        }//end switch

        
        return renderTime;
    }//end render
    
    private long render(FractalImageBuffer target){
        final int STEPS = target.getDimension();

        float t_x=0, t_y=0;
        int nx=0, ny=0;

        long start = System.nanoTime(); //begin timing
        
        while(t_x < 1){
            c.setReal(((lerp(-BOUNDS,BOUNDS, t_x)) * zoom) + transX); //get the real part at this point in the scanline, apply zoom and translation
            while(t_y < 1){
                c.setImaginary(((lerp(-BOUNDS,BOUNDS, t_y)) * zoom) + transY); //get the imaginary part at this point in the scanline, apply zoom and translation

                switch(colourMethod){
                    case White -> fg.white(iterations, target.get(nx, ny), c, z);
                    case EscapeTimes -> fg.escapeTimes(iterations, target.get(nx, ny), c, z,
                        palletteLength, rOffset, bOffset, gOffset);
                    case Potential -> fg.potential(iterations, target.get(nx, ny), c, z, 
                            K, pCoefs);
                    case NormalMap -> fg.normalMap(iterations, target.get(nx,ny), c, z, 
                            _z, lightHeight, u, lightVector);
                    case NormalMapCustom -> fg.normalMap(iterations, target.get(nx,ny), c, z, 
                            _z, lightHeights, u, lightVectors, intensities, fracColour);
                }//end switch
            
                ny++; //go to next y step
                t_y = (float)ny / (float)STEPS; //get next t for y
            }//end nested while
            //reset y lerping items
            ny=0;
            t_y=0;
    
            nx++; //go to next x step    
            t_x = (float)nx / (float)STEPS; //get the next t for x
        }//end while
        
        //renderer.colorMode(RGB, 1.0f);
        
        return (System.nanoTime()-start);
    }//end rednerEscTime
    
    private long renderSupersampled(FractalImageBuffer target){
        
        FractalImageBuffer grid = new FractalImageBuffer(supersampleFactor);
        long start=System.nanoTime(); //start timing
        
        double halfDim = target.getDimension()/2.0d;
        double quarterDim = target.getDimension()/4.0d;
        
        for(int y=0; y < target.getDimension(); y++)
            for(int x=0; x < target.getDimension(); x++)
                supersample((double)x, (double)y, grid, 
                        target.get(x,y), halfDim, quarterDim);
        
        //renderer.colorMode(RGB, (float)supersampleFactor*(float)supersampleFactor);//do this all the time here in case sample factor changes
        
        return (System.nanoTime()-start);
    }//end render Supersampled
    
    
    private void supersample(double x, double y, FractalImageBuffer subPixGrid, ColourRGB target, double halfDim, double quarterDim){
        float t_x=0, t_y=0;
        int nx=0, ny=0;

        /******************************************************************************
         -Making a sqaure region out of the raster point (x,y) with (x,y) as the center.
         -This square touches the sides of the other squares with centers (x+1,y), (x+1, y-1), etc.
         -So it's corners are (x-1/2, y-1/2) and (x+1/2, y+1/2)
         -Now, lerp scan lines across the x and y axes. Steps are given as the sample factor
        -Hence, if the x lerps 'factor' steps and the y does too, then we get a 'factor'*'factor' subpixel grid
        -Then that grid is averaged.
  
        -the statements below transform us from the reals into the 'complex numbers' (just the 2d PrV setup for Mandlebrot set)
        -so lerp in C, and don't have to convert from R every lerp iteration
        *********************************************************************************/
        double lxBound = ((((x-0.5) - halfDim) / quarterDim) * zoom) +transX; //lower x bound (not like upper and lower bounds of poset/fucntions, just the start and end of a line to lerp across)
        double uxBound = ((((x+0.5) - halfDim) / quarterDim) * zoom) +transX; //upper x bound
  
        double lyBound = ((((y-0.5) - halfDim) / quarterDim) * zoom) +transY; //lower y bound
        double uyBound = ((((y+0.5) - halfDim) / quarterDim) * zoom) +transY;//upper y bound
        
         while(t_x < 1){
            c.setReal( lerp(lxBound, uxBound, t_x)); //get the real part at this point in the scanline, apply zoom and translation
            while(t_y < 1){
                c.setImaginary(lerp(lyBound, uyBound, t_y)); //get the imaginary part at this point in the scanline, apply zoom and translation

                switch(colourMethod){
                    case White -> fg.white(iterations, subPixGrid.get(nx, ny), c, z);
                    case EscapeTimes -> fg.escapeTimes(iterations, subPixGrid.get(nx, ny), c, z,
                        palletteLength, rOffset, bOffset, gOffset);
                    case Potential -> fg.potential(iterations, subPixGrid.get(nx, ny), c, z, 
                            K, pCoefs);
                    case NormalMap -> fg.normalMap(iterations, subPixGrid.get(nx,ny), c, z, 
                            _z, lightHeight, u, lightVector);
                    case NormalMapCustom -> fg.normalMap(iterations, subPixGrid.get(nx, ny), c, z, 
                            _z, lightHeights, u, lightVectors, intensities, fracColour);
                }//end switch
                
                ny++; //go to next y step
                t_y = (float)ny / (float)supersampleFactor; //get next t for y
            }//end nested while
            //reset y lerping items
            ny=0;
            t_y=0;
    
            nx++; //go to next x step    
            t_x = (float)nx / (float)supersampleFactor; //get the next t for x
        }//end while
         
         target.assign(subPixGrid.averageColour());
    }//end supersample
    
    public long fractalPreviewSegment(FractalImageBuffer target, double x, double y, double w, double h){
        final int STEPS = target.getDimension();

        float t_x=0, t_y=0;
        int nx=0, ny=0;
        
        double previewDepth = this.zoom*previewZoomFactor;//this.zoom*PREVIEW_ZOOM_FACTOR; //this.zoom/50; //zoom far in from original value for the preview
        
        //where to render for the preview
        x = (((x - (w/2)) / (w/4)) * zoom) + transX; //want x and y to reflect where the mouse is currently in the fractal, NOT in the zoom
        y = (((y - (h/2)) / (h/4)) * zoom) + transY;
 
        long start = System.nanoTime(); //begin timing
        
        while(t_x < 1){
            c.setReal(((lerp(-BOUNDS,BOUNDS, t_x)) * previewDepth) + x); //get the real part at this point in the scanline, apply zoom and translation
            while(t_y < 1){
                c.setImaginary(((lerp(-BOUNDS,BOUNDS, t_y)) * previewDepth) + y); //get the imaginary part at this point in the scanline, apply zoom and translation
                
                  switch(colourMethod){
                    case White -> fg.white(iterations, target.get(nx, ny), c, z);
                    case EscapeTimes -> fg.escapeTimes(iterations, target.get(nx, ny), c, z,
                        palletteLength, rOffset, bOffset, gOffset);
                    case Potential -> fg.potential(iterations, target.get(nx, ny), c, z, 
                            K, pCoefs);
                    case NormalMap -> fg.normalMap(iterations, target.get(nx,ny), c, z, 
                            _z, lightHeight, u, lightVector);
                    case NormalMapCustom -> fg.normalMap(iterations, target.get(nx,ny), c, z, 
                            _z, lightHeights, u, lightVectors, intensities, fracColour);
                  }//end switch
                
                ny++; //go to next y step
                t_y = (float)ny / (float)STEPS; //get next t for y
            }//end nested while
            //reset y lerping items
            ny=0;
            t_y=0;
    
            nx++; //go to next x step    
            t_x = (float)nx / (float)STEPS; //get the next t for x
        }//end while
        return System.nanoTime()-start;
    }//end fractalSegment
    
    private double lerp(double a, double b, double t){
        return ((1-t)*a) + (t*b);
    }//end lerp
    
    //wrapper methods just in case more needs to be done before calling these in the future
    public void render(){
        renderer.redraw();
    }//end render
    
    public void updateGUI(){
        gui.setInputs();
    }//end updateGUI
    
}//end class